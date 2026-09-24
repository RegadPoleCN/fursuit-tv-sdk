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

package com.furrist.rp.furtv.sdk.model

import kotlin.js.JsExport
import kotlin.js.JsName
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject

/** 用户资料公开信息响应。 */
@JsExport
@JsName("UserProfileResponse")
@Serializable
public data class UserProfileResponse(
    @SerialName("success") public val success: Boolean,
    @SerialName("user") public val user: UserProfile,
    @SerialName("requestId") public val requestId: String,
)

/** 用户资料公开信息。 */
@JsExport
@JsName("UserProfile")
@Serializable
public data class UserProfile(
    @SerialName("id") public val id: Int,
    @SerialName("username") public val username: String,
    @SerialName("nickname") public val nickname: String? = null,
    @SerialName("avatar_url") public val avatarUrl: String? = null,
    @SerialName("fursuit_species") public val fursuitSpecies: String? = null,
    @SerialName("fursuit_birthday") public val fursuitBirthday: String? = null,
    @SerialName("fursuit_maker") public val fursuitMaker: String? = null,
    @SerialName("showcase_portrait") public val showcasePortrait: String? = null,
    @SerialName("showcase_landscape") public val showcaseLandscape: String? = null,
    @SerialName("showcase_other") public val showcaseOther: String? = null,
    @SerialName("introduction") public val introduction: String? = null,
    @SerialName("interests") public val interests: List<String>? = null,
    @SerialName("location") public val location: String? = null,
    @SerialName("social_links") public val socialLinks: UserProfileSocialLinks? = null,
    @SerialName("contact_info") public val contactInfo: UserProfileContactInfo? = null,
    @Serializable(with = UserProfilePrivacySettingsSerializer::class)
    @SerialName("privacy_settings") public val privacySettings: UserProfilePrivacySettings? = null,
    @SerialName("characters") public val characters: List<UserProfileCharacter>? = null,
    @SerialName("other_verified_types") public val otherVerifiedTypes: List<String>? = null,
    @SerialName("view_count") public val viewCount: Int? = null,
    @SerialName("is_verified")
    @Serializable(with = BooleanAsIntSerializer::class)
    public val isVerified: Boolean? = null,
    @SerialName("created_at") public val createdAt: String? = null,
    @SerialName("destinations") public val destinations: List<UserDestination>? = null,
    @SerialName("destination") public val destination: String? = null,
    @SerialName("destination_expires_at") public val destinationExpiresAt: String? = null,
    @SerialName("fursuit_images") public val fursuitImages: List<String>? = null,
    @SerialName("page_banner") public val pageBanner: String? = null,
    @SerialName("platform_level") public val platformLevel: Int? = null,
    @SerialName("like_count") public val likeCount: Int? = null,
    @SerialName("contact_request") public val contactRequest: ContactRequestState? = null,
    @SerialName("has_completed_contact") @Serializable(with = BooleanAsIntSerializer::class)
    public val hasCompletedContact: Boolean? = null,
    @SerialName("profile_flags") public val profileFlags: List<String>? = null,
    @SerialName("contact_reputation_level") public val contactReputationLevel: Int? = null,
)

/** 用户社交链接。带自定义 KSerializer：反序列化时把 server JSON 拆为 entries (string-valued) + custom (CustomLink 列表)。 */
@JsExport
@JsName("UserProfileSocialLinks")
@Serializable(with = UserProfileSocialLinksSerializer::class)
public data class UserProfileSocialLinks(
    public val entries: Map<String, String> = emptyMap(),
    public val custom: List<CustomLink> = emptyList(),
)

/** 用户联系方式。同 UserProfileSocialLinks 模式。 */
@JsExport
@JsName("UserProfileContactInfo")
@Serializable(with = UserProfileContactInfoSerializer::class)
public data class UserProfileContactInfo(
    public val entries: Map<String, String> = emptyMap(),
    public val custom: List<CustomLink> = emptyList(),
)

/** 自定义链接（socialLinks/contactInfo 中 `custom` 数组元素）。 */
@JsExport
@JsName("CustomLink")
@Serializable
public data class CustomLink(
    public val url: String,
    public val name: String,
)

/** UserProfileSocialLinks 的 KSerializer。 */
public object UserProfileSocialLinksSerializer : KSerializer<UserProfileSocialLinks> {
    @Serializable
    private data class Surrogate(
        val entries: Map<String, String> = emptyMap(),
        val custom: List<CustomLink> = emptyList(),
    )

