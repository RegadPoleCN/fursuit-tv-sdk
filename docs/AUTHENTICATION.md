# 认证方式说明

## 认证方式概述

Fursuit.TV SDK 支持两种认证方式：

1. **Client 认证**：适用于应用级 API，使用 `X-Api-Key` 或 `Authorization: Bearer` 认证头
2. **OAuth 认证**：适用于用户授权场景，仅可用于 UserInfo 接口

## Client 认证

### 1. 使用 apiKey

**认证头**: `X-Api-Key: your-api-key`

**适用场景**：应用级 API，简单的服务端到服务端调用

```kotlin
val sdk = FursuitTvSdk(apiKey = "your-api-key")
```

### 2. 使用 appId + appSecret（推荐）

**认证头**: `X-Api-Key: <apiKey>`（优先）或 `Authorization: Bearer <accessToken>`

**适用场景**：应用级 API，支持令牌自动刷新

```kotlin
val sdk = FursuitTvSdk(
    appId = "vap_xxxxxxxxxxxxxxxx",
    appSecret = "your-app-secret"
)

runBlocking {
    sdk.auth.exchangeToken(appId, appSecret)
}
```

### 3. 使用 accessToken

**认证头**: `Authorization: Bearer <accessToken>`

**适用场景**：应用级 API，适用于已有访问令牌的用户

```kotlin
val sdk = FursuitTvSdk(accessToken = "your-access-token")
```

## OAuth 认证

**认证头**: `Authorization: Bearer <oauth-token>`

**适用场景**：用户授权场景（仅可用于 UserInfo 接口）

**注意**：OAuth token 和 Client token 不通用，不能混用

**使用流程**：

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

## 认证方式对比

| 特性 | Client 认证 | OAuth 认证 |
|------|------------|-----------|
| **端点** | `POST /api/auth/token` | `GET /api/proxy/account/sso/authorize` |
| **参数** | `appId` + `appSecret` | 仅 `appId` |
| **返回令牌** | `accessToken` 和 `apiKey`（两个不同的值） | `access_token`（OAuth 专用） |
| **认证头** | `X-Api-Key`（优先）或 `Authorization: Bearer` | `Authorization: Bearer` |
| **是否需要用户授权** | 否 | 是 |
| **令牌刷新** | 支持（剩余 <= 300 秒自动刷新） | 支持（使用 refresh_token） |
| **适用场景** | 服务端到服务端调用 | 需要用户授权的应用 |

## 重要说明

- **apiKey 和 accessToken 是两个不同的值**：apiKey 用于 `X-Api-Key` 认证头，accessToken 用于 `Authorization: Bearer` 认证头
- **服务端优先使用 X-Api-Key**：当同时传入两种认证头时，服务端优先使用 `X-Api-Key`
- **OAuth token 限制**：OAuth token 仅可用于 UserInfo 接口，不能用于其他 API
- **令牌自动刷新**：使用 appId + appSecret 初始化时，SDK 会自动管理令牌刷新（当剩余有效期 <= 300 秒时自动刷新）

## 相关文档

- [VDS 认证方式与服务器端点](../vds-docs/认证方式与服务器端点.md)
- [VDS 签名交换](../vds-docs/基础接口/签名交换.md)
- [VDS 签名换新](../vds-docs/基础接口/签名换新.md)
