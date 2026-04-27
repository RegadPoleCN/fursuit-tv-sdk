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

/**
 * 聚会相关 API。
 *
 * 提供聚会列表、统计、详情、报名、附近搜索等聚会相关功能的访问接口。
 * 所有方法均通过 HTTP GET 请求获取数据，返回对应的聚会数据模型。
 *
 * ## 主要功能
 * - 年度统计（getYearStats）：获取当前年度聚会统计数据
 * - 月历查询（getMonthly, getMonthlyDistance）：按月份查询聚会列表，支持距离计算
 * - 附近搜索（getNearby, getNearbyMode）：基于地理位置查找附近聚会
 * - 聚会详情（getGatheringDetail）：获取单个聚会的完整信息
 * - 报名管理（getRegistrations）：查询聚会的报名列表，支持 cursor-based 分页
 *
 * ## 分页说明
 * - getRegistrations() 方法使用 **cursor-based 分页**：通过 cursor 游标翻页
 * - 返回的 GatheringRegistrationsData 包含 nextCursor 用于获取下一页
 * - 其他列表方法返回完整结果集（无分页）
 *
 * @param httpClient 配置好的 HTTP 客户端
 * @param baseUrl API 基础 URL，默认为 https://open-global.vdsentnet.com
 * @see GatheringModels 聚会相关的数据模型定义
 * @see FursuitTvSdkException 异常层次结构
 */
