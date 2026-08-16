package com.furrist.rp.furtv.sdk.contract

import com.furrist.rp.furtv.sdk.model.OAuthTokenData
import com.furrist.rp.furtv.sdk.model.TokenData
import com.furrist.rp.furtv.sdk.model.UserInfoData
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlinx.serialization.json.Json

class AuthContractTest {
    private val json = Json { ignoreUnknownKeys = true; isLenient = true; coerceInputValues = true }

    @Test
    fun `token exchange response decodes`() {
        val data = json.decodeFromString<TokenData>(ContractFixture.readFixture("vdsdocs/auth/token-exchange.json"))
        assertNotNull(data.accessToken)
        assertNotNull(data.apiKey)
        assertEquals("Bearer", data.tokenType)
        assertEquals(3600, data.expiresIn)
        assertEquals("vap_xxxxxxxxxxxxxxxx", data.appId)
        assertEquals(listOf("furtv", "furtv.gathering.timeline"), data.grants)
    }

    @Test
    fun `oauth token response decodes with snake_case fields`() {
        val data = json.decodeFromString<OAuthTokenData>(ContractFixture.readFixture("vdsdocs/auth/oauth-token.json"))
        assertNotNull(data.accessToken)
        assertEquals("Bearer", data.tokenType)
        assertEquals(31556952, data.expiresIn)
        assertNotNull(data.refreshToken)
        assertEquals("openid profile", data.scope)
    }

    @Test
    fun `userinfo response decodes flat`() {
        val data = json.decodeFromString<UserInfoData>(ContractFixture.readFixture("vdsdocs/auth/oauth-userinfo.json"))
        assertEquals("10001", data.sub)
        assertEquals("示例用户", data.nickname)
        assertEquals("example_user", data.username)
        assertEquals(1774002667L, data.updatedAt)
        assertEquals(35L, data.aud)
    }
}
