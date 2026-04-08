# 认证方式修正说明

## 重要更正

**apiKey 和 accessToken 是两个不同的值！**

根据 VDS 官方文档，签名交换接口（POST /api/auth/token）返回的响应中包含两个**不同**的字段：

```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiIs...",
  "apiKey": "eyJhbGciOiJIUzI1NiIs...",
  "tokenType": "Bearer",
  "expiresInSeconds": 3600,
  "appId": "vap_xxxxxxxxxxxxxxxx",
  "grants": ["furtv", "furtv.gathering.timeline"],
  "requestId": "6ff8d966-b3f6-46a6-9fe3-24fd6553ef52"
}
```

## 认证方式说明

### 1. 签名认证（Signature Authentication）

**端点**: `POST /api/auth/token`

**参数**:
- `appId` 或 `clientId`（二选一）
- `clientSecret`

**返回**:
- `accessToken`: 用于 `Authorization: Bearer <accessToken>` 认证头
- `apiKey`: 用于 `X-Api-Key: <apiKey>` 认证头
- **两者是不同的值**

**认证头选择**:
- 可使用 `X-Api-Key: <apiKey>`（推荐）
- 或使用 `Authorization: Bearer <accessToken>`
- 同时传入时，服务端优先使用 `X-Api-Key`

### 2. OAuth 流程（OAuth 2.0 Authorization Code Flow）

**授权端点**: `GET /api/proxy/account/sso/authorize`

**令牌交换**: `POST /api/proxy/account/sso/token`

**参数**:
- 仅需要 `appId`，不需要 `appSecret`

**返回**:
- `access_token`: OAuth 专用访问令牌

**认证头**:
- 仅使用 `Authorization: Bearer <access_token>`

## SDK 使用方式

### 方式 1: 使用 appId + appSecret（签名认证）

```kotlin
val sdk = FursuitTvSdk(
    appId = "vap_xxxxxxxxxxxxxxxx",
    appSecret = "your-app-secret"
)

// 获取令牌
val tokenInfo = sdk.auth.exchangeToken(appId, appSecret)

// 获取 accessToken（用于 Authorization 头）
val accessToken = sdk.auth.getAccessToken()

// 获取 apiKey（用于 X-Api-Key 头）
val apiKey = sdk.auth.getApiKey()

// 自动获取有效令牌（带刷新）
val validToken = sdk.auth.getValidAccessToken(appId, appSecret)
```

### 方式 2: 使用 OAuth（需要用户授权）

```kotlin
val config = OAuthConfig(callbackHost = "localhost", callbackPort = 8080)
val sdk = FursuitTvSdk.initWithOAuth("vap_xxxxxxxxxxxxxxxx", config)

// OAuth 流程仅返回 access_token
val accessToken = sdk.auth.getAccessToken()
```

### 方式 3: 直接使用 apiKey

```kotlin
val sdk = FursuitTvSdk(apiKey = "your-api-key")
// SDK 会使用 X-Api-Key 头
```

### 方式 4: 直接使用 accessToken

```kotlin
val sdk = FursuitTvSdk(accessToken = "your-access-token")
// SDK 会使用 Authorization: Bearer 头
```

## 认证方式对比

| 特性 | 签名认证 | OAuth 流程 |
|------|---------|-----------|
| **端点** | `POST /api/auth/token` | `GET /api/proxy/account/sso/authorize` |
| **参数** | `appId` + `appSecret` | 仅 `appId` |
| **返回令牌** | `accessToken` 和 `apiKey`（两个不同的值） | `access_token`（OAuth 专用） |
| **认证头** | `X-Api-Key`（优先）或 `Authorization: Bearer` | `Authorization: Bearer` |
| **是否需要用户授权** | 否 | 是 |
| **令牌刷新** | 支持（剩余 <= 300 秒自动刷新） | 支持（使用 refresh_token） |
| **适用场景** | 服务端到服务端调用 | 需要用户授权的应用 |

## 已修改的文件

### 源代码文件

1. **AuthModels.kt**
   - `TokenData` 添加 `apiKey` 字段
   - `TokenInfo` 添加 `apiKey` 字段
   - 更新注释说明两者是不同的值

2. **AuthManager.kt**
   - 添加 `getApiKey()` 方法获取 apiKey
   - 重命名 `getApiKey(appId, appSecret)` 为 `getValidAccessToken(appId, appSecret)`
   - 更新 `exchangeToken()` 方法处理返回的 apiKey
   - 更新 `refreshToken()` 方法处理返回的 apiKey
   - 更新 `updateHttpClient()` 方法使用正确的令牌

### 待更新的文档文件

以下文档文件需要更新以反映正确的认证方式：

- docs/README.md
- docs/configuration.md
- docs/examples.md
- docs/endpoints.md
- docs/AUTHENTICATION.md

## 关键修改点

### 之前的错误理解

❌ apiKey 和 accessToken 是同一个值

### 正确的理解

✅ apiKey 和 accessToken 是签名交换接口返回的两个不同的值
✅ apiKey 用于 X-Api-Key 认证头
✅ accessToken 用于 Authorization: Bearer 认证头
✅ 服务端优先使用 X-Api-Key 认证头

## 相关文档

- [VDS 认证方式与服务器端点](../vds-docs/认证方式与服务器端点.md)
- [VDS 签名交换](../vds-docs/基础接口/签名交换.md)
- [VDS 签名换新](../vds-docs/基础接口/签名换新.md)
