package com.furrist.rp.furtv.sdk.model

import kotlin.js.JsExport
import kotlinx.serialization.Serializable

/**
 * 搜索参数
 * 用于搜索接口的请求参数
 * @param query 搜索关键词
 * @param type 搜索类型（可选）
 * @param cursor 分页游标（可选）
 * @param limit 返回数量限制（可选）
 */
@JsExport
@Serializable
public data class SearchParams(
    /**
     * 搜索关键词
     */
    public val query: String,
    /**
     * 搜索类型
     */
    public val type: String? = null,
    /**
     * 分页游标
     */
    public val cursor: String? = null,
    /**
     * 返回数量限制
     */
    public val limit: Int? = null,
)

/**
 * 随机推荐参数
 * 用于随机推荐接口的请求参数
 * @param count 返回数量（可选）
 * @param personalized 是否个性化推荐（可选）
 */
@JsExport
@Serializable
public data class RandomFursuitParams(
    /**
     * 返回数量
     */
    public val count: Int? = null,
    /**
     * 是否个性化推荐
     */
    public val personalized: Boolean? = null,
)

/**
 * 聚会月历参数
 * 用于聚会月历接口的请求参数
 * @param year 年份
 * @param month 月份
 * @param lat 纬度（可选，用于计算距离）
 * @param lng 经度（可选，用于计算距离）
 */
@JsExport
@Serializable
public data class GatheringMonthlyParams(
    /**
     * 年份
     */
    public val year: Int,
    /**
     * 月份
     */
    public val month: Int,
    /**
     * 纬度（用于计算距离）
     */
    public val lat: Double? = null,
    /**
     * 经度（用于计算距离）
     */
    public val lng: Double? = null,
)

/**
 * 附近聚会参数
 * 用于附近聚会接口的请求参数
 * @param lat 纬度
 * @param lng 经度
 * @param radius 搜索半径（米，可选）
 */
@JsExport
@Serializable
public data class GatheringNearbyParams(
    /**
     * 纬度
     */
    public val lat: Double,
    /**
     * 经度
     */
    public val lng: Double,
    /**
     * 半径（米）
     */
    public val radius: Int? = null,
)

/**
 * 聚会报名列表参数
 * 用于聚会报名列表接口的请求参数
 * @param gatheringId 聚会 ID
 * @param status 报名状态筛选（可选）
 * @param cursor 分页游标（可选）
 * @param limit 返回数量限制（可选）
 */
@JsExport
@Serializable
public data class GatheringRegistrationsParams(
    /**
     * 聚会 ID
     */
    public val gatheringId: String,
    /**
     * 报名状态筛选
     */
    public val status: String? = null,
    /**
     * 分页游标
     */
    public val cursor: String? = null,
    /**
     * 返回数量限制
     */
    public val limit: Int? = null,
)

/**
 * 学校搜索参数
 * 用于学校搜索接口的请求参数
 * @param query 搜索关键词
 * @param cursor 分页游标（可选）
 * @param limit 返回数量限制（可选）
 */
@JsExport
@Serializable
public data class SchoolSearchParams(
    /**
     * 搜索关键词
     */
    public val query: String,
    /**
     * 分页游标
     */
    public val cursor: String? = null,
    /**
     * 返回数量限制
     */
    public val limit: Int? = null,
)