    override val descriptor: SerialDescriptor = Surrogate.serializer().descriptor

    override fun serialize(encoder: Encoder, value: UserProfileSocialLinks) {
        val surrogate = Surrogate(entries = value.entries, custom = value.custom)
        encoder.encodeSerializableValue(Surrogate.serializer(), surrogate)
    }

    override fun deserialize(decoder: Decoder): UserProfileSocialLinks {
        val surrogate = decoder.decodeSerializableValue(Surrogate.serializer())
        return UserProfileSocialLinks(entries = surrogate.entries, custom = surrogate.custom)
    }
}

/** UserProfileContactInfo 的 KSerializer。 */
public object UserProfileContactInfoSerializer : KSerializer<UserProfileContactInfo> {
    @Serializable
    private data class Surrogate(
        val entries: Map<String, String> = emptyMap(),
        val custom: List<CustomLink> = emptyList(),
    )

    override val descriptor: SerialDescriptor = Surrogate.serializer().descriptor

    override fun serialize(encoder: Encoder, value: UserProfileContactInfo) {
        val surrogate = Surrogate(entries = value.entries, custom = value.custom)
        encoder.encodeSerializableValue(Surrogate.serializer(), surrogate)
    }

    override fun deserialize(decoder: Decoder): UserProfileContactInfo {
        val surrogate = decoder.decodeSerializableValue(Surrogate.serializer())
        return UserProfileContactInfo(entries = surrogate.entries, custom = surrogate.custom)
    }
}

/** 用户扩列按钮状态（contact_request 嵌套对象）。 */
@JsExport
@JsName("ContactRequestState")
@Serializable
public data class ContactRequestState(
    @SerialName("button_state") public val buttonState: String? = null,
    @SerialName("can_request") public val canRequest: Boolean? = null,
    @SerialName("reason_code") public val reasonCode: String? = null,
    public val message: String? = null,
    @SerialName("requires_auth")
    @Serializable(with = BooleanAsIntSerializer::class)
    public val requiresAuth: Boolean? = null,
    @SerialName("button_text") public val buttonText: String? = null,
)

/** 用户资料隐私设置。 */
@JsExport
@JsName("UserProfilePrivacySettings")
@Serializable
public data class UserProfilePrivacySettings(
    @SerialName("show_email") public val showEmail: Boolean? = null,
    @SerialName("allow_contact") public val allowContact: Boolean? = null,
    @SerialName("show_location") public val showLocation: Boolean? = null,
    @SerialName("allow_messages") public val allowMessages: Boolean? = null,
    @SerialName("allow_return_images") public val allowReturnImages: Boolean? = null,
    @SerialName("profile_public") public val profilePublic: Boolean? = null,
    @SerialName("show_visitor_details") public val showVisitorDetails: Boolean? = null,
    @SerialName("allow_map_share_invites") public val allowMapShareInvites: Boolean? = null,
    @SerialName("contact_request_policy") public val contactRequestPolicy: String? = null,
    @SerialName("contact_request_min_level") public val contactRequestMinLevel: Int? = null,
    @SerialName("contact_request_block_flagged_users") public val contactRequestBlockFlaggedUsers: Boolean? = null,
)

/** UserProfilePrivacySettings 的 KSerializer：兼容 camelCase 与 snake_case。 */
public object UserProfilePrivacySettingsSerializer : KSerializer<UserProfilePrivacySettings> {
    private val camelToSnake: Map<String, String> =
        mapOf(
            "showEmail" to "show_email",
            "allowContact" to "allow_contact",
            "showLocation" to "show_location",
            "allowReturnImages" to "allow_return_images",
            "allowMapShareInvites" to "allow_map_share_invites",
            "contactRequestPolicy" to "contact_request_policy",
            "contactRequestMinLevel" to "contact_request_min_level",
            "contactRequestBlockFlaggedUsers" to "contact_request_block_flagged_users",
        )

    private val plugin: KSerializer<UserProfilePrivacySettings> by lazy { UserProfilePrivacySettings.serializer() }

    override val descriptor: SerialDescriptor
        get() = plugin.descriptor

    override fun serialize(encoder: Encoder, value: UserProfilePrivacySettings) {
        plugin.serialize(encoder, value)
    }

    override fun deserialize(decoder: Decoder): UserProfilePrivacySettings {
        val jsonDecoder = decoder as? JsonDecoder ?: error("UserProfilePrivacySettingsSerializer 仅支持 Json 格式")
        return jsonDecoder.json.decodeFromJsonElement(plugin, transform(jsonDecoder.decodeJsonElement()))
    }

