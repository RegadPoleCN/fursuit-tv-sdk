# Fursuit.TV SDK Kotlin/JS (Node.js) 示例

这个示例项目演示如何在 Node.js 环境中使用 Kotlin/JS 编译的 Fursuit.TV SDK。

## ⚠️ 重要说明

**此示例使用 Kotlin 语言编写**，通过 Kotlin/JS 编译为 JavaScript 在 Node.js 中运行。

- ✅ 支持 Node.js 16.0.0+ 环境
- ❌ 不支持浏览器环境（OAuth 回调需要 HTTP 服务器）

## 前置要求

- Node.js 16.0.0 或更高版本
- npm 或 yarn
- Fursuit.TV 开发者账号

## 快速开始

### 1. 安装依赖

```bash
npm install
```

### 2. 配置凭证

编辑 `src/index.js`（Kotlin 源码），替换配置常量：

```kotlin
const val CLIENT_ID = "vap_xxxxxxxxxxxxxxxx"     // 你的 clientId
const val CLIENT_SECRET = "your-client-secret"    // 你的 clientSecret
const val API_KEY = "your-api-key"               // 你的 apiKey（可选）
```

### 3. 运行示例

```bash
# 构建并运行
npm run build && npm start
```

## SDK 初始化方式

### 方式 1: DSL 签名交换（推荐）

使用 Kotlin DSL 构建器初始化：

```kotlin
val sdk = fursuitTvSdk {
    clientId = "vap_xxxxxxxxxxxxxxxx"
    clientSecret = "your-client-secret"
}

// 直接调用 API
val profile = sdk.user.getUserProfile("username")
```

### 方式 2: 使用 apiKey

如果已有有效 apiKey：

```kotlin
val sdk = FursuitTvSdk.create(apiKey = "your-api-key")

val profile = sdk.user.getUserProfile("username")
```

## API 调用示例

所有示例代码都在 `src/index.js` 中（Kotlin 源码）。

### 基础接口 (BaseApi)

```kotlin
val health = sdk.base.health()
println("服务状态: ${health.status}")

val version = sdk.base.getAndroidVersion()
println("最新版本: ${version.version}")
```

### 用户资料 (UserApi)

```kotlin
val profile = sdk.user.getUserProfile("username")
println("显示名称: ${profile.displayName}")

// 点赞状态 - 只传 username
val likeStatus = sdk.user.getLikeStatus("username")
println("点赞数: ${likeStatus.likeCount}")

// 访客记录 - 传 username
val visitors = sdk.user.getUserVisitors("username")
println("访客数量: ${visitors.visitors.size}")

// 社交徽章 - 传 username
val badges = sdk.user.getSocialBadges("username")
println("徽章数量: ${badges.badges.size}")

// 商店商品 - 传 username
val products = sdk.user.getStoreProducts("username")
println("商品数量: ${products.products.size}")
```

### 搜索发现 (SearchApi)

```kotlin
val popular = sdk.search.getPopular()

val results = sdk.search.search(
    query = "fox",
    type = "user",
    limit = 10
)
results.cursor?.let { println("下一页: $it") }

val suggestions = sdk.search.getSearchSuggestions("fox")
```

### 聚会活动 (GatheringApi)

```kotlin
val stats = sdk.gathering.getYearStats()
println("总聚会数: ${stats.totalGatherings}")

val gatherings = sdk.gathering.getMonthly(year = 2025, month = 4)

val nearby = sdk.gathering.getNearby(lat = 37.7749, lng = -122.4194)
```

### 学校角色 (SchoolApi)

```kotlin
val schools = sdk.school.searchSchools(query = "大学", limit = 10)

val userSchools = sdk.school.getUserSchools(username = "username")

val characters = sdk.school.getUserCharacters(username = "username")
```

## 错误处理

SDK 提供完整的异常层次结构：

```kotlin
try {
    val profile = sdk.user.getUserProfile("username")
} catch (e: NotFoundException) {
    println("用户不存在: ${e.message}")
} catch (e: AuthenticationException) {
    println("认证失败: ${e.message}")
} catch (e: TokenExpiredException) {
    println("令牌过期，SDK 会自动刷新")
} catch (e: NetworkException) {
    println("网络错误: ${e.message}")
} catch (e: ApiException) {
    println("API 错误 (${e.statusCode}): ${e.message}")
}
```

## 并发调用

使用 Kotlin 协程并发调用多个 API：

```kotlin
val usernames = listOf("user1", "user2", "user3")

val results = usernames.map { username ->
    async {
        try {
            sdk.user.getUserProfile(username)
        } catch (e: FursuitTvSdkException) {
            null
        }
    }
}.awaitAll()
```

## 项目结构

```
examples/js/
├── package.json          # npm 配置
├── src/
│   └── index.js          # Kotlin/JS 示例代码（298 行）
└── README.md             # 本文档
```

## 注意事项

### 1. 仅支持 Node.js

此示例需要在 Node.js 环境中运行：
- OAuth 回调需要本地 HTTP 服务器
- 浏览器环境有 CORS 限制
- 需要 Node.js 16+ 的异步支持

### 2. 异步编程

所有 API 都是 Kotlin suspend 函数：
- 在协程作用域内调用（runBlocking / coroutineScope）
- 使用 async/awaitAll 进行并发操作

### 3. 资源释放

使用完毕后必须调用 `close()`：

```kotlin
val sdk = fursuitTvSdk { ... }
try {
    // 使用 SDK
} finally {
    sdk.close()  // 必须调用！
}
```

### 4. 自动令牌管理

使用 DSL 初始化时：
- 首次自动完成签名交换
- 令牌即将过期时自动刷新（≤300秒）
- 无需手动管理令牌生命周期

## 浏览器环境替代方案

如需在浏览器中使用，建议：

1. **后端代理模式**
   ```kotlin
   // 前端调用自己的后端 API
   val response = fetch("/api/user/profile?username=user")
   ```

2. **预获取令牌**
   - 后端完成 OAuth 流程
   - 前端使用 accessToken 调用 API

## 更多信息

- [认证文档](../../docs/authentication.md)
- [JVM 示例](../jvm/README.md)
- [API 参考](../../docs/API_REFERENCE.md)

## 许可证

MIT License
