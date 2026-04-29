# 认证详解

Fursuit.TV SDK 支持两种认证方式：**签名交换（Token Exchange）**和 **OAuth 2.0 授权码**。选择哪种方式取决于你的应用类型和使用场景。

## 认证方式对比

| 维度 | 签名交换 (Token Exchange) | OAuth 2.0 授权码 |
|------|--------------------------|------------------|
| **适用场景** | 服务端应用、后台任务、API 调用 | 客户端应用、需要用户登录 |
| **凭证类型** | clientId + clientSecret | clientId + clientSecret + 用户授权 |
| **获取令牌** | apiKey + accessToken（平台签名） | oauthAccessToken + refreshToken（用户令牌） |
| **认证头（业务 API）** | `X-Api-Key`（推荐）/ `Authorization: Bearer` | `X-Api-Key`（平台签名，非 OAuth 令牌） |
| **认证头（OAuth API）** | 不适用 | 双认证头：`Authorization: Bearer` + `X-OAuth-Access-Token` |
| **令牌刷新** | 自动（SDK 内置，≤300秒触发） | 需手动调用 refreshOAuthToken() |
| **前置条件** | 无 | ⚠️ 必须先完成签名交换获取 platformAccessToken |
| **典型用途** | 调用业务 API（user/search/gathering/school） | 获取用户信息（getUserInfo） |
| **安全级别** | 高（服务端凭证，不暴露给客户端） | 更高（PKCE 防止中间人攻击） |

## 方式一：签名交换（Token Exchange）

### 工作原理

签名交换是 SDK 的主要认证方式，通过应用凭证（clientId + clientSecret）向 VDS 服务器请求平台签名。`clientId` 即 VDS 文档中的 `appId`。

```
clientId + clientSecret → POST /api/auth/token → TokenInfo {accessToken, apiKey}
```

服务器返回两种令牌：
- **accessToken**: 用于 `Authorization: Bearer` 头（备选方案）
- **apiKey**: 用于 `X-Api-Key` 头（**推荐**，SDK 优先使用）

### 使用场景

- ✅ 服务端应用（Web 后端、微服务）
- ✅ 后台任务（定时任务、数据处理脚本）
- ✅ 批量数据导入/导出
- ✅ 仅需调用业务 API，不需要用户身份

### DSL 初始化示例

```kotlin
import com.furrist.rp.furtv.sdk.fursuitTvSdk
import com.furrist.rp.furtv.sdk.user.getUserProfile

// 使用 DSL 方式初始化（推荐）
// fursuitTvSdk 是 suspend 函数，提供 clientId + clientSecret 时自动完成令牌交换
val sdk = fursuitTvSdk {
    clientId = "vap_xxxxxxxxxxxxxxxx"
    clientSecret = "your-client-secret-here"
}

// SDK 自动完成签名交换，可直接使用所有 API
val profile = sdk.user.getUserProfile("exampleUser")
println("用户: ${profile.displayName}")

// 使用完成后关闭 SDK
sdk.close()
```

### 令牌管理

SDK 内置自动令牌管理机制：

#### apiKey vs accessToken

| 令牌 | 认证头 | 优先级 | 说明 |
|------|--------|--------|------|
| **apiKey** | `X-Api-Key: <apiKey>` | **优先** | SDK 默认使用此头 |
| **accessToken** | `Authorization: Bearer <accessToken>` | 备选 | 当 apiKey 为空时自动切换 |

#### 自动刷新机制

SDK 会在以下情况自动刷新令牌：
- 剩余有效期 **≤ 300 秒**时自动触发刷新
- 刷新失败时会**回退到重新 exchangeToken()**
- 整个过程对开发者透明，无需手动干预

```kotlin
// 无需手动管理令牌，SDK 会自动处理
val sdk = fursuitTvSdk { clientId = "..."; clientSecret = "..." }

// 即使长时间运行，SDK 也会自动刷新令牌
// 例如：后台定时任务运行数小时也没问题
while (true) {
    val users = sdk.search.getPopular()
    processUsers(users)
    delay(60_000) // 每分钟执行一次
}
```

### 其他初始化方式

除了 DSL 方式，还支持其他初始化方法：

#### 方式 2: 已有 apiKey

如果你已经从 VDS 开发者控制台获取了 apiKey，可以直接使用：

```kotlin
val sdk = FursuitTvSdk.create(apiKey = "your-existing-api-key")
```

#### 方式 3: 显式签名交换

