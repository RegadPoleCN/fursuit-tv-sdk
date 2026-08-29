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
import kotlin.time.Clock
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonTransformingSerializer

// ============================================================================
// Auth - 签名交换 & OAuth & 令牌管理模型
// ============================================================================

/**
 * 签名交换请求，用于获取 apiKey/accessToken。
 */
@JsExport
@JsName("TokenExchangeRequest")
@Serializable
public data class TokenExchangeRequest(
    @SerialName("clientId")
    public val clientId: String,
    @SerialName("clientSecret")
    public val clientSecret: String,
)

/** 令牌刷新信息。 */
@JsExport
@JsName("TokenRefreshInfo")
@Serializable
public data class TokenRefreshInfo(
    @SerialName("mode")
    public val mode: String,
    @SerialName("refreshWindowSeconds")
    public val refreshWindowSeconds: Int,
    @SerialName("previousTokenSecondsRemaining")
    public val previousTokenSecondsRemaining: Int,
)

/**
 * 令牌数据，包含访问令牌信息（签名交换接口返回）。
 *
 * 注：[appId] 字段保留以兼容服务端响应格式；与 [SdkConfig.clientId] 在多数情况下等价。
 */
@JsExport
@JsName("TokenData")
@Serializable
public data class TokenData(
    public val accessToken: String,
    public val apiKey: String,
    @SerialName("expiresInSeconds")
    public val expiresIn: Int,
    public val tokenType: String,
    @SerialName("appId")
    public val appId: String? = null,
    @SerialName("grants")
    public val grants: List<String>? = null,
    @SerialName("refresh")
    public val refresh: TokenRefreshInfo? = null,
    public val requestId: String? = null,
)

/** OAuth 授权 URL 参数。 */
@JsExport
@JsName("OAuthAuthorizeParams")
@Serializable
public data class OAuthAuthorizeParams(
    public val clientId: String,
    public val redirectUri: String,
    public val state: String? = null,
    public val scope: String? = null,
    public val responseType: String = "code",
    @SerialName("code_challenge")
    public val codeChallenge: String? = null,
    @SerialName("code_challenge_method")
    public val codeChallengeMethod: String? = null,
)

/**
 * OAuth 2.0 授权码流程 + 回调服务器配置（统一 `OAuthConfig`）。
 */
@JsExport
@JsName("OAuthConfig")
@Serializable
public data class OAuthConfig(
    public val callbackHost: String = DEFAULT_CALLBACK_HOST,
    public val callbackPort: Int = DEFAULT_CALLBACK_PORT,
    public val callbackPath: String = DEFAULT_CALLBACK_PATH,
    public val timeoutSeconds: Int = DEFAULT_TIMEOUT_SECONDS,
) {
    init {
        require(callbackPort in 1..MAX_PORT_NUMBER) {
            "callbackPort must be between 1 and $MAX_PORT_NUMBER"
        }
        require(callbackPath.startsWith("/")) { "callbackPath must start with '/'" }
        require(timeoutSeconds > 0) { "timeoutSeconds must be positive" }
        require(callbackHost.isNotBlank()) { "callbackHost must not be blank" }
    }

    public companion object {
        public const val DEFAULT_CALLBACK_HOST: String = "localhost"
        public const val DEFAULT_CALLBACK_PORT: Int = 8080
        public const val DEFAULT_CALLBACK_PATH: String = "/callback"
        public const val DEFAULT_TIMEOUT_SECONDS: Int = 300
        private const val MAX_PORT_NUMBER = 65535
    }
}

/** OAuth 令牌请求。 */
@JsExport
@JsName("OAuthTokenRequest")
@Serializable
public data class OAuthTokenRequest(
    @SerialName("grant_type")
    public val grantType: String = "authorization_code",
    @SerialName("client_secret")
    public val clientSecret: String,
    @SerialName("code")
    public val code: String,
    @SerialName("redirect_uri")
    public val redirectUri: String,
    @SerialName("client_id")
    public val clientId: String,
    @SerialName("code_verifier")
    public val codeVerifier: String? = null,
)

/** OAuth 令牌数据。 */
@JsExport
@JsName("OAuthTokenData")
@Serializable
public data class OAuthTokenData(
    @SerialName("access_token")
    public val accessToken: String,
    @SerialName("expires_in")
    public val expiresIn: Int,
    @SerialName("token_type")
    public val tokenType: String,
    public val scope: String? = null,
    @SerialName("refresh_token")
    public val refreshToken: String? = null,
    public val requestId: String? = null,
)

/** 用户信息数据。 */
@JsExport
@JsName("UserInfoData")
@Serializable
public data class UserInfoData(
    public val sub: String,
    public val nickname: String? = null,
    @SerialName("avatar_url")
    public val avatarUrl: String? = null,
    public val email: String? = null,
    public val name: String? = null,
    public val username: String? = null,
    @SerialName("updated_at")
    public val updatedAt: Long? = null,
    @SerialName("phone_number")
    public val phoneNumber: String? = null,
    @SerialName("iss")
    public val iss: String? = null,
    @SerialName("aud")
    public val aud: Long? = null,
    public val requestId: String? = null,
)

/** 令牌信息，SDK 内部使用的令牌存储结构（sealed class）。 */
@JsExport
@JsName("TokenInfo")
@Serializable
public sealed class TokenInfo {
    public abstract val expiresAt: Long
    public abstract val tokenType: String

    /** 剩余有效期 ≤ 270s 时返回 true（refresh window 300s − skew 30s）。 */
    @JsName("isExpired")
    public fun isExpired(): Boolean {
        val now = Clock.System.now().toEpochMilliseconds()
        return (expiresAt - now) <= REFRESH_WINDOW_MS - SKEW_MS
    }

    internal companion object {
        internal const val REFRESH_WINDOW_MS: Long = 300_000L
        internal const val SKEW_MS: Long = 30_000L
    }

    /** 平台签名，来自签名交换端点。 */
    @Serializable
    public data class Platform(
        public val apiKey: String,
        public override val expiresAt: Long,
        public override val tokenType: String,
    ) : TokenInfo()

