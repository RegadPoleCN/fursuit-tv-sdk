package com.furrist.rp.furtv.sdk.auth

import com.furrist.rp.furtv.sdk.exception.OAuthException
import com.furrist.rp.furtv.sdk.exception.TokenExpiredException
import com.furrist.rp.furtv.sdk.http.HttpClientConfig
import com.furrist.rp.furtv.sdk.model.SdkConfig
import com.furrist.rp.furtv.sdk.utils.toHex
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi
import kotlin.js.JsExport
import kotlin.js.JsName
import kotlin.random.Random
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import love.forte.plugin.suspendtrans.annotation.JvmAsync
import love.forte.plugin.suspendtrans.annotation.JvmBlocking

/**
 * 认证管理器
 *
 * 负责令牌管理和 OAuth 流程。支持签名交换和 OAuth 两种认证方式。
 *
 * @property config SDK 配置
 */
@JvmBlocking
@JvmAsync
@Suppress("TooManyFunctions")
@JsExport
@JsName("AuthManager")
public class AuthManager internal constructor(
    private val config: SdkConfig,
) {
    private var httpClient: HttpClient = HttpClientConfig.createClient(config)
    private val tokenMutex = Mutex()

    // 令牌信息
    private var tokenInfo: TokenInfo? = null

    // 是否为 OAuth 令牌（决定使用哪种认证头）
    // true: OAuth 流程的 access_token，使用 Authorization: Bearer
    // false: 签名交换的令牌，使用 X-Api-Key（优先）或 Authorization: Bearer
    private var isOAuthToken: Boolean = false

    // OAuth 流程中使用的客户端 ID 和重定向 URI，用于令牌刷新
    private var oauthClientId: String? = null
    private var oauthRedirectUri: String? = null

    // 平台签名
    private var platformAccessToken: String? = null

    private var callbackHandler: OAuthCallbackHandler? = createDefaultOAuthHandler()

    /**
     * 获取当前访问令牌
     * @return 当前访问令牌，如果未认证则返回 null
     */
    @JsName("getAccessToken")
    public fun getAccessToken(): String? = tokenInfo?.accessToken

    /**
     * 获取当前 API 密钥
     * @return 当前 API 密钥，如果未认证则返回 null
     */
    @JsName("getApiKey")
    public fun getApiKey(): String? = tokenInfo?.apiKey

    /**
     * 检查是否已认证
     * @return 如果已认证且令牌未过期返回 true，否则返回 false
     */
    @JsName("isAuthenticated")
    public fun isAuthenticated(): Boolean = tokenInfo?.isExpired()?.not() == true

    /**
     * 设置令牌信息
     * @param tokenInfo 要设置的令牌信息
     */
    @JsName("setTokenInfo")
    public fun setTokenInfo(tokenInfo: TokenInfo) {
        this.tokenInfo = tokenInfo
        updateHttpClient()
    }

    /**
     * 清除令牌信息
     * 清除当前存储的令牌并重置 HTTP 客户端
     */
    @JsName("clearToken")
    public fun clearToken() {
        tokenInfo = null
        updateHttpClient()
    }

    /**
     * 设置自定义 OAuth 回调处理器
     * @param handler 回调处理器
     */
    @JsName("setOAuthCallbackHandler")
    public fun setOAuthCallbackHandler(handler: OAuthCallbackHandler) {
        this.callbackHandler = handler
    }

    /**
     * 执行完整的 OAuth 登录流程
     *
     * 1. 自动生成状态和 PKCE 参数
     * 2. 调用回调处理器开始监听
     * 3. 验证回调中的 state 参数（防止 CSRF 攻击）
     * 4. 交换授权码获取用户令牌
     *
     * @param scope 权限范围（可选）
     * @return TokenInfo 获取到的用户令牌信息
     * @throws IllegalStateException 如果没有可用的回调处理器
     * @throws OAuthException 如果 state 验证失败或 OAuth 流程出错
     */
    @JsName("loginWithOAuth")
    public suspend fun loginWithOAuth(scope: String? = null): TokenInfo {
        val handler = callbackHandler ?: throw IllegalStateException("OAuth callback handler not set")
        val oauthConfig = OAuthConfig(enablePkce = true)

        val state = StateManager.generateState()
        StateManager.storeState(state, oauthConfig.stateTimeoutMinutes)

        handler.startListening()

        val pkceParams = generatePkceParameters(oauthConfig.enablePkce)
        val authorizeUrl =
            getOAuthAuthorizeUrl(
                redirectUri = handler.callbackUrl,
                scope = scope,
                state = state,
                enablePkce = oauthConfig.enablePkce,
                codeChallenge = pkceParams?.codeChallenge,
            )

        val result = handler.startAndGetCallback(authorizeUrl)

        return processOAuthCallbackResult(result, handler.callbackUrl, pkceParams?.codeVerifier)
    }

    private suspend fun processOAuthCallbackResult(
        result: OAuthCallbackResult,
        callbackUrl: String,
        codeVerifier: String?,
    ): TokenInfo {
        when (result) {
            is OAuthCallbackResult.Success -> {
                if (!StateManager.consumeState(result.state)) {
                    throw OAuthException("State mismatch or expired", errorCode = "state_mismatch")
                }
                return exchangeOAuthToken(result.code, callbackUrl, codeVerifier)
            }
            is OAuthCallbackResult.Error -> {
                throw OAuthException(
                    "OAuth failed: ${result.message}",
                    errorCode = result.errorCode,
                    cause = result.cause,
                )
            }
        }
    }

    /**
     * 使用应用凭证（clientId + clientSecret）进行签名交换，获取**平台签名**。
     *
     * 平台签名包含 accessToken 和 apiKey，是后续 OAuth 流程的前置步骤。
     * 调用成功后，内部会自动保存 platformAccessToken 字段供 OAuth 接口使用。
     *
     * @param clientId 应用 ID（格式 vap_xxxx），SDK 统一使用 clientId 命名，与 VDS 文档中的 appId 等价
     * @param clientSecret 应用密钥
     * @return 平台签名（TokenInfo），包含 accessToken 和 apiKey
     */
    @JsName("exchangeToken")
    public suspend fun exchangeToken(clientId: String, clientSecret: String): TokenInfo {
        val response =
            httpClient.post("${config.baseUrl}/api/auth/token") {
                contentType(ContentType.Application.Json)
                setBody(TokenExchangeRequest(clientId, clientSecret))
            }.body<TokenExchangeResponse>()

        val newTokenInfo = response.data.toTokenInfo()

        tokenMutex.withLock {
            tokenInfo = newTokenInfo
            isOAuthToken = false // 标记为非 OAuth 令牌
            platformAccessToken = newTokenInfo.accessToken
            updateHttpClient()
        }

        return newTokenInfo
    }

    /**
     * 刷新访问令牌。
     * @return TokenInfo 新的令牌信息
     * @throws TokenExpiredException 如果没有可用的令牌
     */
    @JsName("refreshToken")
    public suspend fun refreshToken(): TokenInfo {
        val currentAccessToken =
            tokenInfo?.accessToken
                ?: throw TokenExpiredException("No access token available")

        val response =
            httpClient.post("${config.baseUrl}/api/auth/token/refresh") {
                header("Authorization", "Bearer $currentAccessToken")
            }.body<TokenRefreshResponse>()

        val newTokenInfo = response.data.toTokenInfo()

        tokenMutex.withLock {
            tokenInfo = newTokenInfo
            updateHttpClient()
        }

        return newTokenInfo
    }

    /**
     * 获取有效的访问令牌（自动刷新）
     *
     * 自动检查令牌有效性，过期或即将过期时自动刷新。
     *
     * @param clientId 应用 ID（用于签名交换回退，格式 vap_xxxx），SDK 统一使用 clientId 命名，与 VDS 文档中的 appId 等价
     * @param clientSecret 应用密钥
     * @return 当前或刷新后的访问令牌
     */
    @JsName("getValidAccessToken")
    public suspend fun getValidAccessToken(clientId: String, clientSecret: String): String {
        return tokenMutex.withLock {
            if (tokenInfo == null) {
                exchangeToken(clientId, clientSecret)
            } else if (tokenInfo!!.isExpired()) {
                try {
                    if (isOAuthToken && tokenInfo?.refreshToken != null) {
                        refreshOAuthToken()
                    } else {
                        refreshToken()
                    }
                } catch (_: Exception) {
                    exchangeToken(clientId, clientSecret)
                }
            }
            tokenInfo!!.accessToken
        }
    }

    /**
     * 如果需要，刷新令牌
     * 检查令牌是否过期（剩余时间 <= 300 秒），如果是则自动刷新
     * @return TokenInfo 当前或刷新后的令牌信息，如果未认证则返回 null
     * @deprecated 使用 [getValidAccessToken] 代替，该方法需要 clientId 和 clientSecret
     */
    @Deprecated(
        "Use getValidAccessToken(clientId, clientSecret) instead",
        ReplaceWith("getValidAccessToken(clientId, clientSecret)"),
    )
    @JsName("refreshTokenIfNeeded")
    public suspend fun refreshTokenIfNeeded(): TokenInfo? {
        return tokenMutex.withLock {
            if (tokenInfo?.isExpired() == true) {
                refreshToken()
            } else {
                tokenInfo
            }
        }
    }

    /**
     * 生成 PKCE code verifier
     * @return 随机的 code_verifier 字符串
     */
    private fun generateCodeVerifier(): String = Random.nextBytes(32).toHex()

    /**
     * 生成 PKCE code challenge
     * 对 code_verifier 进行 SHA256 哈希并进行 base64url 编码
     * 使用纯 Kotlin 实现的 SHA256 算法，支持所有 Kotlin Multiplatform 平台
     * @param verifier code_verifier 字符串
     * @return code_challenge base64url 编码的 SHA256 哈希值
     */
    @OptIn(ExperimentalEncodingApi::class)
    private fun generateCodeChallenge(verifier: String): String {
        val sha256Hash = verifier.encodeToByteArray().sha256()
        return Base64.UrlSafe.encode(sha256Hash).replace("=", "")
    }

    /**
     * Generate PKCE parameters if enabled.
     * @param enablePkce Whether to enable PKCE (Proof Key for Code Exchange)
     * @return PkceParameters if enabled, null otherwise
     */
    private fun generatePkceParameters(enablePkce: Boolean): PkceParameters? {
        if (!enablePkce) return null

        val codeVerifier = generateCodeVerifier()
        val codeChallenge = generateCodeChallenge(codeVerifier)

        return PkceParameters(
            codeVerifier = codeVerifier,
            codeChallenge = codeChallenge,
        )
    }

    /**
     * 生成 OAuth 授权 URL。
     * @param redirectUri 重定向 URI
     * @param scope 权限范围（可选）
     * @param state 状态参数，用于防止 CSRF 攻击（可选）
     * @param enablePkce 是否启用 PKCE（可选，默认启用）
     * @param codeChallenge PKCE code_challenge 值（可选，启用 PKCE 时若未提供则自动生成）
     * @return 完整的授权 URL
     * @throws IllegalStateException 当缺少 clientId 时抛出
     */
    @JsName("getOAuthAuthorizeUrl")
    public fun getOAuthAuthorizeUrl(
        redirectUri: String,
        scope: String? = null,
        state: String? = null,
        enablePkce: Boolean = true,
        codeChallenge: String? = null,
    ): String {
        val clientId = config.clientId ?: throw IllegalStateException("clientId is not configured in SDK")

        var effectiveCodeChallenge: String? = null
        var codeChallengeMethod: String? = null

        if (enablePkce) {
            effectiveCodeChallenge = codeChallenge ?: generatePkceParameters(true)?.codeChallenge
            codeChallengeMethod = "SHA256"
        }

        val queryParams =
            buildString {
                append("?client_id=$clientId")
                append("&redirect_uri=$redirectUri")
                append("&response_type=code")
                scope?.let { append("&scope=$it") }
                state?.let { append("&state=$it") }
                effectiveCodeChallenge?.let { append("&code_challenge=$it") }
                codeChallengeMethod?.let { append("&code_challenge_method=$it") }
            }
        return "${config.baseUrl}/api/proxy/account/sso/authorize$queryParams"
    }

    /**
     * PKCE (Proof Key for Code Exchange) parameters for OAuth security.
     * @property codeVerifier The random verifier generated by client
     * @property codeChallenge The SHA256 hash of codeVerifier, base64url encoded
     */
    @JsName("PkceParameters")
    public data class PkceParameters(
        public val codeVerifier: String,
        public val codeChallenge: String,
    )

    /**
     * 使用授权码换取 OAuth 用户令牌。
     *
     * ⚠️ **重要前置条件**: 必须先调用 [exchangeToken] 完成签名交换！
     *
     * 此方法需要使用"开放平台签名"（即签名交换获取的 platformAccessToken）
     * 作为 Authorization Bearer 认证头，而不是 config.apiKey 或 OAuth access_token。
     *
     * @param code OAuth 授权码（从回调 URL 中获取）
     * @param redirectUri 重定向 URI（必须与授权时一致）
     * @param codeVerifier PKCE code_verifier（如果使用了 PKCE）
     * @return OAuth 用户令牌信息（包含 oauth access_token 和 refresh_token）
     * @throws IllegalStateException 如果未完成签名交换（platformAccessToken 为空）
     * @throws OAuthCallbackException 如果授权失败
     * @see exchangeToken 必须先调用此方法获取平台签名
     */
    @JsName("exchangeOAuthToken")
    @Suppress("ThrowsCount")
    public suspend fun exchangeOAuthToken(
        code: String,
        redirectUri: String,
        codeVerifier: String? = null,
    ): TokenInfo {
        val clientId = config.clientId ?: throw IllegalStateException("clientId is not configured in SDK")
        val clientSecret = config.clientSecret ?: throw IllegalStateException("clientSecret is not configured in SDK")

        val requestBody =
            mutableMapOf(
                "grant_type" to "authorization_code",
                "client_id" to clientId,
                "client_secret" to clientSecret,
                "code" to code,
                "redirect_uri" to redirectUri,
            )

        codeVerifier?.let {
            requestBody["code_verifier"] = it
        }

        val currentPlatformToken =
            platformAccessToken
                ?: tokenInfo?.accessToken
                ?: throw IllegalStateException(
                    "未找到平台签名。请先调用 exchangeToken(clientId, clientSecret) 完成签名交换。" +
                        "OAuth Token 交换接口需要使用'开放平台签名'作为 Authorization Bearer 认证头。",
                )

        if (platformAccessToken == null && tokenInfo?.accessToken != null) {
            platformAccessToken = tokenInfo!!.accessToken
        }

        val response =
            httpClient.post("${config.baseUrl}/api/proxy/account/sso/token") {
                contentType(ContentType.Application.FormUrlEncoded)
                header("Authorization", "Bearer $currentPlatformToken")
                setBody(requestBody)
            }.body<OAuthTokenResponse>()

        val newTokenInfo = response.data.toTokenInfo()

        tokenMutex.withLock {
            tokenInfo = newTokenInfo
            isOAuthToken = true // 标记为 OAuth 令牌
            oauthClientId = clientId
            oauthRedirectUri = redirectUri
            updateHttpClient()
        }

        return newTokenInfo
    }

    /**
     * 刷新 OAuth 用户令牌（非平台签名）。
     *
     * 此方法刷新的是通过 [exchangeOAuthToken] 获取的 OAuth access_token，
     * 而非签名交换获取的平台签名。同样需要平台签名作为 Authorization Bearer 认证头。
     *
     * @return 新的 OAuth 用户令牌信息（TokenInfo）
     * @throws TokenExpiredException 如果没有可用的 refreshToken
     * @throws IllegalStateException 如果未完成签名交换或缺少 OAuth 配置参数
     */
    @JsName("refreshOAuthToken")
    @Suppress("ThrowsCount")
    public suspend fun refreshOAuthToken(): TokenInfo {
        val refreshToken =
            tokenInfo?.refreshToken
                ?: throw TokenExpiredException("No refresh token available")

        val clientId = oauthClientId ?: throw IllegalStateException("OAuth client ID not available")
        val clientSecret = config.clientSecret ?: throw IllegalStateException("clientSecret is not configured in SDK")
        val redirectUri = oauthRedirectUri ?: throw IllegalStateException("OAuth redirect URI not available")

        val platformToken =
            this.platformAccessToken
                ?: throw IllegalStateException(
                    "未找到平台签名。请先调用 exchangeToken(clientId, clientSecret) 完成签名交换。" +
                        "OAuth Token 刷新接口需要使用'开放平台签名'作为 Authorization Bearer 认证头。",
                )

        val response =
            httpClient.post("${config.baseUrl}/api/proxy/account/sso/token") {
                contentType(ContentType.Application.FormUrlEncoded)
                header("Authorization", "Bearer $platformToken")
                setBody(
                    mapOf(
                        "grant_type" to "refresh_token",
                        "refresh_token" to refreshToken,
                        "client_id" to clientId,
                        "client_secret" to clientSecret,
                        "redirect_uri" to redirectUri,
                    ),
                )
            }.body<OAuthTokenResponse>()

        val newTokenInfo =
            response.data.toTokenInfo().copy(
                refreshToken = response.data.refreshToken ?: tokenInfo?.refreshToken,
            )

        tokenMutex.withLock {
            tokenInfo = newTokenInfo
            isOAuthToken = true
            updateHttpClient()
        }

        return newTokenInfo
    }

    /**
     * 查询已授权用户的公开信息。
     *
     * 此方法使用**双认证头机制**：
     * - `Authorization: Bearer <platformAccessToken>` - 验证应用身份（你是哪个应用？）
     *   来源: 签名交换时获取并保存到 platformAccessToken 字段
     * - `X-OAuth-Access-Token: <oauthAccessToken>` - 标识用户身份（你是哪个用户？）
     *   来源: OAuth 授权码流程获取的 tokenInfo.accessToken
     *
     * @return 用户信息数据（UserInfoData）
     * @throws IllegalStateException 如果缺少必要的令牌
     */
    @JsName("getUserInfo")
    public suspend fun getUserInfo(): UserInfoData {
        val response =
            httpClient.get("${config.baseUrl}/api/proxy/account/sso/userinfo") {
                header("X-OAuth-Access-Token", tokenInfo?.accessToken ?: "")
                header("Authorization", "Bearer $platformAccessToken")
            }.body<UserInfoResponse>()

        return response.data
    }

    /**
     * 更新 HTTP 客户端（当令牌变化时）
     * 使用新的访问令牌重新配置 HTTP 客户端
     * 根据令牌类型选择合适的认证头：
     * - OAuth 令牌：使用 Authorization Bearer
     * - 签名交换令牌：使用 X-Api-Key（优先）
     */
    private fun updateHttpClient() {
        val authToken =
            when {
                isOAuthToken -> platformAccessToken ?: tokenInfo?.apiKey ?: tokenInfo?.accessToken
                else -> tokenInfo?.apiKey ?: tokenInfo?.accessToken
            }
        httpClient = HttpClientConfig.createClient(config, authToken)
    }

    /**
     * 关闭客户端
     * 释放认证管理器占用的资源
     */
    @JsName("close")
    public fun close() {
        httpClient.close()
    }
}
