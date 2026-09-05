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

import com.furrist.rp.furtv.sdk.model.OAuthConfig
import io.ktor.http.*
import io.ktor.network.selector.*
import io.ktor.network.sockets.*
import io.ktor.utils.io.*
import kotlin.concurrent.Volatile
import kotlin.time.Duration.Companion.seconds
import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

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
        private const val HEADER_END_NOT_FOUND = -1
        private val HEADER_TERMINATOR = byteArrayOf(0x0D, 0x0A, 0x0D, 0x0A)
        private const val RESPONSE_OK =
            "HTTP/1.1 200 OK\r\nContent-Type: text/plain\r\nConnection: close\r\n\r\n" +
                "Success. You can close this window.\n"
        private const val RESPONSE_BAD_REQUEST =
            "HTTP/1.1 400 Bad Request\r\nContent-Type: text/plain\r\nConnection: close\r\n\r\n" +
                "Bad request.\n"
    }

    override suspend fun startListening() {
        mutex.withLock {
            // 幂等守卫——已在监听时直接返回，避免二次 bind 抛 BindException
            if (serverSocket != null) return
            pendingDeferred = CompletableDeferred()
        }
        val selectorManager = SelectorManager(Dispatchers.Default)
        val server = aSocket(selectorManager).tcp().bind(config.callbackHost, config.callbackPort)
        serverSocket = server
        scope.launch {
            while (isActive) {
                val socket = server.accept()
                try {
                    val request = readCallbackRequest(socket.openReadChannel())
                    val firstLine = request.lineSequence().firstOrNull().orEmpty()
                    val rawQuery = firstLine.substringAfter('?').substringBefore(' ')
                    val parameters = parseQueryString(rawQuery)
                    val params = parameters.entries().associate { it.key to it.value.firstOrNull().orEmpty() }
                    handleRequest(params, socket)
                    try {
                        socket.close()
                    } catch (_: Throwable) {
                    }
                } catch (_: Throwable) {
                    // ignore
                }
            }
        }
    }

    /**
     * 读到请求头结束符（\r\n\r\n）即返回；浏览器 GET 保持连接时不再挂起到超时。
     * 有 body 时按 Content-Length 补充读取。
     */
    private suspend fun readCallbackRequest(readChannel: ByteReadChannel): String {
        val buf = ByteArray(READ_BUFFER_SIZE)
        var total = 0
        while (total < buf.size && findHeaderEnd(buf, total) < 0) {
            val n = readChannel.readAvailable(buf, total, buf.size - total)
            if (n <= 0) break
            total += n
        }
        var requestEnd = total
        val headerEnd = findHeaderEnd(buf, total)
        if (headerEnd >= 0) {
            val contentLength = parseContentLength(buf, headerEnd)
            if (contentLength > 0) {
                while (total < buf.size && total < headerEnd + contentLength) {
                    val n = readChannel.readAvailable(buf, total, buf.size - total)
                    if (n <= 0) break
                    total += n
                }
                requestEnd = total
            }
        }
        return buf.copyOf(requestEnd).decodeToString()
    }

    private fun findHeaderEnd(buf: ByteArray, limit: Int): Int {
        val t = HEADER_TERMINATOR
        outer@ for (i in 0..limit - t.size) {
            for (j in t.indices) if (buf[i + j] != t[j]) continue@outer
            return i + t.size
        }
        return HEADER_END_NOT_FOUND
    }

    private fun parseContentLength(buf: ByteArray, headerEnd: Int): Int {
        val headers = buf.copyOf(headerEnd).decodeToString()
        val line =
            headers.lineSequence().firstOrNull { it.startsWith("content-length:", ignoreCase = true) }
                ?: return 0
        return line.substringAfter(':').trim().toIntOrNull() ?: 0
    }

    private suspend fun handleRequest(
        params: Map<String, String>,
        socket: Socket,
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