    /** OAuth 用户令牌，来自 OAuth 授权码流程。 */
    @Serializable
    public data class OAuth(
        public val oauthToken: String,
        public val refreshToken: String? = null,
        public val scope: String? = null,
        public val redirectUri: String,
        public override val expiresAt: Long,
        public override val tokenType: String,
    ) : TokenInfo()
}

/** 将签名交换令牌数据转换为 TokenInfo.Platform。 */
@JsExport
@JsName("toTokenInfo")
public fun TokenData.toTokenInfo(): TokenInfo.Platform =
    TokenInfo.Platform(
        apiKey = apiKey,
        expiresAt = Clock.System.now().toEpochMilliseconds() + expiresIn * 1000L - TokenInfo.SKEW_MS,
        tokenType = tokenType,
    )

/** 将 OAuth 令牌数据转换为 TokenInfo.OAuth。 */
@JsExport
@JsName("toTokenInfoFromOAuth")
public fun OAuthTokenData.toTokenInfo(redirectUri: String): TokenInfo.OAuth =
    TokenInfo.OAuth(
        oauthToken = accessToken,
        refreshToken = refreshToken,
        scope = scope,
        redirectUri = redirectUri,
        expiresAt = Clock.System.now().toEpochMilliseconds() + expiresIn * 1000L - TokenInfo.SKEW_MS,
        tokenType = tokenType,
    )

// ============================================================================
// Base - 基础接口模型
// ============================================================================

/** HelloWorld 接口响应，无 data 包装。 */
@JsExport
@JsName("HelloWorldResponse")
@Serializable
public data class HelloWorldResponse(
    public val success: Boolean,
    public val message: String,
    public val verify: String,
    public val appId: String,
    public val requestId: String,
)

/** 健康检查接口响应。 */
@JsExport
@JsName("HealthResponse")
@Serializable
public data class HealthResponse(
    public val success: Boolean,
    public val message: String,
    public val timestamp: String,
    public val requestId: String,
)

/** Android 版本信息响应包装。 */
@JsExport
@JsName("AndroidVersionResponse")
@Serializable
public data class AndroidVersionResponse(
    public val success: Boolean,
    public val data: AndroidVersionData,
    public val requestId: String,
)

/** Android 应用版本数据。 */
@JsExport
@JsName("AndroidVersionData")
@Serializable
public data class AndroidVersionData(
    public val version: String,
    public val versionCode: Int,
    public val description: String,
    public val forceUpdate: Boolean,
    public val downloadUrl: String,
    public val updateTime: String,
    public val minSupportedVersion: String,
    public val changelog: List<String>,
)

/** Android 版本检查请求体。 */
@JsExport
@JsName("AndroidVersionCheckRequest")
@Serializable
public data class AndroidVersionCheckRequest(
    public val currentVersion: String,
    public val currentVersionCode: Int,
)

/** Android 版本检查响应包装。 */
@JsExport
@JsName("AndroidVersionCheckResponse")
@Serializable
public data class AndroidVersionCheckResponse(
    public val success: Boolean,
    public val data: AndroidVersionCheckData,
    public val requestId: String,
)

/** Android 版本检查结果数据。 */
@JsExport
@JsName("AndroidVersionCheckData")
@Serializable
public data class AndroidVersionCheckData(
    public val needUpdate: Boolean,
    public val forceUpdate: Boolean,
    public val currentVersion: VersionInfo,
    public val latestVersion: AndroidVersionData,
)

/** 版本基本信息。 */
@JsExport
@JsName("VersionInfo")
@Serializable
public data class VersionInfo(
    public val version: String,
    public val versionCode: Int,
)

/** 主题包清单响应包装。 */
@JsExport
@JsName("ThemePacksManifestResponse")
@Serializable
public data class ThemePacksManifestResponse(
    public val success: Boolean,
    public val data: ThemePacksManifestData,
    public val requestId: String,
)

/** 主题包清单数据。 */
@JsExport
@JsName("ThemePacksManifestData")
@Serializable
public data class ThemePacksManifestData(
    public val updatedAt: String,
    public val themes: List<ThemePack>,
)

/** 主题包信息。 */
@JsExport
@JsName("ThemePack")
@Serializable
public data class ThemePack(
    public val id: String,
    public val zipUrl: String,
    public val metadata: ThemePackMetadata? = null,
)

/**
 * vds-docs 服务端在多个端点用 `0`/`1` 整数或 `true`/`false` 布尔表示布尔值。
 * 解码时两者都接受：`0`=false，`1`=true，`true`=true，`false`=false`。
 * 序列化时统一输出整数（`true`=1，`false`=0`）。
 *
 * **设计动机**：vds-docs 同字段在不同端点混用 `0/1` 和 `true/false`。
 * SDK 一律对外暴露 `Boolean` 类型，业务方无需关心 wire format 差异。
 */
public object BooleanAsIntSerializer : KSerializer<Boolean> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("BooleanAsInt", PrimitiveKind.BOOLEAN)

    override fun serialize(encoder: Encoder, value: Boolean) {
        encoder.encodeInt(if (value) 1 else 0)
    }

    override fun deserialize(decoder: Decoder): Boolean {
        val raw = decoder.decodeString()
        return when (raw.lowercase()) {
            "true", "1" -> true
            "false", "0" -> false
            else -> raw.toBooleanStrictOrNull() ?: (raw.toIntOrNull()?.let { it != 0 } ?: false)
        }
    }
}

/** 主题包元信息。 */
@JsExport
@JsName("ThemePackMetadata")
@Serializable
public data class ThemePackMetadata(
    public val name: String? = null,
    public val author: ThemePackAuthor? = null,
    public val intro: String? = null,
    public val version: String? = null,
    public val themeCss: String? = null,
    public val homeBackground: ThemePackHomeBackground? = null,
    public val preview: ThemePackPreview? = null,
)

/** 主题包作者。 */
@JsExport
@JsName("ThemePackAuthor")
@Serializable
public data class ThemePackAuthor(
    public val username: String? = null,
)

