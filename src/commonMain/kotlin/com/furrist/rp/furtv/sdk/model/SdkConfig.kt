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

import io.ktor.client.plugins.logging.*
import kotlin.js.JsExport
import kotlin.js.JsName
import kotlin.jvm.JvmStatic

/**
 * SDK 日志级别枚举，控制 HTTP 请求日志输出详细程度。
 */
@JsExport
@JsName("SdkLogLevel")
public enum class SdkLogLevel {
    /**
     * 关闭所有日志
     */
    OFF,

    /**
     * 仅输出错误日志
     */
    ERROR,

    /**
     * 输出警告及以上级别日志
     */
    WARNING,

    /**
     * 输出信息及以上级别日志（默认）
     */
    INFO,

    /**
     * 输出调试及以上级别日志，包含请求/响应体
     */
    DEBUG,

    /**
     * 输出所有级别日志
     */
    ALL, ;

    /**
     * 将 SDK 日志级别转换为 Ktor 客户端的 [LogLevel]。
     *
     * @return 对应的 Ktor 日志级别
     */
    internal fun toKtorLogLevel(): LogLevel =
        when (this) {
            OFF -> LogLevel.NONE
            ERROR -> LogLevel.INFO
            WARNING -> LogLevel.INFO
            INFO -> LogLevel.INFO
            DEBUG -> LogLevel.BODY
            ALL -> LogLevel.ALL
        }
}

/**
 * Fursuit.TV SDK 不可变配置。
 *
 * @property baseUrl API 基础 URL
 * @property clientId 客户端 ID（即 VDS 文档中的 appId），用于签名交换或 OAuth
 * @property clientSecret 客户端密钥，用于签名交换或 OAuth
 * @property requestTimeout 请求超时时间（毫秒）
 * @property connectTimeout 连接超时时间（毫秒）
 * @property socketTimeout 套接字超时时间（毫秒）
 * @property logLevel HTTP 日志级别，参见 [SdkLogLevel]
 * @property enableRetry 是否启用重试
 * @property maxRetries 最大重试次数
 * @property retryInterval 重试间隔（毫秒）
 */
@JsExport
@JsName("SdkConfig")
public class SdkConfig(
    @JsName("baseUrl") public val baseUrl: String = DEFAULT_BASE_URL,
    @JsName("clientId") public val clientId: String? = null,
    @JsName("clientSecret") public val clientSecret: String? = null,
    @JsName("requestTimeout") public val requestTimeout: Long = DEFAULT_REQUEST_TIMEOUT,
    @JsName("connectTimeout") public val connectTimeout: Long = DEFAULT_CONNECT_TIMEOUT,
    @JsName("socketTimeout") public val socketTimeout: Long = DEFAULT_SOCKET_TIMEOUT,
    @JsName("logLevel") public val logLevel: SdkLogLevel = SdkLogLevel.INFO,
    @JsName("enableRetry") public val enableRetry: Boolean = true,
    @JsName("maxRetries") public val maxRetries: Int = DEFAULT_MAX_RETRIES,
    @JsName("retryInterval") public val retryInterval: Long = DEFAULT_RETRY_INTERVAL,
) {
    public companion object {
        /** API 基础 URL 默认值。 */
        public const val DEFAULT_BASE_URL: String = "https://open-global.vdsentnet.com"

        /** 请求超时时间（毫秒）默认值。 */
        public const val DEFAULT_REQUEST_TIMEOUT: Long = 30000L

        /** 连接超时时间（毫秒）默认值。 */
        public const val DEFAULT_CONNECT_TIMEOUT: Long = 10000L

        /** 套接字超时时间（毫秒）默认值。 */
        public const val DEFAULT_SOCKET_TIMEOUT: Long = 30000L

        /** 最大重试次数默认值。 */
        public const val DEFAULT_MAX_RETRIES: Int = 3

        /** 重试间隔（毫秒）默认值。 */
        public const val DEFAULT_RETRY_INTERVAL: Long = 1000L

        /**
         * 为签名交换创建配置。
         *
         * @param clientId 客户端 ID（即 VDS 文档中的 appId，格式 vap_xxxx）
         * @param clientSecret 客户端密钥
         * @return SdkConfig 实例
         */
        @JvmStatic
        @JsName("forTokenExchange")
        public fun forTokenExchange(clientId: String, clientSecret: String): SdkConfig =
            SdkConfig(clientId = clientId, clientSecret = clientSecret)
    }
}
