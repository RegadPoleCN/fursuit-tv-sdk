package com.furrist.rp.furtv.sdk.search

import com.furrist.rp.furtv.sdk.model.DiscoverySearchParams
import com.furrist.rp.furtv.sdk.model.DiscoverySpeciesSearchParams
import com.furrist.rp.furtv.sdk.model.RandomFursuitParams
import com.furrist.rp.furtv.sdk.model.SearchParams
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter

/**
 * 搜索和发现 API
 * 提供热门推荐、随机推荐、搜索、物种查询等发现功能的访问接口
 *
 * 参考官方文档：
 * - [热门推荐](../../../../vds-docs/Fursuit.TV 兽频道/发现与搜索 - 推荐能力/热门推荐（furtv.discovery.popular）.md)
 * - [随机推荐](../../../../vds-docs/Fursuit.TV 兽频道/发现与搜索 - 推荐能力/随机推荐（furtv.discovery.random）.md)
 * - [搜索](../../../../vds-docs/Fursuit.TV 兽频道/发现与搜索 - 检索能力/搜索（furtv.discovery.search）.md)
 * - [搜索建议](../../../../vds-docs/Fursuit.TV 兽频道/发现与搜索 - 检索能力/搜索建议（furtv.discovery.search.suggestions）.md)
 * - [物种列表](../../../../vds-docs/Fursuit.TV 兽频道/发现与搜索 - 检索能力/物种列表（furtv.discovery.species）.md)
 * - [按物种搜索](../../../../vds-docs/Fursuit.TV 兽频道/发现与搜索 - 检索能力/按物种搜索（furtv.discovery.species.search）.md)
 * - [热门地区](../../../../vds-docs/Fursuit.TV 兽频道/发现与搜索 - 检索能力/热门地区（furtv.discovery.locations.popular）.md)
 *
 * @param httpClient 配置好的 HTTP 客户端
 * @param baseUrl API 基础 URL
 */
