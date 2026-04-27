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

/**
 * 基础接口 API。
 *
 * 提供系统验证、健康检查、版本管理、主题资源等基础功能的访问接口。
 * 包含 GET 和 POST 两种请求方式，返回对应的基础数据模型。
 *
 * ## 主要功能
 * - 系统验证（helloWorld）：测试 API 连接性和认证状态
 * - 健康检查（health）：检查服务端运行状态
 * - 版本管理（getAndroidVersion, checkAndroidVersion）：获取和检查 Android 客户端版本
 * - 主题资源（getThemePacksManifest）：获取可用主题包清单
 *
 * @param httpClient 配置好的 HTTP 客户端
 * @param baseUrl API 基础 URL，默认为 https://open-global.vdsentnet.com
 * @see BaseModels 基础数据模型定义
 * @see FursuitTvSdkException 异常层次结构
 */
@JsExport
@JsName("BaseApi")
public class BaseApi internal constructor(
    private val httpClient: HttpClient,
    private val baseUrl: String = "https://open-global.vdsentnet.com",
) {
    /**
     * 请求验证接口（Hello World）。
     *
     * 用于测试 API 连接性和当前认证状态。
     * 可在应用启动时调用以验证 SDK 配置是否正确。
     *
     * @return 验证响应对象（HelloWorldResponse），包含服务端返回的确认信息
     * @throws NetworkException 网络连接失败或超时
     * @throws AuthenticationException 认证凭证缺失或无效
     */
    public suspend fun helloWorld(): HelloWorldResponse {
        return httpClient.get("$baseUrl/api/proxy/base/hello-world")
            .body<HelloWorldResponse>()
    }

    /**
     * 健康检查接口。
     *
     * 检查 Fursuit.TV API 服务的运行状态和可用性。
     * 可用于监控、故障检测等场景。
     *
     * @return 健康状态响应对象（HealthResponse），包含服务状态信息
     * @throws NetworkException 网络连接失败或服务不可用
     */
    public suspend fun health(): HealthResponse {
        return httpClient.get("$baseUrl/api/proxy/furtv/health")
            .body<HealthResponse>()
    }

    /**
     * 获取 Android 客户端最新版本信息。
     *
     * 返回当前 Android 客户端的最新版本号、下载链接等信息。
     * 可用于应用内更新提示、版本对比等场景。
     *
     * @return 版本信息数据对象（AndroidVersionData），包含 version、downloadUrl 等字段
     * @throws NetworkException 网络连接失败或超时
     * @throws TokenExpiredException 访问令牌已过期或无效
     * @throws AuthenticationException 认证凭证缺失或无效
     */
    public suspend fun getAndroidVersion(): AndroidVersionData {
        val response =
            httpClient.get("$baseUrl/api/proxy/furtv/version/android")
                .body<AndroidVersionResponse>()
        return response.data
    }

    /**
     * 检查 Android 客户端版本更新。
     *
     * 将当前客户端版本与服务端最新版本进行对比，返回是否需要更新。
     * 可用于启动时自动检查更新、强制更新提示等场景。
     *
     * @param currentVersion 当前版本字符串（如 "1.2.3"）
     * @param currentVersionCode 当前版本号（整数），null 表示不提供
     * @return 版本检查结果数据对象（AndroidVersionCheckData），包含 updateAvailable 等字段
     * @throws NetworkException 网络连接失败或超时
     * @throws TokenExpiredException 访问令牌已过期或无效
     * @throws AuthenticationException 认证凭证缺失或无效
     * @throws ValidationException 版本格式无效
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
     * 获取主题包清单。
     *
     * 返回所有可用的主题包列表，包括主题名称、版本、下载链接等信息。
     * 可用于主题选择界面、主题下载管理等场景。
     *
     * @return 主题包清单数据对象（ThemePacksManifestData），包含 themePacks 列表
     * @throws NetworkException 网络连接失败或超时
     * @throws TokenExpiredException 访问令牌已过期或无效
     * @throws AuthenticationException 认证凭证缺失或无效
     */
    public suspend fun getThemePacksManifest(): ThemePacksManifestData {
        val response =
            httpClient.get("$baseUrl/api/proxy/furtv/theme-packs/manifest")
                .body<ThemePacksManifestResponse>()
        return response.data
    }
}
