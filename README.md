# Fursuit.TV SDK

[![Build and Test](https://github.com/RegadPoleCN/fursuit-tv-sdk/actions/workflows/build.yml/badge.svg)](https://github.com/RegadPoleCN/fursuit-tv-sdk/actions/workflows/build.yml)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.1.20+-purple.svg)](https://kotlinlang.org)
[![Platform](https://img.shields.io/badge/Platform-JVM%20%7C%20JS%20%7C%20Native-lightgrey.svg)](https://kotlinlang.org/docs/multiplatform.html)
[![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)

> 本仓库为第三方 SDK，与兽频道及 VDS 官方无关。基于 Kotlin Multiplatform 构建的跨平台 SDK，为 Fursuit.TV 和 VDS 账户系统提供完整的 API 访问能力。

## 🚀 快速开始

### 1. 添加依赖

```kotlin
// Gradle (Kotlin DSL)
dependencies {
    implementation("com.furrist.rp:fursuit-tv-sdk:0.1.0")
}
```

### 2. 初始化 SDK（四种方式）

**方式 1: 使用 apiKey（最简单）**
```kotlin
val sdk = FursuitTvSdk(apiKey = "your-api-key")
```

**方式 2: 使用 clientId + clientSecret（推荐）**
```kotlin
val sdk = FursuitTvSdk(
    clientId = "vap_xxxxxxxxxxxxxxxx",
    clientSecret = "your-client-secret"
)
runBlocking {
    sdk.auth.exchangeToken(clientId, clientSecret)
}
// SDK 会自动管理令牌刷新
```

**方式 3: OAuth 2.0（需要用户授权）**
```kotlin
// 第一步：初始化 SDK（传入 clientId 和 clientSecret）
val sdk = FursuitTvSdk(
    clientId = "vap_xxxxxxxxxxxxxxxx",
    clientSecret = "your-client-secret"
)

// 第二步：调用 OAuth 授权（自动使用 SDK 配置中的 clientId 和 clientSecret）
val oauthResult = sdk.auth.initOAuth(
    config = OAuthConfig(callbackHost = "localhost", callbackPort = 8080)
)
```

**方式 4: 使用 accessToken**
```kotlin
val sdk = FursuitTvSdk(accessToken = "your-access-token")
```

### 3. 调用 API

```kotlin
// 获取用户资料
val profile = sdk.user.getUserProfile("username")
println("昵称：${profile.displayName}")

// 获取热门推荐
val popular = sdk.search.getPopular()
println("热门用户：${popular.users.size}")
```

## 📦 API 模块

| 模块 | 描述 | 详细文档 |
|------|------|----------|
| **Auth** | 认证与授权 | [auth.md](docs/api/auth.md) |
| **Base** | 基础接口 | [base.md](docs/api/base.md) |
| **User** | 用户资料 | [user.md](docs/api/user.md) |
| **Search** | 搜索发现 | [search.md](docs/api/search.md) |
| **Gathering** | 聚会活动 | [gathering.md](docs/api/gathering.md) |
| **School** | 学校角色 | [school.md](docs/api/school.md) |

## ❌ 错误处理

```kotlin
try {
    val profile = sdk.user.getUserProfile("username")
} catch (e: TokenExpiredException) {
    // 令牌过期，SDK 会自动刷新
} catch (e: AuthenticationException) {
    println("认证失败：${e.message}")
} catch (e: NotFoundException) {
    println("资源不存在")
} catch (e: ApiException) {
    println("API 错误：${e.statusCode}")
}
```

## 🖥️ 平台支持

| 平台 | 要求 | 构建命令 |
|------|------|----------|
| **JVM** | Java 17+ | `./gradlew jvmJar` |
| **JS** | Node.js 16+ | `./gradlew jsJar` |
| **Native** | iOS/macOS/Linux/Windows | `./gradlew compileKotlin<Platform>` |

## 📚 文档导航

- **[API 参考](docs/api/)** - 完整的 API 文档
- **[开发者指南](docs/DEVELOPER_GUIDE.md)** - 5 分钟快速上手
- **[认证与配置](docs/authentication.md)** - 认证方式和配置详解
- **[最佳实践](docs/BEST_PRACTICES.md)** - API 使用技巧
- **[故障排除](docs/TROUBLESHOOTING.md)** - 常见问题
- **[平台指南](docs/PLATFORM_GUIDE.md)** - 特定平台配置
- **[OAuth 指南](docs/oauth.md)** - OAuth 2.0 流程详解

## ⚙️ 配置示例

```kotlin
val config = SdkConfig(
    apiKey = "your-api-key",
    baseUrl = "https://open-global.vdsentnet.com",
    requestTimeout = 60000,
    logLevel = LogLevel.DEBUG,
    enableRetry = true,
    maxRetries = 3
)
val sdk = FursuitTvSdk(config)
```

## 🤝 贡献

欢迎提交 Issue 和 Pull Request！

1. Fork 本仓库
2. 创建特性分支
3. 提交更改
4. 推送到分支
5. 开启 Pull Request

## 📄 许可证

MIT License - 查看 [LICENSE](LICENSE) 文件。

## 🙏 致谢

感谢所有为这个项目做出贡献的开发者们！

---

**注意**: 本 SDK 仅供学习和研究使用。请确保遵守 Fursuit.TV 的使用条款和服务协议。
