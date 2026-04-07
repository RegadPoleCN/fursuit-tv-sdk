# 使用示例

本页面提供了 Fursuit.TV SDK 的常见使用场景示例代码。

## 基本使用

### 四种初始化方式对比

SDK 提供四种初始化方式，适用于不同的使用场景：

```kotlin
// 方式 1: 使用 apiKey（适用于已有 apiKey 的用户）
// 认证头：X-Api-Key
// 适用场景：已有 VDS 颁发的 apiKey，简单的服务端调用
val sdk1 = FursuitTvSdk(apiKey = "your-api-key")

// 方式 2: 使用 clientId + clientSecret（推荐，适用于新用户）
// 认证头：Authorization: Bearer <access_token>
// 适用场景：需要 OAuth 2.0 客户端凭证流程，SDK 自动管理令牌刷新
val sdk2 = FursuitTvSdk(
    clientId = "your-client-id",
    clientSecret = "your-client-secret"
)
// 获取初始令牌
runBlocking {
    sdk2.auth.exchangeToken(clientId, clientSecret)
}

// 方式 3: 使用 OAuth 本地回调（适用于需要用户授权的应用）
// 认证头：Authorization: Bearer <oauth-token>
// 适用场景：需要用户登录并授权的完整 OAuth 2.0 授权码模式
val config = OAuthConfig(
    callbackHost = "localhost",
    callbackPort = 8080,
    callbackPath = "/callback",
    stateTimeoutMinutes = 10
)
val sdk3 = FursuitTvSdk.initWithOAuth("your-app-id", config)
runBlocking {
    val tokenInfo = sdk3.auth.initWithOAuth("your-app-id", config)
}

// 方式 4: 使用 accessToken（适用于已有访问令牌的用户）
// 认证头：Authorization: Bearer <access_token>
// 适用场景：已有通过其他方式获取的访问令牌（如从持久化存储恢复）
val sdk4 = FursuitTvSdk(
    accessToken = "your-access-token",
    baseUrl = "https://open-global.vdsentnet.com"
)

// 使用完毕后关闭 SDK
sdk1.close()
sdk2.close()
sdk3.close()
sdk4.close()
```

### 四种初始化方式对比表

| 初始化方式 | 认证头 | 适用场景 | 是否需要用户授权 | 令牌管理 |
|-----------|--------|---------|----------------|---------|
| `apiKey` | `X-Api-Key` | 已有 apiKey 的简单服务端调用 | 否 | 长期有效，无需刷新 |
| `clientId` + `clientSecret` | `Authorization: Bearer` | OAuth 2.0 客户端凭证流程 | 否 | 自动刷新（剩余 <= 300 秒） |
| OAuth 本地回调 | `Authorization: Bearer` | 需要用户授权的完整 OAuth 流程 | 是 | 自动刷新（剩余 <= 300 秒） |
| `accessToken` | `Authorization: Bearer` | 已有访问令牌的场景 | 视情况而定 | 需手动管理或使用 getApiKey |

### 选择建议

- **已有 apiKey**：使用方式 1（apiKey）
- **新用户，服务端调用**：使用方式 2（clientId + clientSecret）
- **需要用户授权**：使用方式 3（OAuth 本地回调）
- **从存储恢复令牌**：使用方式 4（accessToken）

### OAuth 认证流程

#### OAuth 本地回调完整示例（推荐）

```kotlin
// 步骤 1: 配置 OAuth 回调参数
val config = OAuthConfig(
    callbackHost = "localhost",
    callbackPort = 8080,
    callbackPath = "/callback",
    stateTimeoutMinutes = 10,
    enablePkce = false
)

// 步骤 2: 使用 OAuth 初始化 SDK
val sdk = FursuitTvSdk.initWithOAuth("your-app-id", config)

// 步骤 3: 启动 OAuth 授权流程（自动打开浏览器）
runBlocking {
    try {
        val tokenInfo = sdk.auth.initWithOAuth(
            appId = "your-app-id",
            config = config,
            scope = "user:read user:write"
        )
        
        println("授权成功！")
        println("访问令牌：${tokenInfo.accessToken}")
        println("有效期：${tokenInfo.expiresAt}")
        
        // 现在可以调用 API 了
        val userProfile = sdk.user.getUserProfile("username")
        println("用户：${userProfile.name}")
        
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

### 跨平台 OAuth 实现示例

#### JVM 平台示例（桌面应用）

```kotlin
// JVM 平台：自动打开浏览器，完整的本地回调服务器
fun main() = runBlocking {
    val sdk = FursuitTvSdk()
    
    try {
        // 使用默认配置，自动打开系统浏览器
        val oauthResult = sdk.auth.initWithOAuth(
            clientId = "vap_xxxxxxxxxxxxxxxx",
            clientSecret = "your-app-secret"
        )
        
        println("授权成功！")
        println("用户 ID: ${oauthResult.userId}")
        println("用户名：${oauthResult.username}")
        
        // 使用 OAuth 令牌调用 API
        val userProfile = sdk.user.getUserProfile(oauthResult.username)
        println("用户资料：${userProfile.displayName}")
        
    } catch (e: Exception) {
        println("OAuth 失败：${e.message}")
    } finally {
        sdk.close()
    }
}
```

**特点**：
- 自动打开系统默认浏览器
- 本地 HTTP 服务器监听 `http://localhost:8080/callback`
- 授权完成后自动关闭回调服务器
- 支持 PKCE 增强安全性

