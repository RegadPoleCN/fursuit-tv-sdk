package com.furrist.rp.furtv.sdk

import com.furrist.rp.furtv.sdk.auth.AuthManager
import com.furrist.rp.furtv.sdk.base.BaseApi
import com.furrist.rp.furtv.sdk.gathering.GatheringApi
import com.furrist.rp.furtv.sdk.http.HttpClientConfig
import com.furrist.rp.furtv.sdk.model.MutableSdkConfig
import com.furrist.rp.furtv.sdk.model.SdkConfig
import com.furrist.rp.furtv.sdk.model.TokenInfo
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
 * 推荐通过 [fursuitTvSdk]（Kotlin suspend）或 [fursuitTvSdkBlocking]（Java / JVM 阻塞）创建实例。
 *
 * @param config SDK 配置
 * @param tokenInfo 可选的令牌信息（用于外部注入已缓存的 TokenInfo）
 */
@JsExport
@JsName("FursuitTvSdk")
public class FursuitTvSdk internal constructor(
    private val config: SdkConfig,
    tokenInfo: TokenInfo? = null,
) {
    @JsName("_httpClient")
    private val httpClient: HttpClient = HttpClientConfig.getClient(config)

    /**
     * 认证管理器
     */
    @JsName("auth")
    public val auth: AuthManager =
        AuthManager(config, httpClient).apply {
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
     * 关闭 SDK 客户端并释放资源（关闭共享的 HttpClient）。
     */
    @JsName("close")
    public fun close() {
        httpClient.close()
    }
}

/**
 * 使用 DSL 方式创建 FursuitTvSdk（Kotlin suspend 入口）。
 *
 * 当同时提供 `clientId` + `clientSecret` 时，自动完成签名交换获取令牌。
 *
 * ```kotlin
 * val sdk = fursuitTvSdk {
 *     clientId = "vap_xxx"
 *     clientSecret = "your-secret"
 * }
 * ```
 *
 * Java 调用方请使用 [fursuitTvSdkBlocking] 或 `JvmFursuitTvSdkBuilder` 链式 Builder。
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
        val httpClient = HttpClientConfig.getClient(config)
        val authManager = AuthManager(config, httpClient)
        val tokenInfo = authManager.exchangeToken(config.clientId, config.clientSecret)
        return FursuitTvSdk(config, tokenInfo)
    }

    return FursuitTvSdk(config)
}

/**
 * `fursuitTvSdkBlocking` 同步入口声明。
 *
 * - JVM：`src/jvmMain/kotlin/com/furrist/rp/furtv/sdk/FursuitTvSdkJvm.kt`
 *   中的 `actual` 使用 `runBlocking { fursuitTvSdk(...) }` 实现。
 * - JS / Native：不支持阻塞线程；调用方应直接使用 suspend `fursuitTvSdk { ... }`
 *   或将其包装在自己的 `Promise` / `Future` 中。
 */
public expect fun fursuitTvSdkBlocking(block: (MutableSdkConfig) -> Unit): FursuitTvSdk