需要先创建 SDK 实例，再手动调用签名交换：

```kotlin
val sdk = FursuitTvSdk.createForTokenExchange(
    clientId = "vap_xxx",
    clientSecret = "your-secret"
)

// 此时已完成签名交换，可以查看返回的 TokenInfo
println("API Key: ${sdk.auth.getApiKey()?.take(20)}...")
println("Access Token: ${sdk.auth.getAccessToken()?.take(20)}...")
```

## 方式二：OAuth 2.0 授权码

> **⚠️ 重要前置条件**
>
> 在使用 OAuth 流程之前，**必须先完成签名交换**以获取 platformAccessToken。
>
> 如果未完成前置条件就调用 loginWithOAuth()，将抛出 IllegalStateException：
> ```
> IllegalStateException: 未找到平台签名。请先调用 exchangeToken(clientId, clientSecret) 完成签名交换。
> ```

### 使用场景

- 🖥️ 桌面应用（需要用户登录授权）
- 📱 移动应用（iOS/Android）
- 🌐 Web 应用（单页应用）
- 需要获取**用户身份信息**的场景（不仅仅是业务数据）

### OAuth 授权码流程

完整的 OAuth 2.0 授权码流程包含 4 个步骤：

#### Step 1: 生成授权 URL

SDK 根据你的配置生成 VDS 授权页面 URL：

```kotlin
val authorizeUrl = sdk.auth.getOAuthAuthorizeUrl(
    redirectUri = "http://localhost:8080/callback",
    state = "random-state-string",  // CSRF 防护
    enablePkce = true
)
// 输出: https://open-global.vdsentnet.com/api/proxy/account/sso/authorize?...
```

#### Step 2: 用户授权

打开浏览器跳转到授权 URL，用户在 VDS 页面登录并授权。

#### Step 3: 接收回调

用户授权后，VDS 会重定向到你指定的回调 URL，携带 authorization code：

```
http://localhost:8080/callback?code=AUTHORIZATION_CODE&state=random-state-string
```

#### Step 4: 交换令牌

使用收到的 code 向 VDS 交换用户的访问令牌（需要平台签名作为认证头）：

```kotlin
val tokenInfo = sdk.auth.exchangeOAuthToken(
    code = receivedCode,
    redirectUri = "http://localhost:8080/callback"
)
// 返回 oauthAccessToken + refreshToken
```

### PKCE 安全增强

SDK 支持 PKCE（Proof Key for Code Exchange）来防止授权码拦截攻击：

**启用方式**：`loginWithOAuth()` 默认启用 PKCE；手动流程在 `getOAuthAuthorizeUrl()` 中设置 `enablePkce = true`

PKCE 工作流程：
1. SDK 生成随机的 `code_verifier`（43-128 字符）
2. 计算 `code_challenge = SHA256(code_verifier)` 的 Base64URL 编码
3. 授权请求中包含 code_challenge
4. 令牌交换时提交 code_verifier 进行验证

> **推荐**: 生产环境务必启用 PKCE（`loginWithOAuth()` 默认已启用）

### 自动化流程：loginWithOAuth()

为了简化 OAuth 流程，SDK 提供了 `loginWithOAuth()` 方法，一站式处理整个流程：

```kotlin
import com.furrist.rp.furtv.sdk.auth.OAuthCallbackServerConfig
import com.furrist.rp.furtv.sdk.auth.createDefaultOAuthHandler

// Step 1: 初始化 SDK（自动完成签名交换）
val sdk = fursuitTvSdk {
    clientId = "vap_xxxxxxxxxxxxxxxx"
    clientSecret = "your-client-secret-here"
}

// Step 2: 配置回调处理器（可选，默认使用平台默认实现）
val handler = createDefaultOAuthHandler(
    OAuthCallbackServerConfig(
        callbackHost = "localhost",
        callbackPort = 8080,
        callbackPath = "/callback",
        timeoutSeconds = 300
    )
)
sdk.auth.setOAuthCallbackHandler(handler)

// Step 3: 启动 OAuth 流程（自动处理回调监听、浏览器、令牌交换）
val tokenInfo = sdk.auth.loginWithOAuth()
println("OAuth 成功！Token 类型: ${tokenInfo.tokenType}")

// Step 4: 获取用户信息（使用双认证头）
val userInfo = sdk.auth.getUserInfo()
println("用户昵称: ${userInfo.nickname}")
println("用户名: ${userInfo.username}")

sdk.close()
```

