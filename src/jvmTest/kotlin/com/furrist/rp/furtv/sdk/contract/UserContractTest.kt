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

package com.furrist.rp.furtv.sdk.contract

import com.furrist.rp.furtv.sdk.model.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import kotlinx.serialization.json.Json

class UserContractTest {
    private val json =
        Json {
            ignoreUnknownKeys = true
            isLenient = true
        }

    @Test fun `user profile flat with UserProfileSocialLinks typed wrapper`() {
        val wrapper = json.decodeFromString<UserProfileResponse>(ContractFixture.readFixture("vdsdocs/user/profile.json"))
        val profile = wrapper.user
        assertEquals(1024, profile.id)
        assertEquals("fox_demo", profile.username)
        assertEquals("狐狐", profile.nickname)
        // socialLinks is a typed wrapper; verify deserialization works
        assertNotNull(profile.socialLinks)
        // The fixture's social_links has "weibo" and a "custom" array — assert at least one is present
        assertTrue(profile.socialLinks?.custom?.isNotEmpty() == true || profile.socialLinks?.entries?.isNotEmpty() == true)
        // can_request / requires_auth / contact_reputation_level 必须正确映射
        assertNotNull(profile.contactRequest)
        assertEquals(false, profile.contactRequest?.canRequest)
        assertEquals(true, profile.contactRequest?.requiresAuth)
        assertEquals(1, profile.contactReputationLevel)
    }

    @Test fun `privacy settings accept camelCase-only variants`() {
        // 随机推荐.md 第三例 privacy_settings 仅含 camelCase 键
        val data =
            json.decodeFromString(
                UserProfilePrivacySettingsSerializer,
                """{"showEmail": true, "allowContact": false, "contactRequestPolicy": "level"}""",
            )
        assertEquals(true, data.showEmail)
        assertEquals(false, data.allowContact)
        assertEquals("level", data.contactRequestPolicy)
    }

    @Test fun `id lookup flat`() {
        val wrapper = json.decodeFromString<UserIdResponse>(ContractFixture.readFixture("vdsdocs/user/id-lookup.json"))
        assertEquals(18, wrapper.user.id)
        assertEquals("fox_demo", wrapper.user.username)
    }

    @Test fun `like status flat`() {
        val data = json.decodeFromString<LikeStatusResponse>(ContractFixture.readFixture("vdsdocs/user/like-status.json"))
        assertEquals(233L, data.likeCount)
        assertEquals(false, data.isLiked)
        assertEquals(0, data.daysUntilCanLike)
    }

    @Test fun `relationships flat`() {
        val data = json.decodeFromString<UserRelationshipsResponse>(ContractFixture.readFixture("vdsdocs/user/relationships.json"))
        assertEquals(1, data.relationships.size)
        assertEquals("couple", data.relationships[0].relationshipType)
    }

    @Test fun `visitors flat with counters`() {
        val data = json.decodeFromString<UserVisitorsResponse>(ContractFixture.readFixture("vdsdocs/user/visitors.json"))
        assertNotNull(data.visitors)
        assertEquals(1, data.visitors!!.size)
        assertEquals(123456, data.visitors[0].visitId)
        assertEquals(66, data.visitors[0].visitorId)
        assertEquals(1L, data.totalViews)
        assertTrue(data.hasMore == true)
    }

    @Test fun `social badges flat`() {
        val data = json.decodeFromString<SocialBadgesResponse>(ContractFixture.readFixture("vdsdocs/user/social-badges.json"))
        assertEquals(2, data.totalCount)
        assertEquals(1, data.badges.size)
        assertEquals("认证装师", data.badges[0].title)
    }

    @Test fun `social badge detail flat`() {
        val data = json.decodeFromString<SocialBadgeDetailResponse>(ContractFixture.readFixture("vdsdocs/user/social-badge-detail.json"))
        assertEquals("认证装师", data.badge.title)
    }

    @Test fun `store products flat`() {
        val data = json.decodeFromString<StoreProductsResponse>(ContractFixture.readFixture("vdsdocs/user/store-products.json"))
        assertEquals(1, data.products.size)
        assertEquals("尾巴挂件", data.products[0].name)
        assertEquals("129.00", data.products[0].price)
        assertTrue(data.isMerchantVerified == true)
    }
}
