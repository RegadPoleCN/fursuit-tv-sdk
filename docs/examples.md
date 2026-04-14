# 使用示例

本页面提供了 Fursuit.TV SDK 的常见使用场景示例代码。

## 基本使用

### 初始化方式

#### 使用 apiKey

```kotlin
// 使用默认配置
val sdk = FursuitTvSdk(apiKey = "your-api-key")

// 使用自定义配置
val config = SdkConfig.builder()
    .apiKey("your-api-key")
    .baseUrl("https://open-global.vdsentnet.com")
    .build()
val sdk = FursuitTvSdk(config)
```

#### 使用 appId + appSecret（推荐）

```kotlin
// 使用 appId 和 appSecret 初始化
val sdk = FursuitTvSdk(
    appId = "vap_xxxxxxxxxxxxxxxx",
    appSecret = "your-app-secret"
)

// 获取初始令牌
runBlocking {
    sdk.auth.exchangeToken(appId, appSecret)
}
```

#### 使用 accessToken

```kotlin
// 直接使用已有的 accessToken 初始化
val sdk = FursuitTvSdk(
    accessToken = "your-access-token",
    baseUrl = "https://open-global.vdsentnet.com"
)
```

#### 使用 OAuth 认证

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

## API 使用示例

### 用户相关

```kotlin
// 获取用户资料
val userProfile = sdk.user.getUserProfile("username")
println("User name: ${userProfile.name}")
println("User bio: ${userProfile.bio}")

// 获取用户访客记录
val visitors = sdk.user.getUserVisitors("username")
println("Total visitors: ${visitors.visitors.size}")

// 获取用户商店商品
val products = sdk.user.getUserStoreProducts("username")
println("Total products: ${products.products.size}")
```

### 搜索和发现

```kotlin
// 获取热门推荐
val popularData = sdk.search.getPopularDiscovery()
println("Total popular users: ${popularData.users.size}")

// 搜索用户
val searchResults = sdk.search.searchDiscovery("fox")
println("Total results: ${searchResults.results.size}")

// 获取随机推荐
val randomUsers = sdk.search.getRandomDiscovery()
println("Total random users: ${randomUsers.size}")
```

### 聚会相关

```kotlin
// 获取聚会月历
val gatherings = sdk.gathering.getMonthly(2024, 12)
println("Total gatherings: ${gatherings.size}")

// 获取附近聚会
val nearbyGatherings = sdk.gathering.getNearby(37.7749, -122.4194, 10000)
println("Total nearby gatherings: ${nearbyGatherings.size}")

// 获取聚会详情
val detail = sdk.gathering.getGatheringDetail("gathering-id")
println("Gathering: ${detail.name}")
```

### 学校相关

```kotlin
// 搜索学校
val schools = sdk.school.searchSchools("北京大学")
println("Total schools: ${schools.schools.size}")

// 获取用户学校信息
val userSchool = sdk.school.getUserSchools()
println("User school: ${userSchool.schools.size}")

// 获取用户角色列表
val characters = sdk.school.getUserCharacters("username")
println("Total characters: ${characters.characters.size}")
```

## 错误处理

```kotlin
try {
    val userProfile = sdk.user.getUserProfile("username")
    println("User name: ${userProfile.name}")
} catch (e: FursuitTvSdkException) {
    println("Error: ${e.message}")
}
```

## 高级用法

### 自定义配置

```kotlin
val config = SdkConfig.builder()
    .apiKey("your-api-key")
    .baseUrl("https://open-global.vdsentnet.com")
    .requestTimeout(60000)
    .connectTimeout(10000)
    .socketTimeout(30000)
    .logLevel(LogLevel.DEBUG)
    .enableRetry(true)
    .maxRetries(3)
    .retryInterval(1000)
    .build()

val sdk = FursuitTvSdk(config)
```

### 令牌管理

```kotlin
// 交换令牌
val tokenInfo = sdk.auth.exchangeToken("vap_xxxxxxxxxxxxxxxx", "your-app-secret")

// 刷新令牌
val refreshedToken = sdk.auth.refreshToken()

// 检查认证状态
val isAuthenticated = sdk.auth.isAuthenticated()
```
