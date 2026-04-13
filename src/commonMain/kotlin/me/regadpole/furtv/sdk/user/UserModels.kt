package me.regadpole.furtv.sdk.user

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// ==================== 用户资料 ====================

/**
 * 用户资料公开信息响应
 * 端点：GET /api/proxy/furtv/users/profile
 * 用户资料接口的响应包装
 */
@Serializable
public data class UserProfileResponse(
    @SerialName("success")
    public val success: Boolean,
    @SerialName("data")
    public val data: UserProfile,
    @SerialName("requestId")
    public val requestId: String,
)

/**
 * 用户资料公开信息
 * 包含用户的公开资料信息
 * @param id 用户 ID
 * @param username 用户名
 * @param displayName 显示名称
 * @param avatarUrl 头像 URL
 * @param bio 个人简介
 * @param species 物种
 * @param location 地理位置
 * @param destinations 目的地列表
 * @param privacy 隐私设置
 */
@Serializable
public data class UserProfile(
    @SerialName("id")
    public val id: String,
    @SerialName("username")
    public val username: String,
    @SerialName("displayName")
    public val displayName: String,
    @SerialName("avatarUrl")
    public val avatarUrl: String? = null,
    @SerialName("bio")
    public val bio: String? = null,
    @SerialName("species")
    public val species: String? = null,
    @SerialName("location")
    public val location: String? = null,
    @SerialName("destinations")
    public val destinations: List<String>? = null,
    @SerialName("privacy")
    public val privacy: UserPrivacySettings,
)

/**
 * 用户隐私设置
 * 包含用户的隐私偏好设置
 * @param showVisitorDetails 是否显示访客详情
 * @param showRelationships 是否显示关系列表
 */
@Serializable
public data class UserPrivacySettings(
    @SerialName("showVisitorDetails")
    public val showVisitorDetails: Boolean,
    @SerialName("showRelationships")
    public val showRelationships: Boolean,
)

// ==================== 用户 ID 查询 ====================

/**
 * 用户基础信息 ID 查询响应
 * 端点：GET /api/proxy/furtv/users/id
 * 通过标识符查询用户 ID 的响应包装
 */
@Serializable
public data class UserIdResponse(
    @SerialName("success")
    public val success: Boolean,
    @SerialName("data")
    public val data: UserIdData,
    @SerialName("requestId")
    public val requestId: String,
)

/**
 * 用户 ID 数据
 * 包含用户的基础信息 ID
 * @param id 用户 ID
 * @param username 用户名
 * @param displayName 显示名称
 * @param avatarUrl 头像 URL
 */
@Serializable
public data class UserIdData(
    @SerialName("id")
    public val id: String,
    @SerialName("username")
    public val username: String,
    @SerialName("displayName")
    public val displayName: String,
    @SerialName("avatarUrl")
    public val avatarUrl: String? = null,
)

// ==================== 点赞状态 ====================

/**
 * 用户点赞状态响应
 * 端点：GET /api/proxy/furtv/fursuit/likestatus
 * 点赞状态接口的响应包装
 */
@Serializable
public data class LikeStatusResponse(
    @SerialName("success")
    public val success: Boolean,
    @SerialName("data")
    public val data: LikeStatusData,
    @SerialName("requestId")
    public val requestId: String,
)

/**
 * 点赞状态数据
 * 包含用户的点赞相关信息
 * @param canLike 是否可以点赞
 * @param isLiked 是否已点赞
 * @param likeCount 点赞总数量
 */
@Serializable
public data class LikeStatusData(
    @SerialName("canLike")
    public val canLike: Boolean,
    @SerialName("isLiked")
    public val isLiked: Boolean,
    @SerialName("likeCount")
    public val likeCount: Int,
)

// ==================== 用户关系 ====================

/**
 * 用户关系公开列表响应
 * 端点：GET /api/proxy/furtv/relationships/user
 * 用户关系接口的响应包装
 */
@Serializable
public data class UserRelationshipsResponse(
    @SerialName("success")
    public val success: Boolean,
    @SerialName("data")
    public val data: UserRelationshipsData,
    @SerialName("requestId")
    public val requestId: String,
)

/**
 * 用户关系数据
 * 包含用户的关系列表信息
 * @param userId 用户 ID
 * @param partners 关系伴侣列表
 * @param totalCount 关系总数
 */
@Serializable
public data class UserRelationshipsData(
    @SerialName("userId")
    public val userId: String,
    @SerialName("partners")
    public val partners: List<RelationshipPartner>,
    @SerialName("totalCount")
    public val totalCount: Int,
)

/**
 * 关系伴侣
 * 表示一个关系伴侣的信息
 * @param userId 伴侣用户 ID
 * @param username 伴侣用户名
 * @param displayName 伴侣显示名称
 * @param avatarUrl 伴侣头像 URL
 * @param relationshipType 关系类型
 */
@Serializable
public data class RelationshipPartner(
    @SerialName("userId")
    public val userId: String,
    @SerialName("username")
    public val username: String,
    @SerialName("displayName")
    public val displayName: String,
    @SerialName("avatarUrl")
    public val avatarUrl: String? = null,
    @SerialName("relationshipType")
    public val relationshipType: String,
)

// ==================== 用户访客 ====================

/**
 * 用户访客记录响应
 * 端点：GET /api/proxy/furtv/users/visitors
 * 访客记录接口的响应包装
 */
@Serializable
public data class UserVisitorsResponse(
    @SerialName("success")
    public val success: Boolean,
    @SerialName("data")
    public val data: UserVisitorsData,
    @SerialName("requestId")
    public val requestId: String,
)

