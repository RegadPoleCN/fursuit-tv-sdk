package com.furrist.rp.furtv.sdk.base

import kotlin.js.JsExport
import kotlin.js.JsName
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * HelloWorld 接口响应，无 data 包装。
 *
 * @param success 请求是否成功
 * @param message 响应消息
 * @param verify 验证状态
 * @param appId 应用 ID
 * @param requestId 请求 ID
 */
@JsExport
@JsName("HelloWorldResponse")
@Serializable
public data class HelloWorldResponse(
    public val success: Boolean,
    public val message: String,
    public val verify: String,
    public val appId: String,
    public val requestId: String,
)

/**
 * 健康检查接口响应，无 data 包装。
 *
 * @param success 请求是否成功
 * @param message 健康状态消息
 * @param timestamp 时间戳
 * @param requestId 请求 ID
 */
@JsExport
@JsName("HealthResponse")
@Serializable
public data class HealthResponse(
    public val success: Boolean,
    public val message: String,
    public val timestamp: String,
    public val requestId: String,
)

/**
 * Android 版本信息响应包装。
 *
 * @param success 请求是否成功
 * @param data 版本数据
 * @param requestId 请求 ID
 */
@JsExport
@JsName("AndroidVersionResponse")
@Serializable
public data class AndroidVersionResponse(
    public val success: Boolean,
    public val data: AndroidVersionData,
    public val requestId: String,
)

/**
 * Android 应用版本数据。
 *
 * @param version 版本号
 * @param versionCode 版本代码
 * @param description 版本描述
 * @param forceUpdate 是否强制更新
 * @param downloadUrl 下载链接
 * @param updateTime 更新时间
 * @param minSupportedVersion 最低支持版本
 * @param changelog 更新日志列表
 */
@JsExport
@JsName("AndroidVersionData")
@Serializable
public data class AndroidVersionData(
    public val version: String,
    public val versionCode: Int,
    public val description: String,
    public val forceUpdate: Boolean,
    public val downloadUrl: String,
    public val updateTime: String,
    public val minSupportedVersion: String,
    public val changelog: List<String>,
)

/**
 * Android 版本检查请求体。
 *
 * @param currentVersion 当前版本字符串
 * @param currentVersionCode 当前版本代码（可选）
 */
@JsExport
@JsName("AndroidVersionCheckRequest")
@Serializable
public data class AndroidVersionCheckRequest(
    public val currentVersion: String,
    public val currentVersionCode: Int? = null,
)

/**
 * Android 版本检查响应包装。
 *
 * @param success 请求是否成功
 * @param data 版本检查结果数据
 * @param requestId 请求 ID
 */
@JsExport
@JsName("AndroidVersionCheckResponse")
@Serializable
public data class AndroidVersionCheckResponse(
    public val success: Boolean,
    public val data: AndroidVersionCheckData,
    public val requestId: String,
)

/**
 * Android 版本检查结果数据。
 *
 * @param needUpdate 是否需要更新
 * @param forceUpdate 是否强制更新
 * @param currentVersion 当前版本信息
 * @param latestVersion 最新版本信息
 */
@JsExport
@JsName("AndroidVersionCheckData")
@Serializable
public data class AndroidVersionCheckData(
    public val needUpdate: Boolean,
    public val forceUpdate: Boolean,
    public val currentVersion: VersionInfo,
    public val latestVersion: AndroidVersionData,
)

/**
 * 版本基本信息。
 *
 * @param version 版本字符串
 * @param versionCode 版本代码
 */
@JsExport
@JsName("VersionInfo")
@Serializable
public data class VersionInfo(
    public val version: String,
    public val versionCode: Int,
)

/**
 * 主题包清单响应包装。
 *
 * @param success 请求是否成功
 * @param data 主题包清单数据
 * @param requestId 请求 ID
 */
@JsExport
@JsName("ThemePacksManifestResponse")
@Serializable
public data class ThemePacksManifestResponse(
    public val success: Boolean,
    public val data: ThemePacksManifestData,
    public val requestId: String,
)

/**
 * 主题包清单数据。
 *
 * @param version 清单版本
 * @param packs 主题包列表
 */
@JsExport
@JsName("ThemePacksManifestData")
@Serializable
public data class ThemePacksManifestData(
    public val version: String,
    public val packs: List<ThemePack>,
)

/**
 * 主题包信息。
 *
 * @param id 主题包 ID
 * @param name 主题包名称
 * @param zipUrl 下载链接
 * @param updatedAt 更新时间
 */
@JsExport
@JsName("ThemePack")
@Serializable
public data class ThemePack(
    public val id: String,
    public val name: String,
    @SerialName("zip_url") public val zipUrl: String,
    @SerialName("updated_at") public val updatedAt: String,
)
