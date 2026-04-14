# 认证与配置指南

本文档详细说明 Fursuit.TV SDK 的认证方式和配置选项。

## 认证方式

SDK 支持四种认证方式，根据初始化方式自动选择合适的认证头：

### 1. 使用 apiKey（最简单）

**认证头**: `X-Api-Key: your-api-key`

**适用场景**: 已有 apiKey 的简单调用

```kotlin
val sdk = FursuitTvSdk(apiKey = "your-api-key")
```

### 2. 使用 clientId + clientSecret（推荐）

**认证头**: `Authorization: Bearer <accessToken>`（优先）或 `X-Api-Key: <apiKey>`

**适用场景**: 服务端应用，需要长期访问 API，支持自动令牌刷新

```kotlin
val sdk = FursuitTvSdk(
    clientId = "vap_xxxxxxxxxxxxxxxx",
    clientSecret = "your-client-secret"
)

// 获取初始令牌
runBlocking {
    sdk.auth.exchangeToken(clientId, clientSecret)
}

// SDK 会自动管理令牌刷新（当剩余有效期 <= 300 秒时）
```

### 3. OAuth 2.0（需要用户授权）

**认证头**: `Authorization: Bearer <oauth-token>`

**适用场景**: 桌面应用、移动应用、Web 应用等需要用户登录和授权的场景

```kotlin
// 第一步：初始化 SDK（传入 clientId 和 clientSecret）
val sdk = FursuitTvSdk(
    clientId = "vap_xxxxxxxxxxxxxxxx",
    clientSecret = "your-client-secret"
)

// 第二步：配置 OAuth 回调
val config = OAuthConfig(
    callbackHost = "localhost",
    callbackPort = 8080,
    callbackPath = "/callback"
)

// 第三步：启动 OAuth 授权（自动使用 SDK 配置中的 clientId 和 clientSecret）
val oauthResult = sdk.auth.initOAuth(config = config)
```

**注意**: OAuth 流程从 SDK 配置自动获取 clientId 和 clientSecret，无需在方法调用时传入。

### 4. 使用 accessToken 直接初始化

**认证头**: `Authorization: Bearer <accessToken>`

**适用场景**: 已有 access_token 的场景，例如从本地存储中读取之前保存的令牌

```kotlin
val sdk = FursuitTvSdk(accessToken = "your-access-token")
```

## 认证头说明

| 初始化方式 | 认证头 | 说明 |
|-----------|--------|------|
| `apiKey` | `X-Api-Key` | 简单的 API 密钥认证 |
| `clientId` + `clientSecret` | `Authorization: Bearer` | 签名认证，支持自动刷新 |
| OAuth | `Authorization: Bearer` | OAuth 2.0 授权码模式 |
| `accessToken` | `Authorization: Bearer` | 已有访问令牌 |

**重要说明**:
- apiKey 和 accessToken 是签名交换接口返回的两个不同的值
- apiKey 用于 `X-Api-Key` 头
- accessToken 用于 `Authorization: Bearer` 头
- SDK 自动选择合适的认证头

## 服务器端点

SDK 默认使用 VDS 官方推荐的全球端点：

```
https://open-global.vdsentnet.com
```

### 可选端点

| 端点 | 地区 | 服务提供商 |
|------|------|------------|
| `https://open-global.vdsentnet.com` | 全球 | 默认推荐，节点优选自动匹配 |
| `https://open-cn1.vdsentnet.com` | 中国上海 | Harry |

### 自定义端点

```kotlin
val config = SdkConfig.builder()
    .apiKey("your-api-key")
    .baseUrl("https://open-cn1.vdsentnet.com")  // 选择更适合的端点
    .build()
val sdk = FursuitTvSdk(config)
```

## 配置选项

### SdkConfig 类

通过 `SdkConfig` 类配置 SDK 行为：

```kotlin
val config = SdkConfig(
    // 必需配置
    apiKey = "your-api-key",  // 或使用其他认证方式
    
    // 可选配置
    baseUrl = "https://open-global.vdsentnet.com",  // API 基础 URL
    requestTimeout = 60000,                     // 请求超时（毫秒）
    logLevel = LogLevel.DEBUG,                  // 日志级别
    enableRetry = true,                         // 启用重试机制
    maxRetries = 3                              // 最大重试次数
)

val sdk = FursuitTvSdk(config)
```

### 常用配置项

| 配置项 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| `baseUrl` | String | `https://open-global.vdsentnet.com` | API 基础 URL |
| `requestTimeout` | Long | 30000 | 请求超时时间（毫秒） |
| `logLevel` | LogLevel | `LogLevel.INFO` | 日志级别 |
| `enableRetry` | Boolean | true | 是否启用重试机制 |
| `maxRetries` | Int | 3 | 最大重试次数 |

### LogLevel 枚举

