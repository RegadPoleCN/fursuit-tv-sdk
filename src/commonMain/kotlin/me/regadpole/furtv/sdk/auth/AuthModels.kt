package me.regadpole.furtv.sdk.auth

import kotlinx.datetime.Clock
import kotlinx.serialization.Serializable

/**
 * 令牌交换请求
 * 用于签名交换接口的请求体
 * @param clientId 客户端 ID
 * @param clientSecret 客户端密钥
 */
@Serializable
public data class TokenExchangeRequest(
    public val clientId: String,
    public val clientSecret: String
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
    public val requestId: String
)

/**
 * 令牌数据
 * 包含访问令牌信息
 * @param accessToken 访问令牌，用于 API 认证
 * @param expiresIn 令牌有效期（秒）
 * @param tokenType 令牌类型，通常为 "Bearer"
 */
@Serializable
public data class TokenData(
    public val accessToken: String,
    public val expiresIn: Int,
    public val tokenType: String
)

/**
 * 令牌刷新请求
 * 用于令牌刷新接口的请求体
 * 根据文档，刷新接口不需要请求体，只需要 Authorization header
 * 使用空对象作为请求体
 */
@Serializable
public data class TokenRefreshRequest(
    val dummy: String? = null // 占位符，实际不会发送
)

/**
 * 令牌刷新响应
 * 令牌刷新接口的响应包装
 * @param success 请求是否成功
 * @param data 新的令牌数据
 * @param requestId 请求 ID
 */
@Serializable
public data class TokenRefreshResponse(
    public val success: Boolean,
    public val data: TokenData,
    public val requestId: String
)

/**
 * OAuth 授权 URL 参数
 * 用于生成 OAuth 授权 URL 的参数
 * @param clientId 客户端 ID
 * @param redirectUri 授权后重定向 URI
 * @param state 可选的状态参数，用于防止 CSRF 攻击
 * @param scope 可选的权限范围
 */
@Serializable
public data class OAuthAuthorizeParams(
    public val clientId: String,
    public val redirectUri: String,
    public val state: String? = null,
    public val scope: String? = null
)

/**
 * OAuth 配置
 * 用于配置 OAuth 回调参数
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
    public val enablePkce: Boolean = false
)

/**
 * OAuth 令牌请求
 * 用于 OAuth 令牌交换接口的请求体
 * @param appId 应用 ID
 * @param code 授权码
 * @param redirectUri 重定向 URI（必须与授权时一致）
 */
@Serializable
public data class OAuthTokenRequest(
    public val appId: String,
    public val code: String,
    public val redirectUri: String
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
    public val requestId: String
)

/**
 * OAuth 令牌数据
 * 包含 OAuth 访问令牌信息
 * @param accessToken 访问令牌
 * @param expiresIn 有效期（秒）
 * @param tokenType 令牌类型
 * @param scope 授权的权限范围
 */
@Serializable
public data class OAuthTokenData(
    public val accessToken: String,
    public val expiresIn: Int,
    public val tokenType: String,
    public val scope: String? = null
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
    public val requestId: String
)

/**
 * 用户信息数据
 * 包含 OAuth 用户的基本信息
 * @param sub 用户唯一标识符
 * @param nickname 用户昵称
 * @param avatarUrl 用户头像 URL
 * @param email 用户邮箱
 */
@Serializable
public data class UserInfoData(
    public val sub: String,
    public val nickname: String? = null,
    public val avatarUrl: String? = null,
    public val email: String? = null
)

/**
 * 令牌信息
 * SDK 内部使用的令牌存储结构
 * @param accessToken 访问令牌
 * @param expiresAt 过期时间戳（毫秒）
 * @param tokenType 令牌类型
 */
@Serializable
public data class TokenInfo(
    public val accessToken: String,
    public val expiresAt: Long,
    public val tokenType: String
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
