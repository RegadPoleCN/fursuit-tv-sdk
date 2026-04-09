package me.regadpole.furtv.sdk.gathering

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import me.regadpole.furtv.sdk.model.GatheringMonthlyParams
import me.regadpole.furtv.sdk.model.GatheringNearbyParams
import me.regadpole.furtv.sdk.model.GatheringRegistrationsParams

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
     * @return GatheringStatsData 聚会统计数据
     */
    public suspend fun getGatheringStatsThisYear(): GatheringStatsData {
        val response =
            httpClient.get("$baseUrl/api/proxy/furtv/gatherings/stats/this-year")
                .body<GatheringStatsResponse>()
        return response.data
    }

    /**
     * 获取聚会月历
     * 获取指定年月的聚会列表
     * 端点：GET /api/proxy/furtv/gatherings/monthly
     * @param params 聚会月历参数
     * @return List<GatheringMonthlyItem> 聚会列表
     */
    public suspend fun getGatheringMonthly(params: GatheringMonthlyParams): List<GatheringMonthlyItem> {
        val response =
            httpClient.get("$baseUrl/api/proxy/furtv/gatherings/monthly") {
                parameter("year", params.year)
                parameter("month", params.month)
            }.body<GatheringMonthlyResponse>()
        return response.data
    }

    /**
     * 获取聚会月历（重载方法，保持向后兼容）
     * @param year 年份
     * @param month 月份
     * @return List<GatheringMonthlyItem> 聚会列表
     */
    public suspend fun getGatheringMonthly(year: Int, month: Int): List<GatheringMonthlyItem> {
        return getGatheringMonthly(GatheringMonthlyParams(year, month))
    }

    /**
     * 获取聚会月历（带距离）
     * 获取指定年月的聚会列表，并计算与指定位置的距离
     * 端点：GET /api/proxy/furtv/gatherings/monthly-distance
     * @param params 聚会月历参数（包含经纬度）
     * @return List<GatheringWithDistance> 带距离的聚会列表
     */
    public suspend fun getGatheringMonthlyDistance(params: GatheringMonthlyParams): List<GatheringWithDistance> {
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
     * 获取聚会月历（带距离，重载方法，保持向后兼容）
     * @param year 年份
     * @param month 月份
     * @param lat 纬度（可选）
     * @param lng 经度（可选）
     * @return List<GatheringWithDistance> 带距离的聚会列表
     */
    public suspend fun getGatheringMonthlyDistance(
        year: Int,
        month: Int,
        lat: Double? = null,
        lng: Double? = null,
    ): List<GatheringWithDistance> {
        return getGatheringMonthlyDistance(GatheringMonthlyParams(year, month, lat, lng))
    }

    /**
     * 获取附近聚会
     * 根据地理位置获取附近的聚会
     * 端点：GET /api/proxy/furtv/gatherings/nearby
     * @param params 附近聚会参数
     * @return List<GatheringNearby> 附近聚会列表
     */
    public suspend fun getGatheringNearby(params: GatheringNearbyParams): List<GatheringNearby> {
        val response =
            httpClient.get("$baseUrl/api/proxy/furtv/gatherings/nearby") {
                parameter("lat", params.lat)
                parameter("lng", params.lng)
                params.radius?.let { parameter("radius", it) }
            }.body<GatheringNearbyResponse>()
        return response.data
    }

    /**
     * 获取附近聚会（重载方法，保持向后兼容）
     * @param lat 纬度
     * @param lng 经度
     * @param radius 搜索半径（米）
     * @return List<GatheringNearby> 附近聚会列表
     */
    public suspend fun getGatheringNearby(
        lat: Double,
        lng: Double,
        radius: Int? = null,
    ): List<GatheringNearby> {
        return getGatheringNearby(GatheringNearbyParams(lat, lng, radius))
    }

    /**
     * 获取聚会附近模式
     * 获取聚会附近模式的信息
     * 端点：GET /api/proxy/furtv/gatherings/nearby-mode
     * @return GatheringNearbyModeData 附近模式数据
     */
    public suspend fun getGatheringNearbyMode(): GatheringNearbyModeData {
        val response =
            httpClient.get("$baseUrl/api/proxy/furtv/gatherings/nearby-mode")
                .body<GatheringNearbyModeResponse>()
        return response.data
    }

    /**
     * 获取聚会详情
     * 获取聚会的详细信息，包括议程、标签、报名统计等
     * 端点：GET /api/proxy/furtv/gatherings/:id
     * @param id 聚会 ID
     * @return GatheringDetail 聚会详情
     */
    public suspend fun getGatheringDetail(id: String): GatheringDetail {
        val response =
            httpClient.get("$baseUrl/api/proxy/furtv/gatherings/$id")
                .body<GatheringDetailResponse>()
        return response.data
    }

    /**
     * 获取聚会报名列表
     * 获取聚会的报名人员列表
     * 端点：GET /api/proxy/furtv/gatherings/:id/registrations
     * @param params 报名列表参数
     * @return GatheringRegistrationsData 报名列表数据
     */
    public suspend fun getGatheringRegistrations(params: GatheringRegistrationsParams): GatheringRegistrationsData {
        val response =
            httpClient.get("$baseUrl/api/proxy/furtv/gatherings/${params.gatheringId}/registrations") {
                params.status?.let { parameter("status", it) }
                params.cursor?.let { parameter("cursor", it) }
                params.limit?.let { parameter("limit", it) }
            }.body<GatheringRegistrationsResponse>()
        return response.data
    }

    /**
     * 获取聚会报名列表（重载方法，保持向后兼容）
     * @param id 聚会 ID
     * @param status 报名状态筛选（可选）
     * @param cursor 分页游标（可选）
     * @param limit 返回数量限制（可选）
     * @return GatheringRegistrationsData 报名列表数据
     */
    public suspend fun getGatheringRegistrations(
        id: String,
        status: String? = null,
        cursor: String? = null,
        limit: Int? = null,
    ): GatheringRegistrationsData {
        return getGatheringRegistrations(GatheringRegistrationsParams(id, status, cursor, limit))
    }
}
