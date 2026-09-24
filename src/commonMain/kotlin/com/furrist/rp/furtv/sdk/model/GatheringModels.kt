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
import kotlinx.serialization.json.JsonObject

/** 聚会年度统计响应。 */
@JsExport
@JsName("GatheringYearStatsResponse")
@Serializable
public data class GatheringYearStatsResponse(
    @SerialName("success") public val success: Boolean,
    @SerialName("total") public val total: Int,
    @SerialName("requestId") public val requestId: String,
)

/** 聚会月历响应。 */
@JsExport
@JsName("GatheringMonthlyResponse")
@Serializable
public data class GatheringMonthlyResponse(
    @SerialName("success") public val success: Boolean,
    @SerialName("data") public val data: GatheringMonthlyData,
    @SerialName("requestId") public val requestId: String,
)

/** 聚会月历数据。 */
@JsExport
@JsName("GatheringMonthlyData")
@Serializable
public data class GatheringMonthlyData(
    @SerialName("year") public val year: Int,
    @SerialName("month") public val month: Int,
    @SerialName("gatherings") public val gatherings: List<GatheringMonthlyItem>,
    @SerialName("total") public val total: Int,
)

/** 聚会月历项。 */
@JsExport
@JsName("GatheringMonthlyItem")
@Serializable
public data class GatheringMonthlyItem(
    @SerialName("id") public val id: Int,
    @SerialName("title") public val title: String,
    @SerialName("description") public val description: String? = null,
    @SerialName("type") public val type: String? = null,
    @SerialName("typeClass") public val typeClass: String? = null,
    @SerialName("content_source") public val contentSource: String? = null,
    @SerialName("day") public val day: String? = null,
    @SerialName("weekday") public val weekday: String? = null,
    @SerialName("time") public val time: String? = null,
    @SerialName("endTime") public val endTime: String? = null,
    @SerialName("location") public val location: String? = null,
    @SerialName("locationPublic") public val locationPublic: String? = null,
    @SerialName("participants") public val participants: String? = null,
    @SerialName("logo") public val logo: String? = null,
    @SerialName("status") public val status: String? = null,
    @SerialName("badges") public val badges: List<GatheringBadge>? = null,
    @SerialName("is_furtv_coop_driven")
    @Serializable(with = BooleanAsIntSerializer::class)
    public val isFurtvCoopDriven: Boolean? = null,
    @SerialName("sourceCount") public val sourceCount: Int? = null,
    @SerialName("initialSource") public val initialSource: String? = null,
    @SerialName("dataSources") public val dataSources: List<DataSource>? = null,
    @SerialName("organizer") public val organizer: String? = null,
    @SerialName("organizerAvatar") public val organizerAvatar: String? = null,
    @SerialName("feeType") public val feeType: String? = null,
    @SerialName("feeAmount") public val feeAmount: String? = null,
    @SerialName("registrationStatus") public val registrationStatus: String? = null,
    @SerialName("requiresApproval") @Serializable(with = BooleanAsIntSerializer::class)
    public val requiresApproval: Boolean? = null,
)

/** 聚会徽章。 */
@JsExport
@JsName("GatheringBadge")
@Serializable
public data class GatheringBadge(
    @SerialName("code") public val code: String? = null,
    @SerialName("title") public val title: String? = null,
)

/** 数据来源。 */
@JsExport
@JsName("DataSource")
@Serializable
public data class DataSource(
    @SerialName("source_code") public val sourceCode: String? = null,
    @SerialName("source_url") public val sourceUrl: String? = null,
    @SerialName("name") public val name: String? = null,
    @SerialName("logo_url") public val logoUrl: String? = null,
)

/** 聚会月历距离响应。 */
@JsExport
@JsName("GatheringMonthlyDistanceResponse")
@Serializable
public data class GatheringMonthlyDistanceResponse(
    @SerialName("success") public val success: Boolean,
    @SerialName("data") public val data: GatheringMonthlyDistanceData,
    @SerialName("requestId") public val requestId: String,
)

