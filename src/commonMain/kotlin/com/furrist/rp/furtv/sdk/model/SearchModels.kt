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

/** 热门用户响应。 */
@JsExport
@JsName("PopularResponse")
@Serializable
public data class PopularResponse(
    @SerialName("success") public val success: Boolean,
    @SerialName("users") public val users: List<PopularUser>,
    @SerialName("requestId") public val requestId: String,
)

/** 热门用户。 */
@JsExport
@JsName("PopularUser")
@Serializable
public data class PopularUser(
    @SerialName("id") public val id: Int,
    @SerialName("username") public val username: String,
    @SerialName("nickname") public val nickname: String? = null,
    @SerialName("avatar_url") public val avatarUrl: String? = null,
    @SerialName("fursuit_species") public val fursuitSpecies: String? = null,
    @SerialName("fursuit_maker") public val fursuitMaker: String? = null,
    @SerialName("showcase_portrait") public val showcasePortrait: String? = null,
    @SerialName("introduction") public val introduction: String? = null,
    @SerialName("view_count") public val viewCount: Int? = null,
    @SerialName("is_verified")
    @Serializable(with = BooleanAsIntSerializer::class)
    public val isVerified: Boolean? = null,
    @SerialName("like_count") public val likeCount: Int? = null,
    @SerialName("created_at") public val createdAt: String? = null,
    @SerialName("destination") public val destination: String? = null,
    @SerialName("destination_expires_at") public val destinationExpiresAt: String? = null,
    @SerialName("popularity_score") public val popularityScore: String? = null,
)

/** 随机推荐响应。 */
@JsExport
@JsName("RandomFursuitResponse")
@Serializable
public data class RandomFursuitResponse(
    @SerialName("success") public val success: Boolean,
    @SerialName("fursuit") public val fursuit: RandomFursuit? = null,
    @SerialName("fursuits") public val fursuits: List<RandomFursuit>? = null,
    @SerialName("count") public val count: Int? = null,
    @SerialName("requested_count") public val requestedCount: Int? = null,
    @SerialName("debug_info") public val debugInfo: RandomDebugInfo? = null,
    @SerialName("requestId") public val requestId: String,
)

/** 单个随机推荐 fursuit。 */
@JsExport
@JsName("RandomFursuit")
@Serializable
public data class RandomFursuit(
    @SerialName("id") public val id: Int,
    @SerialName("username") public val username: String,
    @SerialName("nickname") public val nickname: String? = null,
    @SerialName("avatar_url") public val avatarUrl: String? = null,
    @SerialName("fursuit_species") public val fursuitSpecies: String? = null,
    @SerialName("fursuit_maker") public val fursuitMaker: String? = null,
    @SerialName("destination") public val destination: String? = null,
    @SerialName("introduction") public val introduction: String? = null,
    @SerialName("view_count") public val viewCount: Int? = null,
    @SerialName("is_verified")
    @Serializable(with = BooleanAsIntSerializer::class)
    public val isVerified: Boolean? = null,
    @SerialName("fursuit_birthday") public val fursuitBirthday: String? = null,
    @SerialName("showcase_portrait") public val showcasePortrait: String? = null,
    @SerialName("showcase_landscape") public val showcaseLandscape: String? = null,
    @SerialName("showcase_other") public val showcaseOther: String? = null,
    @SerialName("destinations") public val destinations: List<String>? = null,
    @SerialName("destination_expires_at") public val destinationExpiresAt: String? = null,
    @Serializable(with = UserProfilePrivacySettingsSerializer::class)
    @SerialName("privacy_settings") public val privacySettings: UserProfilePrivacySettings? = null,
    @SerialName("has_all_images") public val hasAllImages: Boolean? = null,
    @SerialName("contact_info") public val contactInfo: UserProfileContactInfo? = null,
    @SerialName("contact_request") public val contactRequest: ContactRequestState? = null,
    @SerialName("has_completed_contact")
    @Serializable(with = BooleanAsIntSerializer::class)
    public val hasCompletedContact: Boolean? = null,
    @SerialName("today_status") public val todayStatus: TodayStatus? = null,
)

/** 随机推荐用户今日状态。 */
@JsExport
@JsName("TodayStatus")
@Serializable
public data class TodayStatus(
    @SerialName("has_today") public val hasToday: Boolean? = null,
)

/** 随机推荐调试信息。 */
@JsExport
@JsName("RandomDebugInfo")
@Serializable
public data class RandomDebugInfo(
    @SerialName("is_personalized") public val isPersonalized: Boolean? = null,
    @SerialName("cache_hit_count") public val cacheHitCount: Int? = null,
    @SerialName("response_ms") public val responseMs: Int? = null,
)

/** 搜索响应。 */
@JsExport
@JsName("SearchResponse")
@Serializable
public data class SearchResponse(
    @SerialName("success") public val success: Boolean,
    @SerialName("users") public val users: List<SearchUser>,
    @SerialName("search_type") public val searchType: String? = null,
    @SerialName("search_keywords") public val searchKeywords: List<String>? = null,
    @SerialName("pagination") public val pagination: SearchPagination? = null,
    @SerialName("has_more") public val hasMore: Boolean = false,
    @SerialName("total") public val total: Int? = null,
    @SerialName("next_cursor") public val nextCursor: String? = null,
    @SerialName("total_is_estimate") public val totalIsEstimate: Boolean? = null,
    @SerialName("requestId") public val requestId: String,
)

