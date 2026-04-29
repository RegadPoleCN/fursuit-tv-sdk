package com.furrist.rp.furtv.sdk.model

import kotlin.js.JsExport
import kotlin.js.JsName
import kotlinx.serialization.Serializable

/**
 * 通用 API 响应包装。
 *
 * @param T 响应数据类型
 */
@JsExport
@JsName("ApiResponse")
@Serializable
public data class ApiResponse<T>(
    public val success: Boolean,
    public val data: T,
    public val requestId: String,
)

/**
 * 分页请求参数。
 *
 * @param cursor 分页游标
 * @param limit 每页返回数量限制
 */
@JsExport
@JsName("PaginationParams")
@Serializable
public data class PaginationParams(
    public val cursor: String? = null,
    public val limit: Int? = null,
)

/**
 * 分页响应数据。
 *
 * @param T 数据类型
 */
@JsExport
@JsName("PaginatedResponse")
@Serializable
public data class PaginatedResponse<T>(
    public val items: List<T>,
    public val nextCursor: String? = null,
    public val hasMore: Boolean = false,
)

/**
 * 地理位置坐标。
 */
@JsExport
@JsName("GeoLocation")
@Serializable
public data class GeoLocation(
    public val lat: Double,
    public val lng: Double,
)

/**
 * 图片资源。
 */
@JsExport
@JsName("ImageResource")
@Serializable
public data class ImageResource(
    public val url: String,
    public val width: Int? = null,
    public val height: Int? = null,
)

/**
 * 时间范围（ISO 8601 格式）。
 */
@JsExport
@JsName("TimeRange")
@Serializable
public data class TimeRange(
    public val start: String,
    public val end: String,
)
