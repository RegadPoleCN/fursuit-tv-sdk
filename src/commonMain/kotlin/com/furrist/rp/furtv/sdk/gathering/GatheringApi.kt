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

package com.furrist.rp.furtv.sdk.gathering

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
 * 聚会相关 API。
 *
 * @param auth 认证管理器（提供 `withFreshToken` 包装 + re-exchange）
 * @param httpClient 配置好的 HTTP 客户端
 * @param baseUrl API 基础 URL
 */
@JvmBlocking
@JvmAsync
@JsExport
@JsName("GatheringApi")
public class GatheringApi internal constructor(
    private val auth: AuthManager,
    private val httpClient: HttpClient,
    private val baseUrl: String = "https://open-global.vdsentnet.com",
) {
    @JsName("getYearStats")
    public suspend fun getYearStats(): GatheringYearStatsResponse =
        auth.withFreshToken {
            httpClient.get("$baseUrl/api/proxy/furtv/gatherings/stats/this-year")
                .body<GatheringYearStatsResponse>()
        }

    @JsName("getMonthly")
    public suspend fun getMonthly(params: GatheringMonthlyParams): GatheringMonthlyData =
        auth.withFreshToken {
            httpClient.get("$baseUrl/api/proxy/furtv/gatherings/monthly") {
                parameter("year", params.year)
                parameter("month", params.month)
            }.body<GatheringMonthlyResponse>().data
        }

    @JsName("getMonthlyDistance")
    public suspend fun getMonthlyDistance(
        params: GatheringMonthlyParams,
        lat: Double,
        lng: Double,
    ): GatheringMonthlyDistanceData =
        auth.withFreshToken {
            httpClient.get("$baseUrl/api/proxy/furtv/gatherings/monthly-distance") {
                parameter("year", params.year)
                parameter("month", params.month)
                // #18：聚会月历距离.md 明示 lat/lng 必填
                parameter("lat", lat)
                parameter("lng", lng)
            }.body<GatheringMonthlyDistanceResponse>().data
        }

    @JsName("getNearby")
    public suspend fun getNearby(): List<GatheringNearbyItem> =
        auth.withFreshToken {
            // #26：聚会附近.md 无查询参数章节，lat/lng/radius 为文档外参数
            httpClient.get("$baseUrl/api/proxy/furtv/gatherings/nearby")
                .body<GatheringNearbyResponse>().data
        }

    @JsName("getNearbyMode")
    public suspend fun getNearbyMode(): GatheringNearbyModeData =
        auth.withFreshToken {
            httpClient.get("$baseUrl/api/proxy/furtv/gatherings/nearby-mode")
                .body<GatheringNearbyModeResponse>().data
        }

    @JsName("getGatheringDetail")
    public suspend fun getGatheringDetail(id: String): GatheringDetailData =
        auth.withFreshToken {
            httpClient.get("$baseUrl/api/proxy/furtv/gatherings/$id")
                .body<GatheringDetailResponse>().gathering
        }

    @JsName("getRegistrations")
    public suspend fun getRegistrations(params: GatheringRegistrationsParams): GatheringRegistrationsResponse =
        auth.withFreshToken {
            // #26：聚会报名列表.md 仅定义路径参数 id，status/cursor/limit 为文档外参数
            httpClient.get("$baseUrl/api/proxy/furtv/gatherings/${params.gatheringId}/registrations")
                .body<GatheringRegistrationsResponse>()
        }
}
