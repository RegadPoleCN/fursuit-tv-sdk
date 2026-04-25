# 配置选项

本文档详细说明 Fursuit.TV SDK 的所有配置选项。

## 核心配置参数

### 认证配置

| 参数 | 类型 | 默认值 | 必填 | 说明 |
|------|------|--------|------|------|
| `clientId` | `String?` | `null` | 条件 | 应用 ID，格式为 `vap_xxxx` |
| `clientSecret` | `String?` | `null` | 条件 | 应用密钥 |
| `apiKey` | `String?` | `null` | 条件 | VDS 颁发的 API 密钥 |

**说明**:
- `clientId` + `clientSecret` 与 `apiKey` 二选一
- 签名交换：使用 `clientId` + `clientSecret`
- 已有密钥：使用 `apiKey`

### 网络配置

| 参数 | 类型 | 默认值 | 说明 |
|------|------|--------|------|
| `baseUrl` | `String` | `"https://open-global.vdsentnet.com"` | API 基础 URL |
| `requestTimeout` | `Long` | `30000` | 请求超时（毫秒） |
| `connectTimeout` | `Long` | `10000` | 连接超时（毫秒） |
| `socketTimeout` | `Long` | `30000` | 套接字超时（毫秒） |

### 日志配置

| 参数 | 类型 | 默认值 | 说明 |
|------|------|--------|------|
| `logLevel` | `LogLevel` | `LogLevel.INFO` | HTTP 日志级别 |

**日志级别**:
```kotlin
LogLevel.OFF      // 禁用
LogLevel.ERROR    // 仅错误
LogLevel.WARNING  // 警告及以上
LogLevel.INFO     // 信息及以上（默认）
LogLevel.DEBUG    // 调试及以上
LogLevel.ALL      // 所有
```

### 重试配置

| 参数 | 类型 | 默认值 | 说明 |
|------|------|--------|------|
| `enableRetry` | `Boolean` | `true` | 是否启用重试 |
| `maxRetries` | `Int` | `3` | 最大重试次数 |
| `retryInterval` | `Long` | `1000` | 重试间隔（毫秒） |

## MutableSdkConfig 完整属性列表

以下是 `MutableSdkConfig` 支持的所有可配置属性：

| # | 属性名 | 类型 | 默认值 | 说明 |
|---|--------|------|--------|------|
| 1 | `baseUrl` | `String` | `"https://open-global.vdsentnet.com"` | API 服务器地址 |
| 2 | `apiKey` | `String?` | `null` | VDS 颁发的 API 密钥（设置后忽略 clientId/clientSecret） |
| 3 | `clientId` | `String?` | `null` | 应用 ID（格式 `vap_xxxxxxxx`，32位十六进制） |
| 4 | `clientSecret` | `String?` | `null` | 应用密钥（来自 VDS 开发者控制台） |
| 5 | `requestTimeout` | `Long` | `30000` | 请求超时时间（毫秒），建议范围 10000-60000 |
| 6 | `connectTimeout` | `Long` | `10000` | 连接超时时间（毫秒），建议范围 5000-30000 |
| 7 | `socketTimeout` | `Long` | `30000` | 套接字读写超时（毫秒），建议范围 10000-60000 |
| 8 | `logLevel` | `LogLevel` | `INFO` | HTTP 客户端日志级别（OFF/ERROR/WARNING/INFO/DEBUG/ALL） |
| 9 | `enableRetry` | `Boolean` | `true` | 是否在请求失败时自动重试 |
| 10 | `maxRetries` | `Int` | `3` | 失败后的最大重试次数（0-10） |
| 11 | `retryInterval` | `Long` | `1000` | 重试间隔时间（毫秒），建议范围 500-5000 |

## 配置方式

### DSL 配置（推荐）

```kotlin
val sdk = fursuitTvSdk {
    // 认证（二选一）
    clientId = "vap_xxxxxxxxxxxxxxxx"
    clientSecret = "your-app-secret"
    
    // 网络
    baseUrl = "https://open-global.vdsentnet.com"
    connectTimeout = 10000
    
    // 日志
    logLevel = LogLevel.DEBUG
    
    // 重试
    enableRetry = true
    maxRetries = 3
}
```

### 工厂方法

```kotlin
// 签名交换
val config = SdkConfig.forTokenExchange(
    clientId = "vap_xxx",
    clientSecret = "your-secret"
)

// 使用 apiKey
val config = SdkConfig.withApiKey("your-api-key")
```

## 高级配置

> **注意**: 如需自定义 HTTP 引擎（如代理、SSL、连接池等高级功能），应直接使用 `SdkConfig` 构造函数而非 `MutableSdkConfig` DSL。`MutableSdkConfig` 仅支持上述 11 个基本配置项。

### 使用 SdkConfig 构造函数进行高级配置

对于需要更精细控制的场景（如自定义 HTTP 引擎、代理服务器等），可以使用 `SdkConfig` 直接构造：

```kotlin
val config = SdkConfig(
    baseUrl = "https://open-global.vdsentnet.com",
    apiKey = "your-api-key",
    requestTimeout = 30_000L,
    logLevel = LogLevel.INFO,
    enableRetry = true,
    maxRetries = 3,
    retryInterval = 1_000L
)
val sdk = FursuitTvSdk(config)
```

## 平台特定配置

### JVM

```kotlin
val sdk = fursuitTvSdk {
    // JVM 平台可以使用更短的超时
    connectTimeout = 10000
}
```

### JavaScript

```kotlin
val sdk = fursuitTvSdk {
    // 浏览器环境自动使用 Fetch API
    // 无需额外配置
}
```

### Native

```kotlin
val sdk = fursuitTvSdk {
    // Native 平台使用更长的超时
    connectTimeout = 20000
}
```

## 配置验证

```kotlin
// 检查配置是否有效
val config = sdk.config
require(config.clientId != null || config.apiKey != null) {
    "必须配置 clientId 或 apiKey"
}

require(config.baseUrl.startsWith("https://")) {
    "baseUrl 必须使用 HTTPS"
}
```

## 相关文档

- [快速开始](getting-started.md) - 基础配置示例
- [认证详解](authentication.md) - 认证方式选择和配置
- [平台指南](platform-guide.md) - 各平台配置说明
