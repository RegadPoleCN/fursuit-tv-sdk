package com.furrist.rp.furtv.sdk.model

import kotlin.js.JsExport
import kotlinx.serialization.Serializable

/**
 * 搜索参数。
 *
 * @param query 搜索关键词
 * @param type 搜索类型（可选）
 */
@JsExport
@Serializable
public data class SearchParams(
    public val query: String,
    public val type: String? = null,
    public val cursor: String? = null,
    public val limit: Int? = null,
    public val page: Int? = null,
)

/**
 * 随机推荐参数。
 *
 * @param count 返回数量（可选）
 * @param personalized 是否个性化推荐（可选）
 */
@JsExport
@Serializable
public data class RandomFursuitParams(
    public val count: Int? = null,
    public val personalized: Boolean? = null,
)

/**
 * 聚会月历参数。
 *
 * @param year 年份
 * @param month 月份
 * @param lat 纬度（可选，用于计算距离）
 * @param lng 经度（可选，用于计算距离）
 */
@JsExport
@Serializable
public data class GatheringMonthlyParams(
    public val year: Int,
    public val month: Int,
    public val lat: Double? = null,
    public val lng: Double? = null,
)

/**
 * 附近聚会参数。
 *
 * @param lat 纬度
 * @param lng 经度
 * @param radius 搜索半径（米，可选）
 */
@JsExport
@Serializable
public data class GatheringNearbyParams(
    public val lat: Double? = null,
    public val lng: Double? = null,
    public val radius: Int? = null,
)

/**
 * 聚会报名列表参数。
 *
 * @param gatheringId 聚会 ID
 * @param status 报名状态筛选（可选）
 */
@JsExport
@Serializable
public data class GatheringRegistrationsParams(
    public val gatheringId: String,
    public val status: String? = null,
    public val cursor: String? = null,
    public val limit: Int? = null,
)

/**
 * 学校搜索参数。
 *
 * @param query 搜索关键词
 */
@JsExport
@Serializable
public data class SchoolSearchParams(
    public val query: String,
    public val cursor: String? = null,
    public val limit: Int? = null,
)
