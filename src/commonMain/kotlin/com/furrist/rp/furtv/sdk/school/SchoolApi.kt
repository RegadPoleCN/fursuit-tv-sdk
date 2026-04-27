package com.furrist.rp.furtv.sdk.school

import com.furrist.rp.furtv.sdk.model.SchoolSearchParams
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import kotlin.js.JsExport
import kotlin.js.JsName

/**
 * 学校和角色相关 API。
 *
 * 提供学校搜索、学校详情、用户关联学校以及用户角色列表的访问接口。
 * 所有方法均通过 HTTP GET 请求获取数据，返回对应的学校/角色数据模型。
 *
 * ## 主要功能
 * - 学校搜索（searchSchools）：按关键词搜索学校，支持分页
 * - 学校详情（getSchoolDetail）：获取单个学校的完整资料
 * - 用户学校（getUserSchools）：查询指定用户关联的学校信息
 * - 用户角色（getUserCharacters）：查询指定用户的角色列表
 *
 * ## 分页说明
 * - searchSchools() 方法使用 **cursor-based 分页**：通过 cursor 游标翻页
 * - 返回的 SchoolSearchData 包含 nextCursor 用于获取下一页
 * - 其他方法返回完整结果集（无分页）
 *
 * @param httpClient 配置好的 HTTP 客户端
 * @param baseUrl API 基础 URL，默认为 https://open-global.vdsentnet.com
 * @see SchoolModels 学校/角色数据模型定义
 * @see FursuitTvSdkException 异常层次结构
 */
@JsExport
@JsName("SchoolApi")
public class SchoolApi internal constructor(
    private val httpClient: HttpClient,
    private val baseUrl: String = "https://open-global.vdsentnet.com",
) {
    /**
     * 按关键词搜索学校。
     *
     * 支持按学校名称、地点等关键词进行模糊搜索，使用 cursor-based 分页。
     * 返回匹配的学校列表和分页信息。
     *
     * @param params 搜索参数对象，包含 query/cursor/limit 字段
     * @return 学校搜索结果数据对象（SchoolSearchData），包含 schools 列表和 nextCursor
     * @throws NetworkException 网络连接失败或超时
     * @throws TokenExpiredException 访问令牌已过期或无效
     * @throws AuthenticationException 认证凭证缺失或无效
     * @throws ValidationException 参数验证失败（如 query 为空）
     * @throws ApiException 服务器返回业务错误(4xx/5xx)
     */
    @JsName("searchSchoolsWithParams")
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
     * 按关键词搜索学校（重载方法，保持向后兼容）。
     *
     * 便捷方法：直接传入参数而非使用 SchoolSearchParams 对象。
     * 使用 cursor-based 分页，通过 cursor 获取下一页结果。
     *
     * @param query 搜索关键词（支持模糊匹配，建议长度 1-50 字符）
     * @param cursor 分页游标（从上一次搜索结果的 nextCursor 获取），null 表示首页
     * @param limit 返回数量限制（建议 1-100），null 表示使用服务端默认值
     * @return 学校搜索结果数据对象（SchoolSearchData），包含 schools 列表和 nextCursor
     * @throws NetworkException 网络连接失败或超时
     * @throws TokenExpiredException 访问令牌已过期或无效
     * @throws AuthenticationException 认证凭证缺失或无效
     */
    public suspend fun searchSchools(
        query: String,
        cursor: String? = null,
        limit: Int? = null,
    ): SchoolSearchData {
        return searchSchools(SchoolSearchParams(query, cursor, limit))
    }

    /**
     * 获取学校详情。
     *
     * 根据学校 ID 返回该学校的完整信息，包括名称、地点、描述等。
     * 可用于学校详情页面展示。
     *
     * @param schoolId 学校唯一标识符（UUID 格式）
     * @return 学校详情对象（SchoolDetail），包含学校的完整信息
     * @throws NetworkException 网络连接失败或超时
     * @throws TokenExpiredException 访问令牌已过期或无效
     * @throws AuthenticationException 认证凭证缺失或无效
     * @throws NotFoundException 指定 schoolId 的学校不存在
     */
    public suspend fun getSchoolDetail(schoolId: String): SchoolDetail {
        val response =
            httpClient.get("$baseUrl/api/proxy/furtv/schools/$schoolId")
                .body<SchoolDetailResponse>()
        return response.data
    }

    /**
     * 获取用户的学校关联信息。
     *
     * 查询指定用户关联的所有学校列表，包括学校 ID 和关联详情。
     * 可用于展示用户的学校背景、教育经历等场景。
     *
     * @param userId 用户唯一标识符（UUID 格式）
     * @return 用户学校信息对象（UserSchoolsData），包含 userId、username 和 schools 列表
     * @throws NetworkException 网络连接失败或超时
     * @throws TokenExpiredException 访问令牌已过期或无效
     * @throws AuthenticationException 认证凭证缺失或无效
     * @throws NotFoundException 指定 userId 的用户不存在
     */
    public suspend fun getUserSchools(userId: String): UserSchoolsData {
        val response =
            httpClient.get("$baseUrl/api/proxy/furtv/schools/user/$userId")
                .body<UserSchoolsResponse>()
        return response.data
    }

    /**
     * 获取用户的角色列表。
     *
     * 查询指定用户名下的所有角色（OC/Fursona）信息，包括角色名称、物种、描述等。
     * 可用于展示用户的角色集合、个人主页等场景。
     *
     * @param username 用户名（非 UUID，是用户的显示名称）
     * @return 用户角色数据对象（UserCharactersData），包含 userId、username 和 characters 列表
     * @throws NetworkException 网络连接失败或超时
     * @throws TokenExpiredException 访问令牌已过期或无效
     * @throws AuthenticationException 认证凭证缺失或无效
     * @throws NotFoundException 指定 username 的用户不存在或无角色数据
     */
    public suspend fun getUserCharacters(username: String): UserCharactersData {
        val response =
            httpClient.get("$baseUrl/api/proxy/furtv/characters/user/$username")
                .body<UserCharactersResponse>()
        return response.data
    }
}
