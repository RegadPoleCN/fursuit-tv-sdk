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

package com.furrist.rp.furtv.sdk.model

import kotlin.js.JsExport
import kotlin.js.JsName

/**
 * 可变的 SDK 配置构建器，作为 `fursuitTvSdk { ... }` / `fursuitTvSdkBlocking { ... }` DSL 的内部 builder。
 *
 * 用户只需直接设置属性即可，例如：
 *
 * ```kotlin
 * fursuitTvSdk {
 *     clientId = "vap_xxx"
 *     clientSecret = "your-secret"
 * }
 * ```
 *
 * [!] 安全警告：此类的 `@JsExport` 暴露在 JS 端可被任何代码修改。
 *     生产环境请仅在受信代码路径使用，不可从用户输入或网络响应直接传入
 *     `MutableSdkConfig` 实例以避免配置被篡改。
 *
 * `MutableSdkConfig` 仍是 `@JsExport` 公开类，但不提供链式 `setXxx(...)` 方法（直接赋属性更直观）。
 *
 * @property baseUrl API 基础 URL
 * @property clientId 客户端 ID（即 VDS 文档中的 appId）
 * @property clientSecret 客户端密钥
 * @property requestTimeout 请求超时时间（毫秒）
 * @property connectTimeout 连接超时时间（毫秒）
 * @property socketTimeout 套接字超时时间（毫秒）
 * @property logLevel HTTP 日志级别，参见 [SdkLogLevel]
 * @property enableRetry 是否启用重试
 * @property maxRetries 最大重试次数
 * @property retryInterval 重试间隔（毫秒）
 */
@JsExport
@JsName("MutableSdkConfig")
public class MutableSdkConfig {
    @JsName("baseUrl")
    public var baseUrl: String = SdkConfig.DEFAULT_BASE_URL

    @JsName("clientId")
    public var clientId: String? = null

    @JsName("clientSecret")
    public var clientSecret: String? = null

    @JsName("requestTimeout")
    public var requestTimeout: Long = SdkConfig.DEFAULT_REQUEST_TIMEOUT

    @JsName("connectTimeout")
    public var connectTimeout: Long = SdkConfig.DEFAULT_CONNECT_TIMEOUT

    @JsName("socketTimeout")
    public var socketTimeout: Long = SdkConfig.DEFAULT_SOCKET_TIMEOUT

    @JsName("logLevel")
    public var logLevel: SdkLogLevel = SdkLogLevel.INFO

    @JsName("enableRetry")
    public var enableRetry: Boolean = true

    @JsName("maxRetries")
    public var maxRetries: Int = SdkConfig.DEFAULT_MAX_RETRIES

    @JsName("retryInterval")
    public var retryInterval: Long = SdkConfig.DEFAULT_RETRY_INTERVAL

    internal fun toImmutable(): SdkConfig =
        SdkConfig(
            baseUrl = baseUrl,
            clientId = clientId,
            clientSecret = clientSecret,
            requestTimeout = requestTimeout,
            connectTimeout = connectTimeout,
            socketTimeout = socketTimeout,
            logLevel = logLevel,
            enableRetry = enableRetry,
            maxRetries = maxRetries,
            retryInterval = retryInterval,
        )
}

/**
 * Copy all fields from `this` MutableSdkConfig to [target].
 *
 * Used by `FursuitTvSdkBuilder.build()` to forward builder-captured config into the
 * `fursuitTvSdk { ... }` DSL block (single source of truth for `toImmutable()`).
 */
internal fun MutableSdkConfig.copyTo(target: MutableSdkConfig) {
    target.baseUrl = baseUrl
    target.clientId = clientId
    target.clientSecret = clientSecret
    target.requestTimeout = requestTimeout
    target.connectTimeout = connectTimeout
    target.socketTimeout = socketTimeout
    target.logLevel = logLevel
    target.enableRetry = enableRetry
    target.maxRetries = maxRetries
    target.retryInterval = retryInterval
}
