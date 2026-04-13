package me.regadpole.furtv.sdk.search

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// ==================== Discovery 热门推荐 ====================

/**
 * Discovery 热门推荐响应
 * 热门推荐接口的响应包装
 * 端点：/api/proxy/furtv/discovery/popular
 */
@Serializable
public data class DiscoveryPopularResponse(
    @SerialName("success")
    public val success: Boolean,
    @SerialName("data")
    public val data: DiscoveryPopularData,
    @SerialName("requestId")
    public val requestId: String,
)

/**
 * Discovery 热门推荐数据
 * 包含热门用户列表
 */
@Serializable
public data class DiscoveryPopularData(
    @SerialName("users")
    public val users: List<DiscoveryPopularUser>,
)

/**
 * Discovery 热门用户
 * 表示一个热门用户的信息
 * @param username 用户名
 * @param displayName 显示名称
 * @param avatarUrl 头像 URL
 * @param popularity 热门程度值
 */
@Serializable
public data class DiscoveryPopularUser(
    @SerialName("username")
    public val username: String,
    @SerialName("displayName")
    public val displayName: String,
    @SerialName("avatarUrl")
    public val avatarUrl: String? = null,
    @SerialName("popularity")
    public val popularity: Int,
)

// ==================== Discovery 随机推荐 ====================

/**
 * Discovery 随机推荐响应
 * 随机推荐接口的响应包装
 * 端点：/api/proxy/furtv/discovery/random
 */
@Serializable
public data class DiscoveryRandomResponse(
    @SerialName("success")
    public val success: Boolean,
    @SerialName("data")
    public val data: List<DiscoveryRandomUser>,
    @SerialName("requestId")
    public val requestId: String,
)

/**
 * Discovery 随机推荐用户
 * 表示一个随机推荐的用户信息
 * @param username 用户名
 * @param displayName 显示名称
 * @param avatarUrl 头像 URL
 * @param species 物种
 * @param description 描述
 */
@Serializable
public data class DiscoveryRandomUser(
    @SerialName("username")
    public val username: String,
    @SerialName("displayName")
    public val displayName: String,
    @SerialName("avatarUrl")
    public val avatarUrl: String? = null,
    @SerialName("species")
    public val species: String? = null,
    @SerialName("description")
    public val description: String? = null,
)

// ==================== Discovery 搜索 ====================

/**
 * Discovery 搜索响应
 * 搜索接口的响应包装
 * 端点：/api/proxy/furtv/discovery/search
 */
@Serializable
public data class DiscoverySearchResponse(
    @SerialName("success")
    public val success: Boolean,
    @SerialName("data")
    public val data: DiscoverySearchData,
    @SerialName("requestId")
    public val requestId: String,
)

/**
 * Discovery 搜索数据
 * 包含搜索结果和分页信息
 * @param results 搜索结果列表
 * @param nextCursor 下一页游标
 * @param hasMore 是否还有更多结果
 */
@Serializable
public data class DiscoverySearchData(
    @SerialName("results")
    public val results: List<DiscoverySearchResult>,
    @SerialName("nextCursor")
    public val nextCursor: String? = null,
    @SerialName("hasMore")
    public val hasMore: Boolean,
)

/**
 * Discovery 搜索结果
 * 表示一个搜索结果项
 * @param type 结果类型
 * @param username 用户名
 * @param displayName 显示名称
 * @param avatarUrl 头像 URL
 * @param description 描述
 */
@Serializable
public data class DiscoverySearchResult(
    @SerialName("type")
    public val type: String,
    @SerialName("username")
    public val username: String? = null,
    @SerialName("displayName")
    public val displayName: String? = null,
    @SerialName("avatarUrl")
    public val avatarUrl: String? = null,
    @SerialName("description")
    public val description: String? = null,
)

// ==================== Discovery 搜索建议 ====================

/**
 * Discovery 搜索建议响应
 * 搜索建议接口的响应包装
 * 端点：/api/proxy/furtv/discovery/search/suggestions
 */
@Serializable
public data class DiscoverySearchSuggestionsResponse(
    @SerialName("success")
    public val success: Boolean,
    @SerialName("data")
    public val data: DiscoverySearchSuggestionsData,
    @SerialName("requestId")
    public val requestId: String,
)

/**
 * Discovery 搜索建议数据
 * 包含搜索建议列表
 */
@Serializable
public data class DiscoverySearchSuggestionsData(
    @SerialName("suggestions")
    public val suggestions: List<String>,
)

// ==================== Discovery 物种列表 ====================

