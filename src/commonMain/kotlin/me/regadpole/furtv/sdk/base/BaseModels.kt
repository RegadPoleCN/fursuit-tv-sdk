package me.regadpole.furtv.sdk.base

import kotlinx.serialization.Serializable

/**
 * HelloWorld 响应
 * HelloWorld 接口的响应，直接包含所有字段（无 data 包装）
 * 根据文档，响应格式为：
 * {
 *   "success": true,
 *   "message": "helloworld",
 *   "verify": "request_normal",
 *   "appId": "vap_xxx",
 *   "requestId": "uuid"
 * }
 */
@Serializable
public data class HelloWorldResponse(
    public val success: Boolean,
    public val message: String,
    public val verify: String,
    public val appId: String,
    public val requestId: String
)

/**
 * 健康检查响应
 * 健康检查接口的响应包装
 */
@Serializable
public data class HealthResponse(
    public val success: Boolean,
    public val data: HealthData,
    public val requestId: String
)

/**
 * 健康数据
 * 包含服务健康状态信息
 * @param status 服务状态
 * @param version 服务版本
 * @param uptime 运行时间（秒） */
@Serializable
public data class HealthData(
    public val status: String,
    public val version: String,
    public val uptime: Long
)

/**
 * Android 版本信息响应
 * Android 版本信息接口的响应包装 */
@Serializable
public data class AndroidVersionResponse(
    public val success: Boolean,
    public val data: AndroidVersionData,
    public val requestId: String
)

/**
 * Android 版本数据
 * 包含 Android 应用的版本信息
 * @param version 版本号
 * @param versionCode 版本代码
 * @param downloadUrl 下载链接
 * @param updateTime 更新时间
 * @param minSupportedVersion 最低支持版本
 * @param changelog 更新日志列表
 */
@Serializable
public data class AndroidVersionData(
    public val version: String,
    public val versionCode: Int,
    public val downloadUrl: String,
    public val updateTime: String,
    public val minSupportedVersion: String,
    public val changelog: List<String>
)

/**
 * Android 版本检查请求
 * 用于版本检查接口的请求体
 * @param currentVersion 当前版本字符串
 * @param currentVersionCode 当前版本代码（可选）
 */
@Serializable
public data class AndroidVersionCheckRequest(
    public val currentVersion: String,
    public val currentVersionCode: Int? = null
)

/**
 * Android 版本检查响应
 * 版本检查接口的响应包装
 */
@Serializable
public data class AndroidVersionCheckResponse(
    public val success: Boolean,
    public val data: AndroidVersionCheckData,
    public val requestId: String
)

/**
 * Android 版本检查数据
 * 包含版本检查的结果
 * @param needUpdate 是否需要更新
 * @param forceUpdate 是否强制更新
 * @param currentVersion 当前版本信息
 * @param latestVersion 最新版本信息 */
@Serializable
public data class AndroidVersionCheckData(
    public val needUpdate: Boolean,
    public val forceUpdate: Boolean,
    public val currentVersion: VersionInfo,
    public val latestVersion: AndroidVersionData
)

/**
 * 版本信息
 * 表示一个版本的基本信息
 * @param version 版本字符串
 * @param versionCode 版本代码
 */
@Serializable
public data class VersionInfo(
    public val version: String,
    public val versionCode: Int
)

/**
 * 主题包清单响应
 * 主题包清单接口的响应包装
 */
@Serializable
public data class ThemePacksManifestResponse(
    public val success: Boolean,
    public val data: ThemePacksManifestData,
    public val requestId: String
)

/**
 * 主题包清单数据
 * 包含主题包清单信息
 * @param version 清单版本
 * @param packs 主题包列表 */
@Serializable
public data class ThemePacksManifestData(
    public val version: String,
    public val packs: List<ThemePack>
)

/**
 * 主题包
 * 表示一个主题包的信息
 * @param id 主题包 ID
 * @param name 主题包名称
 * @param zipUrl 下载链接
 * @param updatedAt 更新时间
 */
@Serializable
public data class ThemePack(
    public val id: String,
    public val name: String,
    public val zipUrl: String,
    public val updatedAt: String
)
