package com.furrist.rp.furtv.sdk.school

import com.furrist.rp.furtv.sdk.auth.AuthManager
import com.furrist.rp.furtv.sdk.model.*
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import kotlin.js.JsExport
import kotlin.js.JsName
import love.forte.plugin.suspendtrans.annotation.JvmAsync
import love.forte.plugin.suspendtrans.annotation.JvmBlocking

/**
 * 学校和角色相关 API。
 *
 * @param auth 认证管理器（提供 `withFreshToken` 包装 + re-exchange）
 * @param httpClient 配置好的 HTTP 客户端
 * @param baseUrl API 基础 URL
 */
@JvmBlocking
@JvmAsync
@JsExport
@JsName("SchoolApi")
public class SchoolApi internal constructor(
    private val auth: AuthManager,
    private val httpClient: HttpClient,
    private val baseUrl: String = "https://open-global.vdsentnet.com",
) {
    @JsName("searchSchools")
    public suspend fun searchSchools(params: SchoolSearchParams): SchoolSearchResponse =
        auth.withFreshToken {
            httpClient.get("$baseUrl/api/proxy/furtv/schools/search") {
                parameter("query", params.query)
                params.cursor?.let { parameter("cursor", it) }
                params.limit?.let { parameter("limit", it) }
            }.body<SchoolSearchResponse>()
        }

    @JsName("getSchoolDetail")
    public suspend fun getSchoolDetail(schoolId: String): SchoolDetail =
        auth.withFreshToken {
            httpClient.get("$baseUrl/api/proxy/furtv/schools/$schoolId")
                .body<SchoolDetailResponse>().school
        }

    @JsName("getUserSchools")
    public suspend fun getUserSchools(userId: String): UserSchoolsResponse =
        auth.withFreshToken {
            httpClient.get("$baseUrl/api/proxy/furtv/schools/user/$userId")
                .body<UserSchoolsResponse>()
        }

    @JsName("getUserCharacters")
    public suspend fun getUserCharacters(username: String): UserCharactersResponse =
        auth.withFreshToken {
            httpClient.get("$baseUrl/api/proxy/furtv/characters/user/$username")
                .body<UserCharactersResponse>()
        }
}
