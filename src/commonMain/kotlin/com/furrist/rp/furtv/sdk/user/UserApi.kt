package com.furrist.rp.furtv.sdk.user

import com.furrist.rp.furtv.sdk.auth.AuthManager
import com.furrist.rp.furtv.sdk.model.LikeStatusResponse
import com.furrist.rp.furtv.sdk.model.RelationshipInfo
import com.furrist.rp.furtv.sdk.model.SocialBadge
import com.furrist.rp.furtv.sdk.model.SocialBadgeDetail
import com.furrist.rp.furtv.sdk.model.SocialBadgeDetailResponse
import com.furrist.rp.furtv.sdk.model.SocialBadgeUser
import com.furrist.rp.furtv.sdk.model.SocialBadgesResponse
import com.furrist.rp.furtv.sdk.model.StoreProduct
import com.furrist.rp.furtv.sdk.model.StoreProductsResponse
import com.furrist.rp.furtv.sdk.model.StoreUser
import com.furrist.rp.furtv.sdk.model.UserDestination
import com.furrist.rp.furtv.sdk.model.UserIdData
import com.furrist.rp.furtv.sdk.model.UserIdResponse
import com.furrist.rp.furtv.sdk.model.UserProfile
import com.furrist.rp.furtv.sdk.model.UserProfileCharacter
import com.furrist.rp.furtv.sdk.model.UserProfilePrivacySettings
import com.furrist.rp.furtv.sdk.model.UserProfileResponse
import com.furrist.rp.furtv.sdk.model.UserRelationshipsResponse
import com.furrist.rp.furtv.sdk.model.UserVisitorsResponse
import com.furrist.rp.furtv.sdk.model.VisitorInfo
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
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
    public suspend fun getSocialBadges(username: String): SocialBadgesResponse =
        auth.withFreshToken {
            httpClient.get("$baseUrl/api/proxy/furtv/users/$username/social-badges")
                .body<SocialBadgesResponse>()
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