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

import com.furrist.rp.furtv.sdk.exception.OAuthException
import com.furrist.rp.furtv.sdk.exception.TokenExpiredException
import com.furrist.rp.furtv.sdk.exception.ValidationException
import com.furrist.rp.furtv.sdk.model.*
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlin.concurrent.Volatile
import kotlin.js.JsExport
import kotlin.js.JsName
import kotlin.random.Random
import kotlin.time.Clock
import kotlinx.coroutines.CancellationException
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
    httpClient: HttpClient? = null,
) {
    private val tokenMutex = Mutex()

    @Volatile
    private var tokenInfo: TokenInfo? = null

    private var injectedHttpClient: HttpClient? = httpClient
    internal val client: HttpClient
        get() = injectedHttpClient ?: error("HttpClient is not initialized in AuthManager. Did FursuitTvSdk finish initialization?")

    internal fun bindHttpClient(httpClient: HttpClient) {
        this.injectedHttpClient = httpClient
    }

    private var callbackHandler: OAuthCallbackHandler? = createDefaultOAuthHandler()

    /**
     * 获取当前 API 密钥（platform apiKey）。
     * @return 当前 platform apiKey，如果未认证或 token 不是 TokenInfo.Platform 则返回 null
     */
    @JsName("getApiKey")
    public fun getApiKey(): String? = (tokenInfo as? TokenInfo.Platform)?.apiKey

    /**
     * 获取当前完整的令牌信息。
     */
    @JsName("getTokenInfo")
    public fun getTokenInfo(): TokenInfo? = tokenInfo

    /**
     * 检查是否已认证
     * @return 如果已认证且令牌未过期返回 true，否则返回 false
     */
    @JsName("isAuthenticated")
    public fun isAuthenticated(): Boolean = tokenInfo?.isExpired()?.not() == true

    /**
     * 带令牌预检与重试的业务请求包装器。
     *
     * - 预检 `ensureFreshToken(clientId, clientSecret)`：过期/缺失时安全换新
     * - 执行 [block]；如抛 `TokenExpiredException`（401），再次 `ensureFreshToken` 后重试 [maxRetries] 次
     * - X-Api-Key 由 HTTP 客户端拦截层自动注入
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

    private suspend fun <T> runRetryLoop(remaining: Int, block: suspend () -> T): T {
        return try {
            block()
        } catch (e: TokenExpiredException) {
            if (remaining <= 0) throw e
            ensureFreshToken(requireClientId(), requireClientSecret())
            runRetryLoop(remaining - 1, block)
        }
    }

    private fun requireClientId(): String =
        config.clientId
            ?: error("withFreshToken requires SDK init with clientId. Configure via fursuitTvSdk { clientId = \"...\" }.")

    private fun requireClientSecret(): String =
        config.clientSecret
            ?: error("withFreshToken requires SDK init with clientSecret.")

    /**
     * 预检 token：采用 Double-Checked Locking（双检锁）模式。
     *
     * 1. 锁外快速判断：未过期直接返回；
     * 2. 加锁临界区：排队协程获取锁后二次校验，避免高并发 401 导致的惊群网络风暴；
     * 3. 临界区内直接调用原子网络操作更新状态。
     */
    private suspend fun ensureFreshToken(clientId: String, clientSecret: String) {
        // 1. 无锁快速路径
        val fastPlatform = (tokenInfo as? TokenInfo.Platform)?.takeIf { !it.isExpired() }
        if (fastPlatform != null) return

        // 2. 互斥临界区（Double-Checked Locking，避免并发惊群网络风暴）
        tokenMutex.withLock {
            val current = tokenInfo as? TokenInfo.Platform
            if (current != null && !current.isExpired()) {
                // 已被前序并发协程完成刷新，直接复用
            } else if (current != null) {
                tokenInfo =
                    try {
                        doRefreshToken(current.apiKey)
                    } catch (e: CancellationException) {
                        throw e
                    } catch (_: Exception) {
                        doExchangeToken(clientId, clientSecret)
                    }
            } else {
                tokenInfo = doExchangeToken(clientId, clientSecret)
            }
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
     * 清除当前存储的令牌（不影响已创建的 HTTP 客户端）
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
     */
    @JsName("loginWithOAuth")
    public suspend fun loginWithOAuth(scope: String? = "profile"): TokenInfo {
        ensurePlatformToken()

        val handler = callbackHandler ?: throw IllegalStateException("OAuth callback handler not set")
        val oauthConfig = OAuthConfig()

        val state = StateStoreInternal.generateState()
        StateStoreInternal.storeState(state, oauthConfig.timeoutSeconds / SECONDS_PER_MINUTE)

        val authorizeUrl =
            getOAuthAuthorizeUrl(
                redirectUri = handler.callbackUrl,
                scope = scope,
                state = state,
            )

        val result = handler.startAndGetCallback(authorizeUrl)

        return processOAuthCallbackResult(result, handler.callbackUrl)
    }

    private suspend fun ensurePlatformToken(): TokenInfo.Platform {
        val current = tokenInfo as? TokenInfo.Platform
        if (current != null && !current.isExpired()) return current

        ensureFreshToken(
            clientId = config.clientId ?: error("clientId not configured"),
            clientSecret = config.clientSecret ?: error("clientSecret not configured"),
        )
        return (tokenInfo as? TokenInfo.Platform)
            ?: error("Failed to acquire valid platform token")
    }

    private suspend fun processOAuthCallbackResult(
        result: OAuthCallbackResult,
        callbackUrl: String,
    ): TokenInfo {
        when (result) {
            is OAuthCallbackResult.Success -> {
                if (!StateStoreInternal.consumeState(result.state)) {
                    throw OAuthException("State mismatch or expired", errorCode = "state_mismatch")
                }
                return exchangeOAuthToken(result.code, callbackUrl)
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
     * 使用应用凭证（clientId + clientSecret）进行签名交换，获取平台签名。
     */
    @JsName("exchangeToken")
    public suspend fun exchangeToken(clientId: String, clientSecret: String): TokenInfo.Platform {
        val newTokenInfo =
            tokenMutex.withLock {
                val res = doExchangeToken(clientId, clientSecret)
                tokenInfo = res
                res
            }
        return newTokenInfo
    }

    /**
     * 无锁原子操作：签名交换网络请求。
     */
    private suspend fun doExchangeToken(clientId: String, clientSecret: String): TokenInfo.Platform {
        val response =
            client.post("${config.baseUrl}/api/auth/token") {
                contentType(ContentType.Application.Json)
                setBody(TokenExchangeRequest(clientId, clientSecret))
            }.body<TokenData>()
        return response.toTokenInfo()
    }

    /**
     * 刷新访问令牌。
     */
    @JsName("refreshToken")
    public suspend fun refreshToken(): TokenInfo.Platform {
        val currentApiKey =
            (tokenInfo as? TokenInfo.Platform)?.apiKey
                ?: throw TokenExpiredException("No platform token to refresh")

        val newTokenInfo =
            tokenMutex.withLock {
                val res = doRefreshToken(currentApiKey)
                tokenInfo = res
                res
            }
        return newTokenInfo
    }

    /**
     * 无锁原子操作：签名换新网络请求。
     */
    private suspend fun doRefreshToken(currentApiKey: String): TokenInfo.Platform {
        val response =
            try {
                client.post("${config.baseUrl}/api/auth/token/refresh") {
                    header("X-Api-Key", currentApiKey)
                }.body<TokenData>()
            } catch (e: ValidationException) {
                if (e.message?.contains("RefreshTooEarly") == true) {
                    val clientId = config.clientId ?: error("clientId not configured")
                    val clientSecret = config.clientSecret ?: error("clientSecret not configured")
                    return doExchangeToken(clientId, clientSecret)
                }
                throw e
            }
        return response.toTokenInfo()
    }

    /**
     * 生成 OAuth 授权 URL。
     */
    @JsName("getOAuthAuthorizeUrl")
    public fun getOAuthAuthorizeUrl(
        redirectUri: String,
        scope: String? = null,
        state: String? = null,
    ): String {
        val clientId = config.clientId ?: throw IllegalStateException("clientId is not configured in SDK")

        val queryParams =
            buildString {
                append("?client_id=${clientId.encodeURLParameter()}")
                append("&redirect_uri=${redirectUri.encodeURLParameter()}")
                append("&response_type=code")
                scope?.let { append("&scope=${it.encodeURLParameter()}") }
                state?.let { append("&state=${it.encodeURLParameter()}") }
            }
        return "${config.baseUrl}/api/proxy/account/sso/authorize$queryParams"
    }

    /**
     * 使用授权码换取 OAuth 用户令牌。
     */
    @JsName("exchangeOAuthToken")
    @Suppress("ThrowsCount")
    public suspend fun exchangeOAuthToken(
        code: String,
        redirectUri: String,
    ): TokenInfo.OAuth {
        val clientId = config.clientId ?: throw IllegalStateException("clientId is not configured in SDK")
        val clientSecret = config.clientSecret ?: throw IllegalStateException("clientSecret is not configured in SDK")

        val requestBody =
            mapOf(
                "grant_type" to "authorization_code",
                "client_id" to clientId,
                "client_secret" to clientSecret,
                "code" to code,
                "redirect_uri" to redirectUri,
            )

        val response =
            client.post("${config.baseUrl}/api/proxy/account/sso/token") {
                contentType(ContentType.Application.FormUrlEncoded)
                setBody(requestBody)
            }.body<OAuthTokenData>()

        val newTokenInfo = response.toTokenInfo(redirectUri = redirectUri)

        tokenMutex.withLock {
            tokenInfo = newTokenInfo
        }
        return newTokenInfo
    }

    /**
     * 查询已授权用户的公开信息。
     */
    @JsName("getUserInfo")
    public suspend fun getUserInfo(): UserInfoData {
        val oauth =
            (tokenInfo as? TokenInfo.OAuth)?.takeIf { !it.isExpired() }
                ?: throw IllegalStateException("No valid OAuth token. Call loginWithOAuth() first.")
        val response =
            client.get("${config.baseUrl}/api/proxy/account/sso/userinfo") {
                header("Authorization", "Bearer ${oauth.oauthToken}")
            }.body<UserInfoData>()

        return response
    }

    internal companion object {
        internal const val SECONDS_PER_MINUTE: Int = 60

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
