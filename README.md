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

### 2. 初始化 SDK

#### 方式 1: 签名交换（推荐，用于业务 API）

```kotlin
// DSL 方式（自动获取 apiKey）
val sdk = fursuitTvSdk {
    clientId = "vap_xxx"
    clientSecret = "your-secret"
}

// 可以直接调用业务 API
val profile = sdk.user.getUserProfile("username")
```

#### 方式 2: 已有 apiKey

```kotlin
val sdk = fursuitTvSdk {
    apiKey = "your-api-key"
}
```

#### 方式 3: OAuth（仅用于获取用户信息）

```kotlin
// 初始化 SDK
val sdk = fursuitTvSdk {
    clientId = "vap_xxx"
    clientSecret = "your-secret"
}

// 生成授权 URL
val authUrl = sdk.auth.getOAuthAuthorizeUrl(
    redirectUri = "my-app://callback",
    scope = "user.profile",
    state = "random-state"
)

// 用户授权后，使用回调中的 code 交换 token
val tokenInfo = sdk.auth.exchangeOAuthToken(
    code = "code-from-callback",
    redirectUri = "my-app://callback"
)

// 获取用户信息
val userInfo = sdk.auth.getUserInfo()
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

- **[快速开始](docs/getting-started.md)** - 5 分钟快速上手
- **[认证详解](docs/authentication.md)** - 签名交换 vs OAuth
- **[配置选项](docs/configuration.md)** - 所有配置参数说明
- **[错误处理](docs/error-handling.md)** - 异常类型和处理
- **[OAuth 指南](docs/oauth-guide.md)** - OAuth 2.0 完整流程
- **[最佳实践](docs/best-practices.md)** - API 使用技巧
- **[故障排除](docs/troubleshooting.md)** - 常见问题
- **[平台指南](docs/platform-guide.md)** - 特定平台配置
- **[API 参考](docs/api/)** - 完整的 API 文档

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
