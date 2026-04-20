package com.furrist.rp.furtv.sdk.gathering

import com.furrist.rp.furtv.sdk.model.GatheringMonthlyParams
import com.furrist.rp.furtv.sdk.model.GatheringNearbyParams
import com.furrist.rp.furtv.sdk.model.GatheringRegistrationsParams
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter

/**
 * 聚会相关 API
 * 提供聚会列表、统计、详情、报名等聚会相关功能的访问接口
 * @param httpClient 配置好的 HTTP 客户端
 * @param baseUrl API 基础 URL，默认为 https://open-global.vdsentnet.com
 */
public class GatheringApi(
    private val httpClient: HttpClient,
    private val baseUrl: String = "https://open-global.vdsentnet.com",
) {
    /**
     * 获取聚会年度统计
     * @return 聚会年度统计数据
     */
    public suspend fun getYearStats(): GatheringYearStatsData {
        val response =
            httpClient.get("$baseUrl/api/proxy/furtv/gatherings/stats/this-year")
                .body<GatheringYearStatsResponse>()
        return response.data
    }

    /**
     * 获取聚会月历
     * @param year 年份
     * @param month 月份
     * @return 聚会列表
     */
    public suspend fun getMonthly(year: Int, month: Int): List<GatheringMonthlyItem> {
        val response =
            httpClient.get("$baseUrl/api/proxy/furtv/gatherings/monthly") {
                parameter("year", year)
                parameter("month", month)
            }.body<GatheringMonthlyResponse>()
        return response.data
    }

    /**
     * 获取聚会月历（参数对象版本）
     * @param params 聚会月历参数
     * @return 聚会列表
     */
    public suspend fun getMonthly(params: GatheringMonthlyParams): List<GatheringMonthlyItem> {
        return getMonthly(params.year, params.month)
    }

    /**
     * 获取聚会月历距离
     * @param year 年份
     * @param month 月份
     * @param lat 纬度（可选，用于计算距离）
     * @param lng 经度（可选，用于计算距离）
     * @return 带距离的聚会列表
     */
    public suspend fun getMonthlyDistance(
        year: Int,
        month: Int,
        lat: Double? = null,
        lng: Double? = null,
    ): List<GatheringMonthlyDistanceItem> {
        val response =
            httpClient.get("$baseUrl/api/proxy/furtv/gatherings/monthlydistance") {
                parameter("year", year)
                parameter("month", month)
                lat?.let { parameter("lat", it) }
                lng?.let { parameter("lng", it) }
            }.body<GatheringMonthlyDistanceResponse>()
        return response.data
    }

    /**
     * 获取聚会月历距离（参数对象版本）
     * @param params 聚会月历参数（包含经纬度）
     * @return 带距离的聚会列表
     */
    public suspend fun getMonthlyDistance(params: GatheringMonthlyParams): List<GatheringMonthlyDistanceItem> {
        return getMonthlyDistance(params.year, params.month, params.lat, params.lng)
    }

    /**
     * 获取聚会附近
     * @param lat 纬度
     * @param lng 经度
     * @param radius 搜索半径（米，可选）
     * @return 附近聚会列表
     */
    public suspend fun getNearby(
        lat: Double,
        lng: Double,
        radius: Int? = null,
    ): List<GatheringNearbyItem> {
        val response =
            httpClient.get("$baseUrl/api/proxy/furtv/gatherings/nearby") {
                parameter("lat", lat)
                parameter("lng", lng)
                radius?.let { parameter("radius", it) }
            }.body<GatheringNearbyResponse>()
        return response.data
    }

    /**
     * 获取聚会附近（参数对象版本）
     * @param params 附近聚会参数
     * @return 附近聚会列表
     */
    public suspend fun getNearby(params: GatheringNearbyParams): List<GatheringNearbyItem> {
        return getNearby(params.lat, params.lng, params.radius)
    }

    /**
     * 获取聚会附近模式
     * @return 附近模式数据
     */
    public suspend fun getNearbyMode(): GatheringNearbyModeData {
        val response =
            httpClient.get("$baseUrl/api/proxy/furtv/gatherings/nearbymode")
                .body<GatheringNearbyModeResponse>()
        return response.data
    }

    /**
     * 获取聚会详情
     * @param id 聚会 ID
     * @return 聚会详情数据
     */
    public suspend fun getGatheringDetail(id: String): GatheringDetailData {
        val response =
            httpClient.get("$baseUrl/api/proxy/furtv/gatherings/$id")
                .body<GatheringDetailResponse>()
        return response.data
    }

    /**
     * 获取聚会报名列表
     * @param id 聚会 ID
     * @param status 报名状态筛选（可选）
     * @param cursor 分页游标（可选）
     * @param limit 返回数量限制（可选）
     * @return 报名列表数据
     */
    public suspend fun getRegistrations(
        id: String,
        status: String? = null,
        cursor: String? = null,
        limit: Int? = null,
    ): GatheringRegistrationsData {
        val response =
            httpClient.get("$baseUrl/api/proxy/furtv/gatherings/$id/registrations") {
                status?.let { parameter("status", it) }
                cursor?.let { parameter("cursor", it) }
                limit?.let { parameter("limit", it) }
            }.body<GatheringRegistrationsResponse>()
        return response.data
    }

    /**
     * 获取聚会报名列表（参数对象版本）
     * @param params 报名列表参数
     * @return 报名列表数据
     */
    public suspend fun getRegistrations(params: GatheringRegistrationsParams): GatheringRegistrationsData {
        return getRegistrations(params.gatheringId, params.status, params.cursor, params.limit)
    }
}