#### JS 平台示例（Node.js）

```kotlin
// JS 平台（Node.js）：手动打开浏览器，本地 HTTP 服务器接收回调
fun main() = runBlocking {
    val sdk = FursuitTvSdk()
    
    try {
        // 配置 Node.js 环境
        val config = OAuthConfig(
            callbackHost = "127.0.0.1",  // 使用 IP 地址
            callbackPort = 3000,         // 避免端口冲突
            callbackPath = "/oauth/callback",
            stateTimeoutMinutes = 15     // 更长的超时时间
        )
        
        // 获取授权 URL
        val authorizeUrl = sdk.auth.getOAuthAuthorizeUrl(
            clientId = "vap_xxxxxxxxxxxxxxxx",
            redirectUri = "http://127.0.0.1:3000/oauth/callback"
        )
        
        println("请在浏览器中打开以下 URL：")
        println(authorizeUrl)
        
        // 启动本地回调服务器等待授权
        val oauthResult = sdk.auth.initWithOAuth(
            clientId = "vap_xxxxxxxxxxxxxxxx",
            clientSecret = "your-app-secret",
            redirectUri = "http://127.0.0.1:3000/oauth/callback",
            callbackPort = 3000
        )
        
        println("授权成功！用户：${oauthResult.username}")
        
    } catch (e: Exception) {
        println("OAuth 失败：${e.message}")
    } finally {
        sdk.close()
    }
}
```

**特点**：
- 需要手动复制 URL 到浏览器打开
- 本地 HTTP 服务器接收回调
- 建议使用更长的超时时间
- 适用于后端服务和命令行工具

#### Native 平台示例（iOS/macOS/Windows/Linux）

```kotlin
// Native 平台：使用平台特定的 URL 打开机制
fun main() = runBlocking {
    val sdk = FursuitTvSdk()
    
    try {
        // 配置 Native 平台
        val config = OAuthConfig(
            callbackHost = "localhost",
            callbackPort = 0,  // 随机端口
            callbackPath = "/callback",
            enablePkce = true  // 推荐使用 PKCE
        )
        
        // 使用自定义 URL Scheme（可选）
        // 在 Info.plist (iOS) 或 .desktop (Linux) 中配置
        val customRedirectUri = "myapp://oauth/callback"
        
        val oauthResult = sdk.auth.initWithOAuth(
            clientId = "vap_xxxxxxxxxxxxxxxx",
            clientSecret = "your-app-secret",
            redirectUri = customRedirectUri,
            callbackPort = 0
        )
        
        println("授权成功！")
        println("用户：${oauthResult.username}")
        
        // 保存令牌以便下次使用
        saveTokenToKeychain(oauthResult.accessToken)
        
    } catch (e: Exception) {
        println("OAuth 失败：${e.message}")
    } finally {
        sdk.close()
    }
}

// 示例：保存令牌到系统钥匙串
fun saveTokenToKeychain(token: String) {
    // iOS/macOS: 使用 Keychain
    // Windows: 使用 Credential Manager
    // Linux: 使用 Secret Service API
    println("令牌已保存")
}
```

**特点**：
- 支持自定义 URL Scheme（如 `myapp://oauth/callback`）
- 使用平台特定的 URL 打开机制
- iOS/macOS 可使用 Universal Links
- 移动端推荐使用系统浏览器或 SFSafariViewController

### 使用 VDS OAuth 端点的完整流程

基于 VDS OAuth 文档的完整实现示例：

