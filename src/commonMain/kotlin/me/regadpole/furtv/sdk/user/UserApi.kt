package me.regadpole.furtv.sdk.user

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter

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
     *
     * 端点：`GET /api/proxy/furtv/users/profile`
     *
     * 官方文档：[用户资料公开信息](../../../../vds-docs/Fursuit.TV 兽频道/用户公开资料 - 基础信息/用户资料公开信息（furtv.users.profile）.md)
     *
     * 获取指定用户的公开资料信息，包括用户名、显示名称、头像、简介、物种等。
     *
     * @param userId 用户 ID
     * @return UserProfile 用户资料公开信息
     * @throws Exception 当请求失败时抛出异常
     */
    public suspend fun getUserProfile(userId: String): UserProfile {
        val response =
            httpClient.get("$baseUrl/api/proxy/furtv/users/profile") {
                parameter("userId", userId)
            }.body<UserProfileResponse>()
        return response.data
    }

    /**
     * 用户基础信息 ID 查询
     * 端点：GET /api/proxy/furtv/users/id
     *
     * 通过用户标识符（用户名等）查询用户的基础信息和 ID。
     *
     * @param identifier 用户标识符（如用户名）
     * @return UserIdData 用户基础信息数据
     * @throws Exception 当请求失败时抛出异常
     */
    public suspend fun getUserId(identifier: String): UserIdData {
        val response =
            httpClient.get("$baseUrl/api/proxy/furtv/users/id") {
                parameter("identifier", identifier)
            }.body<UserIdResponse>()
        return response.data
    }

    /**
     * 获取用户点赞状态
     * 端点：GET /api/proxy/furtv/fursuit/likestatus
     *
     * 获取当前用户对指定用户的点赞状态，包括是否可以点赞、是否已点赞、点赞总数等。
     *
     * @param userId 目标用户 ID
     * @param targetType 目标类型（如 "user"）
     * @param targetId 目标 ID（通常与 userId 相同）
     * @return LikeStatusData 点赞状态数据
     * @throws Exception 当请求失败时抛出异常
     */
    public suspend fun getLikeStatus(userId: String, targetType: String, targetId: String): LikeStatusData {
        val response =
            httpClient.get("$baseUrl/api/proxy/furtv/fursuit/likestatus") {
                parameter("userId", userId)
                parameter("targetType", targetType)
                parameter("targetId", targetId)
            }.body<LikeStatusResponse>()
        return response.data
    }

    /**
     * 获取用户关系公开列表
     * 端点：GET /api/proxy/furtv/relationships/user
     *
     * 获取指定用户的关系公开列表，包括伴侣关系、关系类型等信息。
     *
     * @param userId 用户 ID
     * @param type 关系类型（可选，如 "partner"）
     * @return UserRelationshipsData 用户关系数据
     * @throws Exception 当请求失败时抛出异常
     */
    public suspend fun getUserRelationships(userId: String, type: String? = null): UserRelationshipsData {
        val response =
            httpClient.get("$baseUrl/api/proxy/furtv/relationships/user") {
                parameter("userId", userId)
                type?.let { parameter("type", it) }
            }.body<UserRelationshipsResponse>()
        return response.data
    }

    /**
     * 获取用户访客记录
     * 端点：GET /api/proxy/furtv/users/visitors
     *
     * 获取指定用户的访客记录列表，包括访客用户信息和访问时间。
     *
     * @param userId 用户 ID
     * @return UserVisitorsData 用户访客数据
     * @throws Exception 当请求失败时抛出异常
     */
    public suspend fun getUserVisitors(userId: String): UserVisitorsData {
        val response =
            httpClient.get("$baseUrl/api/proxy/furtv/users/visitors") {
                parameter("userId", userId)
            }.body<UserVisitorsResponse>()
        return response.data
    }

    /**
     * 获取用户社交徽章列表
     * 端点：GET /api/proxy/furtv/users/socialbadges
     *
     * 获取指定用户的社交徽章列表，包括徽章 ID、名称、描述、图标等信息。
     *
     * @param userId 用户 ID
     * @return SocialBadgesData 用户社交徽章数据
     * @throws Exception 当请求失败时抛出异常
     */
    public suspend fun getSocialBadges(userId: String): SocialBadgesData {
        val response =
            httpClient.get("$baseUrl/api/proxy/furtv/users/socialbadges") {
                parameter("userId", userId)
            }.body<SocialBadgesResponse>()
        return response.data
    }

    /**
     * 获取用户社交徽章详情
     * 端点：GET /api/proxy/furtv/users/socialbadges/detail
     *
     * 获取指定用户徽章的详细信息，包括授予者、授予时间等详细信息。
     *
     * @param badgeId 徽章 ID（用户徽章 ID）
     * @return SocialBadgeDetailData 社交徽章详情数据
     * @throws Exception 当请求失败时抛出异常
     */
    public suspend fun getSocialBadgeDetail(badgeId: String): SocialBadgeDetailData {
        val response =
            httpClient.get("$baseUrl/api/proxy/furtv/users/socialbadges/detail") {
                parameter("badgeId", badgeId)
            }.body<SocialBadgeDetailResponse>()
        return response.data
    }

    /**
     * 获取用户商店商品
     * 端点：GET /api/proxy/furtv/users/storeproducts
     *
     * 获取指定用户的商店商品列表，包括商品 ID、名称、价格、描述等信息。
     *
     * @param userId 用户 ID
     * @return StoreProductsData 用户商店商品数据
     * @throws Exception 当请求失败时抛出异常
     */
    public suspend fun getStoreProducts(userId: String): StoreProductsData {
        val response =
            httpClient.get("$baseUrl/api/proxy/furtv/users/storeproducts") {
                parameter("userId", userId)
            }.body<StoreProductsResponse>()
        return response.data
    }
}