**loginWithOAuth() 自动完成的操作**：
1. 自动生成 state 和 PKCE 参数
2. 调用回调处理器的 `startAndGetCallback()` 启动监听并引导用户
3. 等待用户授权并接收回调
4. 验证 state 参数（CSRF 防护）
5. 使用 authorization code 交换令牌
6. 返回 TokenInfo 对象

### OAuthCallbackHandler 接口

`loginWithOAuth()` 通过 `OAuthCallbackHandler` 接口处理回调，支持自定义实现：

```kotlin
public interface OAuthCallbackHandler {
    public val callbackUrl: String
    public suspend fun startListening()
    public suspend fun waitForCallback(): OAuthCallbackResult
    public suspend fun startAndGetCallback(authorizeUrl: String): OAuthCallbackResult
    public suspend fun stop()
}
```

通过 `setOAuthCallbackHandler()` 设置自定义处理器。

### 手动流程 vs 自动流程

| 特性 | 手动流程 | 自动流程 (loginWithOAuth) |
|------|---------|---------------------|
| 适用场景 | Web 服务器、自定义 UI | 桌面/移动应用、快速原型 |
| 回调处理 | 自己实现 HTTP 端点 | SDK 通过 OAuthCallbackHandler 自动处理 |
| 浏览器控制 | 可嵌入 WebView | 自动打开系统浏览器 |
| 代码量 | ~50 行 | ~10 行 |
| 推荐度 | 生产环境 Web 应用 | 快速开发、桌面应用 |

## 令牌类型总览

SDK 中涉及 4 种不同的令牌，每种有不同的来源和用途：

| 令牌类型 | 来源 | 用途 | 认证头 | 有效期 | 典型使用场景 |
|---------|------|------|--------|--------|-------------|
| **apiKey** | 签名交换返回 | 业务 API 调用（**推荐**） | `X-Api-Key` | 与 accessToken 相同 (~2h) | 所有业务 API |
| **accessToken** (platform) | 签名交换返回 | OAuth 接口认证 + 业务 API 备选 | `Authorization: Bearer` | ~2 小时 | OAuth token 交换的凭证 |
| **oauthAccessToken** | OAuth 授权码流程 | **仅用于 getUserInfo()** | `X-OAuth-Access-Token` | ~2 小时 | 获取用户身份信息 |
| **platformAccessToken** | 签名交换内部保存 | OAuth token exchange/refresh 的凭证 | `Authorization: Bearer` | 与 accessToken 相同 | 内部使用，开发者无需关心 |

### 认证头机制说明

#### 业务 API（user/search/gathering/school 等）

无论是否完成 OAuth，业务 API 始终使用**平台签名**认证：

```
GET /api/user/profile/exampleUser HTTP/1.1
X-Api-Key: <apiKey>                              ← 平台签名（优先）
Authorization: Bearer <platformAccessToken>       ← 备选
```

> ⚠️ OAuth 获取的 oauthAccessToken **不能**用于调用业务 API，业务 API 使用的是平台签名。

#### OAuth API（getUserInfo 等）

OAuth 相关接口使用**双认证头**机制：

```
GET /api/proxy/account/sso/userinfo HTTP/1.1
Authorization: Bearer <platformAccessToken>     ← 验证应用身份（平台签名）
X-OAuth-Access-Token: <oauthAccessToken>       ← 标识用户身份（OAuth 令牌）
```

**为什么需要两个头？**
- `Authorization: Bearer` - 证明调用者是合法的应用（通过签名交换验证）
- `X-OAuth-Access-Token` - 标识要查询哪个用户的信息（通过 OAuth 授权获取）

这种设计确保了：
- ✅ 只有经过认证的应用才能查询用户信息
- ✅ 应用只能查询已授权的用户信息
- ✅ 符合 OAuth 2.0 的最佳实践

## 最佳实践

### 如何选择认证方式？

根据你的应用类型选择合适的认证方式：

```
┌─────────────────────────────────┐
│ 你的应用是什么类型？             │
└─────────────┬───────────────────┘
              │
   ┌──────────┼──────────┐
   ▼          ▼          ▼
服务端     客户端     已有 apiKey
应用       应用       (无需签名交换)
   │          │          │
   ▼          ▼          ▼
签名交换   签名交换    直接使用
+         +         apiKey
(仅此即可)  OAuth      create()
            │
            ▼
        获取用户信息
```

