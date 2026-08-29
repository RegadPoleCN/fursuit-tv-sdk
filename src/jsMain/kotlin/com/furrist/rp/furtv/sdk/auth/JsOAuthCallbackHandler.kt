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
import io.ktor.http.parseQueryString
import kotlinx.browser.window
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.await
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

    // #5：Node.js 本地回调服务器（dynamic 持有 node:http Server 实例）
    private var nodeServer: dynamic = null

    private fun importNodeHttp(): dynamic = js("import('node:http')")

    override suspend fun startListening() {
        if (!isBrowser) {
            startNodeServer()
            return
        }
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

    private suspend fun startNodeServer() {
        val deferred = CompletableDeferred<OAuthCallbackResult>()
        deferredResult = deferred
        val http = importNodeHttp().await<dynamic>()
        val server = http.createServer { req, res ->
            try {
                val rawQuery = (req.url as String?).orEmpty().substringAfter('?', "")
                val parameters = parseQueryString(rawQuery)
                val params = parameters.entries().associate { it.key to it.value.firstOrNull().orEmpty() }
                handleAuthorizationCallback(params, deferred)
            } catch (_: Throwable) {
                // 回调解析失败不影响服务器继续监听
            }
            // 禁止向 dynamic 的 Node API 传 Kotlin 集合当 headers，一律用 setHeader
            res.setHeader("Content-Type", "text/plain")
            res.writeHead(200)
            res.end("Success. You can close this window.")
            nodeServer?.close()
        }
        nodeServer = server
        server.listen(config.callbackPort, config.callbackHost)
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
                            "Node.js callback server is not running. Call startListening() first.",
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
        nodeServer?.close()
        nodeServer = null
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
public fun buildCallbackUrl(c: OAuthConfig): String =
    buildString {
        append("http://")
        append(c.callbackHost)
        if (c.callbackPort != 80) {
            append(':')
            append(c.callbackPort)
        }
        append(c.callbackPath)
    }
