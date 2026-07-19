package com.furrist.rp.furtv.sdk.search

import com.furrist.rp.furtv.sdk.model.PopularData
import com.furrist.rp.furtv.sdk.model.PopularLocationsData
import com.furrist.rp.furtv.sdk.model.PopularLocationsResponse
import com.furrist.rp.furtv.sdk.model.PopularResponse
import com.furrist.rp.furtv.sdk.model.RandomFursuit
import com.furrist.rp.furtv.sdk.model.RandomFursuitParams
import com.furrist.rp.furtv.sdk.model.RandomFursuitResponse
import com.furrist.rp.furtv.sdk.model.SearchData
import com.furrist.rp.furtv.sdk.model.SearchParams
import com.furrist.rp.furtv.sdk.model.SearchResponse
import com.furrist.rp.furtv.sdk.model.SearchSuggestionsResponse
import com.furrist.rp.furtv.sdk.model.SpeciesListData
import com.furrist.rp.furtv.sdk.model.SpeciesListResponse
import com.furrist.rp.furtv.sdk.model.SpeciesSearchData
import com.furrist.rp.furtv.sdk.model.SpeciesSearchResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import kotlin.js.JsExport
import kotlin.js.JsName
import love.forte.plugin.suspendtrans.annotation.JvmAsync
import love.forte.plugin.suspendtrans.annotation.JvmBlocking

/** Search and discovery API for popular recommendations, random fursuits, keyword search, and species queries. */
@JvmBlocking
@JvmAsync
@Suppress("TooManyFunctions")
@JsExport
@JsName("SearchApi")
public class SearchApi internal constructor(
    private val httpClient: HttpClient,
    private val baseUrl: String = "https://open-global.vdsentnet.com",
) {
    /** Returns popular users, optionally limited by [limit]. */
    @JsName("getPopular")
    public suspend fun getPopular(limit: Int? = null): PopularData {
        val response =
            httpClient.get("$baseUrl/api/proxy/furtv/popular") {
                limit?.let { parameter("limit", it) }
            }.body<PopularResponse>()
        return response.data
    }

    /**
     * 获取随机兽装用户列表。
     *
     * @param params 随机推荐参数
     * @return 随机兽装用户列表
     */
    @JsName("getRandomFursuit")
    public suspend fun getRandomFursuit(params: RandomFursuitParams): List<RandomFursuit> {
        val response =
            httpClient.get("$baseUrl/api/proxy/furtv/fursuit/random") {
                params.count?.let { parameter("count", it) }
                params.personalized?.let { parameter("personalized", it) }
            }.body<RandomFursuitResponse>()
        return when {
            response.fursuits != null -> response.fursuits
            response.fursuit != null -> listOf(response.fursuit)
            else -> emptyList()
        }
    }

    /**
     * 关键词搜索用户。
     *
     * @param params 搜索参数
     * @return 搜索结果
     */
    @JsName("search")
    public suspend fun search(params: SearchParams): SearchData {
        val response =
            httpClient.get("$baseUrl/api/proxy/furtv/search") {
                parameter("q", params.query)
                params.type?.let { parameter("type", it) }
                params.cursor?.let { parameter("cursor", it) }
                params.limit?.let { parameter("limit", it) }
                params.page?.let { parameter("page", it) }
            }.body<SearchResponse>()
        return response.data
    }

    /** Returns search suggestions for the given [query]. */
    @JsName("getSearchSuggestions")
    public suspend fun getSearchSuggestions(query: String): List<String> {
        val response =
            httpClient.get("$baseUrl/api/proxy/furtv/search/suggestions") {
                parameter("q", query)
            }.body<SearchSuggestionsResponse>()
        return response.data.suggestions
    }

    /** Searches users by [species] with optional pagination. */
    @JsName("searchBySpecies")
    public suspend fun searchBySpecies(
        species: String,
        page: Int? = null,
        limit: Int? = null,
        cursor: String? = null,
    ): SpeciesSearchData {
        val response =
            httpClient.get("$baseUrl/api/proxy/furtv/search/species/$species") {
                page?.let { parameter("page", it) }
                limit?.let { parameter("limit", it) }
                cursor?.let { parameter("cursor", it) }
            }.body<SpeciesSearchResponse>()
        return response.data
    }

    /** Returns the list of all species with statistics. */
    @JsName("getSpeciesList")
    public suspend fun getSpeciesList(): SpeciesListData {
        val response =
            httpClient.get("$baseUrl/api/proxy/furtv/species")
                .body<SpeciesListResponse>()
        return response.data
    }

    /** Returns popular locations structured by provinces and cities. */
    @JsName("getPopularLocations")
    public suspend fun getPopularLocations(): PopularLocationsData {
        val response =
            httpClient.get("$baseUrl/api/proxy/furtv/locations/popular")
                .body<PopularLocationsResponse>()
        return response.data
    }
}
