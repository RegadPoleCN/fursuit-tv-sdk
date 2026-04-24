package com.furrist.rp.furtv.sdk.search

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
     * @return 热门用户列表
     */
    public suspend fun getPopular(): PopularData {
        val response =
            httpClient.get("$baseUrl/api/proxy/furtv/popular")
                .body<PopularResponse>()
        return response.data
    }

    /**
     * 获取随机推荐
     * @param params 随机推荐参数
     * @return 随机推荐的用户列表
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
     * @return 随机推荐的用户列表
     */
    public suspend fun getRandomFursuit(count: Int? = null, personalized: Boolean? = null): List<RandomFursuit> {
        return getRandomFursuit(RandomFursuitParams(count, personalized))
    }

    /**
     * 搜索
     * @param params 搜索参数
     * @return 搜索结果和分页信息
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
     * @return 搜索结果和分页信息
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
     * @param query 搜索关键词
     * @return 搜索建议列表
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
     * @param species 物种名称
     * @return 搜索结果
     */
    public suspend fun searchBySpecies(species: String): SpeciesSearchData {
        val response =
            httpClient.get("$baseUrl/api/proxy/furtv/search/species/$species")
                .body<SpeciesSearchResponse>()
        return response.data
    }

    /**
     * 获取物种列表
     * @return 物种列表和统计
     */
    public suspend fun getSpeciesList(): SpeciesListData {
        val response =
            httpClient.get("$baseUrl/api/proxy/furtv/species")
                .body<SpeciesListResponse>()
        return response.data
    }

    /**
     * 获取热门地区
     * @return 热门地区列表
     */
    public suspend fun getPopularLocations(): PopularLocationsData {
        val response =
            httpClient.get("$baseUrl/api/proxy/furtv/locations/popular")
                .body<PopularLocationsResponse>()
        return response.data
    }
}
