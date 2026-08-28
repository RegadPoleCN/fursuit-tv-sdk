/*
 *   Copyright 2026 RegadPoleCN
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.furrist.rp.furtv.sdk

import com.furrist.rp.furtv.sdk.model.MutableSdkConfig
import com.furrist.rp.furtv.sdk.model.SdkLogLevel
import com.furrist.rp.furtv.sdk.model.copyTo
import kotlin.js.JsExport
import kotlin.js.JsName
import kotlin.jvm.JvmStatic
import love.forte.plugin.suspendtrans.annotation.JvmAsync
import love.forte.plugin.suspendtrans.annotation.JvmBlocking

/**
 * Fursuit.TV SDK 链式 Builder（cross-language）。
 *
 * 单一公共入口，替代旧的 `FursuitTvSdk.create` / `createForTokenExchange` / `withApiKey` /
 * `JvmFursuitTvSdkBuilder` / `fursuitTvSdkBlocking`。suspend `build()` 由 suspend-transform
 * 插件在 JVM bytecode 阶段生成同步 `buildBlocking()` 和异步 `buildAsync()`。
 *
 * 用法（Kotlin / Java / JS / Native）：
 * ```kotlin
 * val sdk = FursuitTvSdkBuilder()
 *     .clientId("vap_xxx")
 *     .clientSecret("your-secret")
 *     .logLevel(SdkLogLevel.INFO)
 *     .build()
 * ```
 */
@JsExport
@JsName("FursuitTvSdkBuilder")
public class FursuitTvSdkBuilder {
    private val config = MutableSdkConfig()

    public fun baseUrl(value: String): FursuitTvSdkBuilder = apply { config.baseUrl = value }

    /**
     * @deprecated apiKey-only init 被禁止。提供 `clientId` + `clientSecret` 走 token exchange。
     * 该字段保留以兼容老代码，但调用方应迁移到 `clientId`/`clientSecret`。
     */
    @Deprecated("apiKey at init is forbidden; provide clientId+clientSecret. The platform apiKey is auto-obtained via token exchange.")
    public fun apiKey(value: String): FursuitTvSdkBuilder = apply { config.apiKey = value }

    public fun clientId(value: String): FursuitTvSdkBuilder = apply { config.clientId = value }

    public fun clientSecret(value: String): FursuitTvSdkBuilder = apply { config.clientSecret = value }

    public fun requestTimeout(value: Long): FursuitTvSdkBuilder = apply { config.requestTimeout = value }

    public fun connectTimeout(value: Long): FursuitTvSdkBuilder = apply { config.connectTimeout = value }

    public fun socketTimeout(value: Long): FursuitTvSdkBuilder = apply { config.socketTimeout = value }

    public fun logLevel(value: SdkLogLevel): FursuitTvSdkBuilder = apply { config.logLevel = value }

    public fun enableRetry(value: Boolean): FursuitTvSdkBuilder = apply { config.enableRetry = value }

    public fun maxRetries(value: Int): FursuitTvSdkBuilder = apply { config.maxRetries = value }

    public fun retryInterval(value: Long): FursuitTvSdkBuilder = apply { config.retryInterval = value }

    /**
     * 构建 `FursuitTvSdk` 实例。**必须**先设置 `clientId` 和 `clientSecret`，否则抛
     * `IllegalStateException`。委托给 `fursuitTvSdk { config.copyTo(it) }` 复用 DSL 的
     * require 校验 + token exchange 路径（单一来源）。
     */
    @JvmBlocking
    @JvmAsync
    public suspend fun build(): FursuitTvSdk {
        require(config.clientId != null && config.clientSecret != null) {
            "FursuitTvSdkBuilder.build() requires both clientId and clientSecret. " +
                "apiKey-only init is forbidden (the platform apiKey is auto-obtained via token exchange). " +
                "Use .clientId(\"...\").clientSecret(\"...\") before .build()."
        }
        return fursuitTvSdk { mutableConfig -> config.copyTo(mutableConfig) }
    }

    public companion object {
        /**
         * 工厂方法（Java 友好）。Java 调用方用 `FursuitTvSdkBuilder.create()` 而不是
         * `FursuitTvSdkBuilder.Companion.create()`，`@JvmStatic` 去掉 `Companion` 中缀。
         */
        @JvmStatic
        public fun create(): FursuitTvSdkBuilder = FursuitTvSdkBuilder()
    }
}
