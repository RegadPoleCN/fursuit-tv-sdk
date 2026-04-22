package com.furrist.rp.furtv.sdk.auth

import kotlinx.datetime.Clock
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * 签名交换请求，用于获取 apiKey/accessToken。
 *
 * @param clientId 应用 ID（格式 vap_xxxx），与 appId 等价
 * @param clientSecret 应用密钥
 */
@Serializable
public data class TokenExchangeRequest(
    @SerialName("clientId")
    public val clientId: String,
    @SerialName("clientSecret")
    public val clientSecret: String,
)

/**
 * 令牌交换响应，签名交换接口的响应包装。
 *
 * @param success 请求是否成功
 * @param data 令牌数据
 * @param requestId 请求 ID，用于日志排查
 */
@Serializable
public data class TokenExchangeResponse(
    public val success: Boolean,
    public val data: TokenData,
    public val requestId: String,
)

/**
 * 令牌刷新信息，包含签名换新策略的相关信息。
 *
 * @param mode 刷新模式
 * @param refreshWindowSeconds 刷新窗口时间（秒）
 * @param previousTokenSecondsRemaining 旧令牌剩余有效期（秒）
 */
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
 *
 * @param accessToken 访问令牌，用于 Authorization: Bearer 认证头
 * @param apiKey API 密钥，用于 X-Api-Key 认证头
 * @param expiresIn 令牌有效期（秒）
 * @param tokenType 令牌类型，通常为 "Bearer"
 * @param refresh 可选的令牌刷新信息
 */
@Serializable
public data class TokenData(
    public val accessToken: String,
    public val apiKey: String,
    @SerialName("expiresInSeconds")
    public val expiresIn: Int,
    public val tokenType: String,
    @SerialName("refresh")
    public val refresh: TokenRefreshInfo? = null,
)

/**
 * 令牌刷新请求，用于令牌刷新接口。
 * 刷新接口不需要请求体，只需要 Authorization header。
 */
@Serializable
public data class TokenRefreshRequest(
    // 占位符，实际不会发送
    val dummy: String? = null,
)

/**
 * 令牌刷新响应，令牌刷新接口的响应包装。
 *
 * @param success 请求是否成功
 * @param data 新的令牌数据
 * @param requestId 请求 ID
 * @param refresh 可选的令牌刷新信息
 */
@Serializable
public data class TokenRefreshResponse(
    public val success: Boolean,
    public val data: TokenData,
    public val requestId: String,
    @SerialName("refresh")
    public val refresh: TokenRefreshInfo? = null,
)

/**
 * OAuth 授权 URL 参数，用于生成 OAuth 授权 URL。
 *
 * @param clientId 应用 ID（格式 vap_xxxx）
 * @param redirectUri 授权后重定向 URI
 * @param state 可选的状态参数，用于防止 CSRF 攻击
 * @param scope 可选的权限范围
 * @param responseType 响应类型，默认为 "code"
 * @param codeChallenge 可选的 PKCE code_challenge
 * @param codeChallengeMethod 可选的 PKCE code_challenge_method，默认为 "SHA256"
 */
@Serializable
public data class OAuthAuthorizeParams(
    public val clientId: String,
    public val redirectUri: String,
    public val state: String? = null,
    public val scope: String? = null,
    public val responseType: String = "code",
    @SerialName("code_challenge")
    public val codeChallenge: String? = null,
    @SerialName("code_challenge_method")
    public val codeChallengeMethod: String? = null,
)

/**
 * OAuth 配置，用于配置 OAuth 回调参数。
 *
 * @param callbackHost 回调主机，默认为 "localhost"
 * @param callbackPort 回调端口，默认为 8080
 * @param callbackPath 回调路径，默认为 "/callback"
 * @param stateTimeoutMinutes 状态超时时间（分钟），默认为 10
 * @param enablePkce 是否启用 PKCE，默认为 false
 */
@Serializable
public data class OAuthConfig(
    public val callbackHost: String = "localhost",
    public val callbackPort: Int = 8080,
    public val callbackPath: String = "/callback",
    public val stateTimeoutMinutes: Int = 10,
    public val enablePkce: Boolean = false,
)

