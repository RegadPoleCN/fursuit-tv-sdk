package com.furrist.rp.furtv.sdk.user

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
 * @param httpClient 配置好的 HTTP 客户端
 * @param baseUrl API 基础 URL
 */
@JvmBlocking
@JvmAsync
@JsExport
@JsName("UserApi")
public class UserApi internal constructor(
    private val httpClient: HttpClient,
    private val baseUrl: String = "https://open-global.vdsentnet.com",
) {
    /**
     * 获取用户资料公开信息。
     *
     * @param username 目标用户的用户名（不区分大小写）
     * @return 用户资料公开信息对象
     * @throws NetworkException 网络连接失败或超时
     * @throws AuthenticationException 认证凭证缺失或无效
     * @throws NotFoundException 指定用户名的用户不存在
     */
    @JsName("getUserProfile")
    public suspend fun getUserProfile(username: String): UserProfile {
        val response =
            httpClient.get("$baseUrl/api/proxy/furtv/users/$username")
                .body<UserProfileResponse>()
        return response.data
    }

    /**
     * 用户基础信息 ID 查询。
     *
     * @param id 用户名或用户 ID 字符串
     * @return 用户基础信息数据对象
     * @throws NetworkException 网络连接失败或超时
     * @throws NotFoundException 指定 ID 的用户不存在
     */
    @JsName("getUserId")
    public suspend fun getUserId(id: String): UserIdData {
        val response =
            httpClient.get("$baseUrl/api/proxy/furtv/users/id/$id")
                .body<UserIdResponse>()
        return response.data
    }

    /**
     * 获取用户点赞状态。
     *
     * @param username 目标用户的用户名
     * @return 点赞状态数据对象
     * @throws NetworkException 网络连接失败或超时
     * @throws NotFoundException 指定用户名的用户不存在
     */
    @JsName("getLikeStatus")
    public suspend fun getLikeStatus(username: String): LikeStatusData {
        val response =
            httpClient.get("$baseUrl/api/proxy/furtv/fursuit/like-status/$username")
                .body<LikeStatusResponse>()
        return response.data
    }

    /**
     * 获取用户关系公开列表。
     *
     * @param userId 目标用户的内部 ID
     * @return 用户关系数据对象
     * @throws NetworkException 网络连接失败或超时
     * @throws NotFoundException 指定 userId 的用户不存在
     */
    @JsName("getUserRelationships")
    public suspend fun getUserRelationships(userId: String): UserRelationshipsData {
        val response =
            httpClient.get("$baseUrl/api/proxy/furtv/relationships/user/$userId")
                .body<UserRelationshipsResponse>()
        return response.data
    }

    /**
     * 获取用户访客记录。
     *
     * @param username 目标用户的用户名
     * @return 用户访客数据对象
     * @throws NetworkException 网络连接失败或超时
     * @throws NotFoundException 指定用户名的用户不存在
     */
    @JsName("getUserVisitors")
    public suspend fun getUserVisitors(username: String): UserVisitorsData {
        val response =
            httpClient.get("$baseUrl/api/proxy/furtv/users/$username/visitors")
                .body<UserVisitorsResponse>()
        return response.data
    }

    /**
     * 获取用户社交徽章列表。
     *
     * @param username 目标用户的用户名
     * @return 用户社交徽章数据对象
     * @throws NetworkException 网络连接失败或超时
     * @throws NotFoundException 指定用户名的用户不存在
     */
    @JsName("getSocialBadges")
    public suspend fun getSocialBadges(username: String): SocialBadgesData {
        val response =
            httpClient.get("$baseUrl/api/proxy/furtv/users/$username/social-badges")
                .body<SocialBadgesResponse>()
        return response.data
    }

    /**
     * 获取用户社交徽章详情。
     *
     * @param username 目标用户的用户名
     * @param userBadgeId 用户徽章 ID
     * @return 社交徽章详情数据对象
     * @throws NetworkException 网络连接失败或超时
     * @throws NotFoundException 指定用户名或徽章 ID 不存在
     */
    @JsName("getSocialBadgeDetail")
    public suspend fun getSocialBadgeDetail(username: String, userBadgeId: String): SocialBadgeDetailData {
        val response =
            httpClient.get("$baseUrl/api/proxy/furtv/users/$username/social-badges/$userBadgeId")
                .body<SocialBadgeDetailResponse>()
        return response.data
    }

    /**
     * 获取用户商店商品。
     *
     * @param username 目标用户的用户名
     * @return 用户商店商品数据对象
     * @throws NetworkException 网络连接失败或超时
     * @throws NotFoundException 指定用户名的用户不存在
     */
    @JsName("getStoreProducts")
    public suspend fun getStoreProducts(username: String): StoreProductsData {
        val response =
            httpClient.get("$baseUrl/api/proxy/furtv/users/$username/store-products")
                .body<StoreProductsResponse>()
        return response.data
    }
}