/**
 * Discovery 物种列表响应
 * 物种列表接口的响应包装
 * 端点：/api/proxy/furtv/discovery/species
 */
@Serializable
public data class DiscoverySpeciesResponse(
    @SerialName("success")
    public val success: Boolean,
    @SerialName("data")
    public val data: DiscoverySpeciesData,
    @SerialName("requestId")
    public val requestId: String,
)

/**
 * Discovery 物种列表数据
 * 包含物种列表和统计信息
 * @param species 物种列表
 * @param totalCount 物种总数
 */
@Serializable
public data class DiscoverySpeciesData(
    @SerialName("species")
    public val species: List<DiscoverySpeciesInfo>,
    @SerialName("totalCount")
    public val totalCount: Int,
)

/**
 * Discovery 物种信息
 * 表示一个物种的统计信息
 * @param id 物种 ID
 * @param name 物种名称
 * @param count 该物种的用户数量
 */
@Serializable
public data class DiscoverySpeciesInfo(
    @SerialName("id")
    public val id: String,
    @SerialName("name")
    public val name: String,
    @SerialName("count")
    public val count: Int,
)

// ==================== Discovery 按物种搜索 ====================

/**
 * Discovery 按物种搜索响应
 * 按物种搜索接口的响应包装
 * 端点：/api/proxy/furtv/discovery/species/search
 */
@Serializable
public data class DiscoverySpeciesSearchResponse(
    @SerialName("success")
    public val success: Boolean,
    @SerialName("data")
    public val data: DiscoverySpeciesSearchData,
    @SerialName("requestId")
    public val requestId: String,
)

/**
 * Discovery 按物种搜索数据
 * 包含物种搜索结果和分页信息
 * @param speciesId 物种 ID
 * @param results 用户列表
 * @param nextCursor 下一页游标
 * @param hasMore 是否还有更多结果
 */
@Serializable
public data class DiscoverySpeciesSearchData(
    @SerialName("speciesId")
    public val speciesId: String,
    @SerialName("results")
    public val results: List<DiscoverySpeciesSearchResult>,
    @SerialName("nextCursor")
    public val nextCursor: String? = null,
    @SerialName("hasMore")
    public val hasMore: Boolean,
)

/**
 * Discovery 物种搜索结果
 * 表示一个按物种搜索的用户结果
 * @param username 用户名
 * @param displayName 显示名称
 * @param avatarUrl 头像 URL
 * @param description 描述
 */
@Serializable
public data class DiscoverySpeciesSearchResult(
    @SerialName("username")
    public val username: String,
    @SerialName("displayName")
    public val displayName: String,
    @SerialName("avatarUrl")
    public val avatarUrl: String? = null,
    @SerialName("description")
    public val description: String? = null,
)

// ==================== Discovery 热门地区 ====================

/**
 * Discovery 热门地区响应
 * 热门地区接口的响应包装
 * 端点：/api/proxy/furtv/discovery/locations/popular
 */
@Serializable
public data class DiscoveryPopularLocationsResponse(
    @SerialName("success")
    public val success: Boolean,
    @SerialName("data")
    public val data: DiscoveryPopularLocationsData,
    @SerialName("requestId")
    public val requestId: String,
)

/**
 * Discovery 热门地区数据
 * 包含热门地区列表
 */
@Serializable
public data class DiscoveryPopularLocationsData(
    @SerialName("locations")
    public val locations: List<DiscoveryLocationInfo>,
)

/**
 * Discovery 地区信息
 * 表示一个地区的信息
 * @param id 地区 ID
 * @param name 地区名称
 * @param count 用户数量
 */
@Serializable
public data class DiscoveryLocationInfo(
    @SerialName("id")
    public val id: String,
    @SerialName("name")
    public val name: String,
    @SerialName("count")
    public val count: Int,
)

// ==================== 热门推荐 ====================

/**
 * 热门推荐响应
 * 热门推荐接口的响应包装 */
@Serializable
public data class PopularResponse(
    public val success: Boolean,
    public val data: PopularData,
    public val requestId: String,
)

/**
 * 热门推荐数据
 * 包含热门用户列表
 */
@Serializable
public data class PopularData(
    public val users: List<PopularUser>,
)

/**
 * 热门用户
 * 表示一个热门用户的信息
 * @param username 用户名
 * @param displayName 显示名称
 * @param avatarUrl 头像 URL
 * @param popularity 热门程度值
 */
@Serializable
public data class PopularUser(
    public val username: String,
    public val displayName: String,
    public val avatarUrl: String? = null,
    public val popularity: Int,
)

