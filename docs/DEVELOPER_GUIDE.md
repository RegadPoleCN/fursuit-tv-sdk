# Fursuit.TV SDK 开发者指南

快速开始使用 Fursuit.TV SDK，5 分钟内上手。

## 📦 安装

### Gradle (Kotlin DSL)

```kotlin
repositories {
    mavenCentral()
}

dependencies {
    implementation("me.regadpole:fursuit-tv-sdk:1.0-SNAPSHOT")
}
```

### Gradle (Groovy)

```groovy
repositories {
    mavenCentral()
}

dependencies {
    implementation 'me.regadpole:fursuit-tv-sdk:1.0-SNAPSHOT'
}
```

### Maven

```xml
<dependency>
    <groupId>me.regadpole</groupId>
    <artifactId>fursuit-tv-sdk</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>
```

## 🚀 快速开始

### 1. 初始化 SDK

SDK 提供四种初始化方式，选择适合你的一种：

#### 方式 1: 使用 apiKey（最简单）

适用于已有 apiKey 的用户：

```kotlin
val sdk = FursuitTvSdk(apiKey = "your-api-key")
```

#### 方式 2: 使用 appId + appSecret（推荐）

适用于新用户，SDK 自动管理令牌：

```kotlin
val sdk = FursuitTvSdk(
    appId = "vap_xxxxxxxxxxxxxxxx",
    appSecret = "your-app-secret"
)

// 获取初始令牌
runBlocking {
    sdk.auth.exchangeToken(appId, appSecret)
}
```

#### 方式 3: OAuth 2.0（需要用户授权）

适用于需要用户登录的应用：

```kotlin
val config = OAuthConfig(callbackHost = "localhost", callbackPort = 8080)
val sdk = FursuitTvSdk.initWithOAuth("vap_xxxxxxxxxxxxxxxx", config)
```

#### 方式 4: 使用 accessToken

适用于已有访问令牌：

```kotlin
val sdk = FursuitTvSdk(accessToken = "your-access-token")
```

### 2. 调用 API

```kotlin
runBlocking {
    // 获取用户资料
    val userProfile = sdk.user.getUserProfile("username")
    println("用户：${userProfile.displayName}")
    
    // 获取热门推荐
    val popular = sdk.search.getPopular()
    popular.users.forEach { user ->
        println("热门用户：${user.displayName}")
    }
}
```

### 3. 关闭 SDK

```kotlin
sdk.close()
```

## 📖 完整示例

```kotlin
import me.regadpole.furtv.sdk.FursuitTvSdk
import kotlinx.coroutines.runBlocking

fun main() = runBlocking {
    // 初始化 SDK（推荐方式）
    val sdk = FursuitTvSdk(
        appId = "vap_xxxxxxxxxxxxxxxx",
        appSecret = "your-app-secret"
    )
    
    try {
        // 获取令牌
        sdk.auth.exchangeToken(appId, appSecret)
        
        // 调用 API
        val userProfile = sdk.user.getUserProfile("username")
        println("用户资料：${userProfile.displayName}")
        
        val popular = sdk.search.getPopular()
        println("热门用户数量：${popular.users.size}")
        
    } catch (e: Exception) {
        println("错误：${e.message}")
        e.printStackTrace()
    } finally {
        sdk.close()
    }
}
```

## 🔐 认证方式说明

SDK 支持两种认证头，根据初始化方式自动选择：

| 初始化方式 | 认证头 | 适用场景 |
|-----------|--------|---------|
| `apiKey` | `X-Api-Key` | 已有 apiKey 的简单调用 |
| `appId` + `appSecret` | `X-Api-Key`（优先） | 签名认证，支持自动刷新 |
| OAuth | `Authorization: Bearer` | 需要用户授权 |
| `accessToken` | `Authorization: Bearer` | 已有访问令牌 |

**重要说明**：
- apiKey 和 accessToken 是签名交换接口返回的两个不同的值
- apiKey 用于 `X-Api-Key` 头
- accessToken 用于 `Authorization: Bearer` 头
- SDK 自动选择合适的认证头

## ⚙️ 配置选项

使用 `SdkConfig` 自定义 SDK 行为：

```kotlin
val config = SdkConfig.builder()
    .apiKey("your-api-key")
    .baseUrl("https://open-global.vdsentnet.com")
    .requestTimeout(60000)
    .logLevel(LogLevel.DEBUG)
    .enableRetry(true)
    .maxRetries(3)
    .build()

val sdk = FursuitTvSdk(config)
```

### 常用配置项

| 配置项 | 默认值 | 说明 |
|--------|--------|------|
| `baseUrl` | `https://open-global.vdsentnet.com` | API 基础 URL |
| `requestTimeout` | 30000 | 请求超时（毫秒） |
| `logLevel` | `LogLevel.INFO` | 日志级别 |
| `enableRetry` | `true` | 启用重试 |
| `maxRetries` | 3 | 最大重试次数 |

## ❌ 错误处理

```kotlin
try {
    val userProfile = sdk.user.getUserProfile("username")
} catch (e: TokenExpiredException) {
    // 令牌过期，会自动刷新
    println("令牌过期")
} catch (e: AuthenticationException) {
    // 认证失败
    println("认证失败：${e.message}")
} catch (e: NotFoundException) {
    // 资源不存在
    println("用户不存在")
} catch (e: NetworkException) {
    // 网络错误
    println("网络连接失败")
} catch (e: ApiException) {
    // API 错误
    println("API 错误：${e.statusCode}")
} catch (e: Exception) {
    // 其他错误
    println("未知错误：${e.message}")
}
```

## 🔄 自动令牌刷新

使用 `appId + appSecret` 初始化时，SDK 会自动管理令牌刷新：

```kotlin
val sdk = FursuitTvSdk(appId = appId, appSecret = appSecret)

// SDK 会在以下情况自动刷新令牌：
// 1. 令牌剩余有效期 <= 300 秒（5 分钟）
// 2. 每次 API 调用前自动检查
// 3. 刷新失败会自动回退到重新获取

// 也可以手动获取有效令牌
val token = sdk.auth.getValidAccessToken(appId, appSecret)
```

## 📚 下一步

- 查看 [API 最佳实践](BEST_PRACTICES.md) 了解更多使用技巧
- 查看 [故障排除](TROUBLESHOOTING.md) 解决常见问题
- 查看 [平台指南](PLATFORM_GUIDE.md) 了解特定平台配置
- 参考 [完整 API 文档](api/) 查看所有接口

## 💡 提示

1. **开发环境**：使用 `LogLevel.DEBUG` 查看详细请求信息
2. **生产环境**：使用 `LogLevel.INFO` 或 `LogLevel.ERROR`
3. **令牌管理**：推荐使用 `appId + appSecret` 方式，自动处理刷新
4. **错误重试**：启用重试机制提高稳定性
5. **资源释放**：使用完毕后调用 `sdk.close()` 释放资源
