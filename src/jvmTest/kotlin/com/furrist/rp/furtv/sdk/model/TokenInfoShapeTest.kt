package com.furrist.rp.furtv.sdk.model

import kotlin.test.Test
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull

/**
 * `TokenInfo` sealed class 属性隔离测试。
 *
 * - `TokenInfo.Platform` 不暴露 OAuth 字段（`oauthToken`、`refreshToken`、`scope`、`redirectUri`）
 * - `TokenInfo.OAuth` 不暴露 platform 字段（`apiKey`）
 */
class TokenInfoShapeTest {
    private fun platform(extra: Map<String, Any?> = emptyMap()): TokenInfo.Platform =
        TokenInfo.Platform(
            apiKey = "test",
            expiresAt = 1L,
            tokenType = "Bearer",
        )

    @Test
    fun platform_doesNotExposeOauthTokenProperty() {
        val token: Any = platform()
        // 编译期：以下访问必须失败（OAuth 字段不在 Platform 上）
        val cls = token::class
        val oauthField = cls.members.firstOrNull { it.name == "oauthToken" }
        assertNotNull(oauthField == null, "TokenInfo.Platform should not expose 'oauthToken'")
    }

    @Test
    fun platform_doesNotExposeRefreshTokenProperty() {
        val token: Any = platform()
        val cls = token::class
        val refreshField = cls.members.firstOrNull { it.name == "refreshToken" }
        assertNotNull(refreshField == null, "TokenInfo.Platform should not expose 'refreshToken'")
    }

    @Test
    fun platform_doesNotExposeScopeProperty() {
        val token: Any = platform()
        val cls = token::class
        val scopeField = cls.members.firstOrNull { it.name == "scope" }
        assertNotNull(scopeField == null, "TokenInfo.Platform should not expose 'scope'")
    }

    @Test
    fun platform_doesNotExposeRedirectUriProperty() {
        val token: Any = platform()
        val cls = token::class
        val redirectField = cls.members.firstOrNull { it.name == "redirectUri" }
        assertNotNull(redirectField == null, "TokenInfo.Platform should not expose 'redirectUri'")
    }

    @Test
    fun platform_hasApiKeyProperty() {
        val token: Any = platform()
        val cls = token::class
        val apiKeyField = cls.members.firstOrNull { it.name == "apiKey" }
        assertNotNull(apiKeyField, "TokenInfo.Platform must expose 'apiKey'")
    }

    @Test
    fun oauth_doesNotExposeApiKeyProperty() {
        val token: Any = TokenInfo.OAuth(
            oauthToken = "x",
            refreshToken = null,
            scope = null,
            redirectUri = "https://example.com/cb",
            expiresAt = 1L,
            tokenType = "Bearer",
        )
        val cls = token::class
        val apiKeyField = cls.members.firstOrNull { it.name == "apiKey" }
        assertNotNull(apiKeyField == null, "TokenInfo.OAuth should not expose 'apiKey'")
    }

    @Test
    fun oauth_hasOauthTokenAndRedirectUriProperties() {
        val token: Any = TokenInfo.OAuth(
            oauthToken = "x",
            refreshToken = null,
            scope = null,
            redirectUri = "https://example.com/cb",
            expiresAt = 1L,
            tokenType = "Bearer",
        )
        val cls = token::class
        val oauthTokenField = cls.members.firstOrNull { it.name == "oauthToken" }
        val redirectField = cls.members.firstOrNull { it.name == "redirectUri" }
        assertNotNull(oauthTokenField, "TokenInfo.OAuth must expose 'oauthToken'")
        assertNotNull(redirectField, "TokenInfo.OAuth must expose 'redirectUri'")
    }
}