@JsExport
@JsName("GatheringApi")
public class GatheringApi internal constructor(
    private val httpClient: HttpClient,
    private val baseUrl: String = "https://open-global.vdsentnet.com",
) {
    /**
     * 获取当前年度聚会统计信息。
     *
     * 返回本年度的聚会总数、已结束、进行中、即将开始等统计数据。
     * 可用于首页展示、年度概览等场景。
     *
     * @return 聚会年度统计数据对象（GatheringYearStatsData）
     * @throws NetworkException 网络连接失败或超时
     * @throws TokenExpiredException 访问令牌已过期或无效
     * @throws AuthenticationException 认证凭证缺失或无效
     */
    public suspend fun getYearStats(): GatheringYearStatsData {
        val response =
            httpClient.get("$baseUrl/api/proxy/furtv/gatherings/stats/this-year")
                .body<GatheringYearStatsResponse>()
        return response.data
    }

    /**
     * 获取指定月份的聚会列表。
     *
     * 返回指定年月内的所有聚会，按日期排序。
     * 可用于日历视图、月度活动列表等场景。
     *
     * @param year 年份（如 2025）
     * @param month 月份（1-12）
     * @return 聚会列表（List\<GatheringMonthlyItem\>），可能为空列表
     * @throws NetworkException 网络连接失败或超时
     * @throws TokenExpiredException 访问令牌已过期或无效
     * @throws AuthenticationException 认证凭证缺失或无效
     * @throws ValidationException 参数值超出允许范围
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
     * 获取指定月份的聚会列表（参数对象版本）。
     *
     * 便捷方法：使用 GatheringMonthlyParams 对象传入参数。
     * 内部委托给 getMonthly(year, month) 方法。
     *
     * @param params 聚会月历参数对象，包含 year 和 month 字段
     * @return 聚会列表（List\<GatheringMonthlyItem\>）
     * @throws NetworkException 网络连接失败或超时
     * @throws TokenExpiredException 访问令牌已过期或无效
     */
    @JsName("getMonthlyWithParams")
    public suspend fun getMonthly(params: GatheringMonthlyParams): List<GatheringMonthlyItem> {
        return getMonthly(params.year, params.month)
    }

    /**
     * 获取指定月份的聚会列表（带距离信息）。
     *
     * 返回指定年月内的所有聚会，并计算每个聚会与指定坐标的距离。
     * 可用于地图展示、距离排序等场景。
     *
     * @param year 年份（如 2025）
     * @param month 月份（1-12）
     * @param lat 参考点纬度（可选），null 时不计算距离
     * @param lng 参考点经度（可选），null 时不计算距离
     * @return 带距离的聚会列表（List\<GatheringMonthlyDistanceItem\>），包含 distance 字段
     * @throws NetworkException 网络连接失败或超时
     * @throws TokenExpiredException 访问令牌已过期或无效
     * @throws AuthenticationException 认证凭证缺失或无效
     * @throws ValidationException 参数值超出允许范围
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
     * 获取指定月份的聚会列表（带距离信息，参数对象版本）。
     *
     * 便捷方法：使用 GatheringMonthlyParams 对象传入参数（包含经纬度）。
     * 内部委托给 getMonthlyDistance(year, month, lat, lng) 方法。
     *
     * @param params 聚会月历参数对象，包含 year/month/lat/lng 字段
     * @return 带距离的聚会列表（List\<GatheringMonthlyDistanceItem\>）
     * @throws NetworkException 网络连接失败或超时
     * @throws TokenExpiredException 访问令牌已过期或无效
     */
    @JsName("getMonthlyDistanceWithParams")
    public suspend fun getMonthlyDistance(params: GatheringMonthlyParams): List<GatheringMonthlyDistanceItem> {
        return getMonthlyDistance(params.year, params.month, params.lat, params.lng)
    }

    /**
     * 获取附近的聚会列表。
     *
     * 基于指定坐标和搜索半径查找附近的聚会，按距离排序。
     * 可用于"附近活动"功能、地图标记等场景。
     *
     * @param lat 中心点纬度（-90 到 90）
     * @param lng 中心点经度（-180 到 180）
     * @param radius 搜索半径（米），null 表示使用服务端默认值
     * @return 附近聚会列表（List\<GatheringNearbyItem\>），包含距离信息
     * @throws NetworkException 网络连接失败或超时
     * @throws TokenExpiredException 访问令牌已过期或无效
     * @throws AuthenticationException 认证凭证缺失或无效
     * @throws ValidationException 坐标值超出有效范围
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
     * 获取附近的聚会列表（参数对象版本）。
     *
     * 便捷方法：使用 GatheringNearbyParams 对象传入参数。
     * 内部委托给 getNearby(lat, lng, radius) 方法。
     *
     * @param params 附近聚会参数对象，包含 lat/lng/radius 字段
     * @return 附近聚会列表（List\<GatheringNearbyItem\>）
     * @throws NetworkException 网络连接失败或超时
     * @throws TokenExpiredException 访问令牌已过期或无效
     */
    @JsName("getNearbyWithParams")
    public suspend fun getNearby(params: GatheringNearbyParams): List<GatheringNearbyItem> {
        return getNearby(params.lat, params.lng, params.radius)
    }

    /**
     * 获取附近搜索模式的配置信息。
     *
     * 返回附近搜索功能的当前模式配置，包括默认半径、最大半径等。
     * 可用于 UI 配置、功能开关等场景。
     *
     * @return 附近模式数据对象（GatheringNearbyModeData），包含模式配置信息
     * @throws NetworkException 网络连接失败或超时
     * @throws TokenExpiredException 访问令牌已过期或无效
     * @throws AuthenticationException 认证凭证缺失或无效
     */
    public suspend fun getNearbyMode(): GatheringNearbyModeData {
        val response =
            httpClient.get("$baseUrl/api/proxy/furtv/gatherings/nearbymode")
                .body<GatheringNearbyModeResponse>()
        return response.data
    }

    /**
     * 获取聚会详情。
     *
     * 根据聚会 ID 返回该聚会的完整信息，包括名称、时间、地点、描述等。
     * 可用于聚会详情页面展示。
     *
     * @param id 聚会唯一标识符（UUID 格式）
     * @return 聚会详情数据对象（GatheringDetailData），包含聚会的完整信息
     * @throws NetworkException 网络连接失败或超时
     * @throws TokenExpiredException 访问令牌已过期或无效
     * @throws AuthenticationException 认证凭证缺失或无效
     * @throws NotFoundException 指定 id 的聚会不存在
     */
    public suspend fun getGatheringDetail(id: String): GatheringDetailData {
        val response =
            httpClient.get("$baseUrl/api/proxy/furtv/gatherings/$id")
                .body<GatheringDetailResponse>()
        return response.data
    }

    /**
     * 获取聚会的报名列表。
     *
     * 查询指定聚会的报名记录，支持按状态筛选和 cursor-based 分页。
     * 返回已报名用户的信息列表和分页游标。
     *
     * @param id 聚会唯一标识符（UUID 格式）
     * @param status 报名状态筛选（如 "confirmed", "pending"），null 表示不限制
     * @param cursor 分页游标（从上一次结果的 nextCursor 获取），null 表示首页
     * @param limit 返回数量限制（建议 1-100），null 表示使用服务端默认值
     * @return 报名列表数据对象（GatheringRegistrationsData），包含 registrations 列表和 nextCursor
     * @throws NetworkException 网络连接失败或超时
     * @throws TokenExpiredException 访问令牌已过期或无效
     * @throws AuthenticationException 认证凭证缺失或无效
     * @throws NotFoundException 指定 id 的聚会不存在
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
     * 获取聚会的报名列表（参数对象版本）。
     *
     * 便捷方法：使用 GatheringRegistrationsParams 对象传入参数。
     * 内部委托给 getRegistrations(id, status, cursor, limit) 方法。
     *
     * @param params 报名列表参数对象，包含 gatheringId/status/cursor/limit 字段
     * @return 报名列表数据对象（GatheringRegistrationsData），包含 registrations 列表和 nextCursor
     * @throws NetworkException 网络连接失败或超时
     * @throws TokenExpiredException 访问令牌已过期或无效
     */
    @JsName("getRegistrationsWithParams")
    public suspend fun getRegistrations(params: GatheringRegistrationsParams): GatheringRegistrationsData {
        return getRegistrations(params.gatheringId, params.status, params.cursor, params.limit)
    }
}