```kotlin
// 完整的 OAuth 流程示例（参考 VDS 文档）
suspend fun completeOAuthFlow() {
    val sdk = FursuitTvSdk()
    
    // 1. 配置 OAuth 参数
    val clientId = "vap_xxxxxxxxxxxxxxxx"  // VDS 应用 ID
    val clientSecret = "your-app-secret"    // VDS 应用 AK
    val redirectUri = "http://localhost:8080/callback"
    
    // 2. 生成随机 state 参数（防 CSRF）
    val state = generateRandomState()
    println("State: $state")
    
    // 3. 构建授权 URL
    val authorizeUrl = buildAuthorizeUrl(
        clientId = clientId,
        redirectUri = redirectUri,
        state = state,
        scope = "openid profile"  // 推荐使用 profile
    )
    
    println("授权 URL: $authorizeUrl")
    
    // 4. 启动本地回调服务器并打开浏览器
    val oauthResult = sdk.auth.initWithOAuth(
        clientId = clientId,
        clientSecret = clientSecret,
        redirectUri = redirectUri
    )
    
    // 5. 获取用户信息
    println("用户 ID: ${oauthResult.userId}")
    println("用户名：${oauthResult.username}")
    println("访问令牌：${oauthResult.accessToken}")
    
    // 6. 使用令牌调用 API
    val userInfo = sdk.auth.getUserInfo()
    println("用户信息：${userInfo.nickname}")
}

// 生成随机 state 参数
fun generateRandomState(): String {
    return (1..32).map { 
        ('0'..'9').random() 
    }.joinToString("")
}

// 构建授权 URL
fun buildAuthorizeUrl(
    clientId: String,
    redirectUri: String,
    state: String,
    scope: String = "openid profile"
): String {
    val baseUrl = "https://account.vds.pub/authorize"
    return buildString {
        append(baseUrl)
        append("?client_id=$clientId")
        append("&redirect_uri=${java.net.URLEncoder.encode(redirectUri, "UTF-8")}")
        append("&response_type=code")
        append("&scope=$scope")
        append("&state=$state")
    }
}
```

#### OAuth 本地回调自动授权流程说明

1. **启动本地服务器**：SDK 在 `localhost:8080` 启动 HTTP 服务器
2. **生成 state 参数**：生成随机 32 字符十六进制字符串防止 CSRF
3. **打开浏览器**：自动打开系统浏览器访问授权页面
4. **用户授权**：用户登录并同意授权
5. **回调处理**：浏览器重定向到 `http://localhost:8080/callback?code=xxx&state=xxx`
6. **提取授权码**：SDK 接收回调，验证 state，提取授权码
7. **交换令牌**：使用授权码和 appId 交换访问令牌
8. **关闭服务器**：自动关闭本地 HTTP 服务器

#### 自定义回调参数示例

```kotlin
// 自定义回调端口和路径
val customConfig = OAuthConfig(
    callbackHost = "127.0.0.1",  // 使用 127.0.0.1 而不是 localhost
    callbackPort = 9000,         // 自定义端口
    callbackPath = "/oauth/callback",  // 自定义回调路径
    stateTimeoutMinutes = 15,    // 15 分钟超时
    enablePkce = true            // 启用 PKCE 增强安全性
)

// 使用自定义配置
val sdk = FursuitTvSdk.initWithOAuth("your-app-id", customConfig)

// 回调 URL 将是：http://127.0.0.1:9000/oauth/callback
```

#### 使用 OAuthCallbackServer 手动控制

```kotlin
// 如果需要更精细的控制，可以直接使用 OAuthCallbackServer
runBlocking {
    // 创建回调服务器配置
    val serverConfig = OAuthCallbackServerConfig(
        callbackHost = "localhost",
        callbackPort = 8080,
        callbackPath = "/callback",
        timeoutSeconds = 300
    )
    
    // 创建回调服务器
    val callbackServer = OAuthCallbackServer(serverConfig)
    
    // 生成随机 state
    val state = callbackServer.generateState()
    println("生成的 state: $state")
    
    // 构建回调 URL
    val redirectUri = serverConfig.buildCallbackUrl()
    println("回调 URL: $redirectUri")
    
    // 构建授权 URL
    val authorizeUrl = "https://fursuit.tv/oauth/authorize?" +
        "client_id=your-app-id&" +
        "redirect_uri=${URLEncoder.encode(redirectUri, "UTF-8")}&" +
        "response_type=code&" +
        "state=$state"
    
    // 打开浏览器
    browse(authorizeUrl)
    
    // 等待回调
    try {
        val result = callbackServer.waitForCallback(state)
        println("授权码：${result.code}")
        
        // 使用授权码交换令牌
        val tokenRequest = OAuthTokenRequest(
            appId = "your-app-id",
            code = result.code,
            redirectUri = redirectUri
        )
        
        val tokenInfo = sdk.auth.exchangeOAuthToken(tokenRequest)
        println("访问令牌：${tokenInfo.accessToken}")
        
    } catch (e: Exception) {
        println("OAuth 流程失败：${e.message}")
    }
}
```

