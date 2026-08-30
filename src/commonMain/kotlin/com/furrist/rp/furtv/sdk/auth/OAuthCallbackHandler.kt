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

package com.furrist.rp.furtv.sdk.auth

import com.furrist.rp.furtv.sdk.model.OAuthConfig
import kotlin.js.JsExport
import kotlin.js.JsName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

/**
 * OAuth 回调结果。
 */
public sealed class OAuthCallbackResult {
    /**
     * 授权成功，携带授权码和 state。
     *
     * @param code 授权码
     * @param state 回调中的 state 参数
     */
    @JsName("OAuthCallbackSuccess")
    @Serializable
    public data class Success(val code: String, val state: String) : OAuthCallbackResult()

    /**
     * 授权失败，携带错误信息。
     *
     * @param message 错误描述
     * @param errorCode OAuth 错误代码（如 "access_denied"），可能为 null
     * @param cause 根本原因，可能为 null
     */
    @JsName("OAuthCallbackError")
    @Serializable
    public data class Error(
        val message: String,
        val errorCode: String? = null,
        @Transient
        val cause: Throwable? = null,
    ) : OAuthCallbackResult()
}

/**
 * OAuth 回调处理器接口。
 *
 * 定义 OAuth 授权回调的监听与处理流程。典型用法：
 * 1. 调用 [startListening] 启动回调服务器
 * 2. 通过 [callbackUrl] 构建授权 URL 并引导用户访问
 * 3. 调用 [waitForCallback] 等待回调结果
 * 4. 调用 [stop] 释放资源
 *
 * 也可使用便捷方法 [startAndGetCallback] 一次性完成上述步骤。
 */
@JsExport
public interface OAuthCallbackHandler {
    /**
     * 回调接收地址，格式为 `http://localhost:{port}{path}`。
     */
    @JsName("callbackUrl")
    public val callbackUrl: String

    /**
     * 启动回调监听。
     *
     * 调用后回调服务器开始就绪，可通过 [callbackUrl] 构建授权 URL。
     *
     * @throws IllegalStateException 如果服务器启动失败
     */
    @JsName("startListening")
    public suspend fun startListening()

    /**
     * 等待 OAuth 回调结果。
     *
     * 阻塞当前协程直到收到回调或超时。调用前须先调用 [startListening]。
     *
     * @return 回调结果
     */
    @JsName("waitForCallback")
    public suspend fun waitForCallback(): OAuthCallbackResult

    /**
     * 便捷方法：启动监听并等待回调。
     *
     * 等价于依次调用 [startListening]、[waitForCallback]。接口默认实现**不会**打开
     * [authorizeUrl]——如何引导用户完成授权由各平台 override 决定（JVM/Native 打开
     * 浏览器或提示复制 URL，浏览器环境依赖中继页转发回调）。
     *
     * @param authorizeUrl 授权端点 URL（供平台实现引导用户时使用）
     * @return 回调结果
     */
    @JsName("startAndGetCallback")
    public suspend fun startAndGetCallback(authorizeUrl: String): OAuthCallbackResult {
        startListening()
        return waitForCallback()
    }

    /**
     * 停止回调监听并释放资源。
     */
    @JsName("stop")
    public suspend fun stop()
}

/**
 * 创建平台默认的 OAuth 回调处理器。
 *
 * 根据当前运行平台自动选择合适的实现：
 * - **JVM**: 启动本地 HTTP 服务器，自动打开浏览器
 * - **JS (浏览器)**: 使用 postMessage 机制监听回调
 * - **JS (Node.js)**: 使用 Node.js http 模块创建本地服务器
 * - **Native**: 启动本地 HTTP 服务器接收回调
 *
 * @param config OAuth 回调配置
 * @return 平台对应的 [OAuthCallbackHandler] 实例
 */
@JsExport
@JsName("createDefaultOAuthHandler")
public expect fun createDefaultOAuthHandler(
    config: OAuthConfig = OAuthConfig(),
): OAuthCallbackHandler
