package com.furrist.rp.furtv.sdk.school

import kotlin.js.JsExport
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * 学校搜索响应。
 *
 * 端点：GET /api/proxy/furtv/schools/search
 *
 * @property success 请求是否成功
 * @property data 搜索结果数据
 * @property requestId 请求唯一标识
 */
@JsExport
@Serializable
public data class SchoolSearchResponse(
    @SerialName("success")
    public val success: Boolean,
    @SerialName("data")
    public val data: SchoolSearchData,
    @SerialName("requestId")
    public val requestId: String,
)

/**
 * 学校搜索结果数据。
 *
 * @property schools 匹配的学校列表
 */
@JsExport
@Serializable
public data class SchoolSearchData(
    @SerialName("schools")
    public val schools: List<SchoolInfo>,
)

/**
 * 学校基本信息。
 *
 * @property id 学校 ID
 * @property name 学校全称
 * @property shortName 学校简称
 * @property location 学校位置
 * @property type 学校类型
 * @property logoUrl 学校 Logo URL
 * @property studentCount 学生数量
 */
@JsExport
@Serializable
public data class SchoolInfo(
    @SerialName("id")
    public val id: Int,
    @SerialName("name")
    public val name: String,
    @SerialName("short_name")
    public val shortName: String? = null,
    @SerialName("location")
    public val location: String? = null,
    @SerialName("type")
    public val type: String? = null,
    @SerialName("logo_url")
    public val logoUrl: String? = null,
    @SerialName("student_count")
    public val studentCount: Int? = null,
)

/**
 * 学校详情响应。
 *
 * 端点：GET /api/proxy/furtv/schools/{schoolId}
 *
 * @property success 请求是否成功
 * @property school 学校详情
 * @property requestId 请求唯一标识
 */
@JsExport
@Serializable
public data class SchoolDetailResponse(
    @SerialName("success")
    public val success: Boolean,
    @SerialName("school")
    public val school: SchoolDetail,
    @SerialName("requestId")
    public val requestId: String,
)

/**
 * 学校详情。
 *
 * @property id 学校 ID
 * @property name 学校全称
 * @property shortName 学校简称
 * @property location 学校位置
 * @property type 学校类型
 * @property logoUrl 学校 Logo URL
 * @property studentCount 学生数量
 */
@JsExport
@Serializable
public data class SchoolDetail(
    @SerialName("id")
    public val id: Int,
    @SerialName("name")
    public val name: String,
    @SerialName("short_name")
    public val shortName: String? = null,
    @SerialName("location")
    public val location: String? = null,
    @SerialName("type")
    public val type: String? = null,
    @SerialName("logo_url")
    public val logoUrl: String? = null,
    @SerialName("student_count")
    public val studentCount: Int? = null,
)

/**
 * 用户学校信息响应。
 *
 * 端点：GET /api/proxy/furtv/schools/user/{userId}
 *
 * @property success 请求是否成功
 * @property data 用户学校数据
 * @property requestId 请求唯一标识
 */
@JsExport
@Serializable
public data class UserSchoolsResponse(
    @SerialName("success")
    public val success: Boolean,
    @SerialName("data")
    public val data: UserSchoolsData,
    @SerialName("requestId")
    public val requestId: String,
)

/**
 * 用户学校数据。
 *
 * @property schools 用户关联的学校列表
 */
@JsExport
@Serializable
public data class UserSchoolsData(
    @SerialName("schools")
    public val schools: List<UserSchoolInfo>,
)

/**
 * 用户学校关联信息。
 *
 * @property userSchoolId 用户学校关联 ID
 * @property className 班级名称
 * @property enrollmentYear 入学年份
 * @property graduationYear 毕业年份
 * @property isCurrent 是否在读（1=在读）
 * @property isPublic 是否公开（1=公开）
 * @property schoolId 学校 ID
 * @property schoolName 学校全称
 * @property shortName 学校简称
 * @property location 学校位置
 * @property type 学校类型
 * @property logoUrl 学校 Logo URL
 * @property studentCount 学生数量
 */
@JsExport
@Serializable
public data class UserSchoolInfo(
    @SerialName("user_school_id")
    public val userSchoolId: Int? = null,
    @SerialName("class_name")
    public val className: String? = null,
    @SerialName("enrollment_year")
    public val enrollmentYear: Int? = null,
    @SerialName("graduation_year")
    public val graduationYear: Int? = null,
    @SerialName("is_current")
    public val isCurrent: Int? = null,
    @SerialName("is_public")
    public val isPublic: Int? = null,
    @SerialName("school_id")
    public val schoolId: Int,
    @SerialName("school_name")
    public val schoolName: String,
    @SerialName("short_name")
    public val shortName: String? = null,
    @SerialName("location")
    public val location: String? = null,
    @SerialName("type")
    public val type: String? = null,
    @SerialName("logo_url")
    public val logoUrl: String? = null,
    @SerialName("student_count")
    public val studentCount: Int? = null,
)

/**
 * 用户角色列表响应。
 *
 * 端点：GET /api/proxy/furtv/characters/user/{username}
 *
 * @property success 请求是否成功
 * @property data 用户角色数据
 * @property requestId 请求唯一标识
 */
@JsExport
@Serializable
public data class UserCharactersResponse(
    @SerialName("success")
    public val success: Boolean,
    @SerialName("data")
    public val data: UserCharactersData,
    @SerialName("requestId")
    public val requestId: String,
)

/**
 * 用户角色数据。
 *
 * @property userId 用户 ID
 * @property username 用户名
 * @property characters 角色列表
 * @property totalCount 角色总数
 */
@JsExport
@Serializable
public data class UserCharactersData(
    @SerialName("userId")
    public val userId: String,
    @SerialName("username")
    public val username: String,
    @SerialName("characters")
    public val characters: List<CharacterInfo>,
    @SerialName("totalCount")
    public val totalCount: Int,
)

/**
 * 角色信息。
 *
 * @property id 角色 ID
 * @property name 角色名称
 * @property species 物种
 * @property gender 性别
 * @property worldview 世界观
 */
@JsExport
@Serializable
public data class CharacterInfo(
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
