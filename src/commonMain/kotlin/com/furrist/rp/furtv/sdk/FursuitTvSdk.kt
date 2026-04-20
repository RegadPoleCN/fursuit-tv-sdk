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
import kotlinx.coroutines.runBlocking

/**
 * Fursuit.TV SDK 主客户端，提供对平台所有 API 的访问接口。
 *
 * @param config SDK 配置
 * @param tokenInfo 可选的令牌信息
 */
public class FursuitTvSdk private constructor(
    config: SdkConfig,
    tokenInfo: TokenInfo? = null,
) {
    private var httpClient: HttpClient = HttpClientConfig.createClient(config, tokenInfo?.accessToken)
    private val config: SdkConfig = config

    /**
     * 认证管理器
     */
    public val auth: AuthManager = AuthManager(config).apply {
        tokenInfo?.let { setTokenInfo(it) }
    }

    /** 基础接口 API */
    public val base: BaseApi by lazy { BaseApi(httpClient, config.baseUrl) }

    /** 用户相关 API */
    public val user: UserApi by lazy { UserApi(httpClient, config.baseUrl) }

    /** 搜索和发现 API */
    public val search: SearchApi by lazy { SearchApi(httpClient, config.baseUrl) }

    /** 聚会相关 API */
    public val gathering: GatheringApi by lazy { GatheringApi(httpClient, config.baseUrl) }

    /** 学校和角色 API */
    public val school: SchoolApi by lazy { SchoolApi(httpClient, config.baseUrl) }

    /**
     * 获取当前配置
     * @return SDK 配置对象
     */
    public fun getConfig(): SdkConfig = config

    /**
     * 关闭 SDK 客户端并释放资源
     */
    public fun close() {
        httpClient.close()
        auth.close()
    }

    internal fun updateHttpClient(accessToken: String?) {
        httpClient = HttpClientConfig.createClient(config, accessToken)
    }

    public companion object {
        /**
     * 为签名交换创建 SDK（自动获取令牌）。
     *
     * @param clientId 应用 ID（格式 vap_xxxx）
     * @param clientSecret 应用密钥
     * @return FursuitTvSdk 实例
     */
        @JvmStatic
        public fun createForTokenExchange(clientId: String, clientSecret: String): FursuitTvSdk {
            val config = SdkConfig.forTokenExchange(clientId, clientSecret)
            // 自动调用签名交换获取令牌
            val authManager = AuthManager(config)
            val tokenInfo = runBlocking {
                authManager.exchangeToken(clientId, clientSecret)
            }
            return FursuitTvSdk(config, tokenInfo)
        }

        /**
         * 使用 API 密钥创建 SDK。
         *
         * @param apiKey VDS 颁发的 API 密钥
         * @return FursuitTvSdk 实例
         */
        @JvmStatic
        public fun create(apiKey: String): FursuitTvSdk =
            FursuitTvSdk(SdkConfig.withApiKey(apiKey))

        /**
         * 使用配置对象创建 SDK。
         *
         * @param config SDK 配置
         * @param tokenInfo 可选的令牌信息
         * @return FursuitTvSdk 实例
         */
        @JvmStatic
        public fun create(config: SdkConfig, tokenInfo: TokenInfo? = null): FursuitTvSdk =
            FursuitTvSdk(config, tokenInfo)

        /**
         * 使用 DSL 方式创建 SDK（如配置中包含 clientId + clientSecret，自动获取令牌）。
         *
         * @param block 配置块
         * @return FursuitTvSdk 实例
         */
        @JvmStatic
        public fun create(block: MutableSdkConfig.() -> Unit): FursuitTvSdk {
            val mutableConfig = MutableSdkConfig()
            mutableConfig.block()
            val config = mutableConfig.toImmutable()
            
            // 如果配置中包含 clientId 和 clientSecret，自动调用签名交换
            if (config.clientId != null && config.clientSecret != null && config.apiKey == null) {
                val authManager = AuthManager(config)
                val tokenInfo = runBlocking {
                    authManager.exchangeToken(config.clientId, config.clientSecret)
                }
                return FursuitTvSdk(config, tokenInfo)
            }
            
            return FursuitTvSdk(config)
        }
    }
}

/**
 * 使用 DSL 方式创建 FursuitTvSdk。
 *
 * @param block 配置块
 * @return FursuitTvSdk 实例
 */
public fun fursuitTvSdk(block: MutableSdkConfig.() -> Unit = {}): FursuitTvSdk =
    FursuitTvSdk.create(block)
