@file:Suppress(
    "MatchingDeclarationName",
    "MagicNumber",
    "TooGenericExceptionCaught",
    "SwallowedException"
)

package me.regadpole.furtv.sdk.auth

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.cio.CIO
import io.ktor.server.engine.embeddedServer
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import io.ktor.util.collections.ConcurrentMap
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.random.Random
import kotlin.time.Duration.Companion.seconds

/**
 * Native 平台 OAuth 回调处理器实现
 * 使用 Ktor CIO 引擎启动嵌入式 HTTP 服务器
 * 
 * @param config OAuth 回调服务器配置
 */
internal class OAuthCallbackHandlerNative(
    private val config: OAuthCallbackServerConfig
) : OAuthCallbackHandler {
    
    override val callbackUrl: String get() = config.buildCallbackUrl()
    
    private val server = embeddedServer(CIO, port = config.callbackPort, host = config.callbackHost) {
        routing {
            get(config.callbackPath) {
                handleCallback(this.call)
            }
        }
    }
    
    private val pendingCallbacks = ConcurrentMap<String, CompletableDeferred<OAuthCallbackResult>>()
    private val mutex = Mutex()
    
    override suspend fun startAndGetCallback(authorizeUrl: String): OAuthCallbackResult {
        val deferred = CompletableDeferred<OAuthCallbackResult>()
        val state = extractStateFromUrl(authorizeUrl)
        
        return mutex.withLock {
            try {
                server.start(wait = false)
                
                if (state != null) {
                    pendingCallbacks[state] = deferred
                }
                
                val timeoutDuration = config.timeoutSeconds.seconds
                val result = withTimeoutOrNull(timeoutDuration) {
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
                pendingCallbacks.clear()
                try {
                    server.stop(gracePeriodMillis = 1000, timeoutMillis = 2000)
                } catch (e: Exception) {
                    // Ignore
                }
            }
        }
    }
    
    override suspend fun stop() {
        try {
            server.stop(gracePeriodMillis = 1000, timeoutMillis = 2000)
        } catch (e: Exception) {
            // Ignore
        }
    }
    
    private fun extractStateFromUrl(url: String): String? {
        return url.substringAfter("state=", "").substringBefore("&")
            .takeIf { it.isNotEmpty() }
    }
    
    private suspend fun handleCallback(call: ApplicationCall) {
        val code = call.request.queryParameters["code"]
        val state = call.request.queryParameters["state"]
        
        if (code == null || state == null) {
            call.respondText("Missing code or state", status = HttpStatusCode.BadRequest)
            return
        }
        
        val deferred = pendingCallbacks[state]
        if (deferred == null) {
            call.respondText("Unknown state", status = HttpStatusCode.BadRequest)
            return
        }
        
        deferred.complete(OAuthCallbackResult.Success(code, state))
        call.respondText("Success")
    }
}

/**
 * 创建 Native 平台的 OAuth 回调处理器
 */
public actual fun createOAuthCallbackHandler(config: OAuthCallbackServerConfig): OAuthCallbackHandler {
    return OAuthCallbackHandlerNative(config)
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
