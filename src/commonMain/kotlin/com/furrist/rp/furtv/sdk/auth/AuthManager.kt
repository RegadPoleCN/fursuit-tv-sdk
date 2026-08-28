/*
 *   Copyright 2026 RegadPoleCN
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.furrist.rp.furtv.sdk.auth

import com.furrist.rp.furtv.sdk.auth.AuthManager.Companion.StateStoreInternal.stateStorage
import com.furrist.rp.furtv.sdk.exception.OAuthException
import com.furrist.rp.furtv.sdk.exception.TokenExpiredException
import com.furrist.rp.furtv.sdk.exception.ValidationException
import com.furrist.rp.furtv.sdk.model.*
import com.furrist.rp.furtv.sdk.utils.toHex
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlin.concurrent.Volatile
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi
import kotlin.js.JsExport
import kotlin.js.JsName
import kotlin.random.Random
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.time.Clock
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
    private val httpClient: HttpClient,
) {
    private val tokenMutex = Mutex()

    // 令牌信息（sealed class；用 @Volatile 保证跨线程可见性）
    @Volatile
    private var tokenInfo: TokenInfo? = null

    private var callbackHandler: OAuthCallbackHandler? = createDefaultOAuthHandler()

    /**
     * 获取当前 API 密钥（platform apiKey）。
     * @return 当前 platform apiKey，如果未认证或 token 不是 TokenInfo.Platform 则返回 null
     */
    @JsName("getApiKey")
    public fun getApiKey(): String? = (tokenInfo as? TokenInfo.Platform)?.apiKey

    /**
     * 检查是否已认证
     * @return 如果已认证且令牌未过期返回 true，否则返回 false
     */
    @JsName("isAuthenticated")
    public fun isAuthenticated(): Boolean = tokenInfo?.isExpired()?.not() == true

    /**
     * 响应式 re-exchange 包装器（init-builder-refactor D11）。
     *
     * - 预检 `ensureFreshToken(clientId, clientSecret)`：过期/缺失则触发 `exchangeToken`
     * - 执行 [block]；如抛 `TokenExpiredException`（401），再次 `ensureFreshToken` 后重试 [maxRetries] 次
     * - X-Api-Key 由 `HttpClientConfig.defaultRequest` 自动注入（无需 `apiKey` 参数）
     *
     * 仅触发 **platform** token exchange；OAuth 路径不受影响（用 `Authorization: Bearer` 直传）。
     */
    @JsName("withFreshToken")
    public suspend fun <T> withFreshToken(
        maxRetries: Int = 1,
        block: suspend () -> T,
    ): T {
        require(maxRetries >= 0) { "maxRetries must be >= 0 (got $maxRetries)" }
        ensureFreshToken(requireClientId(), requireClientSecret())
        return runRetryLoop(maxRetries, block)
    }

    /**
     * 重试循环：执行 [block]；每次抛 [TokenExpiredException] 时重新触发
     * [ensureFreshToken] 后重试 [remaining] 次。`remaining=0` 表示不再重试。
     *
     * 设计动机：防止 401 持续返回时无限循环（确保 maxRetries 是显式上界）。
     */
    private suspend fun <T> runRetryLoop(remaining: Int, block: suspend () -> T): T {
        return try {
            block()
        } catch (e: TokenExpiredException) {
            if (remaining <= 0) throw e
            ensureFreshToken(requireClientId(), requireClientSecret())
            runRetryLoop(remaining - 1, block)
        }
    }

    /** Throws [IllegalStateException] if [SdkConfig.clientId] is missing. */
    private fun requireClientId(): String =
        config.clientId
            ?: error("withFreshToken requires SDK init with clientId. Configure via fursuitTvSdk { clientId = \"...\" }.")

    /** Throws [IllegalStateException] if [SdkConfig.clientSecret] is missing. */
    private fun requireClientSecret(): String =
        config.clientSecret
            ?: error("withFreshToken requires SDK init with clientSecret.")

    /**
     * 预检 token：过期/缺失则触发 `exchangeToken`。
     *
     * **check-then-call** 模式：`tokenMutex` 不可重入，`exchangeToken` 内部已经 `withLock`，
     * 因此不能直接在锁内调 `exchangeToken`（会死锁）。本方法在锁内判断 needsExchange，
     * 在锁**外**调 `exchangeToken`。
     */
    private suspend fun ensureFreshToken(clientId: String, clientSecret: String) {
        val needsExchange =
            tokenMutex.withLock {
                val current = (tokenInfo as? TokenInfo.Platform)?.takeIf { !it.isExpired() }
                current == null
            }
        if (needsExchange) {
            exchangeToken(clientId, clientSecret)
        }
    }

    /**
     * 设置令牌信息
     * @param tokenInfo 要设置的令牌信息
     */
    @JsName("setTokenInfo")
    public fun setTokenInfo(tokenInfo: TokenInfo) {
        this.tokenInfo = tokenInfo
    }

    /**
     * 清除令牌信息
     * 清除当前存储的令牌并重置 HTTP 客户端
     */
    @JsName("clearToken")
    public fun clearToken() {
        tokenInfo = null
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
        // 1. 确保 platform 签名有效（隐式 exchange / refresh）
        ensurePlatformToken()

        val handler = callbackHandler ?: throw IllegalStateException("OAuth callback handler not set")
        val oauthConfig = OAuthConfig(enablePkce = true)

        val state = StateStoreInternal.generateState()
        StateStoreInternal.storeState(state, oauthConfig.timeoutSeconds / SECONDS_PER_MINUTE)

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

    /**
     * 确保 platform 签名有效（隐式 exchange / refresh）。
     * - 无 platform token：exchangeToken 拿新签名
     * - 有 platform token 但过期：refreshToken；失败则 fallback 到 exchangeToken
     * - 有 valid platform token：直接复用
     *
     * 必须先于 [loginWithOAuth] 内部 OAuth 流程调用。
     */
    private suspend fun ensurePlatformToken(): TokenInfo.Platform {
        val current = tokenInfo as? TokenInfo.Platform
        return when {
            current == null ->
                exchangeToken(
                    clientId = config.clientId ?: error("clientId not configured"),
                    clientSecret = config.clientSecret ?: error("clientSecret not configured"),
                )
            current.isExpired() ->
                try {
                    refreshToken()
                } catch (e: Exception) {
                    // refresh 失败后用 exchangeToken 作为 recovery path（spec scenario 要求）
                    val clientId = config.clientId
                    val clientSecret = config.clientSecret
                    if (clientId == null || clientSecret == null) {
                        throw IllegalStateException(
                            "loginWithOAuth recovery failed: refresh failed AND clientId/clientSecret not configured at SDK init. " +
                                "Original error: ${e.message}",
                            e,
                        )
                    }
                    exchangeToken(clientId, clientSecret)
                }
            else -> current
        }
    }

    private suspend fun processOAuthCallbackResult(
        result: OAuthCallbackResult,
        callbackUrl: String,
        codeVerifier: String?,
    ): TokenInfo {
        when (result) {
            is OAuthCallbackResult.Success -> {
                if (!StateStoreInternal.consumeState(result.state)) {
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
     * 平台签名包含 accessToken 和 apiKey（vds-docs 明示两者字面值相同），是后续 OAuth 流程的前置步骤。
     *
     * @param clientId 应用 ID（格式 vap_xxxx），SDK 统一使用 clientId 命名，与 VDS 文档中的 appId 等价
     * @param clientSecret 应用密钥
     * @return 平台签名（TokenInfo.Platform），包含 apiKey
     */
    @JsName("exchangeToken")
    public suspend fun exchangeToken(clientId: String, clientSecret: String): TokenInfo.Platform {
        val response =
            httpClient.post("${config.baseUrl}/api/auth/token") {
                contentType(ContentType.Application.Json)
                setBody(TokenExchangeRequest(clientId, clientSecret))
            }.body<TokenData>()

        val newTokenInfo = response.toTokenInfo()

        tokenMutex.withLock {
            tokenInfo = newTokenInfo
            // isOAuthToken / platformAccessToken 已删除（D4 字段已删）
        }

        return newTokenInfo
    }

    /**
     * 刷新访问令牌。
     * @return TokenInfo.Platform 新的令牌信息
     * @throws TokenExpiredException 如果没有可用的 platform token
     */
    @JsName("refreshToken")
    public suspend fun refreshToken(): TokenInfo.Platform {
        val currentApiKey =
            (tokenInfo as? TokenInfo.Platform)?.apiKey
                ?: throw TokenExpiredException("No platform token to refresh")

        val response =
            try {
                httpClient.post("${config.baseUrl}/api/auth/token/refresh") {
                    // refreshToken 端点用 X-Api-Key 头（per vds-docs 业务 API 风格），不发送 Bearer。
                    // 之前的 Bearer 是误用 apiKey（公开凭证）作 access token 的语义错误。
                    header("X-Api-Key", currentApiKey)
                }.body<TokenData>()
            } catch (e: ValidationException) {
                // RefreshTooEarly：旧 token 已不在 refresh 窗口，直接 exchange
                if (e.message?.contains("RefreshTooEarly") == true) {
                    return exchangeToken(
                        clientId = config.clientId ?: error("clientId not configured"),
                        clientSecret = config.clientSecret ?: error("clientSecret not configured"),
                    )
                }
                throw e
            }

        val newTokenInfo = response.toTokenInfo()

        tokenMutex.withLock { tokenInfo = newTokenInfo }
        return newTokenInfo
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
     * 按 vds-docs `签名交换端点.md` 标准形式，**不发送任何 platform Authorization 头**。
     * 客户端凭证（client_id / client_secret）直接在 form body 中。
     *
     * @param code OAuth 授权码（从回调 URL 中获取）
     * @param redirectUri 重定向 URI（必须与授权时一致）
     * @param codeVerifier PKCE code_verifier（如果使用了 PKCE）
     * @return OAuth 用户令牌信息（TokenInfo.OAuth）
     * @throws IllegalStateException 如果 clientId 或 clientSecret 未配置
     */
    @JsName("exchangeOAuthToken")
    @Suppress("ThrowsCount")
    public suspend fun exchangeOAuthToken(
        code: String,
        redirectUri: String,
        codeVerifier: String? = null,
    ): TokenInfo.OAuth {
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
                // ✅ 显式无 Authorization / X-Api-Key header（OAuth 端点不需要）
                setBody(requestBody)
            }.body<OAuthTokenData>()

        val newTokenInfo = response.toTokenInfo(redirectUri = redirectUri)

        tokenMutex.withLock {
            tokenInfo = newTokenInfo
            // isOAuthToken / oauthClientId / oauthRedirectUri 已删除（D4 字段已删）
        }
        return newTokenInfo
    }

    /**
     * 查询已授权用户的公开信息。
     *
     * 按 vds-docs `用户信息端点.md` 标准形式，**只发送 `Authorization: Bearer <oauthToken>` 头**。
     *
     * @return 用户信息数据（UserInfoData）
     * @throws IllegalStateException 如果没有有效的 OAuth token
     */
    @JsName("getUserInfo")
    public suspend fun getUserInfo(): UserInfoData {
        val oauth =
            (tokenInfo as? TokenInfo.OAuth)?.takeIf { !it.isExpired() }
                ?: throw IllegalStateException("No valid OAuth token. Call loginWithOAuth() first.")
        val response =
            httpClient.get("${config.baseUrl}/api/proxy/account/sso/userinfo") {
                header("Authorization", "Bearer ${oauth.oauthToken}")
                // ✅ 只发 Authorization Bearer（X-OAuth-Access-Token 已删除）
            }.body<UserInfoData>()

        return response
    }

    internal companion object {
        /** OAuth state 过期时间单位换算常量（每分钟多少秒）。 */
        internal const val SECONDS_PER_MINUTE: Int = 60

        /**
         * OAuth `state` 内部管理器。生成、验证、消费 state，
         * 作为 OAuth 回调服务器的 anti-CSRF token。
         *
         * 使用 [Mutex] 保护 [stateStorage] 读写，保证 KMP 线程安全。
         */
        internal object StateStoreInternal {
            private const val STATE_LENGTH = 32
            private const val DEFAULT_TIMEOUT_MINUTES = 10
            private const val MILLIS_PER_MINUTE = 60_000L
            private const val CHARS =
                "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"

            private val stateStorage: MutableMap<String, Long> = mutableMapOf()
            private val stateMutex = Mutex()

            fun generateState(): String =
                (1..STATE_LENGTH)
                    .map { CHARS.random(Random) }
                    .joinToString("")

            suspend fun storeState(state: String, timeoutMinutes: Int = DEFAULT_TIMEOUT_MINUTES) {
                val expiresAt =
                    Clock.System.now().toEpochMilliseconds() + timeoutMinutes * MILLIS_PER_MINUTE
                stateMutex.withLock { stateStorage[state] = expiresAt }
            }

            suspend fun consumeState(state: String): Boolean {
                var expiresAt: Long? = null
                stateMutex.withLock { expiresAt = stateStorage.remove(state) }
                return expiresAt?.let { Clock.System.now().toEpochMilliseconds() < it } ?: false
            }
        }
    }
}
