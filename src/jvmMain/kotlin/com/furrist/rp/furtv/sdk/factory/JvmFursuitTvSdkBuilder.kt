package com.furrist.rp.furtv.sdk.factory

import com.furrist.rp.furtv.sdk.FursuitTvSdk
import com.furrist.rp.furtv.sdk.model.MutableSdkConfig
import io.ktor.client.plugins.logging.LogLevel

/**
 * 纯 Java 风格的链式构建器。
 *
 * @see FursuitTvSdk
 * @see MutableSdkConfig
 */
public class JvmFursuitTvSdkBuilder private constructor(
    private val config: MutableSdkConfig,
) {
    private var useTokenExchange: Boolean = false

    public companion object {
        /**
         * 创建新的 Builder 实例。
         */
        @JvmStatic
        public fun create(): JvmFursuitTvSdkBuilder =
            JvmFursuitTvSdkBuilder(MutableSdkConfig())
    }

    // 基础配置
    public fun baseUrl(url: String): JvmFursuitTvSdkBuilder = apply { config.baseUrl = url }

    public fun apiKey(key: String): JvmFursuitTvSdkBuilder = apply { config.apiKey = key }

    public fun clientId(id: String): JvmFursuitTvSdkBuilder =
        apply {
            config.clientId = id
            useTokenExchange = true
        }

    public fun clientSecret(secret: String): JvmFursuitTvSdkBuilder = apply { config.clientSecret = secret }

    // 超时配置
    public fun requestTimeout(timeout: Long): JvmFursuitTvSdkBuilder = apply { config.requestTimeout = timeout }

    public fun connectTimeout(timeout: Long): JvmFursuitTvSdkBuilder = apply { config.connectTimeout = timeout }

    public fun socketTimeout(timeout: Long): JvmFursuitTvSdkBuilder = apply { config.socketTimeout = timeout }

    // 高级配置
    public fun logLevel(level: LogLevel): JvmFursuitTvSdkBuilder = apply { config.logLevel = level }

    public fun enableRetry(enable: Boolean): JvmFursuitTvSdkBuilder = apply { config.enableRetry = enable }

    public fun maxRetries(retries: Int): JvmFursuitTvSdkBuilder = apply { config.maxRetries = retries }

    public fun retryInterval(interval: Long): JvmFursuitTvSdkBuilder = apply { config.retryInterval = interval }

    /**
     * 同步构建（仅 API Key 模式）。
     *
     * @return FursuitTvSdk 实例
     * @throws IllegalStateException 如果配置了令牌交换参数
     */
    public fun build(): FursuitTvSdk {
        validateConfiguration()

        if (useTokenExchange) {
            throw IllegalStateException(
                "Cannot use synchronous build() with token exchange. Use buildAsync() instead.",
            )
        }

        return FursuitTvSdk.create(config.toImmutable())
    }

    /**
     * 异步构建（支持令牌交换）。
     *
     * @return FursuitTvSdk 实例
     */
    public suspend fun buildAsync(): FursuitTvSdk {
        validateConfiguration()

        return if (useTokenExchange && config.clientId != null && config.clientSecret != null) {
            FursuitTvSdk.createForTokenExchange(config.clientId!!, config.clientSecret!!)
        } else {
            FursuitTvSdk.create(config.toImmutable())
        }
    }

    private fun validateConfiguration() {
        if ((config.clientId != null) xor (config.clientSecret != null)) {
            throw IllegalArgumentException("clientId and clientSecret must both be set or both be null")
        }

        if (config.apiKey == null && config.clientId == null) {
            throw IllegalArgumentException("At least one authentication method must be configured")
        }
    }
}
