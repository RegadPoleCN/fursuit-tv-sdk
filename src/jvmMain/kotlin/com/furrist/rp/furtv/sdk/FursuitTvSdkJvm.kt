package com.furrist.rp.furtv.sdk

import com.furrist.rp.furtv.sdk.model.MutableSdkConfig
import kotlinx.coroutines.runBlocking

/**
 * 使用 DSL 方式创建 FursuitTvSdk（JVM/Java 同步入口）。
 *
 * 这是 [fursuitTvSdk] 的同步包装版本，由 `runBlocking` 在 JVM 上实现。
 * 适用于 Java 调用方或不便使用 suspend / coroutine 的同步脚本场景。
 *
 * 仅在 JVM 上提供。JS/Native 调用方使用 suspend 版本的 [fursuitTvSdk]。
 *
 * @param block 配置块
 * @return FursuitTvSdk 实例
 */
public actual fun fursuitTvSdkBlocking(block: (MutableSdkConfig) -> Unit): FursuitTvSdk =
    runBlocking { fursuitTvSdk(block) }
