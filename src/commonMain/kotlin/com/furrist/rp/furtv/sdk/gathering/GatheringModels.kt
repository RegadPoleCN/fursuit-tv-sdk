package com.furrist.rp.furtv.sdk.gathering

import kotlin.js.JsExport
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// ==================== 聚会年度统计 ====================

/**
 * 聚会年度统计响应
 * 端点：GET /api/proxy/furtv/gatherings/stats/thisyear
 */
@JsExport
@Serializable
public data class GatheringYearStatsResponse(
    @SerialName("success")
    public val success: Boolean,
    @SerialName("data")
    public val data: GatheringYearStatsData,
    @SerialName("requestId")
    public val requestId: String,
)

/**
 * 聚会年度统计数据
 * @param year 年份
 * @param total 聚会总数
 * @param upcoming 即将举行的聚会数
 * @param ongoing 正在进行中的聚会数
 * @param completed 已完成的聚会数
 */
@JsExport
@Serializable
public data class GatheringYearStatsData(
    @SerialName("year")
    public val year: Int,
    @SerialName("total")
    public val total: Int,
    @SerialName("upcoming")
    public val upcoming: Int,
    @SerialName("ongoing")
    public val ongoing: Int,
    @SerialName("completed")
    public val completed: Int,
)

// ==================== 聚会月历 ====================

/**
 * 聚会月历响应
 * 端点：GET /api/proxy/furtv/gatherings/monthly
 */
@JsExport
@Serializable
public data class GatheringMonthlyResponse(
    @SerialName("success")
    public val success: Boolean,
    @SerialName("data")
    public val data: List<GatheringMonthlyItem>,
    @SerialName("requestId")
    public val requestId: String,
)

/**
 * 聚会月历项
 * @param id 聚会 ID
 * @param name 聚会名称
 * @param startDate 开始日期（ISO 8601 格式）
 * @param endDate 结束日期（ISO 8601 格式，可选）
 * @param location 地点
 * @param status 状态
 */
@JsExport
@Serializable
public data class GatheringMonthlyItem(
    @SerialName("id")
    public val id: String,
    @SerialName("name")
    public val name: String,
    @SerialName("startDate")
    public val startDate: String,
    @SerialName("endDate")
    public val endDate: String? = null,
    @SerialName("location")
    public val location: String? = null,
    @SerialName("status")
    public val status: String,
)

// ==================== 聚会月历距离 ====================

/**
 * 聚会月历距离响应
 * 端点：GET /api/proxy/furtv/gatherings/monthlydistance
 */
@JsExport
@Serializable
public data class GatheringMonthlyDistanceResponse(
    @SerialName("success")
    public val success: Boolean,
    @SerialName("data")
    public val data: List<GatheringMonthlyDistanceItem>,
    @SerialName("requestId")
    public val requestId: String,
)

/**
 * 聚会月历距离项
 * @param id 聚会 ID
 * @param name 聚会名称
 * @param startDate 开始日期（ISO 8601 格式）
 * @param endDate 结束日期（ISO 8601 格式，可选）
 * @param location 地点
 * @param status 状态
 * @param distance 距离（公里，可选）
 */
@JsExport
@Serializable
public data class GatheringMonthlyDistanceItem(
    @SerialName("id")
    public val id: String,
    @SerialName("name")
    public val name: String,
    @SerialName("startDate")
    public val startDate: String,
    @SerialName("endDate")
    public val endDate: String? = null,
    @SerialName("location")
    public val location: String? = null,
    @SerialName("status")
    public val status: String,
    @SerialName("distance")
    public val distance: Double? = null,
)

// ==================== 聚会附近 ====================

/**
 * 聚会附近响应
 * 端点：GET /api/proxy/furtv/gatherings/nearby
 */
@JsExport
@Serializable
public data class GatheringNearbyResponse(
    @SerialName("success")
    public val success: Boolean,
    @SerialName("data")
    public val data: List<GatheringNearbyItem>,
    @SerialName("requestId")
    public val requestId: String,
)

/**
 * 聚会附近项
 * @param id 聚会 ID
 * @param name 聚会名称
 * @param location 地点
 * @param lat 纬度
 * @param lng 经度
 * @param distance 距离（公里）
 * @param startDate 开始日期（ISO 8601 格式）
 * @param endDate 结束日期（ISO 8601 格式，可选）
 * @param status 状态
 */
@JsExport
@Serializable
public data class GatheringNearbyItem(
    @SerialName("id")
    public val id: String,
    @SerialName("name")
    public val name: String,
    @SerialName("location")
    public val location: String,
    @SerialName("lat")
    public val lat: Double,
    @SerialName("lng")
    public val lng: Double,
    @SerialName("distance")
    public val distance: Double,
    @SerialName("startDate")
    public val startDate: String,
    @SerialName("endDate")
    public val endDate: String? = null,
    @SerialName("status")
    public val status: String,
)

// ==================== 聚会附近模式 ====================

/**
 * 聚会附近模式响应
 * 端点：GET /api/proxy/furtv/gatherings/nearbymode
 */
@JsExport
@Serializable
public data class GatheringNearbyModeResponse(
    @SerialName("success")
    public val success: Boolean,
    @SerialName("data")
    public val data: GatheringNearbyModeData,
    @SerialName("requestId")
    public val requestId: String,
)

