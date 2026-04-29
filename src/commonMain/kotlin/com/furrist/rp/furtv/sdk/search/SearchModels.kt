package com.furrist.rp.furtv.sdk.search

import kotlin.js.JsExport
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@JsExport
@Serializable
public data class PopularResponse(
    @SerialName("success")
    public val success: Boolean,
    @SerialName("data")
    public val data: PopularData,
    @SerialName("requestId")
    public val requestId: String,
)

/** Popular users data containing a list of trending users. */
@JsExport
@Serializable
public data class PopularData(
    @SerialName("users")
    public val users: List<PopularUser>,
)

/** A popular user with full profile information from VDS. */
@JsExport
@Serializable
public data class PopularUser(
    @SerialName("id")
    public val id: Int,
    @SerialName("username")
    public val username: String,
    @SerialName("nickname")
    public val nickname: String? = null,
    @SerialName("avatar_url")
    public val avatarUrl: String? = null,
    @SerialName("fursuit_species")
    public val fursuitSpecies: String? = null,
    @SerialName("fursuit_maker")
    public val fursuitMaker: String? = null,
    @SerialName("showcase_portrait")
    public val showcasePortrait: String? = null,
    @SerialName("introduction")
    public val introduction: String? = null,
    @SerialName("view_count")
    public val viewCount: Int? = null,
    @SerialName("is_verified")
    public val isVerified: Boolean? = null,
    @SerialName("like_count")
    public val likeCount: Int? = null,
    @SerialName("created_at")
    public val createdAt: String? = null,
    @SerialName("destination")
    public val destination: String? = null,
    @SerialName("destination_expires_at")
    public val destinationExpiresAt: String? = null,
    @SerialName("popularity_score")
    public val popularityScore: Int? = null,
)

/** Response for random fursuit recommendation supporting single and multiple results. */
@JsExport
@Serializable
public data class RandomFursuitResponse(
    @SerialName("success")
    public val success: Boolean,
    @SerialName("fursuit")
    public val fursuit: RandomFursuit? = null,
    @SerialName("fursuits")
    public val fursuits: List<RandomFursuit>? = null,
    @SerialName("count")
    public val count: Int? = null,
    @SerialName("requested_count")
    public val requestedCount: Int? = null,
    @SerialName("debug_info")
    public val debugInfo: RandomDebugInfo? = null,
    @SerialName("requestId")
    public val requestId: String,
)

/** A randomly recommended fursuit user. */
@JsExport
@Serializable
public data class RandomFursuit(
    @SerialName("id")
    public val id: Int,
    @SerialName("username")
    public val username: String,
    @SerialName("nickname")
    public val nickname: String? = null,
    @SerialName("avatar_url")
    public val avatarUrl: String? = null,
    @SerialName("fursuit_species")
    public val fursuitSpecies: String? = null,
    @SerialName("fursuit_maker")
    public val fursuitMaker: String? = null,
    @SerialName("location")
    public val location: String? = null,
    @SerialName("destination")
    public val destination: String? = null,
    @SerialName("introduction")
    public val introduction: String? = null,
    @SerialName("view_count")
    public val viewCount: Int? = null,
    @SerialName("is_verified")
    public val isVerified: Boolean? = null,
)

/** Debug information returned by the random fursuit endpoint. */
@JsExport
@Serializable
public data class RandomDebugInfo(
    @SerialName("is_personalized")
    public val isPersonalized: Boolean? = null,
    @SerialName("cache_hit_count")
    public val cacheHitCount: Int? = null,
    @SerialName("response_ms")
    public val responseMs: Int? = null,
)

@JsExport
@Serializable
public data class SearchResponse(
    @SerialName("success")
    public val success: Boolean,
    @SerialName("data")
    public val data: SearchData,
    @SerialName("requestId")
    public val requestId: String,
)

/** Search results with pagination and metadata from VDS. */
@JsExport
@Serializable
public data class SearchData(
    @SerialName("users")
    public val users: List<SearchUser>,
    @SerialName("search_type")
    public val searchType: String? = null,
    @SerialName("search_keywords")
    public val searchKeywords: List<String>? = null,
    @SerialName("pagination")
    public val pagination: SearchPagination? = null,
    @SerialName("has_more")
    public val hasMore: Boolean = false,
    @SerialName("total")
    public val total: Int? = null,
    @SerialName("next_cursor")
    public val nextCursor: String? = null,
)

/** Pagination metadata for search results. */
@JsExport
@Serializable
public data class SearchPagination(
    @SerialName("page")
    public val page: Int? = null,
    @SerialName("limit")
    public val limit: Int? = null,
    @SerialName("total")
    public val total: Int? = null,
    @SerialName("total_pages")
    public val totalPages: Int? = null,
    @SerialName("next_cursor")
    public val nextCursor: String? = null,
)

