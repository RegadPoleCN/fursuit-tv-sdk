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

class GatheringContractTest {
    private val json =
        Json {
            ignoreUnknownKeys = true
            isLenient = true
        }

    @Test fun `year stats flat with total`() {
        val data = json.decodeFromString<GatheringYearStatsResponse>(ContractFixture.readFixture("vdsdocs/gathering/year-stats.json"))
        assertEquals(127, data.total)
    }

    @Test fun `monthly object type with year month gatherings total`() {
        val wrapper = json.decodeFromString<GatheringMonthlyResponse>(ContractFixture.readFixture("vdsdocs/gathering/monthly.json"))
        val data: GatheringMonthlyData = wrapper.data
        assertEquals(2026, data.year)
        assertEquals(4, data.month)
        assertEquals(2, data.gatherings.size)
        assertEquals(2, data.total)
    }

    @Test fun `monthly distance object type`() {
        val wrapper =
            json.decodeFromString<GatheringMonthlyDistanceResponse>(
                ContractFixture.readFixture("vdsdocs/gathering/monthly-distance.json"),
            )
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
        // nearby-mode 元素类型为 GatheringNearbyModeItem
        assertEquals("https://example.com/gathering-logo.jpg", data.data.gatherings[0].avatarUrl)
        assertEquals(0, data.data.gatherings[0].currentParticipants)
        assertEquals(null, data.data.gatherings[0].maxParticipants)
    }

    @Test fun `detail uses gathering field not data`() {
        val data = json.decodeFromString<GatheringDetailResponse>(ContractFixture.readFixture("vdsdocs/gathering/detail.json"))
        // vds-docs 在部分布尔字段使用 0/1 整数，模型经 BooleanAsIntSerializer 兼容两者
        assertEquals(1001, data.gathering.id)
        assertEquals("南京兽聚", data.gathering.title)
        // 聚会详情补齐字段
        assertEquals("official", data.gathering.contentSource)
        assertEquals("system", data.gathering.organizerType)
        assertEquals("2026-02-16T00:00:00.000Z", data.gathering.createdAt)
        assertNotNull(data.gathering.rawPayload)
        assertEquals(false, data.gathering.isFurtvCoopBadgeEnabled)
    }

    @Test fun `registrations uses registrations field not data`() {
        val data =
            json.decodeFromString<GatheringRegistrationsResponse>(
                ContractFixture.readFixture("vdsdocs/gathering/registrations.json"),
            )
        assertEquals(2, data.registrations.size)
    }
}
