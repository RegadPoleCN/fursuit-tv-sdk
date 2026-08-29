/*
 *   Copyright 2026 RegadPoleCN
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.furrist.rp.furtv.sdk

import com.furrist.rp.furtv.sdk.auth.AuthHolder
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
import io.ktor.client.*
import kotlin.concurrent.Volatile
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
    @Volatile private var closed: Boolean = false,
) {
    /**
     * AuthHolder：late-bound AuthManager 引用，供 `HttpClientConfig.defaultRequest` 通过
     * 闭包按请求读取最新 apiKey（@Volatile 保证跨线程可见性）。
     */
    private val authHolder: AuthHolder = AuthHolder()

    @JsName("_httpClient")
    private val httpClient: HttpClient = HttpClientConfig.getClient(config, authHolder)

    /**
     * 认证管理器
     */
    @JsName("auth")
    public val auth: AuthManager =
        AuthManager(config, httpClient).apply {
            tokenInfo?.let { setTokenInfo(it) }
        }

    init {
        // #41：将 AuthManager 晚绑定到 authHolder，defaultRequest 才能按请求
        // 读取并注入 X-Api-Key（此前全仓库无赋值，业务请求从不携带认证头）。
        authHolder.auth = auth
    }

    /** 基础接口 API */
    @JsName("base")
    public val base: BaseApi = BaseApi(auth, httpClient, config.baseUrl)

    /** 用户相关 API */
    @JsName("user")
    public val user: UserApi = UserApi(auth, httpClient, config.baseUrl)

    /** 搜索和发现 API */
    @JsName("search")
    public val search: SearchApi = SearchApi(auth, httpClient, config.baseUrl)

    /** 聚会相关 API */
    @JsName("gathering")
    public val gathering: GatheringApi = GatheringApi(auth, httpClient, config.baseUrl)

    /** 学校和角色 API */
    @JsName("school")
    public val school: SchoolApi = SchoolApi(auth, httpClient, config.baseUrl)

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
        check(!closed) { "FursuitTvSdk already closed (close is irreversible)" }
        httpClient.close()
        closed = true
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

    // #8：配置级 apiKey 已删除，条件简化为 clientId + clientSecret
    if (config.clientId != null && config.clientSecret != null) {
        val tempHolder = com.furrist.rp.furtv.sdk.auth.AuthHolder()
        val httpClient = HttpClientConfig.getClient(config, tempHolder)
        val authManager = AuthManager(config, httpClient)
        val tokenInfo = authManager.exchangeToken(config.clientId, config.clientSecret)
        return FursuitTvSdk(config, tokenInfo)
    }

    return FursuitTvSdk(config)
}
