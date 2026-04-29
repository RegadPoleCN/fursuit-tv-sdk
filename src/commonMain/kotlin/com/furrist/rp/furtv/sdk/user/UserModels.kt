package com.furrist.rp.furtv.sdk.user

import kotlin.js.JsExport
import kotlin.js.JsName
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// ==================== 用户资料 ====================

/**
 * 用户资料公开信息响应。
 *
 * 端点：GET /api/proxy/furtv/users/profile
 */
@JsExport
@JsName("UserProfileResponse")
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
 * 用户资料公开信息，包含 VDS 返回的完整用户资料。
 *
 * @param id 用户 ID
 * @param username 用户名
 * @param nickname 昵称
 * @param avatarUrl 头像 URL
 * @param fursuitSpecies 兽装物种
 * @param fursuitBirthday 兽装生日
 * @param fursuitMaker 兽装制作者
 * @param showcasePortrait 展示竖图 URL
 * @param showcaseLandscape 展示横图 URL
 * @param showcaseOther 展示其他图 URL
 * @param introduction 自我介绍
 * @param interests 兴趣标签列表
 * @param location 地理位置
 * @param socialLinks 社交链接映射
 * @param contactInfo 联系方式映射
 * @param privacySettings 隐私设置
 * @param characters 角色列表
 * @param otherVerifiedTypes 其他认证类型
 * @param viewCount 主页浏览量
 * @param isVerified 是否已认证
 * @param createdAt 注册时间
 * @param destinations 目的地列表
 * @param destination 当前目的地
 * @param destinationExpiresAt 当前目的地过期时间
 */
@JsExport
@JsName("UserProfile")
@Serializable
public data class UserProfile(
    @SerialName("id")
    public val id: Int,
    @SerialName("username")
    public val username: String,
    @SerialName("nickname")
    public val nickname: String? = null,
    @SerialName("avatar_url")
    public val avatarUrl: String? = null,
    @SerialName("fursuit_species")
    public val fursuitSpecies: String? = null,
    @SerialName("fursuit_birthday")
    public val fursuitBirthday: String? = null,
    @SerialName("fursuit_maker")
    public val fursuitMaker: String? = null,
    @SerialName("showcase_portrait")
    public val showcasePortrait: String? = null,
    @SerialName("showcase_landscape")
    public val showcaseLandscape: String? = null,
    @SerialName("showcase_other")
    public val showcaseOther: String? = null,
    @SerialName("introduction")
    public val introduction: String? = null,
    @SerialName("interests")
    public val interests: List<String>? = null,
    @SerialName("location")
    public val location: String? = null,
    @SerialName("social_links")
    public val socialLinks: Map<String, String>? = null,
    @SerialName("contact_info")
    public val contactInfo: Map<String, String>? = null,
    @SerialName("privacy_settings")
    public val privacySettings: UserProfilePrivacySettings? = null,
    @SerialName("characters")
    public val characters: List<UserProfileCharacter>? = null,
    @SerialName("other_verified_types")
    public val otherVerifiedTypes: List<String>? = null,
    @SerialName("view_count")
    public val viewCount: Int? = null,
    @SerialName("is_verified")
    public val isVerified: Boolean? = null,
    @SerialName("created_at")
    public val createdAt: String? = null,
    @SerialName("destinations")
    public val destinations: List<UserDestination>? = null,
    @SerialName("destination")
    public val destination: String? = null,
    @SerialName("destination_expires_at")
    public val destinationExpiresAt: String? = null,
)

/**
 * 用户资料隐私设置。
 *
 * @param showEmail 是否公开邮箱
 * @param allowContact 是否允许联系
 * @param showLocation 是否公开位置
 * @param allowMessages 是否允许私信
 * @param allowReturnImages 是否允许返图
 * @param profilePublic 是否公开资料
 * @param showVisitorDetails 是否显示访客详情
 */
@JsExport
@JsName("UserProfilePrivacySettings")
@Serializable
public data class UserProfilePrivacySettings(
    @SerialName("showEmail")
    public val showEmail: Boolean? = null,
    @SerialName("allowContact")
    public val allowContact: Boolean? = null,
    @SerialName("showLocation")
    public val showLocation: Boolean? = null,
    @SerialName("allow_messages")
    public val allowMessages: Boolean? = null,
    @SerialName("allow_return_images")
    public val allowReturnImages: Boolean? = null,
    @SerialName("profile_public")
    public val profilePublic: Boolean? = null,
    @SerialName("show_visitor_details")
    public val showVisitorDetails: Boolean? = null,
)

