package me.regadpole.furtv.sdk.auth

import me.regadpole.furtv.sdk.exception.TokenExpiredException
import me.regadpole.furtv.sdk.http.HttpClientConfig
import me.regadpole.furtv.sdk.model.SdkConfig
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.datetime.Clock
import kotlin.time.Duration.Companion.minutes

/**
 * 认证管理器
 * 负责令牌的管理、刷新和 OAuth 流程
 * 提供签名交换、令牌刷新、OAuth 授权、用户信息获取等功能
 * 
 * @property config SDK 配置对象，包含 API 端点和认证信息
 */
public class AuthManager(
    private val config: SdkConfig
) {
    private var httpClient: HttpClient = HttpClientConfig.createClient(config)
    private val tokenMutex = Mutex()

    // 令牌信息
    private var tokenInfo: TokenInfo? = null
    // 是否为 OAuth 令牌（决定使用哪种认证头）
    // true: OAuth 流程的 access_token，使用 Authorization: Bearer
    // false: 签名交换的令牌，使用 X-Api-Key（优先）或 Authorization: Bearer
    private var isOAuthToken: Boolean = false

    /**
     * 获取当前访问令牌
     * @return 当前访问令牌，如果未认证则返回 null
     */
    public fun getAccessToken(): String? = tokenInfo?.accessToken

    /**
     * 获取当前 API 密钥
     * @return 当前 API 密钥，如果未认证则返回 null
     */
    public fun getApiKey(): String? = tokenInfo?.apiKey

    /**
     * 检查是否已认证
     * @return 如果已认证且令牌未过期返回 true，否则返回 false
     */
    public fun isAuthenticated(): Boolean = tokenInfo?.isExpired()?.not() == true

    /**
     * 设置令牌信息
     * @param tokenInfo 要设置的令牌信息
     */
    public fun setTokenInfo(tokenInfo: TokenInfo) {
        this.tokenInfo = tokenInfo
        updateHttpClient()
    }

    /**
     * 清除令牌信息
     * 清除当前存储的令牌并重置 HTTP 客户端
     */
    public fun clearToken() {
        tokenInfo = null
        updateHttpClient()
    }

    /**
     * 签名交换 - 使用 appId 和 appSecret 获取令牌
     * 端点：POST /api/auth/token
     * 
     * 此方法用于获取签名认证所需的 accessToken 和 apiKey
     * accessToken 用于 Authorization: Bearer 认证头
     * apiKey 用于 X-Api-Key 认证头
     * 两者是不同的值
     * 
     * @param appId 应用 ID（格式 vap_xxxx）
     * @param appSecret 应用密钥
     * @return TokenInfo 包含 accessToken 和 apiKey
     */
    public suspend fun exchangeToken(appId: String, appSecret: String): TokenInfo {
        val response = httpClient.post("${config.baseUrl}/api/auth/token") {
            contentType(ContentType.Application.Json)
            setBody(TokenExchangeRequest(appId, appSecret))
        }.body<TokenExchangeResponse>()

        val newTokenInfo = TokenInfo(
            accessToken = response.data.accessToken,
            apiKey = response.data.apiKey,
            expiresAt = Clock.System.now().toEpochMilliseconds() + (response.data.expiresIn * 1000),
            tokenType = response.data.tokenType
        )

        tokenMutex.withLock {
            tokenInfo = newTokenInfo
            isOAuthToken = false  // 标记为非 OAuth 令牌
            updateHttpClient()
        }

        return newTokenInfo
    }

    /**
     * 刷新令牌
     * 使用当前的访问令牌刷新获取新的访问令牌
     * 端点：POST /api/auth/token/refresh
     * @return TokenInfo 新的令牌信息
     * @throws TokenExpiredException 如果没有可用的令牌
     */
    public suspend fun refreshToken(): TokenInfo {
        val currentAccessToken = tokenInfo?.accessToken
            ?: throw TokenExpiredException("No access token available")

        val response = httpClient.post("${config.baseUrl}/api/auth/token/refresh") {
            contentType(ContentType.Application.Json)
            // 刷新接口使用 Authorization header
            header("Authorization", "Bearer $currentAccessToken")
            // 空请求体
            setBody(TokenRefreshRequest())
        }.body<TokenRefreshResponse>()

        val newTokenInfo = TokenInfo(
            accessToken = response.data.accessToken,
            apiKey = response.data.apiKey,
            expiresAt = Clock.System.now().toEpochMilliseconds() + (response.data.expiresIn * 1000),
            tokenType = response.data.tokenType
        )

        tokenMutex.withLock {
            tokenInfo = newTokenInfo
            updateHttpClient()
        }

        return newTokenInfo
    }

    /**
     * 获取有效的访问令牌（自动刷新）
     * 提供统一的获取令牌方法，实现自动检查和刷新逻辑
     * 遵循文档中的伪代码逻辑：
     * 1. 如果没有令牌或已过期，调用 exchangeToken
     * 2. 如果剩余有效期 <= 300 秒，调用 refreshToken
     * 3. 如果刷新失败，回退到 exchangeToken
     * 
     * 注意：此方法需要 appId 和 appSecret 作为回退凭证
     * @param appId 应用 ID（用于回退）
     * @param appSecret 应用密钥（用于回退）
     * @return 当前或刷新后的访问令牌（accessToken）
     */
    public suspend fun getValidAccessToken(appId: String, appSecret: String): String {
        return tokenMutex.withLock {
            val now = Clock.System.now().toEpochMilliseconds()
            
            // 1. 如果没有令牌或已过期，获取新令牌
            if (tokenInfo == null || tokenInfo?.isExpired() == true) {
                exchangeToken(appId, appSecret)
                return@withLock tokenInfo!!.accessToken
            }
            
            // 2. 如果剩余有效期 <= 300 秒，刷新令牌
            if (tokenInfo!!.isExpired()) { // isExpired() now checks <= 300 seconds
                @Suppress("SwallowedException", "TooGenericExceptionCaught")
                try {
                    refreshToken()
                } catch (e: Exception) {
                    // 3. 刷新失败，回退到 exchangeToken
                    // 记录异常但不抛出，继续执行 exchangeToken
                    // 注意：这里捕获通用 Exception 是因为网络错误可能是多种类型
                    exchangeToken(appId, appSecret)
                }
            }
            
            return@withLock tokenInfo!!.accessToken
        }
    }

    /**
     * 如果需要，刷新令牌
     * 检查令牌是否过期（剩余时间 <= 300 秒），如果是则自动刷新
     * @return TokenInfo 当前或刷新后的令牌信息，如果未认证则返回 null
     * @deprecated 使用 [getValidAccessToken] 代替，该方法需要 appId 和 appSecret
     */
    @Deprecated("Use getValidAccessToken(appId, appSecret) instead", ReplaceWith("getValidAccessToken(appId, appSecret)"))
    public suspend fun refreshTokenIfNeeded(): TokenInfo? {
        return tokenMutex.withLock {
            if (tokenInfo?.isExpired() == true) {
                refreshToken()
            } else {
                tokenInfo
            }
        }
    }

    /**
     * 生成 OAuth 授权 URL
     * 用于 VDS 账户 OAuth 授权流程的第一步
     * 端点：GET /api/proxy/account/sso/authorize
     * @param params OAuth 授权参数
     * @return 完整的授权 URL
     */
    public fun getOAuthAuthorizeUrl(params: OAuthAuthorizeParams): String {
        val queryParams = buildString {
            append("?client_id=${params.appId}")
            append("&redirect_uri=${params.redirectUri}")
            params.state?.let { append("&state=$it") }
            params.scope?.let { append("&scope=$it") }
        }
        return "${config.baseUrl}/api/proxy/account/sso/authorize$queryParams"
    }

    /**
     * 使用 OAuth 初始化 SDK
     * 完整的 OAuth 授权流程：
     * 1. 创建 OAuthCallbackHandler（自动选择平台实现）
     * 2. 生成随机 state
     * 3. 构建授权 URL
     * 4. 等待回调（handler 会自动打开浏览器）
     * 5. 验证 state
     * 6. 使用授权码交换 access_token（仅需 appId）
     * 7. 返回 TokenInfo
     * 
     * @param appId 应用 ID（OAuth 流程仅需 appId，不需要 appSecret）
     * @param config OAuth 配置（包含回调主机、端口等）
     * @param scope 可选的权限范围
     * @return TokenInfo 包含访问令牌
     * @throws OAuthCallbackException 当回调失败或 state 验证失败时抛出
     */
    public suspend fun initWithOAuth(appId: String, config: OAuthConfig, scope: String? = null): TokenInfo {
        val serverConfig = OAuthCallbackServerConfig(
            callbackHost = config.callbackHost,
            callbackPort = config.callbackPort,
            callbackPath = config.callbackPath,
            timeoutSeconds = config.stateTimeoutMinutes * 60L
        )
        val handler = createOAuthCallbackHandler(serverConfig)
        
        val state = generateState()
        val redirectUri = handler.callbackUrl
        
        val authorizeParams = OAuthAuthorizeParams(
            appId = appId,
            redirectUri = redirectUri,
            state = state,
            scope = scope
        )
        
        val authorizeUrl = getOAuthAuthorizeUrl(authorizeParams)
        
        // 等待回调（handler 会处理打开浏览器等逻辑）
        val result = handler.startAndGetCallback(authorizeUrl)
        
        return when (result) {
            is OAuthCallbackResult.Success -> {
                // 验证 state
                if (result.state != state) {
                    throw OAuthCallbackException(
                        "State mismatch: expected $state, got ${result.state}"
                    )
                }
                
                // 交换令牌
                val tokenRequest = OAuthTokenRequest(
                    appId = appId,
                    code = result.code,
                    redirectUri = redirectUri
                )
                
                exchangeOAuthToken(tokenRequest)
            }
            is OAuthCallbackResult.Error -> {
                throw OAuthCallbackException(result.message, result.cause)
            }
        }
    }

    /**
     * OAuth 令牌交换
     * 使用授权码换取访问令牌
     * 端点：POST /api/proxy/account/sso/token
     * @param request OAuth 令牌请求
     * @return TokenInfo 包含访问令牌
     */
    public suspend fun exchangeOAuthToken(request: OAuthTokenRequest): TokenInfo {
        val response = httpClient.post("${config.baseUrl}/api/proxy/account/sso/token") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body<OAuthTokenResponse>()

        val newTokenInfo = TokenInfo(
            accessToken = response.data.accessToken,
            apiKey = "",  // OAuth 流程没有 apiKey，设置为空字符串
            expiresAt = Clock.System.now().toEpochMilliseconds() + (response.data.expiresIn * 1000),
            tokenType = response.data.tokenType
        )

        tokenMutex.withLock {
            tokenInfo = newTokenInfo
            isOAuthToken = true  // 标记为 OAuth 令牌
            updateHttpClient()
        }

        return newTokenInfo
    }

    /**
     * 获取用户信息
     * 获取当前认证用户的详细信息
     * 端点：GET /api/proxy/account/sso/userinfo
     * @return UserInfoData 用户信息数据
     */
    public suspend fun getUserInfo(): UserInfoData {
        val response = httpClient.get("${config.baseUrl}/api/proxy/account/sso/userinfo") {
            header("Authorization", "Bearer ${tokenInfo?.accessToken}")
        }.body<UserInfoResponse>()

        return response.data
    }

    /**
     * 更新 HTTP 客户端（当令牌变化时）
     * 使用新的访问令牌重新配置 HTTP 客户端
     * 根据令牌类型选择合适的认证头：
     * - OAuth 令牌：使用 Authorization Bearer
     * - 签名交换令牌：使用 X-Api-Key（优先）
     */
    private fun updateHttpClient() {
        // OAuth 场景使用 Authorization Bearer，签名交换场景使用 X-Api-Key
        // 对于签名交换，优先使用 apiKey（通过 useApiKey 参数控制）
        httpClient = HttpClientConfig.createClient(
            config, 
            if (isOAuthToken) tokenInfo?.accessToken else tokenInfo?.apiKey,
            !isOAuthToken  // useApiKey = true 表示使用 X-Api-Key 头
        )
    }

    /**
     * 关闭客户端
     * 释放认证管理器占用的资源
     */
    public fun close() {
        httpClient.close()
    }
}
