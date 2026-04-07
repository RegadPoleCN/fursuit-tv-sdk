# Fursuit.TV SDK

[![Kotlin](https://img.shields.io/badge/Kotlin-2.1.20+-purple.svg)](https://kotlinlang.org)
[![Platform](https://img.shields.io/badge/Platform-JVM%20%7C%20JS%20%7C%20Native-lightgrey.svg)](https://kotlinlang.org/docs/multiplatform.html)
[![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)

基于 Kotlin Multiplatform 构建的跨平台 SDK，为 Fursuit.TV 和 VDS 账户系统提供完整的 API 访问能力。支持 JVM、JS 和 Native（iOS、macOS、Linux、Windows）平台。

## 📖 目录

- [功能特性](#-功能特性)
- [快速开始](#-快速开始)
- [API 模块](#-api-模块)
- [使用示例](#-使用示例)
- [错误处理](#-错误处理)
- [配置选项](#-配置选项)
- [平台支持](#-平台支持)
- [开发指南](#-开发指南)
- [许可证](#-许可证)

## ✨ 功能特性

- 🌍 **跨平台支持** - 一次编写，多端运行（JVM、JS、iOS、macOS、Linux、Windows）
- 🔐 **完整认证流程** - 支持签名交换和 OAuth 2.0 授权
- 🔄 **自动令牌刷新** - 智能管理访问令牌，无需手动刷新
- 📡 **响应式 API** - 基于 Kotlin 协程的异步编程模型
- 🛡️ **完善的错误处理** - 细粒度的异常分类和详细的错误信息
- 🔁 **内置重试机制** - 网络请求失败自动重试，提高可靠性
- 📝 **详细的日志记录** - 可配置的日志级别，便于调试
- 🎯 **类型安全** - 完整的 Kotlin 类型定义，编译时检查

## 🚀 快速开始

### 1. 添加依赖

#### Gradle (Kotlin DSL)

```kotlin
repositories {
    mavenCentral()
}

dependencies {
    implementation("me.regadpole:fursuit-tv-sdk:1.0-SNAPSHOT")
}
```

#### Gradle (Groovy)

```groovy
repositories {
    mavenCentral()
}

dependencies {
    implementation 'me.regadpole:fursuit-tv-sdk:1.0-SNAPSHOT'
}
```

#### Maven

```xml
<dependency>
    <groupId>me.regadpole</groupId>
    <artifactId>fursuit-tv-sdk</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>
```

### 2. 初始化 SDK

SDK 支持四种初始化方式，请根据您的使用场景选择：

#### 方式 1: 使用 apiKey（适用于已有 apiKey 的用户）

如果您已经从 Fursuit.TV 获取了 apiKey，可以直接使用此方式。SDK 会在请求头中使用 `X-Api-Key` 进行认证。

```kotlin
// 方式 1: 使用默认配置
val sdk = FursuitTvSdk(apiKey = "your-api-key")

// 方式 2: 使用自定义配置
val config = SdkConfig(
    apiKey = "your-api-key",
    baseUrl = "https://open-global.vdsentnet.com",
    requestTimeout = 60000,
    logLevel = LogLevel.DEBUG
)
val sdk = FursuitTvSdk(config)
```

**适用场景**：已有 apiKey 的用户，快速集成，无需处理令牌交换流程。

#### 方式 2: 使用 clientId + clientSecret（适用于服务端应用）

适用于服务端应用，通过签名交换获取访问令牌。SDK 会在获取令牌后使用 `X-Api-Key` 进行认证。

```kotlin
// 使用 clientId 和 clientSecret 初始化
val sdk = FursuitTvSdk(
    clientId = "your-client-id",
    clientSecret = "your-client-secret"
)

// 手动获取初始令牌（在协程中）
runBlocking {
    sdk.auth.exchangeToken(
        clientId = "your-client-id",
        clientSecret = "your-client-secret"
    )
}
// SDK 会自动管理令牌的刷新（当剩余有效期 <= 300 秒时）
```

**适用场景**：服务端应用、后台任务、需要长期访问 API 的场景。

#### 方式 3: OAuth 2.0 授权（适用于需要用户授权的应用）

适用于需要用户登录和授权的应用场景。SDK 提供自动化的 OAuth 流程，支持本地回调服务器。

**基本用法（使用默认本地回调）：**

```kotlin
// 初始化 SDK（无需预先提供凭证）
val sdk = FursuitTvSdk()

// 使用 initWithOAuth 方法，自动启动本地回调服务器并打开浏览器
val oauthResult = sdk.auth.initWithOAuth(
    clientId = "your-client-id",
    clientSecret = "your-client-secret"
)

// oauthResult 包含授权后的完整信息
println("访问令牌：${oauthResult.accessToken}")
println("用户 ID: ${oauthResult.userId}")
println("用户名：${oauthResult.username}")

// 后续 API 请求会自动使用 Authorization: Bearer 头进行认证
```

**适用场景**：桌面应用、移动应用、Web 应用等需要用户登录和授权的场景。

**高级用法（自定义 OAuth 配置）：**

```kotlin
// 使用 OAuthConfig 自定义回调参数
val config = OAuthConfig(
    clientId = "your-client-id",
    clientSecret = "your-client-secret",
    redirectUri = "http://localhost:8080/callback",  // 自定义本地回调地址
    callbackPort = 8080,                              // 本地回调服务器端口
    state = "custom-state-string"                     // 自定义 state 参数（可选）
)

val sdk = FursuitTvSdk()
val oauthResult = sdk.auth.initWithOAuth(config)

// 或者使用带命名参数的版本
val oauthResult = sdk.auth.initWithOAuth(
    clientId = "your-client-id",
    clientSecret = "your-client-secret",
    redirectUri = "http://localhost:8080/callback",
    callbackPort = 8080
)
```

**OAuthConfig 参数说明：**

| 参数 | 类型 | 默认值 | 说明 |
|------|------|--------|------|
| `clientId` | String | - | 客户端 ID（必需） |
| `clientSecret` | String | - | 客户端密钥（必需） |
| `redirectUri` | String | `http://localhost:{port}/callback` | OAuth 回调 URI |
| `callbackPort` | Int | 随机可用端口 | 本地回调服务器监听端口 |
| `state` | String | 自动生成 | CSRF 保护参数，建议保持默认 |

**🌍 跨平台 OAuth 支持说明：**

SDK 的 OAuth 实现使用 Ktor CIO 引擎，支持全平台：

- **JVM 平台**：使用 CIO 嵌入式服务器，自动启动本地 HTTP 回调服务器
- **JS 平台（Browser）**：使用 postMessage API 监听回调消息，无需启动服务器
- **JS 平台（Node.js）**：使用 CIO 服务器，支持本地回调
- **Native 平台（iOS/macOS/Linux/Windows）**：使用 CIO 嵌入式服务器，支持本地回调

SDK 会根据运行平台自动选择合适的实现，无需手动配置。

详见 [跨平台 OAuth 实现](docs/oauth.md)。

#### 方式 4: 使用 access_token 直接初始化（适用于已有令牌的场景）

如果您已经通过其他方式获取了 access_token，可以直接使用此方式初始化 SDK。

```kotlin
// 方式 1: 使用 access_token 直接初始化
val sdk = FursuitTvSdk(accessToken = "your-access-token")

// 方式 2: 使用自定义配置
val config = SdkConfig(
    accessToken = "your-access-token",
    baseUrl = "https://open-global.vdsentnet.com",
    requestTimeout = 60000,
    logLevel = LogLevel.DEBUG
)
val sdk = FursuitTvSdk(config)
```

**适用场景**：已有 access_token 的场景，例如从本地存储中读取之前保存的令牌。

### 3. 认证流程

#### 签名交换（服务器端推荐）

```kotlin
val tokenInfo = sdk.auth.exchangeToken(
    clientId = "your-client-id",
    clientSecret = "your-client-secret"
)
println("访问令牌：${tokenInfo.accessToken}")
```

#### OAuth 2.0 自动授权流程（推荐）

```kotlin
// 使用 initWithOAuth 方法，自动完成整个 OAuth 流程
val sdk = FursuitTvSdk()
val oauthResult = sdk.auth.initWithOAuth(
    clientId = "your-client-id",
    clientSecret = "your-client-secret"
)

// oauthResult 包含完整的授权结果
println("访问令牌：${oauthResult.accessToken}")
println("刷新令牌：${oauthResult.refreshToken}")
println("用户 ID: ${oauthResult.userId}")
println("用户名：${oauthResult.username}")
println("令牌有效期：${oauthResult.expiresIn} 秒")
```

#### OAuth 2.0 手动流程（高级用法）

如果您需要更精细地控制 OAuth 流程，可以使用手动方式：

```kotlin
val sdk = FursuitTvSdk()

// 1. 生成授权 URL
val authorizeUrl = sdk.auth.getOAuthAuthorizeUrl(
    clientId = "your-client-id",
    redirectUri = "http://localhost:8080/callback"
)

// 2. 在浏览器中打开授权 URL
// 用户完成授权后，会从回调 URL 获取 authorization_code

// 3. 使用授权码交换令牌
val oauthToken = sdk.auth.exchangeOAuthToken(
    clientId = "your-client-id",
    clientSecret = "your-client-secret",
    code = "authorization-code",
    redirectUri = "http://localhost:8080/callback"
)

// 4. 获取用户信息（可选）
val userInfo = sdk.auth.getUserInfo()
```

### 🔐 认证头说明

SDK 根据初始化方式的不同，使用两种认证头：

| 初始化方式 | 认证头 | 说明 |
|-----------|--------|------|
| **apiKey** | `X-Api-Key: your-api-key` | 直接使用 apiKey 进行认证 |
| **clientId + clientSecret** | `X-Api-Key: <access_token>` | 通过签名交换获取访问令牌，使用 X-Api-Key 头 |
| **OAuth 2.0** | `Authorization: Bearer <access_token>` | 通过 OAuth 授权获取访问令牌，使用 Bearer 认证 |
| **access_token** | `Authorization: Bearer <access_token>` | 直接使用已有的 access_token，使用 Bearer 认证 |

**重要区别**：
- `X-Api-Key`：用于服务端到服务端的认证，通过 clientId/clientSecret 或 apiKey 直接访问 API
- `Authorization: Bearer`：用于用户授权场景，代表用户执行操作，需要用户登录授权

## 📦 API 模块

SDK 包含以下主要 API 模块：

| 模块 | 描述 | 主要功能 |
|------|------|----------|
| **Auth** | 认证与授权 | 令牌交换、OAuth 授权、令牌刷新、用户信息 |
| **Base** | 基础接口 | 健康检查、版本信息、主题包管理 |
| **User** | 用户资料 | 用户信息、点赞状态、访客记录、社交徽章 |
| **Search** | 搜索发现 | 热门推荐、随机推荐、搜索、物种列表 |
| **Gathering** | 聚会活动 | 聚会统计、月历视图、附近聚会、报名管理 |
| **School** | 学校角色 | 学校搜索、角色管理、用户学校信息 |

## 💡 使用示例

### 基础接口

```kotlin
// 健康检查
val health = sdk.base.health()
println("服务状态：${health.status}, 版本：${health.version}")

// 获取主题包清单
val themePacks = sdk.base.getThemePacksManifest()
themePacks.packs.forEach { pack ->
    println("主题包：${pack.name}")
}
```

### 用户资料

```kotlin
// 获取用户资料
val userProfile = sdk.user.getUserProfile("username")
println("昵称：${userProfile.displayName}, 物种：${userProfile.species}")

// 获取点赞状态
val likeStatus = sdk.user.getLikeStatus("username")
println("点赞数：${likeStatus.likeCount}, 已点赞：${likeStatus.isLiked}")

// 获取访客记录
val visitors = sdk.user.getUserVisitors("username")
println("访客总数：${visitors.totalCount}")

// 获取社交徽章
val badges = sdk.user.getUserSocialBadges("username")
badges.badges.forEach { badge ->
    println("徽章：${badge.name}")
}
```

### 搜索与发现

```kotlin
// 热门推荐
val popular = sdk.search.getPopular()
popular.users.forEach { user ->
    println("热门用户：${user.displayName}")
}

// 随机推荐
val randomFursuits = sdk.search.getRandomFursuit(count = 5)
randomFursuits.forEach { fursuit ->
    println("推荐：${fursuit.displayName}")
}

// 搜索
val results = sdk.search.search(query = "fox", page = 1, pageSize = 20)
results.results.forEach { item ->
    println("搜索结果：${item.displayName}")
}

// 物种列表
val speciesList = sdk.search.getSpeciesList()
speciesList.species.forEach { species ->
    println("物种：${species.name}, 数量：${species.count}")
}
```

### 聚会活动

```kotlin
// 年度统计
val stats = sdk.gathering.getGatheringStatsThisYear()
println("今年聚会总数：${stats.total}, 即将到来：${stats.upcoming}")

// 月历视图
val monthlyGatherings = sdk.gathering.getGatheringMonthly(year = 2026, month = 4)
monthlyGatherings.forEach { gathering ->
    println("聚会：${gathering.name}, 时间：${gathering.date}")
}

// 附近聚会
val nearby = sdk.gathering.getGatheringNearby(
    latitude = 39.9042,
    longitude = 116.4074,
    radius = 10000
)
nearby.forEach { gathering ->
    println("附近聚会：${gathering.name}, 距离：${gathering.distance}m")
}

// 聚会详情
val detail = sdk.gathering.getGatheringDetail("gathering-id")
println("聚会名称：${detail.name}, 地点：${detail.location}")

// 报名列表
val registrations = sdk.gathering.getGatheringRegistrations("gathering-id")
println("报名人数：${registrations.total}")
```

### 学校与角色

```kotlin
// 搜索学校
val schools = sdk.school.searchSchools(query = "北京大学")
schools.schools.forEach { school ->
    println("学校：${school.name}, 位置：${school.location}")
}

// 获取用户角色
val characters = sdk.school.getUserCharacters("username")
characters.characters.forEach { character ->
    println("角色：${character.name}, 物种：${character.species}")
}

// 获取用户学校信息
val userSchool = sdk.school.getUserSchool("user-id")
println("用户学校：${userSchool.school?.name}, 入学年份：${userSchool.enrollmentYear}")
```

## ❌ 错误处理

SDK 提供细粒度的异常分类：

```kotlin
try {
    val userProfile = sdk.user.getUserProfile("username")
    // 处理成功结果
} catch (e: TokenExpiredException) {
    // 令牌过期，SDK 会自动刷新（当使用 getApiKey 时）
    // 或手动调用 refreshToken()
    val newToken = sdk.auth.refreshToken()
} catch (e: AuthenticationException) {
    // 认证失败
    println("认证失败：${e.message}")
} catch (e: NotFoundException) {
    // 资源未找到
    println("资源不存在：${e.message}")
} catch (e: ValidationException) {
    // 参数验证失败
    println("参数错误：${e.message}")
} catch (e: NetworkException) {
    // 网络错误
    println("网络连接失败：${e.message}")
} catch (e: ApiException) {
    // 通用 API 错误
    println("API 错误：${e.statusCode} - ${e.message}")
} catch (e: Exception) {
    // 其他未知错误
    println("未知错误：${e.message}")
}
```

### 自动令牌刷新

SDK 实现了自动令牌刷新机制，当令牌剩余有效期 <= 300 秒（5 分钟）时会自动刷新：

```kotlin
// 使用 getApiKey 方法会自动检查并刷新令牌
val apiKey = sdk.auth.getApiKey(
    clientId = "your-client-id",
    clientSecret = "your-client-secret"
)
// 如果令牌即将过期，会自动刷新
// 如果刷新失败，会自动回退到 exchangeToken
```

### 异常类型说明

| 异常类型 | 描述 | 处理建议 |
|----------|------|----------|
| `TokenExpiredException` | 访问令牌已过期 | 调用 `refreshToken()` 刷新令牌 |
| `AuthenticationException` | 认证失败 | 检查凭证是否正确，重新认证 |
| `NotFoundException` | 请求的资源不存在 | 检查资源 ID 是否正确 |
| `ValidationException` | 请求参数验证失败 | 检查并修正请求参数 |
| `NetworkException` | 网络连接错误 | 检查网络状态，可重试 |
| `ApiException` | 通用 API 错误 | 根据状态码处理 |
| `FursuitTvSdkException` | 所有 SDK 异常的基类 | 捕获所有 SDK 相关异常 |

## ⚙️ 配置选项

通过 `SdkConfig` 类配置 SDK 行为：

```kotlin
val config = SdkConfig(
    // 必需配置
    apiKey = "your-api-key",
    
    // 可选配置
    baseUrl = "https://open-global.vdsentnet.com",        // API 基础 URL
    requestTimeout = 60000,                     // 请求超时（毫秒）
    connectTimeout = 10000,                     // 连接超时（毫秒）
    socketTimeout = 30000,                      // 套接字超时（毫秒）
    logLevel = LogLevel.DEBUG,                  // 日志级别：DEBUG, INFO, WARN, ERROR
    enableRetry = true,                         // 启用重试机制
    maxRetries = 3,                             // 最大重试次数
    retryInterval = 1000,                       // 重试间隔（毫秒）
    enableLogging = true,                       // 启用 HTTP 日志记录
    userAgent = "MyApp/1.0.0"                   // 自定义 User-Agent
)

val sdk = FursuitTvSdk(config)
```

### 日志级别说明

| 级别 | 描述 | 输出内容 |
|------|------|----------|
| `DEBUG` | 调试模式 | 详细的 HTTP 请求/响应信息、头部、body |
| `INFO` | 信息模式 | 基本的请求方法、URL、状态码 |
| `WARN` | 警告模式 | 仅输出警告和错误信息 |
| `ERROR` | 错误模式 | 仅输出错误信息 |
| `NONE` | 静默模式 | 不输出任何日志 |

## 🖥️ 平台支持

### JVM 平台

- **要求**: Java 17+
- **HTTP 客户端**: ktor-client-java
- **构建命令**: `./gradlew jvmJar`
- **测试命令**: `./gradlew jvmTest`

### JS 平台

- **要求**: Node.js 16+ 或现代浏览器
- **HTTP 客户端**: ktor-client-js
- **构建命令**: `./gradlew jsJar`
- **测试命令**: `./gradlew jsTest`

### Native 平台

#### iOS / macOS

- **HTTP 客户端**: ktor-client-darwin
- **支持架构**: x64, arm64
- **构建命令**: `./gradlew compileKotlinIosX64` / `compileKotlinMacosArm64`

#### Linux

- **HTTP 客户端**: ktor-client-curl
- **支持架构**: x64, arm64
- **构建命令**: `./gradlew compileKotlinLinuxX64`

#### Windows

- **HTTP 客户端**: ktor-client-curl
- **支持架构**: x64
- **构建命令**: `./gradlew compileKotlinMingwX64`

## 🛠️ 开发指南

### 构建项目

```bash
# 克隆仓库
git clone https://github.com/your-org/fursuit-tv-sdk.git
cd fursuit-tv-sdk

# 编译所有目标
./gradlew build

# 运行测试
./gradlew allTests

# 发布到本地 Maven
./gradlew publishToMavenLocal
```

### 运行测试

```bash
# 运行所有测试
./gradlew allTests

# 仅运行 JVM 测试
./gradlew jvmTest

# 仅运行 JS 测试
./gradlew jsTest

# 查看测试报告
open build/reports/tests/allTests/index.html
```

### 代码规范

```bash
# 格式化代码
./gradlew ktlintFormat

# 检查代码规范
./gradlew ktlintCheck
```

### 发布流程

1. 更新版本号：修改 `gradle.properties` 中的 `version`
2. 生成变更日志：更新 `CHANGELOG.md`
3. 构建并测试：`./gradlew build && ./gradlew allTests`
4. 发布到 Maven Central: `./gradlew publish`
5. 创建 Git 标签：`git tag -a v1.0.0 -m "Version 1.0.0"`
6. 推送标签：`git push origin v1.0.0`

## 📚 相关资源

- [完整 API 文档](docs/api/)
- [配置指南](docs/configuration.md)
- [使用示例](docs/examples.md)
- [VDS 认证文档](vds-docs/)
- [Kotlin 官方文档](https://kotlinlang.org)
- [Ktor 客户端文档](https://ktor.io)

## 🤝 贡献指南

欢迎提交 Issue 和 Pull Request！

1. Fork 本仓库
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 开启 Pull Request

## 📄 许可证

本项目采用 MIT 许可证 - 查看 [LICENSE](LICENSE) 文件了解详情。

## 👥 维护者

- **RegadPole** - [@regadpole](https://github.com/regadpole)

## 🙏 致谢

感谢所有为这个项目做出贡献的开发者们！

---

**注意**: 本 SDK 仅供学习和研究使用。在使用本 SDK 时，请确保遵守 Fursuit.TV 的使用条款和服务协议。
