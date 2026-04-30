# Fursuit.TV SDK Kotlin/JS 示例项目

这个示例项目演示如何在 Kotlin/JS (Node.js) 平台上使用 Fursuit.TV SDK。

## 前置要求

- Node.js 16.0.0 或更高版本
- Gradle 8.0+
- Fursuit.TV 开发者账号（clientId 和 clientSecret）

## 快速开始

### 1. 配置凭证

编辑 `src/jsMain/kotlin/Main.kt`，替换占位符：

- `"vap_xxxxxxxxxxxxxxxx"` — 替换为你的 clientId（即 VDS 文档中的 appId）
- `"your-client-secret-here"` — 替换为你的 clientSecret
- `"your-api-key-here"` — 替换为你的 API Key

### 2. 运行示例

```bash
./gradlew jsNodeRun
```

## SDK 初始化方式

### 方式 1: DSL 签名交换（推荐）

```kotlin
val sdk = FursuitTvSdk.create {
    clientId = "vap_xxxxxxxxxxxxxxxx"
    clientSecret = "your-client-secret"
    logLevel = SdkLogLevel.INFO
}
```

### 方式 2: 已有 apiKey

```kotlin
val sdk = FursuitTvSdk.create("your-api-key")
```

### 方式 3: 顶层 DSL 函数（JS 友好）

```kotlin
val sdk = fursuitTvSdk { config ->
    config.clientId = "vap_xxxxxxxxxxxxxxxx"
    config.clientSecret = "your-client-secret"
}
```

> 注意：`fursuitTvSdk` 的 lambda 参数类型为 `(MutableSdkConfig) -> Unit`，
> 需要通过参数（如 `config`）访问属性；而 `FursuitTvSdk.create` 的 lambda
> 类型为 `MutableSdkConfig.() -> Unit`，可直接访问属性。

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

val speciesList = sdk.search.getSpeciesList()
println("物种数量: ${speciesList.species.size}")

val searchResult = sdk.search.search(query = "fox")
println("搜索结果: ${searchResult.users.size}")
```

### 聚会活动 (GatheringApi)

```kotlin
val yearStats = sdk.gathering.getYearStats()
println("聚会总数: ${yearStats.total}")

val nearbyMode = sdk.gathering.getNearbyMode()
println("附近聚会数: ${nearbyMode.gatherings.size}")
```

### 学校角色 (SchoolApi)

```kotlin
val schools = sdk.school.searchSchools(query = "大学", limit = 10)
println("搜索结果: ${schools.schools.size}")
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

## 并发调用

```kotlin
val results = coroutineScope {
    listOf("user1", "user2", "user3").map { username ->
        async { sdk.user.getUserProfile(username) }
    }.awaitAll()
}
```

## Kotlin/JS 注意事项

1. **协程入口**：Kotlin/JS 没有 `runBlocking`，使用 `GlobalScope.launch` 启动协程
2. **suspend 函数**：所有 API 方法都是 `suspend` 函数，需在协程中调用
3. **资源释放**：使用完毕后必须调用 `sdk.close()` 释放资源
4. **Long 类型**：SDK 中 `Long` 类型在 JS 中导出为 `BigInt`

## 项目结构

```
examples/kotlin-js/
├── build.gradle.kts
├── settings.gradle.kts
└── src/jsMain/kotlin/
    └── Main.kt
```

## 相关示例

- [JVM 示例](../jvm/README.md)
- [JS/TS 示例](../js/README.md)
- [Java 示例](../java/README.md)