/**
 * 用户角色信息。
 *
 * @param id 角色 ID
 * @param name 角色名称
 * @param species 物种
 * @param gender 性别
 * @param worldview 世界观
 */
@JsExport
@JsName("UserProfileCharacter")
@Serializable
public data class UserProfileCharacter(
    @SerialName("id")
    public val id: String,
    @SerialName("name")
    public val name: String,
    @SerialName("species")
    public val species: String? = null,
    @SerialName("gender")
    public val gender: String? = null,
    @SerialName("worldview")
    public val worldview: String? = null,
)

/**
 * 用户目的地信息。
 *
 * @param id 目的地 ID
 * @param name 目的地名称
 * @param startDate 开始日期
 * @param endDate 结束日期
 * @param gatheringId 关联聚会 ID
 */
@JsExport
@JsName("UserDestination")
@Serializable
public data class UserDestination(
    @SerialName("id")
    public val id: Int,
    @SerialName("name")
    public val name: String,
    @SerialName("start_date")
    public val startDate: String? = null,
    @SerialName("end_date")
    public val endDate: String? = null,
    @SerialName("gathering_id")
    public val gatheringId: Int? = null,
)

// ==================== 用户 ID 查询 ====================

/**
 * 用户基础信息 ID 查询响应。
 *
 * 端点：GET /api/proxy/furtv/users/id
 */
@JsExport
@JsName("UserIdResponse")
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
 * 用户 ID 数据，包含 VDS 返回的用户基础信息。
 *
 * @param id 用户 ID
 * @param username 用户名
 * @param nickname 昵称
 * @param avatarUrl 头像 URL
 * @param fursuitSpecies 兽装物种
 * @param location 地理位置
 */
@JsExport
@JsName("UserIdData")
@Serializable
public data class UserIdData(
    @SerialName("id")
    public val id: Int,
    @SerialName("username")
    public val username: String,
    @SerialName("nickname")
    public val nickname: String? = null,
    @SerialName("avatar_url")
    public val avatarUrl: String? = null,
    @SerialName("fursuit_species")
    public val fursuitSpecies: String? = null,
    @SerialName("location")
    public val location: String? = null,
)

// ==================== 点赞状态 ====================

/**
 * 用户点赞状态响应。
 *
 * 端点：GET /api/proxy/furtv/fursuit/likestatus
 */
@JsExport
@JsName("LikeStatusResponse")
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
 * 点赞状态数据，包含 VDS 返回的点赞相关信息。
 *
 * @param likeCount 点赞总数
 * @param isLiked 当前用户是否已点赞
 * @param canLike 当前用户是否可以点赞
 * @param daysUntilCanLike 距离可点赞的天数
 */
@JsExport
@JsName("LikeStatusData")
@Serializable
public data class LikeStatusData(
    @SerialName("like_count")
    public val likeCount: Int,
    @SerialName("is_liked")
    public val isLiked: Boolean,
    @SerialName("can_like")
    public val canLike: Boolean,
    @SerialName("days_until_can_like")
    public val daysUntilCanLike: Int? = null,
)

// ==================== 用户关系 ====================

/**
 * 用户关系公开列表响应。
 *
 * 端点：GET /api/proxy/furtv/relationships/user
 */
@JsExport
@JsName("UserRelationshipsResponse")
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
 * 用户关系数据，包含 VDS 返回的关系列表。
 *
 * @param relationships 关系列表
 */
@JsExport
@JsName("UserRelationshipsData")
@Serializable
public data class UserRelationshipsData(
    @SerialName("relationships")
    public val relationships: List<RelationshipInfo>,
)

/**
 * 关系信息，表示一条用户关系记录。
 *
 * @param id 关系记录 ID
 * @param relationshipType 关系类型
 * @param createdAt 关系建立时间
 * @param partnerId 伴侣用户 ID
 * @param partnerUsername 伴侣用户名
 * @param partnerNickname 伴侣昵称
 * @param partnerAvatar 伴侣头像 URL
 * @param partnerSpecies 伴侣物种
 */
