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
- **异常**: `AuthenticationException` - 凭证无效

### refreshToken()

刷新访问令牌

- **端点**: `POST /api/auth/token/refresh`
- **返回**: `TokenInfo` - 新令牌
- **异常**: `TokenExpiredException` - 没有可用的令牌

### getValidAccessToken(clientId, clientSecret)

获取有效令牌（自动检查和刷新）

- **逻辑**: 
  1. 无令牌或过期 → exchangeToken
  2. 有效期 ≤ 300 秒 → 刷新
  3. 刷新失败 → exchangeToken
- **参数**:
  - `clientId` (String): 应用 ID
  - `clientSecret` (String): 应用密钥
- **返回**: `String` - 有效令牌

### loginWithOAuth(scope)

执行完整的 OAuth 登录流程

- **流程**:
  1. 自动生成 state 和 PKCE 参数
  2. 调用回调处理器的 `startAndGetCallback()` 启动监听并引导用户
  3. 验证回调中的 state 参数（防止 CSRF 攻击）
  4. 使用 authorization code 交换令牌
- **参数**:
  - `scope` (String?, 可选): 权限范围
- **返回**: `TokenInfo` - OAuth 用户令牌
- **异常**:
  - `IllegalStateException` - 没有可用的回调处理器或未完成签名交换
  - `OAuthException` - state 验证失败或 OAuth 流程出错（含 `errorCode` 字段）

### setOAuthCallbackHandler(handler)

设置自定义 OAuth 回调处理器

- **参数**:
  - `handler` (OAuthCallbackHandler): 回调处理器实现

### getOAuthAuthorizeUrl(redirectUri, scope, state, enablePkce, codeChallenge)

生成 OAuth 授权 URL

- **端点**: `GET /api/proxy/account/sso/authorize`
- **参数**:
  - `redirectUri` (String): 回调 URI
  - `scope` (String?, 可选): 权限范围
  - `state` (String?, 可选): CSRF 防护
  - `enablePkce` (Boolean, 默认 true): 启用 PKCE
  - `codeChallenge` (String?, 可选): PKCE code_challenge
- **返回**: `String` - 授权 URL
- **异常**: `IllegalStateException` - 缺少 clientId

### exchangeOAuthToken(code, redirectUri, codeVerifier)

OAuth 令牌交换

- **端点**: `POST /api/proxy/account/sso/token`
- **认证头**: `Authorization: Bearer <platformAccessToken>`（需要平台签名）
- **参数**:
  - `code` (String): 授权码
  - `redirectUri` (String): 回调 URI
  - `codeVerifier` (String?, 可选): PKCE 验证器
- **返回**: `TokenInfo` - OAuth 令牌
- **异常**:
  - `IllegalStateException` - 未完成签名交换，缺少 platformAccessToken

### refreshOAuthToken()

刷新 OAuth 令牌

- **端点**: `POST /api/proxy/account/sso/token`
- **认证头**: `Authorization: Bearer <platformAccessToken>`（需要平台签名）
- **返回**: `TokenInfo`
- **异常**:
  - `TokenExpiredException` - 没有可用的 refreshToken
  - `IllegalStateException` - 未完成签名交换或缺少 OAuth 配置参数

### getUserInfo()

获取 OAuth 用户信息

- **端点**: `GET /api/proxy/account/sso/userinfo`
- **认证头**: 双认证头（`Authorization: Bearer <platformAccessToken>` + `X-OAuth-Access-Token: <oauthAccessToken>`）
- **返回**: `UserInfoData`（sub, nickname, avatarUrl, email, name, username, updatedAt, phoneNumber, iss, aud）

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
| expiresAt | Long | 过期时间戳（毫秒） |
| tokenType | String | 令牌类型（Bearer） |
| refreshToken | String? | 刷新令牌 |

### TokenData

| 字段 | 类型 | 说明 |
|------|------|------|
| accessToken | String | 访问令牌 |
| apiKey | String | API 密钥 |
| expiresIn | Int | 有效期（秒） |
| tokenType | String | 令牌类型（Bearer） |
| appId | String? | 应用 ID（vap_xxxx），SDK 中 clientId 与此等价 |
| grants | List\<String\>? | 授权的权限范围列表 |
| refresh | TokenRefreshInfo? | 令牌刷新信息 |

### TokenRefreshInfo

| 字段 | 类型 | 说明 |
|------|------|------|
| mode | String | 刷新模式 |
| refreshWindowSeconds | Int | 刷新窗口时间（秒） |
| previousTokenSecondsRemaining | Int | 旧令牌剩余有效期（秒） |

### UserInfoData

| 字段 | 类型 | 说明 |
|------|------|------|
| sub | String | 用户唯一标识符 |
| nickname | String? | 昵称 |
| avatarUrl | String? | 头像 URL |
| email | String? | 邮箱 |
| name | String? | 姓名 |
| username | String? | 用户名 |
| updatedAt | Long? | 更新时间戳（毫秒） |
| phoneNumber | String? | 电话号码 |
| iss | String? | 令牌签发者（issuer） |
| aud | Long? | 令牌受众（audience） |

### OAuthCallbackResult

| 变体 | 字段 | 说明 |
|------|------|------|
| `Success` | `code: String`, `state: String` | 授权成功，携带授权码和 state |
| `Error` | `message: String`, `errorCode: String?`, `cause: Throwable?` | 授权失败 |

### OAuthCallbackHandler

| 方法 | 说明 |
|------|------|
| `callbackUrl: String` | 回调接收地址 |
| `startListening()` | 启动回调监听 |
| `waitForCallback(): OAuthCallbackResult` | 等待回调结果 |
| `startAndGetCallback(authorizeUrl: String): OAuthCallbackResult` | 便捷方法：启动监听并等待回调 |
| `stop()` | 停止监听并释放资源 |

### OAuthCallbackServerConfig

| 字段 | 类型 | 默认值 | 说明 |
|------|------|--------|------|
| callbackHost | String | `"localhost"` | 监听主机名 |
| callbackPort | Int | `8080` | 监听端口 |
| callbackPath | String | `"/callback"` | 回调路径 |
| timeoutSeconds | Long | `300` | 超时时间（秒） |

## 认证头

| 场景 | 认证头 | 说明 |
|------|--------|------|
| 业务 API（签名交换） | `X-Api-Key: <apiKey>` | 优先使用 |
| 业务 API（备选） | `Authorization: Bearer <accessToken>` | apiKey 为空时使用 |
| 业务 API（OAuth 后） | `X-Api-Key: <apiKey>` | 仍使用平台签名，非 OAuth 令牌 |
| OAuth API（getUserInfo） | `Authorization: Bearer <platformAccessToken>` + `X-OAuth-Access-Token: <oauthAccessToken>` | 双认证头 |
| OAuth API（exchangeOAuthToken/refreshOAuthToken） | `Authorization: Bearer <platformAccessToken>` | 平台签名认证 |

## 相关文档

- [OAuth 指南](../oauth-guide.md)
- [错误处理](../error-handling.md)
- [认证详解](../authentication.md)
