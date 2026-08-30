# 认证详解

Fursuit.TV SDK 支持两种认证方式：**签名交换（Token Exchange）** 和 **OAuth 2.0 授权码**。选择哪种方式取决于你的应用类型和使用场景。

## SDK 初始化：2 种写法

SDK 初始化时**必须**同时提供 `clientId` + `clientSecret`（apiKey-only init 已被禁用）。有两种入口：

### 写法 A：Kotlin DSL（suspend，coroutine 上下文）

```kotlin
import com.furrist.rp.furtv.sdk.fursuitTvSdk
import com.furrist.rp.furtv.sdk.user.getUserProfile

val sdk = fursuitTvSdk {
    clientId = "vap_xxxxxxxxxxxxxxxx"
    clientSecret = "your-client-secret-here"
}

// SDK 自动完成签名交换，可直接使用所有 API
val profile = sdk.user.getUserProfile("exampleUser")
println("用户: ${profile.user.nickname}")

sdk.close()
```

适用：Kotlin 协程代码、JS/TS Promise 上下文、Native 平台。

### 写法 B：链式 Builder（跨语言，Java 友好）

**Kotlin：**
```kotlin
import com.furrist.rp.furtv.sdk.FursuitTvSdkBuilder
import com.furrist.rp.furtv.sdk.model.SdkLogLevel

val sdk = FursuitTvSdkBuilder()
    .clientId("vap_xxxxxxxxxxxxxxxx")
    .clientSecret("your-client-secret-here")
    .logLevel(SdkLogLevel.INFO)
    .build()  // suspend，需在 coroutine 上下文
```

**Java（JVM，`@JvmBlocking` / `@JvmAsync` 自动生成）：**
```java
import com.furrist.rp.furtv.sdk.FursuitTvSdk;
import com.furrist.rp.furtv.sdk.FursuitTvSdkBuilder;
import java.util.concurrent.CompletableFuture;

// 同步（JVM）
FursuitTvSdk sdk = FursuitTvSdkBuilder.create()
    .clientId("vap_xxxxxxxxxxxxxxxx")
    .clientSecret("your-client-secret-here")
    .logLevel(SdkLogLevel.INFO)
    .buildBlocking();

// 异步（JVM，返回 Future）
CompletableFuture<FursuitTvSdk> future = FursuitTvSdkBuilder.create()
    .clientId("vap_xxxxxxxxxxxxxxxx")
    .clientSecret("your-client-secret-here")
    .buildAsync();
```

**JS/TS（suspend → Promise）：**
```typescript
import { FursuitTvSdkBuilder, SdkLogLevel } from "@regadpole/fursuit-tv-sdk";

const sdk = await FursuitTvSdkBuilder.create()
    .clientId("vap_xxxxxxxxxxxxxxxx")
    .clientSecret("your-client-secret-here")
    .logLevel(SdkLogLevel.INFO)
    .build();
```

**机制：** `FursuitTvSdkBuilder.build()` 是 `suspend fun`，标注 `@JvmBlocking` + `@JvmAsync`。suspend-transform 插件在 JVM 字节码层面生成 `buildBlocking(): FursuitTvSdk`（同步）和 `buildAsync(): CompletableFuture<FursuitTvSdk>`。JS/TS 上 `suspend fun` 自动映射为 Promise。Native 同 JS。

### 选择决策树

```
调用方是什么？
├─ Kotlin coroutine
│  ├─ 简单配置 → fursuitTvSdk { ... }        (写法 A)
│  └─ 复杂链式 → FursuitTvSdkBuilder().build() (写法 B)
├─ JVM 同步 Kotlin
│  └─ runBlocking { fursuitTvSdk { ... } }     (写法 A)
├─ Java
│  └─ FursuitTvSdkBuilder.create()...buildBlocking() / buildAsync()  (写法 B)
├─ JS/TS
│  ├─ 简单配置 → await fursuitTvSdk({...})     (写法 A)
│  └─ 复杂链式 → await FursuitTvSdkBuilder.create()...build()  (写法 B)
└─ Native
   ├─ 简单配置 → fursuitTvSdk { ... }
   └─ 复杂链式 → FursuitTvSdkBuilder().build()
```

