package com.furrist.rp.furtv.sdk.auth

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

public class NativeOAuthCallbackHandler(
    private val config: OAuthCallbackServerConfig = OAuthCallbackServerConfig.DEFAULT,
) : OAuthCallbackHandler {
    override val callbackUrl: String get() = config.buildCallbackUrl()

    private val mutex = Mutex()
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private var pendingCallback: CompletableDeferred<OAuthCallbackResult>? = null
    private var selectorManager: SelectorManager? = null
    private var serverSocket: ServerSocket? = null

    override suspend fun startListening() {
        mutex.withLock {
            if (serverSocket != null) return@withLock

            val deferred = CompletableDeferred<OAuthCallbackResult>()
            pendingCallback = deferred

            val selector = SelectorManager()
            selectorManager = selector

            val server = aSocket(selector).tcp().bind(config.callbackHost, config.callbackPort)
            serverSocket = server

            scope.launch {
                try {
                    while (isActive) {
                        val socket = server.accept()
                        handleConnection(socket, deferred)
                    }
                } catch (_: Exception) {
                }
            }
        }
    }

    @Suppress("ReturnCount")
    private suspend fun handleConnection(
        socket: io.ktor.network.sockets.Socket,
        deferred: CompletableDeferred<OAuthCallbackResult>,
    ) {
        val input = socket.openReadChannel()
        val output = socket.openWriteChannel()

        try {
            val buffer = ByteArray(4096)
            val bytesRead = input.readAvailable(buffer)
            if (bytesRead <= 0) return
            val requestText = buffer.decodeToString(0, bytesRead)
            val requestLine = requestText.substringBefore("\r\n")
            val pathWithQuery = requestLine.substringAfter(" ").substringBefore(" ")
            val queryString = pathWithQuery.substringAfter("?", "")

            val params = parseQueryString(queryString)
            val error = params["error"]

            if (error != null) {
                val errorDescription = params["error_description"] ?: error
                deferred.complete(
                    OAuthCallbackResult.Error(message = errorDescription, errorCode = error),
                )
                val body = "Authorization failed: $error"
                output.writeString(
                    "HTTP/1.1 400 Bad Request\r\nContent-Type: text/html\r\nConnection: close\r\n\r\n$body",
                )
                output.flush()
                return
            }

            val code = params["code"]
            val state = params["state"]

            if (code == null || state == null) {
                output.writeString(
                    "HTTP/1.1 400 Bad Request\r\n" +
                        "Content-Type: text/html\r\nConnection: close\r\n\r\nMissing code or state",
                )
                output.flush()
                return
            }

            deferred.complete(OAuthCallbackResult.Success(code, state))
            output.writeString(
                "HTTP/1.1 200 OK\r\nContent-Type: text/html\r\nConnection: close\r\n" +
                    "\r\nSuccess! You can close this window.",
            )
            output.flush()
        } finally {
            socket.close()
        }
    }

    override suspend fun waitForCallback(): OAuthCallbackResult {
        val deferred = pendingCallback
            ?: throw IllegalStateException("Not listening. Call startListening() first.")
        return withTimeoutOrNull(config.timeoutSeconds.seconds) {
            deferred.await()
        } ?: OAuthCallbackResult.Error("Timeout waiting for OAuth callback")
    }

    override suspend fun startAndGetCallback(authorizeUrl: String): OAuthCallbackResult {
        startListening()
        println("Please open this URL in your browser: $authorizeUrl")
        return waitForCallback()
    }

    override suspend fun stop() {
        mutex.withLock {
            serverSocket?.close()
            serverSocket = null
            selectorManager?.close()
            selectorManager = null
            pendingCallback = null
        }
    }

    private fun OAuthCallbackServerConfig.buildCallbackUrl(): String {
        return URLBuilder(protocol = URLProtocol.HTTP, host = callbackHost, port = callbackPort).apply {
            path(callbackPath)
        }.buildString()
    }
}
