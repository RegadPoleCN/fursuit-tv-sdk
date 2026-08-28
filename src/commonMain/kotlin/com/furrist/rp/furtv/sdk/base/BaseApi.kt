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

package com.furrist.rp.furtv.sdk.base

import com.furrist.rp.furtv.sdk.auth.AuthManager
import com.furrist.rp.furtv.sdk.model.*
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlin.js.JsExport
import kotlin.js.JsName
import love.forte.plugin.suspendtrans.annotation.JvmAsync
import love.forte.plugin.suspendtrans.annotation.JvmBlocking

/**
 * 基础接口 API，提供 helloWorld、health、version 和 theme-packs 端点。
 *
 * @param auth 认证管理器（提供 `withFreshToken` 包装 + re-exchange）
 * @param httpClient 配置好的 HTTP 客户端
 * @param baseUrl API 基础 URL
 */
@JvmBlocking
@JvmAsync
@JsExport
@JsName("BaseApi")
public class BaseApi internal constructor(
    private val auth: AuthManager,
    private val httpClient: HttpClient,
    private val baseUrl: String = "https://open-global.vdsentnet.com",
) {
    /**
     * 请求验证接口（Hello World）。
     *
     * @return 验证响应对象
     */
    @JsName("helloWorld")
    public suspend fun helloWorld(): HelloWorldResponse =
        auth.withFreshToken {
            httpClient.get("$baseUrl/api/proxy/base/hello-world")
                .body<HelloWorldResponse>()
        }

    /**
     * 健康检查接口。
     *
     * @return 健康状态响应对象
     */
    @JsName("health")
    public suspend fun health(): HealthResponse =
        auth.withFreshToken {
            httpClient.get("$baseUrl/api/proxy/furtv/health")
                .body<HealthResponse>()
        }

    /**
     * 获取 Android 客户端最新版本信息。
     *
     * @return 版本信息数据对象
     */
    @JsName("getAndroidVersion")
    public suspend fun getAndroidVersion(): AndroidVersionData =
        auth.withFreshToken {
            httpClient.get("$baseUrl/api/proxy/furtv/version/android")
                .body<AndroidVersionResponse>()
                .data
        }

    /**
     * 检查 Android 客户端版本更新。
     *
     * @param currentVersion 当前版本字符串（如 "1.2.3"）
     * @param currentVersionCode 当前版本号，null 表示不提供
     * @return 版本检查结果数据对象
     */
    @JsName("checkAndroidVersion")
    public suspend fun checkAndroidVersion(
        currentVersion: String,
        currentVersionCode: Int? = null,
    ): AndroidVersionCheckData =
        auth.withFreshToken {
            httpClient.post("$baseUrl/api/proxy/furtv/version/android/check") {
                contentType(ContentType.Application.Json)
                setBody(AndroidVersionCheckRequest(currentVersion, currentVersionCode))
            }.body<AndroidVersionCheckResponse>().data
        }

    /**
     * 获取主题包清单。
     *
     * @return 主题包清单数据对象
     */
    @JsName("getThemePacksManifest")
    public suspend fun getThemePacksManifest(): ThemePacksManifestData =
        auth.withFreshToken {
            httpClient.get("$baseUrl/api/proxy/furtv/theme-packs/manifest")
                .body<ThemePacksManifestResponse>()
                .data
        }
}
