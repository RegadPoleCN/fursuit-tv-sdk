package com.furrist.rp.furtv.sdk.user

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get

/**
 * 用户相关 API。
 *
 * 提供用户资料公开信息、关系、访客、徽章、商店等用户相关功能的访问接口。
 * 所有方法均通过 HTTP GET 请求获取数据，返回对应的用户数据模型。
 *
 * ## 主要功能
 * - 用户资料查询（getUserProfile, getUserId）
 * - 社交互动状态（getLikeStatus, getUserRelationships）
 * - 访客记录（getUserVisitors）
 * - 徽章系统（getSocialBadges, getSocialBadgeDetail）
 * - 商店商品（getStoreProducts）
 *
 * @param httpClient 配置好的 HTTP 客户端
 * @param baseUrl API 基础 URL，默认为 https://open-global.vdsentnet.com
 * @see UserModels 用户数据模型定义
 * @see FursuitTvSdkException 异常层次结构
 */
public class UserApi(
    private val httpClient: HttpClient,
    private val baseUrl: String = "https://open-global.vdsentnet.com",
) {
    /**
     * 获取用户资料公开信息。
     *
     * 通过用户名查询用户的公开资料，包括显示名称、物种、性别、
     * 头像 URL 等基本信息。
     *
     * @param username 目标用户的用户名（不区分大小写）
     * @return 用户资料公开信息对象（UserProfile），包含 displayName、species 等字段
     * @throws NetworkException 网络连接失败或超时
     * @throws TokenExpiredException 访问令牌已过期或无效
     * @throws AuthenticationException 认证凭证缺失或无效
     * @throws NotFoundException 指定用户名的用户不存在
     * @throws ApiException 服务器返回其他业务错误(4xx/5xx)
     */
    public suspend fun getUserProfile(username: String): UserProfile {
        val response =
            httpClient.get("$baseUrl/api/proxy/furtv/users/$username")
                .body<UserProfileResponse>()
        return response.data
    }

    /**
     * 用户基础信息 ID 查询。
     *
     * 通过用户名或 ID 查询用户的基础身份信息（userId）。
     * 可用于获取用户的内部唯一标识符。
     *
     * @param id 用户名或用户 ID 字符串
     * @return 用户基础信息数据对象（UserIdData），包含 userId 等字段
     * @throws NetworkException 网络连接失败或超时
     * @throws TokenExpiredException 访问令牌已过期或无效
     * @throws AuthenticationException 认证凭证缺失或无效
     * @throws NotFoundException 指定 ID 的用户不存在
     * @throws ApiException 服务器返回其他业务错误(4xx/5xx)
     */
    public suspend fun getUserId(id: String): UserIdData {
        val response =
            httpClient.get("$baseUrl/api/proxy/furtv/users/id/$id")
                .body<UserIdResponse>()
        return response.data
    }

    /**
     * 获取用户点赞状态。
     *
     * 查询当前认证用户对目标用户的点赞状态，包括点赞数等信息。
     *
     * @param username 目标用户的用户名
     * @return 点赞状态数据对象（LikeStatusData），包含 likeCount 等字段
     * @throws NetworkException 网络连接失败或超时
     * @throws TokenExpiredException 访问令牌已过期或无效
     * @throws AuthenticationException 认证凭证缺失或无效
     * @throws NotFoundException 指定用户名的用户不存在
     * @throws ApiException 服务器返回其他业务错误(4xx/5xx)
     */
    public suspend fun getLikeStatus(username: String): LikeStatusData {
        val response =
            httpClient.get("$baseUrl/api/proxy/furtv/fursuit/like-status/$username")
                .body<LikeStatusResponse>()
        return response.data
    }

    /**
     * 获取用户关系公开列表。
     *
     * 查询指定用户的关注/粉丝关系列表，包括互相关注等信息。
     *
     * @param userId 目标用户的内部 ID（可通过 getUserId 获取）
     * @return 用户关系数据对象（UserRelationshipsData），包含 relationships 列表
     * @throws NetworkException 网络连接失败或超时
     * @throws TokenExpiredException 访问令牌已过期或无效
     * @throws AuthenticationException 认证凭证缺失或无效
     * @throws NotFoundException 指定 userId 的用户不存在
     * @throws ApiException 服务器返回其他业务错误(4xx/5xx)
     */
    public suspend fun getUserRelationships(userId: String): UserRelationshipsData {
        val response =
            httpClient.get("$baseUrl/api/proxy/furtv/relationships/user/$userId")
                .body<UserRelationshipsResponse>()
        return response.data
    }

    /**
     * 获取用户访客记录。
     *
     * 查询指定用户的个人资料页面访问记录，包括访客信息和访问时间。
     *
     * @param username 目标用户的用户名
     * @return 用户访客数据对象（UserVisitorsData），包含 visitors 列表
     * @throws NetworkException 网络连接失败或超时
     * @throws TokenExpiredException 访问令牌已过期或无效
     * @throws AuthenticationException 认证凭证缺失或无效
     * @throws NotFoundException 指定用户名的用户不存在
     * @throws ApiException 服务器返回其他业务错误(4xx/5xx)
     */
    public suspend fun getUserVisitors(username: String): UserVisitorsData {
        val response =
            httpClient.get("$baseUrl/api/proxy/furtv/users/$username/visitors")
                .body<UserVisitorsResponse>()
        return response.data
    }

    /**
     * 获取用户社交徽章列表。
     *
     * 查询指定用户已获得的社交徽章列表，包括徽章 ID、类型、获取时间等。
     *
     * @param username 目标用户的用户名
     * @return 用户社交徽章数据对象（SocialBadgesData），包含 badges 列表
     * @throws NetworkException 网络连接失败或超时
     * @throws TokenExpiredException 访问令牌已过期或无效
     * @throws AuthenticationException 认证凭证缺失或无效
     * @throws NotFoundException 指定用户名的用户不存在
     * @throws ApiException 服务器返回其他业务错误(4xx/5xx)
     */
    public suspend fun getSocialBadges(username: String): SocialBadgesData {
        val response =
            httpClient.get("$baseUrl/api/proxy/furtv/users/$username/social-badges")
                .body<SocialBadgesResponse>()
        return response.data
    }

    /**
     * 获取用户社交徽章详情。
     *
     * 查询指定用户某个社交徽章的详细信息，包括徽章描述、图标 URL 等。
     *
     * @param username 目标用户的用户名
     * @param userBadgeId 用户徽章 ID（从 getSocialBadges 列表中获取）
     * @return 社交徽章详情数据对象（SocialBadgeDetailData）
     * @throws NetworkException 网络连接失败或超时
     * @throws TokenExpiredException 访问令牌已过期或无效
     * @throws AuthenticationException 认证凭证缺失或无效
     * @throws NotFoundException 指定用户名或徽章 ID 不存在
     * @throws ApiException 服务器返回其他业务错误(4xx/5xx)
     */
    public suspend fun getSocialBadgeDetail(username: String, userBadgeId: String): SocialBadgeDetailData {
        val response =
            httpClient.get("$baseUrl/api/proxy/furtv/users/$username/social-badges/$userBadgeId")
                .body<SocialBadgeDetailResponse>()
        return response.data
    }

    /**
     * 获取用户商店商品。
     *
     * 查询指定用户在商店中可购买或已购买的商品列表。
     *
     * @param username 目标用户的用户名
     * @return 用户商店商品数据对象（StoreProductsData），包含 products 列表
     * @throws NetworkException 网络连接失败或超时
     * @throws TokenExpiredException 访问令牌已过期或无效
     * @throws AuthenticationException 认证凭证缺失或无效
     * @throws NotFoundException 指定用户名的用户不存在
     * @throws ApiException 服务器返回其他业务错误(4xx/5xx)
     */
    public suspend fun getStoreProducts(username: String): StoreProductsData {
        val response =
            httpClient.get("$baseUrl/api/proxy/furtv/users/$username/store-products")
                .body<StoreProductsResponse>()
        return response.data
    }
}
