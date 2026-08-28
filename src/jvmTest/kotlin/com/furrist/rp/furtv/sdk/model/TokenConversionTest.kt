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

package com.furrist.rp.furtv.sdk.model

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlin.time.Clock

/**
 * `TokenData.toTokenInfo()` 与 `OAuthTokenData.toTokenInfo(redirectUri)` 转换函数测试。
 *
 * - `TokenData.toTokenInfo()` 必须返回 `TokenInfo.Platform`
 * - `OAuthTokenData.toTokenInfo(redirectUri)` 必须返回 `TokenInfo.OAuth`，`oauthToken` 来自 `accessToken` 字段
 * - `expiresAt = now + expiresIn * 1000 - 30_000`（30 秒 SKEW 应用）
 * - `redirectUri` 由调用方传入并存入 `TokenInfo.OAuth.redirectUri`
 */
class TokenConversionTest {
    private fun nowMs(): Long = Clock.System.now().toEpochMilliseconds()

    @Test
    fun tokenData_toTokenInfo_producesPlatform() {
        val source =
            TokenData(
                accessToken = "access-token-value",
                apiKey = "platform-api-key",
                expiresIn = 3600,
                tokenType = "Bearer",
            )

        val before = nowMs()
        val platform = source.toTokenInfo()
        val after = nowMs()

        assertEquals("platform-api-key", platform.apiKey, "apiKey must match server field")
        assertEquals("Bearer", platform.tokenType)
        val expectedMin = before + 3600 * 1000L - 30_000L
        val expectedMax = after + 3600 * 1000L - 30_000L
        assertTrue(
            platform.expiresAt in expectedMin..expectedMax,
            "expiresAt = now + 3600s - 30s skew (got ${platform.expiresAt}, expected $expectedMin..$expectedMax)",
        )
    }

    @Test
    fun tokenData_toTokenInfo_returnTypeIsPlatform() {
        val source =
            TokenData(
                accessToken = "x",
                apiKey = "y",
                expiresIn = 60,
                tokenType = "Bearer",
            )

        val platform = source.toTokenInfo()
        val platformType: TokenInfo.Platform = platform
        assertEquals("y", platformType.apiKey)
    }

    @Test
    fun oauthTokenData_toTokenInfo_producesOAuthWithCorrectFields() {
        val redirectUri = "https://example.com/callback"
        val source =
            OAuthTokenData(
                accessToken = "oauth-access-token",
                expiresIn = 7200,
                tokenType = "Bearer",
                scope = "openid profile",
                refreshToken = "oauth-refresh-token",
            )

        val before = nowMs()
        val oauth = source.toTokenInfo(redirectUri = redirectUri)
        val after = nowMs()

        assertEquals("oauth-access-token", oauth.oauthToken)
        assertEquals("oauth-refresh-token", oauth.refreshToken)
        assertEquals("openid profile", oauth.scope)
        assertEquals(redirectUri, oauth.redirectUri)
        assertEquals("Bearer", oauth.tokenType)
        val expectedMin = before + 7200 * 1000L - 30_000L
        val expectedMax = after + 7200 * 1000L - 30_000L
        assertTrue(
            oauth.expiresAt in expectedMin..expectedMax,
            "expiresAt = now + 7200s - 30s skew (got ${oauth.expiresAt}, expected $expectedMin..$expectedMax)",
        )
    }

    @Test
    fun oauthTokenData_toTokenInfo_nullRefreshToken() {
        val source =
            OAuthTokenData(
                accessToken = "oauth-token",
                expiresIn = 60,
                tokenType = "Bearer",
                scope = null,
                refreshToken = null,
            )

        val oauth = source.toTokenInfo(redirectUri = "https://example.com/cb")
        assertNull(oauth.refreshToken, "refreshToken must be null when server omits it")
        assertNull(oauth.scope, "scope must be null when server omits it")
    }

    @Test
    fun tokenData_toTokenInfo_appliesSkew() {
        val before = System.currentTimeMillis() + 60_000L - 30_000L
        val platform =
            TokenData(
                accessToken = "x",
                apiKey = "k",
                expiresIn = 60,
                tokenType = "Bearer",
            ).toTokenInfo()
        val after = System.currentTimeMillis() + 60_000L - 30_000L
        assertTrue(
            platform.expiresAt in before..after,
            "expiresAt must equal now + 60s - 30s skew",
        )
    }
}
