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
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withTimeoutOrNull

/**
 * Jvm 实现的 OAuth 回调处理器（基于 Ktor CIO）
 */
internal class JvmOAuthCallbackHandler(
    private val config: OAuthCallbackServerConfig,
) : OAuthCallbackHandler {
    private val logger = KtorSimpleLogger("com.furrist.rp.furtv.sdk.auth.JvmOAuthCallbackHandler")
    private val mutex = Mutex()
    private val pendingCallbacks = ConcurrentMap<String, CompletableDeferred<OAuthCallbackResult>>()

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

    override suspend fun startAndGetCallback(authorizeUrl: String): OAuthCallbackResult {
        val deferred = CompletableDeferred<OAuthCallbackResult>()
        val state = extractStateFromUrl(authorizeUrl)

        return mutex.withLock {
            try {
                server.start(wait = false)
                if (state != null) pendingCallbacks[state] = deferred

                val timeoutDuration = config.timeoutSeconds.seconds
                val result = withTimeoutOrNull(timeoutDuration) { deferred.await() }

                result ?: OAuthCallbackResult.Error("Timeout waiting for OAuth callback")
            } finally {
                pendingCallbacks.clear()
                server.stop(shutdownGracePeriodMillis, shutdownTimeoutMillis)
            }
        }
    }

    override suspend fun stop() {
        mutex.withLock {
            server.stop(shutdownGracePeriodMillis, shutdownTimeoutMillis)
        }
    }

    private suspend fun handleCallback(call: ApplicationCall) {
        val code = call.request.queryParameters["code"]
        val state = call.request.queryParameters["state"]

        if (code == null || state == null) {
            call.respondText("Missing code or state", status = HttpStatusCode.BadRequest)
            return
        }

        pendingCallbacks[state]?.complete(OAuthCallbackResult.Success(code, state))
        call.respondText("Success! You can close this window.")
    }

    private fun extractStateFromUrl(url: String): String? {
        return url.substringAfter("state=", "").substringBefore("&").takeIf { it.isNotEmpty() }
    }

    private fun OAuthCallbackServerConfig.buildCallbackUrl(): String {
        return URLBuilder(protocol = URLProtocol.HTTP, host = callbackHost, port = callbackPort).apply {
            path(callbackPath)
        }.buildString()
    }
}
