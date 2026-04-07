package me.regadpole.furtv.sdk.auth

import io.ktor.http.URLBuilder
import io.ktor.http.URLProtocol
import io.ktor.http.path

/**
 * OAuth 回调服务器配置
 * @property callbackHost 回调主机地址，默认为 localhost
 * @property callbackPort 回调端口，默认为 8080
 * @property callbackPath 回调路径，默认为 /callback
 * @property timeoutSeconds 超时时间（秒），默认为 300 秒（5 分钟）
 */
public data class OAuthCallbackServerConfig(
    public val callbackHost: String = "localhost",
    public val callbackPort: Int = 8080,
    public val callbackPath: String = "/callback",
    public val timeoutSeconds: Long = 300
) {
    /**
     * 构建完整的回调 URL
     * @return 回调 URL 字符串
     */
    public fun buildCallbackUrl(): String {
        return URLBuilder(protocol = URLProtocol.HTTP, host = callbackHost, port = callbackPort).apply {
            path(callbackPath)
        }.buildString()
    }
}

/**
 * OAuth 回调结果
 * 统一的返回格式，所有平台使用相同的 sealed class
 */
public sealed class OAuthCallbackResult {
    /**
     * 成功回调
     * @param code 授权码
     * @param state 状态参数
     */
    public data class Success(
        public val code: String,
        public val state: String
    ) : OAuthCallbackResult()
    
    /**
     * 错误回调
     * @param message 错误消息
     * @param cause 可选的异常原因
     */
    public data class Error(
        public val message: String,
        public val cause: Throwable? = null
    ) : OAuthCallbackResult()
}

/**
 * OAuth 回调异常
 * 用于在 OAuth 回调过程中抛出异常
 */
public class OAuthCallbackException(
    message: String,
    cause: Throwable? = null
) : Exception(message, cause)

/**
 * OAuth 回调处理器接口
 * 统一的接口定义，所有平台使用相同的 API
 */
public interface OAuthCallbackHandler {
    /**
     * 回调 URL
     */
    public val callbackUrl: String
    
    /**
     * 启动回调处理器并等待回调
     * @param authorizeUrl 授权 URL，用于打开浏览器
     * @return OAuthCallbackResult 回调结果
     */
    public suspend fun startAndGetCallback(authorizeUrl: String): OAuthCallbackResult
    
    /**
     * 停止回调处理器
     */
    public suspend fun stop()
}

/**
 * 生成随机 state 参数用于 CSRF 保护
 * 使用加密安全的随机数生成器生成 32 字符的十六进制字符串
 * @return 随机生成的 state 字符串
 */
public expect fun generateState(): String