**简单总结**:
- 只调用业务 API（user/search/gathering/school）→ **仅需签名交换**
- 需要获取用户登录信息 → **签名交换 + OAuth**
- 已经有 VDS 颁发的 apiKey → **直接使用，无需任何交换**

### 安全建议

#### 1. 凭证管理

⚠️ **千万不要**将 clientSecret 硬编码在代码中！

```kotlin
// ❌ 错误做法：硬编码密钥
val secret = "my-hardcoded-secret"

// ✅ 正确做法：使用环境变量或密钥管理系统
val secret = System.getenv("FURSUITTV_CLIENT_SECRET")
// 或从配置文件读取（确保 .gitignore）
```

#### 2. 生产环境配置

```kotlin
val sdk = fursuitTvSdk {
    clientId = System.getenv("FURSUITTV_CLIENT_ID")!!
    clientSecret = System.getenv("FURSUITTV_CLIENT_SECRET")!!
    requestTimeout = 30_000L     // 30秒超时
    enableRetry = true           // 启用重试
    maxRetries = 3               // 最大重试3次
    logLevel = SdkLogLevel.ERROR // 生产环境使用 ERROR 或 OFF
}
```

#### 3. 资源释放

始终在不再需要时调用 `sdk.close()` 释放资源：

```kotlin
val sdk = fursuitTvSdk { ... }
try {
    // 使用 SDK
} finally {
    sdk.close()  // 关闭 HTTP 客户端，释放连接
}
```

Kotlin 推荐使用 `.use` 扩展（如果实现了 Closeable）或 try-finally。

### 常见错误与解决方案

#### 错误 1: 未完成签名交换就调用 OAuth

**症状**: 抛出 `IllegalStateException: 未找到平台签名。请先调用 exchangeToken(clientId, clientSecret) 完成签名交换。`

**原因**: 直接调用 `loginWithOAuth()` 但之前没有完成签名交换

**解决方案**:
```kotlin
// ❌ 错误：未完成签名交换就尝试 OAuth
// 如果 SDK 未配置 clientId + clientSecret，loginWithOAuth() 将抛出异常

// ✅ 正确：先通过 DSL 初始化（自动完成签名交换）
val sdk = fursuitTvSdk { clientId = "..."; clientSecret = "..." }
sdk.auth.loginWithOAuth()  // ✅ 正常工作
```

#### 错误 2: 使用 oauthAccessToken 调用业务 API

**症状**: 返回 `401 Unauthorized` 错误

**原因**: oauthAccessToken 只能用于 `getUserInfo()`，不能用于其他 API。业务 API 使用平台签名（apiKey 或 platformAccessToken）。

**解决方案**:
```kotlin
val sdk = fursuitTvSdk { ... }
sdk.auth.loginWithOAuth()

// ❌ 错误：试图用 oauthToken 调用业务 API（内部实现不会这样做，但概念上要理解）
// SDK 会自动使用正确的令牌（apiKey 或 platform accessToken）

// ✅ 正确：直接调用 API，SDK 会自动选择正确的认证头
val profile = sdk.user.getUserProfile("username")  // 使用平台签名（apiKey）
val userInfo = sdk.auth.getUserInfo()              // 使用双认证头
```

#### 错误 3: 忘记关闭 SDK 导致资源泄漏

**症状**: 长时间运行后出现连接池耗尽警告

**原因**: 未调用 `sdk.close()` 释放 HTTP 客户端资源

**解决方案**:
```kotlin
// 在应用退出前或不再需要时
runtime.addShutdownHook(Thread {
    sdk.close()
})
```

#### 错误 4: clientId/clientSecret 格式错误

**症状**: 返回 `400 Bad Request` 或认证失败

**原因**: 
- clientId 格式不正确（应以 `vap_` 开头）
- clientSecret 包含特殊字符未正确转义
- 凭证已过期或被撤销

**解决方案**:
1. 检查 [VDS 开发者控制台](https://vds.com) 确认凭证正确
2. 确保 clientId 格式为 `vap_xxxxxxxxxxxxxxxx`（32位十六进制）
3. 如果怀疑凭证泄露，立即在控制台中轮换（regenerate）

## 相关资源

- [OAuth 详细指南](oauth-guide.md) - OAuth 2.0 完整流程和安全考虑
- [Auth API 参考](api/auth.md) - AuthManager 所有方法的详细文档
- [SDK 配置选项](configuration.md) - 所有配置参数说明
- [错误处理指南](error-handling.md) - 异常类型和处理策略
