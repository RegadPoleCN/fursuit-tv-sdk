# Fursuit.TV SDK API 总览

> 本文档是合并后的 API 总览；旧版按模块拆分的 `docs/api/{base,user,search,gathering,school}.md` 已删除。

## 模块列表

| 模块 | 描述 | 端点数 |
|------|------|--------|
| [base](#base) | 基础接口（健康检查、版本、主题包） | 5 |
| [user](#user) | 用户资料 / 关系 / 访客 / 徽章 / 商店 | 8 |
| [search](#search) | 搜索 / 热门 / 随机 / 物种 / 地点 | 7 |
| [gathering](#gathering) | 聚会月历 / 距离 / 附近 / 详情 / 报名 | 6 |
| [school](#school) | 学校搜索 / 详情 / 用户学校 / 用户角色 | 4 |

所有 API 均通过 `sdk.{module}.{method}(params)` 调用。

---

## base

通过 `sdk.base` 访问。

| 端点 | 描述 | 签名 |
|------|------|------|
| `helloWorld()` | 测试 Hello World（无需认证） | `suspend fun helloWorld(): HelloWorldResponse` |
| `health()` | 健康检查 | `suspend fun health(): HealthResponse` |
| `getAndroidVersion()` | 获取 Android 最新版本 | `suspend fun getAndroidVersion(): AndroidVersionData` |
| `checkAndroidVersion(request)` | 检查当前版本是否需要更新 | `suspend fun checkAndroidVersion(req: AndroidVersionCheckRequest): AndroidVersionCheckData` |
| `getThemePacksManifest()` | 获取主题包清单 | `suspend fun getThemePacksManifest(): ThemePacksManifestData` |

---

## user

通过 `sdk.user` 访问。

| 端点 | 描述 | 签名 |
|------|------|------|
| `getUserProfile(username)` | 获取用户公开资料 | `suspend fun getUserProfile(username: String): UserProfile` |
| `getUserId(id)` | 通过 ID 查询用户基础信息 | `suspend fun getUserId(id: String): UserIdData` |
| `getLikeStatus(username)` | 获取点赞状态 | `suspend fun getLikeStatus(username: String): LikeStatusData` |
| `getUserRelationships(userId)` | 获取用户关系列表 | `suspend fun getUserRelationships(userId: String): UserRelationshipsData` |
| `getUserVisitors(username)` | 获取用户访客记录 | `suspend fun getUserVisitors(username: String): UserVisitorsData` |
| `getSocialBadges(username)` | 获取用户社交徽章列表 | `suspend fun getSocialBadges(username: String): SocialBadgesData` |
| `getSocialBadgeDetail(username, userBadgeId)` | 获取单个社交徽章详情 | `suspend fun getSocialBadgeDetail(username: String, userBadgeId: String): SocialBadgeDetailData` |
| `getStoreProducts(username)` | 获取用户商店商品 | `suspend fun getStoreProducts(username: String): StoreProductsData` |

---

## search

通过 `sdk.search` 访问。所有端点均使用参数对象。

| 端点 | 描述 | 签名 |
|------|------|------|
| `getPopular(limit)` | 获取热门用户 | `suspend fun getPopular(limit: Int? = null): PopularData` |
| `getRandomFursuit(params)` | 随机推荐兽装 | `suspend fun getRandomFursuit(params: RandomFursuitParams): List<RandomFursuit>` |
| `search(params)` | 关键词搜索 | `suspend fun search(params: SearchParams): SearchData` |
| `getSearchSuggestions(query)` | 获取搜索建议 | `suspend fun getSearchSuggestions(query: String): List<String>` |
| `searchBySpecies(species, page, limit, cursor)` | 按物种搜索 | `suspend fun searchBySpecies(species: String, page: Int? = null, limit: Int? = null, cursor: String? = null): SpeciesSearchData` |
| `getSpeciesList()` | 获取所有物种列表 | `suspend fun getSpeciesList(): SpeciesListData` |
| `getPopularLocations()` | 获取热门地点 | `suspend fun getPopularLocations(): PopularLocationsData` |

### 参数对象

```kotlin
data class SearchParams(
    val query: String,
    val type: String? = null,
    val cursor: String? = null,
    val limit: Int? = null,
    val page: Int? = null,
)

data class RandomFursuitParams(
    val count: Int? = null,
    val personalized: Boolean? = null,
)
```

---

## gathering

通过 `sdk.gathering` 访问。所有端点均使用参数对象。

| 端点 | 描述 | 签名 |
|------|------|------|
| `getYearStats()` | 获取本年度聚会统计 | `suspend fun getYearStats(): GatheringYearStatsData` |
| `getMonthly(params)` | 获取月份聚会列表 | `suspend fun getMonthly(params: GatheringMonthlyParams): List<GatheringMonthlyItem>` |
| `getMonthlyDistance(params)` | 获取月份聚会列表（带距离） | `suspend fun getMonthlyDistance(params: GatheringMonthlyParams): List<GatheringMonthlyDistanceItem>` |
| `getNearby(params)` | 获取附近聚会 | `suspend fun getNearby(params: GatheringNearbyParams): List<GatheringNearbyItem>` |
| `getNearbyMode()` | 获取附近搜索模式配置 | `suspend fun getNearbyMode(): GatheringNearbyModeData` |
| `getGatheringDetail(id)` | 获取聚会详情 | `suspend fun getGatheringDetail(id: String): GatheringDetailData` |
| `getRegistrations(params)` | 获取聚会报名列表 | `suspend fun getRegistrations(params: GatheringRegistrationsParams): GatheringRegistrationsData` |

### 参数对象

```kotlin
data class GatheringMonthlyParams(
    val year: Int,
    val month: Int,
    val lat: Double? = null,
    val lng: Double? = null,
)

data class GatheringNearbyParams(
    val lat: Double? = null,
    val lng: Double? = null,
    val radius: Int? = null,
)

data class GatheringRegistrationsParams(
    val gatheringId: String,
    val status: String? = null,
    val cursor: String? = null,
    val limit: Int? = null,
)
```

---

## school

通过 `sdk.school` 访问。

| 端点 | 描述 | 签名 |
|------|------|------|
| `searchSchools(params)` | 按关键词搜索学校 | `suspend fun searchSchools(params: SchoolSearchParams): SchoolSearchData` |
| `getSchoolDetail(schoolId)` | 获取学校详情 | `suspend fun getSchoolDetail(schoolId: String): SchoolDetail` |
| `getUserSchools(userId)` | 获取用户关联学校 | `suspend fun getUserSchools(userId: String): UserSchoolsData` |
| `getUserCharacters(username)` | 获取用户角色列表 | `suspend fun getUserCharacters(username: String): UserCharactersData` |

### 参数对象

```kotlin
data class SchoolSearchParams(
    val query: String,
    val cursor: String? = null,
    val limit: Int? = null,
)
```

---

## 通用响应结构

所有 API 方法返回业务数据（data class）。HTTP 错误由 SDK 异常体系处理（见 [`error-handling.md`](./error-handling.md)）。

| 异常 | HTTP 状态 | 触发条件 |
|------|-----------|---------|
| `TokenExpiredException` | 401 | 认证凭证缺失或过期 |
| `AuthenticationException` | 403 | 权限不足 |
| `NotFoundException` | 404 | 资源不存在 |
| `ValidationException` | 400 | 请求参数无效 |
| `ApiException` | 5xx | 服务端错误 |
| `NetworkException` | - | 网络错误 / 超时 |