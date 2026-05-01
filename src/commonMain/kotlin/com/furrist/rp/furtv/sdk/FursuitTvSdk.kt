package com.furrist.rp.furtv.sdk

import com.furrist.rp.furtv.sdk.auth.AuthManager
import com.furrist.rp.furtv.sdk.auth.TokenInfo
import com.furrist.rp.furtv.sdk.base.BaseApi
import com.furrist.rp.furtv.sdk.gathering.GatheringApi
import com.furrist.rp.furtv.sdk.http.HttpClientConfig
import com.furrist.rp.furtv.sdk.model.MutableSdkConfig
import com.furrist.rp.furtv.sdk.model.SdkConfig
import com.furrist.rp.furtv.sdk.school.SchoolApi
import com.furrist.rp.furtv.sdk.search.SearchApi
import com.furrist.rp.furtv.sdk.user.UserApi
import io.ktor.client.HttpClient
import kotlin.js.JsExport
import kotlin.js.JsName
import love.forte.plugin.suspendtrans.annotation.JvmAsync
import love.forte.plugin.suspendtrans.annotation.JvmBlocking

/**
 * Fursuit.TV SDK 主客户端，提供 base、user、search、gathering、school 等 API 模块的访问接口。
 *
 * @param config SDK 配置
 * @param tokenInfo 可选的令牌信息
 */
@JsExport
@JsName("FursuitTvSdk")
public class FursuitTvSdk internal constructor(
    private val config: SdkConfig,
    tokenInfo: TokenInfo? = null,
) {
    @JsName("_httpClient")
    private var httpClient: HttpClient =
        HttpClientConfig.createClient(
            config,
            tokenInfo?.takeIf { it.apiKey.isNotEmpty() }?.apiKey ?: tokenInfo?.accessToken,
        )

    /**
     * 认证管理器
     */
    @JsName("auth")
    public val auth: AuthManager =
        AuthManager(config).apply {
            tokenInfo?.let { setTokenInfo(it) }
        }

    /** 基础接口 API */
    @JsName("base")
    public val base: BaseApi = BaseApi(httpClient, config.baseUrl)

    /** 用户相关 API */
    @JsName("user")
    public val user: UserApi = UserApi(httpClient, config.baseUrl)

    /** 搜索和发现 API */
    @JsName("search")
    public val search: SearchApi = SearchApi(httpClient, config.baseUrl)

    /** 聚会相关 API */
    @JsName("gathering")
    public val gathering: GatheringApi = GatheringApi(httpClient, config.baseUrl)

    /** 学校和角色 API */
    @JsName("school")
    public val school: SchoolApi = SchoolApi(httpClient, config.baseUrl)

    /**
     * 获取当前配置。
     *
     * @return SDK 配置对象
     */
    @JsName("getConfig")
    public fun getConfig(): SdkConfig = config

    /**
     * 关闭 SDK 客户端并释放资源
     */
    @JsName("close")
    public fun close() {
        httpClient.close()
        auth.close()
    }

    @JsName("updateHttpClient")
    internal fun updateHttpClient(accessToken: String?) {
        httpClient = HttpClientConfig.createClient(config, accessToken)
    }

    public companion object {
        /**
         * 为签名交换创建 SDK，自动获取令牌。
         *
         * @param clientId 客户端 ID（即 VDS 文档中的 appId，格式 vap_xxxx）
         * @param clientSecret 客户端密钥
         * @return FursuitTvSdk 实例
         */
        @JvmBlocking
        @JvmAsync
        @JsName("createForTokenExchange")
        public suspend fun createForTokenExchange(clientId: String, clientSecret: String): FursuitTvSdk {
            val config = SdkConfig.forTokenExchange(clientId, clientSecret)
            val authManager = AuthManager(config)
            val tokenInfo = authManager.exchangeToken(clientId, clientSecret)
            return FursuitTvSdk(config, tokenInfo)
        }

        /**
         * 使用 API 密钥创建 SDK。
         *
         * @param apiKey VDS 颁发的 API 密钥
         * @return FursuitTvSdk 实例
         */
        @JsName("createWithApiKey")
        public fun create(apiKey: String): FursuitTvSdk =
            FursuitTvSdk(SdkConfig.withApiKey(apiKey))

        /**
         * 使用配置对象创建 SDK。
         *
         * @param config SDK 配置
         * @param tokenInfo 可选的令牌信息
         * @return FursuitTvSdk 实例
         */
        @JsName("createWithConfig")
        public fun create(config: SdkConfig, tokenInfo: TokenInfo? = null): FursuitTvSdk =
            FursuitTvSdk(config, tokenInfo)

        /**
         * 使用 DSL 方式创建 SDK，如配置中包含 clientId + clientSecret 则自动交换令牌。
         *
         * @param block 配置块
         * @return FursuitTvSdk 实例
         */
        @JvmBlocking
        @JvmAsync
        @JsName("createWithDsl")
        public suspend fun create(block: MutableSdkConfig.() -> Unit): FursuitTvSdk {
            val mutableConfig = MutableSdkConfig()
            mutableConfig.block()
            val config = mutableConfig.toImmutable()

            if (config.clientId != null && config.clientSecret != null && config.apiKey == null) {
                val authManager = AuthManager(config)
                val tokenInfo = authManager.exchangeToken(config.clientId, config.clientSecret)
                return FursuitTvSdk(config, tokenInfo)
            }

            return FursuitTvSdk(config)
        }
    }
}

/**
 * 使用 DSL 方式创建 FursuitTvSdk，为 suspend 函数，当 clientId + clientSecret 均提供时自动交换令牌。
 *
 * @param block 配置块
 * @return FursuitTvSdk 实例
 */
@JvmBlocking
@JvmAsync
@JsExport
@JsName("fursuitTvSdk")
public suspend fun fursuitTvSdk(block: (MutableSdkConfig) -> Unit): FursuitTvSdk {
    val mutableConfig = MutableSdkConfig()
    block(mutableConfig)
    val config = mutableConfig.toImmutable()

    if (config.clientId != null && config.clientSecret != null && config.apiKey == null) {
        val authManager = AuthManager(config)
        val tokenInfo = authManager.exchangeToken(config.clientId, config.clientSecret)
        return FursuitTvSdk(config, tokenInfo)
    }

    return FursuitTvSdk(config)
}
