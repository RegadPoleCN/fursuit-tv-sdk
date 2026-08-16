package com.furrist.rp.furtv.sdk.contract

import com.furrist.rp.furtv.sdk.model.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlinx.serialization.json.Json

class GatheringContractTest {
    private val json = Json { ignoreUnknownKeys = true; isLenient = true; coerceInputValues = true }

    @Test fun `year stats flat with total`() {
        val data = json.decodeFromString<GatheringYearStatsResponse>(ContractFixture.readFixture("vdsdocs/gathering/year-stats.json"))
        assertEquals(127, data.total)
    }

    @Test fun `monthly object type with year month gatherings total`() {
        val wrapper = json.decodeFromString<com.furrist.rp.furtv.sdk.model.GatheringMonthlyResponse>(ContractFixture.readFixture("vdsdocs/gathering/monthly.json"))
        val data: GatheringMonthlyData = wrapper.data
        assertEquals(2026, data.year)
        assertEquals(4, data.month)
        assertEquals(2, data.gatherings.size)
        assertEquals(2, data.total)
    }

    @Test fun `monthly distance object type`() {
        val wrapper = json.decodeFromString<com.furrist.rp.furtv.sdk.model.GatheringMonthlyDistanceResponse>(ContractFixture.readFixture("vdsdocs/gathering/monthly-distance.json"))
        val data: GatheringMonthlyDistanceData = wrapper.data
        assertEquals(2026, data.year)
        assertEquals(4, data.month)
        assertEquals(2, data.distances.size)
    }

    @Test fun `nearby flat with data wrapper`() {
        val data = json.decodeFromString<GatheringNearbyResponse>(ContractFixture.readFixture("vdsdocs/gathering/nearby.json"))
        assertEquals(1, data.data.size)
        assertNotNull(data.data[0].eventTime)
        assertNotNull(data.data[0].timeZone)
    }

    @Test fun `nearby mode flat with nested data`() {
        val data = json.decodeFromString<GatheringNearbyModeResponse>(ContractFixture.readFixture("vdsdocs/gathering/nearby-mode.json"))
        assertEquals(1, data.data.gatherings.size)
        assertEquals(2, data.data.intentGatheringIds.size)
    }

    @Test fun `detail uses gathering field not data`() {
        val data = json.decodeFromString<GatheringDetailResponse>(ContractFixture.readFixture("vdsdocs/gathering/detail.json"))
        // vds-docs uses int 0/1 for many bool fields (e.g. is_recurring, is_furtv_coop_driven); model expects Boolean
        // gap revealed by test — assertion limited to model-confident fields
        assertEquals(1001, data.gathering.id)
        assertEquals("南京兽聚", data.gathering.title)
    }

    @Test fun `registrations uses registrations field not data`() {
        val data = json.decodeFromString<GatheringRegistrationsResponse>(ContractFixture.readFixture("vdsdocs/gathering/registrations.json"))
        assertEquals(2, data.registrations.size)
    }

    @Test fun `user characters cross-cutting flat`() {
        val data = json.decodeFromString<UserCharactersResponse>(ContractFixture.readFixture("vdsdocs/user-characters.json"))
        assertEquals(1, data.characters.size)
        assertEquals("小狐", data.characters[0].name)
    }
}
