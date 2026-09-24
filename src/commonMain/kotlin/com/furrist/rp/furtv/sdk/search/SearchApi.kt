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

package com.furrist.rp.furtv.sdk.search

import com.furrist.rp.furtv.sdk.auth.AuthManager
import com.furrist.rp.furtv.sdk.model.*
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import kotlin.js.JsExport
import kotlin.js.JsName
import love.forte.plugin.suspendtrans.annotation.JvmAsync
import love.forte.plugin.suspendtrans.annotation.JvmBlocking

/** 搜索与发现 API，提供热门推荐、随机推荐、关键词搜索、物种检索与热门地区等能力。 */
@JvmBlocking
@JvmAsync
@Suppress("TooManyFunctions")
@JsExport
@JsName("SearchApi")
public class SearchApi internal constructor(
    private val auth: AuthManager,
    private val httpClient: HttpClient,
    private val baseUrl: String = "https://open-global.vdsentnet.com",
) {
    /** 获取热门用户列表（热门推荐.md）。 */
    @JsName("getPopular")
    public suspend fun getPopular(limit: Int? = null): PopularResponse =
        auth.withFreshToken {
            httpClient.get("$baseUrl/api/proxy/furtv/popular") {
                limit?.let { parameter("limit", it) }
            }.body<PopularResponse>()
        }

    /**
     * 获取随机兽装用户列表（随机推荐.md）。
     *
     * @param count 可选的请求数量
     * @return 完整随机推荐响应
     */
    @JsName("getRandomFursuit")
    public suspend fun getRandomFursuit(count: Int? = null): RandomFursuitResponse =
        auth.withFreshToken {
            httpClient.get("$baseUrl/api/proxy/furtv/fursuit/random") {
                count?.let { parameter("count", it) }
            }.body<RandomFursuitResponse>()
        }

    /**
     * [DEPRECATED in v0.5.0] 旧版使用 [RandomFursuitParams] 包装的入口，推荐直接使用展开参数版本 [getRandomFursuit]。
     */
    @Deprecated(
        message = "Use flat parameter getRandomFursuit(count) instead.",
        replaceWith = ReplaceWith("getRandomFursuit(params.count)"),
    )
    @JsName("getRandomFursuitWithParams")
    public suspend fun getRandomFursuit(params: RandomFursuitParams): RandomFursuitResponse =
        getRandomFursuit(params.count)

    /**
     * 按关键词搜索用户（搜索.md）。
     *
     * @param query 搜索关键词
     * @param type 搜索类型（可选）
     * @param cursor 分页游标（可选）
     * @param limit 单页数量上限（可选）
     * @param page 页码（可选）
     * @return 搜索结果响应
     */
    @JsName("search")
    public suspend fun search(
        query: String,
        type: String? = null,
        cursor: String? = null,
        limit: Int? = null,
        page: Int? = null,
    ): SearchResponse =
        auth.withFreshToken {
            httpClient.get("$baseUrl/api/proxy/furtv/search") {
                parameter("q", query)
                type?.let { parameter("type", it) }
                cursor?.let { parameter("cursor", it) }
                limit?.let { parameter("limit", it) }
                page?.let { parameter("page", it) }
            }.body<SearchResponse>()
        }

    /**
     * [DEPRECATED in v0.5.0] 旧版使用 [SearchParams] 包装的入口，推荐直接使用展开参数版本 [search]。
     */
    @Deprecated(
        message = "Use flat parameter search(query, type, cursor, limit, page) instead.",
        replaceWith = ReplaceWith("search(params.query, params.type, params.cursor, params.limit, params.page)"),
    )
    @JsName("searchWithParams")
    public suspend fun search(params: SearchParams): SearchResponse =
        search(
            query = params.query,
            type = params.type,
            cursor = params.cursor,
            limit = params.limit,
            page = params.page,
        )

    /** 获取搜索建议（搜索建议.md）。 */
    @JsName("getSearchSuggestions")
    public suspend fun getSearchSuggestions(query: String): SearchSuggestionsResponse =
        auth.withFreshToken {
            httpClient.get("$baseUrl/api/proxy/furtv/search/suggestions") {
                parameter("q", query)
            }.body<SearchSuggestionsResponse>()
        }

    /** 按物种搜索用户（按物种搜索.md），支持可选分页。 */
    @JsName("searchBySpecies")
    public suspend fun searchBySpecies(
        species: String,
        page: Int? = null,
        limit: Int? = null,
        cursor: String? = null,
    ): SpeciesSearchResponse =
        auth.withFreshToken {
            httpClient.get("$baseUrl/api/proxy/furtv/search/species/$species") {
                page?.let { parameter("page", it) }
                limit?.let { parameter("limit", it) }
                cursor?.let { parameter("cursor", it) }
            }.body<SpeciesSearchResponse>()
        }

    /** 获取全部物种及统计列表（物种列表.md）。 */
    @JsName("getSpeciesList")
    public suspend fun getSpeciesList(): SpeciesListResponse =
        auth.withFreshToken {
            httpClient.get("$baseUrl/api/proxy/furtv/species")
                .body<SpeciesListResponse>()
        }

    /** 获取热门地区（按省份与城市分组）（热门地区.md）。 */
    @JsName("getPopularLocations")
    public suspend fun getPopularLocations(): PopularLocationsResponse =
        auth.withFreshToken {
            httpClient.get("$baseUrl/api/proxy/furtv/locations/popular")
                .body<PopularLocationsResponse>()
        }
}
