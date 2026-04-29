package com.furrist.rp.furtv.sdk.auth

import kotlin.js.JsExport
import kotlin.js.JsName
import kotlin.jvm.JvmStatic
import kotlinx.serialization.Serializable

/**
 * OAuth 回调结果。
 */
public sealed class OAuthCallbackResult {
    /**
     * 授权成功，携带授权码和 state。
     *
     * @param code 授权码
     * @param state 回调中的 state 参数
     */
    @JsName("OAuthCallbackSuccess")
    @Serializable
    public data class Success(val code: String, val state: String) : OAuthCallbackResult()

    /**
     * 授权失败，携带错误信息。
     *
     * @param message 错误描述
     * @param errorCode OAuth 错误代码（如 "access_denied"），可能为 null
     * @param cause 根本原因，可能为 null
     */
    @JsName("OAuthCallbackError")
    public data class Error(
        val message: String,
        val errorCode: String? = null,
        val cause: Throwable? = null,
    ) : OAuthCallbackResult()
}

/**
 * OAuth 回调处理器接口。
 *
 * 定义 OAuth 授权回调的监听与处理流程。典型用法：
 * 1. 调用 [startListening] 启动回调服务器
 * 2. 通过 [callbackUrl] 构建授权 URL 并引导用户访问
 * 3. 调用 [waitForCallback] 等待回调结果
 * 4. 调用 [stop] 释放资源
 *
 * 也可使用便捷方法 [startAndGetCallback] 一次性完成上述步骤。
 */
@JsExport
public interface OAuthCallbackHandler {
    /**
     * 回调接收地址，格式为 `http://localhost:{port}{path}`。
     */
    public val callbackUrl: String

    /**
     * 启动回调监听。
     *
     * 调用后回调服务器开始就绪，可通过 [callbackUrl] 构建授权 URL。
     *
     * @throws IllegalStateException 如果服务器启动失败
     */
    public suspend fun startListening()

    /**
     * 等待 OAuth 回调结果。
     *
     * 阻塞当前协程直到收到回调或超时。调用前须先调用 [startListening]。
     *
     * @return 回调结果
     */
    public suspend fun waitForCallback(): OAuthCallbackResult

    /**
     * 便捷方法：启动监听、引导用户到授权 URL 并等待回调。
     *
     * 等价于依次调用 [startListening]、打开 [authorizeUrl]、[waitForCallback]。
     * 各平台实现会自动选择合适方式引导用户（如打开浏览器或弹窗）。
     *
     * @param authorizeUrl 授权端点 URL
     * @return 回调结果
     */
    public suspend fun startAndGetCallback(authorizeUrl: String): OAuthCallbackResult {
        startListening()
        return waitForCallback()
    }

    /**
     * 停止回调监听并释放资源。
     */
    public suspend fun stop()
}

/**
 * OAuth 回调服务器配置。
 *
 * @param callbackHost 监听主机名，默认 "localhost"
 * @param callbackPort 监听端口，默认 8080
 * @param callbackPath 回调路径，默认 "/callback"
 * @param timeoutSeconds 等待回调超时秒数，默认 300
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
 * 创建平台默认的 OAuth 回调处理器。
 *
 * 根据当前运行平台自动选择合适的实现：
 * - **JVM**: 启动本地 HTTP 服务器，自动打开浏览器
 * - **JS (浏览器)**: 使用 postMessage 机制监听回调
 * - **JS (Node.js)**: 使用 Node.js http 模块创建本地服务器
 * - **Native**: 启动本地 HTTP 服务器接收回调
 *
 * @param config 回调服务器配置，默认使用 [OAuthCallbackServerConfig.DEFAULT]
 * @return 平台对应的 [OAuthCallbackHandler] 实例
 */
@JsExport
@JsName("createDefaultOAuthHandler")
public expect fun createDefaultOAuthHandler(
    config: OAuthCallbackServerConfig = OAuthCallbackServerConfig.DEFAULT,
): OAuthCallbackHandler
