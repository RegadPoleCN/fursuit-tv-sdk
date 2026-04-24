package com.furrist.rp.furtv.sdk.search

import kotlinx.serialization.Serializable

// ==================== 标准搜索 API 响应模型 ====================

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
