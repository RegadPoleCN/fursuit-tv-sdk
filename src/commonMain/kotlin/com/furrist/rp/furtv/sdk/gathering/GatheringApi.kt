package com.furrist.rp.furtv.sdk.gathering

import com.furrist.rp.furtv.sdk.auth.AuthManager
import com.furrist.rp.furtv.sdk.model.DataSource
import com.furrist.rp.furtv.sdk.model.GatheringBadge
import com.furrist.rp.furtv.sdk.model.GatheringDetailData
import com.furrist.rp.furtv.sdk.model.GatheringDetailResponse
import com.furrist.rp.furtv.sdk.model.GatheringMonthlyData
import com.furrist.rp.furtv.sdk.model.GatheringMonthlyDistanceData
import com.furrist.rp.furtv.sdk.model.GatheringMonthlyDistanceResponse
import com.furrist.rp.furtv.sdk.model.GatheringMonthlyDistanceItem
import com.furrist.rp.furtv.sdk.model.GatheringMonthlyParams
import com.furrist.rp.furtv.sdk.model.GatheringMonthlyResponse
import com.furrist.rp.furtv.sdk.model.GatheringNearbyItem
import com.furrist.rp.furtv.sdk.model.GatheringNearbyModeData
import com.furrist.rp.furtv.sdk.model.GatheringNearbyModeResponse
import com.furrist.rp.furtv.sdk.model.GatheringNearbyParams
import com.furrist.rp.furtv.sdk.model.GatheringNearbyResponse
import com.furrist.rp.furtv.sdk.model.GatheringRegistrationsParams
import com.furrist.rp.furtv.sdk.model.GatheringRegistrationsResponse
import com.furrist.rp.furtv.sdk.model.GatheringYearStatsResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
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
    public suspend fun getMonthlyDistance(params: GatheringMonthlyParams): GatheringMonthlyDistanceData =
        auth.withFreshToken {
            httpClient.get("$baseUrl/api/proxy/furtv/gatherings/monthly-distance") {
                parameter("year", params.year)
                parameter("month", params.month)
                params.lat?.let { parameter("lat", it) }
                params.lng?.let { parameter("lng", it) }
            }.body<GatheringMonthlyDistanceResponse>().data
        }

    @JsName("getNearby")
    public suspend fun getNearby(params: GatheringNearbyParams): List<GatheringNearbyItem> =
        auth.withFreshToken {
            httpClient.get("$baseUrl/api/proxy/furtv/gatherings/nearby") {
                params.lat?.let { parameter("lat", it) }
                params.lng?.let { parameter("lng", it) }
                params.radius?.let { parameter("radius", it) }
            }.body<GatheringNearbyResponse>().data
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
            httpClient.get("$baseUrl/api/proxy/furtv/gatherings/${params.gatheringId}/registrations") {
                params.status?.let { parameter("status", it) }
                params.cursor?.let { parameter("cursor", it) }
                params.limit?.let { parameter("limit", it) }
            }.body<GatheringRegistrationsResponse>()
        }
}