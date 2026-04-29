package com.furrist.rp.furtv.sdk.search

import com.furrist.rp.furtv.sdk.model.RandomFursuitParams
import com.furrist.rp.furtv.sdk.model.SearchParams
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import kotlin.js.JsExport
import kotlin.js.JsName

/** Search and discovery API for popular recommendations, random fursuits, keyword search, and species queries. */
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

    /** Returns random fursuit users based on [params]. Handles both single and multiple result responses. */
    @JsName("getRandomFursuitWithParams")
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

    /** Returns random fursuit users (overload for backward compatibility). */
    @JsName("getRandomFursuit")
    public suspend fun getRandomFursuit(count: Int? = null, personalized: Boolean? = null): List<RandomFursuit> =
        getRandomFursuit(RandomFursuitParams(count, personalized))

    /** Searches users by keyword using [params]. */
    @JsName("searchWithParams")
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

    /** Searches users by keyword (overload for backward compatibility). */
    @JsName("search")
    public suspend fun search(
        query: String,
        type: String? = null,
        cursor: String? = null,
        limit: Int? = null,
        page: Int? = null,
    ): SearchData {
        val response =
            httpClient.get("$baseUrl/api/proxy/furtv/search") {
                parameter("q", query)
                type?.let { parameter("type", it) }
                cursor?.let { parameter("cursor", it) }
                limit?.let { parameter("limit", it) }
                page?.let { parameter("page", it) }
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
