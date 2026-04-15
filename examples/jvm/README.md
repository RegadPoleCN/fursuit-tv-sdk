# Fursuit.TV SDK JVM 示例项目

这个示例项目演示如何在 JVM 平台上使用 Fursuit.TV SDK。

## 前置要求

- JDK 17 或更高版本
- Gradle 8.0+
- Fursuit.TV 开发者账号（appId 和 appSecret）

## 配置

1. 克隆或下载此示例项目

2. 编辑 `src/main/kotlin/Main.kt`，替换配置：

```kotlin
const val APP_ID = "vap_xxxxxxxxxxxxxxxx"  // 你的 appId
const val APP_SECRET = "your-app-secret"    // 你的 appSecret
const val API_KEY = "your-api-key"          // 你的 apiKey（可选）
```

## 运行

```bash
# 使用 Gradle 运行
./gradlew run

# 或使用 IDE 直接运行 Main.kt
```

## 示例内容

### 示例 1: 使用 appId + appSecret

```kotlin
val sdk = FursuitTvSdk(
    appId = APP_ID,
    appSecret = APP_SECRET
)

// SDK 会自动管理令牌
val profile = sdk.user.getUserProfile("username")
```

### 示例 2: 使用 apiKey

```kotlin
val sdk = FursuitTvSdk(apiKey = API_KEY)
val profile = sdk.user.getUserProfile("username")
```

### 示例 3: 错误处理

```kotlin
try {
    val profile = sdk.user.getUserProfile("username")
} catch (e: NotFoundException) {
    println("用户不存在")
} catch (e: AuthenticationException) {
    println("认证失败")
} catch (e: Exception) {
    println("其他错误：${e.message}")
}
```

### 示例 4: 批量操作

```kotlin
val usernames = listOf("user1", "user2", "user3")
val profiles = usernames.map { username ->
    async { sdk.user.getUserProfile(username) }
}.awaitAll()
```

## 项目结构

```
examples/jvm/
├── build.gradle.kts          # Gradle 构建配置
├── settings.gradle.kts       # Gradle 设置
└── src/main/kotlin/
    └── Main.kt               # 示例代码
```

## 依赖

- Fursuit.TV SDK: `com.furrist.rp:fursuit-tv-sdk:0.1.0`
- Kotlinx Coroutines: `1.10.1`
- Ktor Client (Java): `3.4.2`

## 注意事项

1. **不要将凭证提交到版本控制**
   - 使用环境变量或配置文件
   - 将敏感信息添加到 `.gitignore`

2. **资源释放**
   - 使用完毕后调用 `sdk.close()`

3. **错误处理**
   - 始终使用 try-catch 处理异常
   - 记录 requestId 便于排查问题

## 更多信息

- [开发者指南](../../docs/DEVELOPER_GUIDE.md)
- [最佳实践](../../docs/BEST_PRACTICES.md)
- [平台指南](../../docs/PLATFORM_GUIDE.md)

## 许可证

MIT License