/** 主题包首页背景设置。 */
@JsExport
@JsName("ThemePackHomeBackground")
@Serializable
public data class ThemePackHomeBackground(
    public val opacity: Double? = null,
    public val blur: Int? = null,
)

/** 主题包预览色板。 */
@JsExport
@JsName("ThemePackPreview")
@Serializable
public data class ThemePackPreview(
    public val surface0: String? = null,
    public val surface1: String? = null,
    public val accent: String? = null,
    public val accentRgb: String? = null,
    public val contrastRgb: String? = null,
)

// ============================================================================
// User - 用户资料模型
// ============================================================================

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

/** UserProfileSocialLinks 的 KSerializer：拆 server JSONObject 为 entries (string) + custom (数组)。 */
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

/** UserProfileContactInfo 的 KSerializer：同 UserProfileSocialLinksSerializer 结构。 */
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

/** 用户资料隐私设置。文档示例中部分键仅以 camelCase 出现（如随机推荐.md:273-275），双拼写兼容。 */
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

/**
 * UserProfilePrivacySettings 的 KSerializer：把 camelCase 键改写为 snake_case（#13）。
 * 服务端在不同端点混用两种拼写（用户资料公开信息.md、随机推荐.md），snake_case 原键优先不改写。
 */
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

    /** lazy：打破 "object 初始化 -> serializer() -> object 初始化" 的循环引用 */
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

/** 用户点赞状态响应（flat）。 */
@JsExport
@JsName("LikeStatusResponse")
@Serializable
public data class LikeStatusResponse(
    @SerialName("success") public val success: Boolean,
    @SerialName("like_count") public val likeCount: Long? = null,
    @SerialName("is_liked")
    @Serializable(with = BooleanAsIntSerializer::class)
    public val isLiked: Boolean? = null,
    @SerialName("can_like") public val canLike: Boolean? = null,
    @SerialName("days_until_can_like") public val daysUntilCanLike: Int? = null,
    @SerialName("requestId") public val requestId: String? = null,
)

/** 用户关系公开列表响应（flat）。 */
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

/** 用户访客记录响应（flat）。 */
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

/** 用户社交徽章列表响应（flat）。 */
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

/** 社交徽章详情响应（flat）。 */
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

/** 商店商品响应（flat）。 */
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

// ============================================================================
// Search - 搜索模型
// ============================================================================

/** 热门用户响应（flat：users 直接挂在顶层）。 */
@JsExport
@JsName("PopularResponse")
@Serializable
public data class PopularResponse(
    @SerialName("success") public val success: Boolean,
    @SerialName("users") public val users: List<PopularUser>,
    @SerialName("requestId") public val requestId: String,
)

/** 热门用户。 */
@JsExport
@JsName("PopularUser")
@Serializable
public data class PopularUser(
    @SerialName("id") public val id: Int,
    @SerialName("username") public val username: String,
    @SerialName("nickname") public val nickname: String? = null,
    @SerialName("avatar_url") public val avatarUrl: String? = null,
    @SerialName("fursuit_species") public val fursuitSpecies: String? = null,
    @SerialName("fursuit_maker") public val fursuitMaker: String? = null,
    @SerialName("showcase_portrait") public val showcasePortrait: String? = null,
    @SerialName("introduction") public val introduction: String? = null,
    @SerialName("view_count") public val viewCount: Int? = null,
    @SerialName("is_verified")
    @Serializable(with = BooleanAsIntSerializer::class)
    public val isVerified: Boolean? = null,
    @SerialName("like_count") public val likeCount: Int? = null,
    @SerialName("created_at") public val createdAt: String? = null,
    @SerialName("destination") public val destination: String? = null,
    @SerialName("destination_expires_at") public val destinationExpiresAt: String? = null,
    @SerialName("popularity_score") public val popularityScore: String? = null,
)

/** 随机推荐响应。 */
@JsExport
@JsName("RandomFursuitResponse")
@Serializable
public data class RandomFursuitResponse(
    @SerialName("success") public val success: Boolean,
    @SerialName("fursuit") public val fursuit: RandomFursuit? = null,
    @SerialName("fursuits") public val fursuits: List<RandomFursuit>? = null,
    @SerialName("count") public val count: Int? = null,
    @SerialName("requested_count") public val requestedCount: Int? = null,
    @SerialName("debug_info") public val debugInfo: RandomDebugInfo? = null,
    @SerialName("requestId") public val requestId: String,
)

/** 单个随机推荐 fursuit。 */
@JsExport
@JsName("RandomFursuit")
@Serializable
public data class RandomFursuit(
    @SerialName("id") public val id: Int,
    @SerialName("username") public val username: String,
    @SerialName("nickname") public val nickname: String? = null,
    @SerialName("avatar_url") public val avatarUrl: String? = null,
    @SerialName("fursuit_species") public val fursuitSpecies: String? = null,
    @SerialName("fursuit_maker") public val fursuitMaker: String? = null,
    @SerialName("location") public val location: String? = null,
    @SerialName("destination") public val destination: String? = null,
    @SerialName("introduction") public val introduction: String? = null,
    @SerialName("view_count") public val viewCount: Int? = null,
    @SerialName("is_verified")
    @Serializable(with = BooleanAsIntSerializer::class)
    public val isVerified: Boolean? = null,
    @SerialName("fursuit_birthday") public val fursuitBirthday: String? = null,
    @SerialName("showcase_portrait") public val showcasePortrait: String? = null,
    @SerialName("showcase_landscape") public val showcaseLandscape: String? = null,
    @SerialName("showcase_other") public val showcaseOther: String? = null,
    @SerialName("destinations") public val destinations: List<String>? = null,
    @SerialName("destination_expires_at") public val destinationExpiresAt: String? = null,
    @Serializable(with = UserProfilePrivacySettingsSerializer::class)
    @SerialName("privacy_settings") public val privacySettings: UserProfilePrivacySettings? = null,
    @SerialName("has_all_images") public val hasAllImages: Boolean? = null,
    @SerialName("contact_info") public val contactInfo: UserProfileContactInfo? = null,
    @SerialName("contact_request") public val contactRequest: ContactRequestState? = null,
    @SerialName("has_completed_contact")
    @Serializable(with = BooleanAsIntSerializer::class)
    public val hasCompletedContact: Boolean? = null,
    @SerialName("today_status") public val todayStatus: TodayStatus? = null,
)