| 枚举值 | 说明 |
|--------|------|
| `LogLevel.NONE` | 禁用日志 |
| `LogLevel.ERROR` | 仅错误日志 |
| `LogLevel.WARNING` | 警告和错误日志 |
| `LogLevel.INFO` | 信息、警告和错误日志 |
| `LogLevel.DEBUG` | 所有日志（包括调试信息） |

### OAuth 配置

`OAuthConfig` 用于配置 OAuth 本地回调服务器的参数：

```kotlin
public data class OAuthConfig(
    public val callbackHost: String = "localhost",
    public val callbackPort: Int = 8080,
    public val callbackPath: String = "/callback",
    public val stateTimeoutMinutes: Int = 10,
    public val enablePkce: Boolean = false
)
```

**参数说明**：

| 参数 | 类型 | 默认值 | 说明 |
|------|------|--------|------|
| `callbackHost` | String | "localhost" | 回调服务器监听的主机地址 |
| `callbackPort` | Int | 8080 | 回调服务器监听的端口号 |
| `callbackPath` | String | "/callback" | 回调处理的路径 |
| `stateTimeoutMinutes` | Int | 10 | state 参数超时时间（分钟） |
| `enablePkce` | Boolean | false | 是否启用 PKCE 增强安全性 |

## 自动令牌刷新

当使用 `appId` 和 `appSecret` 初始化 SDK 时，SDK 会自动管理令牌的刷新：

1. 初始调用 `exchangeToken()` 获取访问令牌
2. 当令牌剩余有效期 <= 300 秒（5 分钟）时，自动调用 `refreshToken()` 刷新
3. 如果刷新失败，会自动回退到 `exchangeToken()` 重新获取

```kotlin
val sdk = FursuitTvSdk(
    appId = "vap_xxxxxxxxxxxxxxxx",
    appSecret = "your-app-secret"
)

// 首次获取令牌
runBlocking {
    sdk.auth.exchangeToken(appId, appSecret)
}

// 后续 API 调用会自动检查并刷新令牌
val userProfile = sdk.user.getUserProfile("username")
```

## 配置示例

### 基本配置（使用 apiKey）

```kotlin
val config = SdkConfig(apiKey = "your-api-key")
val sdk = FursuitTvSdk(config)
```

### 基本配置（使用 appId + appSecret）

```kotlin
val sdk = FursuitTvSdk(
    appId = "vap_xxxxxxxxxxxxxxxx",
    appSecret = "your-app-secret"
)
```

### 自定义超时设置

```kotlin
val config = SdkConfig(
    apiKey = "your-api-key",
    requestTimeout = 60000, // 60 秒
    connectTimeout = 15000,  // 15 秒
    socketTimeout = 45000    // 45 秒
)
```

### 自定义日志级别

```kotlin
val config = SdkConfig(
    apiKey = "your-api-key",
    logLevel = LogLevel.DEBUG // 启用调试日志
)
```

### 完全自定义配置

```kotlin
val config = SdkConfig(
    baseUrl = "https://open-global.vdsentnet.com",
    apiKey = "your-api-key",
    requestTimeout = 60000,
    connectTimeout = 10000,
    socketTimeout = 30000,
    logLevel = LogLevel.DEBUG,
    enableRetry = true,
    maxRetries = 3,
    retryInterval = 1000
)
```

## 最佳实践

1. **API 密钥安全**: 不要硬编码 API 密钥，应该从安全的配置管理系统中获取

2. **认证方式选择**:
   - 使用 `apiKey` 初始化时，SDK 使用 `X-Api-Key` 头，适用于简单的 API 密钥认证场景
   - 使用 `appId` + `appSecret` 初始化时，SDK 使用 `Authorization: Bearer` 头，适用于签名认证流程，支持令牌自动刷新
   - 使用 OAuth 本地回调时，SDK 使用 `Authorization: Bearer` 头，适用于需要用户授权的场景
   - 推荐使用签名认证方式（appId + appSecret），提供更安全的令牌管理机制

3. **超时设置**: 根据网络环境调整超时设置。在较差的网络环境下，建议增加超时时间

4. **日志级别**: 在开发环境中使用 `LogLevel.DEBUG`，在生产环境中使用 `LogLevel.INFO` 或 `LogLevel.ERROR`

5. **重试设置**: 对于不稳定的网络环境，启用重试机制可以提高请求成功率

6. **自动令牌刷新**: 使用 `appId` + `appSecret` 或 OAuth 本地回调初始化时，SDK 会自动管理令牌刷新（剩余有效期 <= 300 秒时触发），无需手动处理

7. **PKCE 安全**: 在生产环境中使用 OAuth 时，建议启用 PKCE（Proof Key for Code Exchange）以提供更高的安全性

## 相关文档

- [开发者指南](DEVELOPER_GUIDE.md) - 快速上手
- [OAuth 实现指南](oauth.md) - OAuth 流程详解
- [平台指南](PLATFORM_GUIDE.md) - 特定平台配置
- [故障排除](TROUBLESHOOTING.md) - 常见问题
