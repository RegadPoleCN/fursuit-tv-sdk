package me.regadpole.furtv.sdk.auth

import kotlinx.datetime.Clock
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * 签名交换请求（用于获取 apiKey/accessToken）
 * 用于签名交换接口的请求体
 * @param clientId 应用 ID（格式 vap_xxxx）
 * @param clientSecret 应用密钥（与 appSecret 等价）
 */
@Serializable
public data class TokenExchangeRequest(
    public val clientId: String,
    public val clientSecret: String,
)

/**
 * 令牌交换响应
 * 签名交换接口的响应包装
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
 * 令牌刷新信息
 * 包含签名换新策略的相关信息
 *
 * 官方文档：vds-docs/基础接口/签名换新.md
 *
 * 字段说明：
 * - mode: 刷新模式
 *   - 描述当前刷新的方式
 *   - 如 "exchange_current_access_token" 表示使用当前 access_token 交换
 * - refreshWindowSeconds: 刷新窗口时间（秒）
 *   - 表示在令牌过期前多少秒进行刷新
 *   - 通常为 300 秒（5 分钟）
 *   - 当剩余时间 <= 此值时触发刷新
 * - previousTokenSecondsRemaining: 旧令牌剩余有效期（秒）
 *   - 刷新时旧令牌还剩多少秒过期
 *   - 用于诊断和日志记录
 *
 * 响应示例：
 * ```json
 * {
 *   "refresh": {
 *     "mode": "exchange_current_access_token",
 *     "refreshWindowSeconds": 300,
 *     "previousTokenSecondsRemaining": 221
 *   }
 * }
 * ```
 *
 * 刷新规则：
 * - 当剩余有效期 <= 300 秒时可以刷新
 * - 刷新成功后获得新的 accessToken 和 apiKey
 * - 新令牌的有效期重新计算（通常 3600 秒）
 *
 * @property mode 刷新模式
 * @property refreshWindowSeconds 刷新窗口时间（秒），表示在令牌过期前多少秒进行刷新
 * @property previousTokenSecondsRemaining 旧令牌剩余有效期（秒）
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
 * 令牌数据
 * 包含访问令牌信息（签名交换接口返回）
 * 注意：accessToken 和 apiKey 是两个不同的值
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
    public val expiresIn: Int,
    public val tokenType: String,
    @SerialName("refresh")
    public val refresh: TokenRefreshInfo? = null,
)

/**
 * 令牌刷新请求
 * 用于令牌刷新接口的请求体
 * 根据文档，刷新接口不需要请求体，只需要 Authorization header
 * 使用空对象作为请求体
 */
@Serializable
public data class TokenRefreshRequest(
    val dummy: String? = null, // 占位符，实际不会发送
)

