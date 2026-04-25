package com.furrist.rp.furtv.sdk.search

import com.furrist.rp.furtv.sdk.model.RandomFursuitParams
import com.furrist.rp.furtv.sdk.model.SearchParams
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter

/**
 * 搜索和发现 API。
 *
 * 提供热门推荐、随机推荐、搜索、物种查询等发现功能的访问接口。
 * 所有方法均通过 HTTP GET 请求获取数据，返回对应的搜索/发现数据模型。
 *
 * ## 主要功能
 * - 热门推荐（getPopular）：获取当前热门用户列表
 * - 随机推荐（getRandomFursuit）：获取随机用户（支持个性化）
 * - 关键词搜索（search）：按关键词和类型搜索用户
 * - 搜索建议（getSearchSuggestions）：获取搜索补全建议
 * - 物种相关（searchBySpecies, getSpeciesList）：按物种筛选和浏览
 * - 地理位置（getPopularLocations）：获取热门地区列表
 *
 * ## 分页说明
 * - search() 方法使用 **cursor-based 分页**：通过 cursor 游标翻页
 * - 返回的 SearchData 包含 nextCursor 用于获取下一页
 * - 其他列表方法返回完整结果集（无分页）
 *
 * @param httpClient 配置好的 HTTP 客户端
 * @param baseUrl API 基础 URL，默认为 https://open-global.vdsentnet.com
 * @see SearchModels 搜索相关的数据模型定义
 * @see FursuitTvSdkException 异常层次结构
 */
