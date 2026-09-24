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
import kotlin.time.Duration.Companion.milliseconds
import kotlinx.browser.window
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.withTimeoutOrNull
import org.w3c.dom.MessageEvent
import org.w3c.dom.events.EventListener

/**
 * JS 平台 OAuth 回调处理器。
 *
 * 运行时自动区分两种环境：
 * - **浏览器**：通过 `window.postMessage` 接收中继页转发的回调（支持原生 JS Object 与 JSON 字符串）
 * - **Node.js**：动态加载 `node:http` 在本机启动回调服务器，直接接收 OAuth 重定向
 */
@JsExport
@JsName("JsOAuthCallbackHandler")
public class JsOAuthCallbackHandler(
    private val config: OAuthConfig,
) : OAuthCallbackHandler {
    override val callbackUrl: String = buildCallbackUrl(config)

    private val isBrowser: Boolean = js("typeof window !== \"undefined\"") as Boolean

    private var deferredResult: CompletableDeferred<OAuthCallbackResult>? = null
    private var messageListener: EventListener? = null

    // Node.js 本地回调服务器（dynamic 持有 node:http Server 实例）
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
                val rawData = (event as? MessageEvent)?.data ?: return@EventListener
                val params = extractParamsFromData(rawData)
                if (params.isNotEmpty()) {
                    handleAuthorizationCallback(params, deferred)
                }
            }
        messageListener = listener
        window.addEventListener("message", listener)
    }

    private fun startNodeServer() {
        val deferred = CompletableDeferred<OAuthCallbackResult>()
        deferredResult = deferred
        val http = importNodeHttp().await<dynamic>()
        val server =
            http.createServer { req, res ->
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
        return withTimeoutOrNull(timeoutMillis.milliseconds) {
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
        // Node.js 无通用浏览器打开方式，控制台输出 URL 提示用户手动复制
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

    /**
     * 兼容原生 JS 对象、JSON 字符串以及 query-string 格式的 postMessage 数据。
     */
    private fun extractParamsFromData(raw: dynamic): Map<String, String> {
        if (raw == null) return emptyMap()
        return when {
            js("typeof raw === 'object'") as Boolean -> {
                val map = mutableMapOf<String, String>()
                val code = raw.code as? String
                val state = raw.state as? String
                val error = raw.error as? String
                val errorDesc = (raw.error_description ?: raw.errorDescription) as? String
                if (code != null) map["code"] = code
                if (state != null) map["state"] = state
                if (error != null) map["error"] = error
                if (errorDesc != null) map["error_description"] = errorDesc
                map
            }
            else -> {
                val str = raw.toString()
                if (str.isBlank()) {
                    emptyMap()
                } else {
                    val parsedObj: dynamic =
                        try {
                            val parsed = JSON.parse<dynamic>(str)
                            if (parsed != null && js("typeof parsed === 'object'") as Boolean) parsed else null
                        } catch (_: Throwable) {
                            null
                        }
                    if (parsedObj != null) {
                        extractParamsFromData(parsedObj)
                    } else {
                        parseQueryLike(str)
                    }
                }
            }
        }
    }

    private fun parseQueryLike(payload: String): Map<String, String> {
        val params = mutableMapOf<String, String>()
        val pairs = payload.trimStart('{').trimEnd('}').split('&', ',')
        for (pair in pairs) {
            val delimiter = if (pair.contains('=')) '=' else ':'
            val sides = pair.split(delimiter, limit = 2)
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
