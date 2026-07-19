package com.furrist.rp.furtv.sdk

import com.furrist.rp.furtv.sdk.model.MutableSdkConfig

/**
 * Native 平台不提供线程阻塞；调用方自行包装 `fursuitTvSdk { ... }` 在 `runBlocking { }` 内。
 */
public actual fun fursuitTvSdkBlocking(block: (MutableSdkConfig) -> Unit): FursuitTvSdk =
    throw UnsupportedOperationException(
        "fursuitTvSdkBlocking is not available on Native. Wrap `fursuitTvSdk { ... }` in your own scheduling.",
    )