/** 随机推荐用户今日状态（随机推荐.md `today_status`）。 */
@JsExport
@JsName("TodayStatus")
@Serializable
public data class TodayStatus(
    @SerialName("has_today") public val hasToday: Boolean? = null,
)

/** 随机推荐调试信息。 */
@JsExport
@JsName("RandomDebugInfo")
@Serializable
public data class RandomDebugInfo(
    @SerialName("is_personalized") public val isPersonalized: Boolean? = null,
    @SerialName("cache_hit_count") public val cacheHitCount: Int? = null,
    @SerialName("response_ms") public val responseMs: Int? = null,
)

/** 搜索响应（flat：users + 顶层元字段）。 */
@JsExport
@JsName("SearchResponse")
@Serializable
public data class SearchResponse(
    @SerialName("success") public val success: Boolean,
    @SerialName("users") public val users: List<SearchUser>,
    @SerialName("search_type") public val searchType: String? = null,
    @SerialName("search_keywords") public val searchKeywords: List<String>? = null,
    @SerialName("pagination") public val pagination: SearchPagination? = null,
    @SerialName("has_more") public val hasMore: Boolean = false,
    @SerialName("total") public val total: Int? = null,
    @SerialName("next_cursor") public val nextCursor: String? = null,
    @SerialName("total_is_estimate") public val totalIsEstimate: Boolean? = null,
    @SerialName("requestId") public val requestId: String,
)

/** 搜索分页信息。 */
@JsExport
@JsName("SearchPagination")
@Serializable
public data class SearchPagination(
    @SerialName("page") public val page: Int? = null,
    @SerialName("limit") public val limit: Int? = null,
    @SerialName("total") public val total: Int? = null,
    @SerialName("total_pages") public val totalPages: Int? = null,
    @SerialName("next_cursor") public val nextCursor: String? = null,
    @SerialName("total_is_estimate") public val totalIsEstimate: Boolean? = null,
)

/** 搜索结果中的用户。 */
@JsExport
@JsName("SearchUser")
@Serializable
public data class SearchUser(
    @SerialName("id") public val id: Int? = null,
    @SerialName("username") public val username: String? = null,
    @SerialName("nickname") public val nickname: String? = null,
    @SerialName("avatar_url") public val avatarUrl: String? = null,
    @SerialName("showcase_portrait") public val showcasePortrait: String? = null,
    @SerialName("fursuit_species") public val fursuitSpecies: String? = null,
    @SerialName("fursuit_maker") public val fursuitMaker: String? = null,
    @SerialName("location") public val location: String? = null,
    @SerialName("destinations") public val destinations: List<String>? = null,
    @SerialName("destination") public val destination: String? = null,
    @SerialName("destination_expires_at") public val destinationExpiresAt: String? = null,
    @SerialName("introduction") public val introduction: String? = null,
    @SerialName("view_count") public val viewCount: Int? = null,
    @SerialName("is_verified")
    @Serializable(with = BooleanAsIntSerializer::class)
    public val isVerified: Boolean? = null,
    @SerialName("created_at") public val createdAt: String? = null,
    @SerialName("like_count") public val likeCount: Int? = null,
    @SerialName("is_liked")
    @Serializable(with = BooleanAsIntSerializer::class)
    public val isLiked: Boolean? = null,
)

/** 搜索建议响应（flat：suggestions 直接挂在顶层）。 */
@JsExport
@JsName("SearchSuggestionsResponse")
@Serializable
public data class SearchSuggestionsResponse(
    @SerialName("success") public val success: Boolean,
    @SerialName("suggestions") public val suggestions: List<String>,
    @SerialName("requestId") public val requestId: String,
)

/** 物种搜索响应（flat：species + users + 元字段）。 */
@JsExport
@JsName("SpeciesSearchResponse")
@Serializable
public data class SpeciesSearchResponse(
    @SerialName("success") public val success: Boolean,
    @SerialName("species") public val species: String,
    @SerialName("users") public val users: List<SpeciesSearchUser>,
    @SerialName("pagination") public val pagination: SearchPagination? = null,
    @SerialName("has_more") public val hasMore: Boolean = false,
    @SerialName("total") public val total: Int? = null,
    @SerialName("next_cursor") public val nextCursor: String? = null,
    @SerialName("total_is_estimate") public val totalIsEstimate: Boolean? = null,
    @SerialName("requestId") public val requestId: String,
)

/** 物种搜索用户。 */
@JsExport
@JsName("SpeciesSearchUser")
@Serializable
public data class SpeciesSearchUser(
    @SerialName("id") public val id: Int? = null,
    @SerialName("username") public val username: String? = null,
    @SerialName("nickname") public val nickname: String? = null,
    @SerialName("avatar_url") public val avatarUrl: String? = null,
    @SerialName("showcase_portrait") public val showcasePortrait: String? = null,
    @SerialName("fursuit_species") public val fursuitSpecies: String? = null,
    @SerialName("fursuit_maker") public val fursuitMaker: String? = null,
    @SerialName("introduction") public val introduction: String? = null,
    @SerialName("view_count") public val viewCount: Int? = null,
    @SerialName("is_verified")
    @Serializable(with = BooleanAsIntSerializer::class)
    public val isVerified: Boolean? = null,
    @SerialName("created_at") public val createdAt: String? = null,
    @SerialName("like_count") public val likeCount: Int? = null,
    @SerialName("is_liked")
    @Serializable(with = BooleanAsIntSerializer::class)
    public val isLiked: Boolean? = null,
)

/** 物种列表响应（flat）。 */
@JsExport
@JsName("SpeciesListResponse")
@Serializable
public data class SpeciesListResponse(
    @SerialName("success") public val success: Boolean,
    @SerialName("species") public val species: List<SpeciesInfo>,
    @SerialName("total") public val total: Int,
    @SerialName("requestId") public val requestId: String,
)

