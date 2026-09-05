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
import kotlin.test.assertTrue
import kotlinx.serialization.json.Json

class BaseContractTest {
    private val json =
        Json {
            ignoreUnknownKeys = true
            isLenient = true
        }

    @Test
    fun `android version check request requires currentVersionCode`() {
        // 文档示例恒携带 currentVersionCode，请求体字段改必填
        val encoded =
            json.encodeToString(
                AndroidVersionCheckRequest.serializer(),
                AndroidVersionCheckRequest("1.2.3", 241),
            )
        assertTrue(encoded.contains("\"currentVersionCode\":241"))
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

    @Test
    fun `health response decodes`() {
        // health 契约测试（fixture 逐字取自健康检查.md 成功示例；诊断字段有意不建模）
        val data = json.decodeFromString<HealthResponse>(ContractFixture.readFixture("vdsdocs/base/health.json"))
        assertEquals(true, data.success)
        assertEquals("Fursuit.TV API is running", data.message)
        assertEquals("23369f3f-f48e-48f3-85ee-6f7c2c241d9e", data.requestId)
    }

    @Test
    fun `android version check response decodes`() {
        // version-check 端点契约测试（fixture 逐字取自安卓版本检查.md 成功示例）
        val wrapper =
            json.decodeFromString<AndroidVersionCheckResponse>(
                ContractFixture.readFixture("vdsdocs/base/android-version-check.json"),
            )
        assertEquals(true, wrapper.success)
        assertEquals(true, wrapper.data.needUpdate)
        assertEquals(false, wrapper.data.forceUpdate)
        assertEquals("ef739ad8-3072-4df2-a1f5-a0cf6578bfd0", wrapper.requestId)
    }
}
