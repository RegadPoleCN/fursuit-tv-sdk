package com.furrist.rp.furtv.sdk.model

import kotlinx.serialization.Serializable

/**
 * 通用 API 响应包装
 * 用于统一 API 响应格式
 * @param T 响应数据类型
 * @param success 请求是否成功
 * @param data 响应数据
 * @param requestId 请求 ID，用于日志排查
 */
@Serializable
public data class ApiResponse<T>(
    public val success: Boolean,
    public val data: T,
    public val requestId: String,
)

/**
 * 分页请求参数
 * 用于支持分页查询
 * @param cursor 分页游标，用于定位下一页
 * @param limit 每页返回数量限制
 */
@Serializable
public data class PaginationParams(
    public val cursor: String? = null,
    public val limit: Int? = null,
)

/**
 * 分页响应数据
 * 通用的分页响应包装
 * @param T 数据类型
 * @param items 数据项列表
 * @param nextCursor 下一页游标
 * @param hasMore 是否还有更多数据
 */
@Serializable
public data class PaginatedResponse<T>(
    public val items: List<T>,
    public val nextCursor: String? = null,
    public val hasMore: Boolean = false,
)

/**
 * 地理位置坐标
 * 表示一个地理位置的经纬度坐标
 * @param lat 纬度
 * @param lng 经度
 */
@Serializable
public data class GeoLocation(
    public val lat: Double,
    public val lng: Double,
)

/**
 * 图片资源
 * 表示一个图片资源的信息
 * @param url 图片 URL
 * @param width 图片宽度（像素）
 * @param height 图片高度（像素）
 */
@Serializable
public data class ImageResource(
    public val url: String,
    public val width: Int? = null,
    public val height: Int? = null,
)

/**
 * 时间范围
 * 表示一个时间范围
 * @param start 开始时间（ISO 8601 格式）
 * @param end 结束时间（ISO 8601 格式）
 */
@Serializable
public data class TimeRange(
    public val start: String,
    public val end: String,
)
