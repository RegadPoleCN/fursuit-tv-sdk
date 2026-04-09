# Fursuit.TV SDK 最佳实践

本文档提供 KMP 开发最佳实践，帮助你更高效、安全地使用 SDK。

## 📋 目录

1. [代码组织](#代码组织)
2. [依赖管理](#依赖管理)
3. [测试最佳实践](#测试最佳实践)
4. [性能优化](#性能优化)
5. [安全最佳实践](#安全最佳实践)

## 代码组织

### 1. 使用依赖注入

推荐使用依赖注入管理 SDK 实例：

```kotlin
// 推荐：使用单例或依赖注入
object SdkProvider {
    private var sdk: FursuitTvSdk? = null
    
    fun getSdk(appId: String, appSecret: String): FursuitTvSdk {
        return sdk ?: FursuitTvSdk(appId, appSecret).also { sdk = it }
    }
    
    fun close() {
        sdk?.close()
        sdk = null
    }
}

// 或者使用 Koin、Hilt 等依赖注入框架
```

### 2. 统一管理配置

```kotlin
// 推荐：创建配置管理类
object AppConfig {
    val appId: String = System.getenv("APP_ID") ?: "default"
    val appSecret: String = System.getenv("APP_SECRET") ?: "default"
    val baseUrl: String = "https://open-global.vdsentnet.com"
}

// 使用配置
val sdk = FursuitTvSdk(
    appId = AppConfig.appId,
    appSecret = AppConfig.appSecret,
    baseUrl = AppConfig.baseUrl
)
```

### 3. 分离业务逻辑

```kotlin
// 推荐：创建 Repository 层
class UserRepository(private val sdk: FursuitTvSdk) {
    suspend fun getUserProfile(username: String): UserProfile {
        return sdk.user.getUserProfile(username)
    }
    
    suspend fun getUserVisitors(username: String): Visitors {
        return sdk.user.getUserVisitors(username)
    }
}

// ViewModel 或 Presenter 使用
class UserViewModel(private val userRepository: UserRepository) {
    fun loadUser(username: String) {
        viewModelScope.launch {
            try {
                val profile = userRepository.getUserProfile(username)
                _uiState.value = UiState.Success(profile)
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.message)
            }
        }
    }
}
```

## 依赖管理

### 1. 使用版本目录

在 `libs.versions.toml` 中统一管理版本：

```toml
[versions]
fursuit-tv-sdk = "1.0-SNAPSHOT"
kotlinx-coroutines = "1.10.1"

[libraries]
fursuit-tv-sdk = { module = "me.regadpole:fursuit-tv-sdk", version.ref = "fursuit-tv-sdk" }
kotlinx-coroutines = { module = "org.jetbrains.kotlinx:kotlinx-coroutines-core", version.ref = "kotlinx-coroutines" }
```

### 2. 只暴露必要的依赖

```kotlin
// build.gradle.kts
dependencies {
    // SDK 已经暴露了 coroutines，因为使用了 suspend 函数
    implementation(libs.fursuit.tv.sdk)
    
    // 其他业务依赖
    implementation(libs.kotlinx.coroutines.core)
}
```

## 测试最佳实践

### 1. 使用 Mock 进行测试

```kotlin
// 推荐：创建测试基类
abstract class BaseApiTest {
    protected lateinit var sdk: FursuitTvSdk
    protected lateinit var testScope: TestScope
    
    @BeforeTest
    fun setup() {
        testScope = TestScope()
        // 配置测试用的 SDK
    }
    
    @AfterTest
    fun teardown() {
        sdk?.close()
        testScope.cancel()
    }
}

// 具体测试
class UserApiTest : BaseApiTest() {
    @Test
    fun testGetUserProfile() = testScope.runTest {
        val profile = sdk.user.getUserProfile("test-user")
        assertNotNull(profile)
        assertEquals("test-user", profile.username)
    }
}
```

### 2. 使用协程测试

```kotlin
// 推荐：使用 TestScope 和 runTest
@Test
fun testAsyncOperation() = runTest {
    val result = async {
        sdk.user.getUserProfile("username")
    }
    
    assertEquals("expected", result.await().displayName)
}
```

### 3. 测试错误处理

```kotlin
@Test
fun testNotFound() = runTest {
    assertFailsWith<NotFoundException> {
        sdk.user.getUserProfile("non-existent-user")
    }
}

@Test
fun testNetworkError() = runTest {
    assertFailsWith<NetworkException> {
        // 模拟网络错误场景
    }
}
```

## 性能优化

### 1. 复用 SDK 实例

```kotlin
// ❌ 不推荐：每次都创建新实例
fun getUser(username: String) {
    val sdk = FursuitTvSdk(appId, appSecret)
    val profile = sdk.user.getUserProfile(username)
    sdk.close()
}

// ✅ 推荐：复用实例
class UserService {
    private val sdk = FursuitTvSdk(appId, appSecret)
    
    suspend fun getUser(username: String) {
        val profile = sdk.user.getUserProfile(username)
        // SDK 会保持打开，可重复使用
    }
    
    fun close() {
        sdk.close()
    }
}
```

### 2. 批量操作

```kotlin
// 推荐：使用 async 并发请求
suspend fun getUsers(usernames: List<String>): List<UserProfile> {
    return usernames.map { username ->
        async { sdk.user.getUserProfile(username) }
    }.awaitAll()
}
```

### 3. 合理配置超时

```kotlin
// 根据网络环境调整超时
val config = SdkConfig.builder()
    .apiKey(apiKey)
    .requestTimeout(60000)  // 较差网络：60 秒
    .connectTimeout(15000)  // 连接超时：15 秒
    .socketTimeout(45000)   // 读取超时：45 秒
    .build()
```

### 4. 启用重试机制

```kotlin
// 推荐：启用重试提高稳定性
val config = SdkConfig.builder()
    .enableRetry(true)
    .maxRetries(3)
    .retryInterval(1000)
    .build()
```

## 安全最佳实践

### 1. 保护凭证

```kotlin
// ❌ 不推荐：硬编码凭证
val appId = "vap_xxxxxxxxxxxxxxxx"
val appSecret = "hardcoded-secret"

// ✅ 推荐：从环境变量或配置文件读取
val appId = System.getenv("APP_ID")
val appSecret = System.getenv("APP_SECRET")

// 或使用加密的配置文件
val config = EncryptedConfig.load("config.enc")
val appId = config.appId
val appSecret = config.appSecret
```

### 2. 安全存储令牌

```kotlin
// 推荐：使用系统安全存储
class TokenStorage {
    fun saveToken(token: TokenInfo) {
        // iOS: Keychain
        // Android: EncryptedSharedPreferences
        // JVM: 加密文件
        // JS: localStorage (仅限开发环境)
    }
    
    fun loadToken(): TokenInfo? {
        // 从安全存储加载
    }
}
```

### 3. 使用 HTTPS

```kotlin
// 确保使用 HTTPS
val config = SdkConfig.builder()
    .baseUrl("https://open-global.vdsentnet.com")  // ✅ HTTPS
    .build()
```

### 4. 启用 PKCE（OAuth 场景）

```kotlin
// 推荐：OAuth 启用 PKCE 增强安全性
val config = OAuthConfig(
    callbackHost = "localhost",
    callbackPort = 8080,
    enablePkce = true  // 启用 PKCE
)
```

### 5. 定期刷新令牌

```kotlin
// SDK 已实现自动刷新，但也可以手动检查
suspend fun ensureValidToken() {
    val token = sdk.auth.getValidAccessToken(appId, appSecret)
    // 令牌已自动刷新（如果即将过期）
}
```

### 6. 日志安全

```kotlin
// ❌ 不推荐：日志中打印敏感信息
println("Token: ${tokenInfo.accessToken}")

// ✅ 推荐：生产环境禁用敏感日志
val config = SdkConfig.builder()
    .logLevel(LogLevel.ERROR)  // 生产环境只记录错误
    .build()
```

## 跨平台注意事项

### 1. 使用 commonMain 代码

```kotlin
// 推荐：业务逻辑放在 commonMain
// src/commonMain/kotlin/
class UserRepository(private val sdk: FursuitTvSdk) {
    suspend fun getUser(username: String): UserProfile {
        return sdk.user.getUserProfile(username)
    }
}
```

### 2. 平台特定代码

```kotlin
// src/jvmMain/kotlin/
actual class TokenStorage actual constructor() {
    actual fun saveToken(token: TokenInfo) {
        // JVM: 保存到文件
    }
}

// src/jsMain/kotlin/
actual class TokenStorage actual constructor() {
    actual fun saveToken(token: TokenInfo) {
        // JS: 保存到 localStorage
    }
}
```

## 监控和诊断

### 1. 启用详细日志（开发环境）

```kotlin
val config = SdkConfig.builder()
    .logLevel(LogLevel.DEBUG)
    .build()

// 监听日志
Logger.setLevel(Level.DEBUG)
```

### 2. 添加性能监控

```kotlin
suspend fun <T> measureTime(block: suspend () -> T): T {
    val start = System.currentTimeMillis()
    return block().also {
        val duration = System.currentTimeMillis() - start
        println("API call took ${duration}ms")
    }
}

// 使用
val profile = measureTime {
    sdk.user.getUserProfile("username")
}
```

## 总结

遵循这些最佳实践可以：
- ✅ 提高代码可维护性
- ✅ 增强应用安全性
- ✅ 优化性能表现
- ✅ 简化测试流程
- ✅ 减少潜在错误

继续学习：
- [开发者指南](DEVELOPER_GUIDE.md) - 快速上手
- [故障排除](TROUBLESHOOTING.md) - 解决常见问题
- [平台指南](PLATFORM_GUIDE.md) - 特定平台配置
