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

import com.furrist.rp.furtv.sdk.model.OAuthTokenData
import com.furrist.rp.furtv.sdk.model.TokenData
import com.furrist.rp.furtv.sdk.model.UserInfoData
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlinx.serialization.json.Json

class AuthContractTest {
    private val json =
        Json {
            ignoreUnknownKeys = true
            isLenient = true
        }

    @Test
    fun `token exchange response decodes`() {
        val data = json.decodeFromString<TokenData>(ContractFixture.readFixture("vdsdocs/auth/token-exchange.json"))
        assertNotNull(data.accessToken)
        assertNotNull(data.apiKey)
        assertEquals("Bearer", data.tokenType)
        assertEquals(3600, data.expiresIn)
        assertEquals("vap_xxxxxxxxxxxxxxxx", data.appId)
        assertEquals(listOf("furtv", "furtv.gathering.timeline"), data.grants)
        // 签名交换响应含 requestId
        assertEquals("6ff8d966-b3f6-46a6-9fe3-24fd6553ef52", data.requestId)
    }

    @Test
    fun `oauth token response decodes with snake_case fields`() {
        val data = json.decodeFromString<OAuthTokenData>(ContractFixture.readFixture("vdsdocs/auth/oauth-token.json"))
        assertNotNull(data.accessToken)
        assertEquals("Bearer", data.tokenType)
        assertEquals(31556952, data.expiresIn)
        assertNotNull(data.refreshToken)
        assertEquals("openid profile", data.scope)
        // OAuth token 响应含 requestId
        assertEquals("435e1d38-e4b6-4224-a629-8927b81c96cc", data.requestId)
    }

    @Test
    fun `userinfo response decodes flat`() {
        val data = json.decodeFromString<UserInfoData>(ContractFixture.readFixture("vdsdocs/auth/oauth-userinfo.json"))
        assertEquals("10001", data.sub)
        assertEquals("示例用户", data.nickname)
        assertEquals("example_user", data.username)
        assertEquals(1774002667L, data.updatedAt)
        assertEquals(35L, data.aud)
        // userinfo 响应含 requestId
        assertEquals("ab7747a5-9601-49ad-ba58-9111592dc3b0", data.requestId)
    }

    @Test
    fun `token refresh response decodes`() {
        // token-refresh.json 此前无任何测试引用，补上契约测试（签名换新.md 响应结构）
        val data = json.decodeFromString<TokenData>(ContractFixture.readFixture("vdsdocs/auth/token-refresh.json"))
        assertEquals("Bearer", data.tokenType)
        assertEquals(7200, data.expiresIn)
        assertEquals("vap_test_app_id", data.appId)
        assertEquals("expiring", data.refresh?.mode)
        assertEquals(300, data.refresh?.refreshWindowSeconds)
    }
}
