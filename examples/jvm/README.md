# Fursuit.TV SDK JVM 示例项目

这个示例项目演示如何在 JVM 平台上使用 Fursuit.TV SDK。

## 前置要求

- JDK 17 或更高版本
- Gradle 8.0+
- Fursuit.TV 开发者账号（clientId 和 clientSecret）

## 快速开始

### 1. 配置凭证

编辑 `src/main/kotlin/Main.kt`，替换配置常量：

```kotlin
const val CLIENT_ID = "vap_xxxxxxxxxxxxxxxx"     // 你的 clientId
const val CLIENT_SECRET = "your-client-secret"    // 你的 clientSecret
const val API_KEY = "your-api-key"               // 你的 apiKey（可选）
```

### 2. 运行示例

```bash
# 使用 Gradle 运行
./gradlew run

# 或使用 IDE 直接运行 Main.kt
```

## SDK 初始化方式

### 方式 1: DSL 签名交换（推荐）

使用 DSL 构建器初始化，SDK 自动完成签名交换：

```kotlin
val sdk = fursuitTvSdk {
    clientId = "vap_xxxxxxxxxxxxxxxx"
    clientSecret = "your-client-secret"
}

// 直接调用 API
val profile = sdk.user.getUserProfile("username")
```

### 方式 2: 已有 apiKey

如果已有有效 apiKey，可直接使用：

```kotlin
val sdk = FursuitTvSdk.create(apiKey = "your-api-key")

val profile = sdk.user.getUserProfile("username")
```

### 方式 3: 显式签名交换

需要查看 TokenInfo 详情时使用：

```kotlin
val sdk = FursuitTvSdk.createForTokenExchange(
    clientId = "vap_xxxxxxxxxxxxxxxx",
    clientSecret = "your-client-secret"
)

val accessToken = sdk.auth.getAccessToken()
val apiKey = sdk.auth.getApiKey()
```

## API 调用示例

### 基础接口 (BaseApi)

```kotlin
// 健康检查
val health = sdk.base.health()
println("服务状态: ${health.status}")

// 版本查询
val version = sdk.base.getAndroidVersion()
println("最新版本: ${version.version}")

// 主题包清单
val manifest = sdk.base.getThemePacksManifest()
println("主题包数量: ${manifest.packs.size}")
```

### 用户资料 (UserApi)

```kotlin
// 获取用户资料
val profile = sdk.user.getUserProfile("username")
println("显示名称: ${profile.displayName}")
println("物种: ${profile.species}")

// 获取用户 ID
val userId = sdk.user.getUserId("username")
println("用户 ID: $userId")

// 点赞状态（只传 username）
val likeStatus = sdk.user.getLikeStatus("username")
println("点赞数: ${likeStatus.likeCount}")
```

### 搜索发现 (SearchApi)

```kotlin
// 热门推荐
val popular = sdk.search.getPopular()
println("热门用户数: ${popular.users.size}")

// 随机推荐
val randomUsers = sdk.search.getRandomFursuit(count = 5, personalized = true)

// 关键词搜索
val results = sdk.search.search(query = "fox", type = "user", limit = 10)
println("搜索结果: ${results.results.size}")
results.cursor?.let { println("下一页游标: $it") }

// 搜索建议
val suggestions = sdk.search.getSearchSuggestions("fox")

// 物种列表
val speciesList = sdk.search.getSpeciesList()

// 热门地区
val locations = sdk.search.getPopularLocations()
```

### 聚会活动 (GatheringApi)

```kotlin
// 年度统计
val stats = sdk.gathering.getYearStats()
println("总聚会数: ${stats.totalGatherings}")

// 月历查询
val gatherings = sdk.gathering.getMonthly(year = 2025, month = 4)
println("本月聚会: ${gatherings.size}")

// 附近聚会
val nearby = sdk.gathering.getNearby(lat = 37.7749, lng = -122.4194, radius = 10000)
```

### 学校角色 (SchoolApi)

```kotlin
// 搜索学校
val schools = sdk.school.searchSchools(query = "大学", limit = 10)
println("结果总数: ${schools.total}")

// 用户学校信息
val userSchools = sdk.school.getUserSchools(username = "username")
println("学校数量: ${userSchools.schools.size}")

// 用户角色列表
val characters = sdk.school.getUserCharacters(username = "username")
println("角色数量: ${characters.characters.size}")
```

## 错误处理

SDK 提供详细的异常层次结构：

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
} catch (e: ValidationException) {
    println("参数错误: ${e.message}")
} catch (e: ApiException) {
    println("API 错误 (${e.statusCode}): ${e.message}")
}
```

## 并发调用最佳实践

使用 Kotlin 协程并发调用多个 API：

```kotlin
val usernames = listOf("user1", "user2", "user3")

// 并发获取多个用户资料
val results = usernames.map { username ->
    async {
        try {
            sdk.user.getUserProfile(username)
        } catch (e: FursuitTvSdkException) {
            null
        }
    }
}.awaitAll()

val successCount = results.filterNotNull().size
println("成功获取: $successCount/${usernames.size}")
```

## 项目结构

```
examples/jvm/
├── build.gradle.kts          # Gradle 构建配置
├── settings.gradle.kts       # Gradle 设置
└── src/main/kotlin/
    └── Main.kt               # 完整示例代码（441 行）
```

## 依赖

- **Fursuit.TV SDK**: `com.furrist.rp:fursuit-tv-sdk:{version}`
- **Kotlinx Coroutines**: `org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.1`
- **Ktor Client (JVM)**: `io.ktor:ktor-client-java:3.4.2`

## 注意事项

### 1. 凭证安全

⚠️ **不要将真实凭证提交到版本控制！**

- 使用环境变量或配置文件管理敏感信息
- 将包含凭证的文件添加到 `.gitignore`
- 示例中使用的是占位符，需要替换为真实值

### 2. 资源释放

使用完毕后必须调用 `close()` 释放资源：

```kotlin
val sdk = fursuitTvSdk { ... }
try {
    // 使用 SDK
} finally {
    sdk.close()  // 必须调用！
}
```

### 3. 异步编程

所有 API 都是 suspend 函数，需要在协程中调用：

```kotlin
fun main() = runBlocking {
    val sdk = fursuitTvSdk { ... }
    // ...
}
```

### 4. 自动令牌刷新

使用 DSL 初始化时，SDK 会自动管理令牌：
- 首次调用时自动完成签名交换
- 令牌即将过期时（≤300秒）自动刷新
- 无需手动处理令牌逻辑

## 更多信息

- [认证文档](../../docs/authentication.md)
- [配置指南](../../docs/configuration.md)
- [API 参考](../../docs/API_REFERENCE.md)

## 许可证

MIT License