#### 交换令牌

```kotlin
// 使用 clientId 和 clientSecret 交换访问令牌（OAuth 2.0 客户端凭证模式）
val tokenInfo = sdk.auth.exchangeToken(
    clientId = "your-client-id",
    clientSecret = "your-client-secret"
)

// 获取访问令牌
val accessToken = sdk.auth.getAccessToken()
```

#### 刷新令牌

```kotlin
// 刷新令牌（当令牌剩余有效期 <= 300 秒时）
val refreshedToken = sdk.auth.refreshToken()
```

#### 自动令牌刷新（推荐）

```kotlin
// 使用 getApiKey 方法会自动检查并刷新令牌
val apiKey = sdk.auth.getApiKey(
    clientId = "your-client-id",
    clientSecret = "your-client-secret"
)
// 如果令牌即将过期（<= 300 秒），会自动刷新
// 如果刷新失败，会自动回退到 exchangeToken
```

#### 手动设置令牌

```kotlin
// 手动设置令牌（例如从持久化存储中恢复）
val manualToken = TokenInfo(
    accessToken = "your-access-token",
    expiresAt = System.currentTimeMillis() + 3600000, // 1 小时后过期
    tokenType = "Bearer"
)
sdk.auth.setTokenInfo(manualToken)
```

### 认证头使用示例

#### 使用 X-Api-Key 头

```kotlin
// 方式 1: 使用 apiKey 初始化时，SDK 会自动添加 X-Api-Key 头
val sdk = FursuitTvSdk(apiKey = "your-api-key")

// SDK 会在所有请求中自动添加：
// X-Api-Key: your-api-key
```

#### 使用 Authorization: Bearer 头

```kotlin
// 方式 1: 使用 clientId + clientSecret 初始化时，SDK 会自动添加 Authorization 头
val sdk = FursuitTvSdk(
    clientId = "your-client-id",
    clientSecret = "your-client-secret"
)

// 交换令牌后，SDK 会在所有请求中自动添加：
// Authorization: Bearer <access_token>

// 方式 2: 手动设置令牌后
val manualToken = TokenInfo(
    accessToken = "your-access-token",
    expiresAt = System.currentTimeMillis() + 3600000,
    tokenType = "Bearer"
)
sdk.auth.setTokenInfo(manualToken)

// SDK 会自动添加 Authorization: Bearer 头
```

#### 认证头对比

```kotlin
// X-Api-Key 方式
val sdk1 = FursuitTvSdk(apiKey = "your-api-key")
// 请求头：X-Api-Key: your-api-key

// Authorization: Bearer 方式
val sdk2 = FursuitTvSdk(
    clientId = "your-client-id",
    clientSecret = "your-client-secret"
)
// 请求头：Authorization: Bearer <access_token>

// 两种方式效果相同，选择取决于你的使用场景：
// - 已有 apiKey：使用 X-Api-Key
// - 使用 OAuth：使用 Authorization: Bearer
```

## 用户相关

### 获取用户资料

```kotlin
val userProfile = sdk.user.getUserProfile("username")
println("User name: ${userProfile.name}")
println("User bio: ${userProfile.bio}")
println("Followers: ${userProfile.followers}")
println("Following: ${userProfile.following}")
```

### 获取用户访客记录

```kotlin
val visitors = sdk.user.getUserVisitors("username")
println("Total visitors: ${visitors.visitors.size}")
visitors.visitors.forEach { visitor ->
    println("Visitor: ${visitor.name}")
    println("Visit time: ${visitor.visitTime}")
}
```

### 获取用户商店商品

```kotlin
val products = sdk.user.getUserStoreProducts("username")
println("Total products: ${products.products.size}")
products.products.forEach { product ->
    println("Product: ${product.name}")
    println("Price: ${product.price}")
    println("Description: ${product.description}")
}
```

## 搜索和发现

### 获取热门推荐

