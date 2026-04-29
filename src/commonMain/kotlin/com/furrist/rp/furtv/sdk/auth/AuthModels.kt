package com.furrist.rp.furtv.sdk.auth

import kotlin.js.JsExport
import kotlin.js.JsName
import kotlinx.datetime.Clock
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * 签名交换请求，用于获取 apiKey/accessToken。
 *
 * VDS 认证系统使用三种类型的凭证：
 * 1. 应用凭证 (App Credentials) - clientId + clientSecret
 * 2. 平台签名 (Platform Signature) - 通过 /api/auth/token 获取
 * 3. OAuth 用户令牌 (OAuth User Token) - 通过 OAuth 2.0 授权码流程获取
 *
 * @param clientId 应用 ID（格式 vap_xxxx），SDK 统一使用 clientId 命名，与 VDS 文档中的 appId 等价
 * @param clientSecret 应用密钥
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

/**
 * 令牌交换响应，签名交换接口的响应包装。
 *
 * @param success 请求是否成功
 * @param data 令牌数据
 * @param requestId 请求 ID，用于日志排查
 */
@JsExport
@JsName("TokenExchangeResponse")
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
 *
 * @param accessToken 访问令牌，用于 Authorization: Bearer 认证头
 * @param apiKey API 密钥，用于 X-Api-Key 认证头
 * @param expiresIn 令牌有效期（秒）
 * @param tokenType 令牌类型，通常为 "Bearer"
 * @param appId 应用 ID（格式 vap_xxxx），与 VDS 文档中的 appId 等价
 * @param grants 授权的权限范围列表
 * @param refresh 可选的令牌刷新信息
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
)

/**
 * 令牌刷新响应，令牌刷新接口的响应包装。
 *
 * @param success 请求是否成功
 * @param data 新的令牌数据
 * @param requestId 请求 ID
 * @param refresh 可选的令牌刷新信息
 */
@JsExport
@JsName("TokenRefreshResponse")
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
 * @param clientId 应用 ID（格式 vap_xxxx），SDK 统一使用 clientId 命名，与 VDS 文档中的 appId 等价
 * @param redirectUri 授权后重定向 URI
 * @param state 可选的状态参数，用于防止 CSRF 攻击
 * @param scope 可选的权限范围
 * @param responseType 响应类型，默认为 "code"
 * @param codeChallenge 可选的 PKCE code_challenge
 * @param codeChallengeMethod 可选的 PKCE code_challenge_method，默认为 "SHA256"
 */
@JsExport
@JsName("OAuthAuthorizeParams")
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
 * OAuth 2.0 授权码流程配置。
 *
 * 控制回调服务器的行为，所有参数都有合理默认值。
 *
 * @param callbackHost 回调地址（默认 "localhost"）
 * @param callbackPort 回调端口（默认 8080，范围 1-65535）
 * @param callbackPath 回调路径（默认 "/callback"，必须以 "/" 开头）
 * @param stateTimeoutMinutes 超时时间（默认 5 分钟）
 * @param enablePkce 是否启用 PKCE 安全增强（默认 false）
 *
 * @throws IllegalArgumentException 参数验证失败
 */
@JsExport
@JsName("OAuthConfig")
@Serializable
public data class OAuthConfig(
    public val callbackHost: String = DEFAULT_CALLBACK_HOST,
    public val callbackPort: Int = DEFAULT_CALLBACK_PORT,
    public val callbackPath: String = DEFAULT_CALLBACK_PATH,
    public val stateTimeoutMinutes: Int = DEFAULT_STATE_TIMEOUT_MINUTES,
    public val enablePkce: Boolean = false,
) {
    init {
        require(callbackPort in 1..MAX_PORT_NUMBER) {
            "callbackPort must be between 1 and $MAX_PORT_NUMBER"
        }
        require(callbackPath.startsWith("/")) { "callbackPath must start with '/'" }
        require(stateTimeoutMinutes > 0) { "stateTimeoutMinutes must be positive" }
        require(callbackHost.isNotBlank()) { "callbackHost must not be blank" }
    }

    public companion object {
        /** 默认回调地址主机名 */
        public const val DEFAULT_CALLBACK_HOST: String = "localhost"
        /** 默认回调端口 */
        public const val DEFAULT_CALLBACK_PORT: Int = 8080
        /** 默认回调路径 */
        public const val DEFAULT_CALLBACK_PATH: String = "/callback"
        /** 默认 state 超时时间（分钟） */
        public const val DEFAULT_STATE_TIMEOUT_MINUTES: Int = 5
        private const val MAX_PORT_NUMBER = 65535
    }
}

/**
 * OAuth 令牌请求，用于 OAuth 令牌交换接口。
 *
 * @param grantType 授权类型，默认为 "authorization_code"
 * @param clientSecret 应用密钥（开放平台签名）
 * @param code 授权码（从 OAuth 授权回调中获取）
 * @param redirectUri 重定向 URI（必须与授权时一致）
 * @param clientId 应用 ID（格式 vap_xxxx），SDK 统一使用 clientId 命名，与 VDS 文档中的 appId 等价
 * @param codeVerifier 可选的 PKCE code_verifier
 */
@JsExport
@JsName("OAuthTokenRequest")
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
@JsExport
@JsName("OAuthTokenResponse")
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
)

/**
 * 用户信息响应，用户信息接口的响应包装。
 *
 * @param success 请求是否成功
 * @param data 用户信息数据
 * @param requestId 请求 ID
 */
@JsExport
@JsName("UserInfoResponse")
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
 * @param iss 令牌签发者（issuer）
 * @param aud 令牌受众（audience）
 */
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
@JsExport
@JsName("TokenInfo")
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
@JsExport
@JsName("toTokenInfo")
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
@JsExport
@JsName("toTokenInfoFromOAuth")
public fun OAuthTokenData.toTokenInfo(): TokenInfo =
    TokenInfo(
        accessToken = accessToken,
        apiKey = "",
        expiresAt = Clock.System.now().toEpochMilliseconds() + (expiresIn * 1000L),
        tokenType = tokenType,
        refreshToken = refreshToken,
    )
