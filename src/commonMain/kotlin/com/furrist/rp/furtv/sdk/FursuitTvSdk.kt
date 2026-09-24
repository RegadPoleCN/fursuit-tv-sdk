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
 * 实现了 [AutoCloseable] 接口，客户端生命周期由本实例自主管控，支持通过 `use { ... }` 块自动释放资源。
 *
 * 推荐通过 [fursuitTvSdk]（Kotlin suspend DSL）或 [FursuitTvSdkBuilder]（链式 Builder）创建实例。
 *
 * @param config SDK 配置
 * @param tokenInfo 可选的令牌信息（用于外部注入已缓存的 TokenInfo）
 */
@JsExport
@JsName("FursuitTvSdk")
public class FursuitTvSdk internal constructor(
    private val config: SdkConfig,
    tokenInfo: TokenInfo? = null,
) : AutoCloseable {
    @Volatile
    private var closed: Boolean = false

    /**
     * 认证管理器
     */
    @JsName("auth")
    public val auth: AuthManager =
        AuthManager(config).apply {
            tokenInfo?.let { setTokenInfo(it) }
        }

    @JsName("_httpClient")
    private val httpClient: HttpClient =
        HttpClientConfig.createClient(config) { auth.getApiKey() }

    init {
        auth.bindHttpClient(httpClient)
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
     * 关闭 SDK 客户端并释放底层 HTTP 客户端与协程资源。
     */
    override fun close() {
        if (closed) return
        closed = true
        httpClient.close()
    }
}

/**
 * 使用 DSL 方式创建 FursuitTvSdk（Kotlin suspend 入口）。
 *
 * 当同时提供 `clientId` + `clientSecret` 且未提供 `tokenInfo` 时，自动完成签名交换获取令牌。
 *
 * ```kotlin
 * val sdk = fursuitTvSdk {
 *     clientId = "vap_xxx"
 *     clientSecret = "your-secret"
 * }
 * ```
 *
 * @param tokenInfo 可选的已缓存 TokenInfo，用于多进程/分布式恢复
 * @param block 配置块
 * @return FursuitTvSdk 实例
 */
@JvmBlocking
@JvmAsync
@JsExport
@JsName("fursuitTvSdk")
public suspend fun fursuitTvSdk(
    tokenInfo: TokenInfo? = null,
    block: (MutableSdkConfig) -> Unit,
): FursuitTvSdk {
    val mutableConfig = MutableSdkConfig()
    block(mutableConfig)
    val config = mutableConfig.toImmutable()

    val sdk = FursuitTvSdk(config, tokenInfo)
    if (config.clientId != null && config.clientSecret != null && tokenInfo == null) {
        sdk.auth.exchangeToken(config.clientId, config.clientSecret)
    }
    return sdk
}

/**
 * 兼容原有单参数无 tokenInfo 重载。
 */
@JvmBlocking
@JvmAsync
@JsExport
@JsName("fursuitTvSdkSimple")
public suspend fun fursuitTvSdk(block: (MutableSdkConfig) -> Unit): FursuitTvSdk =
    fursuitTvSdk(tokenInfo = null, block = block)
