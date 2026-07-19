package com.furrist.rp.furtv.sdk.school

import com.furrist.rp.furtv.sdk.model.SchoolDetail
import com.furrist.rp.furtv.sdk.model.SchoolDetailResponse
import com.furrist.rp.furtv.sdk.model.SchoolInfo
import com.furrist.rp.furtv.sdk.model.SchoolSearchData
import com.furrist.rp.furtv.sdk.model.SchoolSearchParams
import com.furrist.rp.furtv.sdk.model.SchoolSearchResponse
import com.furrist.rp.furtv.sdk.model.UserCharactersData
import com.furrist.rp.furtv.sdk.model.UserCharactersResponse
import com.furrist.rp.furtv.sdk.model.UserSchoolInfo
import com.furrist.rp.furtv.sdk.model.UserSchoolsData
import com.furrist.rp.furtv.sdk.model.UserSchoolsResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import kotlin.js.JsExport
import kotlin.js.JsName
import love.forte.plugin.suspendtrans.annotation.JvmAsync
import love.forte.plugin.suspendtrans.annotation.JvmBlocking

/**
 * 学校和角色相关 API。
 *
 * 提供学校搜索、学校详情、用户关联学校以及用户角色列表的访问接口。
 *
 * @param httpClient 配置好的 HTTP 客户端
 * @param baseUrl API 基础 URL
 */
@JvmBlocking
@JvmAsync
@JsExport
@JsName("SchoolApi")
public class SchoolApi internal constructor(
    private val httpClient: HttpClient,
    private val baseUrl: String = "https://open-global.vdsentnet.com",
) {
    /**
     * 按关键词搜索学校（参数对象版本，唯一公开签名）。
     *
     * @param params 搜索参数对象
     * @return 学校搜索结果数据对象
     * @throws NetworkException 网络连接失败或超时
     */
    @JsName("searchSchools")
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
     * 获取学校详情。
     *
     * @param schoolId 学校唯一标识符
     * @return 学校详情对象
     * @throws NetworkException 网络连接失败或超时
     * @throws NotFoundException 指定 schoolId 的学校不存在
     */
    @JsName("getSchoolDetail")
    public suspend fun getSchoolDetail(schoolId: String): SchoolDetail {
        val response =
            httpClient.get("$baseUrl/api/proxy/furtv/schools/$schoolId")
                .body<SchoolDetailResponse>()
        return response.school
    }

    /**
     * 获取用户的学校关联信息。
     *
     * @param userId 用户唯一标识符
     * @return 用户学校信息对象
     * @throws NetworkException 网络连接失败或超时
     * @throws NotFoundException 指定 userId 的用户不存在
     */
    @JsName("getUserSchools")
    public suspend fun getUserSchools(userId: String): UserSchoolsData {
        val response =
            httpClient.get("$baseUrl/api/proxy/furtv/schools/user/$userId")
                .body<UserSchoolsResponse>()
        return response.data
    }

    /**
     * 获取用户的角色列表。
     *
     * @param username 用户名
     * @return 用户角色数据对象
     * @throws NetworkException 网络连接失败或超时
     * @throws NotFoundException 指定 username 的用户不存在
     */
    @JsName("getUserCharacters")
    public suspend fun getUserCharacters(username: String): UserCharactersData {
        val response =
            httpClient.get("$baseUrl/api/proxy/furtv/characters/user/$username")
                .body<UserCharactersResponse>()
        return response.data
    }
}
