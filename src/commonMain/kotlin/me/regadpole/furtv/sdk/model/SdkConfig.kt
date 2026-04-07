package me.regadpole.furtv.sdk.model

import io.ktor.client.plugins.logging.LogLevel
import kotlin.jvm.JvmStatic

private const val DEFAULT_REQUEST_TIMEOUT = 30000L
private const val DEFAULT_CONNECT_TIMEOUT = 10000L
private const val DEFAULT_SOCKET_TIMEOUT = 30000L
private const val DEFAULT_MAX_RETRIES = 3
private const val DEFAULT_RETRY_INTERVAL = 1000L

/**
 * SDK 配置类
 * 用于统一管理 SDK 的各种配置项，包括 API 端点、超时、重试等设置
 * 
 * @property baseUrl API 基础 URL
 * @property apiKey API 密钥，用于认证
 * @property requestTimeout 请求超时时间（毫秒）
 * @property connectTimeout 连接超时时间（毫秒）
 * @property socketTimeout 套接字超时时间（毫秒）
 * @property logLevel 日志级别
 * @property enableRetry 是否启用重试机制
 * @property maxRetries 最大重试次数
 * @property retryInterval 重试间隔（毫秒）
 * 
 * @see Builder
 */
public class SdkConfig private constructor(
    /**
     * API 基础 URL
     */
    public val baseUrl: String,
    
    /**
     * API 密钥
     * 用于认证和授权所有 API 请求
     */
    public val apiKey: String,
    
    /**
     * 请求超时时间（毫秒）
     * 默认值：30000
     */
    public val requestTimeout: Long,
    
    /**
     * 连接超时时间（毫秒）
     * 默认值：10000
     */
    public val connectTimeout: Long,
    
    /**
     * 套接字超时时间（毫秒）
     * 默认值：30000
     */
    public val socketTimeout: Long,
    
    /**
     * 日志级别
     * 控制 HTTP 请求和响应的日志输出详细程度
     */
    public val logLevel: LogLevel,
    
    /**
     * 是否启用重试机制
     * 默认值：true
     */
    public val enableRetry: Boolean,
    
    /**
     * 最大重试次数
     * 默认值：3
     */
    public val maxRetries: Int,
    
    /**
     * 重试间隔（毫秒）
     * 默认值：1000
     */
    public val retryInterval: Long
) {
    /**
     * SDK 配置构建器
     * 使用 Builder 模式创建 SdkConfig 实例，提供更灵活的配置方式
     * 
     * @see SdkConfig
     */
    public class Builder {
        private var baseUrl: String = "https://open-global.vdsentnet.com"
        private var apiKey: String? = null
        private var requestTimeout: Long = DEFAULT_REQUEST_TIMEOUT
        private var connectTimeout: Long = DEFAULT_CONNECT_TIMEOUT
        private var socketTimeout: Long = DEFAULT_SOCKET_TIMEOUT
        private var logLevel: LogLevel = LogLevel.INFO
        private var enableRetry: Boolean = true
        private var maxRetries: Int = DEFAULT_MAX_RETRIES
        private var retryInterval: Long = DEFAULT_RETRY_INTERVAL

        /**
         * 设置 API 基础 URL
         * @param baseUrl API 基础 URL，默认为 https://api.fursuit.tv
         * @return 当前 Builder 实例
         */
        public fun baseUrl(baseUrl: String): Builder = apply { this.baseUrl = baseUrl }
        
        /**
         * 设置 API 密钥
         * @param apiKey VDS 颁发的 API 密钥，用于认证和授权
         * @return 当前 Builder 实例
         */
        public fun apiKey(apiKey: String): Builder = apply { this.apiKey = apiKey }
        
        /**
         * 设置请求超时时间
         * @param requestTimeout 请求超时时间（毫秒），默认 30000
         * @return 当前 Builder 实例
         */
        public fun requestTimeout(requestTimeout: Long): Builder = apply { this.requestTimeout = requestTimeout }
        
        /**
         * 设置连接超时时间
         * @param connectTimeout 连接超时时间（毫秒），默认 10000
         * @return 当前 Builder 实例
         */
        public fun connectTimeout(connectTimeout: Long): Builder = apply { this.connectTimeout = connectTimeout }
        
        /**
         * 设置套接字超时时间
         * @param socketTimeout 套接字超时时间（毫秒），默认 30000
         * @return 当前 Builder 实例
         */
        public fun socketTimeout(socketTimeout: Long): Builder = apply { this.socketTimeout = socketTimeout }
        
        /**
         * 设置日志级别
         * @param logLevel 日志级别，默认 LogLevel.INFO
         * @return 当前 Builder 实例
         */
        public fun logLevel(logLevel: LogLevel): Builder = apply { this.logLevel = logLevel }
        
        /**
         * 设置是否启用重试机制
         * @param enableRetry 是否启用重试，默认 true
         * @return 当前 Builder 实例
         */
        public fun enableRetry(enableRetry: Boolean): Builder = apply { this.enableRetry = enableRetry }
        
        /**
         * 设置最大重试次数
         * @param maxRetries 最大重试次数，默认 3
         * @return 当前 Builder 实例
         */
        public fun maxRetries(maxRetries: Int): Builder = apply { this.maxRetries = maxRetries }
        
        /**
         * 设置重试间隔
         * @param retryInterval 重试间隔（毫秒），默认 1000
         * @return 当前 Builder 实例
         */
        public fun retryInterval(retryInterval: Long): Builder = apply { this.retryInterval = retryInterval }

        /**
         * 构建 SdkConfig 实例
         * @return 配置好的 SdkConfig 实例
         * @throws IllegalArgumentException 如果未设置 apiKey
         */
        public fun build(): SdkConfig {
            val apiKey = apiKey ?: throw IllegalArgumentException("apiKey is required")
            return SdkConfig(
                baseUrl = baseUrl,
                apiKey = apiKey,
                requestTimeout = requestTimeout,
                connectTimeout = connectTimeout,
                socketTimeout = socketTimeout,
                logLevel = logLevel,
                enableRetry = enableRetry,
                maxRetries = maxRetries,
                retryInterval = retryInterval
            )
        }
    }

    public companion object {
        /**
         * 创建默认配置
         * 使用默认值（除了 apiKey）快速创建配置
         * @param apiKey API 密钥，必需
         * @return 使用默认值的 SdkConfig 实例
         */
        @JvmStatic
        public fun default(apiKey: String): SdkConfig {
            return Builder().apiKey(apiKey).build()
        }

        /**
         * 创建配置构建器
         * @return Builder 实例
         */
        @JvmStatic
        public fun builder(): Builder = Builder()
    }
}
