@file:Suppress(
    "MatchingDeclarationName",
    "MagicNumber",
    "TooGenericExceptionCaught",
    "SwallowedException",
    "UnsafeCastFromDynamic"
)

package me.regadpole.furtv.sdk.auth

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.await
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.random.Random

/**
 * JS 平台 OAuth 回调处理器实现
 * 使用 postMessage API 监听浏览器回调消息
 * 
 * @param config OAuth 回调服务器配置
 */
internal class OAuthCallbackHandlerJs(
    private val config: OAuthCallbackServerConfig
) : OAuthCallbackHandler {
    
    override val callbackUrl: String get() = config.buildCallbackUrl()
    
    private val mutex = Mutex()
    private var messageListener: dynamic = null
    
    override suspend fun startAndGetCallback(authorizeUrl: String): OAuthCallbackResult {
        val deferred = CompletableDeferred<OAuthCallbackResult>()
        
        return mutex.withLock {
            try {
                messageListener = createListener(deferred)
                window.asDynamic().addEventListener("message", messageListener)
                window.asDynamic().open(authorizeUrl, "_blank")
                
                val timeoutMillis = config.timeoutSeconds * 1000
                val result = withTimeoutOrNull<OAuthCallbackResult>(timeoutMillis) {
                    deferred.await()
                }
                
                if (result == null) {
                    OAuthCallbackResult.Error("OAuth callback timeout")
                } else {
                    result
                }
            } catch (e: Exception) {
                OAuthCallbackResult.Error("Failed: ${e.message}", e)
            } finally {
                cleanup()
            }
        }
    }
    
    override suspend fun stop() {
        cleanup()
    }
    
    private fun cleanup() {
        try {
            if (messageListener != null) {
                window.asDynamic().removeEventListener("message", messageListener)
                messageListener = null
            }
        } catch (e: Exception) {
            // Ignore
        }
    }
    
    /**
     * 创建 postMessage 消息监听器
     */
    private fun createListener(deferred: CompletableDeferred<OAuthCallbackResult>): dynamic {
        return { event: dynamic ->
            try {
                val data = event.data
                if (data != null && data.type == "oauth_callback") {
                    val code = data.code as? String
                    val state = data.state as? String
                    if (code != null && state != null) {
                        deferred.complete(OAuthCallbackResult.Success(code, state))
                    }
                }
            } catch (e: Exception) {
                // Ignore
            }
        }
    }
}

/**
 * 创建 JS 平台的 OAuth 回调处理器
 */
public actual fun createOAuthCallbackHandler(config: OAuthCallbackServerConfig): OAuthCallbackHandler {
    return OAuthCallbackHandlerJs(config)
}

/**
 * 生成随机 state 参数用于 CSRF 保护
 */
public actual fun generateState(): String {
    val bytes = ByteArray(16) { Random.nextInt(256).toByte() }
    return bytes.joinToString("") { byte ->
        val hex = (byte.toInt() and 0xFF).toString(16).padStart(2, '0')
        hex
    }
}

@JsName("window")
private external val window: dynamic
