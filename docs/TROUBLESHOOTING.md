# 故障排除

本文档提供 Fursuit.TV SDK 常见问题的诊断和解决方案。

## 目录

1. [认证问题](#认证问题)
2. [网络连接问题](#网络连接问题)
3. [API 调用问题](#api-调用问题)
4. [OAuth 问题](#oauth-问题)
5. [调试工具](#调试工具)

## 认证问题

### 认证失败（401 Unauthorized）

**症状**:
```kotlin
AuthenticationException: Invalid credentials
```

**解决方案**:

```kotlin
// 1. 检查配置
val sdk = fursuitTvSdk {
    clientId = System.getenv("FURSUIT_CLIENT_ID")
    clientSecret = System.getenv("FURSUIT_CLIENT_SECRET")
}

// 2. 验证 clientId 格式（应为 vap_xxxx）
println("Client ID: ${sdk.config.clientId}")

// 3. 重新交换令牌
try {
    val tokenInfo = sdk.auth.exchangeToken(clientId, clientSecret)
} catch (e: AuthenticationException) {
    println("认证失败：${e.message}")
}
```

### 令牌刷新失败

**症状**:
```kotlin
TokenExpiredException: Failed to refresh token
```

**解决方案**:

```kotlin
// 1. 检查令牌状态
if (!sdk.auth.isAuthenticated()) {
    println("令牌已过期")
}

// 2. 重新交换令牌
val newToken = sdk.auth.exchangeToken(clientId, clientSecret)
```

### 权限不足（403 Forbidden）

**解决方案**:

1. 确认应用权限是否充足
2. 检查是否需要用户授权（使用 OAuth）

```kotlin
// 使用 OAuth 获取用户授权
val tokenInfo = sdk.auth.exchangeOAuthToken(code, redirectUri)
```

## 网络连接问题

### 连接超时

**症状**:
```kotlin
NetworkException: Connection timeout
```

**解决方案**:

```kotlin
// 1. 增加超时时间
val sdk = fursuitTvSdk {
    connectTimeout = 20000  // 20 秒
    socketTimeout = 60000   // 60 秒
}

// 2. 检查网络连接
// 3. 检查防火墙设置
// 4. 确认 baseUrl 正确
```

### SSL 握手失败

**解决方案**:

```kotlin
// 1. 检查系统时间是否正确
// 2. 更新 CA 证书
// 3. 启用 TLS 1.2+
System.setProperty("https.protocols", "TLSv1.2,TLSv1.3")
```

## API 调用问题

### 参数验证失败

**症状**:
```kotlin
ValidationException: Invalid parameter: username
```

**解决方案**:

```kotlin
// 检查参数要求
// username: 3-20 个字符，只能包含字母数字下划线
val username = "valid_username"  // ✅
val username = "ab"              // ❌ 太短
val username = "user@name"       // ❌ 包含特殊字符
```

### 资源不存在（404）

**解决方案**:

```kotlin
try {
    val profile = sdk.user.getUserProfile("username")
} catch (e: NotFoundException) {
    println("资源不存在")
} catch (e: ApiException) {
    if (e.statusCode == 404) {
        println("资源不存在")
    }
}
```

## OAuth 问题

### 回调失败

**症状**: 用户授权后无法回调到应用

**解决方案**:

```kotlin
// 1. 检查 redirectUri 是否与注册的一致
val authUrl = sdk.auth.getOAuthAuthorizeUrl(
    redirectUri = "my-app://callback"  // 必须完全匹配
)

// 2. 检查 URL Scheme 配置
// Android: AndroidManifest.xml
// iOS: Info.plist
```

### State 验证失败

**解决方案**:

```kotlin
// 1. 生成并保存 state
val state = generateRandomState()
saveState(state)

// 2. 生成授权 URL
val authUrl = sdk.auth.getOAuthAuthorizeUrl(
    redirectUri = redirectUri,
    state = state
)

// 3. 回调时验证
if (receivedState != loadState()) {
    throw SecurityException("Invalid state")
}
```

## 调试工具

### 启用详细日志

```kotlin
val sdk = fursuitTvSdk {
    logLevel = SdkLogLevel.DEBUG  // 或 SdkLogLevel.ALL
}
```

### 使用 curl 测试

```bash
# 获取日志中的 curl 命令
# SDK 会在 DEBUG 级别输出 curl 格式的请求

# 手动测试
curl -X GET "https://open-global.vdsentnet.com/api/user/profile" \
  -H "X-Api-Key: your-api-key"
```

### 检查 SDK 配置

```kotlin
val config = sdk.config
println("Base URL: ${config.baseUrl}")
println("Client ID: ${config.clientId}")
println("Log Level: ${config.logLevel}")
```

## 相关文档

- [错误处理](error-handling.md) - 异常类型详解
- [配置选项](configuration.md) - 配置参数说明