/**
 * 令牌刷新响应
 * 令牌刷新接口的响应包装
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
 * OAuth 授权 URL 参数
 * 用于生成 OAuth 授权 URL 的参数（OAuth 流程）
 *
 * 官方文档：vds-docs/VDS 账户/授权端点（Authorize，account.sso.authorize）.md
 *
 * 参数说明：
 * - clientId: 应用 ID（格式 vap_xxxx），对应 OAuth 协议的 client_id
 * - redirectUri: 授权后重定向 URI
 *   - 必须是已在开放平台配置的回调地址
 *   - 必须与令牌交换时使用的 redirect_uri 一致
 * - state: 可选的状态参数，用于防止 CSRF 攻击
 *   - 建议使用随机字符串
 *   - 回调时会原样返回此参数
 * - scope: 可选的权限范围
 *   - 多个权限用空格分隔
 *   - 如 "user.profile user.email"
 * - responseType: 响应类型，固定为 "code"
 * - codeChallenge: 可选的 PKCE code_challenge
 *   - 使用 SHA256 哈希 code_verifier 并进行 base64url 编码
 *   - 用于增强移动端和 Web 应用的安全性
 * - codeChallengeMethod: 可选的 PKCE code_challenge_method，默认为 "SHA256"
 *
 * 使用示例：
 * ```
 * val params = OAuthAuthorizeParams(
 *     clientId = "vap_xxxxx",
 *     redirectUri = "http://localhost:8080/callback",
 *     state = "random_" + Random.nextLong(),
 *     scope = "user.profile",
 *     codeChallenge = "generated_challenge",
 *     codeChallengeMethod = "SHA256"
 * )
 * val authorizeUrl = authManager.getOAuthAuthorizeUrl(params)
 * ```
 *
 * @property clientId 应用 ID（格式 vap_xxxx）
 * @property redirectUri 授权后重定向 URI
 * @property state 可选的状态参数，用于防止 CSRF 攻击
 * @property scope 可选的权限范围
 * @property responseType 响应类型，默认为 "code"
 * @property codeChallenge 可选的 PKCE code_challenge
 * @property codeChallengeMethod 可选的 PKCE code_challenge_method，默认为 "SHA256"
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
 * OAuth 配置
 * 用于配置 OAuth 回调参数（OAuth 流程）
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
 * OAuth 令牌请求
 * 用于 OAuth 令牌交换接口的请求体（OAuth 流程）
 *
 * 官方文档：vds-docs/VDS 账户/VDS 账户快速接入（OAuth）.md
 *
 * 请求参数说明：
 * - grant_type: 授权类型
 *   - "authorization_code": 使用授权码交换令牌（初次授权）
 *   - "refresh_token": 使用刷新令牌获取新令牌（令牌刷新）
 * - client_id: 应用 ID（格式 vap_xxxx）
 * - client_secret: 应用密钥（开放平台签名，用于验证应用身份）
 * - code: 授权码（从 OAuth 授权回调中获取，仅在 grant_type=authorization_code 时使用）
 * - redirect_uri: 重定向 URI（必须与授权时使用的 URI 一致）
 * - code_verifier: PKCE code_verifier（可选，启用 PKCE 时必需）
 *
 * 使用示例：
 * ```
 * // 初次授权码交换
 * val tokenRequest = OAuthTokenRequest(
 *     clientId = "vap_xxxxx",
 *     clientSecret = sdkConfig.apiKey,
 *     code = "auth_code_from_callback",
 *     redirectUri = "http://localhost:8080/callback",
 *     codeVerifier = "generated_code_verifier"
 * )
 *
 * // 刷新令牌（通过 exchangeOAuthToken 方法）
 * val refreshRequest = OAuthTokenRequest(
 *     grantType = "refresh_token",
 *     clientId = "vap_xxxxx",
 *     clientSecret = sdkConfig.apiKey,
 *     code = "", // 不需要
 *     redirectUri = "http://localhost:8080/callback"
 * )
 * ```
 *
 * @property grantType 授权类型，默认为 "authorization_code"
 * @property clientSecret 应用密钥（开放平台签名）
 * @property code 授权码（从 OAuth 授权回调中获取）
 * @property redirectUri 重定向 URI（必须与授权时一致）
 * @property clientId 应用 ID（格式 vap_xxxx）
 * @property codeVerifier 可选的 PKCE code_verifier
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
 * OAuth 令牌响应
 * OAuth 令牌交换接口的响应包装
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
 * OAuth 令牌数据
 * 包含 OAuth 访问令牌信息（OAuth 流程）
 *
 * 官方文档：vds-docs/VDS 账户/VDS 账户快速接入（OAuth）.md
 *
 * 字段说明：
 * - accessToken: 访问令牌（OAuth 流程专用）
 *   - 用于访问受保护的用户资源
 *   - 通过 Authorization: Bearer <token> 或 X-OAuth-Access-Token: <token> 传递
 * - expiresIn: 有效期（秒），通常为 3600（1 小时）
 * - tokenType: 令牌类型，固定为 "Bearer"
 * - scope: 授权的权限范围（可选）
 *   - 多个权限用空格分隔
 *   - 如 "user.profile user.email"
 * - refreshToken: 刷新令牌（可选）
 *   - 用于在 access_token 过期后获取新的访问令牌
 *   - 仅在初次授权时返回，刷新令牌时可能轮换
 *   - 仅在 grant_type=authorization_code 时返回
 *
 * 响应示例：
 * ```json
 * {
 *   "access_token": "eyJhbGciOiJIUzI1NiIs...",
 *   "expires_in": 3600,
 *   "token_type": "Bearer",
 *   "scope": "user.profile",
 *   "refresh_token": "dGhpcyBpcyBhIHJlZnJlc2g..."
 * }
 * ```
 *
 * @property accessToken 访问令牌（OAuth 流程专用）
 * @property expiresIn 有效期（秒）
 * @property tokenType 令牌类型
 * @property scope 授权的权限范围
 * @property refreshToken 刷新令牌，用于获取新的访问令牌
 */
@Serializable
public data class OAuthTokenData(
    public val accessToken: String,
    public val expiresIn: Int,
    public val tokenType: String,
    public val scope: String? = null,
    @SerialName("refresh_token")
    public val refreshToken: String? = null,
)

