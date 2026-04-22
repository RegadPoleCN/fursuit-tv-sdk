package com.furrist.rp.furtv.sdk.model

import io.ktor.client.plugins.logging.LogLevel

// 导入 SdkConfig 中的常量（通过内部访问）
private const val DEFAULT_BASE_URL = "https://open-global.vdsentnet.com"
private const val DEFAULT_REQUEST_TIMEOUT = 30000L
private const val DEFAULT_CONNECT_TIMEOUT = 10000L
private const val DEFAULT_SOCKET_TIMEOUT = 30000L
private const val DEFAULT_MAX_RETRIES = 3
private const val DEFAULT_RETRY_INTERVAL = 1000L

/**
 * 可变的 SDK 配置构建器，用于 DSL 方式创建 SdkConfig
 *
 * 与 SdkConfig 不同，此类的属性是可变的，支持在 DSL 块中修改。
 * 使用 `toImmutable()` 方法可转换为不可变的 SdkConfig。
 */
public class MutableSdkConfig {
    public var baseUrl: String = DEFAULT_BASE_URL
    public var apiKey: String? = null
    public var clientId: String? = null
    public var clientSecret: String? = null
    public var requestTimeout: Long = DEFAULT_REQUEST_TIMEOUT
    public var connectTimeout: Long = DEFAULT_CONNECT_TIMEOUT
    public var socketTimeout: Long = DEFAULT_SOCKET_TIMEOUT
    public var logLevel: LogLevel = LogLevel.INFO
    public var enableRetry: Boolean = true
    public var maxRetries: Int = DEFAULT_MAX_RETRIES
    public var retryInterval: Long = DEFAULT_RETRY_INTERVAL

    /** 链式 Builder 方法 */
    public fun baseUrl(url: String): MutableSdkConfig = apply { this.baseUrl = url }
    public fun apiKey(key: String): MutableSdkConfig = apply { this.apiKey = key }
    public fun clientId(id: String): MutableSdkConfig = apply { this.clientId = id }
    public fun clientSecret(secret: String): MutableSdkConfig = apply { this.clientSecret = secret }
    public fun requestTimeout(timeout: Long): MutableSdkConfig = apply { this.requestTimeout = timeout }
    public fun connectTimeout(timeout: Long): MutableSdkConfig = apply { this.connectTimeout = timeout }
    public fun socketTimeout(timeout: Long): MutableSdkConfig = apply { this.socketTimeout = timeout }
    public fun logLevel(level: LogLevel): MutableSdkConfig = apply { this.logLevel = level }
    public fun enableRetry(enable: Boolean): MutableSdkConfig = apply { this.enableRetry = enable }
    public fun maxRetries(retries: Int): MutableSdkConfig = apply { this.maxRetries = retries }
    public fun retryInterval(interval: Long): MutableSdkConfig = apply { this.retryInterval = interval }

    internal fun toImmutable(): SdkConfig =
        SdkConfig(
            baseUrl = baseUrl,
            apiKey = apiKey,
            clientId = clientId,
            clientSecret = clientSecret,
            requestTimeout = requestTimeout,
            connectTimeout = connectTimeout,
            socketTimeout = socketTimeout,
            logLevel = logLevel,
            enableRetry = enableRetry,
            maxRetries = maxRetries,
            retryInterval = retryInterval,
        )
}