/** 物种信息。 */
@JsExport
@JsName("SpeciesInfo")
@Serializable
public data class SpeciesInfo(
    @SerialName("species") public val species: String,
    @SerialName("count") public val count: Int,
)

/** 热门地点响应（flat）。 */
@JsExport
@JsName("PopularLocationsResponse")
@Serializable
public data class PopularLocationsResponse(
    @SerialName("success") public val success: Boolean,
    @SerialName("popular_provinces") public val popularProvinces: List<ProvinceInfo>,
    @SerialName("popular_cities") public val popularCities: List<CityInfo>,
    @SerialName("total_users") public val totalUsers: Int? = null,
    @SerialName("requestId") public val requestId: String,
)

/** 省份信息。 */
@JsExport
@JsName("ProvinceInfo")
@Serializable
public data class ProvinceInfo(
    @SerialName("province") public val province: String,
    @SerialName("count") public val count: Int,
)

/** 城市信息。 */
@JsExport
@JsName("CityInfo")
@Serializable
public data class CityInfo(
    @SerialName("province") public val province: String,
    @SerialName("city") public val city: String,
    @SerialName("count") public val count: Int,
)

// ============================================================================
// Gathering - 聚会模型
// ============================================================================

/** 聚会年度统计响应（flat：total 直接挂在顶层）。 */
@JsExport
@JsName("GatheringYearStatsResponse")
@Serializable
public data class GatheringYearStatsResponse(
    @SerialName("success") public val success: Boolean,
    @SerialName("total") public val total: Int,
    @SerialName("requestId") public val requestId: String,
)

/** 聚会月历响应（data 改为 Object 类型 GatheringMonthlyData）。 */
@JsExport
@JsName("GatheringMonthlyResponse")
@Serializable
public data class GatheringMonthlyResponse(
    @SerialName("success") public val success: Boolean,
    @SerialName("data") public val data: GatheringMonthlyData,
    @SerialName("requestId") public val requestId: String,
)

/** 聚会月历数据。 */
@JsExport
@JsName("GatheringMonthlyData")
@Serializable
public data class GatheringMonthlyData(
    @SerialName("year") public val year: Int,
    @SerialName("month") public val month: Int,
    @SerialName("gatherings") public val gatherings: List<GatheringMonthlyItem>,
    @SerialName("total") public val total: Int,
)

/** 聚会月历项。 */
@JsExport
@JsName("GatheringMonthlyItem")
@Serializable
public data class GatheringMonthlyItem(
    @SerialName("id") public val id: Int,
    @SerialName("title") public val title: String,
    @SerialName("description") public val description: String? = null,
    @SerialName("type") public val type: String? = null,
    @SerialName("typeClass") public val typeClass: String? = null,
    @SerialName("content_source") public val contentSource: String? = null,
    @SerialName("day") public val day: String? = null,
    @SerialName("weekday") public val weekday: String? = null,
    @SerialName("time") public val time: String? = null,
    @SerialName("endTime") public val endTime: String? = null,
    @SerialName("location") public val location: String? = null,
    @SerialName("locationPublic") public val locationPublic: String? = null,
    @SerialName("participants") public val participants: String? = null,
    @SerialName("logo") public val logo: String? = null,
    @SerialName("status") public val status: String? = null,
    @SerialName("badges") public val badges: List<GatheringBadge>? = null,
    @SerialName("is_furtv_coop_driven")
    @Serializable(with = BooleanAsIntSerializer::class)
    public val isFurtvCoopDriven: Boolean? = null,
    @SerialName("sourceCount") public val sourceCount: Int? = null,
    @SerialName("initialSource") public val initialSource: String? = null,
    @SerialName("dataSources") public val dataSources: List<DataSource>? = null,
    @SerialName("organizer") public val organizer: String? = null,
    @SerialName("organizerAvatar") public val organizerAvatar: String? = null,
    @SerialName("feeType") public val feeType: String? = null,
    @SerialName("feeAmount") public val feeAmount: String? = null,
    @SerialName("registrationStatus") public val registrationStatus: String? = null,
    @SerialName("requiresApproval") @Serializable(with = BooleanAsIntSerializer::class)
    public val requiresApproval: Boolean? = null,
)

/** 聚会徽章。 */
@JsExport
@JsName("GatheringBadge")
@Serializable
public data class GatheringBadge(
    @SerialName("code") public val code: String? = null,
    @SerialName("title") public val title: String? = null,
)

/** 数据来源。 */
@JsExport
@JsName("DataSource")
@Serializable
public data class DataSource(
    @SerialName("source_code") public val sourceCode: String? = null,
    @SerialName("source_url") public val sourceUrl: String? = null,
    @SerialName("name") public val name: String? = null,
    @SerialName("logo_url") public val logoUrl: String? = null,
)

/** 聚会月历距离响应（data 改为 Object 类型 GatheringMonthlyDistanceData）。 */
@JsExport
@JsName("GatheringMonthlyDistanceResponse")
@Serializable
public data class GatheringMonthlyDistanceResponse(
    @SerialName("success") public val success: Boolean,
    @SerialName("data") public val data: GatheringMonthlyDistanceData,
    @SerialName("requestId") public val requestId: String,
)

/** 聚会月历距离数据。 */
@JsExport
@JsName("GatheringMonthlyDistanceData")
@Serializable
public data class GatheringMonthlyDistanceData(
    @SerialName("year") public val year: Int,
    @SerialName("month") public val month: Int,
    @SerialName("distances") public val distances: List<GatheringMonthlyDistanceItem>,
)

/** 聚会月历距离项。 */
@JsExport
@JsName("GatheringMonthlyDistanceItem")
@Serializable
public data class GatheringMonthlyDistanceItem(
    @SerialName("id") public val id: Int,
    @SerialName("distance_meters") public val distanceMeters: Double? = null,
)

