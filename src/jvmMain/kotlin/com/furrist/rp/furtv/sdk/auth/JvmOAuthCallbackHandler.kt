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
import io.ktor.server.application.*
import io.ktor.server.cio.*
import io.ktor.server.engine.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.util.logging.*
import java.awt.Desktop
import java.net.URI
import kotlin.time.Duration.Companion.seconds
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withTimeoutOrNull

private class JvmOAuthCallbackHandler(
    private val config: OAuthConfig,
) : OAuthCallbackHandler {
    private val logger = KtorSimpleLogger("com.furrist.rp.furtv.sdk.auth.JvmOAuthCallbackHandler")

    private val mutex = Mutex()

    @Volatile
    private var pendingDeferred: CompletableDeferred<OAuthCallbackResult>? = null

    override val callbackUrl: String get() = buildCallbackUrl(config)

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
        val deferred =
            mutex.withLock { pendingDeferred }
                ?: throw IllegalStateException("Not listening. Call startListening() first.")

        return try {
            val timeoutDuration = config.timeoutSeconds.toLong().seconds
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
        logger.info("Please open this URL in your browser: $authorizeUrl")
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
 * JVM 实现：创建 [JvmOAuthCallbackHandler]（基于 Ktor CIO，自动打开浏览器）。
 */
public actual fun createDefaultOAuthHandler(config: OAuthConfig): OAuthCallbackHandler =
    JvmOAuthCallbackHandler(config)
