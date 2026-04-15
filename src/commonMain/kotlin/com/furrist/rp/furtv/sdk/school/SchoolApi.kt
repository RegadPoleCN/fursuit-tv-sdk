package com.furrist.rp.furtv.sdk.school

import com.furrist.rp.furtv.sdk.model.SchoolSearchParams
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter

/**
 * 学校和角色相关 API
 * 提供学校信息、角色管理等学校和角色相关功能的访问接口
 *
 * 参考官方文档：
 * - [学校搜索](../../../../vds-docs/Fursuit.TV 兽频道/学校与角色 - 学校/学校搜索（furtv.schools.search）.md)
 * - [学校详情](../../../../vds-docs/Fursuit.TV 兽频道/学校与角色 - 学校/学校详情（furtv.schools.detail）.md)
 * - [用户学校信息](../../../../vds-docs/Fursuit.TV 兽频道/学校与角色 - 学校/用户学校信息（furtv.schools.user）.md)
 * - [用户角色列表](../../../../vds-docs/Fursuit.TV 兽频道/学校与角色 - 角色/用户角色列表（furtv.characters.user）.md)
 *
 * @param httpClient 配置好的 HTTP 客户端
 * @param baseUrl API 基础 URL，默认为 https://open-global.vdsentnet.com
 */
public class SchoolApi(
    private val httpClient: HttpClient,
    private val baseUrl: String = "https://open-global.vdsentnet.com",
) {
    /**
     * 搜索学校
     * 根据关键词搜索学校，支持分页
     *
     * 端点：`GET /api/proxy/furtv/schools/search`
     *
     * 官方文档：[学校搜索](../../../../vds-docs/Fursuit.TV 兽频道/学校与角色 - 学校/学校搜索（furtv.schools.search）.md)
     *
     * @param params 搜索参数，包含关键词、分页游标和返回数量限制
     * @return SchoolSearchData 学校搜索结果，包含学校列表和总数
     * @throws Exception 当请求失败时抛出异常
     *
     * @see SchoolSearchParams
     * @see SchoolSearchData
     */
    public suspend fun searchSchools(params: SchoolSearchParams): SchoolSearchData {
        val response =
            httpClient.get("$baseUrl/api/proxy/furtv/schools/search") {
                parameter("query", params.query)
                params.cursor?.let { parameter("cursor", it) }
                params.limit?.let { parameter("limit", it) }
            }.body<SchoolSearchResponse>()
        return response.data
    }

    /**
     * 搜索学校（重载方法，保持向后兼容）
     * 根据关键词搜索学校，支持分页
     *
     * 端点：GET /api/proxy/furtv/schools/search
     *
     * @param query 搜索关键词
     * @param cursor 分页游标（可选）
     * @param limit 返回数量限制（可选）
     * @return SchoolSearchData 学校搜索结果，包含学校列表和总数
     * @throws Exception 当请求失败时抛出异常
     *
     * @see SchoolSearchData
     */
    public suspend fun searchSchools(
        query: String,
        cursor: String? = null,
        limit: Int? = null,
    ): SchoolSearchData {
        return searchSchools(SchoolSearchParams(query, cursor, limit))
    }

    /**
     * 获取学校详情
     * 根据学校 ID 获取学校的详细信息
     *
     * 端点：`GET /api/proxy/furtv/schools/:schoolId`
     *
     * 官方文档：[学校详情](../../../../vds-docs/Fursuit.TV 兽频道/学校与角色 - 学校/学校详情（furtv.schools.detail）.md)
     *
     * @param schoolId 学校 ID
     * @return SchoolDetail 学校详情，包含学校的完整信息
     * @throws Exception 当请求失败时抛出异常
     *
     * @see SchoolDetail
     */
    public suspend fun getSchoolDetail(schoolId: String): SchoolDetail {
        val response =
            httpClient.get("$baseUrl/api/proxy/furtv/schools/$schoolId")
                .body<SchoolDetailResponse>()
        return response.data
    }

    /**
     * 获取用户学校信息
     * 获取指定用户的学校信息列表，包括用户关联的所有学校及其相关信息
     *
     * 端点：`GET /api/proxy/furtv/schools/user/:userId`
     *
     * 官方文档：[用户学校信息](../../../../vds-docs/Fursuit.TV 兽频道/学校与角色 - 学校/用户学校信息（furtv.schools.user）.md)
     *
     * @param userId 用户 ID
     * @return UserSchoolsData 用户学校信息，包含用户 ID、用户名和学校列表
     * @throws Exception 当请求失败时抛出异常
     *
     * @see UserSchoolsData
     */
    public suspend fun getUserSchools(userId: String): UserSchoolsData {
        val response =
            httpClient.get("$baseUrl/api/proxy/furtv/schools/user/$userId")
                .body<UserSchoolsResponse>()
        return response.data
    }

    /**
     * 获取用户角色列表
     * 获取指定用户的角色列表，包括所有角色的详细信息
     *
     * 端点：`GET /api/proxy/furtv/characters/user/:username`
     *
     * 官方文档：[用户角色列表](../../../../vds-docs/Fursuit.TV 兽频道/学校与角色 - 角色/用户角色列表（furtv.characters.user）.md)
     *
     * @param username 用户名
     * @return UserCharactersData 用户角色数据，包含用户 ID、用户名和角色列表
     * @throws Exception 当请求失败时抛出异常
     *
     * @see UserCharactersData
     * @see CharacterInfo
     */
    public suspend fun getUserCharacters(username: String): UserCharactersData {
        val response =
            httpClient.get("$baseUrl/api/proxy/furtv/characters/user/$username")
                .body<UserCharactersResponse>()
        return response.data
    }
}
