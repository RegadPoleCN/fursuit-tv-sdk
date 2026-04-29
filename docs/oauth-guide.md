# OAuth 完整指南

本指南介绍如何在 Fursuit.TV SDK 中使用 OAuth 2.0 授权。

## 目录

1. [快速开始](#快速开始)
2. [配置说明](#配置说明)
3. [回调处理器](#回调处理器)
4. [令牌管理](#令牌管理)
5. [安全最佳实践](#安全最佳实践)
6. [错误处理](#错误处理)

## 快速开始

### 基础用法

```kotlin
val sdk = fursuitTvSdk {
    clientId = "vap_xxxxxxxxxxxxxxxx"
    clientSecret = "your-app-secret"
}

try {
    val tokenInfo = sdk.auth.loginWithOAuth()
    println("授权成功！")
} catch (e: OAuthException) {
    println("失败: ${e.message}, errorCode: ${e.errorCode}")
} finally {
    sdk.close()
}
```

### 自定义回调配置

通过 `setOAuthCallbackHandler()` 设置自定义回调处理器：

```kotlin
val handler = createDefaultOAuthHandler(
    OAuthCallbackServerConfig(
        callbackHost = "localhost",
        callbackPort = 9000,
        callbackPath = "/callback",
        timeoutSeconds = 300
    )
)
sdk.auth.setOAuthCallbackHandler(handler)

val tokenInfo = sdk.auth.loginWithOAuth(scope = "user.profile")
```

### ⚠️ 使用要点

- **前置条件**：必须先完成签名交换（通过 DSL 初始化 SDK 即可自动完成）。`clientId` 即 VDS 文档中的 `appId`
- **阻塞调用**：挂起函数，等待用户完成授权或超时
- **自动管理**：回调处理器自动启动/关闭，无需手动处理
- **资源释放**：务必在 `finally` 中调用 `sdk.close()`

---

## 配置说明

### OAuthCallbackServerConfig 参数

| 参数 | 类型 | 默认值 | 说明 |
|------|------|--------|------|
| `callbackHost` | `String` | `"localhost"` | 回调地址 |
| `callbackPort` | `Int` | `8080` | 端口 (1-65535) |
| `callbackPath` | `String` | `"/callback"` | 回调路径（必须以 "/" 开头） |
| `timeoutSeconds` | `Long` | `300` | 超时时间（秒） |

### OAuthConfig 参数

| 参数 | 类型 | 默认值 | 说明 |
|------|------|--------|------|
| `callbackHost` | `String` | `"localhost"` | 回调地址 |
| `callbackPort` | `Int` | `8080` | 端口 (1-65535) |
| `callbackPath` | `String` | `"/callback"` | 回调路径（必须以 "/" 开头） |
| `stateTimeoutMinutes` | `Int` | `5` | 超时时间（分钟） |
| `enablePkce` | `Boolean` | `false` | 启用 PKCE（移动端推荐）。注意：`loginWithOAuth()` 内部始终创建 `OAuthConfig(enablePkce = true)`，即自动启用 PKCE |

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
| `OAuthConfig` | OAuth 流程配置 | callbackHost/Port/Path, timeoutMinutes, enablePkce |
| `OAuthCallbackServerConfig` | 回调服务器配置 | callbackHost/Port/Path, timeoutSeconds |

> 💡 `OAuthCallbackServerConfig` 用于 `createDefaultOAuthHandler()`，`OAuthConfig` 为内部流程配置。

---

## 回调处理器

### OAuthCallbackHandler 接口

`OAuthCallbackHandler` 定义了 OAuth 授权回调的监听与处理流程：

```kotlin
public interface OAuthCallbackHandler {
    public val callbackUrl: String
    public suspend fun startListening()
    public suspend fun waitForCallback(): OAuthCallbackResult
    public suspend fun startAndGetCallback(authorizeUrl: String): OAuthCallbackResult
    public suspend fun stop()
}
```

### 典型使用流程

1. 调用 `startListening()` 启动回调服务器
2. 通过 `callbackUrl` 构建授权 URL 并引导用户访问
3. 调用 `waitForCallback()` 等待回调结果
4. 调用 `stop()` 释放资源

也可使用便捷方法 `startAndGetCallback(authorizeUrl)` 一次性完成上述步骤。

### OAuthCallbackResult

回调结果为密封类：

```kotlin
sealed class OAuthCallbackResult {
    data class Success(val code: String, val state: String) : OAuthCallbackResult()
    data class Error(val message: String, val errorCode: String? = null, val cause: Throwable? = null) : OAuthCallbackResult()
}
```

### 平台默认实现

`createDefaultOAuthHandler()` 根据运行平台自动选择实现：

| 平台 | 实现 | 说明 |
|------|------|------|
| **JVM** | `JvmOAuthCallbackHandler` | 本地 HTTP 服务器 + 自动打开浏览器 |
| **JS (浏览器)** | `JsOAuthCallbackHandler` | postMessage 机制监听回调 |
| **JS (Node.js)** | `JsOAuthCallbackHandler` | Node.js http 模块创建本地服务器 |
| **Native** | `NativeOAuthCallbackHandler` | Ktor CIO 本地 HTTP 服务器 |

### 自定义回调处理器

```kotlin
class CustomOAuthHandler : OAuthCallbackHandler {
    override val callbackUrl = "https://myapp.com/oauth/callback"

    override suspend fun startListening() {
        // 启动你的回调监听逻辑
    }

    override suspend fun waitForCallback(): OAuthCallbackResult {
        // 等待并返回回调结果
    }

    override suspend fun startAndGetCallback(authorizeUrl: String): OAuthCallbackResult {
        startListening()
        // 引导用户到 authorizeUrl
        return waitForCallback()
    }

    override suspend fun stop() {
        // 释放资源
    }
}

sdk.auth.setOAuthCallbackHandler(CustomOAuthHandler())
val tokenInfo = sdk.auth.loginWithOAuth()
```

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

1. **移动端启用 PKCE** (`enablePkce = true`)，`loginWithOAuth()` 默认启用 PKCE
2. **生产环境使用 HTTPS**
3. **加密存储 Token**（不使用明文）
4. **定期轮换密钥**（clientSecret）

---

## 错误处理

### 常见异常

| 异常类型 | 场景 | 处理建议 |
|---------|------|---------|
| `IllegalStateException` | 缺少 clientId/clientSecret 或回调处理器 | 检查 SDK 初始化配置 |
| `OAuthException` | OAuth 授权失败 | 查看 `errorCode` 和消息内容定位原因 |

### OAuth 错误码

| errorCode | 含义 | 处理建议 |
|-----------|------|---------|
| `access_denied` | 用户拒绝授权 | 提示用户重新授权 |
| `invalid_grant` | 授权码无效或过期 | 重新发起 OAuth 流程 |
| `state_mismatch` | State 参数不匹配 | 检查代码逻辑（理论上不应发生） |
| `unauthorized_client` | 客户端未授权 | 检查 clientId 配置 |
| `unsupported_response_type` | 不支持的响应类型 | 检查 OAuth 配置 |
| `invalid_scope` | 无效的权限范围 | 检查 scope 参数 |
| `server_error` | 服务器内部错误 | 稍后重试 |
| `temporarily_unavailable` | 服务暂时不可用 | 稍后重试 |

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
    val tokenInfo = sdk.auth.loginWithOAuth()
} catch (e: IllegalStateException) {
    // 配置错误或回调处理器未设置
    logger.error("初始化错误: ${e.message}")
} catch (e: OAuthException) {
    // OAuth 授权失败
    when (e.errorCode) {
        "access_denied" -> showAccessDeniedDialog()
        "state_mismatch" -> logger.error("State 验证失败")
        else -> {
            when {
                e.message?.contains("timeout") == true -> showTimeoutDialog()
                e.message?.contains("port") == true -> changePortAndRetry()
                else -> showError(e.message ?: "未知错误")
            }
        }
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
