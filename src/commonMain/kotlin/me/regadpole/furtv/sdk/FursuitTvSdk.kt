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
 * **认证方式说明**：
 * - Client 认证：适用于应用级 API，使用 X-Api-Key 或 Authorization Bearer
 *   - 通过签名交换接口（POST /api/auth/token）获取 accessToken 和 apiKey
 *   - 认证头：X-Api-Key: <apiKey> 或 Authorization: Bearer <accessToken>
 * - OAuth 认证：适用于用户授权场景，仅可用于 UserInfo 接口
 *   - 通过 OAuth 授权流程获取 access_token
 *   - 认证头：X-OAuth-Access-Token: <access_token> + Authorization: Bearer <apiKey>（双 header）
 *
 * 官方文档参考：
 * - vds-docs/基础接口/签名交换.md - 签名交换流程
 * - vds-docs/认证方式与服务器端点.md - 认证方式说明
 * - vds-docs/VDS 账户/VDS 账户快速接入（OAuth）.md - OAuth 流程
 *
 * ```kotlin
 * // ========== 方式 1: 使用 apiKey（Client 认证）==========
 * // 认证头：X-Api-Key
 * // 适用场景：应用级 API
 * // 说明：apiKey 通过签名交换接口获取（使用 appId + appSecret）
 * val sdk1 = FursuitTvSdk(apiKey = "your-api-key")
 *
 * // ========== 方式 2: 使用 clientId + clientSecret（推荐，Client 认证）==========
 * // 认证头：Authorization: Bearer <accessToken>
 * // 适用场景：应用级 API
 * // 说明：通过签名交换获取 accessToken
 * // 官方文档：vds-docs/基础接口/签名交换.md
 * val sdk2 = FursuitTvSdk(
 *     clientId = "vap_xxxxxxxxxxxxxxxx",
 *     clientSecret = "your-client-secret"
 * )
 * runBlocking {
 *     sdk2.auth.exchangeToken(clientId, clientSecret)
 * }
 *
 * // ========== 方式 3: 使用 accessToken（Client 认证）==========
 * // 认证头：Authorization: Bearer <accessToken>
 * // 适用场景：应用级 API
 * val sdk3 = FursuitTvSdk(accessToken = "your-access-token")
 *
 * // ========== 方式 4: 使用 OAuth 认证（OAuth 2.0 授权码模式）==========
 * // 认证头：X-OAuth-Access-Token: <oauth-token> + Authorization: Bearer <apiKey>
 * // 适用场景：用户授权场景（仅可用于 UserInfo 接口）
 * // 前置条件：必须先调用 exchangeToken() 获取 Client accessToken
 * // 警告：OAuth token 和 Client token 不通用，不能混用
 * // 官方文档：vds-docs/VDS 账户/VDS 账户快速接入（OAuth）.md
 * runBlocking {
 *     // 第一步：先通过签名交换获取 Client accessToken
 *     val clientSdk = FursuitTvSdk(clientId = "vap_xxxxx", clientSecret = "secret")
 *     clientSdk.auth.exchangeToken("vap_xxxxx", "secret")
 *
 *     // 第二步：使用 OAuth 获取用户授权
 *     val config = OAuthConfig(callbackHost = "localhost", callbackPort = 8080)
 *     val oauthSdk = FursuitTvSdk.initWithOAuth("vap_xxxxx", config)
 *
 *     // 第三步：调用 UserInfo 接口
 *     val userInfo = oauthSdk.user.getUserProfile("username")
 * }
 *
 * // ========== 调用 API ==========
 * val userProfile = sdk.user.getUserProfile("username")
 * val popularUsers = sdk.search.getPopular()
 *
 * // ========== 关闭 SDK ==========
 * sdk.close()
 * ```
 *
 * @see [签名交换](基础接口/签名交换.md)
 * @see [认证方式与服务器端点](认证方式与服务器端点.md)
 * @see [VDS 账户快速接入（OAuth）](VDS 账户/VDS 账户快速接入（OAuth）.md)
 */
public class FursuitTvSdk {
    private var httpClient: HttpClient
    private val config: SdkConfig

    /**
     * 使用 API 密钥初始化 SDK（Java 友好）
     * 使用默认配置创建 SDK 实例
     *
     * **认证方式**：X-Api-Key（Client 认证）
     * **适用场景**：应用级 API
     *
     * 官方文档：vds-docs/认证方式与服务器端点.md
     *
     * @param apiKey VDS 颁发的 API 密钥，用于认证和授权
     *
     * @see [签名交换](基础接口/签名交换.md)
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
     * **认证方式**：Authorization Bearer（Client 认证）
     * **适用场景**：应用级 API
     *
     * 官方文档：vds-docs/认证方式与服务器端点.md
     *
     * @param accessToken 访问令牌
     * @param baseUrl API 基础 URL，默认为 https://open-global.vdsentnet.com
     *
     * @see [认证方式与服务器端点](认证方式与服务器端点.md)
     */
    public constructor(accessToken: String, baseUrl: String = "https://open-global.vdsentnet.com") {
        this.config =
            SdkConfig.builder()
                .apiKey("")
                .baseUrl(baseUrl)
                .build()
        this.httpClient = HttpClientConfig.createClient(config, accessToken)
        this.auth = AuthManager(config)
    }

