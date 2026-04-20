# 认证 API (Auth)

认证模块提供令牌交换、OAuth 授权、令牌刷新和用户信息获取。

## API 方法

### exchangeToken(clientId, clientSecret)

签名交换获取令牌

- **端点**: `POST /api/auth/token`
- **参数**: 
  - `clientId` (String): 应用 ID（vap_xxxx）
  - `clientSecret` (String): 应用密钥
- **返回**: `TokenInfo`（accessToken, apiKey, expiresAt, refreshToken）

### refreshToken()

刷新访问令牌

- **端点**: `POST /api/auth/token/refresh`
- **返回**: `TokenInfo` - 新令牌

### getValidAccessToken(clientId, clientSecret)

获取有效令牌（自动检查和刷新）

- **逻辑**: 
  1. 无令牌或过期 → exchangeToken
  2. 有效期 ≤ 300 秒 → 刷新
  3. 刷新失败 → exchangeToken
- **返回**: `String` - 有效令牌

### getOAuthAuthorizeUrl(redirectUri, scope, state, enablePkce)

生成 OAuth 授权 URL

- **端点**: `GET /api/proxy/account/sso/authorize`
- **参数**:
  - `redirectUri` (String): 回调 URI
  - `scope` (String?, 可选): 权限范围
  - `state` (String?, 可选): CSRF 防护
  - `enablePkce` (Boolean, 默认 true): 启用 PKCE
- **返回**: `String` - 授权 URL

### exchangeOAuthToken(code, redirectUri, codeVerifier)

OAuth 令牌交换

- **端点**: `POST /api/proxy/account/sso/token`
- **参数**:
  - `code` (String): 授权码
  - `redirectUri` (String): 回调 URI
  - `codeVerifier` (String?, 可选): PKCE 验证器
- **返回**: `TokenInfo` - OAuth 令牌

### refreshOAuthToken()

刷新 OAuth 令牌

- **端点**: `POST /api/proxy/account/sso/token`
- **返回**: `TokenInfo`

### getUserInfo()

获取 OAuth 用户信息

- **端点**: `GET /api/proxy/account/sso/userinfo`
- **返回**: `UserInfo`（sub, nickname, avatarUrl, email, username）

### getAccessToken() / getApiKey()

获取当前令牌

- `getAccessToken()`: 访问令牌
- `getApiKey()`: API 密钥
- `isAuthenticated()`: 是否已认证

## 数据模型

### TokenInfo

| 字段 | 类型 | 说明 |
|------|------|------|
| accessToken | String | 访问令牌 |
| apiKey | String | API 密钥 |
| expiresAt | Long | 过期时间戳 |
| tokenType | String | 令牌类型（Bearer） |
| refreshToken | String? | 刷新令牌 |

### UserInfo

| 字段 | 类型 | 说明 |
|------|------|------|
| sub | String | 用户 ID |
| nickname | String | 昵称 |
| avatarUrl | String? | 头像 |
| email | String? | 邮箱 |
| username | String | 用户名 |

## 认证头

| 方式 | 认证头 |
|------|--------|
| apiKey | `X-Api-Key` |
| accessToken | `Authorization: Bearer` |
| OAuth | `Authorization: Bearer` |

## 相关文档

- [OAuth 指南](../oauth-guide.md)
- [错误处理](../error-handling.md)