/** 聚会月历距离数据。 */
@JsExport
@JsName("GatheringMonthlyDistanceData")
@Serializable
public data class GatheringMonthlyDistanceData(
    @SerialName("year") public val year: Int,
    @SerialName("month") public val month: Int,
    @SerialName("distances") public val distances: List<GatheringMonthlyDistanceItem>,
)

/** 聚会月历距离项。 */
@JsExport
@JsName("GatheringMonthlyDistanceItem")
@Serializable
public data class GatheringMonthlyDistanceItem(
    @SerialName("id") public val id: Int,
    @SerialName("distance_meters") public val distanceMeters: Double? = null,
)

/** 聚会附近响应。 */
@JsExport
@JsName("GatheringNearbyResponse")
@Serializable
public data class GatheringNearbyResponse(
    @SerialName("success") public val success: Boolean,
    @SerialName("data") public val data: List<GatheringNearbyItem>,
    @SerialName("requestId") public val requestId: String,
)

/** 聚会附近项。 */
@JsExport
@JsName("GatheringNearbyItem")
@Serializable
public data class GatheringNearbyItem(
    @SerialName("id") public val id: Int,
    @SerialName("title") public val title: String,
    @SerialName("event_date") public val eventDate: String? = null,
    @SerialName("end_date") public val endDate: String? = null,
    @SerialName("address") public val address: String? = null,
    @SerialName("city") public val city: String? = null,
    @SerialName("lat") public val lat: Double? = null,
    @SerialName("lng") public val lng: Double? = null,
    @SerialName("badges") public val badges: List<GatheringBadge>? = null,
    @SerialName("is_furtv_coop_driven")
    @Serializable(with = BooleanAsIntSerializer::class)
    public val isFurtvCoopDriven: Boolean? = null,
    @SerialName("event_time") public val eventTime: String? = null,
    @SerialName("end_clock_time") public val endClockTime: String? = null,
    @SerialName("start_time") public val startTime: String? = null,
    @SerialName("end_time") public val endTime: String? = null,
    @SerialName("time_zone") public val timeZone: String? = null,
)

/** 聚会附近模式响应。 */
@JsExport
@JsName("GatheringNearbyModeResponse")
@Serializable
public data class GatheringNearbyModeResponse(
    @SerialName("success") public val success: Boolean,
    @SerialName("data") public val data: GatheringNearbyModeData,
    @SerialName("requestId") public val requestId: String,
)

/** 聚会附近模式数据。 */
@JsExport
@JsName("GatheringNearbyModeData")
@Serializable
public data class GatheringNearbyModeData(
    @SerialName("gatherings") public val gatherings: List<GatheringNearbyModeItem>,
    @SerialName("intent_gathering_ids") public val intentGatheringIds: List<Int>,
)

/** 聚会附近模式项。 */
@JsExport
@JsName("GatheringNearbyModeItem")
@Serializable
public data class GatheringNearbyModeItem(
    @SerialName("id") public val id: Int,
    @SerialName("title") public val title: String,
    @SerialName("event_date") public val eventDate: String? = null,
    @SerialName("end_date") public val endDate: String? = null,
    @SerialName("event_time") public val eventTime: String? = null,
    @SerialName("end_clock_time") public val endClockTime: String? = null,
    @SerialName("start_time") public val startTime: String? = null,
    @SerialName("end_time") public val endTime: String? = null,
    @SerialName("time_zone") public val timeZone: String? = null,
    @SerialName("avatar_url") public val avatarUrl: String? = null,
    @SerialName("current_participants") public val currentParticipants: Int? = null,
    @SerialName("max_participants") public val maxParticipants: Int? = null,
    @SerialName("address") public val address: String? = null,
    @SerialName("city") public val city: String? = null,
    @SerialName("lat") public val lat: Double? = null,
    @SerialName("lng") public val lng: Double? = null,
    @SerialName("badges") public val badges: List<GatheringBadge>? = null,
    @SerialName("is_furtv_coop_driven")
    @Serializable(with = BooleanAsIntSerializer::class)
    public val isFurtvCoopDriven: Boolean? = null,
)