// ==================== 随机推荐 ====================

/**
 * 随机推荐响应
 * 随机推荐接口的响应包装 */
@Serializable
public data class RandomFursuitResponse(
    public val success: Boolean,
    public val data: List<RandomFursuit>,
    public val requestId: String,
)

/**
 * 随机推荐用户
 * 表示一个随机推荐的用户信息
 * @param username 用户名
 * @param displayName 显示名称
 * @param avatarUrl 头像 URL
 * @param species 物种
 */
@Serializable
public data class RandomFursuit(
    public val username: String,
    public val displayName: String,
    public val avatarUrl: String? = null,
    public val species: String? = null,
)

// ==================== 搜索 ====================

/**
 * 搜索响应
 * 搜索接口的响应包装 */
@Serializable
public data class SearchResponse(
    public val success: Boolean,
    public val data: SearchData,
    public val requestId: String,
)

/**
 * 搜索数据
 * 包含搜索结果和分页信息
 * @param results 搜索结果列表
 * @param nextCursor 下一页游标
 * @param hasMore 是否还有更多结果
 */
@Serializable
public data class SearchData(
    public val results: List<SearchResult>,
    public val nextCursor: String? = null,
    public val hasMore: Boolean,
)

/**
 * 搜索结果
 * 表示一个搜索结果项
 * @param type 结果类型
 * @param username 用户名
 * @param displayName 显示名称
 * @param avatarUrl 头像 URL
 * @param description 描述
 */
@Serializable
public data class SearchResult(
    public val type: String,
    public val username: String? = null,
    public val displayName: String? = null,
    public val avatarUrl: String? = null,
    public val description: String? = null,
)

// ==================== 搜索建议 ====================

/**
 * 搜索建议响应
 * 搜索建议接口的响应包装 */
@Serializable
public data class SearchSuggestionsResponse(
    public val success: Boolean,
    public val data: SearchSuggestionsData,
    public val requestId: String,
)

/**
 * 搜索建议数据
 * 包含搜索建议列表
 */
@Serializable
public data class SearchSuggestionsData(
    public val suggestions: List<String>,
)

// ==================== 物种搜索 ====================

/**
 * 物种搜索响应
 * 物种搜索接口的响应包装 */
@Serializable
public data class SpeciesSearchResponse(
    public val success: Boolean,
    public val data: SpeciesSearchData,
    public val requestId: String,
)

/**
 * 物种搜索数据
 * 包含物种搜索结果
 * @param species 物种名称
 * @param users 用户列表
 * @param totalCount 总数
 */
@Serializable
public data class SpeciesSearchData(
    public val species: String,
    public val users: List<SpeciesUser>,
    public val totalCount: Int,
)

/**
 * 物种用户
 * 表示属于某个物种的用户
 * @param username 用户名
 * @param displayName 显示名称
 * @param avatarUrl 头像 URL
 */
@Serializable
public data class SpeciesUser(
    public val username: String,
    public val displayName: String,
    public val avatarUrl: String? = null,
)

// ==================== 物种列表 ====================

/**
 * 物种列表响应
 * 物种列表接口的响应包装 */
@Serializable
public data class SpeciesListResponse(
    public val success: Boolean,
    public val data: SpeciesListData,
    public val requestId: String,
)

/**
 * 物种列表数据
 * 包含物种列表和统计信息
 * @param species 物种列表
 * @param totalCount 物种总数
 */
@Serializable
public data class SpeciesListData(
    public val species: List<SpeciesInfo>,
    public val totalCount: Int,
)

/**
 * 物种信息
 * 表示一个物种的统计信息
 * @param name 物种名称
 * @param count 该物种的用户数量
 */
@Serializable
public data class SpeciesInfo(
    public val name: String,
    public val count: Int,
)

// ==================== 热门地区 ====================

/**
 * 热门地区响应
 * 热门地区接口的响应包装 */
@Serializable
public data class PopularLocationsResponse(
    public val success: Boolean,
    public val data: PopularLocationsData,
    public val requestId: String,
)

/**
 * 热门地区数据
 * 包含热门地区列表
 */
@Serializable
public data class PopularLocationsData(
    public val locations: List<LocationInfo>,
)

/**
 * 地区信息
 * 表示一个地区的信息
 * @param province 省份
 * @param city 城市
 * @param count 用户数量
 */
@Serializable
public data class LocationInfo(
    public val province: String,
    public val city: String? = null,
    public val count: Int,
)
