package com.furrist.rp.furtv.sdk.auth

import kotlin.js.JsExport
import kotlin.js.JsName
import kotlin.jvm.JvmStatic
import kotlinx.serialization.Serializable

/**
 * OAuth 回调结果
 */
@JsExport
public sealed class OAuthCallbackResult {
    @Serializable
    public data class Success(val code: String, val state: String) : OAuthCallbackResult()

    public data class Error(val message: String, val cause: Throwable? = null) : OAuthCallbackResult()
}

/**
 * OAuth 回调处理器接口
 */
@JsExport
public interface OAuthCallbackHandler {
    public val callbackUrl: String

    public suspend fun startAndGetCallback(authorizeUrl: String): OAuthCallbackResult

    public suspend fun stop()
}

/**
 * OAuth 回调服务器配置
 */
@JsExport
@Serializable
@JsName("OAuthCallbackServerConfig")
public data class OAuthCallbackServerConfig(
    public val callbackHost: String = "localhost",
    public val callbackPort: Int = 8080,
    public val callbackPath: String = "/callback",
    public val timeoutSeconds: Long = 300,
) {
    public companion object {
        @JvmStatic
        @JsName("default")
        public val DEFAULT: OAuthCallbackServerConfig = OAuthCallbackServerConfig()
    }
}

/**
 * 创建平台默认的 OAuth 回调处理器
 */
public expect fun createDefaultOAuthHandler(
    config: OAuthCallbackServerConfig = OAuthCallbackServerConfig.DEFAULT,
): OAuthCallbackHandler
