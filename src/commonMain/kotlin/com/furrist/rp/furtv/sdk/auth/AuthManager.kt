package com.furrist.rp.furtv.sdk.auth

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
import kotlin.random.Random
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.datetime.Clock

/**
 * 认证管理器
 *
 * 负责令牌管理和 OAuth 流程。支持签名交换和 OAuth 两种认证方式。
 *
 * @property config SDK 配置
 */
@Suppress("TooManyFunctions")
public class AuthManager(
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

    /**
     * 获取当前访问令牌
     * @return 当前访问令牌，如果未认证则返回 null
     */
    public fun getAccessToken(): String? = tokenInfo?.accessToken

    /**
     * 获取当前 API 密钥
     * @return 当前 API 密钥，如果未认证则返回 null
     */
    public fun getApiKey(): String? = tokenInfo?.apiKey

    /**
     * 检查是否已认证
     * @return 如果已认证且令牌未过期返回 true，否则返回 false
     */
    public fun isAuthenticated(): Boolean = tokenInfo?.isExpired()?.not() == true

    /**
     * 设置令牌信息
     * @param tokenInfo 要设置的令牌信息
     */
    public fun setTokenInfo(tokenInfo: TokenInfo) {
        this.tokenInfo = tokenInfo
        updateHttpClient()
    }

    /**
     * 清除令牌信息
     * 清除当前存储的令牌并重置 HTTP 客户端
     */
    public fun clearToken() {
        tokenInfo = null
        updateHttpClient()
    }

    /**
     * 签名交换 - 使用 clientId + clientSecret 获取令牌
     *
     * 端点：POST /api/auth/token
     *
     * @param clientId 应用 ID（格式 vap_xxxx）
     * @param clientSecret 应用密钥
     * @return TokenInfo 包含 accessToken 和 apiKey
     * @throws ClientRequestException 请求失败时抛出
     */
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
            updateHttpClient()
        }

        return newTokenInfo
    }

    /**
     * 刷新令牌
     * 端点：POST /api/auth/token/refresh
     *
     * @return TokenInfo 新的令牌信息
     * @throws TokenExpiredException 如果没有可用的令牌
     */
    public suspend fun refreshToken(): TokenInfo {
        val currentAccessToken =
            tokenInfo?.accessToken
                ?: throw TokenExpiredException("No access token available")

        val response =
            httpClient.post("${config.baseUrl}/api/auth/token/refresh") {
                contentType(ContentType.Application.Json)
                header("Authorization", "Bearer $currentAccessToken")
                setBody(TokenRefreshRequest())
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
     * @param clientId 应用 ID（用于签名交换回退，格式 vap_xxxx）
     * @param clientSecret 应用密钥
     * @return 当前或刷新后的访问令牌
     */
    public suspend fun getValidAccessToken(clientId: String, clientSecret: String): String {
        return tokenMutex.withLock {
            val now = Clock.System.now().toEpochMilliseconds()

            // 1. 如果没有令牌或已过期，获取新令牌
            if (tokenInfo == null || tokenInfo?.isExpired() == true) {
                exchangeToken(clientId, clientSecret)
                return@withLock tokenInfo!!.accessToken
            }

            // 2. 如果剩余有效期 <= 300 秒，刷新令牌
            if (tokenInfo!!.isExpired()) { // isExpired() now checks <= 300 seconds
                @Suppress("SwallowedException", "TooGenericExceptionCaught")
                try {
                    if (isOAuthToken && tokenInfo?.refreshToken != null) {
                        // OAuth 令牌刷新
                        refreshOAuthToken()
                    } else {
                        // 签名交换令牌刷新
                        refreshToken()
                    }
                } catch (e: Exception) {
                    // 3. 刷新失败，回退到 exchangeToken
                    // 记录异常但不抛出，继续执行 exchangeToken
                    // 注意：这里捕获通用 Exception 是因为网络错误可能是多种类型
                    exchangeToken(clientId, clientSecret)
                }
            }

            return@withLock tokenInfo!!.accessToken
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
     * 生成 OAuth 授权 URL
     * 端点：GET /api/proxy/account/sso/authorize
     *
     * @param redirectUri 重定向 URI
     * @param scope 权限范围（可选）
     * @param state 状态参数（可选，用于防止 CSRF 攻击）
     * @param enablePkce 是否启用 PKCE（可选，默认启用）
     * @return 完整的授权 URL
     * @throws IllegalStateException 当缺少 clientId 时抛出
     */
    public fun getOAuthAuthorizeUrl(
        redirectUri: String,
        scope: String? = null,
        state: String? = null,
        enablePkce: Boolean = true,
    ): String {
        val clientId = config.clientId ?: throw IllegalStateException("clientId is not configured in SDK")

        var codeVerifier: String? = null
        var codeChallenge: String? = null
        var codeChallengeMethod: String? = null

        if (enablePkce) {
            codeVerifier = generateCodeVerifier()
            codeChallenge = generateCodeChallenge(codeVerifier)
            codeChallengeMethod = "SHA256"
        }

        val queryParams =
            buildString {
                append("?client_id=$clientId")
                append("&redirect_uri=$redirectUri")
                append("&response_type=code")
                scope?.let { append("&scope=$it") }
                state?.let { append("&state=$it") }
                codeChallenge?.let { append("&code_challenge=$it") }
                codeChallengeMethod?.let { append("&code_challenge_method=$it") }
            }
        return "${config.baseUrl}/api/proxy/account/sso/authorize$queryParams"
    }

    /**
     * 使用 OAuth 初始化 SDK
     * 完整的 OAuth 授权流程：生成参数 → 打开授权页面 → 等待回调 → 交换令牌
     *
     * @param config OAuth 配置（回调地址、PKCE 等）
     * @param scope 权限范围（可选）
     * @return TokenInfo 包含访问令牌和刷新令牌
     * @throws OAuthCallbackException 当回调或令牌交换失败时抛出
     * @throws IllegalStateException 当缺少 clientId 时抛出
     */
    @Suppress("LongMethod", "ThrowsCount", "MaxLineLength")
    public suspend fun initOAuth(config: OAuthConfig, scope: String? = null): TokenInfo {
        // From SDK config, get clientId and clientSecret
        val clientId = this.config.clientId
            ?: throw IllegalStateException("clientId is required for OAuth")
        val clientSecret = this.config.clientSecret
            ?: throw IllegalStateException("clientSecret is required for OAuth")

        val serverConfig = OAuthCallbackServerConfig(
            callbackHost = config.callbackHost,
            callbackPort = config.callbackPort,
            callbackPath = config.callbackPath,
            timeoutSeconds = config.stateTimeoutMinutes * 60L,
        )
        val handler = OAuthCallbackHandler(serverConfig)

        val state = Random.nextBytes(16).toHex()
        val redirectUri = handler.callbackUrl

        // 生成 PKCE 参数（如果启用）
        var codeVerifier: String? = null
        if (config.enablePkce) {
            codeVerifier = generateCodeVerifier()
        }

        // 生成授权 URL（自动从 SDK 配置获取 clientId）
        val authorizeUrl = getOAuthAuthorizeUrl(
            redirectUri = redirectUri,
            scope = scope,
            state = state,
            enablePkce = config.enablePkce,
        )

        // 等待回调（handler 会处理打开浏览器等逻辑）
        val result = handler.startAndGetCallback(authorizeUrl)

        return when (result) {
            is OAuthCallbackResult.Success -> {
                // 验证 state
                if (result.state != state) {
                    throw OAuthCallbackException(
                        "State mismatch: expected $state, got ${result.state}",
                    )
                }

                // 交换令牌（自动从 SDK 配置获取 clientId 和 clientSecret）
                exchangeOAuthToken(
                    code = result.code,
                    redirectUri = redirectUri,
                    codeVerifier = codeVerifier,
                )
            }
            is OAuthCallbackResult.Error -> {
                throw OAuthCallbackException(result.message, result.cause)
            }
        }
    }

    /**
     * OAuth 令牌交换
     * 端点：POST /api/proxy/account/sso/token
     *
     * @param code 授权码（从 OAuth 授权回调中获取）
     * @param redirectUri 重定向 URI（必须与授权时一致）
     * @param codeVerifier 可选的 PKCE code_verifier
     * @return TokenInfo 包含访问令牌和刷新令牌
     * @throws OAuthCallbackException 当令牌交换失败时抛出
     * @throws IllegalStateException 当缺少 clientId 或 clientSecret 时抛出
     */
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

        val response =
            httpClient.post("${config.baseUrl}/api/proxy/account/sso/token") {
                contentType(ContentType.Application.FormUrlEncoded)
                header("Authorization", "Bearer ${config.apiKey}")
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
     * 刷新 OAuth 令牌
     * 端点：POST /api/proxy/account/sso/token
     *
     * @return TokenInfo 新的令牌信息
     * @throws TokenExpiredException 如果没有可用的 refresh_token
     */
    @Suppress("ThrowsCount")
    public suspend fun refreshOAuthToken(): TokenInfo {
        val refreshToken =
            tokenInfo?.refreshToken
                ?: throw TokenExpiredException("No refresh token available")

        val clientId = oauthClientId ?: throw IllegalStateException("OAuth client ID not available")
        val clientSecret = config.clientSecret ?: throw IllegalStateException("clientSecret is not configured in SDK")
        val redirectUri = oauthRedirectUri ?: throw IllegalStateException("OAuth redirect URI not available")

        val response =
            httpClient.post("${config.baseUrl}/api/proxy/account/sso/token") {
                contentType(ContentType.Application.FormUrlEncoded)
                header("Authorization", "Bearer ${config.apiKey}")
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

        val newTokenInfo = response.data.toTokenInfo().copy(
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
     * 获取用户信息
     * 端点：GET /api/proxy/account/sso/userinfo
     *
     * @return UserInfoData 用户信息
     * @throws OAuthCallbackException 当认证失败时抛出
     */
    public suspend fun getUserInfo(): UserInfoData {
        val response =
            httpClient.get("${config.baseUrl}/api/proxy/account/sso/userinfo") {
                // X-OAuth-Access-Token: OAuth access_token 用于标识用户（必须）
                header("X-OAuth-Access-Token", tokenInfo?.accessToken ?: "")

                // Authorization: Bearer <开放平台签名> 用于验证应用身份（可选）
                // 场景 A：config.apiKey 不为空时，使用双 header
                // 场景 B：config.apiKey 为空时（纯 OAuth），只使用 X-OAuth-Access-Token
                if (config.apiKey != null && config.apiKey.isNotEmpty()) {
                    header("Authorization", "Bearer ${config.apiKey}")
                }
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
        // OAuth 场景使用 Authorization Bearer，签名交换场景使用 X-Api-Key
        // 智能选择逻辑由 HttpClientConfig 自动处理：
        // - 当 config.apiKey 存在时，使用 X-Api-Key
        // - 当仅有 accessToken 时，使用 Authorization Bearer
        httpClient =
            HttpClientConfig.createClient(
                config,
                if (isOAuthToken) tokenInfo?.accessToken else tokenInfo?.apiKey,
            )
    }

    /**
     * 关闭客户端
     * 释放认证管理器占用的资源
     */
    public fun close() {
        httpClient.close()
    }
}
