package me.regadpole.furtv.sdk

import io.ktor.client.HttpClient
import me.regadpole.furtv.sdk.auth.AuthManager
import me.regadpole.furtv.sdk.auth.OAuthConfig
import me.regadpole.furtv.sdk.auth.TokenInfo
import me.regadpole.furtv.sdk.base.BaseApi
import me.regadpole.furtv.sdk.gathering.GatheringApi
import me.regadpole.furtv.sdk.http.HttpClientConfig
import me.regadpole.furtv.sdk.model.SdkConfig
import me.regadpole.furtv.sdk.school.SchoolApi
import me.regadpole.furtv.sdk.search.SearchApi
import me.regadpole.furtv.sdk.user.UserApi

/**
 * Fursuit.TV SDK 主客户端
 *
 * 提供多种初始化方式，适用于不同的使用场景：
 *
 * ```kotlin
 * // 方式 1: 使用 apiKey（适用于已有 apiKey 的用户）
 * // 认证头：X-Api-Key
 * // 说明：apiKey 通过签名交换接口获取（使用 appId + appSecret）
 * val sdk = FursuitTvSdk(apiKey = "your-api-key")
 *
 * // 方式 2: 使用 appId + appSecret（推荐，适用于新用户）
 * // 认证头：Authorization: Bearer <accessToken>
 * // 说明：SDK 会自动调用签名交换接口获取 accessToken（即 apiKey）
 * val sdk = FursuitTvSdk(
 *     appId = "vap_xxxxxxxxxxxxxxxx",
 *     appSecret = "your-app-secret"
 * )
 * runBlocking {
 *     sdk.auth.exchangeToken(appId, appSecret)
 * }
 *
 * // 方式 3: 使用 OAuth 认证（适用于需要用户授权的应用）
 * // 认证头：Authorization: Bearer <oauth-token>
 * // 说明：完整的 OAuth 2.0 授权码模式，需要用户登录授权
 * val config = OAuthConfig(callbackHost = "localhost", callbackPort = 8080)
 * val sdk = FursuitTvSdk.initWithOAuth("vap_xxxxxxxxxxxxxxxx", config)
 *
 * // 方式 4: 使用 accessToken（适用于已有访问令牌的用户）
 * // 认证头：Authorization: Bearer <accessToken>
 * // 说明：accessToken 即 apiKey，两者是同一个值
 * val sdk = FursuitTvSdk(accessToken = "your-access-token")
 *
 * // 调用 API
 * val userProfile = sdk.user.getUserProfile("username")
 * val popularUsers = sdk.search.getPopular()
 *
 * // 关闭 SDK
 * sdk.close()
 * ```
 */
public class FursuitTvSdk {
    private var httpClient: HttpClient
    private val config: SdkConfig

    /**
     * 使用 API 密钥初始化 SDK（Java 友好）
     * 使用默认配置创建 SDK 实例
     *
     * 认证头：X-Api-Key
     *
     * @param apiKey VDS 颁发的 API 密钥，用于认证和授权
     */
    public constructor(apiKey: String) {
        this.config = SdkConfig.default(apiKey)
        this.httpClient = HttpClientConfig.createClient(config, null)
        this.auth = AuthManager(config)
    }

    /**
     * 使用 API 密钥和自定义基础 URL 初始化 SDK
     *
     * 认证头：X-Api-Key
     *
     * @param apiKey VDS 颁发的 API 密钥
     * @param baseUrl API 基础 URL，默认为 https://open-global.vdsentnet.com
     * @param tokenInfo 可选的令牌信息，用于恢复之前的认证状态
     */
    @Suppress("LongParameterList")
    public constructor(
        apiKey: String,
        baseUrl: String = "https://open-global.vdsentnet.com",
        tokenInfo: TokenInfo? = null,
    ) {
        this.config =
            SdkConfig.builder()
                .apiKey(apiKey)
                .baseUrl(baseUrl)
                .build()
        this.httpClient = HttpClientConfig.createClient(config, tokenInfo?.accessToken)
        this.auth =
            AuthManager(config).apply {
                tokenInfo?.let { setTokenInfo(it) }
            }
    }

    /**
     * 使用访问令牌初始化 SDK
     * 适用于已有访问令牌的用户（例如从 OAuth 流程或其他认证方式获得）
     *
     * 认证头：Authorization: Bearer <accessToken>
     *
     * @param accessToken 访问令牌
     * @param baseUrl API 基础 URL，默认为 https://api.fursuit.tv
     */
    public constructor(accessToken: String, baseUrl: String = "https://api.fursuit.tv") {
        this.config =
            SdkConfig.builder()
                .apiKey("")
                .baseUrl(baseUrl)
                .build()
        this.httpClient = HttpClientConfig.createClient(config, accessToken)
        this.auth = AuthManager(config)
    }