/** 聚会详情响应。 */
@JsExport
@JsName("GatheringDetailResponse")
@Serializable
public data class GatheringDetailResponse(
    @SerialName("success") public val success: Boolean,
    @SerialName("gathering") public val gathering: GatheringDetailData,
    @SerialName("requestId") public val requestId: String,
)

/** 聚会详情数据。 */
@JsExport
@JsName("GatheringDetailData")
@Serializable
public data class GatheringDetailData(
    @SerialName("id") public val id: Int,
    @SerialName("title") public val title: String,
    @SerialName("description") public val description: String? = null,
    @SerialName("event_date") public val eventDate: String? = null,
    @SerialName("end_date") public val endDate: String? = null,
    @SerialName("event_time") public val eventTime: String? = null,
    @SerialName("end_time") public val endTime: String? = null,
    @SerialName("type") public val type: String? = null,
    @SerialName("type_class") public val typeClass: String? = null,
    @SerialName("type_display") public val typeDisplay: String? = null,
    @SerialName("status") public val status: String? = null,
    @SerialName("location_public") public val locationPublic: String? = null,
    @SerialName("location_city") public val locationCity: String? = null,
    @SerialName("location_lat") public val locationLat: Double? = null,
    @SerialName("location_lng") public val locationLng: Double? = null,
    @SerialName("logo_url") public val logoUrl: String? = null,
    @SerialName("banner_url") public val bannerUrl: String? = null,
    @SerialName("organizer_id") public val organizerId: Int? = null,
    @SerialName("organizer_username") public val organizerUsername: String? = null,
    @SerialName("organizer_nickname") public val organizerNickname: String? = null,
    @SerialName("organizer_avatar") public val organizerAvatar: String? = null,
    @SerialName("co_organizers") public val coOrganizers: List<CoOrganizer>? = null,
    @SerialName("agenda") public val agenda: List<AgendaItem>? = null,
    @SerialName("tags") public val tags: List<String>? = null,
    @SerialName("source_count") public val sourceCount: Int? = null,
    @SerialName("data_sources") public val dataSources: List<DataSource>? = null,
    @SerialName("badges") public val badges: List<GatheringBadge>? = null,
    @SerialName("is_furtv_coop_driven")
    @Serializable(with = BooleanAsIntSerializer::class)
    public val isFurtvCoopDriven: Boolean? = null,
    @SerialName("interested_count") public val interestedCount: Int? = null,
    @SerialName("is_interested") public val isInterested: Boolean? = null,
    @SerialName("going_friends_count") public val goingFriendsCount: Int? = null,
    @SerialName("registration_stats") public val registrationStats: GatheringRegistrationStats? = null,
    @SerialName("view_count") public val viewCount: Int? = null,
    @SerialName("location_district") public val locationDistrict: String? = null,
    @SerialName("location_detail") public val locationDetail: String? = null,
    @SerialName("fee_type") public val feeType: String? = null,
    @SerialName("fee_amount") public val feeAmount: String? = null,
    @SerialName("fee_description") public val feeDescription: String? = null,
    @SerialName("registration_deadline") public val registrationDeadline: String? = null,
    @SerialName("registration_open_days") public val registrationOpenDays: Int? = null,
    @SerialName("requires_approval") @Serializable(with = BooleanAsIntSerializer::class)
    public val requiresApproval: Boolean? = null,
    @SerialName("auto_approve_limit") public val autoApproveLimit: Int? = null,
    @SerialName("group_chat_link") public val groupChatLink: String? = null,
    @SerialName("group_chat_type") public val groupChatType: String? = null,
    @SerialName("cancellation_reason") public val cancellationReason: String? = null,
    @SerialName("requirements") public val requirements: String? = null,
    @SerialName("notes") public val notes: String? = null,
    @SerialName("is_recurring")
    @Serializable(with = BooleanAsIntSerializer::class)
    public val isRecurring: Boolean? = null,
    @SerialName("series_id") public val seriesId: Int? = null,
    @SerialName("parent_gathering_id") public val parentGatheringId: Int? = null,
    @SerialName("recurrence_rule") public val recurrenceRule: String? = null,
    @SerialName("recurrence_config") public val recurrenceConfig: String? = null,
    @SerialName("destination_count") public val destinationCount: Int? = null,
    @SerialName("interested_friends") public val interestedFriends: List<String>? = null,
    @SerialName("going_friends") public val goingFriends: List<String>? = null,
    @SerialName("organizer_species") public val organizerSpecies: String? = null,
    @SerialName("max_participants") public val maxParticipants: Int? = null,
    @SerialName("current_participants") public val currentParticipants: Int? = null,
    @SerialName("min_participants") public val minParticipants: Int? = null,
    @SerialName("content_source") public val contentSource: String? = null,
    @SerialName("external_source") public val externalSource: String? = null,
    @SerialName("external_id") public val externalId: String? = null,
    @SerialName("source_url") public val sourceUrl: String? = null,
    @SerialName("raw_payload") public val rawPayload: JsonObject? = null,
    @SerialName("sync_skip_source_updates") public val syncSkipSourceUpdates: Int? = null,
    @SerialName("sync_skip_reason") public val syncSkipReason: String? = null,
    @SerialName("sync_skip_updated_at") public val syncSkipUpdatedAt: String? = null,
    @SerialName("sync_skip_updated_by") public val syncSkipUpdatedBy: String? = null,
    @SerialName("organizer_type") public val organizerType: String? = null,
    @SerialName("registration_status") public val registrationStatus: String? = null,
    @SerialName("created_at") public val createdAt: String? = null,
    @SerialName("updated_at") public val updatedAt: String? = null,
    @SerialName("is_furtv_coop_badge_enabled")
    @Serializable(with = BooleanAsIntSerializer::class)
    public val isFurtvCoopBadgeEnabled: Boolean? = null,
)

