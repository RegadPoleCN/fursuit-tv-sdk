package com.furrist.rp.furtv.sdk.gathering

import kotlin.js.JsExport
import kotlin.js.JsName
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// ==================== 聚会年度统计 ====================

/**
 * 聚会年度统计响应。
 *
 * @property success 请求是否成功
 * @property data 年度统计数据
 * @property requestId 请求 ID
 */
@JsExport
@JsName("GatheringYearStatsResponse")
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
 * 聚会年度统计数据。
 *
 * @property total 聚会总数
 */
@JsExport
@JsName("GatheringYearStatsData")
@Serializable
public data class GatheringYearStatsData(
    @SerialName("total")
    public val total: Int,
)

// ==================== 聚会月历 ====================

/**
 * 聚会月历响应。
 *
 * @property success 请求是否成功
 * @property data 月历聚会列表
 * @property requestId 请求 ID
 */
@JsExport
@JsName("GatheringMonthlyResponse")
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
 * 聚会月历项。
 *
 * @property id 聚会 ID
 * @property title 聚会标题
 * @property description 描述
 * @property type 聚会类型
 * @property typeClass 聚会类型分类
 * @property contentSource 内容来源
 * @property day 日期
 * @property weekday 星期
 * @property time 开始时间
 * @property endTime 结束时间
 * @property location 地点
 * @property locationPublic 公开地点
 * @property participants 参与人数描述
 * @property logo Logo URL
 * @property status 状态
 * @property badges 徽章列表
 * @property isFurtvCoopDriven 是否为 Fursuit.TV 合作驱动
 * @property sourceCount 数据来源数量
 * @property initialSource 初始来源
 * @property dataSources 数据来源列表
 * @property organizer 组织者名称
 * @property organizerAvatar 组织者头像 URL
 * @property feeType 费用类型
 * @property feeAmount 费用金额
 * @property registrationStatus 报名状态
 * @property requiresApproval 是否需要审核
 */
@JsExport
@JsName("GatheringMonthlyItem")
@Serializable
public data class GatheringMonthlyItem(
    @SerialName("id")
    public val id: Int,
    @SerialName("title")
    public val title: String,
    @SerialName("description")
    public val description: String? = null,
    @SerialName("type")
    public val type: String? = null,
    @SerialName("typeClass")
    public val typeClass: String? = null,
    @SerialName("content_source")
    public val contentSource: String? = null,
    @SerialName("day")
    public val day: String? = null,
    @SerialName("weekday")
    public val weekday: String? = null,
    @SerialName("time")
    public val time: String? = null,
    @SerialName("endTime")
    public val endTime: String? = null,
    @SerialName("location")
    public val location: String? = null,
    @SerialName("locationPublic")
    public val locationPublic: String? = null,
    @SerialName("participants")
    public val participants: String? = null,
    @SerialName("logo")
    public val logo: String? = null,
    @SerialName("status")
    public val status: String? = null,
    @SerialName("badges")
    public val badges: List<GatheringBadge>? = null,
    @SerialName("is_furtv_coop_driven")
    public val isFurtvCoopDriven: Boolean? = null,
    @SerialName("sourceCount")
    public val sourceCount: Int? = null,
    @SerialName("initialSource")
    public val initialSource: String? = null,
    @SerialName("dataSources")
    public val dataSources: List<DataSource>? = null,
    @SerialName("organizer")
    public val organizer: String? = null,
    @SerialName("organizerAvatar")
    public val organizerAvatar: String? = null,
    @SerialName("feeType")
    public val feeType: String? = null,
    @SerialName("feeAmount")
    public val feeAmount: String? = null,
    @SerialName("registrationStatus")
    public val registrationStatus: String? = null,
    @SerialName("requiresApproval")
    public val requiresApproval: Boolean? = null,
)

/**
 * 聚会徽章。
 *
 * @property code 徽章代码
 * @property title 徽章标题
 */
@JsExport
@JsName("GatheringBadge")
@Serializable
public data class GatheringBadge(
    @SerialName("code")
    public val code: String? = null,
    @SerialName("title")
    public val title: String? = null,
)

/**
 * 数据来源。
 *
 * @property sourceCode 来源代码
 * @property sourceUrl 来源 URL
 * @property name 来源名称
 * @property logoUrl 来源 Logo URL
 */
@JsExport
@JsName("DataSource")
@Serializable
public data class DataSource(
    @SerialName("source_code")
    public val sourceCode: String? = null,
    @SerialName("source_url")
    public val sourceUrl: String? = null,
    @SerialName("name")
    public val name: String? = null,
    @SerialName("logo_url")
    public val logoUrl: String? = null,
)

// ==================== 聚会月历距离 ====================

/**
 * 聚会月历距离响应。
 *
 * @property success 请求是否成功
 * @property data 带距离的月历聚会列表
 * @property requestId 请求 ID
 */
