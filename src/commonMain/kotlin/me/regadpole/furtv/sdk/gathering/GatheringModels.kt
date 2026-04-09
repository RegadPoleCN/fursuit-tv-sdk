package me.regadpole.furtv.sdk.gathering

import kotlinx.serialization.Serializable

// ==================== 聚会列表与统计 ====================

/**
 * 聚会统计响应
 * 聚会年度统计接口的响应包装 */
@Serializable
public data class GatheringStatsResponse(
    public val success: Boolean,
    public val data: GatheringStatsData,
    public val requestId: String,
)

/**
 * 聚会统计数据
 * 包含年度聚会的统计信息
 * @param year 年份
 * @param total 聚会总数
 * @param upcoming 即将举行的聚会数
 * @param ongoing 正在进行中的聚会数
 * @param completed 已完成的聚会数 */
@Serializable
public data class GatheringStatsData(
    public val year: Int,
    public val total: Int,
    public val upcoming: Int,
    public val ongoing: Int,
    public val completed: Int,
)

/**
 * 聚会月历响应
 * 聚会月历接口的响应包装 */
@Serializable
public data class GatheringMonthlyResponse(
    public val success: Boolean,
    public val data: List<GatheringMonthlyItem>,
    public val requestId: String,
)

/**
 * 聚会月历项
 * 表示一个月份中的聚会项
 * @param id 聚会 ID
 * @param name 聚会名称
 * @param startDate 开始日期
 * @param endDate 结束日期
 * @param location 地点
 * @param status 状态 */
@Serializable
public data class GatheringMonthlyItem(
    public val id: String,
    public val name: String,
    public val startDate: String,
    public val endDate: String? = null,
    public val location: String? = null,
    public val status: String,
)

/**
 * 聚会月历距离响应
 * 带距离的聚会月历接口响应包装
 */
@Serializable
public data class GatheringMonthlyDistanceResponse(
    public val success: Boolean,
    public val data: List<GatheringWithDistance>,
    public val requestId: String,
)

/**
 * 带距离的聚会
 * 表示一个带距离信息的聚会
 * @param id 聚会 ID
 * @param name 聚会名称
 * @param startDate 开始日期
 * @param location 地点
 * @param distance 距离（公里）
 * @param status 状态 */
@Serializable
public data class GatheringWithDistance(
    public val id: String,
    public val name: String,
    public val startDate: String,
    public val location: String? = null,
    public val distance: Double? = null,
    public val status: String,
)

/**
 * 附近聚会响应
 * 附近聚会接口的响应包装 */
@Serializable
public data class GatheringNearbyResponse(
    public val success: Boolean,
    public val data: List<GatheringNearby>,
    public val requestId: String,
)

/**
 * 附近聚会
 * 表示一个附近的聚会
 * @param id 聚会 ID
 * @param name 聚会名称
 * @param location 地点
 * @param lat 纬度
 * @param lng 经度
 * @param distance 距离（公里）
 * @param startDate 开始日期 */
@Serializable
public data class GatheringNearby(
    public val id: String,
    public val name: String,
    public val location: String,
    public val lat: Double,
    public val lng: Double,
    public val distance: Double,
    public val startDate: String,
)

/**
 * 附近模式响应
 * 附近模式接口的响应包装 */
@Serializable
public data class GatheringNearbyModeResponse(
    public val success: Boolean,
    public val data: GatheringNearbyModeData,
    public val requestId: String,
)

/**
 * 附近模式数据
 * 包含附近模式的信息
 * @param mode 模式
 * @param intentGatheringIds 意图聚会 ID 列表
 */
@Serializable
public data class GatheringNearbyModeData(
    public val mode: String,
    public val intentGatheringIds: List<String>,
)

// ==================== 聚会详情与报名 ====================

/**
 * 聚会详情响应
 * 聚会详情接口的响应包装 */
@Serializable
public data class GatheringDetailResponse(
    public val success: Boolean,
    public val data: GatheringDetail,
    public val requestId: String,
)

/**
 * 聚会详情
 * 包含聚会的详细信息
 * @param id 聚会 ID
 * @param name 聚会名称
 * @param description 描述
 * @param startDate 开始日期
 * @param endDate 结束日期
 * @param location 地点
 * @param lat 纬度
 * @param lng 经度
 * @param agenda 议程列表
 * @param tags 标签列表
 * @param registrationStats 报名统计
 * @param status 状态
 * @param organizer 组织者信息 */
@Serializable
public data class GatheringDetail(
    public val id: String,
    public val name: String,
    public val description: String? = null,
    public val startDate: String,
    public val endDate: String? = null,
    public val location: String,
    public val lat: Double? = null,
    public val lng: Double? = null,
    public val agenda: List<AgendaItem>? = null,
    public val tags: List<String>? = null,
    public val registrationStats: RegistrationStats,
    public val status: String,
    public val organizer: OrganizerInfo,
)

/**
 * 议程项
 * 表示聚会的一个议程项
 * @param time 时间
 * @param title 标题
 * @param description 描述
 */
@Serializable
public data class AgendaItem(
    public val time: String,
    public val title: String,
    public val description: String? = null,
)

/**
 * 报名统计
 * 包含聚会报名的统计信息
 * @param total 总报名人数
 * @param approved 已批准人数
 * @param pending 待审核人数
 * @param checkedIn 已签到人数
 * @param capacity 容量限制
 */
@Serializable
public data class RegistrationStats(
    public val total: Int,
    public val approved: Int,
    public val pending: Int,
    public val checkedIn: Int,
    public val capacity: Int? = null,
)

/**
 * 组织者信息
 * 包含聚会组织者的信息
 * @param userId 组织者用户 ID
 * @param username 组织者用户名
 * @param displayName 组织者显示名称
 * @param avatarUrl 组织者头像 URL
 */
@Serializable
public data class OrganizerInfo(
    public val userId: String,
    public val username: String,
    public val displayName: String,
    public val avatarUrl: String? = null,
)

/**
 * 报名列表响应
 * 报名列表接口的响应包装 */
@Serializable
public data class GatheringRegistrationsResponse(
    public val success: Boolean,
    public val data: GatheringRegistrationsData,
    public val requestId: String,
)

/**
 * 报名列表数据
 * 包含聚会报名成员列表
 * @param gatheringId 聚会 ID
 * @param registrations 报名列表
 * @param totalCount 总人数 */
@Serializable
public data class GatheringRegistrationsData(
    public val gatheringId: String,
    public val registrations: List<Registration>,
    public val totalCount: Int,
)

/**
 * 报名信息
 * 表示一个报名者的信息
 * @param id 报名 ID
 * @param userId 用户 ID
 * @param username 用户名
 * @param displayName 显示名称
 * @param avatarUrl 头像 URL
 * @param status 报名状态
 * @param checkedIn 是否已签到
 * @param registeredAt 报名时间
 */
@Serializable
public data class Registration(
    public val id: String,
    public val userId: String,
    public val username: String,
    public val displayName: String,
    public val avatarUrl: String? = null,
    public val status: String,
    public val checkedIn: Boolean,
    public val registeredAt: String,
)
