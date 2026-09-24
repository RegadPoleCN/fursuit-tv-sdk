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
 * 基础接口 API，提供 helloWorld、health、version、theme-packs、request-logs 查询及安全图片鉴权能力。
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
     * @return 版本信息响应包装（含 success/requestId 元数据）
     */
    @JsName("getAndroidVersion")
    public suspend fun getAndroidVersion(): AndroidVersionResponse =
        auth.withFreshToken {
            httpClient.get("$baseUrl/api/proxy/furtv/version/android")
                .body<AndroidVersionResponse>()
        }

    /**
     * 检查 Android 客户端版本更新。
     *
     * @param currentVersion 当前版本字符串（如 "1.2.3"）
     * @param currentVersionCode 当前版本号（必填）
     * @return 版本检查响应包装（含 success/requestId 元数据）
     */
    @JsName("checkAndroidVersion")
    public suspend fun checkAndroidVersion(
        currentVersion: String,
        currentVersionCode: Int,
    ): AndroidVersionCheckResponse =
        auth.withFreshToken {
            httpClient.post("$baseUrl/api/proxy/furtv/version/android/check") {
                contentType(ContentType.Application.Json)
                setBody(AndroidVersionCheckRequest(currentVersion, currentVersionCode))
            }.body<AndroidVersionCheckResponse>()
        }

    /**
     * 获取主题包清单。
     *
     * @return 主题包清单响应包装（含 success/requestId 元数据）
     */
    @JsName("getThemePacksManifest")
    public suspend fun getThemePacksManifest(): ThemePacksManifestResponse =
        auth.withFreshToken {
            httpClient.get("$baseUrl/api/proxy/furtv/theme-packs/manifest")
                .body<ThemePacksManifestResponse>()
        }

    /**
     * 按 query 关键词检索平台请求日志（官方《RequestID查询.md》）。
     *
     * @param query 检索词或 requestId
     * @return 日志搜索结果响应
     */
    @JsName("searchRequestLogs")
    public suspend fun searchRequestLogs(query: String): RequestLogsSearchResponse =
        auth.withFreshToken {
            httpClient.get("$baseUrl/api/vds-auth/request-logs/search") {
                parameter("q", query)
            }.body<RequestLogsSearchResponse>()
        }

    /**
     * 按特定 requestId 精确查询日志详情（官方《RequestID查询.md》）。
     *
     * @param requestId 请求追踪 ID
     * @return 日志详情响应
     */
    @JsName("getRequestLog")
    public suspend fun getRequestLog(requestId: String): RequestLogDetailResponse =
        auth.withFreshToken {
            httpClient.get("$baseUrl/api/vds-auth/request-logs/$requestId")
                .body<RequestLogDetailResponse>()
        }

    /**
     * 下载官方可信代理后的安全图片。
     *
     * 依据官方《图片链接说明》，兽频道图片链接仅 30 秒有效期且必须携带应用签名头。
     * 本方法自动在请求中注入最新 `X-Api-Key` 并获取原始二进制数据。
     *
     * @param imageUrl 可信代理图片地址（如 https://imageproxy-vdp.vdsentnet.com/ugc/...）
     * @return 图片字节数组
     */
    @JsName("fetchAuthorizedImage")
    public suspend fun fetchAuthorizedImage(imageUrl: String): ByteArray =
        auth.withFreshToken {
            httpClient.get(imageUrl).body<ByteArray>()
        }
}