    /**
     * 使用 appId 和 appSecret 初始化 SDK（推荐方式）
     * 适用于新用户，SDK 会自动调用签名交换接口获取 accessToken（即 apiKey）
     * 并自动管理令牌的刷新（当剩余有效期 <= 300 秒时）
     *
     * 认证头：Authorization: Bearer <accessToken>
     *
     * @param appId 应用 ID（格式 vap_xxxx）
     * @param appSecret 应用密钥
     * @param baseUrl API 基础 URL，默认为 https://open-global.vdsentnet.com
     */
    public constructor(appId: String, appSecret: String, baseUrl: String = "https://open-global.vdsentnet.com") {
        this.config =
            SdkConfig.builder()
                .apiKey("") // 临时空 apiKey，后续通过 exchangeToken 获取
                .baseUrl(baseUrl)
                .build()
        this.auth = AuthManager(config)
        // 注意：此构造函数不会自动获取令牌
        // 用户需要在协程作用域中手动调用 auth.exchangeToken(appId, appSecret)
        // 或者使用 AuthManager 提供的其他异步方法
        this.httpClient = HttpClientConfig.createClient(config, null)
    }

    /**
     * 使用自定义配置初始化 SDK
     * 允许完全自定义 SDK 的各种参数
     *
     * 认证头：X-Api-Key 或 Authorization: Bearer（取决于配置）
     *
     * @param config SDK 配置对象，允许自定义各种参数
     * @param tokenInfo 可选的令牌信息，用于恢复之前的认证状态
     */
    public constructor(config: SdkConfig, tokenInfo: TokenInfo? = null) {
        this.config = config
        this.httpClient = HttpClientConfig.createClient(config, tokenInfo?.accessToken)
        this.auth =
            AuthManager(config).apply {
                tokenInfo?.let { setTokenInfo(it) }
            }
    }

    /**
     * 认证管理器
     * 提供令牌交换、刷新、OAuth 授权等认证相关功能
     */
    public val auth: AuthManager

    /**
     * 基础接口 API
     * 提供 HelloWorld 验证、健康检查、版本检查、主题包等基础功能
     */
    public val base: BaseApi by lazy { BaseApi(httpClient, config.baseUrl) }

    /**
     * 用户相关 API
     * 提供用户资料、关系、访客、徽章、商店等用户相关功能
     */
    public val user: UserApi by lazy { UserApi(httpClient, config.baseUrl) }

    /**
     * 搜索和发现 API
     * 提供热门推荐、随机推荐、搜索、物种查询等发现功能
     */
    public val search: SearchApi by lazy { SearchApi(httpClient, config.baseUrl) }

    /**
     * 聚会相关 API
     * 提供聚会列表、统计、详情、报名等聚会相关功能
     */
    public val gathering: GatheringApi by lazy { GatheringApi(httpClient, config.baseUrl) }

    /**
     * 学校和角色 API
     * 提供学校信息、角色管理等学校和角色相关功能
     */
    public val school: SchoolApi by lazy { SchoolApi(httpClient, config.baseUrl) }

    /**
     * 更新 HTTP 客户端（当令牌变化时）
     * 内部使用，用于在令牌更新后重新配置 HTTP 客户端
     * @param accessToken 新的访问令牌
     */
    internal fun updateHttpClient(accessToken: String?) {
        httpClient = HttpClientConfig.createClient(config, accessToken)
    }

    /**
     * 获取当前配置
     * @return 当前 SDK 配置对象
     */
    public fun getConfig(): SdkConfig {
        return config
    }

    /**
     * 关闭 SDK 客户端
     * 释放 HTTP 客户端和认证管理器占用的资源
     * 在不再需要 SDK 时应调用此方法
     */
    public fun close() {
        httpClient.close()
        auth.close()
    }

    /**
     * 伴生对象，提供静态工厂方法
     */
    public companion object {
        /**
         * 使用 OAuth 认证初始化 SDK（静态方法）
         * 此方法提供一个便捷的 OAuth 初始化入口，自动完成 OAuth 授权流程
         *
         * 注意：此方法是 suspend 函数，会阻塞直到 OAuth 流程完成
         * 需要在协程作用域中调用
         *
         * @param appId 应用 ID
         * @param config OAuth 配置，包含回调服务器等信息
         * @return 初始化后的 FursuitTvSdk 实例
         *
         * 使用示例：
         * ```kotlin
         * runBlocking {
         *     val config = OAuthConfig(
         *         callbackHost = "localhost",
         *         callbackPort = 8080,
         *         callbackPath = "/callback"
         *     )
         *     val sdk = FursuitTvSdk.initWithOAuth("your-app-id", config)
         * }
         * ```
         */
        public suspend fun initWithOAuth(appId: String, config: OAuthConfig): FursuitTvSdk {
            val sdkConfig =
                SdkConfig.builder()
                    .apiKey("")
                    .baseUrl("https://open-global.vdsentnet.com")
                    .build()
            val sdk = FursuitTvSdk(sdkConfig)

            // 直接启动 OAuth 流程
            sdk.auth.initWithOAuth(appId, config)

            return sdk
        }
    }
}