/** 议程项。 */
@JsExport
@JsName("AgendaItem")
@Serializable
public data class AgendaItem(
    @SerialName("time") public val time: String,
    @SerialName("title") public val title: String,
    @SerialName("description") public val description: String? = null,
)

/** 协办者。 */
@JsExport
@JsName("CoOrganizer")
@Serializable
public data class CoOrganizer(
    @SerialName("user_id") public val userId: Int? = null,
    @SerialName("username") public val username: String? = null,
    @SerialName("nickname") public val nickname: String? = null,
    @SerialName("avatar") public val avatar: String? = null,
)

/** 聚会报名统计。 */
@JsExport
@JsName("GatheringRegistrationStats")
@Serializable
public data class GatheringRegistrationStats(
    @SerialName("total_registrations") public val totalRegistrations: Int? = null,
    @SerialName("approved_count") public val approvedCount: Int? = null,
    @SerialName("pending_count") public val pendingCount: Int? = null,
)

/** 聚会报名列表响应。 */
@JsExport
@JsName("GatheringRegistrationsResponse")
@Serializable
public data class GatheringRegistrationsResponse(
    @SerialName("success") public val success: Boolean,
    @SerialName("registrations") public val registrations: List<List<RegistrationItem>>,
    @SerialName("requestId") public val requestId: String,
)

/** 报名项。 */
@JsExport
@JsName("RegistrationItem")
@Serializable
public data class RegistrationItem(
    @SerialName("id") public val id: Int,
    @SerialName("status") public val status: String? = null,
    @SerialName("registration_time") public val registrationTime: String? = null,
    @SerialName("checked_in") public val checkedIn: Int? = null,
    @SerialName("user_id") public val userId: Int,
    @SerialName("username") public val username: String,
    @SerialName("nickname") public val nickname: String? = null,
    @SerialName("avatar_url") public val avatarUrl: String? = null,
    @SerialName("fursuit_species") public val fursuitSpecies: String? = null,
)

/** 聚会月历参数。 */
@JsExport
@JsName("GatheringMonthlyParams")
@Serializable
public data class GatheringMonthlyParams(
    public val year: Int,
    public val month: Int,
)

/** 聚会报名列表参数。 */
@JsExport
@JsName("GatheringRegistrationsParams")
@Serializable
public data class GatheringRegistrationsParams(
    public val gatheringId: String,
)
