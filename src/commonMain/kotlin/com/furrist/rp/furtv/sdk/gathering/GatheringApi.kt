package com.furrist.rp.furtv.sdk.gathering

import com.furrist.rp.furtv.sdk.model.GatheringMonthlyParams
import com.furrist.rp.furtv.sdk.model.GatheringNearbyParams
import com.furrist.rp.furtv.sdk.model.GatheringRegistrationsParams
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
     * 获取指定月份的聚会列表。
     *
     * @param year 年份
     * @param month 月份（1-12）
     * @return 聚会列表
     * @throws NetworkException 网络连接失败或超时
     * @throws AuthenticationException 认证凭证缺失或无效
     * @throws ValidationException 参数值超出允许范围
     */
    @JsName("getMonthly")
    public suspend fun getMonthly(year: Int, month: Int): List<GatheringMonthlyItem> {
        val response =
            httpClient.get("$baseUrl/api/proxy/furtv/gatherings/monthly") {
                parameter("year", year)
                parameter("month", month)
            }.body<GatheringMonthlyResponse>()
        return response.data
    }

    /**
     * 获取指定月份的聚会列表（参数对象版本）。
     *
     * @param params 聚会月历参数对象
     * @return 聚会列表
     * @throws NetworkException 网络连接失败或超时
     */
    @JsName("getMonthlyWithParams")
    public suspend fun getMonthly(params: GatheringMonthlyParams): List<GatheringMonthlyItem> =
        getMonthly(params.year, params.month)

    /**
     * 获取指定月份的聚会列表（带距离信息）。
     *
     * @param year 年份
     * @param month 月份（1-12）
     * @param lat 参考点纬度，null 时不计算距离
     * @param lng 参考点经度，null 时不计算距离
     * @return 带距离的聚会列表
     * @throws NetworkException 网络连接失败或超时
     * @throws ValidationException 参数值超出允许范围
     */
    @JsName("getMonthlyDistance")
    public suspend fun getMonthlyDistance(
        year: Int,
        month: Int,
        lat: Double? = null,
        lng: Double? = null,
    ): List<GatheringMonthlyDistanceItem> {
        val response =
            httpClient.get("$baseUrl/api/proxy/furtv/gatherings/monthly-distance") {
                parameter("year", year)
                parameter("month", month)
                lat?.let { parameter("lat", it) }
                lng?.let { parameter("lng", it) }
            }.body<GatheringMonthlyDistanceResponse>()
        return response.data
    }

    /**
     * 获取指定月份的聚会列表（带距离信息，参数对象版本）。
     *
     * @param params 聚会月历参数对象
     * @return 带距离的聚会列表
     * @throws NetworkException 网络连接失败或超时
     */
    @JsName("getMonthlyDistanceWithParams")
    public suspend fun getMonthlyDistance(params: GatheringMonthlyParams): List<GatheringMonthlyDistanceItem> =
        getMonthlyDistance(params.year, params.month, params.lat, params.lng)

    /**
     * 获取附近的聚会列表。
     *
     * @param lat 中心点纬度（-90 到 90），null 表示使用服务端默认定位
     * @param lng 中心点经度（-180 到 180），null 表示使用服务端默认定位
     * @param radius 搜索半径（米），null 表示使用服务端默认值
     * @return 附近聚会列表
     * @throws NetworkException 网络连接失败或超时
     */
    @JsName("getNearby")
    public suspend fun getNearby(
        lat: Double? = null,
        lng: Double? = null,
        radius: Int? = null,
    ): List<GatheringNearbyItem> {
        val response =
            httpClient.get("$baseUrl/api/proxy/furtv/gatherings/nearby") {
                lat?.let { parameter("lat", it) }
                lng?.let { parameter("lng", it) }
                radius?.let { parameter("radius", it) }
            }.body<GatheringNearbyResponse>()
        return response.data
    }

    /**
     * 获取附近的聚会列表（参数对象版本）。
     *
     * @param params 附近聚会参数对象
     * @return 附近聚会列表
     * @throws NetworkException 网络连接失败或超时
     */
    @JsName("getNearbyWithParams")
    public suspend fun getNearby(params: GatheringNearbyParams): List<GatheringNearbyItem> =
        getNearby(params.lat, params.lng, params.radius)

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
     * 获取聚会的报名列表。
     *
     * @param id 聚会唯一标识符
     * @param status 报名状态筛选，null 表示不限制
     * @param cursor 分页游标，null 表示首页
     * @param limit 返回数量限制，null 表示使用服务端默认值
     * @return 报名列表数据对象
     * @throws NetworkException 网络连接失败或超时
     * @throws NotFoundException 指定 id 的聚会不存在
     */
    @JsName("getRegistrations")
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
     * 获取聚会的报名列表（参数对象版本）。
     *
     * @param params 报名列表参数对象
     * @return 报名列表数据对象
     * @throws NetworkException 网络连接失败或超时
     */
    @JsName("getRegistrationsWithParams")
    public suspend fun getRegistrations(params: GatheringRegistrationsParams): GatheringRegistrationsData =
        getRegistrations(params.gatheringId, params.status, params.cursor, params.limit)
}
