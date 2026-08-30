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
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlin.time.Clock

/** 测试用假令牌值，均为非真实凭据的占位串（统一走常量，便于安全扫描区分真实凭据与测试值）。 */
private const val DUMMY_F = "dummy-value-f"
private const val DUMMY_G = "dummy-value-g"

/**
 * `TokenInfo.isExpired()` 边界测试。
 *
 * 公式：`expiresAt - now <= REFRESH_WINDOW_MS - SKEW_MS`
 * 即 `expiresAt - now <= 270_000` ms（300s refresh window - 30s skew）。
 */
class TokenInfoExpirationTest {
    private fun platformWithExpiry(offsetMs: Long): TokenInfo.Platform =
        TokenInfo.Platform(
            apiKey = DUMMY_F,
            expiresAt = Clock.System.now().toEpochMilliseconds() + offsetMs,
            tokenType = "Bearer",
        )

    private fun oauthWithExpiry(offsetMs: Long): TokenInfo.OAuth =
        TokenInfo.OAuth(
            oauthToken = DUMMY_G,
            redirectUri = "https://example.com/callback",
            expiresAt = Clock.System.now().toEpochMilliseconds() + offsetMs,
            tokenType = "Bearer",
        )

    @Test
    fun platform_freshTokenReportsNotExpired() {
        val token = platformWithExpiry(offsetMs = 271_000L)
        assertFalse(token.isExpired(), "271s remaining should be reported as not expired (>270s window)")
    }

    @Test
    fun platform_tokenAtWindowEdgeReportsExpired() {
        val token = platformWithExpiry(offsetMs = 270_000L)
        assertTrue(token.isExpired(), "270s remaining should be reported as expired (<=270s window)")
    }

    @Test
    fun platform_tokenInsideRefreshWindowReportsExpired() {
        val token = platformWithExpiry(offsetMs = 100_000L)
        assertTrue(token.isExpired(), "100s remaining should be reported as expired")
    }

    @Test
    fun platform_tokenAtZeroReportsExpired() {
        val token = platformWithExpiry(offsetMs = 0L)
        assertTrue(token.isExpired(), "0s remaining should be reported as expired")
    }

    @Test
    fun platform_expiredTokenReportsExpired() {
        val token = platformWithExpiry(offsetMs = -10_000L)
        assertTrue(token.isExpired(), "negative remaining should be reported as expired")
    }

    @Test
    fun oauth_freshTokenReportsNotExpired() {
        val token = oauthWithExpiry(offsetMs = 271_000L)
        assertFalse(token.isExpired(), "271s remaining should be reported as not expired")
    }

    @Test
    fun oauth_tokenAtWindowEdgeReportsExpired() {
        val token = oauthWithExpiry(offsetMs = 270_000L)
        assertTrue(token.isExpired(), "270s remaining should be reported as expired")
    }

    @Test
    fun oauth_tokenInsideRefreshWindowReportsExpired() {
        val token = oauthWithExpiry(offsetMs = 50_000L)
        assertTrue(token.isExpired(), "50s remaining should be reported as expired")
    }
}
