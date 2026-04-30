# Fursuit.TV SDK 文档导航

欢迎使用 Fursuit.TV SDK！本文档提供了完整的 API 使用指南和最佳实践。

## 快速入口

| 文档 | 描述 |
|------|------|
| [快速开始](getting-started.md) | 5 分钟内完成 SDK 集成 |
| [认证详解](authentication.md) | 签名交换 vs OAuth 对比 |
| [配置选项](configuration.md) | 所有配置参数说明 |
| [错误处理](error-handling.md) | 异常类型和处理策略 |

## 完整文档目录

### 入门指南

- **[快速开始](getting-started.md)** - 安装、初始化和第一个 API 调用
- **[配置选项](configuration.md)** - SDK 配置参数详解
- **[错误处理](error-handling.md)** - 异常类型识别和处理策略

### 认证与授权

- **[认证详解](authentication.md)** - 两种认证方式对比和技术细节
- **[OAuth 完整指南](oauth-guide.md)** - OAuth 2.0 授权流程详解

### 进阶指南

- **[最佳实践](best-practices.md)** - API 使用技巧和性能优化
- **[故障排除](TROUBLESHOOTING.md)** - 常见问题和解决方案
- **[平台指南](platform-guide.md)** - JVM/JS/Native 平台特定配置

### API 参考文档

- **[API 文档导航](api/README.md)** - 所有 API 模块的完整参考

## API 模块概览

| 模块 | 功能 | 文档 |
|------|------|------|
| `auth` | 认证与令牌管理 | [api/auth.md](api/auth.md) |
| `base` | 基础接口（健康检查、版本） | [api/base.md](api/base.md) |
| `user` | 用户资料查询 | [api/user.md](api/user.md) |
| `search` | 搜索与发现 | [api/search.md](api/search.md) |
| `gathering` | 聚会活动 | [api/gathering.md](api/gathering.md) |
| `school` | 学校与角色 | [api/school.md](api/school.md) |

## 认证方式对比

| 特性 | 签名交换 | OAuth 2.0 |
|------|----------|-----------|
| **适用场景** | 服务端应用、后台 API 调用 | 需要用户登录的客户端应用 |
| **是否需要用户交互** | 否 | 是 |
| **获取的令牌** | `apiKey` + `accessToken` | `accessToken`（含 `refreshToken`） |
| **认证头** | `X-Api-Key`（优先）或 `Authorization: Bearer` | `Authorization: Bearer` |
| **令牌刷新** | 自动刷新（SDK 内部管理） | 手动或自动（使用 `refreshToken`） |
| **权限范围** | 应用级别权限 | 用户授权的权限范围（scope） |
| **安全性** | 高（密钥保存在服务端） | 高（支持 PKCE） |

## 快速示例

### 使用签名交换初始化

```kotlin
val sdk = fursuitTvSdk {
    clientId = "vap_xxx"
    clientSecret = "your-secret"
}

// fursuitTvSdk 是 suspend 函数，提供 clientId + clientSecret 时自动完成令牌交换
// 直接调用 API
val profile = sdk.user.getUserProfile("username")
```

### 使用已有 apiKey

```kotlin
val sdk = fursuitTvSdk {
    apiKey = "your-api-key"
}
```

### 使用 OAuth 流程

```kotlin
val sdk = fursuitTvSdk {
    clientId = "vap_xxx"
    clientSecret = "your-secret"
}

// 生成授权 URL
val authUrl = sdk.auth.getOAuthAuthorizeUrl(
    redirectUri = "my-app://callback",
    scope = "user.profile"
)

// 用户授权后，使用回调中的 code 交换 token
val tokenInfo = sdk.auth.exchangeOAuthToken(
    code = "code-from-callback",
    redirectUri = "my-app://callback"
)

// 获取用户信息
val userInfo = sdk.auth.getUserInfo()
```

### Java 示例

```java
import com.furrist.rp.furtv.sdk.factory.JvmFursuitTvSdkBuilder;

// API Key 模式（同步）
FursuitTvSdk sdk = JvmFursuitTvSdkBuilder.create()
    .apiKey("your-api-key")
    .build();

// 签名交换模式（需通过 await 辅助方法）
FursuitTvSdk sdk = await((scope, cont) ->
    JvmFursuitTvSdkBuilder.create()
        .clientId("vap_xxx")
        .clientSecret("your-secret")
        .buildAsync(cont)
);
```

### JavaScript/TypeScript 示例

```typescript
import { fursuitTvSdk } from "@regadpole/fursuit-tv-sdk";

const sdk = await fursuitTvSdk({
    clientId: "vap_xxx",
    clientSecret: "your-secret",
});
```

## 术语说明

- **`clientId`** - 应用 ID（即 VDS 文档中的 `appId`），格式为 `vap_xxxx`，用于标识应用身份
- **`clientSecret`** - 应用密钥，与 `clientId` 配对使用，需妥善保管
- **`apiKey`** - VDS 颁发的 API 密钥，用于签名交换后的 API 认证
- **`accessToken`** - 访问令牌，用于 `Authorization: Bearer` 认证头
- **`refreshToken`** - 刷新令牌（仅 OAuth），用于获取新的 `accessToken`
- **`state`** - OAuth 流程中的状态参数，用于防止 CSRF 攻击

## 相关资源

- [GitHub 仓库](https://github.com/RegadPoleCN/fursuit-tv-sdk)
- [Kotlin Multiplatform 文档](https://kotlinlang.org/docs/multiplatform.html)
- [OAuth 2.0 规范](https://oauth.net/2/)
- [PKCE 规范 (RFC 7636)](https://tools.ietf.org/html/rfc7636)

## 需要帮助？

如遇到问题，请查看：

1. [故障排除](TROUBLESHOOTING.md) - 常见问题解答
2. [最佳实践](best-practices.md) - API 使用技巧
3. GitHub Issues - 提交问题或查看已知问题

---

**注意**: 本 SDK 为第三方实现，与 Fursuit.TV 及 VDS 官方无关。使用时请遵守相关服务条款。
