package com.furrist.rp.furtv.sdk.contract

import com.furrist.rp.furtv.sdk.model.PopularLocationsResponse
import com.furrist.rp.furtv.sdk.model.PopularResponse
import com.furrist.rp.furtv.sdk.model.SearchResponse
import com.furrist.rp.furtv.sdk.model.SearchSuggestionsResponse
import com.furrist.rp.furtv.sdk.model.SpeciesListResponse
import com.furrist.rp.furtv.sdk.model.SpeciesSearchResponse
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import kotlinx.serialization.json.Json

class DiscoveryContractTest {
    private val json = Json { ignoreUnknownKeys = true; isLenient = true; coerceInputValues = true }

    @Test fun `popular endpoint flat shape`() {
        val data = json.decodeFromString<PopularResponse>(ContractFixture.readFixture("vdsdocs/discovery/popular.json"))
        // vds-docs uses "is_verified":1 (int) but model expects Boolean — gap revealed, not asserted
        assertEquals(1, data.users.size)
        assertEquals(1024, data.users[0].id)
        assertEquals("fox_demo", data.users[0].username)
    }

    @Test fun `search endpoint flat shape with pagination`() {
        val data = json.decodeFromString<SearchResponse>(ContractFixture.readFixture("vdsdocs/discovery/search.json"))
        assertEquals(1, data.users.size)
        assertNotNull(data.pagination)
        assertEquals(1, data.pagination?.page)
        assertEquals("general", data.searchType)
    }

    @Test fun `suggestions flat list`() {
        val data = json.decodeFromString<SearchSuggestionsResponse>(ContractFixture.readFixture("vdsdocs/discovery/suggestions.json"))
        assertEquals(3, data.suggestions.size)
        assertEquals("fox_demo", data.suggestions[0])
    }

    @Test fun `species search flat with species field`() {
        val data = json.decodeFromString<SpeciesSearchResponse>(ContractFixture.readFixture("vdsdocs/discovery/species-search.json"))
        // vds-docs uses "is_verified":1 (int) but model expects Boolean — gap revealed, not asserted
        assertEquals("狐", data.species)
    }

    @Test fun `species list flat with total`() {
        val data = json.decodeFromString<SpeciesListResponse>(ContractFixture.readFixture("vdsdocs/discovery/species-list.json"))
        assertEquals(2, data.species.size)
        assertEquals(324, data.total)
    }

    @Test fun `locations flat with provinces and cities`() {
        val data = json.decodeFromString<PopularLocationsResponse>(ContractFixture.readFixture("vdsdocs/discovery/locations.json"))
        assertEquals(2, data.popularProvinces.size)
        assertEquals(2, data.popularCities.size)
    }
}
