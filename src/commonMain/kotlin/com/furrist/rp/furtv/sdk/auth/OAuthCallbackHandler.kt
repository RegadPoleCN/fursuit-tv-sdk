package com.furrist.rp.furtv.sdk.auth

import io.ktor.http.HttpStatusCode
import io.ktor.http.URLBuilder
import io.ktor.http.URLProtocol
import io.ktor.http.path
import io.ktor.server.application.ApplicationCall
import io.ktor.server.cio.CIO
import io.ktor.server.engine.embeddedServer
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import io.ktor.util.collections.ConcurrentMap
import kotlin.random.Random
import kotlin.time.Duration.Companion.seconds
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withTimeoutOrNull

/**
 * OAuth 回调服务器配置
 * @property callbackHost 回调主机地址，默认为 localhost
 * @property callbackPort 回调端口，默认为 8080
 * @property callbackPath 回调路径，默认为 /callback
 * @property timeoutSeconds 超时时间（秒），默认为 300 秒（5 分钟）
 */
public data class OAuthCallbackServerConfig(
    public val callbackHost: String = "localhost",
    public val callbackPort: Int = 8080,
    public val callbackPath: String = "/callback",
    public val timeoutSeconds: Long = 300,
) {
    /**
     * 构建完整的回调 URL
     * @return 回调 URL 字符串
     */
    public fun buildCallbackUrl(): String {
        return URLBuilder(protocol = URLProtocol.HTTP, host = callbackHost, port = callbackPort).apply {
            path(callbackPath)
        }.buildString()
    }
}

/**
 * OAuth 回调结果
 * 统一的返回格式，所有平台使用相同的 sealed class
 */
public sealed class OAuthCallbackResult {
    /**
     * 成功回调
     * @param code 授权码
     * @param state 状态参数
     */
    public data class Success(
        public val code: String,
        public val state: String,
    ) : OAuthCallbackResult()

    /**
     * 错误回调
     * @param message 错误消息
     * @param cause 可选的异常原因
     */
    public data class Error(
        public val message: String,
        public val cause: Throwable? = null,
    ) : OAuthCallbackResult()
}

/**
 * OAuth 回调异常
 * 用于在 OAuth 回调过程中抛出异常
 */
public class OAuthCallbackException(
    message: String,
    cause: Throwable? = null,
) : Exception(message, cause)

/**
 * OAuth 回调处理器
 * 使用 Ktor CIO 引擎启动嵌入式 HTTP 服务器
 *
 * @param config OAuth 回调服务器配置
 */
public class OAuthCallbackHandler(
    private val config: OAuthCallbackServerConfig,
) {
    /**
     * 回调 URL
     */
    public val callbackUrl: String get() = config.buildCallbackUrl()

    // Constants for OAuth callback configuration
    private companion object {
        private const val STATE_BYTE_SIZE = 16
        private const val MAX_BYTE_VALUE = 256
        private const val HEX_RADIX = 16
        private const val HEX_PADDING = 2
    }

    private val server =
        embeddedServer(CIO, port = config.callbackPort, host = config.callbackHost) {
            routing {
                get(config.callbackPath) {
                    handleCallback(this.call)
                }
            }
        }

    /**
     * 生成随机 state 参数用于 CSRF 保护
     * 使用加密安全的随机数生成器生成 32 字符的十六进制字符串
     * @return 随机生成的 state 字符串
     */
    private fun generateState(): String {
        val bytes = ByteArray(STATE_BYTE_SIZE) { Random.nextInt(MAX_BYTE_VALUE).toByte() }
        return bytes.joinToString("") { byte ->
            val hex = (byte.toInt() and 0xFF).toString(HEX_RADIX).padStart(HEX_PADDING, '0')
            hex
        }
    }

    private val pendingCallbacks = ConcurrentMap<String, CompletableDeferred<OAuthCallbackResult>>()
    private val mutex = Mutex()

    /**
     * 启动回调处理器并等待回调
     * @param authorizeUrl 授权 URL，用于打开浏览器
     * @return OAuthCallbackResult 回调结果
     */
    public suspend fun startAndGetCallback(authorizeUrl: String): OAuthCallbackResult {
        val deferred = CompletableDeferred<OAuthCallbackResult>()
        val state = extractStateFromUrl(authorizeUrl)

        return mutex.withLock {
            try {
                server.start(wait = false)

                if (state != null) {
                    pendingCallbacks[state] = deferred
                }

                val timeoutDuration = config.timeoutSeconds.seconds
                val result =
                    withTimeoutOrNull(timeoutDuration) {
                        deferred.await()
                    }

                if (result == null) {
                    OAuthCallbackResult.Error("OAuth callback timeout")
                } else {
                    result
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: IllegalStateException) {
                OAuthCallbackResult.Error("Failed to start server: ${e.message}", e)
            } catch (e: Exception) {
                OAuthCallbackResult.Error("Failed to start server: ${e.message}", e)
            } finally {
                pendingCallbacks.clear()
                server.stop(gracePeriodMillis = 1000, timeoutMillis = 2000)
            }
        }
    }

    /**
     * 停止回调处理器
     */
    public suspend fun stop() {
        try {
            server.stop(gracePeriodMillis = 1000, timeoutMillis = 2000)
        } catch (e: CancellationException) {
            throw e
        } catch (e: IllegalStateException) {
            // Ignore illegal state errors during stop
        } catch (e: Exception) {
            // Ignore other stop errors
        }
    }

    private fun extractStateFromUrl(url: String): String? {
        return url.substringAfter("state=", "").substringBefore("&")
            .takeIf { it.isNotEmpty() }
    }

    private suspend fun handleCallback(call: ApplicationCall) {
        val code = call.request.queryParameters["code"]
        val state = call.request.queryParameters["state"]

        if (code == null || state == null) {
            call.respondText("Missing code or state", status = HttpStatusCode.BadRequest)
            return
        }

        val deferred = pendingCallbacks[state]
        if (deferred == null) {
            call.respondText("Unknown state", status = HttpStatusCode.BadRequest)
            return
        }

        deferred.complete(OAuthCallbackResult.Success(code, state))
        call.respondText("Success")
    }
}
