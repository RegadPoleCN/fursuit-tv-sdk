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

import com.furrist.rp.furtv.sdk.model.AndroidVersionData
import com.furrist.rp.furtv.sdk.model.AndroidVersionResponse
import com.furrist.rp.furtv.sdk.model.HelloWorldResponse
import com.furrist.rp.furtv.sdk.model.ThemePacksManifestData
import com.furrist.rp.furtv.sdk.model.ThemePacksManifestResponse
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlinx.serialization.json.Json

class BaseContractTest {
    private val json =
        Json {
            ignoreUnknownKeys = true
            isLenient = true
            coerceInputValues = true
        }

    @Test
    fun `hello world response decodes`() {
        val data = json.decodeFromString<HelloWorldResponse>(ContractFixture.readFixture("vdsdocs/base/hello-world.json"))
        assertEquals("helloworld", data.message)
        assertEquals("request_normal", data.verify)
        assertEquals("vap_xxxxxxxxxxxxxxxx", data.appId)
    }

    @Test
    fun `android version response decodes`() {
        val wrapper = json.decodeFromString<AndroidVersionResponse>(ContractFixture.readFixture("vdsdocs/base/android-version.json"))
        val data: AndroidVersionData = wrapper.data
        assertEquals("2.4.1", data.version)
        assertEquals(241, data.versionCode)
        assertEquals("2.2.0", data.minSupportedVersion)
        assertEquals(2, data.changelog.size)
    }

    @Test
    fun `theme packs manifest decodes with nested metadata`() {
        val wrapper = json.decodeFromString<ThemePacksManifestResponse>(ContractFixture.readFixture("vdsdocs/base/theme-packs.json"))
        val data: ThemePacksManifestData = wrapper.data
        assertEquals("2026-03-28T00:00:00Z", data.updatedAt)
        assertEquals(1, data.themes.size)
        val theme = data.themes[0]
        assertEquals("moyufur", theme.id)
        assertEquals("百变墨煜", theme.metadata?.name)
        assertEquals("GeorgeBai", theme.metadata?.author?.username)
    }
}