/** 搜索分页信息。 */
@JsExport
@JsName("SearchPagination")
@Serializable
public data class SearchPagination(
    @SerialName("page") public val page: Int? = null,
    @SerialName("limit") public val limit: Int? = null,
    @SerialName("total") public val total: Int? = null,
    @SerialName("total_pages") public val totalPages: Int? = null,
    @SerialName("next_cursor") public val nextCursor: String? = null,
    @SerialName("total_is_estimate") public val totalIsEstimate: Boolean? = null,
)

/** 搜索结果中的用户。 */
@JsExport
@JsName("SearchUser")
@Serializable
public data class SearchUser(
    @SerialName("id") public val id: Int? = null,
    @SerialName("username") public val username: String? = null,
    @SerialName("nickname") public val nickname: String? = null,
    @SerialName("avatar_url") public val avatarUrl: String? = null,
    @SerialName("showcase_portrait") public val showcasePortrait: String? = null,
    @SerialName("fursuit_species") public val fursuitSpecies: String? = null,
    @SerialName("fursuit_maker") public val fursuitMaker: String? = null,
    @SerialName("location") public val location: String? = null,
    @SerialName("destinations") public val destinations: List<String>? = null,
    @SerialName("destination") public val destination: String? = null,
    @SerialName("destination_expires_at") public val destinationExpiresAt: String? = null,
    @SerialName("introduction") public val introduction: String? = null,
    @SerialName("view_count") public val viewCount: Int? = null,
    @SerialName("is_verified")
    @Serializable(with = BooleanAsIntSerializer::class)
    public val isVerified: Boolean? = null,
    @SerialName("created_at") public val createdAt: String? = null,
    @SerialName("like_count") public val likeCount: Int? = null,
    @SerialName("is_liked")
    @Serializable(with = BooleanAsIntSerializer::class)
    public val isLiked: Boolean? = null,
)

/** 搜索建议响应。 */
@JsExport
@JsName("SearchSuggestionsResponse")
@Serializable
public data class SearchSuggestionsResponse(
    @SerialName("success") public val success: Boolean,
    @SerialName("suggestions") public val suggestions: List<String>,
    @SerialName("requestId") public val requestId: String,
)

/** 物种搜索响应。 */
@JsExport
@JsName("SpeciesSearchResponse")
@Serializable
public data class SpeciesSearchResponse(
    @SerialName("success") public val success: Boolean,
    @SerialName("species") public val species: String,
    @SerialName("users") public val users: List<SpeciesSearchUser>,
    @SerialName("pagination") public val pagination: SearchPagination? = null,
    @SerialName("has_more") public val hasMore: Boolean = false,
    @SerialName("total") public val total: Int? = null,
    @SerialName("next_cursor") public val nextCursor: String? = null,
    @SerialName("total_is_estimate") public val totalIsEstimate: Boolean? = null,
    @SerialName("requestId") public val requestId: String,
)

/** 物种搜索用户。 */
@JsExport
@JsName("SpeciesSearchUser")
@Serializable
public data class SpeciesSearchUser(
    @SerialName("id") public val id: Int? = null,
    @SerialName("username") public val username: String? = null,
    @SerialName("nickname") public val nickname: String? = null,
    @SerialName("avatar_url") public val avatarUrl: String? = null,
    @SerialName("showcase_portrait") public val showcasePortrait: String? = null,
    @SerialName("fursuit_species") public val fursuitSpecies: String? = null,
    @SerialName("fursuit_maker") public val fursuitMaker: String? = null,
    @SerialName("introduction") public val introduction: String? = null,
    @SerialName("view_count") public val viewCount: Int? = null,
    @SerialName("is_verified")
    @Serializable(with = BooleanAsIntSerializer::class)
    public val isVerified: Boolean? = null,
    @SerialName("created_at") public val createdAt: String? = null,
    @SerialName("like_count") public val likeCount: Int? = null,
    @SerialName("is_liked")
    @Serializable(with = BooleanAsIntSerializer::class)
    public val isLiked: Boolean? = null,
)

/** 物种列表响应。 */
@JsExport
@JsName("SpeciesListResponse")
@Serializable
public data class SpeciesListResponse(
    @SerialName("success") public val success: Boolean,
    @SerialName("species") public val species: List<SpeciesInfo>,
    @SerialName("total") public val total: Int,
    @SerialName("requestId") public val requestId: String,
)

/** 物种信息。 */
@JsExport
@JsName("SpeciesInfo")
@Serializable
public data class SpeciesInfo(
    @SerialName("species") public val species: String,
    @SerialName("count") public val count: Int,
)

/** 热门地点响应。 */
@JsExport
@JsName("PopularLocationsResponse")
@Serializable
public data class PopularLocationsResponse(
    @SerialName("success") public val success: Boolean,
    @SerialName("popular_provinces") public val popularProvinces: List<ProvinceInfo>,
    @SerialName("popular_cities") public val popularCities: List<CityInfo>,
    @SerialName("requestId") public val requestId: String,
)

/** 省份信息。 */
@JsExport
@JsName("ProvinceInfo")
@Serializable
public data class ProvinceInfo(
    @SerialName("province") public val province: String,
    @SerialName("count") public val count: Int,
)

/** 城市信息。 */
@JsExport
@JsName("CityInfo")
@Serializable
public data class CityInfo(
    @SerialName("province") public val province: String,
    @SerialName("city") public val city: String,
    @SerialName("count") public val count: Int,
)

/** 搜索参数。 */
@JsExport
@JsName("SearchParams")
@Serializable
public data class SearchParams(
    public val query: String,
    public val type: String? = null,
    public val cursor: String? = null,
    public val limit: Int? = null,
    public val page: Int? = null,
)

/** 随机推荐参数。 */
@JsExport
@JsName("RandomFursuitParams")
@Serializable
public data class RandomFursuitParams(
    public val count: Int? = null,
)