## ⚠️ 约束：apiKey-only init 不可表达

0.4.0 起，配置级 `apiKey` 已从 `MutableSdkConfig` / `SdkConfig` / `FursuitTvSdkBuilder` 中**彻底删除**，apiKey-only 初始化在编译期不可表达。

```kotlin
// ✅ 必须同时提供 clientId + clientSecret（platform apiKey 由 token exchange 自动获取）
val sdk = fursuitTvSdk { clientId = "..."; clientSecret = "..." }
val sdk = FursuitTvSdkBuilder().clientId("...").clientSecret("...").build()
```

错误消息：`"FursuitTvSdkBuilder.build() requires both clientId and clientSecret. apiKey-only init is forbidden (the platform apiKey is auto-obtained via token exchange). Use .clientId(...).clientSecret(...) before .build()."`

## 认证方式对比

| 维度 | 签名交换 (Token Exchange) | OAuth 2.0 授权码 |
|------|--------------------------|------------------|
| **适用场景** | 服务端应用、后台任务、API 调用 | 客户端应用、需要用户登录 |
| **凭证类型** | clientId + clientSecret | clientId + clientSecret + 用户授权 |
| **获取令牌** | `apiKey`（平台签名） | `oauthToken`（用户令牌，从 `TokenInfo.OAuth.oauthToken`） |
| **认证头（业务 API）** | `X-Api-Key: <apiKey>` | `X-Api-Key: <apiKey>`（业务 API 仍用平台签名） |
| **认证头（OAuth API）** | 不适用 | `Authorization: Bearer <oauthToken>`（单头） |
| **令牌刷新** | 自动（SDK 内置，≤270秒触发） | 重新调用 `loginWithOAuth()`（无自动刷新） |
| **前置条件** | 无 | 必须先完成签名交换 |
| **典型用途** | 调用业务 API（user/search/gathering/school） | 获取用户信息（getUserInfo） |
| **安全级别** | 高（服务端凭证） | 高（标准授权码流程 + state CSRF 防护） |

## 方式一：签名交换（Token Exchange）

### 工作原理

签名交换是 SDK 的主要认证方式，通过应用凭证（clientId + clientSecret）向 VDS 服务器请求平台签名。`clientId` 即 VDS 文档中的 `appId`。

```
clientId + clientSecret → POST /api/auth/token → TokenInfo.Platform {apiKey}
```

服务器返回：
- **apiKey**: 用于 `X-Api-Key` 头（**推荐**，SDK 优先使用）

### 使用场景

- ✅ 服务端应用（Web 后端、微服务）
- ✅ 后台任务（定时任务、数据处理脚本）
- ✅ 批量数据导入/导出
- ✅ 仅需调用业务 API，不需要用户身份

### 令牌管理

#### apiKey vs accessToken

| 令牌 | 认证头 | 优先级 | 说明 |
|------|--------|--------|------|
| **apiKey** | `X-Api-Key: <apiKey>` | **优先** | SDK 默认使用此头 |
| **accessToken** (platform) | 不单独发送认证头 | — | platform 令牌的 apiKey 即业务请求的 `X-Api-Key` 凭证（OAuth userinfo 单独使用 Bearer） |

#### 自动刷新机制

SDK 会在以下情况自动刷新令牌：
- 剩余有效期 **≤ 270 秒**（300 秒窗口减 30 秒 skew 缓冲）时自动触发刷新
- 刷新失败时会**回退到重新 exchangeToken()**
- 整个过程对开发者透明，无需手动干预

```kotlin
// 无需手动管理令牌，SDK 会自动处理
val sdk = fursuitTvSdk { clientId = "..."; clientSecret = "..." }

while (true) {
    val users = sdk.search.getPopular()
    processUsers(users)
    delay(60_000)
}
```

## 方式二：OAuth 2.0 授权码

> **⚠️ 重要前置条件**
>
> 在使用 OAuth 流程之前，**必须先完成签名交换**。
>
> 如果未完成前置条件就调用 `loginWithOAuth()`，将抛出 IllegalStateException。

### 使用场景

