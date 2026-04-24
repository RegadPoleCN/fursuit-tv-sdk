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
import io.ktor.util.logging.KtorSimpleLogger
import kotlin.time.Duration.Companion.seconds
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withTimeoutOrNull

/**
 * OAuth 回调服务器配置。
 *
 * 定义本地 HTTP 服务器的行为，用于接收 OAuth 2.0 授权回调。
 *
 * @param callbackHost 服务器地址（默认 "localhost"）
 * @param callbackPort 端口号（默认 8080，范围 1-65535）
 * @param callbackPath 回调路径（默认 "/callback"，必须以 "/" 开头）
 * @param timeoutSeconds 超时时间（默认 300 秒/5 分钟，必须 > 0）
 *
 * @throws IllegalArgumentException 参数验证失败
 */
public data class OAuthCallbackServerConfig(
    public val callbackHost: String = "localhost",
    public val callbackPort: Int = 8080,
    public val callbackPath: String = "/callback",
    public val timeoutSeconds: Long = 300,
) {
    init {
        require(callbackPort in 1..65535) { "callbackPort must be between 1 and 65535" }
        require(callbackPath.startsWith("/")) { "callbackPath must start with '/'" }
        require(callbackHost.isNotBlank()) { "callbackHost must not be blank" }
        require(timeoutSeconds > 0) { "timeoutSeconds must be positive" }
    }
    /**
     * 构建完整的回调 URL。
     *
     * @return 回调 URL，如 "http://localhost:8080/callback"
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
 * OAuth 回调处理器（基于 Ktor CIO 嵌入式服务器）。
 *
 * 启动本地 HTTP 服务器接收 OAuth 授权回调，协程安全（Mutex 保护）。
 *
 * @property config 服务器配置
 * @property callbackUrl 完整的回调 URL
 */
public class OAuthCallbackHandler(
    private val config: OAuthCallbackServerConfig,
) {
    /**
     * 日志记录器
     */
    private val logger = KtorSimpleLogger("com.furrist.rp.furtv.sdk.auth.OAuthCallbackHandler")

    /**
     * 回调 URL
     */
    public val callbackUrl: String get() = config.buildCallbackUrl()

    /**
     * 服务器停止相关常量
     */
    private companion object {
        private const val GRACE_PERIOD_MS = 1000L
        private const val STOP_TIMEOUT_MS = 2000L
    }

    private val server =
        embeddedServer(CIO, port = config.callbackPort, host = config.callbackHost) {
            routing {
                get(config.callbackPath) {
                    handleCallback(this.call)
                }
            }
        }

    private val pendingCallbacks = ConcurrentMap<String, CompletableDeferred<OAuthCallbackResult>>()
    private val mutex = Mutex()

    /**
     * 启动服务器并等待 OAuth 回调。
     *
     * @param authorizeUrl 授权 URL（包含 state 参数）
     * @return 回调结果（成功或失败）
     */
    public suspend fun startAndGetCallback(authorizeUrl: String): OAuthCallbackResult {
        // CompletableDeferred acts as a one-time result holder
        // It bridges the gap between async HTTP callback and coroutine-based waiting
        // The deferred completes when handleCallback() is invoked by the Ktor server
        val deferred = CompletableDeferred<OAuthCallbackResult>()

        // Extract state parameter from authorize URL query string
        // Format: https://server?...&state=abc123&...
        // Used to match callback with original authorization request for CSRF protection
        val state = extractStateFromUrl(authorizeUrl)

        // Use mutex to ensure thread-safe access to server state and pending callbacks
        // This prevents race conditions when multiple coroutines interact with the handler
        return mutex.withLock {
            try {
                logger.info("Starting OAuth callback server on ${config.callbackHost}:${config.callbackPort}${config.callbackPath}")
                server.start(wait = false)

                if (state != null) {
                    pendingCallbacks[state] = deferred
                }

                val timeoutDuration = config.timeoutSeconds.seconds
                logger.debug("Waiting for OAuth callback with timeout of ${config.timeoutSeconds}s, state=$state")

                // withTimeoutOrNull returns null if timeout expires without completion
                // This allows graceful handling of user abandonment or network issues
                // The timeout duration comes from config to allow customization per use case
                val result =
                    withTimeoutOrNull(timeoutDuration) {
                        deferred.await()
                    }

                if (result == null) {
                    val timeoutMsg = "OAuth callback timed out after ${config.timeoutSeconds}s (host=${config.callbackHost}, port=${config.callbackPort})"
                    logger.warn(timeoutMsg)
                    OAuthCallbackResult.Error(timeoutMsg)
                } else {
                    logger.info("OAuth callback received successfully, state=$state")
                    result
                }
            } catch (e: CancellationException) {
                logger.error("OAuth callback cancelled (host=${config.callbackHost}, port=${config.callbackPort}): ${e.message}", e)
                throw e
            } catch (e: IllegalStateException) {
                val errorMsg = "Failed to start OAuth callback server on ${config.callbackHost}:${config.callbackPort}: ${e.message}"
                logger.error(errorMsg, e)
                OAuthCallbackResult.Error(errorMsg, e)
            } catch (e: java.net.BindException) {
                val errorMsg = "Port ${config.callbackPort} is already in use on ${config.callbackHost}, cannot start OAuth callback server"
                logger.error(errorMsg, e)
                OAuthCallbackResult.Error(errorMsg, e)
            } catch (e: Exception) {
                val errorMsg = "Unexpected error during OAuth callback on ${config.callbackHost}:${config.callbackPort} (timeout=${config.timeoutSeconds}s): ${e.message}"
                logger.error(errorMsg, e)
                OAuthCallbackResult.Error(errorMsg, e)
            } finally {
                pendingCallbacks.clear()
                stopInternal()
            }
        }
    }

    /**
     * 停止服务器并释放资源（幂等，可多次调用）。
     */
    public suspend fun stop() {
        mutex.withLock {
            stopInternal()
        }
    }

    /**
     * 内部方法：执行服务器关闭逻辑。
     */
    private fun stopInternal() {
        try {
            logger.info("Stopping OAuth callback server on ${config.callbackHost}:${config.callbackPort}")
            server.stop(gracePeriodMillis = GRACE_PERIOD_MS, timeoutMillis = STOP_TIMEOUT_MS)
            pendingCallbacks.clear()
            logger.info("OAuth callback server stopped successfully")
        } catch (e: CancellationException) {
            logger.error("Cancellation during server stop: ${e.message}", e)
            pendingCallbacks.clear()
            throw e
        } catch (e: IllegalStateException) {
            logger.debug("Server was not running or already stopped, ignoring: ${e.message}")
            pendingCallbacks.clear()
        } catch (e: Exception) {
            logger.warn("Error during server stop (non-fatal): ${e.message}", e)
            pendingCallbacks.clear()
        }
    }

    /**
     * 从授权 URL 中提取 state 参数。
     *
     * @param url 授权 URL
     * @return state 值，不存在返回 null
     */
    private fun extractStateFromUrl(url: String): String? {
        return url.substringAfter("state=", "").substringBefore("&")
            .takeIf { it.isNotEmpty() }
    }

    /**
     * 处理 OAuth 回调 HTTP 请求。
     *
     * 验证参数并完成对应的 CompletableDeferred。
     */
    private suspend fun handleCallback(call: ApplicationCall) {
        val code = call.request.queryParameters["code"]
        val state = call.request.queryParameters["state"]
        logger.debug("Received OAuth callback request, code=${code?.take(8)}..., state=$state")

        if (code == null || state == null) {
            logger.warn("OAuth callback missing required parameters: code=$code, state=$state")
            call.respondText("Missing code or state", status = HttpStatusCode.BadRequest)
            return
        }

        val deferred = pendingCallbacks[state]
        if (deferred == null) {
            logger.warn("OAuth callback received with unknown state: $state")
            call.respondText("Unknown state", status = HttpStatusCode.BadRequest)
            return
        }

        deferred.complete(OAuthCallbackResult.Success(code, state))
        call.respondText("Success")
    }
}
