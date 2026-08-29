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

package com.furrist.rp.furtv.sdk.user

import com.furrist.rp.furtv.sdk.auth.AuthManager
import com.furrist.rp.furtv.sdk.model.*
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import kotlin.js.JsExport
import kotlin.js.JsName
import love.forte.plugin.suspendtrans.annotation.JvmAsync
import love.forte.plugin.suspendtrans.annotation.JvmBlocking

/**
 * 用户相关 API。
 *
 * 提供用户资料公开信息、关系、访客、徽章、商店等用户相关功能的访问接口。
 *
 * @param auth 认证管理器（提供 `withFreshToken` 包装 + re-exchange）
 * @param httpClient 配置好的 HTTP 客户端
 * @param baseUrl API 基础 URL
 */
@JvmBlocking
@JvmAsync
@JsExport
@JsName("UserApi")
public class UserApi internal constructor(
    private val auth: AuthManager,
    private val httpClient: HttpClient,
    private val baseUrl: String = "https://open-global.vdsentnet.com",
) {
    @JsName("getUserProfile")
    public suspend fun getUserProfile(username: String): UserProfile =
        auth.withFreshToken {
            httpClient.get("$baseUrl/api/proxy/furtv/users/$username")
                .body<UserProfileResponse>()
                .user
        }

    @JsName("getUserId")
    public suspend fun getUserId(id: String): UserIdData =
        auth.withFreshToken {
            httpClient.get("$baseUrl/api/proxy/furtv/users/id/$id")
                .body<UserIdResponse>()
                .user
        }

    @JsName("getLikeStatus")
    public suspend fun getLikeStatus(username: String): LikeStatusResponse =
        auth.withFreshToken {
            httpClient.get("$baseUrl/api/proxy/furtv/fursuit/like-status/$username")
                .body<LikeStatusResponse>()
        }

    @JsName("getUserRelationships")
    public suspend fun getUserRelationships(userId: String): UserRelationshipsResponse =
        auth.withFreshToken {
            httpClient.get("$baseUrl/api/proxy/furtv/relationships/user/$userId")
                .body<UserRelationshipsResponse>()
        }

    @JsName("getUserVisitors")
    public suspend fun getUserVisitors(username: String): UserVisitorsResponse =
        auth.withFreshToken {
            httpClient.get("$baseUrl/api/proxy/furtv/users/$username/visitors")
                .body<UserVisitorsResponse>()
        }

    @JsName("getSocialBadges")
    public suspend fun getSocialBadges(username: String, limit: Int? = null): SocialBadgesResponse {
        // #19：用户社交徽章列表.md 查询参数 limit，可选、最大 50（无下限）
        require(limit == null || limit <= 50) { "limit must be <= 50" }
        return auth.withFreshToken {
            httpClient.get("$baseUrl/api/proxy/furtv/users/$username/social-badges") {
                limit?.let { parameter("limit", it) }
            }.body<SocialBadgesResponse>()
        }
    }

    @JsName("getSocialBadgeDetail")
    public suspend fun getSocialBadgeDetail(username: String, userBadgeId: String): SocialBadgeDetailResponse =
        auth.withFreshToken {
            httpClient.get("$baseUrl/api/proxy/furtv/users/$username/social-badges/$userBadgeId")
                .body<SocialBadgeDetailResponse>()
        }

    @JsName("getStoreProducts")
    public suspend fun getStoreProducts(username: String): StoreProductsResponse =
        auth.withFreshToken {
            httpClient.get("$baseUrl/api/proxy/furtv/users/$username/store-products")
                .body<StoreProductsResponse>()
        }
}