- 🖥️ 桌面应用（需要用户登录授权）
- 📱 移动应用（iOS/Android）
- 🌐 Web 应用（单页应用）
- 需要获取**用户身份信息**的场景

### OAuth 授权码流程

完整的 OAuth 2.0 授权码流程包含 4 个步骤：

#### Step 1: 生成授权 URL

```kotlin
val authorizeUrl = sdk.auth.getOAuthAuthorizeUrl(
    redirectUri = "http://localhost:8080/callback",
    state = "random-state-string",  // CSRF 防护
)
// 输出: https://open-global.vdsentnet.com/api/proxy/account/sso/authorize?...
```

#### Step 2: 用户授权

打开浏览器跳转到授权 URL，用户在 VDS 页面登录并授权。

#### Step 3: 接收回调

```
http://localhost:8080/callback?code=AUTHORIZATION_CODE&state=random-state-string
```

#### Step 4: 交换令牌

```kotlin
val tokenInfo = sdk.auth.exchangeOAuthToken(
    code = receivedCode,
    redirectUri = "http://localhost:8080/callback"
)
// 返回 TokenInfo.OAuth {oauthToken, refreshToken, scope, redirectUri, expiresAt}
```

### 自动化流程：loginWithOAuth()

```kotlin
import com.furrist.rp.furtv.sdk.auth.createDefaultOAuthHandler
import com.furrist.rp.furtv.sdk.model.OAuthConfig

// Step 1: 初始化 SDK（自动完成签名交换）
val sdk = fursuitTvSdk {
    clientId = "vap_xxxxxxxxxxxxxxxx"
    clientSecret = "your-client-secret-here"
}

// Step 2: 配置回调处理器（可选）
val handler = createDefaultOAuthHandler(
    OAuthConfig(
        callbackHost = "localhost",
        callbackPort = 8080,
        callbackPath = "/callback",
        timeoutSeconds = 300
    )
)
sdk.auth.setOAuthCallbackHandler(handler)

// Step 3: 启动 OAuth 流程
val tokenInfo = sdk.auth.loginWithOAuth()

// Step 4: 获取用户信息
val userInfo = sdk.auth.getUserInfo()
println("用户昵称: ${userInfo.nickname}")
```

`loginWithOAuth()` 自动完成：生成 state、启动回调监听、验证 state、交换令牌、返回 `TokenInfo.OAuth`。

## 令牌类型总览

SDK 中涉及 3 种不同的令牌：

| 令牌 | 来源 | 用途 | 认证头 | 典型使用场景 |
|------|------|------|--------|-------------|
| **apiKey** (`TokenInfo.Platform.apiKey`) | 签名交换返回 | 业务 API 调用 | `X-Api-Key` | 所有业务 API |
| **oauthToken** (`TokenInfo.OAuth.oauthToken`) | OAuth 授权码流程 | 用户身份验证 | `Authorization: Bearer` | `getUserInfo()` |
| **refreshToken** (`TokenInfo.OAuth.refreshToken`) | OAuth 流程 | — | 不直接用于头 | 解析并存储；vds-docs 未记载 OAuth 刷新端点，SDK 暂不使用 |

### 认证头机制说明

#### 业务 API（user/search/gathering/school 等）

业务 API 始终使用**平台签名**认证（`X-Api-Key`）：

```
GET /api/proxy/furtv/users/exampleUser HTTP/1.1
X-Api-Key: <apiKey>     ← 平台签名
```

#### OAuth API（getUserInfo）

OAuth 接口使用**单认证头**机制：

```
GET /api/proxy/account/sso/userinfo HTTP/1.1
Authorization: Bearer <oauthToken>     ← OAuth 用户令牌
```

应用身份（clientId/clientSecret）通过签名交换的 `apiKey` 验证（HttpClientConfig.defaultRequest 通过 AuthHolder 注入 `X-Api-Key`）；用户身份通过 `Authorization: Bearer` 验证。

## 最佳实践

### 如何选择认证方式？

- 只调用业务 API → **仅需签名交换**
- 需要获取用户登录信息 → **签名交换 + OAuth**
- **不再有"已有 apiKey 直接 create()"** 的入口（被禁用）

