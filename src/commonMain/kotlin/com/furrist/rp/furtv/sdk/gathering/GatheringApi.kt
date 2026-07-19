package com.furrist.rp.furtv.sdk.gathering

import com.furrist.rp.furtv.sdk.model.DataSource
import com.furrist.rp.furtv.sdk.model.GatheringBadge
import com.furrist.rp.furtv.sdk.model.GatheringDetailData
import com.furrist.rp.furtv.sdk.model.GatheringDetailResponse
import com.furrist.rp.furtv.sdk.model.GatheringMonthlyDistanceItem
import com.furrist.rp.furtv.sdk.model.GatheringMonthlyDistanceResponse
import com.furrist.rp.furtv.sdk.model.GatheringMonthlyItem
import com.furrist.rp.furtv.sdk.model.GatheringMonthlyParams
import com.furrist.rp.furtv.sdk.model.GatheringMonthlyResponse
import com.furrist.rp.furtv.sdk.model.GatheringNearbyItem
import com.furrist.rp.furtv.sdk.model.GatheringNearbyModeData
import com.furrist.rp.furtv.sdk.model.GatheringNearbyModeResponse
import com.furrist.rp.furtv.sdk.model.GatheringNearbyParams
import com.furrist.rp.furtv.sdk.model.GatheringNearbyResponse
import com.furrist.rp.furtv.sdk.model.GatheringRegistrationsData
import com.furrist.rp.furtv.sdk.model.GatheringRegistrationsParams
import com.furrist.rp.furtv.sdk.model.GatheringRegistrationsResponse
import com.furrist.rp.furtv.sdk.model.GatheringYearStatsData
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
 * 提供聚会列表、统计、详情、报名、附近搜索等聚会相关功能的访问接口。
 *
 * @param httpClient 配置好的 HTTP 客户端
 * @param baseUrl API 基础 URL
 */
@JvmBlocking
@JvmAsync
@JsExport
@JsName("GatheringApi")
public class GatheringApi internal constructor(
    private val httpClient: HttpClient,
    private val baseUrl: String = "https://open-global.vdsentnet.com",
) {
    /**
     * 获取当前年度聚会统计信息。
     *
     * @return 聚会年度统计数据对象
     * @throws NetworkException 网络连接失败或超时
     * @throws AuthenticationException 认证凭证缺失或无效
     */
    @JsName("getYearStats")
    public suspend fun getYearStats(): GatheringYearStatsData {
        val response =
            httpClient.get("$baseUrl/api/proxy/furtv/gatherings/stats/this-year")
                .body<GatheringYearStatsResponse>()
        return response.data
    }

    /**
     * 获取指定月份的聚会列表（参数对象版本）。
     *
     * @param params 聚会月历参数对象（年份、月份、可选距离参考点）
     * @return 聚会列表
     * @throws NetworkException 网络连接失败或超时
     */
    @JsName("getMonthly")
    public suspend fun getMonthly(params: GatheringMonthlyParams): List<GatheringMonthlyItem> {
        val response =
            httpClient.get("$baseUrl/api/proxy/furtv/gatherings/monthly") {
                parameter("year", params.year)
                parameter("month", params.month)
            }.body<GatheringMonthlyResponse>()
        return response.data
    }

    /**
     * 获取指定月份的聚会列表（带距离信息，参数对象版本）。
     *
     * @param params 聚会月历参数对象
     * @return 带距离的聚会列表
     * @throws NetworkException 网络连接失败或超时
     */
    @JsName("getMonthlyDistance")
    public suspend fun getMonthlyDistance(params: GatheringMonthlyParams): List<GatheringMonthlyDistanceItem> {
        val response =
            httpClient.get("$baseUrl/api/proxy/furtv/gatherings/monthly-distance") {
                parameter("year", params.year)
                parameter("month", params.month)
                params.lat?.let { parameter("lat", it) }
                params.lng?.let { parameter("lng", it) }
            }.body<GatheringMonthlyDistanceResponse>()
        return response.data
    }

    /**
     * 获取附近的聚会列表（参数对象版本）。
     *
     * @param params 附近聚会参数对象（中心点纬度经度 + 半径）
     * @return 附近聚会列表
     * @throws NetworkException 网络连接失败或超时
     */
    @JsName("getNearby")
    public suspend fun getNearby(params: GatheringNearbyParams): List<GatheringNearbyItem> {
        val response =
            httpClient.get("$baseUrl/api/proxy/furtv/gatherings/nearby") {
                params.lat?.let { parameter("lat", it) }
                params.lng?.let { parameter("lng", it) }
                params.radius?.let { parameter("radius", it) }
            }.body<GatheringNearbyResponse>()
        return response.data
    }

    /**
     * 获取附近搜索模式的配置信息。
     *
     * @return 附近模式数据对象
     * @throws NetworkException 网络连接失败或超时
     * @throws AuthenticationException 认证凭证缺失或无效
     */
    @JsName("getNearbyMode")
    public suspend fun getNearbyMode(): GatheringNearbyModeData {
        val response =
            httpClient.get("$baseUrl/api/proxy/furtv/gatherings/nearby-mode")
                .body<GatheringNearbyModeResponse>()
        return response.data
    }

    /**
     * 获取聚会详情。
     *
     * @param id 聚会唯一标识符
     * @return 聚会详情数据对象
     * @throws NetworkException 网络连接失败或超时
     * @throws NotFoundException 指定 id 的聚会不存在
     */
    @JsName("getGatheringDetail")
    public suspend fun getGatheringDetail(id: String): GatheringDetailData {
        val response =
            httpClient.get("$baseUrl/api/proxy/furtv/gatherings/$id")
                .body<GatheringDetailResponse>()
        return response.data
    }

    /**
     * 获取聚会的报名列表（参数对象版本）。
     *
     * @param params 报名列表参数对象（gatheringId、status、cursor、limit）
     * @return 报名列表数据对象
     * @throws NetworkException 网络连接失败或超时
     */
    @JsName("getRegistrations")
    public suspend fun getRegistrations(params: GatheringRegistrationsParams): GatheringRegistrationsData {
        val response =
            httpClient.get("$baseUrl/api/proxy/furtv/gatherings/${params.gatheringId}/registrations") {
                params.status?.let { parameter("status", it) }
                params.cursor?.let { parameter("cursor", it) }
                params.limit?.let { parameter("limit", it) }
            }.body<GatheringRegistrationsResponse>()
        return response.data
    }
}
