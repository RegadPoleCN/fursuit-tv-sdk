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
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** 学校搜索响应。 */
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

/** 学校详情响应。 */
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

/** 用户学校信息响应。 */
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

/** 用户角色列表响应。 */
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

/** 学校搜索参数。 */
@JsExport
@JsName("SchoolSearchParams")
@Serializable
public data class SchoolSearchParams(
    public val query: String,
)
