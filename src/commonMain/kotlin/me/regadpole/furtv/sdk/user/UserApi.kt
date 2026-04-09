package me.regadpole.furtv.sdk.user

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get

/**
 * 用户相关 API
 * 提供用户资料、关系、消息、钱包、商店等用户相关功能的访问接口
 * @param httpClient 配置好的 HTTP 客户端
 * @param baseUrl API 基础 URL
 */
public class UserApi(
    private val httpClient: HttpClient,
    private val baseUrl: String = "https://open-global.vdsentnet.com",
) {
    /**
     * 获取用户资料公开信息
     * 端点：GET /api/proxy/furtv/users/:username
     * @param username 用户名
     * @return UserProfile 用户资料信息
     */
    public suspend fun getUserProfile(username: String): UserProfile {
        val response =
            httpClient.get("$baseUrl/api/proxy/furtv/users/$username")
                .body<UserProfileResponse>()
        return response.data
    }

    /**
     * 通过 ID 查询用户基本信息
     * 端点：GET /api/proxy/furtv/users/id/:id
     * @param id 用户 ID
     * @return UserBasicInfo 用户基本信息
     */
    public suspend fun getUserById(id: String): UserBasicInfo {
        val response =
            httpClient.get("$baseUrl/api/proxy/furtv/users/id/$id")
                .body<UserByIdResponse>()
        return response.data
    }

    /**
     * 获取用户点赞状态
     * 端点：GET /api/proxy/furtv/fursuit/like-status/:username
     * @param username 用户名
     * @return LikeStatus 点赞状态信息
     */
    public suspend fun getLikeStatus(username: String): LikeStatus {
        val response =
            httpClient.get("$baseUrl/api/proxy/furtv/fursuit/like-status/$username")
                .body<LikeStatusResponse>()
        return response.data
    }

    /**
     * 获取用户关系公开列表
     * 端点：GET /api/proxy/furtv/relationships/user/:userId
     * @param userId 用户 ID
     * @return RelationshipsData 用户关系列表
     */
    public suspend fun getUserRelationships(userId: String): RelationshipsData {
        val response =
            httpClient.get("$baseUrl/api/proxy/furtv/relationships/user/$userId")
                .body<RelationshipsResponse>()
        return response.data
    }

    /**
     * 获取用户访客记录
     * 端点：GET /api/proxy/furtv/users/:username/visitors
     * @param username 用户名
     * @return VisitorsData 访客记录列表
     */
    public suspend fun getUserVisitors(username: String): VisitorsData {
        val response =
            httpClient.get("$baseUrl/api/proxy/furtv/users/$username/visitors")
                .body<VisitorsResponse>()
        return response.data
    }

    /**
     * 获取用户商店商品
     * 端点：GET /api/proxy/furtv/users/:username/store-products
     * @param username 用户名
     * @return StoreProductsData 商店商品列表
     */
    public suspend fun getUserStoreProducts(username: String): StoreProductsData {
        val response =
            httpClient.get("$baseUrl/api/proxy/furtv/users/$username/store-products")
                .body<StoreProductsResponse>()
        return response.data
    }

    /**
     * 获取用户社交徽章列表
     * 端点：GET /api/proxy/furtv/users/:username/social-badges
     * @param username 用户名
     * @return SocialBadgesData 社交徽章列表
     */
    public suspend fun getUserSocialBadges(username: String): SocialBadgesData {
        val response =
            httpClient.get("$baseUrl/api/proxy/furtv/users/$username/social-badges")
                .body<SocialBadgesResponse>()
        return response.data
    }

    /**
     * 获取用户社交徽章详情
     * 端点：GET /api/proxy/furtv/users/:username/social-badges/:userBadgeId
     * @param username 用户名
     * @param userBadgeId 徽章 ID
     * @return SocialBadgeDetail 徽章详细信息
     */
    public suspend fun getSocialBadgeDetail(username: String, userBadgeId: String): SocialBadgeDetail {
        val response =
            httpClient.get("$baseUrl/api/proxy/furtv/users/$username/social-badges/$userBadgeId")
                .body<SocialBadgeDetailResponse>()
        return response.data
    }
}