    private fun transform(element: JsonElement): JsonElement {
        if (element !is JsonObject) return element
        val mapped = LinkedHashMap<String, JsonElement>()
        for ((key, value) in element) {
            val mappedKey = camelToSnake[key]
            mapped[mappedKey?.takeIf { !element.containsKey(it) } ?: key] = value
        }
        return JsonObject(mapped)
    }
}

/** 用户角色信息。 */
@JsExport
@JsName("UserProfileCharacter")
@Serializable
public data class UserProfileCharacter(
    @SerialName("id") public val id: String,
    @SerialName("name") public val name: String,
    @SerialName("species") public val species: String? = null,
    @SerialName("gender") public val gender: String? = null,
    @SerialName("worldview") public val worldview: String? = null,
)

/** 用户目的地信息。 */
@JsExport
@JsName("UserDestination")
@Serializable
public data class UserDestination(
    @SerialName("id") public val id: Int,
    @SerialName("name") public val name: String,
    @SerialName("start_date") public val startDate: String? = null,
    @SerialName("end_date") public val endDate: String? = null,
    @SerialName("gathering_id") public val gatheringId: Int? = null,
)

/** 用户 ID 查询响应。 */
@JsExport
@JsName("UserIdResponse")
@Serializable
public data class UserIdResponse(
    @SerialName("success") public val success: Boolean,
    @SerialName("user") public val user: UserIdData,
    @SerialName("requestId") public val requestId: String,
)

/** 用户 ID 数据。 */
@JsExport
@JsName("UserIdData")
@Serializable
public data class UserIdData(
    @SerialName("id") public val id: Int,
    @SerialName("username") public val username: String,
    @SerialName("nickname") public val nickname: String? = null,
    @SerialName("avatar_url") public val avatarUrl: String? = null,
    @SerialName("fursuit_species") public val fursuitSpecies: String? = null,
    @SerialName("location") public val location: String? = null,
)

/** 用户点赞状态响应。 */
@JsExport
@JsName("LikeStatusResponse")
@Serializable
public data class LikeStatusResponse(
    @SerialName("success") public val success: Boolean,
    @SerialName("like_count") public val likeCount: Long? = null,
    @SerialName("is_liked")
    @Serializable(with = BooleanAsIntSerializer::class)
    public val isLiked: Boolean? = null,
    @SerialName("days_until_can_like") public val daysUntilCanLike: Int? = null,
    @SerialName("requestId") public val requestId: String? = null,
)

/** 用户关系公开列表响应。 */
@JsExport
@JsName("UserRelationshipsResponse")
@Serializable
public data class UserRelationshipsResponse(
    @SerialName("relationships") public val relationships: List<RelationshipInfo>,
    @SerialName("requestId") public val requestId: String,
)

/** 关系信息。 */
@JsExport
@JsName("RelationshipInfo")
@Serializable
public data class RelationshipInfo(
    @SerialName("id") public val id: Int,
    @SerialName("relationship_type") public val relationshipType: String,
    @SerialName("created_at") public val createdAt: String? = null,
    @SerialName("partner_id") public val partnerId: Int,
    @SerialName("partner_username") public val partnerUsername: String,
    @SerialName("partner_nickname") public val partnerNickname: String? = null,
    @SerialName("partner_avatar") public val partnerAvatar: String? = null,
    @SerialName("partner_species") public val partnerSpecies: String? = null,
)

/** 用户访客记录响应。 */
@JsExport
@JsName("UserVisitorsResponse")
@Serializable
public data class UserVisitorsResponse(
    @SerialName("success") public val success: Boolean,
    @SerialName("visitors") public val visitors: List<VisitorInfo>? = null,
    @SerialName("isOwner")
    @Serializable(with = BooleanAsIntSerializer::class)
    public val isOwner: Boolean? = null,
    @SerialName("message") public val message: String? = null,
    @SerialName("total_views") public val totalViews: Long? = null,
    @SerialName("total_login_visits") public val totalLoginVisits: Long? = null,
    @SerialName("total_login_visitors") public val totalLoginVisitors: Long? = null,
    @SerialName("has_more") public val hasMore: Boolean? = null,
    @SerialName("requestId") public val requestId: String? = null,
)