@Suppress("TooManyFunctions")
public class SearchApi(
    private val httpClient: HttpClient,
    private val baseUrl: String = "https://open-global.vdsentnet.com",
) {
    /**
     * 获取热门推荐列表。
     *
     * 返回当前平台上最受欢迎的用户列表，按热度排序。
     * 可用于首页推荐、发现新用户等场景。
     *
     * @return 热门用户数据对象（PopularData），包含 users 列表
     * @throws NetworkException 网络连接失败或超时
     * @throws TokenExpiredException 访问令牌已过期或无效
     * @throws AuthenticationException 认证凭证缺失或无效
     * @throws ApiException 服务器返回业务错误(4xx/5xx)
     */
    public suspend fun getPopular(): PopularData {
        val response =
            httpClient.get("$baseUrl/api/proxy/furtv/popular")
                .body<PopularResponse>()
        return response.data
    }

    /**
     * 获取随机推荐用户列表。
     *
     * 返回随机选择的用户列表，可用于发现新用户、
     * 随机展示等场景。支持个性化推荐（基于用户偏好）。
     *
     * @param params 随机推荐参数对象，包含 count 和 personalized 字段
     * @return 随机推荐的用户列表（List\<RandomFursuit\>）
     * @throws NetworkException 网络连接失败或超时
     * @throws TokenExpiredException 访问令牌已过期或无效
     * @throws AuthenticationException 认证凭证缺失或无效
     * @throws ValidationException 参数值超出允许范围
     * @throws ApiException 服务器返回业务错误(4xx/5xx)
     */
    public suspend fun getRandomFursuit(params: RandomFursuitParams): List<RandomFursuit> {
        val response =
            httpClient.get("$baseUrl/api/proxy/furtv/fursuit/random") {
                params.count?.let { parameter("count", it) }
                params.personalized?.let { parameter("personalized", it) }
            }.body<RandomFursuitResponse>()
        return response.data
    }

    /**
     * 获取随机推荐（重载方法，保持向后兼容）。
     *
     * 便捷方法：直接传入参数而非使用 RandomFursuitParams 对象。
     * 内部委托给 getRandomFursuit(params) 方法。
     *
     * @param count 返回数量限制（建议 1-50），null 表示使用服务端默认值
     * @param personalized 是否启用个性化推荐（基于用户偏好），null 表示不指定
     * @return 随机推荐的用户列表（List\<RandomFursuit\>）
     * @throws NetworkException 网络连接失败或超时
     * @throws TokenExpiredException 访问令牌已过期或无效
     * @throws AuthenticationException 认证凭证缺失或无效
     */
    public suspend fun getRandomFursuit(count: Int? = null, personalized: Boolean? = null): List<RandomFursuit> {
        return getRandomFursuit(RandomFursuitParams(count, personalized))
    }

    /**
     * 按关键词搜索用户。
     *
     * 支持按关键词和类型（user/fursuit 等）进行搜索，使用 cursor-based 分页。
     * 返回结果包含匹配的用户列表和分页游标。
     *
     * @param params 搜索参数对象，包含 query/type/cursor/limit 字段
     * @return 搜索结果数据对象（SearchData），包含 results 列表和 nextCursor
     * @throws NetworkException 网络连接失败或超时
     * @throws TokenExpiredException 访问令牌已过期或无效
     * @throws AuthenticationException 认证凭证缺失或无效
     * @throws ValidationException 参数验证失败（如 query 为空）
     * @throws ApiException 服务器返回业务错误(4xx/5xx)
     */
    public suspend fun search(params: SearchParams): SearchData {
        val response =
            httpClient.get("$baseUrl/api/proxy/furtv/search") {
                parameter("q", params.query)
                params.type?.let { parameter("type", it) }
                params.cursor?.let { parameter("cursor", it) }
                params.limit?.let { parameter("limit", it) }
            }.body<SearchResponse>()
        return response.data
    }

    /**
     * 按关键词搜索用户（重载方法，保持向后兼容）。
     *
     * 便捷方法：直接传入参数而非使用 SearchParams 对象。
     * 使用 cursor-based 分页，通过 cursor 获取下一页结果。
     *
     * @param query 搜索关键词（支持模糊匹配，建议长度 1-50 字符）
     * @param type 搜索类型过滤（如 "user", "fursuit"），null 表示不限制
     * @param cursor 分页游标（从上一次搜索结果的 nextCursor 获取），null 表示首页
     * @param limit 返回数量限制（建议 1-100），null 表示使用服务端默认值
     * @return 搜索结果数据对象（SearchData），包含 results 列表和 nextCursor
     * @throws NetworkException 网络连接失败或超时
     * @throws TokenExpiredException 访问令牌已过期或无效
     * @throws AuthenticationException 认证凭证缺失或无效
     */
    public suspend fun search(
        query: String,
        type: String? = null,
        cursor: String? = null,
        limit: Int? = null,
    ): SearchData {
        return search(SearchParams(query, type, cursor, limit))
    }

    /**
     * 获取搜索建议列表。
     *
     * 根据输入的关键词返回搜索补全建议，可用于搜索框自动完成功能。
     *
     * @param query 搜索关键词（建议长度 ≥2 字符以获得有效结果）
     * @return 搜索建议字符串列表（List\<String\>），可能为空列表
     * @throws NetworkException 网络连接失败或过时
     * @throws TokenExpiredException 访问令牌已过期或无效
     * @throws AuthenticationException 认证凭证缺失或无效
     */
    public suspend fun getSearchSuggestions(query: String): List<String> {
        val response =
            httpClient.get("$baseUrl/api/proxy/furtv/search/suggestions") {
                parameter("q", query)
            }.body<SearchSuggestionsResponse>()
        return response.data.suggestions
    }

    /**
     * 按物种名称搜索用户。
     *
     * 查找指定物种（species）的用户，可用于物种筛选和浏览场景。
     *
     * @param species 物种名称（如 "wolf", "fox", "dragon"），支持模糊匹配
     * @return 物种搜索结果数据对象（SpeciesSearchData），包含匹配的用户列表
     * @throws NetworkException 网络连接失败或超时
     * @throws TokenExpiredException 访问令牌已过期或无效
     * @throws AuthenticationException 认证凭证缺失或无效
     * @throws NotFoundException 指定物种不存在或无匹配结果
     */
    public suspend fun searchBySpecies(species: String): SpeciesSearchData {
        val response =
            httpClient.get("$baseUrl/api/proxy/furtv/search/species/$species")
                .body<SpeciesSearchResponse>()
        return response.data
    }

    /**
     * 获取所有物种列表及统计信息。
     *
     * 返回平台支持的完整物种列表，包含每个物种的用户数量统计。
     * 可用于物种筛选器、浏览页面等场景。
     *
     * @return 物种列表数据对象（SpeciesListData），包含 species 列表和统计信息
     * @throws NetworkException 网络连接失败或超时
     * @throws TokenExpiredException 访问令牌已过期或无效
     * @throws AuthenticationException 认证凭证缺失或无效
     */
    public suspend fun getSpeciesList(): SpeciesListData {
        val response =
            httpClient.get("$baseUrl/api/proxy/furtv/species")
                .body<SpeciesListResponse>()
        return response.data
    }

    /**
     * 获取热门地区列表。
     *
     * 返回当前平台上用户分布最多的地理位置列表，按用户数量排序。
     * 可用于地区筛选、发现附近用户等场景。
     *
     * @return 热门地区数据对象（PopularLocationsData），包含 locations 列表
     * @throws NetworkException 网络连接失败或超时
     * @throws TokenExpiredException 访问令牌已过期或无效
     * @throws AuthenticationException 认证凭证缺失或无效
     */
    public suspend fun getPopularLocations(): PopularLocationsData {
        val response =
            httpClient.get("$baseUrl/api/proxy/furtv/locations/popular")
                .body<PopularLocationsResponse>()
        return response.data
    }
}