/**
 * 用户信息响应
 * 用户信息接口的响应包装
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
 * 用户信息数据
 * 包含 OAuth 用户的基本信息
 *
 * 官方文档：vds-docs/VDS 账户/用户信息端点（UserInfo，account.sso.userinfo）.md
 *
 * 字段说明（对应官方 API 响应字段）：
 * - sub: 用户唯一标识符
 *   - 对应 OpenID Connect 标准的 subject 字段
 *   - 格式如 "oauth|xxxxx"
 * - nickname: 用户昵称（可选）
 *   - 用户在平台上显示的昵称
 * - avatarUrl: 用户头像 URL（可选）
 *   - 用户头像图片的完整 URL
 * - email: 用户邮箱（可选）
 *   - 需要 user.email 权限才能获取
 * - name: 用户姓名（可选）
 *   - 用户的真实姓名
 * - username: 用户名（可选）
 *   - 用户的登录名或唯一用户名
 * - updatedAt: 用户信息更新时间戳（可选，毫秒）
 *   - 用户信息最后一次更新的时间
 * - phoneNumber: 用户电话号码（可选）
 *   - 需要 user.phone 权限才能获取
 *
 * 响应示例：
 * ```json
 * {
 *   "sub": "oauth|123456789",
 *   "nickname": "FurryUser",
 *   "avatarUrl": "https://example.com/avatar.jpg",
 *   "email": "user@example.com",
 *   "name": "张三",
 *   "username": "furryuser",
 *   "updatedAt": 1234567890000,
 *   "phoneNumber": "+86 13800138000"
 * }
 * ```
 *
 * 使用示例：
 * ```
 * val userInfo = authManager.getUserInfo()
 * println("用户 ID: ${userInfo.sub}")
 * println("昵称：${userInfo.nickname}")
 * println("邮箱：${userInfo.email}")
 * ```
 *
 * @property sub 用户唯一标识符
 * @property nickname 用户昵称
 * @property avatarUrl 用户头像 URL
 * @property email 用户邮箱
 * @property name 用户姓名
 * @property username 用户名
 * @property updatedAt 用户信息更新时间戳（毫秒）
 * @property phoneNumber 用户电话号码
 */
@Serializable
public data class UserInfoData(
    public val sub: String,
    public val nickname: String? = null,
    public val avatarUrl: String? = null,
    public val email: String? = null,
    public val name: String? = null,
    public val username: String? = null,
    public val updatedAt: Long? = null,
    public val phoneNumber: String? = null,
)

/**
 * 令牌信息
 * SDK 内部使用的令牌存储结构
 *
 * 用于统一管理签名交换和 OAuth 两种认证方式获取的令牌。
 *
 * 字段说明：
 * - accessToken: 访问令牌，用于 Authorization: Bearer 认证头
 *   - 签名交换：从 /api/auth/token 接口获取
 *   - OAuth 流程：从 /api/proxy/account/sso/token 接口获取
 * - apiKey: API 密钥，用于 X-Api-Key 认证头
 *   - 仅签名交换流程返回此字段
 *   - OAuth 流程此字段为空字符串
 * - expiresAt: 过期时间戳（毫秒），用于判断令牌是否过期
 *   - 计算方式：当前时间 + expiresIn * 1000
 * - tokenType: 令牌类型，通常为 "Bearer"
 * - refreshToken: 可选的刷新令牌，用于 OAuth 令牌刷新
 *   - 仅 OAuth 流程返回此字段
 *   - 用于在 access_token 过期后获取新的访问令牌
 *
 * 刷新窗口说明：
 * - 当剩余有效期 <= 300 秒（5 分钟）时，令牌被视为即将过期
 * - 此时应调用刷新接口获取新令牌
 * - 刷新窗口大小：REFRESH_WINDOW_MS = 300,000ms
 *
 * 使用示例：
 * ```
 * val tokenInfo = authManager.exchangeToken(appId, appSecret)
 *
 * // 检查是否过期
 * if (tokenInfo.isExpired()) {
 *     // 需要刷新令牌
 *     val newTokenInfo = authManager.refreshToken()
 * }
 *
 * // 获取访问令牌
 * val accessToken = tokenInfo.accessToken
 * ```
 *
 * @property accessToken 访问令牌，用于 Authorization: Bearer 认证头
 * @property apiKey API 密钥，用于 X-Api-Key 认证头
 * @property expiresAt 过期时间戳（毫秒）
 * @property tokenType 令牌类型
 * @property refreshToken 可选的刷新令牌，用于 OAuth 令牌刷新
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
     * 令牌刷新窗口（毫秒）
     * 根据文档，当剩余有效期 <= 300 秒时进行刷新
     */
    private companion object {
        private const val REFRESH_WINDOW_MS = 300_000L // 5 分钟
    }

    /**
     * 检查是否需要刷新令牌
     * 根据文档，当剩余有效期 <= 300 秒（5 分钟）时应该刷新
     * @return 如果令牌已过期或即将过期（<= 300 秒）返回 true
     */
    public fun isExpired(): Boolean {
        val now = Clock.System.now().toEpochMilliseconds()
        val remainingTime = expiresAt - now
        return remainingTime <= REFRESH_WINDOW_MS
    }
}
