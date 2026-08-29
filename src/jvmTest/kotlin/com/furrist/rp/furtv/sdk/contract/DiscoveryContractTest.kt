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

package com.furrist.rp.furtv.sdk.contract

import com.furrist.rp.furtv.sdk.model.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlinx.serialization.json.Json

class DiscoveryContractTest {
    private val json =
        Json {
            ignoreUnknownKeys = true
            isLenient = true
            coerceInputValues = true
        }

    @Test fun `popular endpoint flat shape`() {
        val data = json.decodeFromString<PopularResponse>(ContractFixture.readFixture("vdsdocs/discovery/popular.json"))
        // vds-docs uses "is_verified":1 (int) but model expects Boolean — gap revealed, not asserted
        assertEquals(1, data.users.size)
        assertEquals(1024, data.users[0].id)
        assertEquals("fox_demo", data.users[0].username)
        // 审计项 #12：popularity_score 在文档中为字符串
        assertEquals("2989", data.users[0].popularityScore)
    }

    @Test fun `search endpoint flat shape with pagination`() {
        val data = json.decodeFromString<SearchResponse>(ContractFixture.readFixture("vdsdocs/discovery/search.json"))
        assertEquals(1, data.users.size)
        assertNotNull(data.pagination)
        assertEquals(1, data.pagination?.page)
        assertEquals("general", data.searchType)
        // 审计项 #10/#11：like_count / is_liked / 顶层 total_is_estimate
        assertEquals(4, data.users[0].likeCount)
        assertEquals(false, data.users[0].isLiked)
        assertEquals(false, data.totalIsEstimate)
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
        // 审计项 #10/#11
        assertEquals(35, data.users[0].likeCount)
        assertEquals(false, data.users[0].isLiked)
        assertEquals(false, data.totalIsEstimate)
    }

    @Test fun `species list flat with total`() {
        val data = json.decodeFromString<SpeciesListResponse>(ContractFixture.readFixture("vdsdocs/discovery/species-list.json"))
        assertEquals(2, data.species.size)
        assertEquals(324, data.total)
    }

    @Test fun `random endpoint flat shape`() {
        // 审计项 #28：返回完整 RandomFursuitResponse（fixture 逐字取自随机推荐.md count=1 示例）
        val data = json.decodeFromString<RandomFursuitResponse>(ContractFixture.readFixture("vdsdocs/discovery/random.json"))
        assertEquals(true, data.success)
        assertEquals("MCbcx", data.fursuit?.username)
        assertEquals(1, data.count)
        assertEquals("93ed4318-b9fe-4316-a3f0-fc489ac5edc7", data.requestId)
    }

    @Test fun `locations flat with provinces and cities`() {
        val data = json.decodeFromString<PopularLocationsResponse>(ContractFixture.readFixture("vdsdocs/discovery/locations.json"))
        assertEquals(2, data.popularProvinces.size)
        assertEquals(2, data.popularCities.size)
    }
}
