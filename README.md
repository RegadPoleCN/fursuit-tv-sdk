# Fursuit.TV SDK

[![Build and Test](https://github.com/RegadPoleCN/fursuit-tv-sdk/actions/workflows/build.yml/badge.svg)](https://github.com/RegadPoleCN/fursuit-tv-sdk/actions/workflows/build.yml)
[![Generate Documentation](https://github.com/RegadPoleCN/fursuit-tv-sdk/actions/workflows/publish.yml/badge.svg)](https://github.com/RegadPoleCN/fursuit-tv-sdk/actions/workflows/publish.yml)
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

## 📚 文档资源

- [开发者指南](docs/DEVELOPER_GUIDE.md) - 快速上手
- [最佳实践](docs/BEST_PRACTICES.md) - KMP 开发建议
- [故障排除](docs/TROUBLESHOOTING.md) - 常见问题
- [平台指南](docs/PLATFORM_GUIDE.md) - 平台配置
- [完整 API 文档](docs/api/) - API 参考

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

#### 方式 2: 使用 appId + appSecret（推荐，适用于新用户）

如果您还没有 apiKey，可以使用 appId 和 appSecret 初始化 SDK。SDK 会调用签名交换接口获取 accessToken 和 apiKey，并自动管理令牌的刷新。SDK 会使用 `X-Api-Key` 进行认证。

```kotlin
// 使用 appId 和 appSecret 初始化
val sdk = FursuitTvSdk(
    appId = "vap_xxxxxxxxxxxxxxxx",
    appSecret = "your-app-secret"
)

// 获取初始令牌（在协程中）
runBlocking {
    sdk.auth.exchangeToken(
        appId = "vap_xxxxxxxxxxxxxxxx",
        appSecret = "your-app-secret"
    )
}
// SDK 会自动管理令牌的刷新（当剩余有效期 <= 300 秒时）
```

**适用场景**：新用户，服务端应用，需要长期访问 API 的场景。SDK 会自动调用签名交换接口获取 accessToken 和 apiKey（两个不同的值），并在令牌即将过期时自动刷新。

#### 方式 3: OAuth 2.0 授权（适用于需要用户授权的应用）

适用于需要用户登录和授权的应用场景。SDK 提供自动化的 OAuth 流程，支持本地回调服务器。

**注意**：OAuth 流程仅需要 `appId`，不需要 `appSecret`。

**基本用法（使用默认本地回调）：**

```kotlin
// 初始化 SDK（无需预先提供凭证）
val sdk = FursuitTvSdk()

// 使用 initWithOAuth 方法，自动启动本地回调服务器并打开浏览器
val oauthResult = sdk.auth.initWithOAuth(
    appId = "vap_xxxxxxxxxxxxxxxx"
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

**OAuthConfig 参数**：`callbackHost`（主机）| `callbackPort`（端口）| `callbackPath`（路径）| `stateTimeoutMinutes`（超时）| `enablePkce`（PKCE）

**🌍 跨平台 OAuth 支持说明：**

- **统一实现**：所有平台使用 Ktor CIO 引擎启动本地 HTTP 回调服务器
- **JS 平台**：仅支持 Node.js 环境，不支持 Browser 环境（无法运行 HTTP 服务器）

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
    appId = "vap_xxxxxxxxxxxxxxxx",
    appSecret = "your-app-secret"
)
println("访问令牌（accessToken）：${tokenInfo.accessToken}")
println("API 密钥（apiKey）：${tokenInfo.apiKey}")
```

**注意**：签名交换接口返回的 `accessToken` 和 `apiKey` 是两个不同的值。

#### OAuth 2.0 自动授权流程（推荐）

```kotlin
val sdk = FursuitTvSdk()
val oauthResult = sdk.auth.initWithOAuth(
    appId = "vap_xxxxxxxxxxxxxxxx"
)

// oauthResult 包含完整的授权结果
println("访问令牌：${oauthResult.accessToken}")
println("用户 ID: ${oauthResult.userId}")
println("用户名：${oauthResult.username}")
```

**认证头说明**：
- **apiKey / appId + appSecret**：使用 `X-Api-Key` 认证头
- **OAuth 2.0 / access_token**：使用 `Authorization: Bearer` 认证头

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
```

更多 API 使用示例请查看 [开发者指南](docs/DEVELOPER_GUIDE.md)。

## ❌ 错误处理

SDK 提供细粒度的异常分类：

```kotlin
try {
    val userProfile = sdk.user.getUserProfile("username")
    // 处理成功结果
} catch (e: TokenExpiredException) {
    // 令牌过期，SDK 会自动刷新
} catch (e: AuthenticationException) {
    println("认证失败：${e.message}")
} catch (e: NotFoundException) {
    println("资源不存在：${e.message}")
} catch (e: ValidationException) {
    println("参数错误：${e.message}")
} catch (e: NetworkException) {
    println("网络连接失败：${e.message}")
} catch (e: ApiException) {
    println("API 错误：${e.statusCode} - ${e.message}")
} catch (e: Exception) {
    println("未知错误：${e.message}")
}
```

SDK 实现了自动令牌刷新机制，当令牌剩余有效期 <= 300 秒时会自动刷新。详见 [故障排除](docs/TROUBLESHOOTING.md)。

## ⚙️ 配置选项

通过 `SdkConfig` 类配置 SDK 行为：

```kotlin
val config = SdkConfig(
    // 必需配置
    apiKey = "your-api-key",
    
    // 可选配置
    baseUrl = "https://open-global.vdsentnet.com",        // API 基础 URL
    requestTimeout = 60000,                     // 请求超时（毫秒）
    logLevel = LogLevel.DEBUG,                  // 日志级别：DEBUG, INFO, WARN, ERROR, NONE
    enableRetry = true,                         // 启用重试机制
    maxRetries = 3,                             // 最大重试次数
    enableLogging = true                        // 启用 HTTP 日志记录
)

val sdk = FursuitTvSdk(config)
```

**日志级别**：`DEBUG`（详细）| `INFO`（基本）| `WARN`（警告）| `ERROR`（错误）| `NONE`（静默）

详细配置说明请查看 [平台指南](docs/PLATFORM_GUIDE.md)。

## 🖥️ 平台支持

| 平台 | 状态 | 说明 |
|------|------|------|
| JVM | ✅ | Java 8+ |
| JavaScript | ✅ | Node.js 16+ |
| iOS | ✅ | X64, Arm64 |
| macOS | ✅ | X64, Arm64 |
| Linux | ✅ | X64, Arm64 |
| Windows | ✅ | X64 |
| Android Native | ✅ | 多架构支持 |

### JVM 平台

- **要求**: Java 17+
- **HTTP 客户端**: ktor-client-java
- **构建命令**: `./gradlew jvmJar`
- **测试命令**: `./gradlew jvmTest`

### JS 平台

- **要求**: Node.js 16+
- **HTTP 客户端**: ktor-client-js
- **构建命令**: `./gradlew jsJar`
- **测试命令**: `./gradlew jsTest`
- **注意**: 仅支持 Node.js 环境（不支持 Browser）

### Native 平台

- **iOS/macOS**: ktor-client-darwin，支持 x64/arm64
- **Linux/Windows**: ktor-client-curl，支持 x64
- **构建命令**: `./gradlew compileKotlin<Platform><Arch>`（如 `compileKotlinIosX64`、`compileKotlinLinuxX64`）

详细平台配置请查看 [平台指南](docs/PLATFORM_GUIDE.md)。

## 🛠️ 开发指南

### CI/CD

本项目使用 GitHub Actions 进行持续集成：

- ✅ **build.yml** - 构建和测试（push/PR 触发）
- ✅ **publish.yml** - 文档生成和部署（tag/main 触发）
- ✅ **dependabot.yml** - 自动依赖更新

### 构建项目

```bash
# 克隆仓库
git clone https://github.com/RegadPoleCN/fursuit-tv-sdk.git
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
# 检查代码规范
./gradlew detekt

# 格式化代码
./gradlew ktlintFormat

# 检查代码格式
./gradlew ktlintCheck
```

### 发布流程

1. 更新版本号：修改 `gradle.properties` 中的 `version`
2. 生成变更日志：更新 `CHANGELOG.md`
3. 构建并测试：`./gradlew build && ./gradlew allTests`
4. 创建 Git 标签：`git tag -a v1.0.0 -m "Version 1.0.0"`
5. 推送标签：`git push origin v1.0.0`

CI/CD 会自动生成文档并部署到 GitHub Pages。

## 📚 相关资源

- [开发者指南](docs/DEVELOPER_GUIDE.md) - 快速上手
- [最佳实践](docs/BEST_PRACTICES.md) - KMP 开发建议
- [故障排除](docs/TROUBLESHOOTING.md) - 常见问题
- [平台指南](docs/PLATFORM_GUIDE.md) - 平台配置
- [VDS 认证文档](https://developer.vds.pub/docs)
- [Kotlin 官方文档](https://kotlinlang.org)
- [Ktor 客户端文档](https://ktor.io)

## 🤝 贡献指南

欢迎提交 Issue 和 Pull Request！详细指南请查看 [CONTRIBUTING.md](CONTRIBUTING.md)。

1. Fork 本仓库
2. 创建特性分支
3. 提交更改
4. 推送到分支
5. 开启 Pull Request

## 📄 许可证

本项目采用 MIT 许可证 - 查看 [LICENSE](LICENSE) 文件了解详情。

## 👥 维护者

- **RegadPole** - [@RegadPoleCN](https://github.com/RegadPoleCN)

## 🙏 致谢

感谢所有为这个项目做出贡献的开发者们！

---

**注意**: 本 SDK 仅供学习和研究使用。在使用本 SDK 时，请确保遵守 Fursuit.TV 的使用条款和服务协议。

**联系方式**: 如有问题或建议，请通过 GitHub Issues 联系我们。