/** 聚会附近响应。 */
@JsExport
@JsName("GatheringNearbyResponse")
@Serializable
public data class GatheringNearbyResponse(
    @SerialName("success") public val success: Boolean,
    @SerialName("data") public val data: List<GatheringNearbyItem>,
    @SerialName("requestId") public val requestId: String,
)

/** 聚会附近项。 */
@JsExport
@JsName("GatheringNearbyItem")
@Serializable
public data class GatheringNearbyItem(
    @SerialName("id") public val id: Int,
    @SerialName("title") public val title: String,
    @SerialName("event_date") public val eventDate: String? = null,
    @SerialName("end_date") public val endDate: String? = null,
    @SerialName("address") public val address: String? = null,
    @SerialName("city") public val city: String? = null,
    @SerialName("lat") public val lat: Double? = null,
    @SerialName("lng") public val lng: Double? = null,
    @SerialName("badges") public val badges: List<GatheringBadge>? = null,
    @SerialName("is_furtv_coop_driven")
    @Serializable(with = BooleanAsIntSerializer::class)
    public val isFurtvCoopDriven: Boolean? = null,
    @SerialName("event_time") public val eventTime: String? = null,
    @SerialName("end_clock_time") public val endClockTime: String? = null,
    @SerialName("start_time") public val startTime: String? = null,
    @SerialName("end_time") public val endTime: String? = null,
    @SerialName("time_zone") public val timeZone: String? = null,
)

/** 聚会附近模式响应。 */
@JsExport
@JsName("GatheringNearbyModeResponse")
@Serializable
public data class GatheringNearbyModeResponse(
    @SerialName("success") public val success: Boolean,
    @SerialName("data") public val data: GatheringNearbyModeData,
    @SerialName("requestId") public val requestId: String,
)

/** 聚会附近模式数据。 */
@JsExport
@JsName("GatheringNearbyModeData")
@Serializable
public data class GatheringNearbyModeData(
    @SerialName("gatherings") public val gatherings: List<GatheringNearbyModeItem>,
    @SerialName("intent_gathering_ids") public val intentGatheringIds: List<Int>,
)

/**
 * 聚会附近模式项（聚会附近模式.md 响应元素；#15）。
 * 注：文档第 4 行明示 `participants` 为服务端预留字段（现阶段不处理），故不建模。
 */
@JsExport
@JsName("GatheringNearbyModeItem")
@Serializable
public data class GatheringNearbyModeItem(
    @SerialName("id") public val id: Int,
    @SerialName("title") public val title: String,
    @SerialName("event_date") public val eventDate: String? = null,
    @SerialName("end_date") public val endDate: String? = null,
    @SerialName("event_time") public val eventTime: String? = null,
    @SerialName("end_clock_time") public val endClockTime: String? = null,
    @SerialName("start_time") public val startTime: String? = null,
    @SerialName("end_time") public val endTime: String? = null,
    @SerialName("time_zone") public val timeZone: String? = null,
    @SerialName("avatar_url") public val avatarUrl: String? = null,
    @SerialName("current_participants") public val currentParticipants: Int? = null,
    @SerialName("max_participants") public val maxParticipants: Int? = null,
    @SerialName("address") public val address: String? = null,
    @SerialName("city") public val city: String? = null,
    @SerialName("lat") public val lat: Double? = null,
    @SerialName("lng") public val lng: Double? = null,
    @SerialName("badges") public val badges: List<GatheringBadge>? = null,
    @SerialName("is_furtv_coop_driven")
    @Serializable(with = BooleanAsIntSerializer::class)
    public val isFurtvCoopDriven: Boolean? = null,
)

/** 聚会详情响应（data → gathering）。 */
@JsExport
@JsName("GatheringDetailResponse")
@Serializable
public data class GatheringDetailResponse(
    @SerialName("success") public val success: Boolean,
    @SerialName("gathering") public val gathering: GatheringDetailData,
    @SerialName("requestId") public val requestId: String,
)

