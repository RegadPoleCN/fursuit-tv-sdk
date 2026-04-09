package me.regadpole.furtv.sdk.school

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import me.regadpole.furtv.sdk.model.SchoolSearchParams

/**
 * 学校和角色相关 API
 * 提供学校信息、角色管理等学校和角色相关功能的访问接口
 * @param httpClient 配置好的 HTTP 客户端
 * @param baseUrl API 基础 URL
 */
public class SchoolApi(
    private val httpClient: HttpClient,
    private val baseUrl: String = "https://open-global.vdsentnet.com",
) {
    /**
     * 获取学校详情
     * 获取指定学校的详细信息
     * 端点：GET /api/proxy/furtv/schools/:schoolId
     * @param schoolId 学校 ID
     * @return SchoolDetail 学校详情
     */
    public suspend fun getSchoolDetail(schoolId: String): SchoolDetail {
        val response =
            httpClient.get("$baseUrl/api/proxy/furtv/schools/$schoolId")
                .body<SchoolDetailResponse>()
        return response.data
    }

    /**
     * 搜索学校
     * 根据关键词搜索学校
     * 端点：GET /api/proxy/furtv/schools/search
     * @param params 搜索参数
     * @return SchoolSearchData 搜索结果
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
     * @param query 搜索关键词
     * @param cursor 分页游标
     * @param limit 返回数量限制
     * @return SchoolSearchData 搜索结果
     */
    public suspend fun searchSchools(
        query: String,
        cursor: String? = null,
        limit: Int? = null,
    ): SchoolSearchData {
        return searchSchools(SchoolSearchParams(query, cursor, limit))
    }

    /**
     * 获取用户学校信息
     * 获取指定用户的学校信息
     * 端点：GET /api/proxy/furtv/schools/user/:userId
     * @param userId 用户 ID
     * @return UserSchoolData 用户学校信息
     */
    public suspend fun getUserSchool(userId: String): UserSchoolData {
        val response =
            httpClient.get("$baseUrl/api/proxy/furtv/schools/user/$userId")
                .body<UserSchoolResponse>()
        return response.data
    }

    /**
     * 获取用户角色列表
     * 获取指定用户的角色列表
     * 端点：GET /api/proxy/furtv/characters/user/:username
     * @param username 用户名
     * @return UserCharactersData 角色列表
     */
    public suspend fun getUserCharacters(username: String): UserCharactersData {
        val response =
            httpClient.get("$baseUrl/api/proxy/furtv/characters/user/$username")
                .body<UserCharactersResponse>()
        return response.data
    }
}
