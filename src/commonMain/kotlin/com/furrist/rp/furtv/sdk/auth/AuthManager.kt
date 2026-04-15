package com.furrist.rp.furtv.sdk.auth

import com.furrist.rp.furtv.sdk.exception.TokenExpiredException
import com.furrist.rp.furtv.sdk.http.HttpClientConfig
import com.furrist.rp.furtv.sdk.model.SdkConfig
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi
import kotlin.random.Random
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.datetime.Clock

/**
 * 认证管理器
 * 负责令牌的管理、刷新和 OAuth 流程
 * 提供签名交换、令牌刷新、OAuth 授权、用户信息获取等功能
 *
 * 重要说明：
 * - OAuth token（access_token）仅用于 getUserInfo() 接口
 * - Client token（签名交换的 token）用于其他所有接口
 * - 两种 token 不通用，使用不同的认证头
 *
 * @property config SDK 配置对象，包含 API 端点和认证信息
 */
@Suppress("TooManyFunctions")
public class AuthManager(
    private val config: SdkConfig,
) {
    private var httpClient: HttpClient = HttpClientConfig.createClient(config)
    private val tokenMutex = Mutex()

    // 令牌信息
    private var tokenInfo: TokenInfo? = null

    // 是否为 OAuth 令牌（决定使用哪种认证头）
    // true: OAuth 流程的 access_token，使用 Authorization: Bearer
    // false: 签名交换的令牌，使用 X-Api-Key（优先）或 Authorization: Bearer
    private var isOAuthToken: Boolean = false

    // OAuth 流程中使用的客户端 ID 和重定向 URI，用于令牌刷新
    private var oauthClientId: String? = null
    private var oauthRedirectUri: String? = null

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
     * 签名交换 - 使用 clientId 和 clientSecret 获取令牌
     * 端点：POST /api/auth/token
     *
     * 此方法用于获取签名认证所需的 accessToken 和 apiKey
     * accessToken 用于 Authorization: Bearer 认证头
     * apiKey 用于 X-Api-Key 认证头
     * 两者是不同的值
     *
     * @param clientId 应用 ID（格式 vap_xxxx）
     * @param clientSecret 应用密钥
     * @return TokenInfo 包含 accessToken 和 apiKey
     */
    public suspend fun exchangeToken(clientId: String, clientSecret: String): TokenInfo {
        val response =
            httpClient.post("${config.baseUrl}/api/auth/token") {
                contentType(ContentType.Application.Json)
                setBody(TokenExchangeRequest(clientId, clientSecret))
            }.body<TokenExchangeResponse>()

        val newTokenInfo =
            TokenInfo(
                accessToken = response.data.accessToken,
                apiKey = response.data.apiKey,
                expiresAt = Clock.System.now().toEpochMilliseconds() + (response.data.expiresIn * 1000),
                tokenType = response.data.tokenType,
            )

        tokenMutex.withLock {
            tokenInfo = newTokenInfo
            isOAuthToken = false // 标记为非 OAuth 令牌
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
        val currentAccessToken =
            tokenInfo?.accessToken
                ?: throw TokenExpiredException("No access token available")

        val response =
            httpClient.post("${config.baseUrl}/api/auth/token/refresh") {
                contentType(ContentType.Application.Json)
                // 刷新接口使用 Authorization header
                header("Authorization", "Bearer $currentAccessToken")
                // 空请求体
                setBody(TokenRefreshRequest())
            }.body<TokenRefreshResponse>()

        val newTokenInfo =
            TokenInfo(
                accessToken = response.data.accessToken,
                apiKey = response.data.apiKey,
                expiresAt = Clock.System.now().toEpochMilliseconds() + (response.data.expiresIn * 1000),
                tokenType = response.data.tokenType,
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
     *
     * 官方文档参考：
     * - vds-docs/基础接口/签名交换.md - 签名交换流程
     * - vds-docs/基础接口/签名换新.md - 签名换新机制
     * - vds-docs/开始接入/快速跑通流程.md - 完整接入流程示例
     *
     * 自动刷新逻辑（遵循官方文档伪代码）：
     * 1. 如果没有令牌或已过期：
     *    - 调用 exchangeToken(clientId, clientSecret) 获取新令牌（签名交换）
     *    - 如果是 OAuth 流程，需要重新进行 OAuth 授权
     * 2. 如果剩余有效期 <= 300 秒（5 分钟）：
     *    - 如果是 OAuth 令牌且有 refresh_token：调用 refreshOAuthToken()
     *    - 如果是签名交换令牌：调用 refreshToken()
     * 3. 刷新失败：
     *    - 捕获异常并记录日志
     *    - 回退到 exchangeToken(clientId, clientSecret) 重新获取
     *
     * 令牌有效期说明：
     * - 默认有效期：3600 秒（1 小时）
     * - 刷新窗口：过期前 300 秒（5 分钟）
     * - 刷新模式：exchange_current_access_token
     *
     * 示例：
     * ```
     * // 在发起 API 请求前调用
     * val accessToken = authManager.getValidAccessToken(clientId, clientSecret)
     *
     * // 使用 accessToken 发起请求
     * httpClient.get("https://api.example.com/data") {
     *     header("Authorization", "Bearer $accessToken")
     * }
     * ```
     *
     * @param clientId 应用 ID（用于签名交换回退，格式 vap_xxxx）
     * @param clientSecret 应用密钥（用于签名交换回退）
     * @return 当前或刷新后的访问令牌（accessToken），可直接用于 API 认证
     * @throws Exception 当网络错误或认证失败时抛出（已在内部捕获并回退）
     */
    public suspend fun getValidAccessToken(clientId: String, clientSecret: String): String {
        return tokenMutex.withLock {
            val now = Clock.System.now().toEpochMilliseconds()

            // 1. 如果没有令牌或已过期，获取新令牌
            if (tokenInfo == null || tokenInfo?.isExpired() == true) {
                exchangeToken(clientId, clientSecret)
                return@withLock tokenInfo!!.accessToken
            }

            // 2. 如果剩余有效期 <= 300 秒，刷新令牌
            if (tokenInfo!!.isExpired()) { // isExpired() now checks <= 300 seconds
                @Suppress("SwallowedException", "TooGenericExceptionCaught")
                try {
                    if (isOAuthToken && tokenInfo?.refreshToken != null) {
                        // OAuth 令牌刷新
                        refreshOAuthToken()
                    } else {
                        // 签名交换令牌刷新
                        refreshToken()
                    }
                } catch (e: Exception) {
                    // 3. 刷新失败，回退到 exchangeToken
                    // 记录异常但不抛出，继续执行 exchangeToken
                    // 注意：这里捕获通用 Exception 是因为网络错误可能是多种类型
                    exchangeToken(clientId, clientSecret)
                }
            }

            return@withLock tokenInfo!!.accessToken
        }
    }

    /**
     * 如果需要，刷新令牌
     * 检查令牌是否过期（剩余时间 <= 300 秒），如果是则自动刷新
     * @return TokenInfo 当前或刷新后的令牌信息，如果未认证则返回 null
     * @deprecated 使用 [getValidAccessToken] 代替，该方法需要 clientId 和 clientSecret
     */
    @Deprecated(
        "Use getValidAccessToken(clientId, clientSecret) instead",
        ReplaceWith("getValidAccessToken(clientId, clientSecret)"),
    )
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
     * 生成 PKCE code verifier
     * 生成 32 字节（64 字符十六进制）的随机字符串
     * @return code_verifier 随机字符串
     */
    private fun generateCodeVerifier(): String {
        return Random.nextBytes(32).let { bytes ->
            bytes.joinToString("") { byte ->
                val hex = byte.toInt().and(0xFF).toString(16)
                if (hex.length == 1) "0$hex" else hex
            }
        }
    }

    /**
     * 生成 PKCE code challenge
     * 对 code_verifier 进行 SHA256 哈希并进行 base64url 编码
     * 使用纯 Kotlin 实现的 SHA256 算法，支持所有 Kotlin Multiplatform 平台
     * @param verifier code_verifier 字符串
     * @return code_challenge base64url 编码的 SHA256 哈希值
     */
    @OptIn(ExperimentalEncodingApi::class)
    private fun generateCodeChallenge(verifier: String): String {
        val sha256Hash = verifier.encodeToByteArray().sha256()
        return Base64.UrlSafe.encode(sha256Hash).replace("=", "")
    }

    /**
     * 生成 OAuth 授权 URL
     * 用于 VDS 账户 OAuth 授权流程的第一步
     *
     * 端点：GET /api/proxy/account/sso/authorize
     * 官方文档：vds-docs/VDS 账户/授权端点（Authorize，account.sso.authorize）.md
     *
     * OAuth 流程说明：
     * 1. 生成授权 URL 并引导用户访问
     * 2. 用户登录并授权应用
     * 3. 浏览器重定向到 redirect_uri，附带授权码（code）和状态参数（state）
     * 4. 使用授权码调用 exchangeOAuthToken() 交换访问令牌
     *
     * 参数说明：
     * - client_id: 应用 ID（格式 vap_xxxx）
     * - redirect_uri: 授权后重定向 URI（必须是已在开放平台配置的 URI）
     * - response_type: 响应类型，固定为 "code"
     * - scope: 权限范围（可选），多个权限用空格分隔
     * - state: 状态参数（可选），用于防止 CSRF 攻击，回调时会原样返回
     * - code_challenge: PKCE code_challenge（可选），用于增强安全性
     * - code_challenge_method: PKCE 算法，固定为 "SHA256"
     *
     * 示例：
     * ```
     * val params = OAuthAuthorizeParams(
     *     clientId = "vap_xxxxx",
     *     redirectUri = "http://localhost:8080/callback",
     *     state = "random_state_string",
     *     scope = "user.profile",
     *     codeChallenge = codeChallenge,
     *     codeChallengeMethod = "SHA256"
     * )
     * val authorizeUrl = authManager.getOAuthAuthorizeUrl(params)
     * // 打开浏览器访问 authorizeUrl
     * ```
     *
     * @param params OAuth 授权参数，包含客户端 ID、重定向 URI 等
     * @return 完整的授权 URL，用户访问此 URL 进行授权
     * @see OAuthAuthorizeParams 参数数据类
     * @see initWithOAuth 完整的 OAuth 初始化流程
     */
    public fun getOAuthAuthorizeUrl(params: OAuthAuthorizeParams): String {
        val queryParams =
            buildString {
                append("?client_id=${params.clientId}")
                append("&redirect_uri=${params.redirectUri}")
                append("&response_type=${params.responseType}")
                params.scope?.let { append("&scope=$it") }
                params.state?.let { append("&state=$it") }
                params.codeChallenge?.let { append("&code_challenge=$it") }
                params.codeChallengeMethod?.let { append("&code_challenge_method=$it") }
            }
        return "${config.baseUrl}/api/proxy/account/sso/authorize$queryParams"
    }

    /**
     * 使用 OAuth 初始化 SDK
     * 完整的 OAuth 授权流程实现
     *
     * 官方文档：VDS 账户/VDS 账户快速接入（OAuth）.md
     *
     * OAuth 流程步骤：
     * 1. 创建 OAuthCallbackHandler（自动选择平台实现）
     * 2. 生成随机 state 参数（用于防止 CSRF 攻击）
     * 3. 如果启用了 PKCE，生成 code_verifier 和 code_challenge
     * 4. 构建授权 URL（调用 getOAuthAuthorizeUrl）
     * 5. 等待回调（handler 会自动打开浏览器）
     * 6. 验证 state 参数（确保回调来源可信）
     * 7. 使用授权码交换 access_token（调用 exchangeOAuthToken）
     * 8. 返回 TokenInfo 并更新内部状态
     *
     * PKCE 支持：
     * - 当 config.enablePkce = true 时启用 PKCE 流程
     * - 自动生成 code_verifier（43-128 字符的随机字符串）
     * - 计算 code_challenge（SHA256 哈希的 base64url 编码）
     * - 在令牌交换时发送 code_verifier 进行验证
     *
     * 认证说明：
     * - OAuth 流程从 SDK 配置自动获取 clientId 和 clientSecret
     * - 无需在方法参数中传入
     * - OAuth token（access_token）仅用于 getUserInfo() 接口
     * - Client token（签名交换的 token）和 OAuth token 不通用
     *
     * 示例：
     * ```
     * val oauthConfig = OAuthConfig(
     *     callbackHost = "localhost",
     *     callbackPort = 8080,
     *     callbackPath = "/callback",
     *     enablePkce = true  // 启用 PKCE 增强安全性
     * )
     *
     * val tokenInfo = authManager.initOAuth(oauthConfig, "user.profile")
     * println("访问令牌：${tokenInfo.accessToken}")
     * println("刷新令牌：${tokenInfo.refreshToken}")
     * ```
     *
     * @param config OAuth 配置（包含回调主机、端口、超时等）
     * @param scope 可选的权限范围（如 "user.profile"），多个权限用空格分隔
     * @return TokenInfo 包含访问令牌、刷新令牌和有效期信息
     * @throws OAuthCallbackException 当回调失败、state 验证失败或令牌交换失败时抛出
     * @throws IllegalStateException 当 SDK 配置中缺少 clientId 时抛出
     * @see OAuthConfig OAuth 配置数据类
     * @see OAuthCallbackHandler 回调处理器
     * @see exchangeOAuthToken 令牌交换方法
     */
    @Suppress("LongMethod", "ThrowsCount", "MaxLineLength")
    public suspend fun initOAuth(config: OAuthConfig, scope: String? = null): TokenInfo {
        // From SDK config, get clientId and clientSecret
        val clientId = this.config.clientId
            ?: throw IllegalStateException("clientId is required for OAuth")
        val clientSecret = this.config.clientSecret
            ?: throw IllegalStateException("clientSecret is required for OAuth")

        val serverConfig = OAuthCallbackServerConfig(
            callbackHost = config.callbackHost,
            callbackPort = config.callbackPort,
            callbackPath = config.callbackPath,
            timeoutSeconds = config.stateTimeoutMinutes * 60L,
        )
        val handler = OAuthCallbackHandler(serverConfig)

        val state =
            Random.nextBytes(16).let { bytes ->
                bytes.joinToString("") { byte ->
                    val hex = byte.toInt().and(0xFF).toString(16)
                    if (hex.length == 1) "0$hex" else hex
                }
            }
        val redirectUri = handler.callbackUrl

        var codeVerifier: String? = null
        var codeChallenge: String? = null
        var codeChallengeMethod: String? = null

        if (config.enablePkce) {
            codeVerifier = generateCodeVerifier()
            codeChallenge = generateCodeChallenge(codeVerifier)
            codeChallengeMethod = "SHA256"
        }

        val authorizeParams =
            OAuthAuthorizeParams(
                clientId = clientId,
                redirectUri = redirectUri,
                state = state,
                scope = scope,
                codeChallenge = codeChallenge,
                codeChallengeMethod = codeChallengeMethod,
            )

        val authorizeUrl = getOAuthAuthorizeUrl(authorizeParams)

        // 等待回调（handler 会处理打开浏览器等逻辑）
        val result = handler.startAndGetCallback(authorizeUrl)

        return when (result) {
            is OAuthCallbackResult.Success -> {
                // 验证 state
                if (result.state != state) {
                    throw OAuthCallbackException(
                        "State mismatch: expected $state, got ${result.state}",
                    )
                }

                // 保存 OAuth 参数以供后续刷新使用
                oauthClientId = clientId
                oauthRedirectUri = redirectUri

                // 交换令牌
                val tokenRequest =
                    OAuthTokenRequest(
                        clientId = clientId,
                        clientSecret = clientSecret,
                        code = result.code,
                        redirectUri = redirectUri,
                        codeVerifier = codeVerifier,
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
     *
     * 端点：POST /api/proxy/account/sso/token
     * 官方文档：VDS 账户/VDS 账户快速接入（OAuth）.md
     *
     * 请求格式：
     * - Content-Type: application/x-www-form-urlencoded
     * - Authorization: Bearer <开放平台签名> (使用 config.apiKey) - 符合官方文档要求
     * - 请求体参数：
     *   - grant_type: 授权类型（authorization_code 或 refresh_token）
     *   - client_id: 应用 ID
     *   - client_secret: 应用密钥（开放平台签名）
     *   - code: 授权码（从 OAuth 授权回调中获取）
     *   - redirect_uri: 重定向 URI（必须与授权时一致）
     *   - code_verifier: PKCE code_verifier（可选，启用 PKCE 时必需）
     *
     * 注意：
     * - 此方法使用 config.apiKey 作为 Authorization header，符合官方文档要求
     * - OAuth token（access_token）仅用于 getUserInfo() 接口，不用于此接口
     * - Client token（签名交换的 token）和 OAuth token 不通用
     *
     * 示例：
     * ```
     * val tokenRequest = OAuthTokenRequest(
     *     appId = "vap_xxxxx",
     *     clientSecret = sdkConfig.apiKey,
     *     code = authCode,
     *     redirectUri = "http://localhost:8080/callback",
     *     codeVerifier = codeVerifier
     * )
     * val tokenInfo = authManager.exchangeOAuthToken(tokenRequest)
     * ```
     *
     * @param request OAuth 令牌请求，包含授权码和认证信息
     * @return TokenInfo 包含访问令牌、刷新令牌和有效期信息
     * @throws OAuthCallbackException 当令牌交换失败时抛出
     */
    public suspend fun exchangeOAuthToken(request: OAuthTokenRequest): TokenInfo {
        val requestBody =
            mutableMapOf(
                "grant_type" to request.grantType,
                "client_id" to request.clientId,
                "client_secret" to request.clientSecret,
                "code" to request.code,
                "redirect_uri" to request.redirectUri,
            )

        request.codeVerifier?.let {
            requestBody["code_verifier"] = it
        }

        val response =
            httpClient.post("${config.baseUrl}/api/proxy/account/sso/token") {
                contentType(ContentType.Application.FormUrlEncoded)
                header("Authorization", "Bearer ${config.apiKey}")
                setBody(requestBody)
            }.body<OAuthTokenResponse>()

        val newTokenInfo =
            TokenInfo(
                accessToken = response.data.accessToken,
                apiKey = "", // OAuth 流程没有 apiKey，设置为空字符串
                expiresAt = Clock.System.now().toEpochMilliseconds() + (response.data.expiresIn * 1000),
                tokenType = response.data.tokenType,
                refreshToken = response.data.refreshToken,
            )

        tokenMutex.withLock {
            tokenInfo = newTokenInfo
            isOAuthToken = true // 标记为 OAuth 令牌
            updateHttpClient()
        }

        return newTokenInfo
    }

    /**
     * 刷新 OAuth 令牌
     * 使用 refresh_token 获取新的访问令牌
     *
     * 端点：POST /api/proxy/account/sso/token
     * 官方文档：VDS 账户/VDS 账户快速接入（OAuth）.md
     *
     * 刷新机制说明：
     * - 仅适用于 OAuth 流程获取的令牌（isOAuthToken = true）
     * - 需要有效的 refresh_token（在初次令牌交换时获取）
     * - 使用 client_id 和 client_secret 进行认证（使用 config.apiKey）
     * - refresh_token 使用后可能轮换（取决于服务端配置）
     *
     * 请求参数：
     * - grant_type: "refresh_token"
     * - refresh_token: 刷新令牌
     * - client_id: 应用 ID（OAuth 流程中使用的 appId）
     * - client_secret: 应用密钥（开放平台签名）
     * - redirect_uri: 重定向 URI（必须与授权时一致）
     *
     * 注意：
     * - 此方法使用 config.apiKey 作为 Authorization header
     * - OAuth token（access_token）仅用于 getUserInfo() 接口，不用于刷新流程
     * - Client token（签名交换的 token）和 OAuth token 不通用
     *
     * 示例：
     * ```
     * val newTokenInfo = authManager.refreshOAuthToken()
     * println("新的访问令牌：${newTokenInfo.accessToken}")
     * println("新的刷新令牌：${newTokenInfo.refreshToken}")
     * ```
     *
     * @return TokenInfo 新的令牌信息，包含新的 access_token 和可能的 refresh_token
     * @throws TokenExpiredException 如果没有可用的 refresh_token
     * @throws IllegalStateException 如果 OAuth client_id 或 redirect_uri 不可用
     */
    @Suppress("ThrowsCount")
    public suspend fun refreshOAuthToken(): TokenInfo {
        val refreshToken =
            tokenInfo?.refreshToken
                ?: throw TokenExpiredException("No refresh token available")

        val clientId = oauthClientId ?: throw IllegalStateException("OAuth client ID not available")
        val redirectUri = oauthRedirectUri ?: throw IllegalStateException("OAuth redirect URI not available")

        val response =
            httpClient.post("${config.baseUrl}/api/proxy/account/sso/token") {
                contentType(ContentType.Application.FormUrlEncoded)
                header("Authorization", "Bearer ${config.apiKey}")
                setBody(
                    mapOf(
                        "grant_type" to "refresh_token",
                        "refresh_token" to refreshToken,
                        "client_id" to clientId,
                        "client_secret" to config.apiKey,
                        "redirect_uri" to redirectUri,
                    ),
                )
            }.body<OAuthTokenResponse>()

        val newTokenInfo =
            TokenInfo(
                accessToken = response.data.accessToken,
                apiKey = "",
                expiresAt = Clock.System.now().toEpochMilliseconds() + (response.data.expiresIn * 1000),
                tokenType = response.data.tokenType,
                refreshToken = response.data.refreshToken ?: tokenInfo?.refreshToken,
            )

        tokenMutex.withLock {
            tokenInfo = newTokenInfo
            isOAuthToken = true
            updateHttpClient()
        }

        return newTokenInfo
    }

    /**
     * 获取用户信息
     * 获取当前 OAuth 认证用户的详细信息
     *
     * 端点：GET /api/proxy/account/sso/userinfo
     * 官方文档：VDS 账户/用户信息端点（UserInfo，account.sso.userinfo）.md
     *
     * 重要说明：
     * - OAuth access_token 仅用于此接口（getUserInfo），其他接口使用签名交换的 token
     * - 此接口使用 X-OAuth-Access-Token + Authorization 双 header 认证
     *   - X-OAuth-Access-Token: OAuth access_token（必需，用于标识用户身份）
     *   - Authorization: Bearer <开放平台签名>（可选，当 config.apiKey 不为空时用于验证应用身份）
     *
     * 支持两种认证场景：
     * - 场景 A：有开放平台签名（config.apiKey 不为空）- 使用双 header（Authorization + X-OAuth-Access-Token）
     * - 场景 B：纯 OAuth（config.apiKey 为空）- 只使用 X-OAuth-Access-Token header
     *
     * 响应字段说明：
     * - sub: 用户唯一标识符
     * - nickname: 用户昵称
     * - avatarUrl: 用户头像 URL
     * - email: 用户邮箱
     * - name: 用户姓名
     * - username: 用户名
     * - phoneNumber: 用户电话号码
     * - updatedAt: 用户信息更新时间戳（毫秒）
     *
     * 示例：
     * ```
     * val userInfo = authManager.getUserInfo()
     * println("用户昵称：${userInfo.nickname}")
     * println("用户 ID: ${userInfo.sub}")
     * ```
     *
     * @return UserInfoData 用户信息数据，包含用户的基本资料
     * @throws OAuthCallbackException 当认证失败或令牌无效时抛出
     */
    public suspend fun getUserInfo(): UserInfoData {
        val response =
            httpClient.get("${config.baseUrl}/api/proxy/account/sso/userinfo") {
                // X-OAuth-Access-Token: OAuth access_token 用于标识用户（必须）
                header("X-OAuth-Access-Token", tokenInfo?.accessToken ?: "")

                // Authorization: Bearer <开放平台签名> 用于验证应用身份（可选）
                // 场景 A：config.apiKey 不为空时，使用双 header
                // 场景 B：config.apiKey 为空时（纯 OAuth），只使用 X-OAuth-Access-Token
                if (config.apiKey.isNotEmpty()) {
                    header("Authorization", "Bearer ${config.apiKey}")
                }
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
        // 智能选择逻辑由 HttpClientConfig 自动处理：
        // - 当 config.apiKey 存在时，使用 X-Api-Key
        // - 当仅有 accessToken 时，使用 Authorization Bearer
        httpClient =
            HttpClientConfig.createClient(
                config,
                if (isOAuthToken) tokenInfo?.accessToken else tokenInfo?.apiKey,
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
