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

/** getSocialBadges 的 limit 上限（用户社交徽章列表.md，可选、最大 50）。 */
private const val SOCIAL_BADGES_LIMIT_MAX = 50

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
    /** 获取用户公开资料（用户资料公开信息.md）。 */
    @JsName("getUserProfile")
    public suspend fun getUserProfile(username: String): UserProfileResponse =
        auth.withFreshToken {
            httpClient.get("$baseUrl/api/proxy/furtv/users/$username")
                .body<UserProfileResponse>()
        }

    /** 通过用户数字 ID 查询用户基础信息（用户基础信息ID查询.md）。 */
    @JsName("getUserId")
    public suspend fun getUserId(id: String): UserIdResponse =
        auth.withFreshToken {
            httpClient.get("$baseUrl/api/proxy/furtv/users/id/$id")
                .body<UserIdResponse>()
        }

    /** 查询当前凭证对目标用户的点赞状态（用户点赞状态.md）。 */
    @JsName("getLikeStatus")
    public suspend fun getLikeStatus(username: String): LikeStatusResponse =
        auth.withFreshToken {
            httpClient.get("$baseUrl/api/proxy/furtv/fursuit/like-status/$username")
                .body<LikeStatusResponse>()
        }

    /** 查询用户公开关系列表（用户关系公开列表.md）。 */
    @JsName("getUserRelationships")
    public suspend fun getUserRelationships(userId: String): UserRelationshipsResponse =
        auth.withFreshToken {
            httpClient.get("$baseUrl/api/proxy/furtv/relationships/user/$userId")
                .body<UserRelationshipsResponse>()
        }

    /** 查询用户访客记录（用户访客记录.md）。 */
    @JsName("getUserVisitors")
    public suspend fun getUserVisitors(username: String): UserVisitorsResponse =
        auth.withFreshToken {
            httpClient.get("$baseUrl/api/proxy/furtv/users/$username/visitors")
                .body<UserVisitorsResponse>()
        }

    /**
     * 获取用户社交徽章列表（用户社交徽章列表.md）。
     *
     * @param username 目标用户名
     * @param limit 可选，单页数量上限（文档约定最大 50，超过抛 [IllegalArgumentException]）
     */
    @JsName("getSocialBadges")
    public suspend fun getSocialBadges(username: String, limit: Int? = null): SocialBadgesResponse {
        // 用户社交徽章列表.md 查询参数 limit，可选、最大 50（无下限）
        require(limit == null || limit <= SOCIAL_BADGES_LIMIT_MAX) { "limit must be <= 50" }
        return auth.withFreshToken {
            httpClient.get("$baseUrl/api/proxy/furtv/users/$username/social-badges") {
                limit?.let { parameter("limit", it) }
            }.body<SocialBadgesResponse>()
        }
    }

    /** 获取单个社交徽章详情（用户社交徽章详情.md）。 */
    @JsName("getSocialBadgeDetail")
    public suspend fun getSocialBadgeDetail(username: String, userBadgeId: String): SocialBadgeDetailResponse =
        auth.withFreshToken {
            httpClient.get("$baseUrl/api/proxy/furtv/users/$username/social-badges/$userBadgeId")
                .body<SocialBadgeDetailResponse>()
        }

    /** 获取用户商店在售商品（用户商店商品.md）。 */
    @JsName("getStoreProducts")
    public suspend fun getStoreProducts(username: String): StoreProductsResponse =
        auth.withFreshToken {
            httpClient.get("$baseUrl/api/proxy/furtv/users/$username/store-products")
                .body<StoreProductsResponse>()
        }
}