### 安全建议

#### 1. 凭证管理

⚠️ **千万不要**将 clientSecret 硬编码在代码中！

```kotlin
// ❌ 错误做法：硬编码密钥
val secret = "my-hardcoded-secret"

// ✅ 正确做法：使用环境变量或密钥管理系统
val secret = System.getenv("FURSUITTV_CLIENT_SECRET")
```

#### 2. 生产环境配置

```kotlin
val sdk = fursuitTvSdk {
    clientId = System.getenv("FURSUITTV_CLIENT_ID")!!
    clientSecret = System.getenv("FURSUITTV_CLIENT_SECRET")!!
    requestTimeout = 30_000L
    enableRetry = true
    maxRetries = 3
    logLevel = SdkLogLevel.ERROR
}
```

#### 3. 资源释放

始终在不再需要时调用 `sdk.close()` 释放资源。

### 常见错误与解决方案

#### 错误 1: 未完成签名交换就调用 OAuth

**症状**: 抛出 `IllegalStateException`（具体消息见 `loginWithOAuth` 错误）。

**原因**: 直接调用 `loginWithOAuth()` 但之前没有完成签名交换。

**解决方案**:
```kotlin
// ❌ 错误：未完成签名交换就尝试 OAuth
// 0.4.0 起 `apiKey` 配置项已删除，apiKey-only 初始化在编译期不可表达
sdk.auth.loginWithOAuth()  // 抛异常

// ✅ 正确：先通过 DSL 初始化（自动完成签名交换）
val sdk = fursuitTvSdk { clientId = "..."; clientSecret = "..." }
sdk.auth.loginWithOAuth()  // ✅ 正常工作
```

#### 错误 2: 使用 oauthToken 调用业务 API

**症状**: 返回 `401 Unauthorized` 错误

**原因**: 业务 API 使用 platform 签名（`apiKey`），不接受 OAuth token。

**解决方案**:
```kotlin
val sdk = fursuitTvSdk { clientId = "..."; clientSecret = "..." }
sdk.auth.loginWithOAuth()  // 完成后 sdk.auth.getApiKey() 仍可用

// 业务 API 自动用 apiKey（X-Api-Key 头，由 defaultRequest 注入）
val profile = sdk.user.getUserProfile("exampleUser")  // ✅
```

## 迁移示例：UserProfile.socialLinks 类型变更（dto-completeness BREAKING）

```kotlin
// 旧（v0.2.x）— Map<String, String>
val weibo = user.socialLinks?.get("weibo")
val email = user.contactInfo?.get("email")

// 新（v0.3.0）— typed wrapper with KSerializer
val weibo = user.socialLinks?.entries?.get("weibo")
val email = user.contactInfo?.entries?.get("email")

// 新增：可获取 structured custom 链接
val customLinks = user.socialLinks?.custom  // List<CustomLink>
val customContact = user.contactInfo?.custom
```

迁移点：所有 `?.get("...")` → `?.entries?.get("...")`。0.4.0 起配置级 `apiKey` 已从 `MutableSdkConfig` / `SdkConfig` 中彻底删除（调用统一使用 `clientId/clientSecret`，platform apiKey 由 `auth.getApiKey()` 获取）。

## 浏览器中继页接入约定

JS 浏览器端 SDK 无法直接在页面接收 OAuth 重定向，回调通过 `window.postMessage` 送达。接入方需部署一个**中继页**，其 URL 配置为 OAuth 应用的 `redirect_uri`。中继页收到 OAuth 重定向（query 含 `code`/`state` 或 `error`/`error_description`）时，把 query 参数原样转发给打开它的页面：

```html
<script>
  const q = new URLSearchParams(location.search);
  const payload = Object.fromEntries(q.entries());
  window.opener && window.opener.postMessage(JSON.stringify(payload), "*");
</script>
```

约定：payload 为 query 参数的 JSON 序列化（或 query-like 字符串），SDK 的 `JsOAuthCallbackHandler` 会解析其中的 `code`/`state`/`error`/`error_description` 字段。Node.js 环境无需中继页——SDK 会直接在本机 `callbackHost:callbackPort` 启动回调服务器。