@JsExport
@JsName("GatheringMonthlyDistanceResponse")
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
 * 聚会月历距离项。
 *
 * @property id 聚会 ID
 * @property distanceMeters 距离（米）
 */
@JsExport
@JsName("GatheringMonthlyDistanceItem")
@Serializable
public data class GatheringMonthlyDistanceItem(
    @SerialName("id")
    public val id: Int,
    @SerialName("distance_meters")
    public val distanceMeters: Double? = null,
)

// ==================== 聚会附近 ====================

/**
 * 聚会附近响应。
 *
 * @property success 请求是否成功
 * @property data 附近聚会列表
 * @property requestId 请求 ID
 */
@JsExport
@JsName("GatheringNearbyResponse")
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
 * 聚会附近项。
 *
 * @property id 聚会 ID
 * @property title 聚会标题
 * @property eventDate 活动日期
 * @property endDate 结束日期
 * @property address 地址
 * @property city 城市
 * @property lat 纬度
 * @property lng 经度
 * @property badges 徽章列表
 * @property isFurtvCoopDriven 是否为 Fursuit.TV 合作驱动
 */
@JsExport
@JsName("GatheringNearbyItem")
@Serializable
public data class GatheringNearbyItem(
    @SerialName("id")
    public val id: Int,
    @SerialName("title")
    public val title: String,
    @SerialName("event_date")
    public val eventDate: String? = null,
    @SerialName("end_date")
    public val endDate: String? = null,
    @SerialName("address")
    public val address: String? = null,
    @SerialName("city")
    public val city: String? = null,
    @SerialName("lat")
    public val lat: Double? = null,
    @SerialName("lng")
    public val lng: Double? = null,
    @SerialName("badges")
    public val badges: List<GatheringBadge>? = null,
    @SerialName("is_furtv_coop_driven")
    public val isFurtvCoopDriven: Boolean? = null,
)

// ==================== 聚会附近模式 ====================

/**
 * 聚会附近模式响应。
 *
 * @property success 请求是否成功
 * @property data 附近模式数据
 * @property requestId 请求 ID
 */
@JsExport
@JsName("GatheringNearbyModeResponse")
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
 * 聚会附近模式数据。
 *
 * @property gatherings 附近聚会列表
 * @property intentGatheringIds 意图聚会 ID 列表
 */
@JsExport
@JsName("GatheringNearbyModeData")
@Serializable
public data class GatheringNearbyModeData(
    @SerialName("gatherings")
    public val gatherings: List<GatheringNearbyItem>,
    @SerialName("intent_gathering_ids")
    public val intentGatheringIds: List<Int>,
)

// ==================== 聚会详情 ====================

/**
 * 聚会详情响应。
 *
 * @property success 请求是否成功
 * @property data 聚会详情数据
 * @property requestId 请求 ID
 */
@JsExport
@JsName("GatheringDetailResponse")
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
 * 聚会详情数据。
 *
 * @property id 聚会 ID
 * @property title 聚会标题
 * @property description 描述
 * @property eventDate 活动日期
 * @property endDate 结束日期
 * @property eventTime 活动时间
 * @property endTime 结束时间
 * @property type 聚会类型
 * @property typeClass 聚会类型分类
 * @property typeDisplay 聚会类型显示名称
 * @property status 状态
 * @property locationPublic 公开地点
 * @property locationCity 所在城市
 * @property locationLat 纬度
 * @property locationLng 经度
 * @property logoUrl Logo URL
 * @property bannerUrl Banner URL
 * @property organizerId 组织者用户 ID
 * @property organizerUsername 组织者用户名
 * @property organizerNickname 组织者昵称
 * @property organizerAvatar 组织者头像 URL
 * @property coOrganizers 协办者列表
 * @property agenda 议程列表
 * @property tags 标签列表
 * @property sourceCount 数据来源数量
 * @property dataSources 数据来源列表
 * @property badges 徽章列表
 * @property isFurtvCoopDriven 是否为 Fursuit.TV 合作驱动
 * @property interestedCount 感兴趣人数
 * @property isInterested 当前用户是否感兴趣
 * @property goingFriendsCount 参与的好友数
 * @property registrationStats 报名统计
 * @property viewCount 浏览次数
 */
