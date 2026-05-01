# Fursuit.TV SDK JVM 示例项目

这个示例项目演示如何在 JVM 平台上使用 Fursuit.TV SDK。

## 前置要求

- JDK 17 或更高版本
- Gradle 8.0+
- Fursuit.TV 开发者账号（clientId 和 clientSecret）

## 快速开始

### 1. 配置凭证

编辑 `src/main/kotlin/Main.kt`，替换占位符：

- `"your-api-key"` — 替换为你的 API Key
- `"username"` — 替换为要查询的用户名

### 2. 运行示例

```bash
./gradlew run
```

## SDK 初始化方式

### 方式 1: DSL 签名交换（推荐）

```kotlin
val sdk = FursuitTvSdk.buildBlocking {
    clientId = "vap_xxxxxxxxxxxxxxxx"
    clientSecret = "your-client-secret"
}

val profile = sdk.user.getUserProfile("username")
```

> `buildBlocking()` 是 suspend-transform 插件为 `build()` suspend 函数生成的阻塞变体，
> 适合在非协程上下文（如 `main` 函数）中直接调用。也可以使用 `buildAsync()` 获取
> `CompletableDeferred<FursuitTvSdk>` 进行异步构建。

### 方式 2: 已有 apiKey

```kotlin
val sdk = FursuitTvSdk.create("your-api-key")

val profile = sdk.user.getUserProfile("username")
```

## API 调用示例

### 基础接口 (BaseApi)

```kotlin
val health = sdk.base.health()
println("服务状态: ${health.message}")

val version = sdk.base.getAndroidVersion()
println("最新版本: ${version.version}")

val manifest = sdk.base.getThemePacksManifest()
println("主题包数量: ${manifest.packs.size}")
```

### 用户资料 (UserApi)

```kotlin
val profile = sdk.user.getUserProfile("username")
println("用户名: ${profile.username}")
println("昵称: ${profile.nickname}")

val userId = sdk.user.getUserId("username")
println("用户 ID: ${userId.id}")

val likeStatus = sdk.user.getLikeStatus("username")
println("已点赞: ${likeStatus.isLiked}")
```

### 搜索发现 (SearchApi)

```kotlin
val popular = sdk.search.getPopular()
println("热门用户数: ${popular.users.size}")

val randomFursuits = sdk.search.getRandomFursuit(count = 5)
println("随机兽装数: ${randomFursuits.size}")

val searchResult = sdk.search.search(query = "fox")
println("搜索结果: ${searchResult.users.size}")

val speciesList = sdk.search.getSpeciesList()
println("物种数量: ${speciesList.species.size}")
```

### 聚会活动 (GatheringApi)

```kotlin
val yearStats = sdk.gathering.getYearStats()
println("聚会总数: ${yearStats.total}")

val monthly = sdk.gathering.getMonthly(year = 2025, month = 4)
println("月历聚会数: ${monthly.size}")

val nearbyMode = sdk.gathering.getNearbyMode()
println("附近聚会数: ${nearbyMode.gatherings.size}")
```

### 学校角色 (SchoolApi)

```kotlin
val schools = sdk.school.searchSchools(query = "大学", limit = 10)
println("搜索结果: ${schools.schools.size}")

val userSchools = sdk.school.getUserSchools(userId = "123")
println("学校数量: ${userSchools.schools.size}")

val characters = sdk.school.getUserCharacters(username = "username")
println("角色数量: ${characters.characters.size}")
```

## 错误处理

```kotlin
try {
    val profile = sdk.user.getUserProfile("username")
} catch (e: NotFoundException) {
    println("用户不存在: ${e.message}")
} catch (e: AuthenticationException) {
    println("认证失败: ${e.message}")
} catch (e: TokenExpiredException) {
    println("令牌过期: ${e.message}")
} catch (e: NetworkException) {
    println("网络错误: ${e.message}")
} catch (e: ApiException) {
    println("API 错误 (HTTP ${e.statusCode}): ${e.message}")
} finally {
    sdk.close()
}
```

## 项目结构

```
examples/jvm/
├── build.gradle.kts
├── settings.gradle.kts
└── src/main/kotlin/
    └── Main.kt
```

## 注意事项

- 使用完毕后必须调用 `sdk.close()` 释放资源
- 所有 API 都是 `suspend` 函数，需要在协程中调用
- SDK 会自动管理令牌刷新，无需手动处理
- **Java 消费者**：suspend-transform 插件为每个 suspend 函数生成了 `xxxBlocking()` 和 `xxxAsync()` 变体。Java 代码可使用 `sdk.base.healthBlocking()` 进行阻塞调用，或使用 `sdk.base.healthAsync()` 获取 `CompletableDeferred` 进行异步调用

## 相关示例

- [Kotlin/JS 示例](../kotlin-js/README.md)
- [JS/TS 示例](../js/README.md)
- [Java 示例](../java/README.md)
