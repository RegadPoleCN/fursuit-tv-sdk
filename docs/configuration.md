# 配置指南

## SdkConfig 类

`SdkConfig` 类是 SDK 的核心配置类，用于统一管理 SDK 的各种配置选项。

### 构造函数参数

| 参数 | 类型 | 默认值 | 说明 |
|------|------|--------|------|
| `baseUrl` | String | "https://open-global.vdsentnet.com" | API 基础 URL |
| `apiKey` | String | 必填 | API 密钥（使用 apiKey 初始化时必需） |
| `requestTimeout` | Long | 30000 | 请求超时时间（毫秒） |
| `connectTimeout` | Long | 10000 | 连接超时时间（毫秒） |
| `socketTimeout` | Long | 30000 | 套接字超时时间（毫秒） |
| `logLevel` | LogLevel | LogLevel.INFO | 日志级别 |
| `enableRetry` | Boolean | true | 是否启用重试机制 |
| `maxRetries` | Int | 3 | 最大重试次数 |
| `retryInterval` | Long | 1000 | 重试间隔（毫秒） |

### LogLevel 枚举

| 枚举值 | 说明 |
|--------|------|
| `LogLevel.NONE` | 禁用日志 |
| `LogLevel.ERROR` | 仅错误日志 |
| `LogLevel.WARNING` | 警告和错误日志 |
| `LogLevel.INFO` | 信息、警告和错误日志 |
| `LogLevel.DEBUG` | 所有日志（包括调试信息） |

## OAuth 配置

### OAuthConfig 参数说明

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
| `stateTimeoutMinutes` | Int | 10 | state 参数超时时间（分钟），用于防止 CSRF 攻击 |
| `enablePkce` | Boolean | false | 是否启用 PKCE（Proof Key for Code Exchange）增强安全性 |

### 跨平台 OAuth 配置差异

不同平台上的 OAuth 配置可能有一些特殊考虑：

#### JVM 平台配置

```kotlin
val config = OAuthConfig(
    callbackHost = "localhost",
    callbackPort = 8080,
    callbackPath = "/callback",
    stateTimeoutMinutes = 10,
    enablePkce = true  // 推荐使用 PKCE 增强安全性
)
```

- 自动打开系统默认浏览器
- 本地 HTTP 服务器监听 `http://localhost:8080/callback`
- 授权完成后自动关闭回调服务器

#### JS 平台（Node.js）配置

```kotlin
val config = OAuthConfig(
    callbackHost = "127.0.0.1",  // 使用 IP 地址更稳定
    callbackPort = 3000,         // 避免端口冲突
    callbackPath = "/oauth/callback",
    stateTimeoutMinutes = 15     // 给手动打开浏览器留更多时间
)
```

- 需要手动打开授权 URL
- 本地 HTTP 服务器接收回调
- 建议使用较长的超时时间

#### Native 平台配置

```kotlin
val config = OAuthConfig(
    callbackHost = "localhost",
    callbackPort = 0,  // 使用随机可用端口
    callbackPath = "/callback",
    enablePkce = true
)
```

- 支持自定义 URL Scheme（如 `myapp://oauth/callback`）
- 使用平台特定的 URL 打开机制
- iOS/macOS 可使用 Universal Links

### OAuth 2.0 客户端凭证流程

SDK 支持 OAuth 2.0 客户端凭证流程进行身份验证。使用 `clientId` 和 `clientSecret` 获取访问令牌。

```kotlin
val sdk = FursuitTvSdk(
    clientId = "your-client-id",
    clientSecret = "your-client-secret"
)

// 获取初始令牌
runBlocking {
    sdk.auth.exchangeToken(
        clientId = "your-client-id",
        clientSecret = "your-client-secret"
    )
}
```

### OAuth 回调服务器配置

OAuth 本地回调功能会启动一个本地 HTTP 服务器来接收 OAuth 授权回调。

**配置示例**：

```kotlin
// 基本配置
val config = OAuthConfig(
    callbackHost = "localhost",
    callbackPort = 8080,
    callbackPath = "/callback"
)

// 自定义回调端口和路径
val customConfig = OAuthConfig(
    callbackHost = "127.0.0.1",
    callbackPort = 9000,
    callbackPath = "/oauth/callback",
    stateTimeoutMinutes = 15,
    enablePkce = true
)

// 使用配置初始化 SDK
val sdk = FursuitTvSdk.initWithOAuth("your-app-id", config)
```

**回调 URL 格式**：

完整的回调 URL 由 `callbackHost`、`callbackPort` 和 `callbackPath` 组成：
```
http://localhost:8080/callback
```

**注意事项**：
1. 确保回调端口未被占用
2. 回调 URL 需要与在 Fursuit.TV 开发者平台注册的回调地址一致
3. `stateTimeoutMinutes` 应根据用户授权流程的预计时间设置
4. 启用 PKCE 可以提供更高的安全性，推荐在生产环境中使用

### OAuth 2.0 客户端凭证流程

SDK 支持 OAuth 2.0 客户端凭证流程进行身份验证。使用 `clientId` 和 `clientSecret` 获取访问令牌。

