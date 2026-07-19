package com.furrist.rp.furtv.sdk

import com.furrist.rp.furtv.sdk.model.MutableSdkConfig

/**
 * JS 平台不提供线程阻塞；调用 `await fursuitTvSdk { ... }` 替代。
 */
public actual fun fursuitTvSdkBlocking(block: (MutableSdkConfig) -> Unit): FursuitTvSdk =
    throw UnsupportedOperationException(
        "fursuitTvSdkBlocking is not available on JS. Use `await fursuitTvSdk { ... }` instead.",
    )
