# 最佳实践

本文档总结 Fursuit.TV SDK 使用的最佳实践、性能优化技巧和常见模式。

## 目录

1. [初始化最佳实践](#初始化最佳实践)
2. [API 调用最佳实践](#api-调用最佳实践)
3. [性能优化](#性能优化)
4. [安全最佳实践](#安全最佳实践)
5. [代码组织](#代码组织)

## 初始化最佳实践

### 1. 使用环境变量管理密钥

```kotlin
val sdk = fursuitTvSdk {
    clientId = System.getenv("FURSUIT_CLIENT_ID")
    clientSecret = System.getenv("FURSUIT_CLIENT_SECRET")
    baseUrl = System.getenv("FURSUIT_BASE_URL")
        ?: "https://open-global.vdsentnet.com"
}
```

### 2. 单例模式管理 SDK 实例

```kotlin
object FursuitSdkProvider {
    val sdk: FursuitTvSdk by lazy {
        fursuitTvSdk {
            clientId = System.getenv("FURSUIT_CLIENT_ID")
            clientSecret = System.getenv("FURSUIT_CLIENT_SECRET")
            logLevel = LogLevel.INFO
            enableRetry = true
        }
    }
    
    fun close() = sdk.close()
}

// 使用
val profile = FursuitSdkProvider.sdk.user.getUserProfile("username")

// 应用退出时关闭
Runtime.getRuntime().addShutdownHook(Thread { FursuitSdkProvider.close() })
```

### 3. 根据环境配置

```kotlin
val sdk = fursuitTvSdk {
    clientId = System.getenv("FURSUIT_CLIENT_ID")
    clientSecret = System.getenv("FURSUIT_CLIENT_SECRET")
    
    baseUrl = when (environment) {
        PRODUCTION -> "https://open-global.vdsentnet.com"
        else -> "https://test-api.vdsentnet.com"
    }
    
    logLevel = when (environment) {
        PRODUCTION -> LogLevel.WARNING
        else -> LogLevel.DEBUG
    }
}
```

## API 调用最佳实践

### 1. 使用 try-catch 处理异常

```kotlin
suspend fun getUserProfile(username: String): UserProfile? {
    return try {
        sdk.user.getUserProfile(username)
    } catch (e: NotFoundException) {
        logger.warn("用户不存在：$username")
        null
    } catch (e: NetworkException) {
        logger.error("网络错误", e)
        null
    } catch (e: ApiException) {
        logger.error("API 错误 (${e.statusCode})", e)
        null
    }
}
```

### 2. 使用 Result 类型封装结果

```kotlin
sealed class ApiResult<out T> {
    data class Success<T>(val data: T) : ApiResult<T>()
    data class Error(val exception: FursuitTvSdkException, val message: String) : ApiResult<Nothing>()
}

suspend fun <T> runCatchingApi(operation: String, block: suspend () -> T): ApiResult<T> {
    return try {
        ApiResult.Success(block())
    } catch (e: FursuitTvSdkException) {
        ApiResult.Error(e, "$operation 失败：${e.message}")
    }
}

// 使用
when (val result = runCatchingApi("获取用户") { sdk.user.getUserProfile("username") }) {
    is ApiResult.Success -> println("用户：${result.data.displayName}")
    is ApiResult.Error -> println("错误：${result.message}")
}
```

### 3. 批量操作使用并发

```kotlin
suspend fun getUserProfiles(usernames: List<String>): List<UserProfile> {
    return coroutineScope {
        usernames.map { username ->
            async {
                try {
                    sdk.user.getUserProfile(username)
                } catch (e: Exception) {
                    logger.warn("获取用户 $username 失败", e)
                    null
                }
            }
        }.awaitAll().filterNotNull()
    }
}
```

## 性能优化

### 1. 复用 SDK 实例

```kotlin
// ✅ 推荐：复用实例
class UserService {
    private val sdk = FursuitSdkProvider.sdk
    
    suspend fun getUser(username: String) = sdk.user.getUserProfile(username)
}
```

### 2. 合理设置超时时间

```kotlin
val sdk = fursuitTvSdk {
    connectTimeout = if (isMobileNetwork) 20000 else 10000
    socketTimeout = if (isMobileNetwork) 60000 else 30000
}
```

### 3. 使用分页加载大数据集

```kotlin
suspend fun loadAllUsers(): List<UserProfile> {
    val allUsers = mutableListOf<UserProfile>()
    var page = 1
    val pageSize = 50
    
    while (true) {
        val response = sdk.search.search(
            query = "",
            type = "user",
            limit = pageSize,
            offset = (page - 1) * pageSize
        )
        
        if (response.results.isEmpty()) break
        allUsers.addAll(response.results)
        page++
    }
    
    return allUsers
}
```

## 安全最佳实践

### 1. 保护敏感信息

- 使用环境变量或加密存储管理密钥
- 不要将 clientId/clientSecret 硬编码在代码中
- 使用 EncryptedSharedPreferences（Android）或 Keychain（iOS）存储 token

### 2. 验证 OAuth State

```kotlin
class OAuthManager {
    private val pendingStates = mutableSetOf<String>()
    
    fun generateState(): String {
        val state = Random.nextBytes(16).toHex()
        pendingStates.add(state)
        return state
    }
    
    fun validateState(state: String): Boolean {
        return pendingStates.remove(state)
    }
}

// 使用
val state = oauthManager.generateState()
val authUrl = sdk.auth.getOAuthAuthorizeUrl(redirectUri = redirectUri, state = state)
```

### 3. 启用 PKCE

```kotlin
val authUrl = sdk.auth.getOAuthAuthorizeUrl(
    redirectUri = "my-app://callback",
    enablePkce = true  // 移动端始终启用
)
```

## 代码组织

### 1. 使用 Repository 模式

```kotlin
interface UserRepository {
    suspend fun getUserProfile(username: String): UserProfile?
    suspend fun searchUsers(query: String): List<UserProfile>
}

class FursuitUserRepository(private val sdk: FursuitTvSdk) : UserRepository {
    override suspend fun getUserProfile(username: String): UserProfile? {
        return try {
            sdk.user.getUserProfile(username)
        } catch (e: NotFoundException) {
            null
        }
    }
    
    override suspend fun searchUsers(query: String): List<UserProfile> {
        return try {
            val results = sdk.search.search(query, type = "user")
            results.results.mapNotNull { it as? UserProfile }
        } catch (e: Exception) {
            emptyList()
        }
    }
}
```

### 2. 使用依赖注入

```kotlin
// 使用 Koin
val appModule = module {
    single {
        fursuitTvSdk {
            clientId = get()
            clientSecret = get()
        }
    }
    
    single<UserRepository> { FursuitUserRepository(get()) }
    viewModel { MainViewModel(get()) }
}
```

### 3. 封装 SDK 调用

```kotlin
class FursuitApiClient(private val sdk: FursuitTvSdk) {
    suspend fun <T> safeApiCall(block: suspend () -> T): Result<T> = try {
        Result.success(block())
    } catch (e: FursuitTvSdkException) {
        Result.failure(e)
    }
    
    suspend fun getUserProfile(username: String): Result<UserProfile> = 
        safeApiCall { sdk.user.getUserProfile(username) }
}
```

## 测试建议

### 1. 使用 Mock 进行测试

```kotlin
class UserRepositoryTest {
    private lateinit var mockSdk: FursuitTvSdk
    private lateinit var mockUserApi: UserApi
    
    @Before
    fun setup() {
        mockSdk = mockk()
        mockUserApi = mockk()
        every { mockSdk.user } returns mockUserApi
    }
    
    @Test
    fun `getUserProfile returns user when exists`() = runTest {
        val expectedUser = UserProfile(...)
        every { mockUserApi.getUserProfile("testuser") } returns expectedUser
        
        val result = repository.getUserProfile("testuser")
        assertEquals(expectedUser, result)
    }
}
```

### 2. 使用测试配置

```kotlin
object TestConfig {
    val sdk: FursuitTvSdk by lazy {
        fursuitTvSdk {
            clientId = System.getenv("TEST_CLIENT_ID") ?: "vap_test"
            clientSecret = System.getenv("TEST_CLIENT_SECRET") ?: "test-secret"
            baseUrl = "https://test-api.vdsentnet.com"
            logLevel = LogLevel.OFF
            enableRetry = false
        }
    }
}
```

## 相关文档

- [配置选项](configuration.md) - 配置最佳实践
- [错误处理](error-handling.md) - 错误处理策略
- [OAuth 指南](oauth-guide.md) - OAuth 安全实践
