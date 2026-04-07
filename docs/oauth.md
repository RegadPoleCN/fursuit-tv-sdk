# 跨平台 OAuth 2.0 实现指南

本文档详细说明 Fursuit.TV SDK 的 OAuth 2.0 实现，包括完整的授权流程、各平台的实现差异以及最佳实践。

## 📋 目录

- [OAuth 2.0 基础](#oauth-20-基础)
- [VDS OAuth 端点](#vds-oauth-端点)
- [完整授权流程](#完整授权流程)
- [跨平台实现](#跨平台实现)
- [安全最佳实践](#安全最佳实践)
- [常见问题](#常见问题)

## OAuth 2.0 基础

### 什么是 OAuth 2.0？

OAuth 2.0 是一个开放标准授权协议，允许第三方应用获取用户在资源服务器上的有限访问权限，而无需暴露用户的凭证。

### OAuth 2.0 角色

- **资源所有者（Resource Owner）**：用户
- **客户端（Client）**：你的应用
- **授权服务器（Authorization Server）**：VDS 账户系统
- **资源服务器（Resource Server）**：Fursuit.TV API

### 授权码模式（Authorization Code Flow）

本 SDK 使用 OAuth 2.0 授权码模式，这是最安全的流程：

```
┌─────────┐         ┌──────────────────┐         ┌─────────────┐
│  用户   │         │     客户端       │         │ VDS 账户系统 │
└────┬────┘         └────────┬─────────┘         └──────┬──────┘
     │                       │                          │
     │  1. 发起授权请求       │                          │
     │──────────────────────>│                          │
     │                       │                          │
     │                       │  2. 重定向到授权页面      │
     │<──────────────────────┼──────────────────────────│
     │                       │                          │
     │  3. 登录并授权         │                          │
     │─────────────────────────────────────────────────>│
     │                       │                          │
     │  4. 回调并带回 code    │                          │
     │<──────────────────────────────────────────────────│
     │                       │                          │
     │  5. 发送 code         │                          │
     │──────────────────────>│                          │
     │                       │  6. 交换 access_token    │
     │                       │─────────────────────────>│
     │                       │                          │
     │                       │  7. 返回 access_token    │
     │                       │<─────────────────────────│
     │                       │                          │
     │  8. 返回授权结果      │                          │
     │<──────────────────────┤                          │
     └                       └                          └
```

## VDS OAuth 端点

### 1. 授权端点（Authorize）

**端点地址**：
- 推荐：`GET https://account.vds.pub/authorize`
- 镜像：`GET /api/proxy/account/sso/authorize`

**请求参数**：

| 参数 | 类型 | 必需 | 说明 |
|------|------|------|------|
| `client_id` | String | 是 | 应用 ID（格式 `vap_xxxx`） |
| `redirect_uri` | String | 是 | 回调地址（需预先在应用设置中配置） |
| `response_type` | String | 是 | 固定值 `code` |
| `scope` | String | 是 | 授权范围：`openid` 或 `profile`（推荐） |
| `state` | String | 推荐 | 随机字符串，防止 CSRF 攻击 |

**授权 URL 示例**：

```text
https://account.vds.pub/authorize?client_id=vap_xxxxxxxxxxxxxxxx
  &redirect_uri=https%3A%2F%2Flocalhost%3A8080%2Fcallback
  &response_type=code
  &scope=profile
  &state=cS2SxD0tcxj4nxNbFlp95dZBIWDKrbMV
```

**成功回调**：

```text
http://localhost:8080/callback?code=AUTH_CODE&state=YOUR_STATE
```

**失败回调**：

```text
http://localhost:8080/callback?error=invalid_request&error_description=...&state=YOUR_STATE
```

### 2. 令牌交换端点（Token）

**端点地址**：
- `POST /api/proxy/account/sso/token`

**请求头**：
```http
Authorization: Bearer <你的开放平台签名>
Content-Type: application/x-www-form-urlencoded
```

**请求体参数**：

| 参数 | 类型 | 必需 | 说明 |
|------|------|------|------|
| `grant_type` | String | 是 | 固定值 `authorization_code` |
| `code` | String | 是 | 授权回调中获得的授权码 |
| `redirect_uri` | String | 是 | 授权时使用的回调地址（必须一致） |
| `client_id` | String | 是 | 应用 ID |
| `client_secret` | String | 是 | 应用 AK |

**请求示例**：

```http
POST /api/proxy/account/sso/token HTTP/1.1
Host: open-global.vdsentnet.com
Authorization: Bearer <VDP_BEARER>
Content-Type: application/x-www-form-urlencoded

grant_type=authorization_code
code=AUTH_CODE_FROM_CALLBACK
redirect_uri=https%3A%2F%2Flocalhost%3A8080%2Fcallback
client_id=vap_xxxxxxxxxxxxxxxx
client_secret=YOUR_APP_AK
```

**响应示例**：

```json
{
  "access_token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "token_type": "Bearer",
  "expires_in": 31556952,
  "refresh_token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "scope": "openid profile",
  "requestId": "435e1d38-e4b6-4224-a629-8927b81c96cc"
}
```

**字段说明**：

| 字段 | 说明 |
|------|------|
| `access_token` | 访问令牌，用于调用 API |
| `token_type` | 令牌类型，固定为 `Bearer` |
| `expires_in` | 过期时间（秒），默认 1 年 |
| `refresh_token` | 刷新令牌，用于刷新 access_token |
| `scope` | 授权范围 |

**常见错误**：

```json
{
  "error": "invalid_grant",
  "error_description": "无效的授权码或重定向 URI 不匹配"
}
```

### 3. 用户信息端点（UserInfo）

**端点地址**：
- `GET /api/proxy/account/sso/userinfo`

**请求头**：
```http
Authorization: Bearer <你的开放平台签名>
X-OAuth-Access-Token: <OAuth access_token>
```

**请求示例**：

```http
GET /api/proxy/account/sso/userinfo HTTP/1.1
Host: open-global.vdsentnet.com
Authorization: Bearer <VDP_BEARER>
X-OAuth-Access-Token: <OAUTH_ACCESS_TOKEN>
```

**响应示例**：

```json
{
  "sub": "10001",
  "name": "示例用户",
  "nickname": "示例用户",
  "username": "example_user",
  "avatar_url": "https://venusercontents-1301106215.cos.ap-nanjing.myqcloud.com/user-uploads/10001/example-avatar.jpg",
  "updated_at": 1774002667,
  "email": "e***@example.com",
  "phone_number": "+86137****8072",
  "iss": "https://account.vds.pub",
  "aud": 35,
  "requestId": "ab7747a5-9601-49ad-ba58-9111592dc3b0"
}
```

**Scope 对应字段**：

| Scope | 可获取的字段 |
|-------|-------------|
| `openid` | `sub` |
| `profile` | `sub`, `nickname`, `username`, `avatar_url`, `updated_at` 等基础信息 |

**常见错误**：

```json
{
  "error": "令牌无效"
}
```

## 完整授权流程

### 步骤 1: 准备工作

在开始 OAuth 流程前，需要：

1. 在 VDS 开放平台创建应用，获取 `client_id` 和 `client_secret`
2. 在应用设置中配置回调地址（如 `http://localhost:8080/callback`）
3. 在能力中添加 VDS 账户 SSO，并在权限管理中开启相应权限

### 步骤 2: 初始化 SDK

```kotlin
val sdk = FursuitTvSdk()
```

### 步骤 3: 配置 OAuth

```kotlin
val config = OAuthConfig(
    clientId = "vap_xxxxxxxxxxxxxxxx",
    clientSecret = "your-app-secret",
    redirectUri = "http://localhost:8080/callback",
    callbackPort = 8080,
    state = null,  // null 表示自动生成
    enablePkce = true  // 推荐使用 PKCE
)
```

### 步骤 4: 启动 OAuth 流程

```kotlin
runBlocking {
    try {
        val oauthResult = sdk.auth.initWithOAuth(config)
        
        // 授权成功，获取用户信息
        println("用户 ID: ${oauthResult.userId}")
        println("用户名：${oauthResult.username}")
        println("访问令牌：${oauthResult.accessToken}")
        
        // 现在可以使用 OAuth 令牌调用 API
        val userProfile = sdk.user.getUserProfile(oauthResult.username)
        println("用户资料：${userProfile.displayName}")
        
    } catch (e: OAuthCallbackError.TimeoutError) {
        println("OAuth 回调超时，用户未完成授权")
    } catch (e: OAuthCallbackError.StateMismatchError) {
        println("State 验证失败，可能存在安全问题")
    } catch (e: Exception) {
        println("OAuth 流程失败：${e.message}")
    } finally {
        sdk.close()
    }
}
```

### 步骤 5: 保存令牌（可选）

```kotlin
// 保存令牌到本地存储，下次启动时恢复
saveToken(oauthResult.accessToken)
saveRefreshToken(oauthResult.refreshToken)
saveExpiresAt(oauthResult.expiresAt)
```

### 步骤 6: 恢复令牌（下次启动）

```kotlin
// 从本地存储读取之前保存的令牌
val savedToken = loadToken()
val savedRefreshToken = loadRefreshToken()
val savedExpiresAt = loadExpiresAt()

if (savedToken != null && !isTokenExpired(savedExpiresAt)) {
    // 令牌仍然有效，直接使用
    val sdk = FursuitTvSdk(accessToken = savedToken)
} else if (savedRefreshToken != null) {
    // 令牌已过期，使用刷新令牌刷新
    val sdk = FursuitTvSdk(accessToken = savedRefreshToken)
    sdk.auth.refreshToken()
} else {
    // 需要重新授权
    // 回到步骤 4
}
```

## 跨平台实现

### JVM 平台

**实现方式**：
- 使用 `java.awt.Desktop.browse()` 打开系统默认浏览器
- 使用 Ktor 启动本地 HTTP 服务器监听回调
- 授权完成后自动关闭回调服务器

**代码示例**：

```kotlin
// JVM 平台特定实现
class JvmOAuthPlatform : OAuthPlatform {
    override fun openBrowser(url: String) {
        if (Desktop.isDesktopSupported()) {
            Desktop.getDesktop().browse(URI(url))
        } else {
            throw UnsupportedOperationException("不支持打开浏览器")
        }
    }
    
    override fun startCallbackServer(port: Int): CallbackServer {
        return KtorCallbackServer(port)
    }
}
```

**特点**：
- ✅ 自动打开浏览器
- ✅ 完整的本地回调服务器
- ✅ 支持 PKCE
- ✅ 自动关闭回调服务器
- ⚠️ 需要图形环境（无头服务器不支持）

**适用场景**：
- 桌面应用
- 有图形界面的服务器
- 开发调试环境

### JS 平台（Node.js）

**实现方式**：
- 使用 `open` npm 包打开浏览器（可选）
- 使用 Ktor JS 或 Node.js HTTP 服务器监听回调
- 需要手动处理浏览器打开

**代码示例**：

```kotlin
// JS 平台特定实现
class JsOAuthPlatform : OAuthPlatform {
    override fun openBrowser(url: String) {
        // 在 Node.js 环境中，可以使用 open 包
        // 或者提示用户手动打开
        println("请在浏览器中打开：$url")
    }
    
    override fun startCallbackServer(port: Int): CallbackServer {
        return NodeHttpCallbackServer(port)
    }
}
```

**特点**：
- ⚠️ 可能需要手动打开浏览器
- ✅ 本地 HTTP 服务器接收回调
- ✅ 适用于无头环境
- ⚠️ 需要更长的超时时间
- ✅ 适用于后端服务

**适用场景**：
- Node.js 后端服务
- 命令行工具
- CI/CD 环境

### Native 平台

**实现方式**：
- **iOS/macOS**：使用 `NSWorkspace.shared.open()` 或 `UIApplication.shared.open()`
- **Windows**：使用 `ProcessBuilder` 打开默认浏览器
- **Linux**：使用 `xdg-open` 命令
- 支持自定义 URL Scheme 和 Universal Links

**代码示例**：

```kotlin
// iOS/macOS 平台特定实现
class DarwinOAuthPlatform : OAuthPlatform {
    override fun openBrowser(url: String) {
        NSWorkspace.shared.open(NSURL.URLWithString(url))
    }
    
    override fun startCallbackServer(port: Int): CallbackServer {
        return CurlCallbackServer(port)
    }
}

// Windows 平台特定实现
class WindowsOAuthPlatform : OAuthPlatform {
    override fun openBrowser(url: String) {
        ProcessBuilder("cmd.exe", "/c", "start", url).start()
    }
}

// Linux 平台特定实现
class LinuxOAuthPlatform : OAuthPlatform {
    override fun openBrowser(url: String) {
        ProcessBuilder("xdg-open", url).start()
    }
}
```

**特点**：
- ✅ 使用平台特定的 URL 打开机制
- ✅ 支持自定义 URL Scheme
- ✅ iOS/macOS 支持 Universal Links
- ✅ 适用于移动应用
- ⚠️ 需要配置 URL Scheme

**适用场景**：
- iOS/macOS 应用
- Android 应用（通过 Kotlin Multiplatform）
- Windows/Linux 桌面应用
- 移动应用

### 平台差异对比表

| 特性 | JVM | JS (Node.js) | Native |
|------|-----|--------------|--------|
| 自动打开浏览器 | ✅ | ⚠️ (可选) | ✅ |
| 本地回调服务器 | ✅ | ✅ | ✅ |
| 自定义 URL Scheme | ⚠️ | ⚠️ | ✅ |
| Universal Links | ❌ | ❌ | ✅ (iOS/macOS) |
| PKCE 支持 | ✅ | ✅ | ✅ |
| 无头环境支持 | ❌ | ✅ | ⚠️ |
| 移动端支持 | ❌ | ❌ | ✅ |

## 安全最佳实践

### 1. 使用 PKCE（Proof Key for Code Exchange）

PKCE 可以防止授权码拦截攻击，强烈推荐使用：

```kotlin
val config = OAuthConfig(
    clientId = "vap_xxxxxxxxxxxxxxxx",
    clientSecret = "your-app-secret",
    enablePkce = true  // 启用 PKCE
)
```

**PKCE 流程**：

```
1. 生成 code_verifier（随机字符串）
2. 计算 code_challenge = SHA256(code_verifier)
3. 授权请求时带上 code_challenge
4. 交换令牌时带上 code_verifier
5. 服务器验证 code_challenge 和 code_verifier 匹配
```

### 2. 使用 State 参数防止 CSRF

State 参数用于防止跨站请求伪造攻击：

```kotlin
// 生成随机 state
val state = generateRandomState()

// 验证回调中的 state
if (receivedState != storedState) {
    throw OAuthCallbackError.StateMismatchError()
}
```

**State 生成示例**：

```kotlin
fun generateRandomState(): String {
    val random = SecureRandom()
    val bytes = ByteArray(16)
    random.nextBytes(bytes)
    return bytes.joinToString("") { "%02x".format(it) }
}
```

### 3. 使用 HTTPS

在生产环境中，始终使用 HTTPS：

```kotlin
val config = OAuthConfig(
    redirectUri = "https://yourdomain.com/oauth/callback"  // 使用 HTTPS
)
```

### 4. 保护 Client Secret

永远不要将 `client_secret` 硬编码在客户端代码中：

```kotlin
// ❌ 错误做法
val clientSecret = "hardcoded-secret"

// ✅ 正确做法
val clientSecret = System.getenv("CLIENT_SECRET")  // 从环境变量读取
```

### 5. 安全存储令牌

使用安全的存储机制保存令牌：

```kotlin
// iOS/macOS: 使用 Keychain
fun saveTokenToKeychain(token: String) {
    // 使用 Keychain 存储
}

// Windows: 使用 Credential Manager
fun saveTokenToCredentialManager(token: String) {
    // 使用 Credential Manager 存储
}

// Linux: 使用 Secret Service
fun saveTokenToSecretService(token: String) {
    // 使用 Secret Service 存储
}

// JVM: 使用加密文件
fun saveTokenToEncryptedFile(token: String) {
    // 使用加密文件存储
}
```

### 6. 设置合理的超时时间

```kotlin
val config = OAuthConfig(
    stateTimeoutMinutes = 10  // 10 分钟超时
)
```

### 7. 验证 Scope

仅请求需要的权限：

```kotlin
// 仅请求需要的 scope
val scope = "openid profile"  // 不要请求不需要的权限
```

## 常见问题

### Q1: 回调地址不匹配

**错误**：`redirect_uri_mismatch`

**原因**：回调地址与应用设置的地址不一致

**解决方案**：
1. 检查应用设置中的回调地址列表
2. 确保授权请求中的 `redirect_uri` 与设置完全一致
3. 注意 URL 编码

### Q2: 授权码已使用或过期

**错误**：`invalid_grant`

**原因**：授权码只能使用一次，且有有效期

**解决方案**：
1. 重新发起授权流程
2. 确保网络请求稳定
3. 检查回调服务器是否正常接收

### Q3: State 验证失败

**错误**：`state_mismatch`

**原因**：回调中的 state 与请求时的不一致

**解决方案**：
1. 检查 state 的生成和存储逻辑
2. 确保 state 在请求和回调间保持一致
3. 检查是否有并发请求干扰

### Q4: 本地回调服务器无法启动

**错误**：`Address already in use`

**原因**：端口被占用

**解决方案**：
```kotlin
val config = OAuthConfig(
    callbackPort = 0  // 使用随机可用端口
)
```

### Q5: 无法打开浏览器

**错误**：`UnsupportedOperationException`

**原因**：在无头环境或不支持的系统上

**解决方案**：
1. 手动打开授权 URL
2. 使用深链接或自定义 URL Scheme
3. 在无头环境中使用其他方式授权

### Q6: 移动端如何处理回调

**问题**：移动应用如何接收 OAuth 回调？

**解决方案**：

**iOS/macOS**：
1. 在 Info.plist 中配置 URL Types
2. 实现 `application(_:open:options:)` 方法
3. 使用 Universal Links（推荐）

**Android**：
1. 在 AndroidManifest.xml 中配置 intent-filter
2. 实现 Activity 处理回调
3. 使用 App Links（推荐）

**示例（iOS）**：

```swift
// Info.plist
<key>CFBundleURLTypes</key>
<array>
  <dict>
    <key>CFBundleURLSchemes</key>
    <array>
      <string>myapp</string>
    </array>
  </dict>
</array>

// AppDelegate.swift
func application(_ app: UIApplication, 
                 open url: URL, 
                 options: [UIApplication.OpenURLOptionsKey : Any] = [:]) -> Bool {
    // 处理 OAuth 回调
    OAuthManager.shared.handleCallback(url: url)
    return true
}
```

## 相关资源

- [VDS OAuth 快速接入文档](../vds-docs/VDS 账户/VDS 账户快速接入（OAuth）.md)
- [授权端点文档](../vds-docs/VDS 账户/授权端点（Authorize，account.sso.authorize）.md)
- [令牌交换端点文档](../vds-docs/VDS 账户/签名交换端点（Token，account.sso.token）.md)
- [用户信息端点文档](../vds-docs/VDS 账户/用户信息端点（UserInfo，account.sso.userinfo）.md)
- [OAuth 2.0 RFC 6749](https://tools.ietf.org/html/rfc6749)
- [PKCE RFC 7636](https://tools.ietf.org/html/rfc7636)
