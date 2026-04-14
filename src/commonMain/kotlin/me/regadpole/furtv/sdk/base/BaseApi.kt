package me.regadpole.furtv.sdk.base

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

/**
 * 基础接口 API
 * 提供 VDS 基础功能的访问接口，包括验证、健康检查、版本管理、主题资源等
 *
 * 参考官方文档：
 * - [请求验证接口](../../../../vds-docs/基础接口/请求验证接口（HelloWorld）.md)
 * - [健康检查](../../../../vds-docs/Fursuit.TV 兽频道/基础能力 - 健康与版本/健康检查（furtv.health）.md)
 * - [Android 版本信息](../../../../vds-docs/Fursuit.TV 兽频道/基础能力 - 健康与版本/安卓版本信息（furtv.version.android）.md)
 * - [Android 版本检查](../../../../vds-docs/Fursuit.TV 兽频道/基础能力 - 健康与版本/安卓版本检查（furtv.version.android.check）.md)
 * - [主题包清单](../../../../vds-docs/Fursuit.TV 兽频道/基础能力 - 主题资源/主题包清单（furtv.themepacks.manifest）.md)
 *
 * @param httpClient 配置好的 HTTP 客户端
 * @param baseUrl API 基础 URL，默认为 https://open-global.vdsentnet.com
 */
public class BaseApi(
    private val httpClient: HttpClient,
    private val baseUrl: String = "https://open-global.vdsentnet.com",
) {
    /**
     * HelloWorld - 请求验证接口
     * 用于验证 API 连通性和请求格式是否正确
     *
     * 端点：`GET /api/proxy/base/hello-world`
     *
     * 官方文档：[请求验证接口](../../../../vds-docs/基础接口/请求验证接口（HelloWorld）.md)
     *
     * @return HelloWorldResponse 包含欢迎消息、验证信息、appId 和 requestId
     */
    public suspend fun helloWorld(): HelloWorldResponse {
        return httpClient.get("$baseUrl/api/proxy/base/hello-world")
            .body<HelloWorldResponse>()
    }

    /**
     * 健康检查
     * 检查 Fursuit.TV 服务的可用性状态
     *
     * 端点：`GET /api/proxy/furtv/health`
     *
     * 官方文档：[健康检查](../../../../vds-docs/Fursuit.TV 兽频道/基础能力 - 健康与版本/健康检查（furtv.health）.md)
     *
     * @return HealthResponse 包含服务状态消息、时间戳和 requestId
     */
    public suspend fun health(): HealthResponse {
        return httpClient.get("$baseUrl/api/proxy/furtv/health")
            .body<HealthResponse>()
    }

    /**
     * 获取 Android 版本信息
     * 获取当前最新的 Android 客户端版本信息
     *
     * 端点：`GET /api/proxy/furtv/version/android`
     *
     * 官方文档：[Android 版本信息](../../../../vds-docs/Fursuit.TV 兽频道/基础能力 - 健康与版本/安卓版本信息（furtv.version.android）.md)
     *
     * @return AndroidVersionData 包含版本号、下载链接、更新日志等
     */
    public suspend fun getAndroidVersion(): AndroidVersionData {
        val response =
            httpClient.get("$baseUrl/api/proxy/furtv/version/android")
                .body<AndroidVersionResponse>()
        return response.data
    }

    /**
     * 检查 Android 版本更新
     * 根据当前版本检查是否需要更新
     *
     * 端点：`POST /api/proxy/furtv/version/android/check`
     *
     * 官方文档：[Android 版本检查](../../../../vds-docs/Fursuit.TV 兽频道/基础能力 - 健康与版本/安卓版本检查（furtv.version.android.check）.md)
     *
     * @param currentVersion 当前应用版本字符串
     * @param currentVersionCode 当前应用版本号（可选）
     * @return AndroidVersionCheckData 包含是否需要更新、是否强制更新等信息
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
     * 获取所有可用主题包的列表和下载信息
     *
     * 端点：`GET /api/proxy/furtv/theme-packs/manifest`
     *
     * 官方文档：[主题包清单](../../../../vds-docs/Fursuit.TV 兽频道/基础能力 - 主题资源/主题包清单（furtv.themepacks.manifest）.md)
     *
     * @return ThemePacksManifestData 包含主题包版本和列表
     */
    public suspend fun getThemePacksManifest(): ThemePacksManifestData {
        val response =
            httpClient.get("$baseUrl/api/proxy/furtv/theme-packs/manifest")
                .body<ThemePacksManifestResponse>()
        return response.data
    }
}
