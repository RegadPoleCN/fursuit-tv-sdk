package com.furrist.rp.furtv.sdk.school

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// ==================== 学校搜索 ====================

/**
 * 学校搜索响应
 * 端点：GET /api/proxy/furtv/schools/search
 * 学校搜索接口的响应包装
 */
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
 * 学校搜索数据
 * 包含学校搜索结果
 * @param schools 学校列表
 * @param totalCount 总数量
 * @param cursor 分页游标（可选）
 */
@Serializable
public data class SchoolSearchData(
    @SerialName("schools")
    public val schools: List<SchoolInfo>,
    @SerialName("totalCount")
    public val totalCount: Int,
    @SerialName("cursor")
    public val cursor: String? = null,
)

/**
 * 学校信息
 * 表示一个学校的基本信息
 * @param id 学校 ID
 * @param name 学校名称
 * @param logoUrl 学校 Logo URL
 * @param location 学校位置
 * @param description 学校描述（可选）
 * @param studentCount 学生数量（可选）
 */
@Serializable
public data class SchoolInfo(
    @SerialName("id")
    public val id: String,
    @SerialName("name")
    public val name: String,
    @SerialName("logoUrl")
    public val logoUrl: String? = null,
    @SerialName("location")
    public val location: String? = null,
    @SerialName("description")
    public val description: String? = null,
    @SerialName("studentCount")
    public val studentCount: Int? = null,
)

// ==================== 学校详情 ====================

/**
 * 学校详情响应
 * 端点：GET /api/proxy/furtv/schools/detail
 * 学校详情接口的响应包装
 */
@Serializable
public data class SchoolDetailResponse(
    @SerialName("success")
    public val success: Boolean,
    @SerialName("data")
    public val data: SchoolDetail,
    @SerialName("requestId")
    public val requestId: String,
)

/**
 * 学校详情
 * 包含学校的详细信息
 * @param id 学校 ID
 * @param name 学校名称
 * @param logoUrl 学校 Logo URL
 * @param description 学校描述
 * @param location 学校位置
 * @param lat 纬度（可选）
 * @param lng 经度（可选）
 * @param studentCount 学生数量
 * @param createdAt 创建时间（ISO 8601 格式）
 * @param updatedAt 更新时间（ISO 8601 格式，可选）
 */
@Serializable
public data class SchoolDetail(
    @SerialName("id")
    public val id: String,
    @SerialName("name")
    public val name: String,
    @SerialName("logoUrl")
    public val logoUrl: String? = null,
    @SerialName("description")
    public val description: String? = null,
    @SerialName("location")
    public val location: String? = null,
    @SerialName("lat")
    public val lat: Double? = null,
    @SerialName("lng")
    public val lng: Double? = null,
    @SerialName("studentCount")
    public val studentCount: Int,
    @SerialName("createdAt")
    public val createdAt: String,
    @SerialName("updatedAt")
    public val updatedAt: String? = null,
)

// ==================== 用户学校信息 ====================

/**
 * 用户学校信息响应
 * 端点：GET /api/proxy/furtv/schools/user
 * 用户学校信息接口的响应包装
 */
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
 * 用户学校数据
 * 包含用户的学校信息
 * @param userId 用户 ID
 * @param username 用户名
 * @param schools 用户关联的学校列表
 */
@Serializable
public data class UserSchoolsData(
    @SerialName("userId")
    public val userId: String,
    @SerialName("username")
    public val username: String,
    @SerialName("schools")
    public val schools: List<UserSchoolInfo>,
)

/**
 * 用户学校信息
 * 表示用户与学校的关联信息
 * @param schoolId 学校 ID
 * @param schoolName 学校名称
 * @param logoUrl 学校 Logo URL
 * @param className 班级名称
 * @param graduationYear 毕业年份
 * @param enrollmentYear 入学年份
 * @param isVerified 是否已认证
 */
@Serializable
public data class UserSchoolInfo(
    @SerialName("schoolId")
    public val schoolId: String,
    @SerialName("schoolName")
    public val schoolName: String,
    @SerialName("logoUrl")
    public val logoUrl: String? = null,
    @SerialName("className")
    public val className: String? = null,
    @SerialName("graduationYear")
    public val graduationYear: Int? = null,
    @SerialName("enrollmentYear")
    public val enrollmentYear: Int? = null,
    @SerialName("isVerified")
    public val isVerified: Boolean = false,
)

// ==================== 用户角色列表 ====================

/**
 * 用户角色列表响应
 * 端点：GET /api/proxy/furtv/characters/user
 * 用户角色接口的响应包装
 */
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
 * 用户角色数据
 * 包含用户的角色列表
 * @param userId 用户 ID
 * @param username 用户名
 * @param characters 角色列表
 * @param totalCount 角色总数
 */
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
 * 角色信息
 * 表示一个角色的详细信息
 * @param id 角色 ID
 * @param name 角色名称
 * @param species 物种
 * @param speciesId 物种 ID
 * @param avatarUrl 头像 URL
 * @param description 角色描述
 * @param gender 性别
 * @param isPrimary 是否是主要角色
 * @param createdAt 创建时间（ISO 8601 格式）
 */
@Serializable
public data class CharacterInfo(
    @SerialName("id")
    public val id: String,
    @SerialName("name")
    public val name: String,
    @SerialName("species")
    public val species: String? = null,
    @SerialName("speciesId")
    public val speciesId: String? = null,
    @SerialName("avatarUrl")
    public val avatarUrl: String? = null,
    @SerialName("description")
    public val description: String? = null,
    @SerialName("gender")
    public val gender: String? = null,
    @SerialName("isPrimary")
    public val isPrimary: Boolean = false,
    @SerialName("createdAt")
    public val createdAt: String? = null,
)