/** A user returned in search results. */
@JsExport
@Serializable
public data class SearchUser(
    @SerialName("id")
    public val id: Int? = null,
    @SerialName("username")
    public val username: String? = null,
    @SerialName("nickname")
    public val nickname: String? = null,
    @SerialName("avatar_url")
    public val avatarUrl: String? = null,
    @SerialName("showcase_portrait")
    public val showcasePortrait: String? = null,
    @SerialName("fursuit_species")
    public val fursuitSpecies: String? = null,
    @SerialName("fursuit_maker")
    public val fursuitMaker: String? = null,
    @SerialName("location")
    public val location: String? = null,
    @SerialName("destinations")
    public val destinations: List<String>? = null,
    @SerialName("destination")
    public val destination: String? = null,
    @SerialName("destination_expires_at")
    public val destinationExpiresAt: String? = null,
    @SerialName("introduction")
    public val introduction: String? = null,
    @SerialName("view_count")
    public val viewCount: Int? = null,
    @SerialName("is_verified")
    public val isVerified: Boolean? = null,
    @SerialName("created_at")
    public val createdAt: String? = null,
)

@JsExport
@Serializable
public data class SearchSuggestionsResponse(
    @SerialName("success")
    public val success: Boolean,
    @SerialName("data")
    public val data: SearchSuggestionsData,
    @SerialName("requestId")
    public val requestId: String,
)

/** Search suggestions data containing a list of suggestion strings. */
@JsExport
@Serializable
public data class SearchSuggestionsData(
    @SerialName("suggestions")
    public val suggestions: List<String>,
)

@JsExport
@Serializable
public data class SpeciesSearchResponse(
    @SerialName("success")
    public val success: Boolean,
    @SerialName("data")
    public val data: SpeciesSearchData,
    @SerialName("requestId")
    public val requestId: String,
)

/** Species search results with pagination from VDS. */
@JsExport
@Serializable
public data class SpeciesSearchData(
    @SerialName("species")
    public val species: String,
    @SerialName("users")
    public val users: List<SpeciesSearchUser>,
    @SerialName("pagination")
    public val pagination: SearchPagination? = null,
    @SerialName("has_more")
    public val hasMore: Boolean = false,
    @SerialName("total")
    public val total: Int? = null,
    @SerialName("next_cursor")
    public val nextCursor: String? = null,
)

/** A user returned in species search results. */
@JsExport
@Serializable
public data class SpeciesSearchUser(
    @SerialName("id")
    public val id: Int? = null,
    @SerialName("username")
    public val username: String? = null,
    @SerialName("nickname")
    public val nickname: String? = null,
    @SerialName("avatar_url")
    public val avatarUrl: String? = null,
    @SerialName("showcase_portrait")
    public val showcasePortrait: String? = null,
    @SerialName("fursuit_species")
    public val fursuitSpecies: String? = null,
    @SerialName("fursuit_maker")
    public val fursuitMaker: String? = null,
    @SerialName("introduction")
    public val introduction: String? = null,
    @SerialName("view_count")
    public val viewCount: Int? = null,
    @SerialName("is_verified")
    public val isVerified: Boolean? = null,
    @SerialName("created_at")
    public val createdAt: String? = null,
)

@JsExport
@Serializable
public data class SpeciesListResponse(
    @SerialName("success")
    public val success: Boolean,
    @SerialName("data")
    public val data: SpeciesListData,
    @SerialName("requestId")
    public val requestId: String,
)

/** Species list data with statistics. */
@JsExport
@Serializable
public data class SpeciesListData(
    @SerialName("species")
    public val species: List<SpeciesInfo>,
    @SerialName("total")
    public val total: Int,
)

/** A species entry with its user count. */
@JsExport
@Serializable
public data class SpeciesInfo(
    @SerialName("species")
    public val species: String,
    @SerialName("count")
    public val count: Int,
)

@JsExport
@Serializable
public data class PopularLocationsResponse(
    @SerialName("success")
    public val success: Boolean,
    @SerialName("data")
    public val data: PopularLocationsData,
    @SerialName("requestId")
    public val requestId: String,
)

/** Popular locations data structured by provinces and cities. */
@JsExport
@Serializable
public data class PopularLocationsData(
    @SerialName("popular_provinces")
    public val popularProvinces: List<ProvinceInfo>,
    @SerialName("popular_cities")
    public val popularCities: List<CityInfo>,
    @SerialName("total_users")
    public val totalUsers: Int? = null,
)

/** A province with its user count. */
@JsExport
@Serializable
public data class ProvinceInfo(
    @SerialName("province")
    public val province: String,
    @SerialName("count")
    public val count: Int,
)

/** A city with its province and user count. */
@JsExport
@Serializable
public data class CityInfo(
    @SerialName("province")
    public val province: String,
    @SerialName("city")
    public val city: String,
    @SerialName("count")
    public val count: Int,
)
