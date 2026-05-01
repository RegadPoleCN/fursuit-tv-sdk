package com.furrist.rp.furtv.sdk.base

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlin.js.JsExport
import kotlin.js.JsName
import love.forte.plugin.suspendtrans.annotation.JvmAsync
import love.forte.plugin.suspendtrans.annotation.JvmBlocking

/**
 * 基础接口 API，提供 helloWorld、health、version 和 theme-packs 端点。
 *
 * @param httpClient 配置好的 HTTP 客户端
 * @param baseUrl API 基础 URL
 */
@JvmBlocking
@JvmAsync
@JsExport
@JsName("BaseApi")
public class BaseApi internal constructor(
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
        httpClient.get("$baseUrl/api/proxy/base/hello-world")
            .body<HelloWorldResponse>()

    /**
     * 健康检查接口。
     *
     * @return 健康状态响应对象
     */
    @JsName("health")
    public suspend fun health(): HealthResponse =
        httpClient.get("$baseUrl/api/proxy/furtv/health")
            .body<HealthResponse>()

    /**
     * 获取 Android 客户端最新版本信息。
     *
     * @return 版本信息数据对象
     */
    @JsName("getAndroidVersion")
    public suspend fun getAndroidVersion(): AndroidVersionData {
        val response =
            httpClient.get("$baseUrl/api/proxy/furtv/version/android")
                .body<AndroidVersionResponse>()
        return response.data
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
    ): AndroidVersionCheckData {
        val response =
            httpClient.post("$baseUrl/api/proxy/furtv/version/android/check") {
                contentType(ContentType.Application.Json)
                setBody(AndroidVersionCheckRequest(currentVersion, currentVersionCode))
            }.body<AndroidVersionCheckResponse>()
        return response.data
    }

    /**
     * 获取主题包清单。
     *
     * @return 主题包清单数据对象
     */
    @JsName("getThemePacksManifest")
    public suspend fun getThemePacksManifest(): ThemePacksManifestData {
        val response =
            httpClient.get("$baseUrl/api/proxy/furtv/theme-packs/manifest")
                .body<ThemePacksManifestResponse>()
        return response.data
    }
}
