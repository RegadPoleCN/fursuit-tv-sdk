package com.furrist.rp.furtv.sdk.model

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlin.time.Clock

/**
 * `TokenInfo.isExpired()` 边界测试。
 *
 * 公式：`expiresAt - now <= REFRESH_WINDOW_MS - SKEW_MS`
 * 即 `expiresAt - now <= 270_000` ms（300s refresh window - 30s skew）。
 */
class TokenInfoExpirationTest {
    private fun platformWithExpiry(offsetMs: Long): TokenInfo.Platform =
        TokenInfo.Platform(
            apiKey = "test-api-key",
            expiresAt = Clock.System.now().toEpochMilliseconds() + offsetMs,
            tokenType = "Bearer",
        )

    private fun oauthWithExpiry(offsetMs: Long): TokenInfo.OAuth =
        TokenInfo.OAuth(
            oauthToken = "test-oauth-token",
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