/** 访客信息。 */
@JsExport
@JsName("VisitorInfo")
@Serializable
public data class VisitorInfo(
    @SerialName("visit_id") public val visitId: Int,
    @SerialName("visitor_id") public val visitorId: Int,
    @SerialName("visitor_username") public val visitorUsername: String,
    @SerialName("visitor_nickname") public val visitorNickname: String? = null,
    @SerialName("visitor_avatar") public val visitorAvatar: String? = null,
    @SerialName("created_at") public val createdAt: String,
)

/** 用户社交徽章列表响应。 */
@JsExport
@JsName("SocialBadgesResponse")
@Serializable
public data class SocialBadgesResponse(
    @SerialName("success") public val success: Boolean,
    @SerialName("user") public val user: SocialBadgeUser? = null,
    @SerialName("is_owner")
    @Serializable(with = BooleanAsIntSerializer::class)
    public val isOwner: Boolean? = null,
    @SerialName("total_count") public val totalCount: Int? = null,
    @SerialName("badges") public val badges: List<SocialBadge>,
    @SerialName("requestId") public val requestId: String,
)

/** 社交徽章用户摘要。 */
@JsExport
@JsName("SocialBadgeUser")
@Serializable
public data class SocialBadgeUser(
    @SerialName("id") public val id: Int,
    @SerialName("username") public val username: String,
    @SerialName("nickname") public val nickname: String? = null,
)

/** 社交徽章。 */
@JsExport
@JsName("SocialBadge")
@Serializable
public data class SocialBadge(
    @SerialName("user_badge_id") public val userBadgeId: Int,
    @SerialName("badge_id") public val badgeId: Int,
    @SerialName("title") public val title: String,
    @SerialName("glb_url") public val glbUrl: String? = null,
    @SerialName("awarded_at") public val awardedAt: String,
    @SerialName("expires_at") public val expiresAt: String? = null,
    @SerialName("detail_text") public val detailText: String? = null,
)

/** 社交徽章详情响应。 */
@JsExport
@JsName("SocialBadgeDetailResponse")
@Serializable
public data class SocialBadgeDetailResponse(
    @SerialName("success") public val success: Boolean,
    @SerialName("user") public val user: SocialBadgeUser? = null,
    @SerialName("is_owner")
    @Serializable(with = BooleanAsIntSerializer::class)
    public val isOwner: Boolean? = null,
    @SerialName("badge") public val badge: SocialBadgeDetail,
    @SerialName("requestId") public val requestId: String,
)

/** 社交徽章详情。 */
@JsExport
@JsName("SocialBadgeDetail")
@Serializable
public data class SocialBadgeDetail(
    @SerialName("user_badge_id") public val userBadgeId: Int,
    @SerialName("badge_id") public val badgeId: Int,
    @SerialName("title") public val title: String,
    @SerialName("glb_url") public val glbUrl: String? = null,
    @SerialName("awarded_at") public val awardedAt: String,
    @SerialName("expires_at") public val expiresAt: String? = null,
    @SerialName("detail_text") public val detailText: String? = null,
)

/** 商店商品响应。 */
@JsExport
@JsName("StoreProductsResponse")
@Serializable
public data class StoreProductsResponse(
    @SerialName("success") public val success: Boolean,
    @SerialName("user") public val user: StoreUser? = null,
    @SerialName("is_owner")
    @Serializable(with = BooleanAsIntSerializer::class)
    public val isOwner: Boolean? = null,
    @SerialName("is_merchant_verified")
    @Serializable(with = BooleanAsIntSerializer::class)
    public val isMerchantVerified: Boolean? = null,
    @SerialName("products") public val products: List<StoreProduct>,
    @SerialName("requestId") public val requestId: String,
)

/** 商店用户摘要。 */
@JsExport
@JsName("StoreUser")
@Serializable
public data class StoreUser(
    @SerialName("id") public val id: Int,
    @SerialName("username") public val username: String,
    @SerialName("nickname") public val nickname: String? = null,
)

/** 商店商品。 */
@JsExport
@JsName("StoreProduct")
@Serializable
public data class StoreProduct(
    @SerialName("id") public val id: Int,
    @SerialName("name") public val name: String,
    @SerialName("price") public val price: String? = null,
    @SerialName("image_url") public val imageUrl: String? = null,
    @SerialName("external_url") public val externalUrl: String? = null,
    @SerialName("sort_order") public val sortOrder: Int? = null,
    @SerialName("created_at") public val createdAt: String? = null,
    @SerialName("updated_at") public val updatedAt: String? = null,
)