@JsExport
@JsName("GatheringDetailData")
@Serializable
public data class GatheringDetailData(
    @SerialName("id")
    public val id: Int,
    @SerialName("title")
    public val title: String,
    @SerialName("description")
    public val description: String? = null,
    @SerialName("event_date")
    public val eventDate: String? = null,
    @SerialName("end_date")
    public val endDate: String? = null,
    @SerialName("event_time")
    public val eventTime: String? = null,
    @SerialName("end_time")
    public val endTime: String? = null,
    @SerialName("type")
    public val type: String? = null,
    @SerialName("type_class")
    public val typeClass: String? = null,
    @SerialName("type_display")
    public val typeDisplay: String? = null,
    @SerialName("status")
    public val status: String? = null,
    @SerialName("location_public")
    public val locationPublic: String? = null,
    @SerialName("location_city")
    public val locationCity: String? = null,
    @SerialName("location_lat")
    public val locationLat: Double? = null,
    @SerialName("location_lng")
    public val locationLng: Double? = null,
    @SerialName("logo_url")
    public val logoUrl: String? = null,
    @SerialName("banner_url")
    public val bannerUrl: String? = null,
    @SerialName("organizer_id")
    public val organizerId: Int? = null,
    @SerialName("organizer_username")
    public val organizerUsername: String? = null,
    @SerialName("organizer_nickname")
    public val organizerNickname: String? = null,
    @SerialName("organizer_avatar")
    public val organizerAvatar: String? = null,
    @SerialName("co_organizers")
    public val coOrganizers: List<CoOrganizer>? = null,
    @SerialName("agenda")
    public val agenda: List<AgendaItem>? = null,
    @SerialName("tags")
    public val tags: List<String>? = null,
    @SerialName("source_count")
    public val sourceCount: Int? = null,
    @SerialName("data_sources")
    public val dataSources: List<DataSource>? = null,
    @SerialName("badges")
    public val badges: List<GatheringBadge>? = null,
    @SerialName("is_furtv_coop_driven")
    public val isFurtvCoopDriven: Boolean? = null,
    @SerialName("interested_count")
    public val interestedCount: Int? = null,
    @SerialName("is_interested")
    public val isInterested: Boolean? = null,
    @SerialName("going_friends_count")
    public val goingFriendsCount: Int? = null,
    @SerialName("registration_stats")
    public val registrationStats: GatheringRegistrationStats? = null,
    @SerialName("view_count")
    public val viewCount: Int? = null,
)

/**
 * 议程项。
 *
 * @property time 时间
 * @property title 标题
 * @property description 描述
 */
@JsExport
@JsName("AgendaItem")
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
 * 协办者。
 *
 * @property userId 用户 ID
 * @property username 用户名
 * @property nickname 昵称
 * @property avatar 头像 URL
 */
@JsExport
@JsName("CoOrganizer")
@Serializable
public data class CoOrganizer(
    @SerialName("user_id")
    public val userId: Int? = null,
    @SerialName("username")
    public val username: String? = null,
    @SerialName("nickname")
    public val nickname: String? = null,
    @SerialName("avatar")
    public val avatar: String? = null,
)

/**
 * 聚会报名统计。
 *
 * @property totalRegistrations 总报名人数
 * @property approvedCount 已批准人数
 * @property pendingCount 待审核人数
 */
@JsExport
@JsName("GatheringRegistrationStats")
@Serializable
public data class GatheringRegistrationStats(
    @SerialName("total_registrations")
    public val totalRegistrations: Int? = null,
    @SerialName("approved_count")
    public val approvedCount: Int? = null,
    @SerialName("pending_count")
    public val pendingCount: Int? = null,
)

// ==================== 聚会报名列表 ====================

/**
 * 聚会报名列表响应。
 *
 * @property success 请求是否成功
 * @property data 报名列表数据
 * @property requestId 请求 ID
 */
@JsExport
@JsName("GatheringRegistrationsResponse")
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
 * 聚会报名列表数据。
 *
 * @property registrations 分组报名列表（二维数组）
 */
@JsExport
@JsName("GatheringRegistrationsData")
@Serializable
public data class GatheringRegistrationsData(
    @SerialName("registrations")
    public val registrations: List<List<RegistrationItem>>,
)

/**
 * 报名项。
 *
 * @property id 报名 ID
 * @property status 报名状态
 * @property registrationTime 报名时间
 * @property checkedIn 签到状态
 * @property userId 用户 ID
 * @property username 用户名
 * @property nickname 昵称
 * @property avatarUrl 头像 URL
 * @property fursuitSpecies 兽装物种
 */
@JsExport
@JsName("RegistrationItem")
@Serializable
public data class RegistrationItem(
    @SerialName("id")
    public val id: Int,
    @SerialName("status")
    public val status: String? = null,
    @SerialName("registration_time")
    public val registrationTime: String? = null,
    @SerialName("checked_in")
    public val checkedIn: Int? = null,
    @SerialName("user_id")
    public val userId: Int,
    @SerialName("username")
    public val username: String,
    @SerialName("nickname")
    public val nickname: String? = null,
    @SerialName("avatar_url")
    public val avatarUrl: String? = null,
    @SerialName("fursuit_species")
    public val fursuitSpecies: String? = null,
)
