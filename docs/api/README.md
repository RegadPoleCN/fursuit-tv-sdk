# API 文档导航

Fursuit.TV SDK API 模块参考。

## API 模块

| 模块 | 类 | 描述 | 文档 |
|------|-----|------|------|
| **Auth** | `AuthManager` | 认证与令牌管理 | [auth.md](auth.md) |
| **Base** | `BaseApi` | 基础接口 | [base.md](base.md) |
| **User** | `UserApi` | 用户资料查询 | [user.md](user.md) |
| **Search** | `SearchApi` | 搜索与发现 | [search.md](search.md) |
| **Gathering** | `GatheringApi` | 聚会活动 | [gathering.md](gathering.md) |
| **School** | `SchoolApi` | 学校与角色 | [school.md](school.md) |

## 快速开始

```kotlin
val sdk = fursuitTvSdk {
    clientId = "vap_xxx"
    clientSecret = "your-secret"
}

// 调用 API
val profile = sdk.user.getUserProfile("username")
```

## 认证方式

### 签名交换

```kotlin
val sdk = fursuitTvSdk {
    clientId = "vap_xxx"
    clientSecret = "secret"
}
// 自动获取令牌
```

### OAuth 2.0

```kotlin
val authUrl = sdk.auth.getOAuthAuthorizeUrl(
    redirectUri = "my-app://callback"
)
val tokenInfo = sdk.auth.exchangeOAuthToken(code, redirectUri)
```

**详情**: [oauth-guide.md](../oauth-guide.md)

## 错误处理

所有 API 调用可能抛出：
- `ApiException` - API 错误
- `NetworkException` - 网络错误
- `TokenExpiredException` - 令牌过期
- `NotFoundException` - 资源不存在

**详情**: [error-handling.md](../error-handling.md)

## 配置选项

```kotlin
val sdk = fursuitTvSdk {
    clientId = "vap_xxx"
    clientSecret = "secret"
    logLevel = LogLevel.INFO
    enableRetry = true
}
```

**详情**: [configuration.md](../configuration.md)

## 数据模型

各模块的数据模型定义在对应的 `Models.kt` 文件中：
- `auth/AuthModels.kt` - `TokenInfo`, `UserInfo`
- `user/UserModels.kt` - `UserProfile`, `LikeStatus`
- `search/SearchModels.kt` - `SearchResponse`, `Popular`
- `gathering/GatheringModels.kt` - `Gathering`, `GatheringDetail`
- `school/SchoolModels.kt` - `School`, `CharacterInfo`

## 平台支持

| 平台 | 要求 |
|------|------|
| JVM | Java 17+ |
| JavaScript | Node.js 16+ |
| iOS | iOS 13+ |
| Android | API 21+ |

**详情**: [platform-guide.md](../platform-guide.md)

## API 端点

| 模块 | 端点前缀 |
|------|----------|
| Auth | `/api/auth/` |
| Base | `/api/` |
| User | `/api/proxy/furtv/` |
| Search | `/api/proxy/furtv/` |
| Gathering | `/api/proxy/furtv/` |
| School | `/api/proxy/furtv/` |

详细端点请参考各模块文档。

## 相关资源

- [GitHub 仓库](https://github.com/RegadPoleCN/fursuit-tv-sdk)
- [快速开始](../getting-started.md)
- [最佳实践](../best-practices.md)
