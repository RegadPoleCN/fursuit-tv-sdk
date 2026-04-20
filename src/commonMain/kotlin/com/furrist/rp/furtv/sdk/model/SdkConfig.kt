package com.furrist.rp.furtv.sdk.model

import io.ktor.client.plugins.logging.LogLevel
import kotlin.jvm.JvmOverloads
import kotlin.jvm.JvmStatic

private const val DEFAULT_BASE_URL = "https://open-global.vdsentnet.com"
private const val DEFAULT_REQUEST_TIMEOUT = 30000L
private const val DEFAULT_CONNECT_TIMEOUT = 10000L
private const val DEFAULT_SOCKET_TIMEOUT = 30000L
private const val DEFAULT_MAX_RETRIES = 3
private const val DEFAULT_RETRY_INTERVAL = 1000L

/**
 * Fursuit.TV SDK 配置
 *
 * @property baseUrl API 基础 URL
 * @property apiKey API 密钥（可选，签名交换后可为空）
 * @property clientId 客户端 ID，用于签名交换或 OAuth
 * @property clientSecret 客户端密钥，用于签名交换或 OAuth
 * @property requestTimeout 请求超时时间（毫秒）
 * @property connectTimeout 连接超时时间（毫秒）
 * @property socketTimeout 套接字超时时间（毫秒）
 * @property logLevel HTTP 日志级别
 * @property enableRetry 是否启用重试
 * @property maxRetries 最大重试次数
 * @property retryInterval 重试间隔（毫秒）
 *
 * @see FursuitTvSdk
 */
public class SdkConfig internal constructor(
    public val baseUrl: String = DEFAULT_BASE_URL,
    public val apiKey: String? = null,
    public val clientId: String? = null,
    public val clientSecret: String? = null,
    public val requestTimeout: Long = DEFAULT_REQUEST_TIMEOUT,
    public val connectTimeout: Long = DEFAULT_CONNECT_TIMEOUT,
    public val socketTimeout: Long = DEFAULT_SOCKET_TIMEOUT,
    public val logLevel: LogLevel = LogLevel.INFO,
    public val enableRetry: Boolean = true,
    public val maxRetries: Int = DEFAULT_MAX_RETRIES,
    public val retryInterval: Long = DEFAULT_RETRY_INTERVAL,
) {
    public companion object {
        /**
         * 为签名交换创建配置
         *
         * @param clientId 应用 ID（格式 vap_xxxx）
         * @param clientSecret 应用密钥
         * @return SdkConfig 实例
         */
        @JvmStatic
        public fun forTokenExchange(clientId: String, clientSecret: String): SdkConfig =
            SdkConfig(clientId = clientId, clientSecret = clientSecret)

        /**
         * 为已有 apiKey 创建配置
         *
         * @param apiKey API 密钥
         * @return SdkConfig 实例
         */
        @JvmStatic
        public fun withApiKey(apiKey: String): SdkConfig =
            SdkConfig(apiKey = apiKey)

        /**
         * 使用 DSL 方式创建配置
         *
         * @param block 配置块
         * @return 配置好的 SdkConfig 实例
         */
        @JvmStatic
        public fun sdkConfig(block: SdkConfig.() -> Unit = {}): SdkConfig =
            SdkConfig().apply(block)
    }
}

/**
 * 使用 DSL 方式创建 SdkConfig
 *
 * @param block 配置块
 * @return 配置好的 SdkConfig 实例
 */
public fun sdkConfig(block: SdkConfig.() -> Unit = {}): SdkConfig =
    SdkConfig.sdkConfig(block)
