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

import kotlin.js.JsExport
import kotlin.js.JsName
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** HelloWorld 接口响应，无 data 包装。 */
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

/** 健康检查接口响应。 */
@JsExport
@JsName("HealthResponse")
@Serializable
public data class HealthResponse(
    public val success: Boolean,
    public val message: String,
    public val timestamp: String,
    public val requestId: String,
)

/** Android 版本信息响应包装。 */
@JsExport
@JsName("AndroidVersionResponse")
@Serializable
public data class AndroidVersionResponse(
    public val success: Boolean,
    public val data: AndroidVersionData,
    public val requestId: String,
)

/** Android 应用版本数据。 */
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

/** Android 版本检查请求体。 */
@JsExport
@JsName("AndroidVersionCheckRequest")
@Serializable
public data class AndroidVersionCheckRequest(
    public val currentVersion: String,
    public val currentVersionCode: Int,
)

/** Android 版本检查响应包装。 */
@JsExport
@JsName("AndroidVersionCheckResponse")
@Serializable
public data class AndroidVersionCheckResponse(
    public val success: Boolean,
    public val data: AndroidVersionCheckData,
    public val requestId: String,
)

/** Android 版本检查结果数据。 */
@JsExport
@JsName("AndroidVersionCheckData")
@Serializable
public data class AndroidVersionCheckData(
    public val needUpdate: Boolean,
    public val forceUpdate: Boolean,
    public val currentVersion: VersionInfo,
    public val latestVersion: AndroidVersionData,
)

/** 版本基本信息。 */
@JsExport
@JsName("VersionInfo")
@Serializable
public data class VersionInfo(
    public val version: String,
    public val versionCode: Int,
)

/** 主题包清单响应包装。 */
@JsExport
@JsName("ThemePacksManifestResponse")
@Serializable
public data class ThemePacksManifestResponse(
    public val success: Boolean,
    public val data: ThemePacksManifestData,
    public val requestId: String,
)

/** 主题包清单数据。 */
@JsExport
@JsName("ThemePacksManifestData")
@Serializable
public data class ThemePacksManifestData(
    public val updatedAt: String,
    public val themes: List<ThemePack>,
)

/** 主题包信息。 */
@JsExport
@JsName("ThemePack")
@Serializable
public data class ThemePack(
    public val id: String,
    public val zipUrl: String,
    public val metadata: ThemePackMetadata? = null,
)

/** 主题包元信息。 */
@JsExport
@JsName("ThemePackMetadata")
@Serializable
public data class ThemePackMetadata(
    public val name: String? = null,
    public val author: ThemePackAuthor? = null,
    public val intro: String? = null,
    public val version: String? = null,
    public val themeCss: String? = null,
    public val homeBackground: ThemePackHomeBackground? = null,
    public val preview: ThemePackPreview? = null,
)

/** 主题包作者。 */
@JsExport
@JsName("ThemePackAuthor")
@Serializable
public data class ThemePackAuthor(
    public val username: String? = null,
)

/** 主题包首页背景设置。 */
@JsExport
@JsName("ThemePackHomeBackground")
@Serializable
public data class ThemePackHomeBackground(
    public val opacity: Double? = null,
    public val blur: Int? = null,
)

/** 主题包预览色板。 */
@JsExport
@JsName("ThemePackPreview")
@Serializable
public data class ThemePackPreview(
    public val surface0: String? = null,
    public val surface1: String? = null,
    public val accent: String? = null,
    public val accentRgb: String? = null,
    public val contrastRgb: String? = null,
)

/**
 * 平台请求日志搜索响应（官方《RequestID查询.md》）。
 */
@JsExport
@JsName("RequestLogsSearchResponse")
@Serializable
public data class RequestLogsSearchResponse(
    public val success: Boolean = true,
    public val logs: List<RequestLogItem> = emptyList(),
    public val requestId: String? = null,
)

/**
 * 平台请求日志详情响应（官方《RequestID查询.md》）。
 */
@JsExport
@JsName("RequestLogDetailResponse")
@Serializable
public data class RequestLogDetailResponse(
    public val success: Boolean = true,
    public val log: RequestLogItem? = null,
    public val requestId: String? = null,
)

/**
 * 平台请求日志项。
 */
@JsExport
@JsName("RequestLogItem")
@Serializable
public data class RequestLogItem(
    @SerialName("request_id") public val requestId: String? = null,
    @SerialName("method") public val method: String? = null,
    @SerialName("path") public val path: String? = null,
    @SerialName("status_code") public val statusCode: Int? = null,
    @SerialName("ip") public val ip: String? = null,
    @SerialName("user_agent") public val userAgent: String? = null,
    @SerialName("created_at") public val createdAt: String? = null,
    @SerialName("response_time_ms") public val responseTimeMs: Long? = null,
)
