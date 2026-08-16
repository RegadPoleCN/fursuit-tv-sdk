package com.furrist.rp.furtv.sdk.auth

import com.furrist.rp.furtv.sdk.model.OAuthConfig
import kotlinx.browser.window
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.delay
import kotlinx.coroutines.withTimeoutOrNull
import org.w3c.dom.MessageEvent
import org.w3c.dom.events.EventListener

@JsExport
@JsName("JsOAuthCallbackHandler")
public class JsOAuthCallbackHandler(
    private val config: OAuthConfig,
) : OAuthCallbackHandler {
    override val callbackUrl: String = buildCallbackUrl(config)

    private val isBrowser: Boolean = js("typeof window !== \"undefined\"") as Boolean

    private var deferredResult: CompletableDeferred<OAuthCallbackResult>? = null
    private var messageListener: EventListener? = null

    private companion object {
        private const val NODE_POLL_INTERVAL_MS = 500L
    }

    override suspend fun startListening() {
        if (!isBrowser) return
        val deferred = CompletableDeferred<OAuthCallbackResult>()
        deferredResult = deferred
        val listener =
            EventListener { event ->
                @Suppress("UNCHECKED_CAST")
                val data =
                    (event as? MessageEvent)?.data?.toString()
                        ?.takeIf { it.isNotBlank() } ?: return@EventListener
                val params = parseQueryLike(data)
                handleAuthorizationCallback(params, deferred)
            }
        messageListener = listener
        window.addEventListener("message", listener)
    }

    override suspend fun waitForCallback(): OAuthCallbackResult {
        val timeoutMillis = config.timeoutSeconds * 1000L
        return withTimeoutOrNull(timeoutMillis) {
            deferredResult?.await()
                ?: run {
                    if (isBrowser) {
                        OAuthCallbackResult.Error("No callback received. Did the browser listen for messages?")
                    } else {
                        OAuthCallbackResult.Error(
                            "Node.js callback polling does not run in waitForCallback()." +
                                " Use startAndGetCallback().",
                        )
                    }
                }
        } ?: OAuthCallbackResult.Error("Timeout waiting for OAuth callback")
    }

    override suspend fun startAndGetCallback(authorizeUrl: String): OAuthCallbackResult {
        startListening()
        // In Node.js, attempt to open the URL via the `start` command if available (kept simple).
        if (!isBrowser) {
            try {
                js("console.log('Open this URL in your browser:', authorizeUrl)")
            } catch (_: Throwable) {
            }
        }
        return waitForCallback()
    }

    override suspend fun stop() {
        deferredResult?.complete(OAuthCallbackResult.Error("Callback handler stopped"))
        deferredResult = null
        if (isBrowser) {
            messageListener?.let { window.removeEventListener("message", it) }
        }
    }

    private fun parseQueryLike(payload: String): Map<String, String> {
        val params = mutableMapOf<String, String>()
        val pairs = payload.trimStart('{').trimEnd('}').split(',')
        for (pair in pairs) {
            val sides = pair.split(':', limit = 2)
            if (sides.size == 2) {
                val key = sides[0].trim().trim('"', '\'')
                val value = sides[1].trim().trim('"', '\'')
                params[key] = value
            }
        }
        return params
    }

    private fun handleAuthorizationCallback(
        params: Map<String, String>,
        deferred: CompletableDeferred<OAuthCallbackResult>,
    ) {
        val error = params["error"]
        if (error != null) {
            deferred.complete(
                OAuthCallbackResult.Error(
                    message = params["error_description"] ?: error,
                    errorCode = error,
                ),
            )
            return
        }
        val code = params["code"]
        val state = params["state"]
        if (code == null || state == null) {
            deferred.complete(OAuthCallbackResult.Error("Missing code or state in browser callback"))
            return
        }
        deferred.complete(OAuthCallbackResult.Success(code = code, state = state))
    }
}

/**
 * JS 实现：创建 [JsOAuthCallbackHandler]（运行时检测浏览器 postMessage 或 Node.js http 模块）。
 */
@JsExport
@JsName("createDefaultOAuthHandler")
public actual fun createDefaultOAuthHandler(config: OAuthConfig): OAuthCallbackHandler =
    JsOAuthCallbackHandler(config)

/**
 * 构建回调 URL（HTTP 协议下的本地 URL，例如 `http://localhost:8080/callback`）。
 *
 * JS 环境中 `localhost` 默认使用 HTTP，因为没有本地服务器证书。
 */
public fun buildCallbackUrl(c: OAuthConfig): String = buildString {
    append("http://")
    append(c.callbackHost)
    if (c.callbackPort != 80) {
        append(':')
        append(c.callbackPort)
    }
    append(c.callbackPath)
}

@Suppress("UnusedPrivateMember")
private suspend fun nodeCallbackPoll() {
    // 占位 Node.js 回调轮询（如果 SDK 内部需要使用 http.Server，可以在此扩展）。
    while (true) {
        delay(NODE_POLL_INTERVAL_MS)
    }
}

private const val NODE_POLL_INTERVAL_MS: Long = 500L
