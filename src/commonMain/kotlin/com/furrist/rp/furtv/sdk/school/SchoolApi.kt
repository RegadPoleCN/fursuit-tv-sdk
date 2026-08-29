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
            // #26：学校搜索.md 查询参数仅 query，cursor/limit 为文档外参数
            httpClient.get("$baseUrl/api/proxy/furtv/schools/search") {
                parameter("query", params.query)
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
