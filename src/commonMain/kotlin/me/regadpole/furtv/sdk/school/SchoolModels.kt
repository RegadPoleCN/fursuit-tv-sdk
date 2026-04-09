package me.regadpole.furtv.sdk.school

import kotlinx.serialization.Serializable

// ==================== 学校 ====================

/**
 * 学校详情响应
 * 学校详情接口的响应包装 */
@Serializable
public data class SchoolDetailResponse(
    public val success: Boolean,
    public val data: SchoolDetail,
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
 * @param studentCount 学生数量
 * @param createdAt 创建时间
 */
@Serializable
public data class SchoolDetail(
    public val id: String,
    public val name: String,
    public val logoUrl: String? = null,
    public val description: String? = null,
    public val location: String? = null,
    public val studentCount: Int,
    public val createdAt: String,
)

/**
 * 学校搜索响应
 * 学校搜索接口的响应包装 */
@Serializable
public data class SchoolSearchResponse(
    public val success: Boolean,
    public val data: SchoolSearchData,
    public val requestId: String,
)

/**
 * 学校搜索数据
 * 包含学校搜索结果
 * @param schools 学校列表
 * @param totalCount 总数量 */
@Serializable
public data class SchoolSearchData(
    public val schools: List<SchoolInfo>,
    public val totalCount: Int,
)

/**
 * 学校信息
 * 表示一个学校的基本信息
 * @param id 学校 ID
 * @param name 学校名称
 * @param logoUrl 学校 Logo URL
 * @param location 学校位置
 */
@Serializable
public data class SchoolInfo(
    public val id: String,
    public val name: String,
    public val logoUrl: String? = null,
    public val location: String? = null,
)

/**
 * 用户学校响应
 * 用户学校信息接口的响应包装 */
@Serializable
public data class UserSchoolResponse(
    public val success: Boolean,
    public val data: UserSchoolData,
    public val requestId: String,
)

/**
 * 用户学校数据
 * 包含用户的学校信息
 * @param userId 用户 ID
 * @param school 学校详情
 * @param className 班级名称
 * @param graduationYear 毕业年份
 * @param enrollmentYear 入学年份
 */
@Serializable
public data class UserSchoolData(
    public val userId: String,
    public val school: SchoolDetail? = null,
    public val className: String? = null,
    public val graduationYear: Int? = null,
    public val enrollmentYear: Int? = null,
)

// ==================== 角色 ====================

/**
 * 用户角色响应
 * 用户角色接口的响应包装 */
@Serializable
public data class UserCharactersResponse(
    public val success: Boolean,
    public val data: UserCharactersData,
    public val requestId: String,
)

/**
 * 用户角色数据
 * 包含用户的角色列表
 * @param username 用户名
 * @param characters 角色列表
 */
@Serializable
public data class UserCharactersData(
    public val username: String,
    public val characters: List<CharacterInfo>,
)

/**
 * 角色信息
 * 表示一个角色的信息
 * @param id 角色 ID
 * @param name 角色名称
 * @param species 物种
 * @param avatarUrl 头像 URL
 * @param description 角色描述
 * @param isPrimary 是否是主要角色
 */
@Serializable
public data class CharacterInfo(
    public val id: String,
    public val name: String,
    public val species: String? = null,
    public val avatarUrl: String? = null,
    public val description: String? = null,
    public val isPrimary: Boolean = false,
)
