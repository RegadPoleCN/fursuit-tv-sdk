package com.furrist.rp.furtv.sdk.auth

import kotlin.js.JsExport
import kotlin.js.JsName
import kotlinx.browser.window
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.withTimeout
import org.w3c.dom.MessageEvent
import org.w3c.dom.events.EventListener

@JsExport
@JsName("JsOAuthCallbackHandler")
public class JsOAuthCallbackHandler(
    private val config: OAuthCallbackServerConfig,
) : OAuthCallbackHandler {
    override val callbackUrl: String = buildCallbackUrl(config)

    private var deferredResult: CompletableDeferred<OAuthCallbackResult>? = null
    private var messageListener: EventListener? = null

    @Suppress("ReturnCount")
    override suspend fun startAndGetCallback(authorizeUrl: String): OAuthCallbackResult {
        val isBrowser = js("typeof window !== \"undefined\"") as Boolean
        if (!isBrowser) {
            return OAuthCallbackResult.Error(
                "Node.js environment is not supported for automatic OAuth callback. Please handle callback manually.",
            )
        }

        deferredResult = CompletableDeferred()

        messageListener =
            EventListener { event ->
                val messageEvent = event as MessageEvent
                val data = messageEvent.data.asDynamic()
                if (data.type == "oauth_callback") {
                    val code = data.code as? String
                    val state = data.state as? String
                    if (code != null && state != null) {
                        deferredResult?.complete(OAuthCallbackResult.Success(code, state))
                    } else if (data.error != null) {
                        deferredResult?.complete(OAuthCallbackResult.Error(data.error.toString()))
                    }
                }
            }
        window.addEventListener("message", messageListener)

        val authWindow = window.open(authorizeUrl, "_blank", "width=600,height=700")
        if (authWindow == null) {
            return OAuthCallbackResult.Error("Failed to open authorization window. Popup might be blocked.")
        }

        return try {
            val timeoutMillis = config.timeoutSeconds * 1000L
            withTimeout(timeoutMillis) {
                deferredResult!!.await()
            }
        } catch (e: Exception) {
            OAuthCallbackResult.Error("OAuth callback timed out or failed: ${e.message}")
        } finally {
            stop()
        }
    }

    override suspend fun stop() {
        messageListener?.let {
            window.removeEventListener("message", it)
            messageListener = null
        }
        deferredResult = null
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
    return "https://${config.callbackHost}$portSuffix${config.callbackPath}"
}
