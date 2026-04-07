# 认证 API

认证 API 模块提供了用户认证相关的功能，包括 OAuth 2.0 授权、令牌交换、令牌刷新、自动令牌刷新、OAuth 本地回调等。

## 认证方式说明

SDK 支持两种认证头方式，根据初始化方式自动选择：

### X-Api-Key vs Authorization: Bearer

**X-Api-Key**：
```
X-Api-Key: your-api-key
```
- 用于使用 `apiKey` 初始化的场景
- 更简洁，专门用于 API 密钥认证
- 适用于服务端到服务端调用

**Authorization: Bearer**：
```
Authorization: Bearer your-access-token
```
- OAuth 2.0 标准认证头格式，符合 RFC 6750 规范
- 用于以下场景：
  - 使用 `clientId` + `clientSecret` 初始化
  - 使用 OAuth 本地回调初始化
  - 使用 `accessToken` 直接初始化

### apiKey vs access_token

**apiKey**：
- 由 VDS 平台颁发的长期有效的 API 密钥
- 使用 `X-Api-Key` 请求头进行认证
- 适用于简单的服务端调用，无需用户授权
- 通过 `FursuitTvSdk(apiKey = "xxx")` 初始化

**access_token**：
- OAuth 2.0 流程获取的访问令牌，有有效期限制
- 使用 `Authorization: Bearer` 请求头进行认证
- 适用于需要用户授权的场景
- 通过 OAuth 流程、`clientId` + `clientSecret` 或其他方式获取
- 令牌即将过期时（剩余 <= 300 秒）会自动刷新

## OAuth 2.0 授权流程

完整的 OAuth 2.0 授权码模式流程如下：

### 步骤 1: 获取授权 URL

