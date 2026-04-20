# OAuth 完整指南

本指南详细介绍如何在 Fursuit.TV SDK 中使用 OAuth 2.0 授权流程。

## 目录

1. [OAuth 2.0 简介](#oauth-20-简介)
2. [授权流程概览](#授权流程概览)
3. [准备工作](#准备工作)
4. [标准授权流程](#标准授权流程)
5. [PKCE 授权流程](#pkce-授权流程)
6. [令牌管理](#令牌管理)
7. [获取用户信息](#获取用户信息)

## OAuth 2.0 简介

OAuth 2.0 允许用户授权第三方应用访问其资源，而无需分享凭据。

### 核心概念

| 术语 | 说明 |
|------|------|
| **Client** | 客户端应用（你的应用） |
| **Authorization Server** | 授权服务器（VDS） |
| **Access Token** | 访问令牌，用于 API 调用 |
| **Refresh Token** | 刷新令牌，用于获取新的 Access Token |
| **Scope** | 权限范围 |

## 授权流程概览

```
1. 用户 → 客户端：启动授权
2. 客户端 → 授权服务器：重定向到授权页
3. 用户 → 授权服务器：登录并授权
4. 授权服务器 → 客户端：回调带 authorization code
5. 客户端 → 授权服务器：code 换 token
6. 客户端 → 资源服务器：使用 token 调用 API
```

## 准备工作

### 1. 注册应用

在 VDS 开放平台注册应用，获取：
- `clientId`（应用 ID，格式 `vap_xxxx`）
- `clientSecret`（应用密钥）
- 配置回调 URI

### 2. 配置回调 URI

| 平台 | 示例 |
|------|------|
| Web | `https://yourapp.com/callback` |
| iOS | `yourapp://callback` |
| Android | `yourapp://callback/oauth` |

### 3. 初始化 SDK

```kotlin
val sdk = fursuitTvSdk {
    clientId = "vap_xxxxxxxxxxxxxxxx"
    clientSecret = "your-app-secret"
}
```

## 标准授权流程

### 步骤 1：生成授权 URL

```kotlin
val authUrl = sdk.auth.getOAuthAuthorizeUrl(
    redirectUri = "https://yourapp.com/callback",
    scope = "user.profile",
    state = "random-state-123",  // 防止 CSRF
    enablePkce = false
)
```

### 步骤 2：引导用户授权

#### Web 应用

```kotlin
// Ktor 示例
get("/login") {
    val authUrl = sdk.auth.getOAuthAuthorizeUrl(
        redirectUri = "https://yourapp.com/callback",
        state = generateState()
    )
    call.respondRedirect(authUrl)
}
```

### 步骤 3：处理回调并获取 Token

```kotlin
get("/callback") {
    val code = call.parameters["code"]
    val state = call.parameters["state"]
    
    // 验证 state
    if (!validateState(state)) {
        throw SecurityException("Invalid state")
    }
    
    // 换取 token
    val tokenInfo = sdk.auth.exchangeOAuthToken(
        code = code,
        redirectUri = "https://yourapp.com/callback"
    )
    
    // 保存 token
    saveToken(tokenInfo)
}
```

## PKCE 授权流程

PKCE（Proof Key for Code Exchange）增强移动端安全性。

### 完整流程

```kotlin
// 1. 生成 PKCE 参数
val codeVerifier = generateCodeVerifier()
val codeChallenge = generateCodeChallenge(codeVerifier)

// 2. 生成授权 URL（启用 PKCE）
val authUrl = sdk.auth.getOAuthAuthorizeUrl(
    redirectUri = "my-app://callback",
    scope = "user.profile",
    state = generateState(),
    enablePkce = true
)

// 3. 保存 codeVerifier（用于后续换取 token）
saveCodeVerifier(codeVerifier)

// 4. 引导用户到 authUrl 授权

// 5. 处理回调
val code = intent.data?.getQueryParameter("code")
val tokenInfo = sdk.auth.exchangeOAuthToken(
    code = code,
    redirectUri = "my-app://callback",
    codeVerifier = codeVerifier  // 传入 codeVerifier
)
```

## 令牌管理

### 保存 Token

```kotlin
class TokenStorage {
    fun saveToken(tokenInfo: TokenInfo) {
        // 使用加密存储
        encryptedPrefs.edit()
            .putString("access_token", tokenInfo.accessToken)
            .putString("refresh_token", tokenInfo.refreshToken)
            .putLong("expires_at", tokenInfo.expiresAt)
            .apply()
    }
}
```

### 刷新 Token

```kotlin
suspend fun refreshTokenIfNeeded(): Boolean {
    val tokenInfo = loadToken() ?: return false
    
    // 提前 5 分钟刷新
    if (tokenInfo.expiresAt - System.currentTimeMillis() < 300_000) {
        val newTokenInfo = sdk.auth.refreshOAuthToken()
        saveToken(newTokenInfo)
        return true
    }
    
    return true
}
```

## 获取用户信息

```kotlin
suspend fun getUserInfo(): UserInfo {
    // 确保 token 有效
    if (!refreshTokenIfNeeded()) {
        throw IllegalStateException("No valid token")
    }
    
    // 使用 oauthToken 获取用户信息
    return sdk.auth.getUserInfo()
}

// 使用示例
val userInfo = getUserInfo()
println("用户：${userInfo.displayName}")
println("UID: ${userInfo.uid}")
```

## 移动端 OAuth

### Android

```kotlin
// AndroidManifest.xml
<activity android:name=".OAuthCallbackActivity">
    <intent-filter>
        <action android:name="android.intent.action.VIEW" />
        <category android:name="android.intent.category.DEFAULT" />
        <data android:scheme="my-app" android:host="callback" />
    </intent-filter>
</activity>
```

### iOS

```swift
// Info.plist
<key>CFBundleURLTypes</key>
<array>
  <dict>
    <key>CFBundleURLSchemes</key>
    <array>
      <string>my-app</string>
    </array>
  </dict>
</array>
```

## 安全最佳实践

1. **始终使用 HTTPS**（生产环境）
2. **验证 state 参数**（防止 CSRF）
3. **移动端启用 PKCE**（防止授权码劫持）
4. **加密存储 token**（不使用明文）
5. **定期轮换密钥**（clientSecret）

## 相关文档

- [最佳实践](best-practices.md) - OAuth 安全实践
- [错误处理](error-handling.md) - OAuth 错误处理
