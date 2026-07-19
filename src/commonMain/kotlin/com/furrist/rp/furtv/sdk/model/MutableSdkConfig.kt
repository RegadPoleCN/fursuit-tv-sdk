package com.furrist.rp.furtv.sdk.model

import kotlin.js.JsExport
import kotlin.js.JsName

/**
 * 可变的 SDK 配置构建器，作为 `fursuitTvSdk { ... }` / `fursuitTvSdkBlocking { ... }` DSL 的内部 builder。
 *
 * 用户只需直接设置属性即可，例如：
 *
 * ```kotlin
 * fursuitTvSdk {
 *     clientId = "vap_xxx"
 *     clientSecret = "your-secret"
 * }
 * ```
 *
 * `MutableSdkConfig` 仍是 `@JsExport` 公开类，但不提供链式 `setXxx(...)` 方法（直接赋属性更直观）。
 *
 * @property baseUrl API 基础 URL
 * @property apiKey API 密钥（可选）
 * @property clientId 客户端 ID（即 VDS 文档中的 appId）
 * @property clientSecret 客户端密钥
 * @property requestTimeout 请求超时时间（毫秒）
 * @property connectTimeout 连接超时时间（毫秒）
 * @property socketTimeout 套接字超时时间（毫秒）
 * @property logLevel HTTP 日志级别，参见 [SdkLogLevel]
 * @property enableRetry 是否启用重试
 * @property maxRetries 最大重试次数
 * @property retryInterval 重试间隔（毫秒）
 */
@JsExport
@JsName("MutableSdkConfig")
public class MutableSdkConfig {
    @JsName("baseUrl")
    public var baseUrl: String = SdkConfig.DEFAULT_BASE_URL

    @JsName("apiKey")
    public var apiKey: String? = null

    @JsName("clientId")
    public var clientId: String? = null

    @JsName("clientSecret")
    public var clientSecret: String? = null

    @JsName("requestTimeout")
    public var requestTimeout: Long = SdkConfig.DEFAULT_REQUEST_TIMEOUT

    @JsName("connectTimeout")
    public var connectTimeout: Long = SdkConfig.DEFAULT_CONNECT_TIMEOUT

    @JsName("socketTimeout")
    public var socketTimeout: Long = SdkConfig.DEFAULT_SOCKET_TIMEOUT

    @JsName("logLevel")
    public var logLevel: SdkLogLevel = SdkLogLevel.INFO

    @JsName("enableRetry")
    public var enableRetry: Boolean = true

    @JsName("maxRetries")
    public var maxRetries: Int = SdkConfig.DEFAULT_MAX_RETRIES

    @JsName("retryInterval")
    public var retryInterval: Long = SdkConfig.DEFAULT_RETRY_INTERVAL

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