/**
 * OAuth 令牌请求，用于 OAuth 令牌交换接口。
 *
 * @param grantType 授权类型，默认为 "authorization_code"
 * @param clientSecret 应用密钥（开放平台签名）
 * @param code 授权码（从 OAuth 授权回调中获取）
 * @param redirectUri 重定向 URI（必须与授权时一致）
 * @param clientId 应用 ID（格式 vap_xxxx）
 * @param codeVerifier 可选的 PKCE code_verifier
 */
@Serializable
public data class OAuthTokenRequest(
    @SerialName("grant_type")
    public val grantType: String = "authorization_code",
    @SerialName("client_secret")
    public val clientSecret: String,
    @SerialName("code")
    public val code: String,
    @SerialName("redirect_uri")
    public val redirectUri: String,
    @SerialName("client_id")
    public val clientId: String,
    @SerialName("code_verifier")
    public val codeVerifier: String? = null,
)

/**
 * OAuth 令牌响应，OAuth 令牌交换接口的响应包装。
 *
 * @param success 请求是否成功
 * @param data OAuth 令牌数据
 * @param requestId 请求 ID
 */
@Serializable
public data class OAuthTokenResponse(
    public val success: Boolean,
    public val data: OAuthTokenData,
    public val requestId: String,
)

/**
 * OAuth 令牌数据，包含 OAuth 访问令牌信息。
 *
 * @param accessToken 访问令牌（OAuth 流程专用）
 * @param expiresIn 有效期（秒）
 * @param tokenType 令牌类型
 * @param scope 授权的权限范围
 * @param refreshToken 刷新令牌，用于获取新的访问令牌
 */
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
)

/**
 * 用户信息响应，用户信息接口的响应包装。
 *
 * @param success 请求是否成功
 * @param data 用户信息数据
 * @param requestId 请求 ID
 */
@Serializable
public data class UserInfoResponse(
    public val success: Boolean,
    public val data: UserInfoData,
    public val requestId: String,
)

/**
 * 用户信息数据，包含 OAuth 用户的基本信息。
 *
 * @param sub 用户唯一标识符
 * @param nickname 用户昵称
 * @param avatarUrl 用户头像 URL
 * @param email 用户邮箱
 * @param name 用户姓名
 * @param username 用户名
 * @param updatedAt 用户信息更新时间戳（毫秒）
 * @param phoneNumber 用户电话号码
 */
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
)

/**
 * 令牌信息，SDK 内部使用的令牌存储结构，统一管理签名交换和 OAuth 两种认证方式的令牌。
 *
 * @param accessToken 访问令牌，用于 Authorization: Bearer 认证头
 * @param apiKey API 密钥，用于 X-Api-Key 认证头
 * @param expiresAt 过期时间戳（毫秒）
 * @param tokenType 令牌类型
 * @param refreshToken 可选的刷新令牌，用于 OAuth 令牌刷新
 */
@Serializable
public data class TokenInfo(
    public val accessToken: String,
    public val apiKey: String,
    public val expiresAt: Long,
    public val tokenType: String,
    public val refreshToken: String? = null,
) {
    /**
     * 令牌刷新窗口（毫秒），当剩余有效期 <= 300 秒时进行刷新。
     */
    private companion object {
        private const val REFRESH_WINDOW_MS = 300_000L // 5 分钟
    }

    /**
     * 检查是否需要刷新令牌。
     *
     * @return 如果令牌已过期或即将过期（<= 300 秒）返回 true
     */
    public fun isExpired(): Boolean {
        val now = Clock.System.now().toEpochMilliseconds()
        val remainingTime = expiresAt - now
        return remainingTime <= REFRESH_WINDOW_MS
    }
}

/**
 * 将签名交换令牌数据转换为 TokenInfo。
 *
 * @return 包含访问令牌、API 密钥和过期时间的令牌信息
 */
public fun TokenData.toTokenInfo(): TokenInfo =
    TokenInfo(
        accessToken = accessToken,
        apiKey = apiKey,
        expiresAt = Clock.System.now().toEpochMilliseconds() + (expiresIn * 1000L),
        tokenType = tokenType,
    )

/**
 * 将 OAuth 令牌数据转换为 TokenInfo。
 *
 * @return 包含访问令牌和过期时间的令牌信息（apiKey 为空字符串）
 */
public fun OAuthTokenData.toTokenInfo(): TokenInfo =
    TokenInfo(
        accessToken = accessToken,
        apiKey = "",
        expiresAt = Clock.System.now().toEpochMilliseconds() + (expiresIn * 1000L),
        tokenType = tokenType,
        refreshToken = refreshToken,
    )
