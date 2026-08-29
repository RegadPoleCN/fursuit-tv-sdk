# Fursuit.TV SDK API 参考

> 本文档以当前代码为唯一事实来源生成（0.4.0）。所有方法统一返回完整 `*Response` 包装，
> `success` / `requestId` 等元数据可直接访问。0.3.x → 0.4.0 迁移对照见 `docs/MIGRATION.md`。

## 入口

通过 `FursuitTvSdk`（或 `fursuitTvSdk { }` DSL / `FursuitTvSdkBuilder`）创建 SDK 后，按域访问：

| 属性 | 类 | 端点数 |
|------|----|--------|
| `sdk.base` | `BaseApi` | 5 |
| `sdk.user` | `UserApi` | 8 |
| `sdk.search` | `SearchApi` | 7 |
| `sdk.gathering` | `GatheringApi` | 7 |
| `sdk.school` | `SchoolApi` | 4 |

所有方法为 `suspend`（JVM 上由 suspend-transform 生成 `*Blocking()` / `*Async()` 变体；
JS/Native 直接挂起调用）。

---

## base（BaseApi）

| 方法 | 返回类型 | 端点 |
|------|----------|------|
| `helloWorld()` | `HelloWorldResponse` | `GET /api/proxy/furtv/helloworld` |
| `health()` | `HealthResponse` | `GET /api/proxy/furtv/health` |
| `getAndroidVersion()` | `AndroidVersionResponse` | `GET /api/proxy/furtv/version/android` |
| `checkAndroidVersion(currentVersion: String, currentVersionCode: Int)` | `AndroidVersionCheckResponse` | `POST /api/proxy/furtv/version/android/check` |
| `getThemePacksManifest()` | `ThemePacksManifestResponse` | `GET /api/proxy/furtv/theme-packs/manifest` |

## user（UserApi）

| 方法 | 返回类型 | 端点 |
|------|----------|------|
| `getUserProfile(username: String)` | `UserProfileResponse` | `GET /api/proxy/furtv/users/{username}` |
| `getUserId(id: String)` | `UserIdResponse` | `GET /api/proxy/furtv/users/id/{id}` |
| `getLikeStatus(username: String)` | `LikeStatusResponse` | `GET /api/proxy/furtv/fursuit/like-status/{username}` |
| `getUserRelationships(userId: String)` | `UserRelationshipsResponse` | `GET /api/proxy/furtv/relationships/user/{userId}` |
| `getUserVisitors(username: String)` | `UserVisitorsResponse` | `GET /api/proxy/furtv/users/{username}/visitors` |
| `getSocialBadges(username: String, limit: Int? = null)` | `SocialBadgesResponse` | `GET /api/proxy/furtv/users/{username}/social-badges` |
| `getSocialBadgeDetail(username: String, userBadgeId: String)` | `SocialBadgeDetailResponse` | `GET /api/proxy/furtv/users/{username}/social-badges/{userBadgeId}` |
| `getStoreProducts(username: String)` | `StoreProductsResponse` | `GET /api/proxy/furtv/users/{username}/store-products` |

`getSocialBadges` 的 `limit` 可选、最大 50（超过抛 `IllegalArgumentException`）。

## search（SearchApi）

| 方法 | 返回类型 | 端点 |
|------|----------|------|
| `getPopular(limit: Int? = null)` | `PopularResponse` | `GET /api/proxy/furtv/popular` |
| `getRandomFursuit(params: RandomFursuitParams)` | `RandomFursuitResponse` | `GET /api/proxy/furtv/fursuit/random` |
| `search(params: SearchParams)` | `SearchResponse` | `GET /api/proxy/furtv/search` |
| `getSearchSuggestions(query: String)` | `SearchSuggestionsResponse` | `GET /api/proxy/furtv/search/suggestions` |
| `searchBySpecies(species: String, page: Int? = null, limit: Int? = null, cursor: String? = null)` | `SpeciesSearchResponse` | `GET /api/proxy/furtv/search/species/{species}` |
| `getSpeciesList()` | `SpeciesListResponse` | `GET /api/proxy/furtv/species` |
| `getPopularLocations()` | `PopularLocationsResponse` | `GET /api/proxy/furtv/locations/popular` |

