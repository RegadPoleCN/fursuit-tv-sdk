package com.furrist.rp.furtv.sdk

import com.furrist.rp.furtv.sdk.model.MutableSdkConfig
import com.furrist.rp.furtv.sdk.model.SdkConfig

/**
 * JVM 平台工厂，提供 Java 友好的 @JvmStatic 静态方法。
 */
public object JvmFursuitTvSdkFactory {
    /**
     * 使用 API 密钥创建 SDK 实例。
     *
     * @param apiKey API 密钥
     * @return FursuitTvSdk 实例
     */
    @JvmStatic
    public fun create(apiKey: String): FursuitTvSdk =
        FursuitTvSdk.create(apiKey)

    /**
     * 使用配置对象创建 SDK 实例。
     *
     * @param config SDK 配置
     * @return FursuitTvSdk 实例
     */
    @JvmStatic
    public fun create(config: SdkConfig): FursuitTvSdk =
        FursuitTvSdk.create(config)

    /**
     * 使用 Consumer 模式创建 SDK（Java 友好）。
     *
     * @param block 配置块
     * @return FursuitTvSdk 实例
     */
    @JvmStatic
    public fun createDsl(block: java.util.function.Consumer<MutableSdkConfig>): FursuitTvSdk {
        val mutableConfig = MutableSdkConfig()
        block.accept(mutableConfig)
        return FursuitTvSdk.create(mutableConfig.toImmutable())
    }
}
