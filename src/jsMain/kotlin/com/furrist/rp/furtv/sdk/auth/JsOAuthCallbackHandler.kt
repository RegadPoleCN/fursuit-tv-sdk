package com.furrist.rp.furtv.sdk.auth

import kotlin.js.JsExport
import kotlin.js.JsName
import kotlinx.browser.window
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.delay
import kotlinx.coroutines.withTimeoutOrNull
import org.w3c.dom.MessageEvent
import org.w3c.dom.events.EventListener

@JsExport
@JsName("JsOAuthCallbackHandler")
public class JsOAuthCallbackHandler(
    private val config: OAuthCallbackServerConfig,
) : OAuthCallbackHandler {
    override val callbackUrl: String = buildCallbackUrl(config)

    private val isBrowser: Boolean = js("typeof window !== \"undefined\"") as Boolean

    private var deferredResult: CompletableDeferred<OAuthCallbackResult>? = null
    private var messageListener: EventListener? = null

    override suspend fun startListening() {
        if (isBrowser) {
            startBrowserListening()
        } else {
            startNodeJsListening()
        }
    }

    private fun startBrowserListening() {
        messageListener?.let { window.removeEventListener("message", it) }
        messageListener = null
        deferredResult = CompletableDeferred()
        messageListener =
            EventListener { event ->
                val messageEvent = event as MessageEvent
                val data = messageEvent.data.asDynamic()
                if (data.type == "oauth_callback") {
                    val code = data.code as? String
                    val state = data.state as? String
                    val error = data.error as? String
                    val errorDescription = data.error_description as? String
                    if (code != null && state != null) {
                        deferredResult?.complete(OAuthCallbackResult.Success(code, state))
                    } else if (error != null) {
                        deferredResult?.complete(
                            OAuthCallbackResult.Error(
                                errorDescription ?: error,
                                error,
                            ),
                        )
                    }
                }
            }
        window.addEventListener("message", messageListener)
    }

    private fun startNodeJsListening() {
        js("""
            if (this._nodeServer) {
                this._nodeServer.close();
                this._nodeServer = null;
            }
        """)
        this.asDynamic()._nodeResult = null
        this.asDynamic()._callbackPort = config.callbackPort
        this.asDynamic()._callbackPath = config.callbackPath
        js("""
            var http = require('http');
            var urlModule = require('url');
            var self = this;
            var port = self._callbackPort;
            var path = self._callbackPath;
            var server = http.createServer(function(req, res) {
                var parsedUrl = urlModule.parse(req.url, true);
                var query = parsedUrl.query;
                if (query.error) {
                    res.writeHead(400, {'Content-Type': 'text/html'});
                    res.end('Authorization failed: ' + (query.error_description || query.error));
                    self._nodeResult = {error: query.error, message: query.error_description || query.error};
                } else if (query.code && query.state) {
                    res.writeHead(200, {'Content-Type': 'text/html'});
                    res.end('Success! You can close this window.');
                    self._nodeResult = {code: query.code, state: query.state};
                } else {
                    res.writeHead(400, {'Content-Type': 'text/html'});
                    res.end('Missing code or state parameter.');
                }
            });
            server.listen(port, 'localhost', function() {
                console.log('OAuth callback server listening on http://localhost:' + port + path);
            });
            self._nodeServer = server;
        """)
    }

    override suspend fun waitForCallback(): OAuthCallbackResult {
        return if (isBrowser) {
            waitForBrowserCallback()
        } else {
            waitForNodeJsCallback()
        }
    }

    private suspend fun waitForBrowserCallback(): OAuthCallbackResult {
        val deferred = deferredResult
            ?: return OAuthCallbackResult.Error("Not listening. Call startListening() first.")
        val timeoutMillis = config.timeoutSeconds * 1000L
        return withTimeoutOrNull(timeoutMillis) {
            deferred.await()
        } ?: OAuthCallbackResult.Error("OAuth callback timed out")
    }

    private suspend fun waitForNodeJsCallback(): OAuthCallbackResult {
        val timeoutMillis = config.timeoutSeconds * 1000L
        return withTimeoutOrNull(timeoutMillis) {
            while (this.asDynamic()._nodeResult == null) {
                delay(500L)
            }
            val result: dynamic = this.asDynamic()._nodeResult
            if (result.code != null) {
                OAuthCallbackResult.Success(result.code as String, result.state as String)
            } else {
                OAuthCallbackResult.Error(
                    (result.message as? String) ?: "Unknown error",
                    result.error as? String,
                )
            }
        } ?: OAuthCallbackResult.Error("OAuth callback timed out")
    }

    override suspend fun startAndGetCallback(authorizeUrl: String): OAuthCallbackResult {
        try {
            startListening()
            if (isBrowser) {
                val authWindow = window.open(authorizeUrl, "oauth", "width=600,height=800")
                if (authWindow == null) {
                    return OAuthCallbackResult.Error(
                        "Failed to open authorization window. Popup might be blocked.",
                    )
                }
            } else {
                println("Please open this URL in your browser: $authorizeUrl")
            }
            return waitForCallback()
        } finally {
            stop()
        }
    }

    override suspend fun stop() {
        if (isBrowser) {
            messageListener?.let {
                window.removeEventListener("message", it)
                messageListener = null
            }
            deferredResult = null
        } else {
            stopNodeJs()
        }
    }

    private fun stopNodeJs() {
        js("""
            if (this._nodeServer) {
                this._nodeServer.close();
                this._nodeServer = null;
            }
            this._nodeResult = null;
        """)
    }
}

private const val JS_HTTP_PORT: Int = 80
private const val JS_HTTPS_PORT: Int = 443

private fun buildCallbackUrl(config: OAuthCallbackServerConfig): String {
    val portSuffix =
        if (config.callbackPort != JS_HTTP_PORT && config.callbackPort != JS_HTTPS_PORT) {
            ":${config.callbackPort}"
        } else {
            ""
        }
    return "http://${config.callbackHost}$portSuffix${config.callbackPath}"
}
