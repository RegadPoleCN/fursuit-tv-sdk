# OAuth 2.0 实现指南

本文档详细说明 Fursuit.TV SDK 的 OAuth 2.0 实现，包括完整的授权流程、使用方法以及最佳实践。

## 概述

OAuth 2.0 是一个开放标准授权协议，允许第三方应用获取用户在资源服务器上的有限访问权限，而无需暴露用户的凭证。

## 授权流程

### 1. 准备工作

在开始 OAuth 流程前，需要：

1. 在 VDS 开放平台创建应用，获取 `appId`
2. 在应用设置中配置回调地址（如 `http://localhost:8080/callback`）
3. 在能力中添加 VDS 账户 SSO，并在权限管理中开启相应权限

### 2. 完整授权流程

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

## 使用方法

### 1. 配置 OAuth

```kotlin
val config = OAuthConfig(
    callbackHost = "localhost",
    callbackPort = 8080,
    callbackPath = "/callback",
    stateTimeoutMinutes = 10
)
```

### 2. 启动 OAuth 流程

```kotlin
runBlocking {
    try {
        val tokenInfo = sdk.auth.initWithOAuth(
            appId = "vap_xxxxxxxxxxxxxxxx",
            config = config
        )
        
        // 授权成功
        println("OAuth 授权成功，access_token: ${tokenInfo.accessToken}")
        
        // 现在可以使用 OAuth 令牌调用 UserInfo 接口
        val userProfile = sdk.user.getUserProfile("username")
        println("用户资料：${userProfile.displayName}")
        
    } catch (e: Exception) {
        println("OAuth 流程失败：${e.message}")
    } finally {
        sdk.close()
    }
}
```

## 平台支持

| 平台 | OAuth 支持 | 说明 |
|------|-----------|------|
| **JVM** | ✅ 完全支持 | 使用 Ktor CIO 启动本地 HTTP 服务器 |
| **JS (Node.js)** | ✅ 完全支持 | 使用 Ktor CIO 启动本地 HTTP 服务器 |
| **JS (Browser)** | ❌ 不支持 | 浏览器环境无法运行 HTTP 服务器 |
| **Native (iOS/macOS)** | ✅ 完全支持 | 使用 Ktor CIO 启动本地 HTTP 服务器 |
| **Native (Linux/Windows)** | ✅ 完全支持 | 使用 Ktor CIO 启动本地 HTTP 服务器 |

**重要说明**：
- Browser 环境不支持 OAuth 回调功能，因为浏览器无法运行 HTTP 服务器
- 如果需要在 Browser 环境中使用 OAuth，需要通过后端服务代理 OAuth 流程

## 安全最佳实践

1. **使用 State 参数**：防止 CSRF 攻击
2. **使用 HTTPS**：在生产环境中始终使用 HTTPS
3. **保护 Client Secret**：不要将 `clientSecret` 硬编码在客户端代码中
4. **安全存储令牌**：使用安全的存储机制保存令牌
5. **设置合理的超时时间**：避免令牌过期导致的问题
6. **仅请求需要的权限**：不要请求不需要的 scope

## 常见问题

### Q1: 回调地址不匹配

**错误**：`redirect_uri_mismatch`

**解决方案**：
1. 检查应用设置中的回调地址列表
2. 确保授权请求中的 `redirect_uri` 与设置完全一致
3. 注意 URL 编码

### Q2: 授权码已使用或过期

**错误**：`invalid_grant`

**解决方案**：
1. 重新发起授权流程
2. 确保网络请求稳定
3. 检查回调服务器是否正常接收

### Q3: 本地回调服务器无法启动

**错误**：`Address already in use`

**解决方案**：
```kotlin
val config = OAuthConfig(
    callbackPort = 0  // 使用随机可用端口
)
```

## 相关资源

- [VDS OAuth 快速接入文档](../vds-docs/VDS 账户/VDS 账户快速接入（OAuth）.md)
- [授权端点文档](../vds-docs/VDS 账户/授权端点（Authorize，account.sso.authorize）.md)
- [令牌交换端点文档](../vds-docs/VDS 账户/签名交换端点（Token，account.sso.token）.md)
- [用户信息端点文档](../vds-docs/VDS 账户/用户信息端点（UserInfo，account.sso.userinfo）.md)
