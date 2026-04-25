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

    /**
     * 设置 API 基础 URL。
     *
     * 指定 Fursuit.TV API 服务器的地址。生产环境默认使用官方服务器，
     * 测试环境可指向本地或 staging 服务器。
     *
     * @param url API 服务器地址，必须以 `http://` 或 `https://` 开头
     * @return 此配置实例（支持链式调用）
     */
    public fun baseUrl(url: String): MutableSdkConfig = apply { this.baseUrl = url }

    /**
     * 设置 VDS 颁发的 API 密钥。
     *
     * 用于已有有效 apiKey 的场景，无需进行签名交换。
     * 设置后将忽略 clientId 和 clientSecret 配置。
     *
     * @param key VDS 开发者控制台颁发的 API 密钥字符串
     * @return 此配置实例（支持链式调用）
     * @see SdkConfig.withApiKey
     */
    public fun apiKey(key: String): MutableSdkConfig = apply { this.apiKey = key }

    /**
     * 设置应用客户端 ID。
     *
     * 应用 ID 格式为 `vap_` 后跟 32 位十六进制字符串，
     * 从 VDS 开发者控制台获取。与 clientSecret 配合使用进行签名交换。
     *
     * @param id 客户端 ID 字符串（格式：`vap_xxxxxxxx`）
     * @return 此配置实例（支持链式调用）
     */
    public fun clientId(id: String): MutableSdkConfig = apply { this.clientId = id }

    /**
     * 设置应用密钥。
     *
     * 应用密钥来自 VDS 开发者控制台，与 clientId 配合使用。
     * ⚠️ 请勿将密钥硬编码在代码中，建议使用环境变量或密钥管理服务。
     *
     * @param secret 应用密钥字符串
     * @return 此配置实例（支持链式调用）
     */
    public fun clientSecret(secret: String): MutableSdkConfig = apply { this.clientSecret = secret }

    /**
     * 设置请求超时时间。
     *
     * 指定等待服务器响应的最大时间。超时后将抛出 NetworkException。
     * 建议范围：10000-60000 毫秒（10-60 秒）。
     *
     * @param timeout 超时时间（毫秒），默认 30000
     * @return 此配置实例（支持链式调用）
     */
    public fun requestTimeout(timeout: Long): MutableSdkConfig = apply { this.requestTimeout = timeout }

    /**
     * 设置连接超时时间。
     *
     * 指定建立 TCP 连接的最大时间。适用于网络较差的环境。
     * 建议范围：5000-30000 毫秒（5-30 秒）。
     *
     * @param timeout 超时时间（毫秒），默认 10000
     * @return 此配置实例（支持链式调用）
     */
    public fun connectTimeout(timeout: Long): MutableSdkConfig = apply { this.connectTimeout = timeout }

    /**
     * 设置套接字超时时间。
     *
     * 指定两个数据包之间等待的最大时间。适用于传输大响应体。
     * 建议范围：10000-60000 毫秒（10-60 秒）。
     *
     * @param timeout 超时时间（毫秒），默认 30000
     * @return 此配置实例（支持链式调用）
     */
    public fun socketTimeout(timeout: Long): MutableSdkConfig = apply { this.socketTimeout = timeout }

    /**
     * 设置 HTTP 客户端日志级别。
     *
     * 控制日志输出的详细程度。生产环境建议使用 INFO 或更高级别，
     * 开发调试时可使用 DEBUG 或 ALL 记录完整请求/响应信息。
     *
     * @param level 日志级别（OFF/ERROR/WARNING/INFO/DEBUG/ALL），默认 INFO
     * @return 此配置实例（支持链式调用）
     */
    public fun logLevel(level: LogLevel): MutableSdkConfig = apply { this.logLevel = level }

    /**
     * 是否启用自动重试机制。
     *
     * 当请求因网络问题失败时，SDK 会自动重试。
     * 启用后可配合 maxRetries 和 retryInterval 控制重试行为。
     *
     * @param enable 是否启用重试（true=启用, false=禁用），默认 true
     * @return 此配置实例（支持链式调用）
     */
    public fun enableRetry(enable: Boolean): MutableSdkConfig = apply { this.enableRetry = enable }

    /**
     * 设置最大重试次数。
     *
     * 请求失败后的最大自动重试次数。仅在 enableRetry 为 true 时生效。
     * 建议范围：0-10 次，过多重试可能导致用户体验下降。
     *
     * @param retries 最大重试次数，默认 3
     * @return 此配置实例（支持链式调用）
     */
    public fun maxRetries(retries: Int): MutableSdkConfig = apply { this.maxRetries = retries }

    /**
     * 设置重试间隔时间。
     *
     * 两次重试之间的等待时间（毫秒）。可避免频繁重试对服务器造成压力。
     * 建议范围：500-5000 毫秒。
     *
     * @param interval 重试间隔时间（毫秒），默认 1000
     * @return 此配置实例（支持链式调用）
     */
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
