package com.furrist.rp.furtv.sdk.model

import io.ktor.client.plugins.logging.LogLevel
import kotlin.js.JsExport
import kotlin.js.JsName
import kotlin.jvm.JvmStatic

private const val DEFAULT_BASE_URL = "https://open-global.vdsentnet.com"
private const val DEFAULT_REQUEST_TIMEOUT = 30000L
private const val DEFAULT_CONNECT_TIMEOUT = 10000L
private const val DEFAULT_SOCKET_TIMEOUT = 30000L
private const val DEFAULT_MAX_RETRIES = 3
private const val DEFAULT_RETRY_INTERVAL = 1000L

/**
 * SDK 日志级别枚举，控制 HTTP 请求日志输出详细程度。
 *
 * @property OFF 关闭所有日志
 * @property ERROR 仅输出错误日志
 * @property WARNING 输出警告及以上级别日志
 * @property INFO 输出信息及以上级别日志（默认）
 * @property DEBUG 输出调试及以上级别日志，包含请求/响应体
 * @property ALL 输出所有级别日志
 */
@JsExport
@JsName("SdkLogLevel")
public enum class SdkLogLevel {
    OFF,
    ERROR,
    WARNING,
    INFO,
    DEBUG,
    ALL, ;

    /**
     * 将 SDK 日志级别转换为 Ktor 客户端的 [LogLevel]。
     *
     * @return 对应的 Ktor 日志级别
     */
    internal fun toKtorLogLevel(): LogLevel =
        when (this) {
            OFF -> LogLevel.NONE
            ERROR -> LogLevel.INFO
            WARNING -> LogLevel.INFO
            INFO -> LogLevel.INFO
            DEBUG -> LogLevel.BODY
            ALL -> LogLevel.ALL
        }
}

/**
 * Fursuit.TV SDK 不可变配置。
 *
 * @property baseUrl API 基础 URL
 * @property apiKey API 密钥（可选，签名交换后可为空）
 * @property clientId 客户端 ID（即 VDS 文档中的 appId），用于签名交换或 OAuth
 * @property clientSecret 客户端密钥，用于签名交换或 OAuth
 * @property requestTimeout 请求超时时间（毫秒）
 * @property connectTimeout 连接超时时间（毫秒）
 * @property socketTimeout 套接字超时时间（毫秒）
 * @property logLevel HTTP 日志级别，参见 [SdkLogLevel]
 * @property enableRetry 是否启用重试
 * @property maxRetries 最大重试次数
 * @property retryInterval 重试间隔（毫秒）
 */
@JsExport
@JsName("SdkConfig")
public class SdkConfig(
    @JsName("baseUrl") public val baseUrl: String = DEFAULT_BASE_URL,
    @JsName("apiKey") public val apiKey: String? = null,
    @JsName("clientId") public val clientId: String? = null,
    @JsName("clientSecret") public val clientSecret: String? = null,
    @JsName("requestTimeout") public val requestTimeout: Long = DEFAULT_REQUEST_TIMEOUT,
    @JsName("connectTimeout") public val connectTimeout: Long = DEFAULT_CONNECT_TIMEOUT,
    @JsName("socketTimeout") public val socketTimeout: Long = DEFAULT_SOCKET_TIMEOUT,
    @JsName("logLevel") public val logLevel: SdkLogLevel = SdkLogLevel.INFO,
    @JsName("enableRetry") public val enableRetry: Boolean = true,
    @JsName("maxRetries") public val maxRetries: Int = DEFAULT_MAX_RETRIES,
    @JsName("retryInterval") public val retryInterval: Long = DEFAULT_RETRY_INTERVAL,
) {
    public companion object {
        /**
         * 为签名交换创建配置。
         *
         * @param clientId 客户端 ID（即 VDS 文档中的 appId，格式 vap_xxxx）
         * @param clientSecret 客户端密钥
         * @return SdkConfig 实例
         */
        @JvmStatic
        @JsName("forTokenExchange")
        public fun forTokenExchange(clientId: String, clientSecret: String): SdkConfig =
            SdkConfig(clientId = clientId, clientSecret = clientSecret)

        /**
         * 为已有 apiKey 创建配置。
         *
         * @param apiKey API 密钥
         * @return SdkConfig 实例
         */
        @JvmStatic
        @JsName("withApiKey")
        public fun withApiKey(apiKey: String): SdkConfig =
            SdkConfig(apiKey = apiKey)

        /**
         * 使用 DSL 方式创建配置。
         *
         * @param block 配置块
         * @return SdkConfig 实例
         */
        @JvmStatic
        @JsName("createSdkConfig")
        public fun sdkConfig(block: SdkConfig.() -> Unit = {}): SdkConfig =
            SdkConfig().apply(block)

        /**
         * 创建 Builder 实例用于链式配置。
         *
         * @return MutableSdkConfig 实例，支持链式调用后通过 [MutableSdkConfig.toImmutable] 转换
         */
        @JvmStatic
        @JsName("builder")
        public fun builder(): MutableSdkConfig = MutableSdkConfig()
    }
}

/**
 * 使用 DSL 方式创建 SdkConfig。
 *
 * @param block 配置块
 * @return SdkConfig 实例
 */
@JsExport
@JsName("sdkConfig")
public fun sdkConfig(block: (SdkConfig) -> Unit): SdkConfig =
    SdkConfig().apply(block)
