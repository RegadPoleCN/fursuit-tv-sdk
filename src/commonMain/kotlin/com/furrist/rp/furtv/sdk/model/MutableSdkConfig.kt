package com.furrist.rp.furtv.sdk.model

import kotlin.js.JsExport
import kotlin.js.JsName

// 导入 SdkConfig 中的常量（通过内部访问）
private const val DEFAULT_BASE_URL = "https://open-global.vdsentnet.com"
private const val DEFAULT_REQUEST_TIMEOUT = 30000L
private const val DEFAULT_CONNECT_TIMEOUT = 10000L
private const val DEFAULT_SOCKET_TIMEOUT = 30000L
private const val DEFAULT_MAX_RETRIES = 3
private const val DEFAULT_RETRY_INTERVAL = 1000L

/**
 * 可变的 SDK 配置构建器，属性可修改，通过 [toImmutable] 转换为不可变的 [SdkConfig]。
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
    public var baseUrl: String = DEFAULT_BASE_URL

    @JsName("apiKey")
    public var apiKey: String? = null

    @JsName("clientId")
    public var clientId: String? = null

    @JsName("clientSecret")
    public var clientSecret: String? = null

    @JsName("requestTimeout")
    public var requestTimeout: Long = DEFAULT_REQUEST_TIMEOUT

    @JsName("connectTimeout")
    public var connectTimeout: Long = DEFAULT_CONNECT_TIMEOUT

    @JsName("socketTimeout")
    public var socketTimeout: Long = DEFAULT_SOCKET_TIMEOUT

    @JsName("logLevel")
    public var logLevel: SdkLogLevel = SdkLogLevel.INFO

    @JsName("enableRetry")
    public var enableRetry: Boolean = true

    @JsName("maxRetries")
    public var maxRetries: Int = DEFAULT_MAX_RETRIES

    @JsName("retryInterval")
    public var retryInterval: Long = DEFAULT_RETRY_INTERVAL

    /**
     * 设置 API 基础 URL。
     *
     * @param url API 服务器地址
     * @return 此配置实例（支持链式调用）
     */
    @JsName("setBaseUrl")
    public fun baseUrl(url: String): MutableSdkConfig = apply { this.baseUrl = url }

    /**
     * 设置 API 密钥，设置后将忽略 clientId 和 clientSecret。
     *
     * @param key API 密钥
     * @return 此配置实例（支持链式调用）
     * @see SdkConfig.withApiKey
     */
    @JsName("setApiKey")
    public fun apiKey(key: String): MutableSdkConfig = apply { this.apiKey = key }

    /**
     * 设置客户端 ID（即 VDS 文档中的 appId），与 clientSecret 配合用于签名交换。
     *
     * @param id 客户端 ID（格式 vap_xxxx）
     * @return 此配置实例（支持链式调用）
     */
    @JsName("setClientId")
    public fun clientId(id: String): MutableSdkConfig = apply { this.clientId = id }

    /**
     * 设置客户端密钥，与 clientId 配合使用。
     *
     * @param secret 客户端密钥
     * @return 此配置实例（支持链式调用）
     */
    @JsName("setClientSecret")
    public fun clientSecret(secret: String): MutableSdkConfig = apply { this.clientSecret = secret }

    /**
     * 设置请求超时时间（毫秒）。
     *
     * @param timeout 超时时间（毫秒），默认 30000
     * @return 此配置实例（支持链式调用）
     */
    @JsName("setRequestTimeout")
    public fun requestTimeout(timeout: Long): MutableSdkConfig = apply { this.requestTimeout = timeout }

    /**
     * 设置连接超时时间（毫秒）。
     *
     * @param timeout 超时时间（毫秒），默认 10000
     * @return 此配置实例（支持链式调用）
     */
    @JsName("setConnectTimeout")
    public fun connectTimeout(timeout: Long): MutableSdkConfig = apply { this.connectTimeout = timeout }

    /**
     * 设置套接字超时时间（毫秒）。
     *
     * @param timeout 超时时间（毫秒），默认 30000
     * @return 此配置实例（支持链式调用）
     */
    @JsName("setSocketTimeout")
    public fun socketTimeout(timeout: Long): MutableSdkConfig = apply { this.socketTimeout = timeout }

    /**
     * 设置 HTTP 日志级别。
     *
     * @param level 日志级别，默认 INFO
     * @return 此配置实例（支持链式调用）
     */
    @JsName("setLogLevel")
    public fun logLevel(level: SdkLogLevel): MutableSdkConfig = apply { this.logLevel = level }

    /**
     * 设置是否启用自动重试。
     *
     * @param enable 是否启用，默认 true
     * @return 此配置实例（支持链式调用）
     */
    @JsName("setEnableRetry")
    public fun enableRetry(enable: Boolean): MutableSdkConfig = apply { this.enableRetry = enable }

    /**
     * 设置最大重试次数，仅在 enableRetry 为 true 时生效。
     *
     * @param retries 最大重试次数，默认 3
     * @return 此配置实例（支持链式调用）
     */
    @JsName("setMaxRetries")
    public fun maxRetries(retries: Int): MutableSdkConfig = apply { this.maxRetries = retries }

    /**
     * 设置重试间隔时间（毫秒）。
     *
     * @param interval 重试间隔（毫秒），默认 1000
     * @return 此配置实例（支持链式调用）
     */
    @JsName("setRetryInterval")
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
