# OAuth 完整指南

本指南介绍如何在 Fursuit.TV SDK 中使用 OAuth 2.0 授权。

## 目录

1. [快速开始](#快速开始)
2. [配置说明](#配置说明)
3. [令牌管理](#令牌管理)
4. [安全最佳实践](#安全最佳实践)
5. [错误处理](#错误处理)

## 快速开始

### 基础用法

```kotlin
val sdk = fursuitTvSdk {
    clientId = "vap_xxxxxxxxxxxxxxxx"
    clientSecret = "your-app-secret"
}

try {
    val tokenInfo = sdk.auth.initOAuth(OAuthConfig())
    println("授权成功！")
} catch (e: OAuthCallbackException) {
    println("失败: ${e.message}")
} finally {
    sdk.close()
}
```

### 自定义配置

| 参数 | 默认值 | 说明 |
|------|--------|------|
| `callbackHost` | `"localhost"` | 回调地址 |
| `callbackPort` | `8080` | 端口 (1-65535) |
| `stateTimeoutMinutes` | `5` | 超时时间（分钟） |
| `enablePkce` | `false` | 启用 PKCE（移动端推荐） |

```kotlin
val config = OAuthConfig(
    callbackPort = 9000,
    enablePkce = true
)
val tokenInfo = sdk.auth.initOAuth(config, scope = "user.profile")
```

### ⚠️ 使用要点

- **阻塞调用**：挂起函数，等待用户完成授权或超时
- **自动管理**：本地服务器自动启动/关闭，无需手动处理
- **资源释放**：务必在 `finally` 中调用 `sdk.close()`

---

## 配置说明

### 参数验证规则

所有参数在构造时验证，无效时抛出 `IllegalArgumentException`：

```kotlin
// ✅ 有效
OAuthConfig(callbackPort = 8080, callbackPath = "/callback")

// ❌ 无效
OAuthConfig(callbackPort = 0)           // 端口范围错误
OAuthConfig(callbackPath = "callback")  // 路径缺少 "/"
OAuthConfig(stateTimeoutMinutes = 0)    // 超时必须 > 0
OAuthConfig(callbackHost = "")          // 地址不能为空
```

### 配置类对比

| 类名 | 用途 | 主要参数 |
|------|------|---------|
| `OAuthConfig` | 用户配置 | callbackHost/Port/Path, timeoutMinutes, enablePkce |
| `OAuthCallbackServerConfig` | 内部服务器配置 | callbackHost/Port/Path, timeoutSeconds |

> 💡 通常只需使用 `OAuthConfig`，内部会自动转换为 `OAuthCallbackServerConfig`。

---

## 令牌管理

### 保存 Token

```kotlin
// TokenInfo 包含所有必要信息
tokenInfo.accessToken   // 访问令牌
tokenInfo.refreshToken   // 刷新令牌（可为 null）
tokenInfo.expiresAt      // 过期时间戳（毫秒）
```

### 刷新 Token

SDK 自动管理令牌刷新（剩余有效期 ≤ 5 分钟时自动刷新）：

```kotlin
// 方式 1：使用 getValidAccessToken() 自动刷新
val validToken = sdk.auth.getValidAccessToken(clientId, clientSecret)

// 方式 2：手动检查并刷新
if (tokenInfo.isExpired()) {
    val newTokenInfo = sdk.auth.refreshOAuthToken()
}
```

### 获取用户信息

```kotlin
val userInfo = sdk.auth.getUserInfo()
println("用户：${userInfo.nickname}")
println("UID：${userInfo.sub}")
```

> 📖 详细 API 文档见 [API 参考](api/auth.md)

---

## 安全最佳实践

1. **移动端启用 PKCE** (`enablePkce = true`)
2. **生产环境使用 HTTPS**
3. **加密存储 Token**（不使用明文）
4. **定期轮换密钥**（clientSecret）

---

## 错误处理

### 常见异常

| 异常类型 | 场景 | 处理建议 |
|---------|------|---------|
| `IllegalStateException` | 缺少 clientId/clientSecret | 检查 SDK 初始化配置 |
| `OAuthCallbackException` | 授权失败 | 查看消息内容定位原因 |

### 授权失败场景

| 场景 | 错误信息关键词 | 建议 |
|------|--------------|------|
| 用户超时未授权 | `timeout` | 提示用户重试或延长超时 |
| 端口被占用 | `port.*in use` | 更换端口 |
| State 不匹配 | `State mismatch` | 检查代码逻辑（理论上不应发生） |
| 协程取消 | 抛出 `CancellationException` | 正常取消流程 |

### 错误处理示例

```kotlin
try {
    val tokenInfo = sdk.auth.initOAuth(config)
} catch (e: IllegalStateException) {
    // 配置错误
    logger.error("初始化错误: ${e.message}")
} catch (e: OAuthCallbackException) {
    // 授权失败
    when {
        e.message?.contains("timeout") == true -> showTimeoutDialog()
        e.message?.contains("port") == true -> changePortAndRetry()
        else -> showError(e.message ?: "未知错误")
    }
} finally {
    sdk.close()
}
```

---

## 相关文档

- [API 参考](api/auth.md) - 完整 API 文档
- [最佳实践](best-practices.md) - 安全实践详解
- [错误处理](error-handling.md) - 错误处理策略
