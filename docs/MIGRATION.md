# Fursuit.TV SDK — 迁移指南

> 本指南描述 Fursuit.TV SDK v2.0 的破坏性变更，以及从 v1.x 迁移到 v2.0 的具体步骤。
>
> v2.0 是一个大幅简化的版本：删除了 5 个工厂方法、所有裸参数重载、双层 `*Response/*Data` 包装、合并 6 个模型文件为单文件、合并 OAuthConfig 与 OAuthCallbackServerConfig、保留 6 篇核心文档 + 1 份本迁移指南。
>
> 阅读完成后请参考 [`docs/api.md`](./api.md) 查阅完整的 API 总览。

## 章节

1. [SDK 构造迁移](#1-sdk-构造迁移)
2. [API 重载迁移](#2-api-重载迁移)
3. [OAuth 配置迁移](#3-oauth-配置迁移)
4. [字段 / 数据类迁移](#4-字段--数据类迁移)
5. [AuthManager 方法迁移](#5-authmanager-方法迁移)
6. [跨平台 OAuth 默认实现保留](#6-跨平台-oauth-默认实现保留)
7. [示例项目变更](#7-示例项目变更)

---

## 1. SDK 构造迁移

### 旧版（v1.x，已废弃）

```kotlin
// ❌ 旧工厂方法（全部删除）
val sdk = FursuitTvSdk.createForTokenExchange(clientId = "vap_xxx", clientSecret = "...")
val sdk2 = FursuitTvSdk.create(apiKey = "your-api-key")
val sdk3 = FursuitTvSdk.create(config)

// Java（旧版）
FursuitTvSdk sdk = FursuitTvSdk.createForTokenExchange("vap_xxx", "your-secret");
```

### 新版（v0.3.0）

仅保留 2 种入口（按平台选择）：

```kotlin
// Kotlin (suspend) — DSL 写法（推荐）
val sdk = fursuitTvSdk {
    clientId = "vap_xxx"
    clientSecret = "your-secret"
}

// Java / 链式 Builder 写法（推荐用于 Java）
FursuitTvSdk sdk = FursuitTvSdkBuilder.create()
    .clientId("vap_xxx")
    .clientSecret("your-secret")
    .buildBlocking();
```

### 删除的入口

| 旧 API | 删除原因 |
|--------|---------|
| `FursuitTvSdk.Companion.create(apiKey)` | 5 个并列工厂冗余 |
| `FursuitTvSdk.Companion.createForTokenExchange(...)` | 同上 |
| `FursuitTvSdk.Companion.create(config, tokenInfo)` | 同上 |
| `FursuitTvSdk.Companion.createWithConfig(...)` | 同上 |
| `FursuitTvSdk.Companion.createWithDsl(...)` | DSL 替代 |
| `fursuitTvSdkBlocking { ... }` 整条 expect/actual 链 | `FursuitTvSdkBuilder` 替代（@JvmBlocking 生成 buildBlocking） |
| `JvmFursuitTvSdkBuilder` 类 | 合并到 `FursuitTvSdkBuilder`（仅 rename） |
| `SdkConfig.Companion.builder()` | 与 SDK DSL 重叠 |
| `sdkConfig { ... }`（顶层函数） | 同上 |

### 迁移示例

```diff
- val sdk = FursuitTvSdk.createForTokenExchange("vap_xxx", "your-secret")
+ val sdk = fursuitTvSdk {
+     clientId = "vap_xxx"
+     clientSecret = "your-secret"
+ }
```

```diff
// Java 旧版（删除）
- FursuitTvSdk sdk = JvmFursuitTvSdkBuilder.create()
-     .apiKey("your-api-key")
-     .buildBlocking();
// Java 新版
+ FursuitTvSdk sdk = FursuitTvSdkBuilder.create()
+     .clientId("vap_xxx")
+     .clientSecret("your-secret")
+     .buildBlocking();
```

---

## 2. API 重载迁移

### 旧版（v1.x）

```kotlin
sdk.search("query", type = "users", cursor = null, limit = 50)
sdk.search("query")  // 多个裸重载
sdk.getMonthly(year = 2024, month = 11)
sdk.getMonthlyDistance(year = 2024, month = 11, lat = 1.0, lng = 2.0)
sdk.getNearby(lat = 1.0, lng = 2.0, radius = 5000)
sdk.getRegistrations(gatheringId = "abc", status = "approved")
sdk.getRandomFursuit(count = 5, personalized = true)
sdk.searchSchools(query = "PKU")
```

### 新版（v2.0，0.4.0 修订）

每个 API 仅有一个签名，接收参数对象。0.4.0 起文档外请求参数已移除，参数类仅保留 vds-docs 定义的字段：

```kotlin
sdk.search(SearchParams(query = "query", type = "users", limit = 50))
sdk.getMonthly(GatheringMonthlyParams(year = 2024, month = 11))
// 月历距离的 lat/lng 为必填独立参数（0.4.0 起）
sdk.getMonthlyDistance(GatheringMonthlyParams(year = 2024, month = 11), lat = 1.0, lng = 2.0)
// 0.4.0 起 getNearby 无查询参数
sdk.getNearby()
sdk.getRegistrations(GatheringRegistrationsParams(gatheringId = "abc"))
sdk.getRandomFursuit(RandomFursuitParams(count = 5))
sdk.searchSchools(SchoolSearchParams(query = "PKU"))
```

### 删除的裸重载

| 旧 API | 替换为 |
|--------|--------|
| `search(query, type?, cursor?, limit?, page?)` | `search(SearchParams(...))` |
| `getRandomFursuit(count?, personalized?)` | `getRandomFursuit(RandomFursuitParams(count))` |
| `getMonthly(year, month)` | `getMonthly(GatheringMonthlyParams(year, month))` |
| `getMonthlyDistance(year, month, lat?, lng?)` | `getMonthlyDistance(GatheringMonthlyParams(year, month), lat, lng)`（lat/lng 必填） |
| `getNearby(lat?, lng?, radius?)` | `getNearby()`（0.4.0 起无参） |
| `getRegistrations(id, status?, cursor?, limit?)` | `getRegistrations(GatheringRegistrationsParams(gatheringId))` |
| `searchSchools(query, cursor?, limit?)` | `searchSchools(SchoolSearchParams(query))` |

---

## 3. OAuth 配置迁移

### 旧版（v1.x）

```kotlin
val oauthConfig = OAuthCallbackServerConfig(
    callbackHost = "localhost",
    callbackPort = 8080,
    callbackPath = "/callback",
    timeoutSeconds = 300,
)
val handler = createDefaultOAuthHandler(oauthConfig)
```

```kotlin
// （旧版 enablePkce / stateTimeoutMinutes 配置项已随 0.4.0 PKCE 移除而删除）
```

### 新版（v2.0）

两个 Config 合并为单一 `OAuthConfig`：

```kotlin
val oauthConfig = OAuthConfig(
    callbackHost = "localhost",
    callbackPort = 8080,
    callbackPath = "/callback",
    timeoutSeconds = 300,   // 默认 300，同时承担"回调等待超时"与"state 存储超时"两种角色
)
val handler = createDefaultOAuthHandler(oauthConfig)
```

### 删除的类 / 字段

| 旧项 | 删除原因 |
|------|---------|
| `OAuthCallbackServerConfig` | 已合并到 `OAuthConfig` |
| `OAuthConfig.stateTimeoutMinutes` | 已合并到 `OAuthConfig.timeoutSeconds`（默认 300） |

---

## 4. 字段 / 数据类迁移

### 数据模型合并

- 所有 `*Models.kt` 文件合并为单一 [`src/commonMain/kotlin/com/furrist/rp/furtv/sdk/model/Models.kt`](../src/commonMain/kotlin/com/furrist/rp/furtv/sdk/model/Models.kt)
- ~~所有 `*Response` / `*Data` 双层包装类合并保留为单一 API 返回类型（直接返回业务数据）~~
  **0.4.0 更正**：全部 API 方法统一返回完整 `*Response` 包装（含 `success`/`requestId` 元数据），对照见本文末尾「0.4.0 API 返回值统一」

### 字段迁移

| 旧字段 | 新字段 |
|--------|--------|
| `TokenInfo.expiresIn` | `TokenInfo.expiresIn`（保留） |
| `TokenInfo.apiKey` | `TokenInfo.apiKey`（保留） |
| `OAuthConfig.stateTimeoutMinutes` | `OAuthConfig.timeoutSeconds`（300s = 5min 等价） |
| `OAuthCallbackServerConfig.timeoutSeconds: Long` | `OAuthConfig.timeoutSeconds: Int` |
| `GatheringRegistrationsParams.id` | `GatheringRegistrationsParams.gatheringId` |

### `appId` 字段

- `TokenData.appId: String?` 字段保留以兼容服务端响应格式
- 与 `SdkConfig.clientId`（用户配置）在多数情况下等价

---

## 5. AuthManager 方法迁移

### 保留的方法（12 个）

```kotlin
sdk.auth.exchangeToken(clientId, clientSecret)
sdk.auth.refreshToken()
sdk.auth.loginWithOAuth(scope = "profile")   // 0.4.0 起 scope 默认 "profile"
sdk.auth.setOAuthCallbackHandler(handler)
sdk.auth.getOAuthAuthorizeUrl(redirectUri, scope = null, state = null)
sdk.auth.exchangeOAuthToken(code, redirectUri)
sdk.auth.getUserInfo()
sdk.auth.getApiKey()
sdk.auth.isAuthenticated()
sdk.auth.setTokenInfo(tokenInfo)
sdk.auth.clearToken()
sdk.auth.withFreshToken { /* 业务请求自动完成令牌预检 */ }
```

### 删除的方法

| 旧方法 | 删除原因 |
|--------|---------|
| `getValidAccessToken(clientId, clientSecret)` | 自动刷新语义复杂，调用方应显式调用 `exchangeToken` 或 `refreshToken` |
| `refreshTokenIfNeeded()` | 同上（之前已 `@Deprecated`） |
| `close()` | HttpClient 已共享到 `FursuitTvSdk.close()`；不提供 `@Deprecated` 兼容调用 |

---

## 6. 跨平台 OAuth 默认实现保留

每个平台仍保留独立的、可以直接使用的 OAuth 默认实现：

| 平台 | 实现 | 文件 |
|------|------|------|
| JVM | `JvmOAuthCallbackHandler`（Ktor CIO + 自动打开浏览器） | [src/jvmMain/.../JvmOAuthCallbackHandler.kt](../src/jvmMain/kotlin/com/furrist/rp/furtv/sdk/auth/JvmOAuthCallbackHandler.kt) |
| JS (浏览器 + Node.js) | `JsOAuthCallbackHandler`（`postMessage` / Node `http` 模块自动检测） | [src/jsMain/.../JsOAuthCallbackHandler.kt](../src/jsMain/kotlin/com/furrist/rp/furtv/sdk/auth/JsOAuthCallbackHandler.kt) |
| Native (iOS/macOS/Linux/Windows) | `NativeOAuthCallbackHandler`（Ktor Network 本地 HTTP 服务器） | [src/nativeMain/.../NativeOAuthCallbackHandler.kt](../src/nativeMain/kotlin/com/furrist/rp/furtv/sdk/auth/NativeOAuthCallbackHandler.kt) |

`expect fun createDefaultOAuthHandler(config: OAuthConfig): OAuthCallbackHandler` 跨平台工厂入口保留。

---

## 7. 示例项目变更

### 保留的示例

| 项目 | 路径 | 描述 |
|------|------|------|
| **examples/jvm/** | `examples/jvm/` | JVM Main.kt + build.gradle.kts |
| **examples/js/** | `examples/js/` | 浏览器 + Node 双场景 + package.json |
| **examples/java/** | `examples/java/` | 单一 Main.java（演示 `FursuitTvSdkBuilder.buildBlocking()`） |

### 删除的示例

| 删除项目 | 说明 |
|----------|------|
| `examples/android/` | 删除，Native Android 集成说明已合并到 [`docs/getting-started.md`](./getting-started.md) |
| `examples/ios/` | 删除，iOS Swift 集成说明已合并到 [`docs/getting-started.md`](./getting-started.md) |
| `examples/kotlin-js/` | 删除，与 `examples/js/` 重复 |

### Android / iOS 简要集成

#### Android（`build.gradle.kts`）

```kotlin
dependencies {
    implementation("com.furrist.rp:fursuit-tv-sdk:<version>")
    implementation("io.ktor:ktor-client-android:<ktor-version>")
}

val sdk = runBlocking {
    fursuitTvSdk {
        clientId = "vap_xxx"
        clientSecret = "your-secret"
    }
}
```

#### iOS（Swift Package Manager）

```swift
// Package.swift
dependencies: [
    .package(url: "https://github.com/RegadPoleCN/fursuit-tv-sdk", from: "<version>")
]
```

```swift
import FursuitTvSdk

let sdk = try await FursuitTvSdkKt.fursuitTvSdk { cfg in
    cfg.clientId = "vap_xxx"
    cfg.clientSecret = "your-secret"
}
```

---

## 文档迁移总览

### 删除的文档

- `docs/README.md`（合并到根 `README.md`）
- `docs/oauth-guide.md`（合并到 `docs/authentication.md`）
- `docs/platform-guide.md`（合并到 `docs/getting-started.md`）
- `docs/best-practices.md`（合并到 `docs/getting-started.md`）
- `docs/configuration.md`（合并到 `docs/getting-started.md`）
- `docs/TROUBLESHOOTING.md`（合并到 `docs/error-handling.md`）
- `docs/api/README.md`（合并到 `docs/api.md`）
- `docs/api/{base,user,search,gathering,school}.md`（合并到 `docs/api.md`）
- `docs/publishing/PUBLISHING_GUIDE.md` + `docs/publishing/README.md`（合并到 `docs/getting-started.md`）
- `docs/publishing/gradle.properties.example`（不在文档范围内）

### 保留的文档（6 篇）

| 文档 | 角色 |
|------|------|
| `README.md`（根） | 总入口 + 快速示例 |
| `docs/getting-started.md` | 安装 + 初始化 + 平台集成说明 |
| `docs/authentication.md` | 签名交换 + OAuth 完整流程 |
| `docs/error-handling.md` | 异常体系 + 处理策略 |
| `docs/api.md` | 合并所有 6 个 API 模块的单一文档 |
| `docs/MIGRATION.md` | 本文档：v1.x → v2.0 迁移指南（0.4.0 变更见下方专节） |

---

## 0.4.0 API 返回值统一

全部 API 方法统一返回完整 `*Response` 包装，`success` / `requestId` 等元数据可达。
旧调用 → 新调用对照（逐方法）：

| 方法 | 旧调用 | 新调用 |
|------|--------|--------|
| `user.getUserProfile` | `profile.username` | `profile.user.username` |
| `user.getUserId` | `id.id` | `id.user.id` |
| `gathering.getMonthly` | `monthly.<字段>` | `monthly.data.<字段>` |
| `gathering.getMonthlyDistance` | `distance.<字段>` | `distance.data.<字段>` |
| `gathering.getNearby` | `nearby.size` | `nearby.data.size`（旧返回是 `List`，直接追加 `.data`） |
| `gathering.getNearbyMode` | `nearbyMode.gatherings.size` | `nearbyMode.data.gatherings.size` |
| `gathering.getGatheringDetail` | `detail.title` | `detail.gathering.title` |
| `base.getAndroidVersion` | `v.version` | `v.data.version` |
| `base.checkAndroidVersion` | `r.needUpdate` | `r.data.needUpdate`（字段名是 `needUpdate`，无 `updateAvailable`） |
| `base.getThemePacksManifest` | `m.themes` | `m.data.themes` |
| `school.getSchoolDetail` | `s.name` | `s.school.name` |
| `search.getRandomFursuit` | `list.size` | `resp.fursuits?.size ?: resp.fursuit?.let { 1 } ?: 0` |

其他 0.4.0 变更（详见 CHANGELOG）：

- **破坏性**：配置级 apiKey 删除、PKCE 删除、`checkAndroidVersion` 的 `currentVersionCode` 改必填、`getMonthlyDistance` 新增必填 `lat`/`lng`、文档外请求参数与响应字段移除、删除客户端自造的 `X-Request-ID` 请求头、删除零引用的 `OAuthAuthorizeParams`/`OAuthTokenRequest`
- **行为**：`loginWithOAuth` scope 默认值改为 `"profile"`；`AuthHolder` 接线修复后业务请求恢复自动注入 `X-Api-Key`（`/account/sso/` 端点除外）；业务请求过期窗口内优先换新令牌；sso 端点错误结构化为 `OAuthException(errorCode)`
- **新增字段/类型**：`RandomFursuit` 补 12 个用户数据字段（含新类型 `TodayStatus`）；`GatheringDetailData` 补 14 字段；新增 `GatheringNearbyModeItem`；`SearchUser`/`SpeciesSearchUser` 补 `like_count`/`is_liked`；三个认证 DTO 补 `requestId`
- **有意子集**：`HealthResponse`（db/user_stats 等诊断对象）与 `RandomDebugInfo`（约 38 个调试遥测字段）有意不建模