```kotlin
val popularData = sdk.search.getPopular()
println("Total popular users: ${popularData.users.size}")
popularData.users.forEach { user ->
    println("User: ${user.name} (@${user.username})")
    println("Score: ${user.score}")
}
```

### 搜索用户

```kotlin
val params = SearchParams(
    query = "fox",
    type = "user",
    limit = 20
)
val searchResults = sdk.search.search(params)
println("Total results: ${searchResults.results.size}")
searchResults.results.forEach { result ->
    println("Result: ${result.name} (@${result.username})")
}
```

### 获取随机推荐

```kotlin
val params = RandomFursuitParams(
    count = 10,
    personalized = true
)
val randomUsers = sdk.search.getRandomFursuit(params)
println("Total random users: ${randomUsers.size}")
randomUsers.forEach { user ->
    println("User: ${user.name} (@${user.username})")
}
```

## 聚会相关

### 获取聚会月历

```kotlin
val params = GatheringMonthlyParams(
    year = 2024,
    month = 1
)
val gatherings = sdk.gathering.getGatheringMonthly(params)
println("Total gatherings: ${gatherings.size}")
gatherings.forEach { gathering ->
    println("Gathering: ${gathering.name}")
    println("Date: ${gathering.date}")
    println("Location: ${gathering.location}")
    println("Attendees: ${gathering.attendees}")
}
```

### 获取附近聚会

```kotlin
val params = GatheringNearbyParams(
    lat = 37.7749,
    lng = -122.4194,
    radius = 10000
)
val nearbyGatherings = sdk.gathering.getGatheringNearby(params)
println("Total nearby gatherings: ${nearbyGatherings.size}")
nearbyGatherings.forEach { gathering ->
    println("Gathering: ${gathering.name}")
    println("Distance: ${gathering.distance} km")
    println("Location: ${gathering.location}")
}
```

### 获取聚会详情

```kotlin
val detail = sdk.gathering.getGatheringDetail("gathering-id")
println("Gathering: ${detail.name}")
println("Date: ${detail.date}")
println("Location: ${detail.location}")
println("Description: ${detail.description}")
println("Organizer: ${detail.organizer.name}")
println("Attendees: ${detail.attendees}/${detail.maxAttendees}")
```

## 学校相关

### 搜索学校

```kotlin
val params = SchoolSearchParams(
    query = "university",
    limit = 10
)
val schools = sdk.school.searchSchools(params)
println("Total schools: ${schools.schools.size}")
schools.schools.forEach { school ->
    println("School: ${school.name}")
    println("Location: ${school.city}, ${school.state}, ${school.country}")
    println("Students: ${school.students}")
}
```

### 获取用户学校信息

```kotlin
val userSchool = sdk.school.getUserSchool("user-id")
println("User school: ${userSchool.school.name}")
println("Graduation year: ${userSchool.graduationYear}")
println("Major: ${userSchool.major}")
```

### 获取用户角色列表

```kotlin
val characters = sdk.school.getUserCharacters("username")
println("Total characters: ${characters.characters.size}")
characters.characters.forEach { character ->
    println("Character: ${character.name}")
    println("Species: ${character.species}")
    println("Bio: ${character.bio}")
}
```

## 错误处理

### 基本错误处理

```kotlin
try {
    val userProfile = sdk.user.getUserProfile("username")
    println("User name: ${userProfile.name}")
} catch (e: Exception) {
    println("Error: ${e.message}")
}
```

## 高级用法

### 自定义配置

```kotlin
val config = SdkConfig(
    apiKey = "your-api-key",
    baseUrl = "https://open-global.vdsentnet.com",
    requestTimeout = 60000,
    connectTimeout = 10000,
    socketTimeout = 30000,
    logLevel = LogLevel.DEBUG,
    enableRetry = true,
    maxRetries = 3,
    retryInterval = 1000
)

val sdk = FursuitTvSdk(config)
```

### 自动令牌刷新机制

SDK 实现了智能的自动令牌刷新机制，确保 API 调用不会因令牌过期而失败：

```kotlin
// 初始化 SDK（使用 clientId + clientSecret）
val sdk = FursuitTvSdk(
    clientId = "your-client-id",
    clientSecret = "your-client-secret"
)

// 使用 getApiKey 方法会自动管理令牌刷新
val apiKey = sdk.auth.getApiKey(
    clientId = "your-client-id",
    clientSecret = "your-client-secret"
)

// 如果令牌剩余有效期 <= 300 秒（5 分钟），会自动刷新
// 如果刷新失败，会自动回退到 exchangeToken
```