使用 [`getOAuthAuthorizeUrl`](#getoauthauthorizeurlclientid-redirecturl-scopes-state) 生成授权页面 URL。

### 步骤 2: 用户授权

将用户重定向到授权 URL，用户登录并同意授权。

### 步骤 3: 获取授权码

授权成功后，浏览器会重定向到 `redirectUrl`，并附带 `code` 参数。

### 步骤 4: 交换访问令牌

使用 [`exchangeOAuthToken`](#exchangeoauthtokenclientid-clientsecret-code-redirecturl) 通过授权码换取访问令牌。

### 步骤 5: 获取用户信息

使用 [`getUserInfo`](#getuserinfoaccessToken) 通过访问令牌获取用户基本信息。

## 类定义

```kotlin
class AuthManager(
    private val config: SdkConfig
)
```

## OAuth 本地回调服务器

### OAuthCallbackServer

**功能**：OAuth 回调服务器，用于接收 OAuth 授权回调并提取授权码。

**工作原理**：
1. 启动本地 HTTP 服务器监听指定端口
2. 生成随机 state 参数防止 CSRF 攻击
3. 等待授权回调
4. 收到回调后验证 state 并提取授权码
5. 自动关闭服务器

**示例**：

```kotlin
// 创建回调服务器
val server = OAuthCallbackServer(
    OAuthCallbackServerConfig(
        callbackHost = "localhost",
        callbackPort = 8080,
        callbackPath = "/callback",
        timeoutSeconds = 300
    )
)

// 生成 state
val state = server.generateState()

// 等待回调
val result = server.waitForCallback(state)
println("授权码：${result.code}")
```

### OAuthCallbackServerConfig

**功能**：OAuth 回调服务器配置。

**参数**：
- `callbackHost` (String): 回调主机地址，默认为 "localhost"
- `callbackPort` (Int): 回调端口，默认为 8080
- `callbackPath` (String): 回调路径，默认为 "/callback"
- `timeoutSeconds` (Long): 超时时间（秒），默认为 300 秒

**示例**：

```kotlin
val config = OAuthCallbackServerConfig(
    callbackHost = "127.0.0.1",
    callbackPort = 9000,
    callbackPath = "/oauth/callback",
    timeoutSeconds = 600
)
```

### OAuthCallbackResult

**功能**：OAuth 回调结果数据类。

**字段**：
- `code` (String): 授权码
- `state` (String): 状态参数

### OAuthConfig

**功能**：OAuth 配置数据类，用于简化 OAuth 本地回调配置。

**参数**：
- `callbackHost` (String): 回调主机，默认为 "localhost"
- `callbackPort` (Int): 回调端口，默认为 8080
- `callbackPath` (String): 回调路径，默认为 "/callback"
- `stateTimeoutMinutes` (Int): state 超时时间（分钟），默认为 10
- `enablePkce` (Boolean): 是否启用 PKCE，默认为 false

**示例**：

```kotlin
val config = OAuthConfig(
    callbackHost = "localhost",
    callbackPort = 8080,
    callbackPath = "/callback",
    stateTimeoutMinutes = 15,
    enablePkce = true
)
```

## 方法列表

### initWithOAuth(appId, config, scope)

**功能**：使用 OAuth 本地回调初始化 SDK，完整的 OAuth 授权流程。

**端点**：
- `GET /api/proxy/account/sso/authorize` - 生成授权 URL
- `POST /api/proxy/account/sso/token` - 交换访问令牌

**参数**：
- `appId` (String): 应用 ID
- `config` (OAuthConfig): OAuth 配置，包含回调服务器信息
- `scope` (String?): 可选的权限范围

**返回值**：`TokenInfo` - 包含访问令牌信息。

**OAuth 本地回调自动授权流程**：
1. SDK 创建 OAuthCallbackServer 并启动本地 HTTP 服务器
2. 生成随机 state 参数（32 字符十六进制字符串）
3. 构建授权 URL 并自动打开浏览器
4. 用户登录并同意授权
5. 授权成功后重定向到本地回调地址（如 `http://localhost:8080/callback`）
6. SDK 接收回调，验证 state 参数，提取授权码
7. 自动关闭本地服务器
8. 使用授权码和 appId 交换访问令牌
9. 返回 TokenInfo

**示例**：

```kotlin
// 配置 OAuth 回调
val config = OAuthConfig(
    callbackHost = "localhost",
    callbackPort = 8080,
    callbackPath = "/callback",
    stateTimeoutMinutes = 10
)

// 启动 OAuth 流程
val tokenInfo = sdk.auth.initWithOAuth(
    appId = "your-app-id",
    config = config,
    scope = "user:read user:write"
)

println("访问令牌：${tokenInfo.accessToken}")
println("有效期：${tokenInfo.expiresAt}")
```

**注意事项**：
- 确保回调端口未被占用
- 回调 URL 需要与在 Fursuit.TV 开发者平台注册的回调地址一致
- state 参数用于防止 CSRF 攻击，会自动验证
- 超时时间由 `config.stateTimeoutMinutes` 控制
- 如果用户取消授权或超时，会抛出 `OAuthCallbackError.TimeoutError`

**错误处理**：

```kotlin
try {
    val tokenInfo = sdk.auth.initWithOAuth("app-id", config)
} catch (e: OAuthCallbackError.TimeoutError) {
    println("OAuth 回调超时")
} catch (e: OAuthCallbackError.StateMismatchError) {
    println("State 验证失败，可能存在 CSRF 攻击")
} catch (e: Exception) {
    println("OAuth 流程失败：${e.message}")
}
```

### getOAuthAuthorizeUrl(clientId, redirectUrl, scopes, state)

**功能**：生成 OAuth 2.0 授权页面的 URL。

**参数**：
- `clientId` (String)：客户端 ID
- `redirectUrl` (String)：授权后的重定向 URL
- `scopes` (List<String>)：请求的权限范围列表
- `state` (String?)：可选的状态参数，用于防止 CSRF 攻击

**返回值**：`String` - 完整的授权 URL。

**示例**：

```kotlin
val authorizeUrl = sdk.auth.getOAuthAuthorizeUrl(
    clientId = "your-client-id",
    redirectUrl = "https://yourdomain.com/callback",
    scopes = listOf("user:read", "user:write"),
    state = "random-state-string"
)

// 将用户重定向到此 URL
println("授权 URL：$authorizeUrl")
```

**授权 URL 格式**：
```
https://fursuit.tv/oauth/authorize?
    client_id=your-client-id&
    redirect_uri=https://yourdomain.com/callback&
    response_type=code&
    scope=user:read%20user:write&
    state=random-state-string
```

**使用流程**：
1. 生成授权 URL
2. 在浏览器中打开该 URL
3. 用户登录并同意授权
4. 浏览器重定向到 redirectUrl，附带 code 参数
5. 使用 code 调用 `exchangeOAuthToken` 获取访问令牌

### exchangeOAuthToken(clientId, clientSecret, code, redirectUrl)

**功能**：通过 OAuth 授权码交换访问令牌。

**端点**：`POST /api/auth/oauth/token`

**参数**：
- `clientId` (String)：客户端 ID
- `clientSecret` (String)：客户端密钥
- `code` (String)：授权码（从回调 URL 获取）
- `redirectUrl` (String)：与授权时相同的重定向 URL

**返回值**：`TokenInfo` - 包含访问令牌信息的对象。

**示例**：

```kotlin
// 从回调 URL 获取 code 参数
val code = request.getParameter("code")

val tokenInfo = sdk.auth.exchangeOAuthToken(
    clientId = "your-client-id",
    clientSecret = "your-client-secret",
    code = code,
    redirectUrl = "https://yourdomain.com/callback"
)

println("访问令牌：${tokenInfo.accessToken}")
println("刷新令牌：${tokenInfo.refreshToken}")
println("有效期：${tokenInfo.expiresIn} 秒")
```

**响应字段说明**：
- `accessToken`: 访问令牌，用于 API 认证
- `refreshToken`: 刷新令牌，用于获取新的访问令牌
- `expiresIn`: 令牌有效期（秒）
- `tokenType`: 令牌类型，通常为 "Bearer"
- `scope`: 授权的权限范围

### getUserInfo(accessToken)

**功能**：通过访问令牌获取用户基本信息。

**端点**：`GET /api/auth/oauth/userinfo`

**参数**：
- `accessToken` (String)：访问令牌

**返回值**：`UserInfo` - 用户信息对象。

**示例**：

```kotlin
val userInfo = sdk.auth.getUserInfo(accessToken = "your-access-token")

println("用户 ID: ${userInfo.id}")
println("用户名：${userInfo.username}")
println("邮箱：${userInfo.email}")
println("头像：${userInfo.avatarUrl}")
```

**响应字段说明**：
- `id`: 用户 ID
- `username`: 用户名
- `email`: 邮箱地址
- `avatarUrl`: 头像 URL
- `createdAt`: 账号创建时间

### exchangeToken(clientId, clientSecret)

**功能**：通过客户端 ID 和客户端密钥交换访问令牌。

**端点**：`POST /api/auth/token`

**参数**：
- `clientId` (String)：客户端 ID
- `clientSecret` (String)：客户端密钥

**返回值**：`TokenInfo` - 包含访问令牌信息的对象。

**示例**：

```kotlin
val tokenInfo = sdk.auth.exchangeToken(
    clientId = "your-client-id",
    clientSecret = "your-client-secret"
)

println("访问令牌：${tokenInfo.accessToken}")
println("令牌类型：${tokenInfo.tokenType}")
println("有效期：${tokenInfo.expiresIn} 秒")
```

**响应字段说明**：
- `accessToken`: 访问令牌，用于 API 认证
- `expiresIn`: 令牌有效期（秒）
- `tokenType`: 令牌类型，通常为 "Bearer"

### refreshToken()

**功能**：刷新当前的访问令牌。当令牌剩余有效期 <= 300 秒（5 分钟）时调用。

**端点**：`POST /api/auth/token/refresh`

**返回值**：`TokenInfo` - 包含新的访问令牌信息的对象。

**示例**：

```kotlin
val refreshedToken = sdk.auth.refreshToken()
println("新令牌：${refreshedToken.accessToken}")
println("新有效期：${refreshedToken.expiresIn} 秒")
```

**注意**：
- 刷新接口使用当前的访问令牌进行认证（通过 Authorization header）
- 不需要请求体
- 刷新成功后会更新 SDK 内部的令牌存储

### getApiKey(clientId, clientSecret)

**功能**：获取 API 密钥（自动刷新）。提供统一的令牌获取方法，实现自动检查和刷新逻辑。

**参数**：
- `clientId` (String)：客户端 ID（用于回退）
- `clientSecret` (String)：客户端密钥（用于回退）

**返回值**：`String` - 当前或刷新后的访问令牌。

**示例**：

```kotlin
val apiKey = sdk.auth.getApiKey(
    clientId = "your-client-id",
    clientSecret = "your-client-secret"
)

println("API Key: $apiKey")
```

**自动刷新逻辑**：
1. 如果没有令牌或已过期，调用 `exchangeToken`
2. 如果剩余有效期 <= 300 秒，调用 `refreshToken`
3. 如果刷新失败，回退到 `exchangeToken`

**推荐使用此方法进行 API 调用前的令牌检查。**

### getAccessToken()

**功能**：获取当前的访问令牌。

**返回值**：`String?` - 当前的访问令牌，如果没有则返回 null。

**示例**：

```kotlin
val accessToken = sdk.auth.getAccessToken()
if (accessToken != null) {
    println("当前访问令牌：$accessToken")
} else {
    println("没有可用的访问令牌")
}
```

### setTokenInfo(tokenInfo)

**功能**：手动设置令牌信息。

**参数**：
- `tokenInfo` (TokenInfo)：令牌信息对象

**示例**：

```kotlin
val tokenInfo = TokenInfo(
    accessToken = "your-access-token",
    expiresAt = Clock.System.now().toEpochMilliseconds() + 3600000,
    tokenType = "Bearer"
)
sdk.auth.setTokenInfo(tokenInfo)
```

### isAuthenticated()

**功能**：检查是否已认证（是否有可用的访问令牌）。

**返回值**：`Boolean` - 如果已认证返回 true，否则返回 false。

**示例**：

```kotlin
if (sdk.auth.isAuthenticated()) {
    println("已认证，可以调用 API")
} else {
    println("未认证，请先获取令牌")
}
```

### close()

**功能**：关闭认证管理器，清理资源。

**示例**：

```kotlin
sdk.auth.close()
```

## 数据模型

### TokenInfo

```kotlin
data class TokenInfo(
    val accessToken: String,
    val expiresAt: Long,      // 过期时间戳（毫秒）
    val tokenType: String
) {
    fun isExpired(): Boolean
    // 检查令牌是否过期（剩余时间 <= 300 秒）
}
```

**字段说明**：
- `accessToken`: 访问令牌
- `expiresAt`: 过期时间戳（毫秒），从 1970-01-01 00:00:00 UTC 开始
- `tokenType`: 令牌类型，通常为 "Bearer"

### TokenExchangeResponse

```kotlin
data class TokenExchangeResponse(
    val success: Boolean,
    val data: TokenData,
    val requestId: String
)

data class TokenData(
    val accessToken: String,
    val expiresIn: Int,
    val tokenType: String
)
```

### TokenRefreshResponse

```kotlin
data class TokenRefreshResponse(
    val success: Boolean,
    val data: TokenData,
    val requestId: String
)
```

## 认证流程

### 方式 1: 手动管理令牌

1. **获取令牌**：使用 `exchangeToken` 方法通过客户端 ID 和客户端密钥获取访问令牌。
2. **使用令牌**：SDK 会自动将访问令牌添加到请求头中，用于认证 API 请求。
3. **刷新令牌**：当访问令牌即将过期时（剩余 <= 300 秒），使用 `refreshToken` 方法刷新。
4. **手动设置令牌**：如果已经有令牌，可以使用 `setTokenInfo` 方法手动设置。

### 方式 2: 自动管理令牌（推荐）

使用 `getApiKey()` 方法自动管理令牌：

```kotlin
// 初始化 SDK
val sdk = FursuitTvSdk(
    clientId = "your-client-id",
    clientSecret = "your-client-secret"
)

// 获取初始令牌
runBlocking {
    sdk.auth.exchangeToken(clientId, clientSecret)
}

// 调用 API 前自动检查并刷新令牌
runBlocking {
    val apiKey = sdk.auth.getApiKey(clientId, clientSecret)
    // 现在可以安全地调用 API 了
    val userProfile = sdk.user.getUserProfile("username")
}
```

**优点**：
- 无需手动检查令牌是否过期
- 自动在令牌即将过期时刷新
- 刷新失败时自动回退到重新获取令牌
- 简化代码逻辑

## 错误处理

认证过程中可能会遇到以下错误：

- **TokenExpiredException**: 访问令牌已过期
  - 处理：调用 `refreshToken()` 或 `getApiKey()` 刷新令牌
  
- **AuthenticationException**: 认证失败（客户端 ID 或密钥错误）
  - 处理：检查凭证是否正确，重新调用 `exchangeToken()`
  
- **NetworkException**: 网络连接错误
  - 处理：检查网络状态，稍后重试
  
- **ApiException**: API 调用失败
  - 处理：根据状态码处理（400/401/500 等）

建议使用 try-catch 块捕获这些错误：

```kotlin
try {
    val tokenInfo = sdk.auth.exchangeToken("client-id", "client-secret")
    println("认证成功：${tokenInfo.accessToken}")
} catch (e: TokenExpiredException) {
    println("令牌过期，请刷新")
    val newToken = sdk.auth.refreshToken()
} catch (e: AuthenticationException) {
    println("认证失败：${e.message}")
} catch (e: NetworkException) {
    println("网络错误：${e.message}")
} catch (e: ApiException) {
    println("API 错误：${e.statusCode} - ${e.message}")
} catch (e: Exception) {
    println("未知错误：${e.message}")
}
```

## 最佳实践

1. **使用自动刷新**：推荐使用 `getApiKey()` 方法自动管理令牌，避免手动处理刷新逻辑。

2. **安全存储凭证**：不要将 `clientId` 和 `clientSecret` 硬编码在代码中，应该从安全的配置管理系统中获取。

3. **错误重试**：对于网络错误，可以实现重试机制，提高成功率。

4. **令牌持久化**：如果需要，可以将 `TokenInfo` 持久化到安全存储中，下次启动时恢复。

5. **定期检查**：对于长时间运行的应用，定期检查令牌状态并及时刷新。
