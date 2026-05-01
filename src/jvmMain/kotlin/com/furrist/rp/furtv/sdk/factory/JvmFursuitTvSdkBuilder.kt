package com.furrist.rp.furtv.sdk.factory

import com.furrist.rp.furtv.sdk.FursuitTvSdk
import com.furrist.rp.furtv.sdk.model.MutableSdkConfig
import com.furrist.rp.furtv.sdk.model.SdkLogLevel
import love.forte.plugin.suspendtrans.annotation.JvmAsync
import love.forte.plugin.suspendtrans.annotation.JvmBlocking

/**
 * 纯 Java 风格的链式构建器。
 *
 * @see FursuitTvSdk
 * @see MutableSdkConfig
 */
public class JvmFursuitTvSdkBuilder private constructor(
    private val config: MutableSdkConfig,
) {

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
        }

    public fun clientSecret(secret: String): JvmFursuitTvSdkBuilder = apply { config.clientSecret = secret }

    // 超时配置
    public fun requestTimeout(timeout: Long): JvmFursuitTvSdkBuilder = apply { config.requestTimeout = timeout }

    public fun connectTimeout(timeout: Long): JvmFursuitTvSdkBuilder = apply { config.connectTimeout = timeout }

    public fun socketTimeout(timeout: Long): JvmFursuitTvSdkBuilder = apply { config.socketTimeout = timeout }

    // 高级配置

    /**
     * 设置 HTTP 客户端日志级别。
     *
     * @param level SDK 日志级别，参见 [SdkLogLevel]
     * @return 此构建器实例（支持链式调用）
     */
    public fun logLevel(level: SdkLogLevel): JvmFursuitTvSdkBuilder = apply { config.logLevel = level }

    public fun enableRetry(enable: Boolean): JvmFursuitTvSdkBuilder = apply { config.enableRetry = enable }

    public fun maxRetries(retries: Int): JvmFursuitTvSdkBuilder = apply { config.maxRetries = retries }

    public fun retryInterval(interval: Long): JvmFursuitTvSdkBuilder = apply { config.retryInterval = interval }

    @JvmBlocking
    @JvmAsync
    public suspend fun build(): FursuitTvSdk {
        validateConfiguration()

        return if (config.clientId != null && config.clientSecret != null) {
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