@JsExport
@JsName("RelationshipInfo")
@Serializable
public data class RelationshipInfo(
    @SerialName("id")
    public val id: Int,
    @SerialName("relationship_type")
    public val relationshipType: String,
    @SerialName("created_at")
    public val createdAt: String? = null,
    @SerialName("partner_id")
    public val partnerId: Int,
    @SerialName("partner_username")
    public val partnerUsername: String,
    @SerialName("partner_nickname")
    public val partnerNickname: String? = null,
    @SerialName("partner_avatar")
    public val partnerAvatar: String? = null,
    @SerialName("partner_species")
    public val partnerSpecies: String? = null,
)

// ==================== 用户访客 ====================

/**
 * 用户访客记录响应。
 *
 * 端点：GET /api/proxy/furtv/users/visitors
 */
@JsExport
@JsName("UserVisitorsResponse")
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
 * 用户访客数据，包含 VDS 返回的访客记录。
 *
 * @param visitors 访客列表
 * @param isOwner 当前用户是否为资料所有者
 * @param message 条件性提示消息
 */
@JsExport
@JsName("UserVisitorsData")
@Serializable
public data class UserVisitorsData(
    @SerialName("visitors")
    public val visitors: List<VisitorInfo>,
    @SerialName("isOwner")
    public val isOwner: Boolean? = null,
    @SerialName("message")
    public val message: String? = null,
)

/**
 * 访客信息，表示一条访客记录。
 *
 * @param visitorId 访客用户 ID
 * @param visitorUsername 访客用户名
 * @param visitorNickname 访客昵称
 * @param visitorAvatar 访客头像 URL
 * @param createdAt 访问时间
 */
@JsExport
@JsName("VisitorInfo")
@Serializable
public data class VisitorInfo(
    @SerialName("visitor_id")
    public val visitorId: Int,
    @SerialName("visitor_username")
    public val visitorUsername: String,
    @SerialName("visitor_nickname")
    public val visitorNickname: String? = null,
    @SerialName("visitor_avatar")
    public val visitorAvatar: String? = null,
    @SerialName("created_at")
    public val createdAt: String,
)

// ==================== 社交徽章 ====================

/**
 * 用户社交徽章列表响应。
 *
 * 端点：GET /api/proxy/furtv/users/socialbadges
 */
@JsExport
@JsName("SocialBadgesResponse")
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
 * 用户社交徽章数据，包含 VDS 返回的徽章列表。
 *
 * @param user 徽章所属用户
 * @param isOwner 当前用户是否为资料所有者
 * @param totalCount 徽章总数
 * @param badges 徽章列表
 */
@JsExport
@JsName("SocialBadgesData")
@Serializable
public data class SocialBadgesData(
    @SerialName("user")
    public val user: SocialBadgeUser? = null,
    @SerialName("is_owner")
    public val isOwner: Boolean? = null,
    @SerialName("total_count")
    public val totalCount: Int? = null,
    @SerialName("badges")
    public val badges: List<SocialBadge>,
)

/**
 * 社交徽章用户摘要信息。
 *
 * @param id 用户 ID
 * @param username 用户名
 * @param nickname 昵称
 */
@JsExport
@JsName("SocialBadgeUser")
@Serializable
public data class SocialBadgeUser(
    @SerialName("id")
    public val id: Int,
    @SerialName("username")
    public val username: String,
    @SerialName("nickname")
    public val nickname: String? = null,
)

/**
 * 社交徽章，表示一个用户已获得的徽章。
 *
 * @param userBadgeId 用户徽章记录 ID
 * @param badgeId 徽章模板 ID
 * @param title 徽章标题
 * @param glbUrl 3D 模型 URL（GLB 格式）
 * @param awardedAt 授予时间
 * @param expiresAt 过期时间
 * @param detailText 详情文本
 */
