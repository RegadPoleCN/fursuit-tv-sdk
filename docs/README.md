# Fursuit.TV SDK

Fursuit.TV SDK 是一个跨平台的 Kotlin SDK，用于与 Fursuit.TV API 进行交互。它支持 Kotlin/JVM、Kotlin/JS 和 Kotlin/Native 等平台。

## 功能特点

- 跨平台支持：支持 JVM、JS 和 Native 平台
- 统一的配置系统：通过 `SdkConfig` 类统一管理 SDK 配置
- 完整的 API 覆盖：包含所有 Fursuit.TV API 端点
- 自动重试机制：支持网络请求失败时的自动重试
- 完善的错误处理：提供详细的错误类型和错误信息
- 简洁的 API 设计：使用 Kotlin 协程和挂起函数，提供简洁的 API 调用方式

## 安装

### Gradle

在项目的 `build.gradle.kts` 文件中添加以下依赖：

```kotlin
repositories {
    mavenCentral()
}

dependencies {
    implementation("com.furrist.rp:fursuit-tv-sdk:0.1.0")
}
```

## 快速开始

### 初始化 SDK

SDK 提供多种初始化方式，适用于不同的使用场景：

#### 方式 1: 使用 apiKey（Client 认证）

```kotlin
// 使用默认配置
val sdk = FursuitTvSdk(apiKey = "your-api-key")

// 使用自定义配置
val config = SdkConfig.builder()
    .apiKey("your-api-key")
    .baseUrl("https://open-global.vdsentnet.com")
    .build()
val sdk = FursuitTvSdk(config)
```

**认证头**: `X-Api-Key: your-api-key`

**适用场景**：应用级 API，简单的服务端到服务端调用

#### 方式 2: 使用 appId + appSecret（推荐，Client 认证）

```kotlin
// 使用 appId 和 appSecret 初始化
val sdk = FursuitTvSdk(
    appId = "vap_xxxxxxxxxxxxxxxx",
    appSecret = "your-app-secret"
)

// 获取初始令牌
runBlocking {
    sdk.auth.exchangeToken(appId, appSecret)
}

// SDK 会自动管理令牌刷新（当剩余有效期 <= 300 秒时自动刷新）
```

**认证头**: `X-Api-Key: <apiKey>`（优先）或 `Authorization: Bearer <accessToken>`

**适用场景**：应用级 API，支持令牌自动刷新

#### 方式 3: 使用 accessToken（Client 认证）

```kotlin
// 直接使用已有的 accessToken 初始化
val sdk = FursuitTvSdk(
    accessToken = "your-access-token",
    baseUrl = "https://open-global.vdsentnet.com"
)
```

**认证头**: `Authorization: Bearer <accessToken>`

**适用场景**：应用级 API，适用于已有访问令牌的用户

#### 方式 4: 使用 OAuth 认证（OAuth 2.0 授权码模式）

```kotlin
// 配置 OAuth 回调服务器
val config = OAuthConfig(
    callbackHost = "localhost",
    callbackPort = 8080,
    callbackPath = "/callback"
)

// 第一步：先通过签名交换获取 Client accessToken
val clientSdk = FursuitTvSdk(appId = "vap_xxxxxxxxxxxxxxxx", appSecret = "your-app-secret")
runBlocking {
    clientSdk.auth.exchangeToken("vap_xxxxxxxxxxxxxxxx", "your-app-secret")

    // 第二步：使用 OAuth 获取用户授权
    val oauthSdk = FursuitTvSdk.initWithOAuth("vap_xxxxxxxxxxxxxxxx", config)

    // 第三步：调用 UserInfo 接口
    val userInfo = oauthSdk.user.getUserProfile("username")
}
```

**认证头**: `Authorization: Bearer <oauth-token>`

**适用场景**：用户授权场景（仅可用于 UserInfo 接口）

**注意**：OAuth token 和 Client token 不通用，不能混用

### 认证

#### 交换令牌

```kotlin
// 使用 appId 和 appSecret 交换令牌（签名交换）
val tokenInfo = sdk.auth.exchangeToken(
    appId = "vap_xxxxxxxxxxxxxxxx",
    appSecret = "your-app-secret"
)
```

#### 刷新令牌

```kotlin
// 刷新令牌（当令牌剩余有效期 <= 300 秒时自动触发）
val refreshedToken = sdk.auth.refreshToken()
```

#### 检查认证状态

```kotlin
val isAuthenticated = sdk.auth.isAuthenticated()
```

### 调用 API

```kotlin
// 获取用户资料
val userProfile = sdk.user.getUserProfile("username")

// 获取热门推荐
val popularUsers = sdk.search.getPopularDiscovery()

// 获取聚会列表
val gatherings = sdk.gathering.getMonthly(2024, 12)

// 搜索学校
val schools = sdk.school.searchSchools("北京大学")
```

### 关闭 SDK

```kotlin
sdk.close()
```

## API 模块

SDK 提供以下 API 模块：

- **base**: 基础接口 API，提供健康检查、版本检查、主题包等基础功能
- **user**: 用户相关 API，提供用户资料、关系、访客、徽章、商店等功能
- **search**: 搜索和发现 API，提供热门推荐、随机推荐、搜索、物种查询等功能
- **gathering**: 聚会相关 API，提供聚会列表、统计、详情、报名等功能
- **school**: 学校和角色 API，提供学校信息、角色管理等功能

## 配置

SDK 提供了丰富的配置选项，通过 `SdkConfig` 类进行设置：

- `baseUrl`: API 基础 URL，默认为 "https://open-global.vdsentnet.com"
- `apiKey`: API 密钥
- `requestTimeout`: 请求超时时间，默认为 30000 毫秒
- `connectTimeout`: 连接超时时间，默认为 10000 毫秒
- `socketTimeout`: 套接字超时时间，默认为 30000 毫秒
- `logLevel`: 日志级别，默认为 `LogLevel.INFO`
- `enableRetry`: 是否启用重试机制，默认为 true
- `maxRetries`: 最大重试次数，默认为 3
- `retryInterval`: 重试间隔，默认为 1000 毫秒

## 错误处理

SDK 提供了以下错误类型：

- `FursuitTvSdkException`: 基础异常类
- `ApiException`: API 调用异常
- `NetworkException`: 网络连接异常
- `TokenExpiredException`: 令牌过期异常
- `AuthenticationException`: 认证异常
- `ValidationException`: 参数验证异常
- `NotFoundException`: 未找到资源异常

## 示例

### 完整示例

```kotlin
fun main() = runBlocking {
    val sdk = FursuitTvSdk(appId = "vap_xxxxxxxxxxxxxxxx", appSecret = "your-app-secret")

    try {
        // 认证（签名交换）
        val tokenInfo = sdk.auth.exchangeToken("vap_xxxxxxxxxxxxxxxx", "your-app-secret")

        // 获取用户资料
        val userProfile = sdk.user.getUserProfile("username")

        // 获取热门推荐
        val popular = sdk.search.getPopularDiscovery()

    } catch (e: FursuitTvSdkException) {
        println("错误：${e.message}")
    } finally {
        sdk.close()
    }
}
```