/**
 * 用户访客数据
 * 包含用户的访客记录信息
 * @param userId 用户 ID
 * @param visitors 访客列表
 * @param totalCount 访客总数
 * @param showVisitorDetails 是否显示访客详情
 */
@Serializable
public data class UserVisitorsData(
    @SerialName("userId")
    public val userId: String,
    @SerialName("visitors")
    public val visitors: List<UserVisitor>,
    @SerialName("totalCount")
    public val totalCount: Int,
    @SerialName("showVisitorDetails")
    public val showVisitorDetails: Boolean,
)

/**
 * 访客信息
 * 表示一个访客的信息
 * @param userId 访客用户 ID
 * @param username 访客用户名
 * @param displayName 访客显示名称
 * @param avatarUrl 访客头像 URL
 * @param visitedAt 访问时间
 */
@Serializable
public data class UserVisitor(
    @SerialName("userId")
    public val userId: String,
    @SerialName("username")
    public val username: String,
    @SerialName("displayName")
    public val displayName: String,
    @SerialName("avatarUrl")
    public val avatarUrl: String? = null,
    @SerialName("visitedAt")
    public val visitedAt: String,
)

// ==================== 社交徽章 ====================

/**
 * 用户社交徽章列表响应
 * 端点：GET /api/proxy/furtv/users/socialbadges
 * 社交徽章接口的响应包装
 */
@Serializable
public data class SocialBadgesResponse(
    @SerialName("success")
    public val success: Boolean,
    @SerialName("data")
    public val data: SocialBadgesData,
    @SerialName("requestId")
    public val requestId: String,
)

/**
 * 用户社交徽章数据
 * 包含用户的社交徽章列表
 * @param userId 用户 ID
 * @param badges 徽章列表
 */
@Serializable
public data class SocialBadgesData(
    @SerialName("userId")
    public val userId: String,
    @SerialName("badges")
    public val badges: List<SocialBadge>,
)

/**
 * 社交徽章
 * 表示一个社交徽章的信息
 * @param id 徽章 ID
 * @param name 徽章名称
 * @param description 徽章描述
 * @param iconUrl 徽章图标 URL
 * @param glbUrl 3D 模型 URL（GLB 格式）
 * @param awardedAt 授予时间
 */
@Serializable
public data class SocialBadge(
    @SerialName("id")
    public val id: String,
    @SerialName("name")
    public val name: String,
    @SerialName("description")
    public val description: String? = null,
    @SerialName("iconUrl")
    public val iconUrl: String? = null,
    @SerialName("glbUrl")
    public val glbUrl: String? = null,
    @SerialName("awardedAt")
    public val awardedAt: String,
)

/**
 * 用户社交徽章详情响应
 * 端点：GET /api/proxy/furtv/users/socialbadges/detail
 * 徽章详情接口的响应包装
 */
@Serializable
public data class SocialBadgeDetailResponse(
    @SerialName("success")
    public val success: Boolean,
    @SerialName("data")
    public val data: SocialBadgeDetailData,
    @SerialName("requestId")
    public val requestId: String,
)

/**
 * 用户社交徽章详情数据
 * 包含社交徽章的详细信息
 * @param id 徽章 ID
 * @param userBadgeId 用户徽章 ID
 * @param name 徽章名称
 * @param description 徽章描述
 * @param iconUrl 徽章图标 URL
 * @param glbUrl 3D 模型 URL（GLB 格式）
 * @param awardedAt 授予时间
 * @param awardedBy 授予者
 */
@Serializable
public data class SocialBadgeDetailData(
    @SerialName("id")
    public val id: String,
    @SerialName("userBadgeId")
    public val userBadgeId: String,
    @SerialName("name")
    public val name: String,
    @SerialName("description")
    public val description: String? = null,
    @SerialName("iconUrl")
    public val iconUrl: String? = null,
    @SerialName("glbUrl")
    public val glbUrl: String? = null,
    @SerialName("awardedAt")
    public val awardedAt: String,
    @SerialName("awardedBy")
    public val awardedBy: String? = null,
)

// ==================== 商店商品 ====================

/**
 * 用户商店商品响应
 * 端点：GET /api/proxy/furtv/users/storeproducts
 * 商店商品接口的响应包装
 */
@Serializable
public data class StoreProductsResponse(
    @SerialName("success")
    public val success: Boolean,
    @SerialName("data")
    public val data: StoreProductsData,
    @SerialName("requestId")
    public val requestId: String,
)

/**
 * 用户商店商品数据
 * 包含用户商店的商品信息
 * @param userId 用户 ID
 * @param isMerchant 是否是商家
 * @param products 商品列表
 */
@Serializable
public data class StoreProductsData(
    @SerialName("userId")
    public val userId: String,
    @SerialName("isMerchant")
    public val isMerchant: Boolean,
    @SerialName("products")
    public val products: List<StoreProduct>,
)

/**
 * 商店商品
 * 表示一个商店商品的信息
 * @param id 商品 ID
 * @param name 商品名称
 * @param description 商品描述
 * @param price 价格
 * @param currency 货币单位
 * @param externalUrl 外部购买链接
 * @param imageUrl 商品图片 URL
 */
@Serializable
public data class StoreProduct(
    @SerialName("id")
    public val id: String,
    @SerialName("name")
    public val name: String,
    @SerialName("description")
    public val description: String? = null,
    @SerialName("price")
    public val price: Double? = null,
    @SerialName("currency")
    public val currency: String? = null,
    @SerialName("externalUrl")
    public val externalUrl: String? = null,
    @SerialName("imageUrl")
    public val imageUrl: String? = null,
)
