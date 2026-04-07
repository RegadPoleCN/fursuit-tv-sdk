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
    implementation("me.regadpole:furtv-sdk:1.0.0")
}
```

## 快速开始

### 初始化 SDK

SDK 提供四种初始化方式，适用于不同的使用场景：

#### 方式 1: 使用 apiKey（适用于已有 apiKey 的用户）

```kotlin
// 使用默认配置
val sdk = FursuitTvSdk(apiKey = "your-api-key")

// 使用自定义配置
val config = SdkConfig(
    apiKey = "your-api-key",
    baseUrl = "https://open-global.vdsentnet.com",
    requestTimeout = 60000,
    logLevel = LogLevel.DEBUG
)
val sdk = FursuitTvSdk(config)
```

**认证头**: `X-Api-Key: your-api-key`

**适用场景**: 已有 VDS 颁发的 apiKey，适用于简单的服务端到服务端调用。

#### 方式 2: 使用 clientId + clientSecret（推荐，适用于新用户）

```kotlin
// 使用 clientId 和 clientSecret 初始化
val sdk = FursuitTvSdk(
    clientId = "your-client-id",
    clientSecret = "your-client-secret"
)

// 获取初始令牌
runBlocking {
    sdk.auth.exchangeToken(clientId, clientSecret)
}

// SDK 会自动管理令牌刷新（当剩余有效期 <= 300 秒时自动刷新）
```

**认证头**: `Authorization: Bearer <access_token>`

**适用场景**: 新用户，需要完整的 OAuth 2.0 客户端凭证流程。SDK 会自动调用 token exchange 获取初始令牌，并在令牌即将过期时自动刷新。

#### 方式 3: OAuth 本地回调（适用于需要用户授权的应用）

```kotlin
// 配置 OAuth 回调服务器
val config = OAuthConfig(
    callbackHost = "localhost",
    callbackPort = 8080,
    callbackPath = "/callback"
)

// 使用 OAuth 初始化 SDK
val sdk = FursuitTvSdk.initWithOAuth("your-app-id", config)

// 启动 OAuth 授权流程
runBlocking {
    val result = sdk.auth.initiateOAuthFlow("your-app-id", config)
    // result.code 包含授权码，可用于交换令牌
}
```

**认证头**: `Authorization: Bearer <oauth-token>`

**适用场景**: 需要用户登录并授权的应用场景。SDK 会启动本地回调服务器，自动接收 OAuth 授权回调并提取授权码。

**OAuth 本地回调自动授权流程**:
1. SDK 启动本地 HTTP 服务器监听回调端口
2. 生成随机 state 参数防止 CSRF 攻击
3. 打开浏览器引导用户访问授权页面
4. 用户登录并同意授权
5. 授权成功后重定向到本地回调地址
6. SDK 接收回调并提取授权码
7. 自动关闭本地服务器
8. 使用授权码交换访问令牌

**🌍 跨平台 OAuth 支持说明：**

SDK 的 OAuth 实现在不同平台上有不同的行为：

- **JVM 平台**：自动打开系统浏览器，本地回调服务器监听授权回调
- **JS 平台（Node.js）**：需手动打开授权 URL，本地 HTTP 服务器接收回调
- **Native 平台**：使用平台特定的 URL 打开机制，支持自定义 URL Scheme

详细实现和平台差异请查看 [跨平台 OAuth 实现](oauth.md)。

#### 方式 4: 使用 access_token（适用于已有访问令牌的用户）

```kotlin
// 直接使用已有的 access_token 初始化
val sdk = FursuitTvSdk(
    accessToken = "your-access-token",
    baseUrl = "https://open-global.vdsentnet.com"
)
```

**认证头**: `Authorization: Bearer <access_token>`

**适用场景**: 已有通过其他方式获取的访问令牌（例如从持久化存储中恢复）。

### 认证

#### 交换令牌

```kotlin
// 使用 clientId 和 clientSecret 交换令牌
val tokenInfo = sdk.auth.exchangeToken(
    clientId = "your-client-id",
    clientSecret = "your-client-secret"
)
```

#### 刷新令牌

```kotlin
// 刷新令牌（当令牌剩余有效期 <= 300 秒时自动触发）
val refreshedToken = sdk.auth.refreshToken()
```

#### OAuth 授权码交换

```kotlin
// 使用授权码交换令牌（OAuth 模式）
val tokenInfo = sdk.auth.exchangeCode(
    code = "authorization-code",
    redirectUri = "your-redirect-uri"
)
```

#### 检查认证状态

```kotlin
val isAuthenticated = sdk.auth.isAuthenticated()
```

### 认证头说明

SDK 根据初始化方式自动选择认证头格式：

| 初始化方式 | 认证头格式 | 说明 |
|-----------|-----------|------|
| `apiKey` | `X-Api-Key: your-api-key` | 适用于简单的 API 密钥认证 |
| `clientId` + `clientSecret` | `Authorization: Bearer <access_token>` | 适用于 OAuth 2.0 客户端凭证流程 |
| OAuth 本地回调 | `Authorization: Bearer <oauth-token>` | 适用于需要用户授权的 OAuth 2.0 授权码模式 |
| `accessToken` | `Authorization: Bearer <access_token>` | 适用于已有访问令牌的场景 |

**X-Api-Key 头**：
- 使用 `X-Api-Key: your-api-key` 请求头
- 适用于服务端到服务端调用
- 通过 `apiKey` 参数或 `SdkConfig.apiKey` 配置

**Authorization: Bearer 头**：
- 使用 `Authorization: Bearer access_token` 请求头
- OAuth 2.0 标准认证头格式，符合 RFC 6750 规范
- 通过 OAuth 流程、clientId+clientSecret 或直接传入 accessToken 后自动使用

### 调用 API

```kotlin
// 获取用户资料
val userProfile = sdk.user.getUserProfile("username")

// 获取热门推荐
val popularUsers = sdk.search.getPopular()

// 获取聚会列表
val gatherings = sdk.gathering.getGatheringMonthly(2024, 12)

// 搜索学校
val schools = sdk.school.searchSchools("北京大学")
```

### 关闭 SDK

```kotlin
sdk.close()
```

## API 文档

- [基础 API](api/base.md)
- [用户 API](api/user.md)
- [搜索 API](api/search.md)
- [聚会 API](api/gathering.md)
- [学校 API](api/school.md)

## 配置

SDK 提供了丰富的配置选项，通过 `SdkConfig` 类进行设置：

- `baseUrl`: API 基础 URL，默认为 "https://open-global.vdsentnet.com"
- `apiKey`: API 密钥，必须设置
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
    val sdk = FursuitTvSdk(apiKey = "your-api-key")
    
    try {
        // 认证
        val tokenInfo = sdk.auth.exchangeToken("client-id", "client-secret")
        
        // 获取用户资料
        val userProfile = sdk.user.getUserProfile("username")
        
        // 获取热门推荐
        val popular = sdk.search.getPopular()
        
    } catch (e: FursuitTvSdkException) {
        println("错误: ${e.message}")
    } finally {
        sdk.close()
    }
}
```