`RandomFursuitParams(count: Int? = null)`；`RandomFursuitResponse` 含
`fursuit`（count=1）/ `fursuits`（count>1）/ `count` / `requested_count` / `debug_info` / `requestId`。

`SearchParams(query: String, type: String? = null, cursor: String? = null, limit: Int? = null, page: Int? = null)`。

## gathering（GatheringApi）

| 方法 | 返回类型 | 端点 |
|------|----------|------|
| `getYearStats()` | `GatheringYearStatsResponse` | `GET /api/proxy/furtv/gatherings/year-stats` |
| `getMonthly(params: GatheringMonthlyParams)` | `GatheringMonthlyResponse` | `GET /api/proxy/furtv/gatherings/monthly` |
| `getMonthlyDistance(params: GatheringMonthlyParams, lat: Double, lng: Double)` | `GatheringMonthlyDistanceResponse` | `GET /api/proxy/furtv/gatherings/monthly-distance` |
| `getNearby()` | `GatheringNearbyResponse` | `GET /api/proxy/furtv/gatherings/nearby` |
| `getNearbyMode()` | `GatheringNearbyModeResponse` | `GET /api/proxy/furtv/gatherings/nearby-mode` |
| `getGatheringDetail(id: String)` | `GatheringDetailResponse` | `GET /api/proxy/furtv/gatherings/{id}` |
| `getRegistrations(params: GatheringRegistrationsParams)` | `GatheringRegistrationsResponse` | `GET /api/proxy/furtv/gatherings/{gatheringId}/registrations` |

`GatheringMonthlyParams(year: Int, month: Int)`——月历距离的 `lat`/`lng` 为 `getMonthlyDistance` 的必填独立参数。
`GatheringRegistrationsParams(gatheringId: String)`。

## school（SchoolApi）

| 方法 | 返回类型 | 端点 |
|------|----------|------|
| `searchSchools(params: SchoolSearchParams)` | `SchoolSearchResponse` | `GET /api/proxy/furtv/schools/search` |
| `getSchoolDetail(schoolId: String)` | `SchoolDetailResponse` | `GET /api/proxy/furtv/schools/{schoolId}` |
| `getUserSchools(userId: String)` | `UserSchoolsResponse` | `GET /api/proxy/furtv/schools/user/{userId}` |
| `getUserCharacters(username: String)` | `UserCharactersResponse` | `GET /api/proxy/furtv/characters/user/{username}` |

`SchoolSearchParams(query: String)`——文档仅定义 `query` 一个查询参数。

---

## 认证与 OAuth（AuthManager）

- `exchangeToken(clientId, clientSecret)`：签名交换（`POST /api/auth/token`）
- `refreshToken()`：签名换新（`POST /api/auth/token/refresh`，需 platform apiKey）
- `loginWithOAuth(scope: String? = "profile")`：完整 OAuth 流程（authorize → 回调 → token → userinfo）
- `getOAuthAuthorizeUrl(redirectUri, scope?, state?)`：构造授权 URL（query 值已 URL 编码）
- `exchangeOAuthToken(code, redirectUri)`：授权码换 OAuth 用户令牌
- `getUserInfo()`：`Authorization: Bearer` 访问 userinfo
- `withFreshToken { ... }`：业务请求令牌预检（过期窗口内优先换新、失败回落交换）

## DTO 一览

按域定义于 `com.furrist.rp.furtv.sdk.model`（全部 `@JsExport`）：
请求参数类（`RandomFursuitParams` / `SearchParams` / `GatheringMonthlyParams` /
`GatheringRegistrationsParams` / `SchoolSearchParams`）、各端点 `*Response` 包装与
嵌套数据类（含 0.4.0 新增的 `TodayStatus`、`GatheringNearbyModeItem`）。
字段命名与 vds-docs 响应示例一一对应（snake_case → camelCase）。

## 已移除的 API（0.4.0）

- PKCE：`generateCodeVerifier` / `generateCodeChallenge` / `generatePkceParameters` / `PkceParameters`
- 配置级 apiKey：`SdkConfig.apiKey` / `withApiKey()` / `FursuitTvSdkBuilder.apiKey()`
- 客户端自造请求头 `X-Request-ID`
- 文档外请求参数（nearby lat/lng/radius、registrations status/cursor/limit、
  schools/search cursor/limit、random personalized）与响应字段
  （`RandomFursuit.location`、`total_users`、`can_like`）