@Suppress("TooManyFunctions")
public class SearchApi(
    private val httpClient: HttpClient,
    private val baseUrl: String = "https://open-global.vdsentnet.com",
) {
    /**
     * 获取热门推荐
     * 获取当前热门用户列表
     *
     * 端点：`GET /api/proxy/furtv/popular`
     *
     * 官方文档：[热门推荐](../../../../vds-docs/Fursuit.TV 兽频道/发现与搜索 - 推荐能力/热门推荐（furtv.discovery.popular）.md)
     *
     * @return PopularData 包含热门用户列表
     */
    public suspend fun getPopular(): PopularData {
        val response =
            httpClient.get("$baseUrl/api/proxy/furtv/popular")
                .body<PopularResponse>()
        return response.data
    }

    /**
     * 获取随机推荐
     * 获取随机推荐的用户列表，支持个性化推荐
     * 端点：GET /api/proxy/furtv/fursuit/random
     * @param params 随机推荐参数
     * @return List<RandomFursuit> 随机推荐的用户列表
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
     * 获取随机推荐（重载方法，保持向后兼容）
     * @param count 返回数量
     * @param personalized 是否个性化推荐
     * @return List<RandomFursuit> 随机推荐的用户列表
     */
    public suspend fun getRandomFursuit(count: Int? = null, personalized: Boolean? = null): List<RandomFursuit> {
        return getRandomFursuit(RandomFursuitParams(count, personalized))
    }

    /**
     * 搜索
     * 执行搜索操作，支持多种类型和分页
     * 端点：GET /api/proxy/furtv/search
     * @param params 搜索参数
     * @return SearchData 搜索结果和分页信息
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
     * 搜索（重载方法，保持向后兼容）
     * @param query 搜索关键词
     * @param type 搜索类型
     * @param cursor 分页游标
     * @param limit 返回数量限制
     * @return SearchData 搜索结果和分页信息
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
     * 获取搜索建议
     * 根据关键词获取搜索建议（自动补全）
     * 端点：GET /api/proxy/furtv/search/suggestions
     * @param query 搜索关键词
     * @return List<String> 搜索建议列表
     */
    public suspend fun getSearchSuggestions(query: String): List<String> {
        val response =
            httpClient.get("$baseUrl/api/proxy/furtv/search/suggestions") {
                parameter("q", query)
            }.body<SearchSuggestionsResponse>()
        return response.data.suggestions
    }

    /**
     * 按物种搜索
     * 根据物种名称搜索用户
     * 端点：GET /api/proxy/furtv/search/species/:species
     * @param species 物种名称
     * @return SpeciesSearchData 搜索结果
     */
    public suspend fun searchBySpecies(species: String): SpeciesSearchData {
        val response =
            httpClient.get("$baseUrl/api/proxy/furtv/search/species/$species")
                .body<SpeciesSearchResponse>()
        return response.data
    }

    /**
     * 获取物种列表
     * 获取所有物种及相关统计信息
     * 端点：GET /api/proxy/furtv/species
     * @return SpeciesListData 物种列表和统计
     */
    public suspend fun getSpeciesList(): SpeciesListData {
        val response =
            httpClient.get("$baseUrl/api/proxy/furtv/species")
                .body<SpeciesListResponse>()
        return response.data
    }

    /**
     * 获取热门地区
     * 获取用户数量最多的地区列表
     * 端点：GET /api/proxy/furtv/locations/popular
     * @return PopularLocationsData 热门地区列表
     */
    public suspend fun getPopularLocations(): PopularLocationsData {
        val response =
            httpClient.get("$baseUrl/api/proxy/furtv/locations/popular")
                .body<PopularLocationsResponse>()
        return response.data
    }

    // ==================== Discovery 相关接口 ====================

    /**
     * 获取热门推荐（Discovery）
     * 获取当前热门用户列表
     * 端点：GET /api/proxy/furtv/discovery/popular
     * @return DiscoveryPopularData 包含热门用户列表
     */
    public suspend fun getPopularDiscovery(): DiscoveryPopularData {
        val response =
            httpClient.get("$baseUrl/api/proxy/furtv/discovery/popular")
                .body<DiscoveryPopularResponse>()
        return response.data
    }

    /**
     * 获取随机推荐（Discovery）
     * 获取随机推荐的用户列表，支持个性化推荐
     * 端点：GET /api/proxy/furtv/discovery/random
     * @param count 返回数量（可选）
     * @param personalized 是否个性化推荐（可选）
     * @return List<DiscoveryRandomUser> 随机推荐的用户列表
     */
    public suspend fun getRandomDiscovery(
        count: Int? = null,
        personalized: Boolean? = null,
    ): List<DiscoveryRandomUser> {
        val response =
            httpClient.get("$baseUrl/api/proxy/furtv/discovery/random") {
                count?.let { parameter("count", it) }
                personalized?.let { parameter("personalized", it) }
            }.body<DiscoveryRandomResponse>()
        return response.data
    }

    /**
     * 搜索（Discovery）
     * 执行搜索操作，支持多种类型和分页
     * 端点：GET /api/proxy/furtv/discovery/search
     * @param params 搜索参数
     * @return DiscoverySearchData 搜索结果和分页信息
     */
    public suspend fun searchDiscovery(params: DiscoverySearchParams): DiscoverySearchData {
        val response =
            httpClient.get("$baseUrl/api/proxy/furtv/discovery/search") {
                parameter("q", params.query)
                params.page?.let { parameter("page", it) }
                params.pageSize?.let { parameter("pageSize", it) }
                params.type?.let { parameter("type", it) }
            }.body<DiscoverySearchResponse>()
        return response.data
    }

    /**
     * 搜索（Discovery 重载方法）
     * @param query 搜索关键词
     * @param page 页码（可选，默认 1）
     * @param pageSize 每页数量（可选，默认 20）
     * @param type 搜索类型（可选）
     * @return DiscoverySearchData 搜索结果和分页信息
     */
    public suspend fun searchDiscovery(
        query: String,
        page: Int? = null,
        pageSize: Int? = null,
        type: String? = null,
    ): DiscoverySearchData {
        return searchDiscovery(DiscoverySearchParams(query, page, pageSize, type))
    }

    /**
     * 获取搜索建议（Discovery）
     * 根据关键词获取搜索建议（自动补全）
     * 端点：GET /api/proxy/furtv/discovery/search/suggestions
     * @param query 搜索关键词
     * @return List<String> 搜索建议列表
     */
    public suspend fun getSearchSuggestionsDiscovery(query: String): List<String> {
        val response =
            httpClient.get("$baseUrl/api/proxy/furtv/discovery/search/suggestions") {
                parameter("q", query)
            }.body<DiscoverySearchSuggestionsResponse>()
        return response.data.suggestions
    }

    /**
     * 获取物种列表（Discovery）
     * 获取所有物种及相关统计信息
     * 端点：GET /api/proxy/furtv/discovery/species
     * @return DiscoverySpeciesData 物种列表和统计
     */
    public suspend fun getSpeciesDiscovery(): DiscoverySpeciesData {
        val response =
            httpClient.get("$baseUrl/api/proxy/furtv/discovery/species")
                .body<DiscoverySpeciesResponse>()
        return response.data
    }

    /**
     * 按物种搜索（Discovery）
     * 根据物种 ID 搜索用户，支持分页
     * 端点：GET /api/proxy/furtv/discovery/species/search
     * @param params 按物种搜索参数
     * @return DiscoverySpeciesSearchData 搜索结果和分页信息
     */
    public suspend fun searchBySpeciesDiscovery(params: DiscoverySpeciesSearchParams): DiscoverySpeciesSearchData {
        val response =
            httpClient.get("$baseUrl/api/proxy/furtv/discovery/species/search") {
                parameter("speciesId", params.speciesId)
                params.page?.let { parameter("page", it) }
                params.pageSize?.let { parameter("pageSize", it) }
            }.body<DiscoverySpeciesSearchResponse>()
        return response.data
    }

    /**
     * 按物种搜索（Discovery 重载方法）
     * @param speciesId 物种 ID
     * @param page 页码（可选，默认 1）
     * @param pageSize 每页数量（可选，默认 20）
     * @return DiscoverySpeciesSearchData 搜索结果和分页信息
     */
    public suspend fun searchBySpeciesDiscovery(
        speciesId: String,
        page: Int? = null,
        pageSize: Int? = null,
    ): DiscoverySpeciesSearchData {
        return searchBySpeciesDiscovery(DiscoverySpeciesSearchParams(speciesId, page, pageSize))
    }

    /**
     * 获取热门地区（Discovery）
     * 获取用户数量最多的地区列表
     * 端点：GET /api/proxy/furtv/discovery/locations/popular
     * @return DiscoveryPopularLocationsData 热门地区列表
     */
    public suspend fun getPopularLocationsDiscovery(): DiscoveryPopularLocationsData {
        val response =
            httpClient.get("$baseUrl/api/proxy/furtv/discovery/locations/popular")
                .body<DiscoveryPopularLocationsResponse>()
        return response.data
    }
}
