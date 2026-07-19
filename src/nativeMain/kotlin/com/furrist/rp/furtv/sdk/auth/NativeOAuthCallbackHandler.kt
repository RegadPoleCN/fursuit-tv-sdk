package com.furrist.rp.furtv.sdk.auth

import com.furrist.rp.furtv.sdk.model.OAuthConfig
import io.ktor.http.URLBuilder
import io.ktor.http.URLProtocol
import io.ktor.http.parseQueryString
import io.ktor.http.path
import io.ktor.network.selector.SelectorManager
import io.ktor.network.sockets.ServerSocket
import io.ktor.network.sockets.aSocket
import io.ktor.network.sockets.openReadChannel
import io.ktor.network.sockets.openWriteChannel
import io.ktor.utils.io.readAvailable
import io.ktor.utils.io.writeString
import kotlin.concurrent.Volatile
import kotlin.time.Duration.Companion.seconds
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withTimeoutOrNull

private class NativeOAuthCallbackHandler(
    private val config: OAuthConfig,
) : OAuthCallbackHandler {
    override val callbackUrl: String get() = buildCallbackUrl(config)

    private val mutex = Mutex()

    @Volatile
    private var pendingDeferred: CompletableDeferred<OAuthCallbackResult>? = null

    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    private var serverSocket: ServerSocket? = null

    private companion object {
        private const val READ_BUFFER_SIZE = 4096
        private const val RESPONSE_OK =
            "HTTP/1.1 200 OK\r\nContent-Type: text/plain\r\nConnection: close\r\n\r\n" +
                "Success. You can close this window.\n"
        private const val RESPONSE_BAD_REQUEST =
            "HTTP/1.1 400 Bad Request\r\nContent-Type: text/plain\r\nConnection: close\r\n\r\n" +
                "Bad request.\n"
    }

    override suspend fun startListening() {
        mutex.withLock {
            pendingDeferred = CompletableDeferred()
        }
        val selectorManager = SelectorManager(Dispatchers.Default)
        val server = aSocket(selectorManager).tcp().bind(config.callbackHost, config.callbackPort)
        serverSocket = server
        scope.launch {
            while (isActive) {
                val socket = server.accept()
                try {
                    val readChannel = socket.openReadChannel()
                    val buf = ByteArray(READ_BUFFER_SIZE)
                    var total = 0
                    while (total < buf.size) {
                        val n = readChannel.readAvailable(buf, total, buf.size - total)
                        if (n <= 0) break
                        total += n
                    }
                    val request = buf.copyOf(total).decodeToString()
                    val firstLine = request.lineSequence().firstOrNull().orEmpty()
                    val rawQuery = firstLine.substringAfter('?').substringBefore(' ')
                    val parameters = parseQueryString(rawQuery)
                    val params = parameters.entries().associate { it.key to it.value.firstOrNull().orEmpty() }
                    handleRequest(params, socket)
                } catch (_: Throwable) {
                    // ignore
                }
            }
        }
    }

    private suspend fun handleRequest(
        params: Map<String, String>,
        socket: io.ktor.network.sockets.Socket,
    ) {
        val deferred = pendingDeferred ?: return
        val outcome = dispatch(params)
        val message =
            when (outcome) {
                is OAuthCallbackResult.Error ->
                    if (outcome.message.isNotEmpty()) {
                        RESPONSE_OK + outcome.message + "\n"
                    } else {
                        RESPONSE_BAD_REQUEST
                    }
                is OAuthCallbackResult.Success -> RESPONSE_OK
            }
        try {
            socket.openWriteChannel(autoFlush = true).writeString(message)
        } catch (_: Throwable) {
        }
        deferred.complete(outcome)
    }

    private fun dispatch(params: Map<String, String>): OAuthCallbackResult {
        val error = params["error"]
        if (error != null) {
            return OAuthCallbackResult.Error(
                message = params["error_description"] ?: error,
                errorCode = error,
            )
        }
        val code = params["code"]
        val state = params["state"]
        return if (code == null || state == null) {
            OAuthCallbackResult.Error("Missing code or state")
        } else {
            OAuthCallbackResult.Success(code = code, state = state)
        }
    }

    override suspend fun waitForCallback(): OAuthCallbackResult {
        val deferred =
            mutex.withLock { pendingDeferred }
                ?: throw IllegalStateException("Not listening. Call startListening() first.")
        val timeoutDuration = config.timeoutSeconds.toLong().seconds
        val result =
            withTimeoutOrNull(timeoutDuration) { deferred.await() }
                ?: OAuthCallbackResult.Error("Timeout waiting for OAuth callback")
        mutex.withLock {
            pendingDeferred = null
        }
        return result
    }

    override suspend fun startAndGetCallback(authorizeUrl: String): OAuthCallbackResult {
        startListening()
        // 在 Native 平台没有通用浏览器打开方式，提示用户手动复制
        println("Please open this URL in your browser: $authorizeUrl")
        return waitForCallback()
    }

    override suspend fun stop() {
        mutex.withLock {
            pendingDeferred?.complete(OAuthCallbackResult.Error("Server stopped"))
            pendingDeferred = null
        }
        try {
            serverSocket?.close()
        } catch (_: Throwable) {
        }
        serverSocket = null
    }

    private fun buildCallbackUrl(c: OAuthConfig): String =
        URLBuilder(
            protocol = URLProtocol.HTTP,
            host = c.callbackHost,
            port = c.callbackPort,
        ).apply {
            path(c.callbackPath)
        }.buildString()
}

/**
 * Native 实现：创建 [NativeOAuthCallbackHandler]（基于 Ktor Network 本地 HTTP 服务器）。
 */
public actual fun createDefaultOAuthHandler(config: OAuthConfig): OAuthCallbackHandler =
    NativeOAuthCallbackHandler(config)
