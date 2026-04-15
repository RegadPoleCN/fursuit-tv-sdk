# Fursuit.TV SDK 开发者指南

快速开始使用 Fursuit.TV SDK，5 分钟内上手。

## 📦 安装

### Gradle (Kotlin DSL)

```kotlin
repositories {
    mavenCentral()
}

dependencies {
    implementation("com.furrist.rp:fursuit-tv-sdk:1.0-SNAPSHOT")
}
```

### Gradle (Groovy)

```groovy
repositories {
    mavenCentral()
}

dependencies {
    implementation 'com.furrist.rp:fursuit-tv-sdk:1.0-SNAPSHOT'
}
```

### Maven

```xml
<dependency>
    <groupId>com.furrist.rp</groupId>
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
val sdk = FursuitTvSdk()
val oauthResult = sdk.auth.initWithOAuth("vap_xxxxxxxxxxxxxxxx", config)
```

#### 方式 4: 使用 accessToken

适用于已有访问令牌：

```kotlin
val sdk = FursuitTvSdk(accessToken = "your-access-token")
```

**详细说明**：查看 [认证与配置](authentication.md) 了解所有认证方式和配置选项。

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
import com.furrist.rp.furtv.sdk.FursuitTvSdk
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

## 📚 下一步

- **[API 参考](api/)** - 查看所有 API 模块的详细文档
- **[认证与配置](authentication.md)** - 认证方式和配置详解
- **[最佳实践](BEST_PRACTICES.md) - API 使用技巧
- **[故障排除](TROUBLESHOOTING.md)** - 常见问题
- **[平台指南](PLATFORM_GUIDE.md)** - 特定平台配置
- **[OAuth 指南](oauth.md)** - OAuth 2.0 流程详解

## 💡 提示

1. **开发环境**：使用 `LogLevel.DEBUG` 查看详细请求信息
2. **生产环境**：使用 `LogLevel.INFO` 或 `LogLevel.ERROR`
3. **令牌管理**：推荐使用 `appId + appSecret` 方式，自动处理刷新
4. **错误重试**：启用重试机制提高稳定性
5. **资源释放**：使用完毕后调用 `sdk.close()` 释放资源