/** 聚会详情数据。 */
@JsExport
@JsName("GatheringDetailData")
@Serializable
public data class GatheringDetailData(
    @SerialName("id") public val id: Int,
    @SerialName("title") public val title: String,
    @SerialName("description") public val description: String? = null,
    @SerialName("event_date") public val eventDate: String? = null,
    @SerialName("end_date") public val endDate: String? = null,
    @SerialName("event_time") public val eventTime: String? = null,
    @SerialName("end_time") public val endTime: String? = null,
    @SerialName("type") public val type: String? = null,
    @SerialName("type_class") public val typeClass: String? = null,
    @SerialName("type_display") public val typeDisplay: String? = null,
    @SerialName("status") public val status: String? = null,
    @SerialName("location_public") public val locationPublic: String? = null,
    @SerialName("location_city") public val locationCity: String? = null,
    @SerialName("location_lat") public val locationLat: Double? = null,
    @SerialName("location_lng") public val locationLng: Double? = null,
    @SerialName("logo_url") public val logoUrl: String? = null,
    @SerialName("banner_url") public val bannerUrl: String? = null,
    @SerialName("organizer_id") public val organizerId: Int? = null,
    @SerialName("organizer_username") public val organizerUsername: String? = null,
    @SerialName("organizer_nickname") public val organizerNickname: String? = null,
    @SerialName("organizer_avatar") public val organizerAvatar: String? = null,
    @SerialName("co_organizers") public val coOrganizers: List<CoOrganizer>? = null,
    @SerialName("agenda") public val agenda: List<AgendaItem>? = null,
    @SerialName("tags") public val tags: List<String>? = null,
    @SerialName("source_count") public val sourceCount: Int? = null,
    @SerialName("data_sources") public val dataSources: List<DataSource>? = null,
    @SerialName("badges") public val badges: List<GatheringBadge>? = null,
    @SerialName("is_furtv_coop_driven")
    @Serializable(with = BooleanAsIntSerializer::class)
    public val isFurtvCoopDriven: Boolean? = null,
    @SerialName("interested_count") public val interestedCount: Int? = null,
    @SerialName("is_interested") public val isInterested: Boolean? = null,
    @SerialName("going_friends_count") public val goingFriendsCount: Int? = null,
    @SerialName("registration_stats") public val registrationStats: GatheringRegistrationStats? = null,
    @SerialName("view_count") public val viewCount: Int? = null,
    @SerialName("location_district") public val locationDistrict: String? = null,
    @SerialName("location_detail") public val locationDetail: String? = null,
    @SerialName("fee_type") public val feeType: String? = null,
    @SerialName("fee_amount") public val feeAmount: String? = null,
    @SerialName("fee_description") public val feeDescription: String? = null,
    @SerialName("registration_deadline") public val registrationDeadline: String? = null,
    @SerialName("registration_open_days") public val registrationOpenDays: Int? = null,
    @SerialName("requires_approval") @Serializable(with = BooleanAsIntSerializer::class)
    public val requiresApproval: Boolean? = null,
    @SerialName("auto_approve_limit") public val autoApproveLimit: Int? = null,
    @SerialName("group_chat_link") public val groupChatLink: String? = null,
    @SerialName("group_chat_type") public val groupChatType: String? = null,
    @SerialName("cancellation_reason") public val cancellationReason: String? = null,
    @SerialName("requirements") public val requirements: String? = null,
    @SerialName("notes") public val notes: String? = null,
    @SerialName("is_recurring")
    @Serializable(with = BooleanAsIntSerializer::class)
    public val isRecurring: Boolean? = null,
    @SerialName("series_id") public val seriesId: Int? = null,
    @SerialName("parent_gathering_id") public val parentGatheringId: Int? = null,
    @SerialName("recurrence_rule") public val recurrenceRule: String? = null,
    @SerialName("recurrence_config") public val recurrenceConfig: String? = null,
    @SerialName("destination_count") public val destinationCount: Int? = null,
    @SerialName("interested_friends") public val interestedFriends: List<String>? = null,
    @SerialName("going_friends") public val goingFriends: List<String>? = null,
    @SerialName("organizer_species") public val organizerSpecies: String? = null,
    @SerialName("max_participants") public val maxParticipants: Int? = null,
    @SerialName("current_participants") public val currentParticipants: Int? = null,
    @SerialName("min_participants") public val minParticipants: Int? = null,
    @SerialName("content_source") public val contentSource: String? = null,
    @SerialName("external_source") public val externalSource: String? = null,
    @SerialName("external_id") public val externalId: String? = null,
    @SerialName("source_url") public val sourceUrl: String? = null,
    @SerialName("raw_payload") public val rawPayload: JsonObject? = null,
    @SerialName("sync_skip_source_updates") public val syncSkipSourceUpdates: Int? = null,
    @SerialName("sync_skip_reason") public val syncSkipReason: String? = null,
    @SerialName("sync_skip_updated_at") public val syncSkipUpdatedAt: String? = null,
    @SerialName("sync_skip_updated_by") public val syncSkipUpdatedBy: String? = null,
    @SerialName("organizer_type") public val organizerType: String? = null,
    @SerialName("registration_status") public val registrationStatus: String? = null,
    @SerialName("created_at") public val createdAt: String? = null,
    @SerialName("updated_at") public val updatedAt: String? = null,
    @SerialName("is_furtv_coop_badge_enabled")
    @Serializable(with = BooleanAsIntSerializer::class)
    public val isFurtvCoopBadgeEnabled: Boolean? = null,
)

/** 议程项。 */
@JsExport
@JsName("AgendaItem")
@Serializable
public data class AgendaItem(
    @SerialName("time") public val time: String,
    @SerialName("title") public val title: String,
    @SerialName("description") public val description: String? = null,
)

/** 协办者。 */
@JsExport
@JsName("CoOrganizer")
@Serializable
public data class CoOrganizer(
    @SerialName("user_id") public val userId: Int? = null,
    @SerialName("username") public val username: String? = null,
    @SerialName("nickname") public val nickname: String? = null,
    @SerialName("avatar") public val avatar: String? = null,
)

/** 聚会报名统计。 */
@JsExport
@JsName("GatheringRegistrationStats")
@Serializable
public data class GatheringRegistrationStats(
    @SerialName("total_registrations") public val totalRegistrations: Int? = null,
    @SerialName("approved_count") public val approvedCount: Int? = null,
    @SerialName("pending_count") public val pendingCount: Int? = null,
)

/** 聚会报名列表响应（flat：registrations 直接挂在顶层）。 */
@JsExport
@JsName("GatheringRegistrationsResponse")
@Serializable
public data class GatheringRegistrationsResponse(
    @SerialName("success") public val success: Boolean,
    @SerialName("registrations") public val registrations: List<List<RegistrationItem>>,
    @SerialName("requestId") public val requestId: String,
)

/** 报名项。 */
@JsExport
@JsName("RegistrationItem")
@Serializable
public data class RegistrationItem(
    @SerialName("id") public val id: Int,
    @SerialName("status") public val status: String? = null,
    @SerialName("registration_time") public val registrationTime: String? = null,
    @SerialName("checked_in") public val checkedIn: Int? = null,
    @SerialName("user_id") public val userId: Int,
    @SerialName("username") public val username: String,
    @SerialName("nickname") public val nickname: String? = null,
    @SerialName("avatar_url") public val avatarUrl: String? = null,
    @SerialName("fursuit_species") public val fursuitSpecies: String? = null,
)

// ============================================================================
// School - 学校模型
// ============================================================================

/** 学校搜索响应（flat）。 */
@JsExport
@JsName("SchoolSearchResponse")
@Serializable
public data class SchoolSearchResponse(
    @SerialName("schools") public val schools: List<SchoolInfo>,
    @SerialName("requestId") public val requestId: String,
)

/** 学校基本信息。 */
@JsExport
@JsName("SchoolInfo")
@Serializable
public data class SchoolInfo(
    @SerialName("id") public val id: Int,
    @SerialName("name") public val name: String,
    @SerialName("short_name") public val shortName: String? = null,
    @SerialName("location") public val location: String? = null,
    @SerialName("type") public val type: String? = null,
    @SerialName("logo_url") public val logoUrl: String? = null,
    @SerialName("student_count") public val studentCount: Int? = null,
)

