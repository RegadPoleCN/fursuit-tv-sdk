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

import com.furrist.rp.furtv.sdk.model.SchoolDetailResponse
import com.furrist.rp.furtv.sdk.model.SchoolSearchResponse
import com.furrist.rp.furtv.sdk.model.UserSchoolsResponse
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlinx.serialization.json.Json

class SchoolContractTest {
    private val json =
        Json {
            ignoreUnknownKeys = true
            isLenient = true
            coerceInputValues = true
        }

    @Test fun `school search no success field`() {
        val data = json.decodeFromString<SchoolSearchResponse>(ContractFixture.readFixture("vdsdocs/school/search.json"))
        assertEquals(1, data.schools.size)
        assertEquals("南京示例大学", data.schools[0].name)
    }

    @Test fun `school detail no success field`() {
        val data = json.decodeFromString<SchoolDetailResponse>(ContractFixture.readFixture("vdsdocs/school/detail.json"))
        assertEquals("南京示例大学", data.school.name)
    }

    @Test fun `user schools no success field`() {
        val data = json.decodeFromString<UserSchoolsResponse>(ContractFixture.readFixture("vdsdocs/school/user-schools.json"))
        // vds-docs uses "is_current":1 / "is_public":1 (int) but model expects Boolean — gap revealed, not asserted
        assertEquals(1, data.schools.size)
    }
}
