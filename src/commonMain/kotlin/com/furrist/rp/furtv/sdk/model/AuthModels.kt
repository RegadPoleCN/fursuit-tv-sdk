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

import kotlin.js.JsExport
import kotlin.js.JsName
import kotlin.time.Clock
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * 签名交换请求，用于获取 apiKey/accessToken。
 */
@JsExport
@JsName("TokenExchangeRequest")
@Serializable
public data class TokenExchangeRequest(
    @SerialName("clientId")
    public val clientId: String,
    @SerialName("clientSecret")
    public val clientSecret: String,
)

/** 令牌刷新信息。 */
@JsExport
@JsName("TokenRefreshInfo")
@Serializable
public data class TokenRefreshInfo(
    @SerialName("mode")
    public val mode: String,
    @SerialName("refreshWindowSeconds")
    public val refreshWindowSeconds: Int,
    @SerialName("previousTokenSecondsRemaining")
    public val previousTokenSecondsRemaining: Int,
)

/**
 * 令牌数据，包含访问令牌信息（签名交换接口返回）。
 */
@JsExport
@JsName("TokenData")
@Serializable
public data class TokenData(
    public val accessToken: String,
    public val apiKey: String,
    @SerialName("expiresInSeconds")
    public val expiresIn: Int,
    public val tokenType: String,
    @SerialName("appId")
    public val appId: String? = null,
    @SerialName("grants")
    public val grants: List<String>? = null,
    @SerialName("refresh")
    public val refresh: TokenRefreshInfo? = null,
    public val requestId: String? = null,
)

/**
 * OAuth 2.0 授权码流程 + 回调服务器配置。
 */
@JsExport
@JsName("OAuthConfig")
@Serializable
public data class OAuthConfig(
    public val callbackHost: String = DEFAULT_CALLBACK_HOST,
    public val callbackPort: Int = DEFAULT_CALLBACK_PORT,
    public val callbackPath: String = DEFAULT_CALLBACK_PATH,
    public val timeoutSeconds: Int = DEFAULT_TIMEOUT_SECONDS,
) {
    init {
        require(callbackPort in 1..MAX_PORT_NUMBER) {
            "callbackPort must be between 1 and $MAX_PORT_NUMBER"
        }
        require(callbackPath.startsWith("/")) { "callbackPath must start with '/'" }
        require(timeoutSeconds > 0) { "timeoutSeconds must be positive" }
        require(callbackHost.isNotBlank()) { "callbackHost must not be blank" }
    }

    public companion object {
        public const val DEFAULT_CALLBACK_HOST: String = "localhost"
        public const val DEFAULT_CALLBACK_PORT: Int = 8080
        public const val DEFAULT_CALLBACK_PATH: String = "/callback"
        public const val DEFAULT_TIMEOUT_SECONDS: Int = 300
        private const val MAX_PORT_NUMBER = 65535
    }
}

/** OAuth 令牌数据。 */
@JsExport
@JsName("OAuthTokenData")
@Serializable
public data class OAuthTokenData(
    @SerialName("access_token")
    public val accessToken: String,
    @SerialName("expires_in")
    public val expiresIn: Int,
    @SerialName("token_type")
    public val tokenType: String,
    public val scope: String? = null,
    @SerialName("refresh_token")
    public val refreshToken: String? = null,
    public val requestId: String? = null,
)

/** 用户信息数据。 */
@JsExport
@JsName("UserInfoData")
@Serializable
public data class UserInfoData(
    public val sub: String,
    public val nickname: String? = null,
    @SerialName("avatar_url")
    public val avatarUrl: String? = null,
    public val email: String? = null,
    public val name: String? = null,
    public val username: String? = null,
    @SerialName("updated_at")
    public val updatedAt: Long? = null,
    @SerialName("phone_number")
    public val phoneNumber: String? = null,
    @SerialName("iss")
    public val iss: String? = null,
    @SerialName("aud")
    public val aud: Long? = null,
    public val requestId: String? = null,
)

/** 令牌信息，SDK 内部使用的令牌存储结构（sealed class）。 */
@JsExport
@JsName("TokenInfo")
@Serializable
public sealed class TokenInfo {
    public abstract val expiresAt: Long
    public abstract val tokenType: String

    @JsName("isExpired")
    public fun isExpired(): Boolean {
        val now = Clock.System.now().toEpochMilliseconds()
        return (expiresAt - now) <= REFRESH_WINDOW_MS - SKEW_MS
    }

    internal companion object {
        internal const val REFRESH_WINDOW_MS: Long = 300_000L
        internal const val SKEW_MS: Long = 30_000L
    }

    /** 平台签名，来自签名交换端点。 */
    @Serializable
    public data class Platform(
        public val apiKey: String,
        public override val expiresAt: Long,
        public override val tokenType: String,
    ) : TokenInfo()

    /** OAuth 用户令牌，来自 OAuth 授权码流程。 */
    @Serializable
    public data class OAuth(
        public val oauthToken: String,
        public val refreshToken: String? = null,
        public val scope: String? = null,
        public val redirectUri: String,
        public override val expiresAt: Long,
        public override val tokenType: String,
    ) : TokenInfo()
}

/** 将签名交换令牌数据转换为 TokenInfo.Platform。 */
@JsExport
@JsName("toTokenInfo")
public fun TokenData.toTokenInfo(): TokenInfo.Platform =
    TokenInfo.Platform(
        apiKey = apiKey,
        expiresAt = Clock.System.now().toEpochMilliseconds() + expiresIn * 1000L - TokenInfo.SKEW_MS,
        tokenType = tokenType,
    )

/** 将 OAuth 令牌数据转换为 TokenInfo.OAuth。 */
@JsExport
@JsName("toTokenInfoFromOAuth")
public fun OAuthTokenData.toTokenInfo(redirectUri: String): TokenInfo.OAuth =
    TokenInfo.OAuth(
        oauthToken = accessToken,
        refreshToken = refreshToken,
        scope = scope,
        redirectUri = redirectUri,
        expiresAt = Clock.System.now().toEpochMilliseconds() + expiresIn * 1000L - TokenInfo.SKEW_MS,
        tokenType = tokenType,
    )