    /**
     * 使用 clientId 和 clientSecret 初始化 SDK（推荐方式）
     * 适用于新用户，通过签名交换获取 accessToken
     *
     * **认证方式**：Authorization Bearer（Client 认证）
     * **适用场景**：应用级 API
     *
     * 官方文档：vds-docs/基础接口/签名交换.md
     *
     * @param clientId 客户端 ID（格式 vap_xxxx），与 appId 等价
     * @param clientSecret 客户端密钥，与 appSecret 等价
     * @param baseUrl API 基础 URL，默认为 https://open-global.vdsentnet.com
     *
     * @see [签名交换](基础接口/签名交换.md)
     */
    public constructor(clientId: String, clientSecret: String, baseUrl: String = "https://open-global.vdsentnet.com") {
        this.config =
            SdkConfig.builder()
                .apiKey("") // 临时空 apiKey，后续通过 exchangeToken 获取
                .clientId(clientId)
                .clientSecret(clientSecret)
                .baseUrl(baseUrl)
                .build()
        this.auth = AuthManager(config)
        // 注意：此构造函数不会自动获取令牌
        // 用户需要在协程作用域中手动调用 auth.exchangeToken(clientId, clientSecret)
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
     *
     * 包含的接口：
     * - [health] - 健康检查
     * - [getAndroidVersion] - Android 版本信息
     * - [checkAndroidVersion] - Android 版本检查
     * - [getThemePacksManifest] - 主题包清单
     */
    public val base: BaseApi by lazy { BaseApi(httpClient, config.baseUrl) }

    /**
     * 用户相关 API
     * 提供用户资料、关系、访客、徽章、商店等用户相关功能
     *
     * 包含的接口：
     * - [getUserProfile] - 用户资料公开信息
     * - [getUserId] - 用户基础信息 ID 查询
     * - [getLikeStatus] - 用户点赞状态
     * - [getUserRelationships] - 用户关系公开列表
     * - [getUserVisitors] - 用户访客记录
     * - [getSocialBadges] - 用户社交徽章列表
     * - [getSocialBadgeDetail] - 用户社交徽章详情
     * - [getStoreProducts] - 用户商店商品
     */
    public val user: UserApi by lazy { UserApi(httpClient, config.baseUrl) }

    /**
     * 搜索和发现 API
     * 提供热门推荐、随机推荐、搜索、物种查询等发现功能
     *
     * 包含的接口：
     * - [getPopularDiscovery] - 热门推荐
     * - [getRandomDiscovery] - 随机推荐
     * - [searchDiscovery] - 搜索
     * - [getSearchSuggestionsDiscovery] - 搜索建议
     * - [getSpeciesDiscovery] - 物种列表
     * - [searchBySpeciesDiscovery] - 按物种搜索
     * - [getPopularLocationsDiscovery] - 热门地区
     */
    public val search: SearchApi by lazy { SearchApi(httpClient, config.baseUrl) }

    /**
     * 聚会相关 API
     * 提供聚会列表、统计、详情、报名等聚会相关功能
     *
     * 包含的接口：
     * - [getYearStats] - 聚会年度统计
     * - [getMonthly] - 聚会月历
     * - [getMonthlyDistance] - 聚会月历距离
     * - [getNearby] - 聚会附近
     * - [getNearbyMode] - 聚会附近模式
     * - [getGatheringDetail] - 聚会详情
     * - [getRegistrations] - 聚会报名列表
     */
    public val gathering: GatheringApi by lazy { GatheringApi(httpClient, config.baseUrl) }

    /**
     * 学校和角色 API
     * 提供学校信息、角色管理等学校和角色相关功能
     *
     * 包含的接口：
     * - [searchSchools] - 学校搜索
     * - [getSchoolDetail] - 学校详情
     * - [getUserSchools] - 用户学校信息
     * - [getUserCharacters] - 用户角色列表
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
         * 使用 OAuth 2.0 初始化 SDK（静态方法）
         * @param clientId 客户端 ID（格式 vap_xxxx）
         * @param clientSecret 客户端密钥
         * @param config OAuth 配置
         * @return FursuitTvSdk 实例
         * @deprecated 请使用新的初始化方式：先创建 SDK 实例，然后调用 auth.initOAuth()
         */
        @Deprecated(
            "Use the new initialization pattern: create SDK instance first, then call auth.initOAuth()",
            ReplaceWith("FursuitTvSdk(clientId, clientSecret).apply { runBlocking { auth.initOAuth(config) } }")
        )
        public suspend fun initWithOAuth(clientId: String, clientSecret: String, config: OAuthConfig): FursuitTvSdk {
            val sdkConfig =
                SdkConfig.builder()
                    .apiKey("")
                    .clientId(clientId)
                    .clientSecret(clientSecret)
                    .baseUrl("https://open-global.vdsentnet.com")
                    .build()
            val sdk = FursuitTvSdk(sdkConfig)

            // 直接启动 OAuth 流程
            sdk.auth.initOAuth(config)

            return sdk
        }
    }
}