@JsExport
@JsName("SocialBadge")
@Serializable
public data class SocialBadge(
    @SerialName("user_badge_id")
    public val userBadgeId: Int,
    @SerialName("badge_id")
    public val badgeId: Int,
    @SerialName("title")
    public val title: String,
    @SerialName("glb_url")
    public val glbUrl: String? = null,
    @SerialName("awarded_at")
    public val awardedAt: String,
    @SerialName("expires_at")
    public val expiresAt: String? = null,
    @SerialName("detail_text")
    public val detailText: String? = null,
)

/**
 * 用户社交徽章详情响应。
 *
 * 端点：GET /api/proxy/furtv/users/socialbadges/detail
 */
@JsExport
@JsName("SocialBadgeDetailResponse")
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
 * 社交徽章详情数据，包含 VDS 返回的徽章详细信息。
 *
 * @param user 徽章所属用户
 * @param isOwner 当前用户是否为资料所有者
 * @param badge 徽章详情
 */
@JsExport
@JsName("SocialBadgeDetailData")
@Serializable
public data class SocialBadgeDetailData(
    @SerialName("user")
    public val user: SocialBadgeUser? = null,
    @SerialName("is_owner")
    public val isOwner: Boolean? = null,
    @SerialName("badge")
    public val badge: SocialBadgeDetail,
)

/**
 * 社交徽章详情，表示一个徽章的完整信息。
 *
 * @param userBadgeId 用户徽章记录 ID
 * @param badgeId 徽章模板 ID
 * @param title 徽章标题
 * @param glbUrl 3D 模型 URL（GLB 格式）
 * @param awardedAt 授予时间
 * @param expiresAt 过期时间
 * @param detailText 详情文本
 */
@JsExport
@JsName("SocialBadgeDetail")
@Serializable
public data class SocialBadgeDetail(
    @SerialName("user_badge_id")
    public val userBadgeId: Int,
    @SerialName("badge_id")
    public val badgeId: Int,
    @SerialName("title")
    public val title: String,
    @SerialName("glb_url")
    public val glbUrl: String? = null,
    @SerialName("awarded_at")
    public val awardedAt: String,
    @SerialName("expires_at")
    public val expiresAt: String? = null,
    @SerialName("detail_text")
    public val detailText: String? = null,
)

// ==================== 商店商品 ====================

/**
 * 用户商店商品响应。
 *
 * 端点：GET /api/proxy/furtv/users/storeproducts
 */
@JsExport
@JsName("StoreProductsResponse")
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
 * 用户商店商品数据，包含 VDS 返回的商品列表。
 *
 * @param user 商店所属用户
 * @param isOwner 当前用户是否为资料所有者
 * @param isMerchantVerified 是否已认证商家
 * @param products 商品列表
 */
@JsExport
@JsName("StoreProductsData")
@Serializable
public data class StoreProductsData(
    @SerialName("user")
    public val user: StoreUser? = null,
    @SerialName("is_owner")
    public val isOwner: Boolean? = null,
    @SerialName("is_merchant_verified")
    public val isMerchantVerified: Boolean? = null,
    @SerialName("products")
    public val products: List<StoreProduct>,
)

/**
 * 商店用户摘要信息。
 *
 * @param id 用户 ID
 * @param username 用户名
 * @param nickname 昵称
 */
@JsExport
@JsName("StoreUser")
@Serializable
public data class StoreUser(
    @SerialName("id")
    public val id: Int,
    @SerialName("username")
    public val username: String,
    @SerialName("nickname")
    public val nickname: String? = null,
)

/**
 * 商店商品，表示一个商店中的商品。
 *
 * @param id 商品 ID
 * @param name 商品名称
 * @param price 价格
 * @param imageUrl 商品图片 URL
 * @param externalUrl 外部购买链接
 * @param sortOrder 排序权重
 * @param createdAt 创建时间
 * @param updatedAt 更新时间
 */
@JsExport
@JsName("StoreProduct")
@Serializable
public data class StoreProduct(
    @SerialName("id")
    public val id: Int,
    @SerialName("name")
    public val name: String,
    @SerialName("price")
    public val price: String? = null,
    @SerialName("image_url")
    public val imageUrl: String? = null,
    @SerialName("external_url")
    public val externalUrl: String? = null,
    @SerialName("sort_order")
    public val sortOrder: Int? = null,
    @SerialName("created_at")
    public val createdAt: String? = null,
    @SerialName("updated_at")
    public val updatedAt: String? = null,
)
