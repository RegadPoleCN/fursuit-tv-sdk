package com.furrist.rp.furtv.sdk.base

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

/**
 * 基础接口 API
 * 提供验证、健康检查、版本管理、主题资源等功能
 *
 * @param httpClient HTTP 客户端
 * @param baseUrl API 基础 URL
 */
public class BaseApi(
    private val httpClient: HttpClient,
    private val baseUrl: String = "https://open-global.vdsentnet.com",
) {
    /**
     * HelloWorld - 请求验证接口
     * 端点：GET /api/proxy/base/hello-world
     * @return 验证响应
     */
    public suspend fun helloWorld(): HelloWorldResponse {
        return httpClient.get("$baseUrl/api/proxy/base/hello-world")
            .body<HelloWorldResponse>()
    }

    /**
     * 健康检查
     * 端点：GET /api/proxy/furtv/health
     * @return 健康状态响应
     */
    public suspend fun health(): HealthResponse {
        return httpClient.get("$baseUrl/api/proxy/furtv/health")
            .body<HealthResponse>()
    }

    /**
     * 获取 Android 版本信息
     * 端点：GET /api/proxy/furtv/version/android
     * @return 版本信息数据
     */
    public suspend fun getAndroidVersion(): AndroidVersionData {
        val response =
            httpClient.get("$baseUrl/api/proxy/furtv/version/android")
                .body<AndroidVersionResponse>()
        return response.data
    }

    /**
     * 检查 Android 版本更新
     * 端点：POST /api/proxy/furtv/version/android/check
     *
     * @param currentVersion 当前版本字符串
     * @param currentVersionCode 当前版本号（可选）
     * @return 版本检查结果
     */
    public suspend fun checkAndroidVersion(
        currentVersion: String,
        currentVersionCode: Int? = null,
    ): AndroidVersionCheckData {
        val response =
            httpClient.post("$baseUrl/api/proxy/furtv/version/android/check") {
                contentType(ContentType.Application.Json)
                setBody(AndroidVersionCheckRequest(currentVersion, currentVersionCode))
            }.body<AndroidVersionCheckResponse>()
        return response.data
    }

    /**
     * 获取主题包清单
     * 端点：GET /api/proxy/furtv/theme-packs/manifest
     * @return 主题包清单数据
     */
    public suspend fun getThemePacksManifest(): ThemePacksManifestData {
        val response =
            httpClient.get("$baseUrl/api/proxy/furtv/theme-packs/manifest")
                .body<ThemePacksManifestResponse>()
        return response.data
    }
}
