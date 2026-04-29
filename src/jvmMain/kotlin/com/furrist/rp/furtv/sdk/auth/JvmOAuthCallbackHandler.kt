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
import io.ktor.util.logging.KtorSimpleLogger
import java.awt.Desktop
import java.net.URI
import kotlin.time.Duration.Companion.seconds
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withTimeoutOrNull

internal class JvmOAuthCallbackHandler(
    private val config: OAuthCallbackServerConfig,
) : OAuthCallbackHandler {
    private val logger = KtorSimpleLogger("com.furrist.rp.furtv.sdk.auth.JvmOAuthCallbackHandler")
    private val mutex = Mutex()
    @Volatile
    private var pendingDeferred: CompletableDeferred<OAuthCallbackResult>? = null

    override val callbackUrl: String get() = config.buildCallbackUrl()

    private val server =
        embeddedServer(CIO, port = config.callbackPort, host = config.callbackHost) {
            routing {
                get(config.callbackPath) {
                    handleCallback(this.call)
                }
            }
        }

    private val shutdownGracePeriodMillis: Long = 1000L
    private val shutdownTimeoutMillis: Long = 2000L

    override suspend fun startListening() {
        mutex.withLock {
            pendingDeferred = CompletableDeferred()
            server.start(wait = false)
        }
    }

    override suspend fun waitForCallback(): OAuthCallbackResult {
        val deferred = mutex.withLock { pendingDeferred }
            ?: throw IllegalStateException("Not listening. Call startListening() first.")

        return try {
            val timeoutDuration = config.timeoutSeconds.seconds
            withTimeoutOrNull(timeoutDuration) {
                deferred.await()
            } ?: OAuthCallbackResult.Error("Timeout waiting for OAuth callback")
        } finally {
            mutex.withLock {
                pendingDeferred = null
                server.stop(shutdownGracePeriodMillis, shutdownTimeoutMillis)
            }
        }
    }

    override suspend fun startAndGetCallback(authorizeUrl: String): OAuthCallbackResult {
        startListening()
        openBrowser(authorizeUrl)
        return waitForCallback()
    }

    override suspend fun stop() {
        mutex.withLock {
            pendingDeferred?.complete(OAuthCallbackResult.Error("Server stopped"))
            pendingDeferred = null
            server.stop(shutdownGracePeriodMillis, shutdownTimeoutMillis)
        }
    }

    private fun openBrowser(authorizeUrl: String) {
        if (Desktop.isDesktopSupported()) {
            val desktop = Desktop.getDesktop()
            if (desktop.isSupported(Desktop.Action.BROWSE)) {
                try {
                    desktop.browse(URI(authorizeUrl))
                    return
                } catch (_: Exception) {
                    // Fall through to stdout fallback
                }
            }
        }
        println("Please open this URL in your browser: $authorizeUrl")
    }

    private suspend fun handleCallback(call: ApplicationCall) {
        val error = call.request.queryParameters["error"]
        if (error != null) {
            val errorDescription = call.request.queryParameters["error_description"] ?: error
            pendingDeferred?.complete(OAuthCallbackResult.Error(message = errorDescription, errorCode = error))
            call.respondText("Authorization denied. You can close this window.")
            return
        }

        val code = call.request.queryParameters["code"]
        val state = call.request.queryParameters["state"]

        if (code == null || state == null) {
            call.respondText("Missing code or state", status = HttpStatusCode.BadRequest)
            return
        }

        pendingDeferred?.complete(OAuthCallbackResult.Success(code, state))
        call.respondText("Success! You can close this window.")
    }

    private fun OAuthCallbackServerConfig.buildCallbackUrl(): String {
        return URLBuilder(protocol = URLProtocol.HTTP, host = callbackHost, port = callbackPort).apply {
            path(callbackPath)
        }.buildString()
    }
}
