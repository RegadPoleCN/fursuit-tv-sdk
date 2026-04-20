package com.furrist.rp.furtv.sdk.user

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get

/**
 * 用户相关 API
 * 提供用户资料公开信息、关系、访客、徽章、商店等用户相关功能的访问接口
 *
 * 参考官方文档：
 * - [用户资料公开信息](../../../../vds-docs/Fursuit.TV 兽频道/用户公开资料 - 基础信息/用户资料公开信息（furtv.users.profile）.md)
 * - [用户基础信息 ID 查询](../../../../vds-docs/Fursuit.TV 兽频道/用户公开资料 - 基础信息/用户基础信息 ID 查询（furtv.users.id）.md)
 * - [用户点赞状态](../../../../vds-docs/Fursuit.TV 兽频道/用户公开资料 - 基础信息/用户点赞状态（furtv.fursuit.likestatus）.md)
 * - [用户关系公开列表](../../../../vds-docs/Fursuit.TV 兽频道/用户公开资料 - 关系与访客/用户关系公开列表（furtv.relationships.user）.md)
 * - [用户访客记录](../../../../vds-docs/Fursuit.TV 兽频道/用户公开资料 - 关系与访客/用户访客记录（furtv.users.visitors）.md)
 * - [用户社交徽章列表](../../../../vds-docs/Fursuit.TV 兽频道/用户公开资料 - 徽章与商店/用户社交徽章列表（furtv.users.socialbadges）.md)
 * - [用户社交徽章详情](../../../../vds-docs/Fursuit.TV 兽频道/用户公开资料 - 徽章与商店/用户社交徽章详情（furtv.users.socialbadges.detail）.md)
 * - [用户商店商品](../../../../vds-docs/Fursuit.TV 兽频道/用户公开资料 - 徽章与商店/用户商店商品（furtv.users.storeproducts）.md)
 *
 * @param httpClient 配置好的 HTTP 客户端
 * @param baseUrl API 基础 URL，默认为 https://open-global.vdsentnet.com
 */
public class UserApi(
    private val httpClient: HttpClient,
    private val baseUrl: String = "https://open-global.vdsentnet.com",
) {
    /**
     * 获取用户资料公开信息
     * @param username 用户名
     * @return 用户资料公开信息
     */
    public suspend fun getUserProfile(username: String): UserProfile {
        val response =
            httpClient.get("$baseUrl/api/proxy/furtv/users/$username")
                .body<UserProfileResponse>()
        return response.data
    }

    /**
     * 用户基础信息 ID 查询
     * @param id 用户 ID
     * @return 用户基础信息数据
     */
    public suspend fun getUserId(id: String): UserIdData {
        val response =
            httpClient.get("$baseUrl/api/proxy/furtv/users/id/$id")
                .body<UserIdResponse>()
        return response.data
    }

    /**
     * 获取用户点赞状态
     * @param username 用户名
     * @return 点赞状态数据
     */
    public suspend fun getLikeStatus(username: String): LikeStatusData {
        val response =
            httpClient.get("$baseUrl/api/proxy/furtv/fursuit/like-status/$username")
                .body<LikeStatusResponse>()
        return response.data
    }

    /**
     * 获取用户关系公开列表
     * @param userId 用户 ID
     * @return 用户关系数据
     */
    public suspend fun getUserRelationships(userId: String): UserRelationshipsData {
        val response =
            httpClient.get("$baseUrl/api/proxy/furtv/relationships/user/$userId")
                .body<UserRelationshipsResponse>()
        return response.data
    }

    /**
     * 获取用户访客记录
     * @param username 用户名
     * @return 用户访客数据
     */
    public suspend fun getUserVisitors(username: String): UserVisitorsData {
        val response =
            httpClient.get("$baseUrl/api/proxy/furtv/users/$username/visitors")
                .body<UserVisitorsResponse>()
        return response.data
    }

    /**
     * 获取用户社交徽章列表
     * @param username 用户名
     * @return 用户社交徽章数据
     */
    public suspend fun getSocialBadges(username: String): SocialBadgesData {
        val response =
            httpClient.get("$baseUrl/api/proxy/furtv/users/$username/social-badges")
                .body<SocialBadgesResponse>()
        return response.data
    }

    /**
     * 获取用户社交徽章详情
     * @param username 用户名
     * @param userBadgeId 用户徽章 ID
     * @return 社交徽章详情数据
     */
    public suspend fun getSocialBadgeDetail(username: String, userBadgeId: String): SocialBadgeDetailData {
        val response =
            httpClient.get("$baseUrl/api/proxy/furtv/users/$username/social-badges/$userBadgeId")
                .body<SocialBadgeDetailResponse>()
        return response.data
    }

    /**
     * 获取用户商店商品
     * @param username 用户名
     * @return 用户商店商品数据
     */
    public suspend fun getStoreProducts(username: String): StoreProductsData {
        val response =
            httpClient.get("$baseUrl/api/proxy/furtv/users/$username/store-products")
                .body<StoreProductsResponse>()
        return response.data
    }
}
