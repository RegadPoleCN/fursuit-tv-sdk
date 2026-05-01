package com.furrist.rp.furtv.sdk

import com.furrist.rp.furtv.sdk.model.MutableSdkConfig
import com.furrist.rp.furtv.sdk.model.SdkConfig
import love.forte.plugin.suspendtrans.annotation.JvmAsync
import love.forte.plugin.suspendtrans.annotation.JvmBlocking

public object JvmFursuitTvSdkFactory {
    @JvmStatic
    public fun create(apiKey: String): FursuitTvSdk =
        FursuitTvSdk.create(apiKey)

    @JvmStatic
    public fun create(config: SdkConfig): FursuitTvSdk =
        FursuitTvSdk.create(config)

    @JvmBlocking
    @JvmAsync
    @JvmStatic
    public suspend fun createDsl(block: java.util.function.Consumer<MutableSdkConfig>): FursuitTvSdk {
        val mutableConfig = MutableSdkConfig()
        block.accept(mutableConfig)
        val config = mutableConfig.toImmutable()

        if (config.clientId != null && config.clientSecret != null && config.apiKey == null) {
            return FursuitTvSdk.createForTokenExchange(config.clientId, config.clientSecret)
        }

        return FursuitTvSdk.create(config)
    }
}
