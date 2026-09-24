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
@Suppress("TooManyFunctions")
@JsExport
@JsName("GatheringApi")
public class GatheringApi internal constructor(
    private val auth: AuthManager,
    private val httpClient: HttpClient,
    private val baseUrl: String = "https://open-global.vdsentnet.com",
) {
    /** 获取本年度聚会统计数据（聚会年度统计.md）。 */
    @JsName("getYearStats")
    public suspend fun getYearStats(): GatheringYearStatsResponse =
        auth.withFreshToken {
            httpClient.get("$baseUrl/api/proxy/furtv/gatherings/stats/this-year")
                .body<GatheringYearStatsResponse>()
        }

    /**
     * 获取指定月份的聚会月历（聚会月历.md）。
     *
     * @param year 年份（如 2026）
     * @param month 月份（1-12）
     * @return 聚会月历响应
     */
    @JsName("getMonthly")
    public suspend fun getMonthly(year: Int, month: Int): GatheringMonthlyResponse =
        auth.withFreshToken {
            httpClient.get("$baseUrl/api/proxy/furtv/gatherings/monthly") {
                parameter("year", year)
                parameter("month", month)
            }.body<GatheringMonthlyResponse>()
        }

    /**
     * [DEPRECATED in v0.5.0] 旧版使用 [GatheringMonthlyParams] 包装的入口，推荐直接使用展开参数版本 [getMonthly]。
     */
    @Deprecated(
        message = "Use flat parameters getMonthly(year, month) instead.",
        replaceWith = ReplaceWith("getMonthly(params.year, params.month)"),
    )
    @JsName("getMonthlyWithParams")
    public suspend fun getMonthly(params: GatheringMonthlyParams): GatheringMonthlyResponse =
        getMonthly(params.year, params.month)

    /**
     * 获取指定月份的聚会月历（按距离排序）（聚会月历距离.md）。
     *
     * @param year 年份（如 2026）
     * @param month 月份（1-12）
     * @param lat 纬度（必填）
     * @param lng 经度（必填）
     * @return 聚会月历距离响应
     */
    @JsName("getMonthlyDistance")
    public suspend fun getMonthlyDistance(
        year: Int,
        month: Int,
        lat: Double,
        lng: Double,
    ): GatheringMonthlyDistanceResponse =
        auth.withFreshToken {
            httpClient.get("$baseUrl/api/proxy/furtv/gatherings/monthly-distance") {
                parameter("year", year)
                parameter("month", month)
                parameter("lat", lat)
                parameter("lng", lng)
            }.body<GatheringMonthlyDistanceResponse>()
        }

    /**
     * [DEPRECATED in v0.5.0] 旧版半对象半扁平参数的入口，推荐直接使用全扁平参数版本 [getMonthlyDistance]。
     */
    @Deprecated(
        message = "Use flat parameters getMonthlyDistance(year, month, lat, lng) instead.",
        replaceWith = ReplaceWith("getMonthlyDistance(params.year, params.month, lat, lng)"),
    )
    @JsName("getMonthlyDistanceWithParams")
    public suspend fun getMonthlyDistance(
        params: GatheringMonthlyParams,
        lat: Double,
        lng: Double,
    ): GatheringMonthlyDistanceResponse =
        getMonthlyDistance(params.year, params.month, lat, lng)

    /** 获取附近聚会列表（聚会附近.md，无查询参数）。 */
    @JsName("getNearby")
    public suspend fun getNearby(): GatheringNearbyResponse =
        auth.withFreshToken {
            httpClient.get("$baseUrl/api/proxy/furtv/gatherings/nearby")
                .body<GatheringNearbyResponse>()
        }

    /** 获取附近模式聚会列表（聚会附近模式.md，返回带 avatar_url/参与人数的增强元素）。 */
    @JsName("getNearbyMode")
    public suspend fun getNearbyMode(): GatheringNearbyModeResponse =
        auth.withFreshToken {
            httpClient.get("$baseUrl/api/proxy/furtv/gatherings/nearby-mode")
                .body<GatheringNearbyModeResponse>()
        }

    /** 获取聚会详情（聚会详情.md）。 */
    @JsName("getGatheringDetail")
    public suspend fun getGatheringDetail(id: String): GatheringDetailResponse =
        auth.withFreshToken {
            httpClient.get("$baseUrl/api/proxy/furtv/gatherings/$id")
                .body<GatheringDetailResponse>()
        }

    /**
     * 获取聚会报名列表（聚会报名列表.md）。
     *
     * @param gatheringId 聚会 ID
     * @return 聚会报名列表响应
     */
    @JsName("getRegistrations")
    public suspend fun getRegistrations(gatheringId: String): GatheringRegistrationsResponse =
        auth.withFreshToken {
            httpClient.get("$baseUrl/api/proxy/furtv/gatherings/$gatheringId/registrations")
                .body<GatheringRegistrationsResponse>()
        }

    /**
     * [DEPRECATED in v0.5.0] 旧版使用 [GatheringRegistrationsParams] 包装的入口，推荐直接使用展开参数版本 [getRegistrations]。
     */
    @Deprecated(
        message = "Use flat parameter getRegistrations(gatheringId) instead.",
        replaceWith = ReplaceWith("getRegistrations(params.gatheringId)"),
    )
    @JsName("getRegistrationsWithParams")
    public suspend fun getRegistrations(params: GatheringRegistrationsParams): GatheringRegistrationsResponse =
        getRegistrations(params.gatheringId)
}
