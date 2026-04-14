# 认证 API (Auth)

认证模块提供完整的认证与授权功能，包括令牌交换、OAuth 授权、令牌刷新和用户信息获取。

## 初始化方式

### 1. 使用 apiKey

```kotlin
val sdk = FursuitTvSdk(apiKey = "your-api-key")
```

### 2. 使用 appId + appSecret（推荐）

```kotlin
val sdk = FursuitTvSdk(
    appId = "vap_xxxxxxxxxxxxxxxx",
    appSecret = "your-app-secret"
)

// 获取初始令牌
runBlocking {
    sdk.auth.exchangeToken(appId, appSecret)
}
```

### 3. 使用 accessToken

```kotlin
val sdk = FursuitTvSdk(accessToken = "your-access-token")
```

### 4. OAuth 2.0 授权

```kotlin
val config = OAuthConfig(
    callbackHost = "localhost",
    callbackPort = 8080,
    callbackPath = "/callback"
)

val sdk = FursuitTvSdk()
val oauthResult = sdk.auth.initWithOAuth(
    appId = "vap_xxxxxxxxxxxxxxxx",
    config = config
)
```

## API 方法

### exchangeToken(appId, appSecret)

**签名交换** - 使用 appId 和 appSecret 获取令牌

- **端点**: `POST /api/auth/token`
- **方法**: `suspend fun`
- **参数**:
  - `appId` (String): 应用 ID（格式 vap_xxxx）
  - `appSecret` (String): 应用密钥
- **返回**: `TokenInfo` - 包含 accessToken 和 apiKey
- **异常**: `ApiException` - 当请求失败时

**示例**:

```kotlin
val tokenInfo = sdk.auth.exchangeToken(appId, appSecret)
println("accessToken: ${tokenInfo.accessToken}")
println("apiKey: ${tokenInfo.apiKey}")
println("有效期：${tokenInfo.expiresAt}")
```

### refreshToken()

**刷新令牌** - 使用当前访问令牌刷新获取新的访问令牌

- **端点**: `POST /api/auth/token/refresh`
- **方法**: `suspend fun`
- **参数**: 无
- **返回**: `TokenInfo` - 新的令牌信息
- **异常**: `TokenExpiredException` - 如果没有可用的令牌

**示例**:

```kotlin
val newToken = sdk.auth.refreshToken()
```

### getValidAccessToken(appId, appSecret)

**获取有效的访问令牌（自动刷新）**

自动检查和刷新逻辑：
1. 如果没有令牌或已过期：调用 exchangeToken
2. 如果剩余有效期 <= 300 秒：自动刷新
3. 如果刷新失败：回退到 exchangeToken

- **方法**: `suspend fun`
- **参数**:
  - `appId` (String): 应用 ID
  - `appSecret` (String): 应用密钥
- **返回**: `String` - 有效的访问令牌

**示例**:

```kotlin
val accessToken = sdk.auth.getValidAccessToken(appId, appSecret)
```

### initWithOAuth(appId, config)

**OAuth 2.0 授权** - 完整的 OAuth 授权流程

- **方法**: `suspend fun`
- **参数**:
  - `appId` (String): 应用 ID
  - `config` (OAuthConfig): OAuth 配置
  - `scope` (String?): 权限范围（可选）
- **返回**: `TokenInfo` - OAuth 令牌信息
- **异常**: `OAuthCallbackException` - 当回调失败时

**示例**:

```kotlin
val config = OAuthConfig(
    callbackHost = "localhost",
    callbackPort = 8080
)

val tokenInfo = sdk.auth.initWithOAuth(appId, config)
```

### getUserInfo()

**获取用户信息** - 获取当前 OAuth 认证用户的详细信息

- **端点**: `GET /api/proxy/account/sso/userinfo`
- **方法**: `suspend fun`
- **参数**: 无
- **返回**: `UserInfoData` - 用户信息数据
- **异常**: `OAuthCallbackException` - 当认证失败时

**响应字段**:
- `sub`: 用户唯一标识符
- `nickname`: 用户昵称
- `avatarUrl`: 用户头像 URL
- `email`: 用户邮箱
- `username`: 用户名

**示例**:

```kotlin
val userInfo = sdk.auth.getUserInfo()
println("用户昵称：${userInfo.nickname}")
println("用户 ID: ${userInfo.sub}")
```

### getAccessToken() / getApiKey()

**获取当前令牌**

- `getAccessToken()`: 返回当前访问令牌
- `getApiKey()`: 返回当前 API 密钥
- `isAuthenticated()`: 检查是否已认证

**示例**:

```kotlin
if (sdk.auth.isAuthenticated()) {
    val token = sdk.auth.getAccessToken()
}
```

## 数据模型

### TokenInfo

```kotlin
public data class TokenInfo(
    public val accessToken: String,
    public val apiKey: String,
    public val expiresAt: Long,
    public val tokenType: String = "Bearer",
    public val refreshToken: String? = null
)
```

### OAuthConfig

```kotlin
public data class OAuthConfig(
    public val callbackHost: String = "localhost",
    public val callbackPort: Int = 8080,
    public val callbackPath: String = "/callback",
    public val stateTimeoutMinutes: Int = 10,
    public val enablePkce: Boolean = false
)
```

## 认证头说明

| 初始化方式 | 认证头 | 说明 |
|-----------|--------|------|
| apiKey | `X-Api-Key` | 简单的 API 密钥认证 |
| appId + appSecret | `Authorization: Bearer` | 签名认证，支持自动刷新 |
| OAuth | `Authorization: Bearer` | OAuth 2.0 授权码模式 |
| accessToken | `Authorization: Bearer` | 已有访问令牌 |

## 自动令牌刷新

SDK 会自动管理令牌刷新：

```kotlin
val sdk = FursuitTvSdk(appId = appId, appSecret = appSecret)

// SDK 会在以下情况自动刷新令牌：
// 1. 令牌剩余有效期 <= 300 秒（5 分钟）
// 2. 每次 API 调用前自动检查
// 3. 刷新失败会自动回退到重新获取
```

## 相关文档

- [认证与配置](../authentication.md) - 详细的认证方式和配置
- [OAuth 实现指南](../oauth.md) - OAuth 流程详解
- [故障排除](../TROUBLESHOOTING.md) - 认证常见问题
