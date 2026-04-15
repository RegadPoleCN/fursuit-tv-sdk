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
     * 获取当前年度的聚会统计数据
     * 端点：GET /api/proxy/furtv/gatherings/stats/this-year
     * 官方文档：https://vdsentnet.com/docs/api/gatherings#year-stats
     * @return GatheringYearStatsData 聚会年度统计数据
     */
    public suspend fun getYearStats(): GatheringYearStatsData {
        val response =
            httpClient.get("$baseUrl/api/proxy/furtv/gatherings/stats/this-year")
                .body<GatheringYearStatsResponse>()
        return response.data
    }

    /**
     * 获取聚会月历
     * 获取指定年月的聚会列表
     * 端点：GET /api/proxy/furtv/gatherings/monthly
     * 官方文档：https://vdsentnet.com/docs/api/gatherings#monthly
     * @param year 年份
     * @param month 月份
     * @return List<GatheringMonthlyItem> 聚会列表
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
     * 获取指定年月的聚会列表
     * 端点：GET /api/proxy/furtv/gatherings/monthly
     * 官方文档：https://vdsentnet.com/docs/api/gatherings#monthly
     * @param params 聚会月历参数
     * @return List<GatheringMonthlyItem> 聚会列表
     */
    public suspend fun getMonthly(params: GatheringMonthlyParams): List<GatheringMonthlyItem> {
        return getMonthly(params.year, params.month)
    }

    /**
     * 获取聚会月历距离
     * 获取指定年月的聚会列表，并计算与指定位置的距离
     * 端点：GET /api/proxy/furtv/gatherings/monthlydistance
     * 官方文档：https://vdsentnet.com/docs/api/gatherings#monthly-distance
     * @param year 年份
     * @param month 月份
     * @param lat 纬度（可选，用于计算距离）
     * @param lng 经度（可选，用于计算距离）
     * @return List<GatheringMonthlyDistanceItem> 带距离的聚会列表
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
     * 获取指定年月的聚会列表，并计算与指定位置的距离
     * 端点：GET /api/proxy/furtv/gatherings/monthlydistance
     * 官方文档：https://vdsentnet.com/docs/api/gatherings#monthly-distance
     * @param params 聚会月历参数（包含经纬度）
     * @return List<GatheringMonthlyDistanceItem> 带距离的聚会列表
     */
    public suspend fun getMonthlyDistance(params: GatheringMonthlyParams): List<GatheringMonthlyDistanceItem> {
        return getMonthlyDistance(params.year, params.month, params.lat, params.lng)
    }

    /**
     * 获取聚会附近
     * 根据地理位置获取附近的聚会
     * 端点：GET /api/proxy/furtv/gatherings/nearby
     * 官方文档：https://vdsentnet.com/docs/api/gatherings#nearby
     * @param lat 纬度
     * @param lng 经度
     * @param radius 搜索半径（米，可选）
     * @return List<GatheringNearbyItem> 附近聚会列表
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
     * 根据地理位置获取附近的聚会
     * 端点：GET /api/proxy/furtv/gatherings/nearby
     * 官方文档：https://vdsentnet.com/docs/api/gatherings#nearby
     * @param params 附近聚会参数
     * @return List<GatheringNearbyItem> 附近聚会列表
     */
    public suspend fun getNearby(params: GatheringNearbyParams): List<GatheringNearbyItem> {
        return getNearby(params.lat, params.lng, params.radius)
    }

    /**
     * 获取聚会附近模式
     * 获取聚会附近模式的信息
     * 端点：GET /api/proxy/furtv/gatherings/nearbymode
     * 官方文档：https://vdsentnet.com/docs/api/gatherings#nearby-mode
     * @return GatheringNearbyModeData 附近模式数据
     */
    public suspend fun getNearbyMode(): GatheringNearbyModeData {
        val response =
            httpClient.get("$baseUrl/api/proxy/furtv/gatherings/nearbymode")
                .body<GatheringNearbyModeResponse>()
        return response.data
    }

    /**
     * 获取聚会详情
     * 获取聚会的详细信息，包括议程、标签、报名统计等
     * 端点：GET /api/proxy/furtv/gatherings/:id
     * 官方文档：https://vdsentnet.com/docs/api/gatherings#detail
     * @param id 聚会 ID
     * @return GatheringDetailData 聚会详情数据
     */
    public suspend fun getGatheringDetail(id: String): GatheringDetailData {
        val response =
            httpClient.get("$baseUrl/api/proxy/furtv/gatherings/$id")
                .body<GatheringDetailResponse>()
        return response.data
    }

    /**
     * 获取聚会报名列表
     * 获取聚会的报名人员列表
     * 端点：GET /api/proxy/furtv/gatherings/:id/registrations
     * 官方文档：https://vdsentnet.com/docs/api/gatherings#registrations
     * @param id 聚会 ID
     * @param status 报名状态筛选（可选）
     * @param cursor 分页游标（可选）
     * @param limit 返回数量限制（可选）
     * @return GatheringRegistrationsData 报名列表数据
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
     * 获取聚会的报名人员列表
     * 端点：GET /api/proxy/furtv/gatherings/:id/registrations
     * 官方文档：https://vdsentnet.com/docs/api/gatherings#registrations
     * @param params 报名列表参数
     * @return GatheringRegistrationsData 报名列表数据
     */
    public suspend fun getRegistrations(params: GatheringRegistrationsParams): GatheringRegistrationsData {
        return getRegistrations(params.gatheringId, params.status, params.cursor, params.limit)
    }
}