/**
 * 聚会附近模式数据
 * @param mode 模式
 * @param intentGatheringIds 意图聚会 ID 列表
 */
@JsExport
@Serializable
public data class GatheringNearbyModeData(
    @SerialName("mode")
    public val mode: String,
    @SerialName("intentGatheringIds")
    public val intentGatheringIds: List<String>,
)

// ==================== 聚会详情 ====================

/**
 * 聚会详情响应
 * 端点：GET /api/proxy/furtv/gatherings/detail
 */
@JsExport
@Serializable
public data class GatheringDetailResponse(
    @SerialName("success")
    public val success: Boolean,
    @SerialName("data")
    public val data: GatheringDetailData,
    @SerialName("requestId")
    public val requestId: String,
)

/**
 * 聚会详情数据
 * @param id 聚会 ID
 * @param name 聚会名称
 * @param description 描述
 * @param startDate 开始日期（ISO 8601 格式）
 * @param endDate 结束日期（ISO 8601 格式，可选）
 * @param location 地点
 * @param lat 纬度（可选）
 * @param lng 经度（可选）
 * @param agenda 议程列表
 * @param tags 标签列表
 * @param registrationStats 报名统计
 * @param status 状态
 * @param organizer 组织者信息
 */
@JsExport
@Serializable
public data class GatheringDetailData(
    @SerialName("id")
    public val id: String,
    @SerialName("name")
    public val name: String,
    @SerialName("description")
    public val description: String? = null,
    @SerialName("startDate")
    public val startDate: String,
    @SerialName("endDate")
    public val endDate: String? = null,
    @SerialName("location")
    public val location: String,
    @SerialName("lat")
    public val lat: Double? = null,
    @SerialName("lng")
    public val lng: Double? = null,
    @SerialName("agenda")
    public val agenda: List<AgendaItem>? = null,
    @SerialName("tags")
    public val tags: List<String>? = null,
    @SerialName("registrationStats")
    public val registrationStats: RegistrationStatsData,
    @SerialName("status")
    public val status: String,
    @SerialName("organizer")
    public val organizer: OrganizerData,
)

/**
 * 议程项
 * @param time 时间
 * @param title 标题
 * @param description 描述
 */
@JsExport
@Serializable
public data class AgendaItem(
    @SerialName("time")
    public val time: String,
    @SerialName("title")
    public val title: String,
    @SerialName("description")
    public val description: String? = null,
)

/**
 * 报名统计数据
 * @param total 总报名人数
 * @param approved 已批准人数
 * @param pending 待审核人数
 * @param checkedIn 已签到人数
 * @param capacity 容量限制（可选）
 */
@JsExport
@Serializable
public data class RegistrationStatsData(
    @SerialName("total")
    public val total: Int,
    @SerialName("approved")
    public val approved: Int,
    @SerialName("pending")
    public val pending: Int,
    @SerialName("checkedIn")
    public val checkedIn: Int,
    @SerialName("capacity")
    public val capacity: Int? = null,
)

/**
 * 组织者数据
 * @param userId 组织者用户 ID
 * @param username 组织者用户名
 * @param displayName 组织者显示名称
 * @param avatarUrl 组织者头像 URL（可选）
 */
@JsExport
@Serializable
public data class OrganizerData(
    @SerialName("userId")
    public val userId: String,
    @SerialName("username")
    public val username: String,
    @SerialName("displayName")
    public val displayName: String,
    @SerialName("avatarUrl")
    public val avatarUrl: String? = null,
)

// ==================== 聚会报名列表 ====================

/**
 * 聚会报名列表响应
 * 端点：GET /api/proxy/furtv/gatherings/registrations
 */
@JsExport
@Serializable
public data class GatheringRegistrationsResponse(
    @SerialName("success")
    public val success: Boolean,
    @SerialName("data")
    public val data: GatheringRegistrationsData,
    @SerialName("requestId")
    public val requestId: String,
)

/**
 * 聚会报名列表数据
 * @param gatheringId 聚会 ID
 * @param registrations 报名列表
 * @param totalCount 总人数
 */
@JsExport
@Serializable
public data class GatheringRegistrationsData(
    @SerialName("gatheringId")
    public val gatheringId: String,
    @SerialName("registrations")
    public val registrations: List<RegistrationData>,
    @SerialName("totalCount")
    public val totalCount: Int,
)

/**
 * 报名信息数据
 * @param id 报名 ID
 * @param userId 用户 ID
 * @param username 用户名
 * @param displayName 显示名称
 * @param avatarUrl 头像 URL（可选）
 * @param status 报名状态
 * @param checkedIn 是否已签到
 * @param registeredAt 报名时间（ISO 8601 格式）
 */
@JsExport
@Serializable
public data class RegistrationData(
    @SerialName("id")
    public val id: String,
    @SerialName("userId")
    public val userId: String,
    @SerialName("username")
    public val username: String,
    @SerialName("displayName")
    public val displayName: String,
    @SerialName("avatarUrl")
    public val avatarUrl: String? = null,
    @SerialName("status")
    public val status: String,
    @SerialName("checkedIn")
    public val checkedIn: Boolean,
    @SerialName("registeredAt")
    public val registeredAt: String,
)