/** 学校详情响应（无 success）。 */
@JsExport
@JsName("SchoolDetailResponse")
@Serializable
public data class SchoolDetailResponse(
    @SerialName("school") public val school: SchoolDetail,
    @SerialName("requestId") public val requestId: String,
)

/** 学校详情。 */
@JsExport
@JsName("SchoolDetail")
@Serializable
public data class SchoolDetail(
    @SerialName("id") public val id: Int,
    @SerialName("name") public val name: String,
    @SerialName("short_name") public val shortName: String? = null,
    @SerialName("location") public val location: String? = null,
    @SerialName("type") public val type: String? = null,
    @SerialName("logo_url") public val logoUrl: String? = null,
    @SerialName("student_count") public val studentCount: Int? = null,
)

/** 用户学校信息响应（vds-docs 顶层 `schools: [...]` + requestId，无 success 字段、无 data 包装）。 */
@JsExport
@JsName("UserSchoolsResponse")
@Serializable
public data class UserSchoolsResponse(
    @SerialName("schools") public val schools: List<UserSchoolInfo>,
    @SerialName("requestId") public val requestId: String,
)

/** 用户学校关联信息。 */
@JsExport
@JsName("UserSchoolInfo")
@Serializable
public data class UserSchoolInfo(
    @SerialName("user_school_id") public val userSchoolId: Int? = null,
    @SerialName("class_name") public val className: String? = null,
    @SerialName("enrollment_year") public val enrollmentYear: Int? = null,
    @SerialName("graduation_year") public val graduationYear: Int? = null,
    @SerialName("is_current") public val isCurrent: Int? = null,
    @SerialName("is_public") public val isPublic: Int? = null,
    @SerialName("school_id") public val schoolId: Int,
    @SerialName("school_name") public val schoolName: String,
    @SerialName("short_name") public val shortName: String? = null,
    @SerialName("location") public val location: String? = null,
    @SerialName("type") public val type: String? = null,
    @SerialName("logo_url") public val logoUrl: String? = null,
    @SerialName("student_count") public val studentCount: Int? = null,
)

/** 用户角色列表响应（characters 直接挂在顶层；UserCharactersData 已删除）。 */
@JsExport
@JsName("UserCharactersResponse")
@Serializable
public data class UserCharactersResponse(
    @SerialName("success") public val success: Boolean,
    @SerialName("characters") public val characters: List<CharacterInfo>,
    @SerialName("requestId") public val requestId: String,
)

/** 角色信息。 */
@JsExport
@JsName("CharacterInfo")
@Serializable
public data class CharacterInfo(
    @SerialName("id") public val id: String,
    @SerialName("name") public val name: String,
    @SerialName("species") public val species: String? = null,
    @SerialName("gender") public val gender: String? = null,
    @SerialName("worldview") public val worldview: String? = null,
    @SerialName("images") public val images: List<String>? = null,
    @SerialName("birthday") public val birthday: String? = null,
    @SerialName("created_at") public val createdAt: String? = null,
    @SerialName("updated_at") public val updatedAt: String? = null,
)

// ============================================================================
// Common - 通用模型
// ============================================================================

/**
 * 通用 API 响应包装（顶层 success/data/requestId）。
 * @param T 响应数据类型
 */
@JsExport
@JsName("ApiResponse")
@Serializable
public data class ApiResponse<T>(
    public val success: Boolean,
    public val data: T,
    public val requestId: String,
)

/** 分页请求参数。 */
@JsExport
@JsName("PaginationParams")
@Serializable
public data class PaginationParams(
    public val cursor: String? = null,
    public val limit: Int? = null,
)

/** 分页响应数据。 */
@JsExport
@JsName("PaginatedResponse")
@Serializable
public data class PaginatedResponse<T>(
    public val items: List<T>,
    public val nextCursor: String? = null,
    public val hasMore: Boolean = false,
)

/** 地理位置坐标。 */
@JsExport
@JsName("GeoLocation")
@Serializable
public data class GeoLocation(
    public val lat: Double,
    public val lng: Double,
)

/** 图片资源。 */
@JsExport
@JsName("ImageResource")
@Serializable
public data class ImageResource(
    public val url: String,
    public val width: Int? = null,
    public val height: Int? = null,
)

/** 时间范围（ISO 8601 格式）。 */
@JsExport
@JsName("TimeRange")
@Serializable
public data class TimeRange(
    public val start: String,
    public val end: String,
)

// ============================================================================
// Api Params - 各 API 的参数对象
// ============================================================================

/** 搜索参数。 */
@JsExport
@JsName("SearchParams")
@Serializable
public data class SearchParams(
    public val query: String,
    public val type: String? = null,
    public val cursor: String? = null,
    public val limit: Int? = null,
    public val page: Int? = null,
)

/** 随机推荐参数。 */
@JsExport
@JsName("RandomFursuitParams")
@Serializable
public data class RandomFursuitParams(
    public val count: Int? = null,
    public val personalized: Boolean? = null,
)

/** 聚会月历参数。 */
@JsExport
@JsName("GatheringMonthlyParams")
@Serializable
public data class GatheringMonthlyParams(
    public val year: Int,
    public val month: Int,
)

/** 附近聚会参数。 */
@JsExport
@JsName("GatheringNearbyParams")
@Serializable
public data class GatheringNearbyParams(
    public val lat: Double? = null,
    public val lng: Double? = null,
    public val radius: Int? = null,
)

/** 聚会报名列表参数。 */
@JsExport
@JsName("GatheringRegistrationsParams")
@Serializable
public data class GatheringRegistrationsParams(
    public val gatheringId: String,
    public val status: String? = null,
    public val cursor: String? = null,
    public val limit: Int? = null,
)

/** 学校搜索参数。 */
@JsExport
@JsName("SchoolSearchParams")
@Serializable
public data class SchoolSearchParams(
    public val query: String,
    public val cursor: String? = null,
    public val limit: Int? = null,
)
