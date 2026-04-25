# Fursuit.TV SDK

[![Build and Test](https://github.com/RegadPoleCN/fursuit-tv-sdk/actions/workflows/build.yml/badge.svg)](https://github.com/RegadPoleCN/fursuit-tv-sdk/actions/workflows/build.yml)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.1.20+-purple.svg)](https://kotlinlang.org)
[![Platform](https://img.shields.io/badge/Platform-JVM%20%7C%20JS%20%7C%20Native-lightgrey.svg)](https://kotlinlang.org/docs/multiplatform.html)
[![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)

> 本仓库为第三方 SDK，与兽频道及 VDS 官方无关。基于 Kotlin Multiplatform 构建的跨平台 SDK，为 Fursuit.TV 和 VDS 账户系统提供完整的 API 访问能力。

## 快速开始

```kotlin
// 1. 添加依赖
dependencies {
    implementation("com.furrist.rp:fursuit-tv-sdk:{version}")
}

// 2. 初始化（签名交换方式）
val sdk = fursuitTvSdk {
    clientId = "vap_xxx"
    clientSecret = "your-secret"
}

// 3. 调用 API
val profile = sdk.user.getUserProfile("username")
```

其他初始化方式（已有 apiKey / OAuth）详见 [认证文档](docs/authentication.md)。

## API 模块

| 模块 | 描述 |
|------|------|
| **Auth** | 认证与授权 |
| **User** | 用户资料 |
| **Search** | 搜索发现 |
| **Gathering** | 聚会活动 |
| **School** | 学校角色 |

完整 API 文档见 [docs/api/](docs/api/)。

## 平台支持

JVM (Java 17+) / JS (Node.js 16+) / Native (iOS/macOS/Linux/Windows)

## NPM 包

包名: `@regadpole/fursuit-tv-sdk`

— 本地测试: `./gradlew assembleJsPackage` → `npm pack`

## 文档

- **[快速开始](docs/getting-started.md)** - 5 分钟快速上手
- **[认证详解](docs/authentication.md)** - 签名交换 vs OAuth
- **[错误处理](docs/error-handling.md)** - 异常类型和处理
- **[OAuth 指南](docs/oauth-guide.md)** - OAuth 2.0 完整流程
- **[最佳实践](docs/best-practices.md)** - API 使用技巧
- **[故障排除](docs/troubleshooting.md)** - 常见问题

## 📄 许可证

MIT License - 查看 [LICENSE](LICENSE) 文件。

## 🙏 致谢

感谢所有为这个项目做出贡献的开发者们！

---

**注意**: 本 SDK 仅供学习和研究使用。请确保遵守 Fursuit.TV 的使用条款和服务协议。
