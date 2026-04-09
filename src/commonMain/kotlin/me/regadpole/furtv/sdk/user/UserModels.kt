package me.regadpole.furtv.sdk.user

import kotlinx.serialization.Serializable

// ==================== 用户资料 ====================

/**
 * 用户资料响应
 * 用户资料接口的响应包装 */
@Serializable
public data class UserProfileResponse(
    public val success: Boolean,
    public val data: UserProfile,
    public val requestId: String,
)

/**
 * 用户资料
 * 包含用户的公开资料信息
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
    public val username: String,
    public val displayName: String,
    public val avatarUrl: String? = null,
    public val bio: String? = null,
    public val species: String? = null,
    public val location: String? = null,
    public val destinations: List<String>? = null,
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
    public val showVisitorDetails: Boolean,
    public val showRelationships: Boolean,
)

/**
 * 用户 ID 查询响应
 * 通过 ID 查询用户信息的响应包装 */
@Serializable
public data class UserByIdResponse(
    public val success: Boolean,
    public val data: UserBasicInfo,
    public val requestId: String,
)

/**
 * 用户基本信息
 * 包含用户的最基本信息
 * @param id 用户 ID
 * @param username 用户名
 * @param displayName 显示名称
 * @param avatarUrl 头像 URL
 */
@Serializable
public data class UserBasicInfo(
    public val id: String,
    public val username: String,
    public val displayName: String,
    public val avatarUrl: String? = null,
)

// ==================== 点赞状态 ====================

/**
 * 点赞状态响应
 * 点赞状态接口的响应包装
 */
@Serializable
public data class LikeStatusResponse(
    public val success: Boolean,
    public val data: LikeStatus,
    public val requestId: String,
)

/**
 * 点赞状态
 * 包含用户的点赞相关信息
 * @param canLike 是否可以点赞
 * @param isLiked 是否已点赞
 * @param likeCount 点赞总数量
 */
@Serializable
public data class LikeStatus(
    public val canLike: Boolean,
    public val isLiked: Boolean,
    public val likeCount: Int,
)

// ==================== 关系与访客 ====================

/**
 * 关系列表响应
 * 用户关系接口的响应包装 */
@Serializable
public data class RelationshipsResponse(
    public val success: Boolean,
    public val data: RelationshipsData,
    public val requestId: String,
)

/**
 * 关系数据
 * 包含用户的关系列表信息
 * @param userId 用户 ID
 * @param partners 关系伴侣列表
 * @param totalCount 关系总数
 */
@Serializable
public data class RelationshipsData(
    public val userId: String,
    public val partners: List<RelationshipPartner>,
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
    public val userId: String,
    public val username: String,
    public val displayName: String,
    public val avatarUrl: String? = null,
    public val relationshipType: String,
)

/**
 * 访客记录响应
 * 访客记录接口的响应包装 */
@Serializable
public data class VisitorsResponse(
    public val success: Boolean,
    public val data: VisitorsData,
    public val requestId: String,
)

/**
 * 访客数据
 * 包含用户的访客记录信息
 * @param username 用户名
 * @param visitors 访客列表
 * @param totalCount 访客总数
 * @param showVisitorDetails 是否显示访客详情
 */
@Serializable
public data class VisitorsData(
    public val username: String,
    public val visitors: List<Visitor>,
    public val totalCount: Int,
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
public data class Visitor(
    public val userId: String,
    public val username: String,
    public val displayName: String,
    public val avatarUrl: String? = null,
    public val visitedAt: String,
)

// ==================== 徽章与商店 ====================

/**
 * 商店商品响应
 * 商店商品接口的响应包装 */
@Serializable
public data class StoreProductsResponse(
    public val success: Boolean,
    public val data: StoreProductsData,
    public val requestId: String,
)

/**
 * 商店商品数据
 * 包含用户商店的商品信息
 * @param username 用户名
 * @param isMerchant 是否是商家
 * @param products 商品列表
 */
@Serializable
public data class StoreProductsData(
    public val username: String,
    public val isMerchant: Boolean,
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
    public val id: String,
    public val name: String,
    public val description: String? = null,
    public val price: Double? = null,
    public val currency: String? = null,
    public val externalUrl: String? = null,
    public val imageUrl: String? = null,
)

/**
 * 社交徽章响应
 * 社交徽章接口的响应包装 */
@Serializable
public data class SocialBadgesResponse(
    public val success: Boolean,
    public val data: SocialBadgesData,
    public val requestId: String,
)

/**
 * 社交徽章数据
 * 包含用户的社交徽章列表
 * @param username 用户名
 * @param badges 徽章列表
 */
@Serializable
public data class SocialBadgesData(
    public val username: String,
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
    public val id: String,
    public val name: String,
    public val description: String? = null,
    public val iconUrl: String? = null,
    public val glbUrl: String? = null,
    public val awardedAt: String,
)

/**
 * 徽章详情响应
 * 徽章详情接口的响应包装 */
@Serializable
public data class SocialBadgeDetailResponse(
    public val success: Boolean,
    public val data: SocialBadgeDetail,
    public val requestId: String,
)

/**
 * 社交徽章详情
 * 包含社交徽章的详细信息
 * @param id 徽章 ID
 * @param userBadgeId 用户徽章 ID
 * @param name 徽章名称
 * @param description 徽章描述
 * @param iconUrl 徽章图标 URL
 * @param awardedAt 授予时间
 * @param awardedBy 授予者
 */
@Serializable
public data class SocialBadgeDetail(
    public val id: String,
    public val userBadgeId: String,
    public val name: String,
    public val description: String? = null,
    public val iconUrl: String? = null,
    public val glbUrl: String? = null,
    public val awardedAt: String,
    public val awardedBy: String? = null,
)