```kotlin
val sdk = FursuitTvSdk(
    clientId = "your-client-id",
    clientSecret = "your-client-secret"
)

// 获取初始令牌
runBlocking {
    sdk.auth.exchangeToken(
        clientId = "your-client-id",
        clientSecret = "your-client-secret"
    )
}
```

### 令牌自动管理

当使用 `clientId` 和 `clientSecret` 初始化 SDK 时，令牌会在 401 错误时自动刷新，无需手动干预。

## 配置示例

### 基本配置（使用 apiKey）

```kotlin
val config = SdkConfig(
    apiKey = "your-api-key"
)
```

### 基本配置（使用 clientId + clientSecret）

```kotlin
// 直接使用 clientId 和 clientSecret 初始化
val sdk = FursuitTvSdk(
    clientId = "your-client-id",
    clientSecret = "your-client-secret"
)
```

### 自定义超时设置

```kotlin
val config = SdkConfig(
    apiKey = "your-api-key",
    requestTimeout = 60000, // 60秒
    connectTimeout = 15000,  // 15秒
    socketTimeout = 45000    // 45秒
)
```

### 自定义日志级别

```kotlin
val config = SdkConfig(
    apiKey = "your-api-key",
    logLevel = LogLevel.DEBUG // 启用调试日志
)
```

### 自定义重试设置

```kotlin
val config = SdkConfig(
    apiKey = "your-api-key",
    enableRetry = true,
    maxRetries = 5,         // 最多重试5次
    retryInterval = 2000     // 每次重试间隔2秒
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

## 认证头配置

### X-Api-Key vs Authorization: Bearer

SDK 支持两种认证头方式，根据初始化方式自动选择：

#### 使用 X-Api-Key 头

当使用 `apiKey` 参数初始化 SDK 时，所有请求会自动添加 `X-Api-Key` 头：

```kotlin
// 使用 apiKey 初始化
val sdk = FursuitTvSdk(apiKey = "your-api-key")
// 请求头：X-Api-Key: your-api-key
```

#### 使用 Authorization: Bearer 头

当使用以下方式初始化 SDK 时，所有请求会自动添加 `Authorization: Bearer` 头：

```kotlin
// 方式 1: 使用 clientId + clientSecret 初始化
val sdk = FursuitTvSdk(
    clientId = "your-client-id",
    clientSecret = "your-client-secret"
)
// 请求头：Authorization: Bearer <access_token>

// 方式 2: 使用 OAuth 本地回调
val config = OAuthConfig(callbackHost = "localhost", callbackPort = 8080)
val sdk = FursuitTvSdk.initWithOAuth("your-app-id", config)
// 请求头：Authorization: Bearer <oauth-token>

// 方式 3: 直接使用 accessToken
val sdk = FursuitTvSdk(accessToken = "your-access-token")
// 请求头：Authorization: Bearer <access_token>
```

### 认证头选择逻辑

| 初始化方式 | 认证头 | 说明 |
|-----------|--------|------|
| `apiKey` | `X-Api-Key` | 适用于简单的 API 密钥认证 |
| `clientId` + `clientSecret` | `Authorization: Bearer` | 适用于 OAuth 2.0 客户端凭证流程，支持令牌自动刷新 |
| OAuth 本地回调 | `Authorization: Bearer` | 适用于 OAuth 2.0 授权码模式，需要用户授权 |
| `accessToken` | `Authorization: Bearer` | 适用于已有访问令牌的场景 |

## 使用配置

### 初始化 SDK 时使用配置

```kotlin
val config = SdkConfig(apiKey = "your-api-key")
val sdk = FursuitTvSdk(config)
```

### 获取当前配置

```kotlin
val currentConfig = sdk.getConfig()
println("Current base URL: ${currentConfig.baseUrl}")
println("Current log level: ${currentConfig.logLevel}")
```

## 配置最佳实践

1. **API 密钥安全**：不要硬编码 API 密钥，应该从安全的配置管理系统中获取。

2. **认证头选择**：
   - 使用 `apiKey` 初始化时，SDK 使用 `X-Api-Key` 头，适用于简单的 API 密钥认证场景
   - 使用 `clientId` + `clientSecret` 初始化时，SDK 使用 `Authorization: Bearer` 头，适用于 OAuth 2.0 流程，支持令牌自动刷新
   - 推荐使用 OAuth 2.0 方式（clientId + clientSecret），提供更安全的令牌管理机制

3. **超时设置**：根据网络环境调整超时设置。在较差的网络环境下，建议增加超时时间。

4. **日志级别**：在开发环境中使用 `LogLevel.DEBUG`，在生产环境中使用 `LogLevel.INFO` 或 `LogLevel.ERROR`。

5. **重试设置**：对于不稳定的网络环境，启用重试机制可以提高请求成功率。

6. **Base URL**：使用 VDS 官方 API 时，应设置为 `https://open-global.vdsentnet.com`。

7. **自动令牌刷新**：使用 `clientId` + `clientSecret` 初始化时，SDK 会在收到 401 错误时自动刷新令牌，无需手动处理。
