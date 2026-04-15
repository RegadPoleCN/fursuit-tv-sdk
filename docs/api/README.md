# Fursuit.TV SDK API 参考文档

本目录包含 Fursuit.TV SDK 的所有 API 模块详细文档。

## API 模块概览

| 模块 | 描述 | 主要功能 | 文档 |
|------|------|----------|------|
| **Auth** | 认证与授权 | 令牌交换、OAuth 授权、令牌刷新、用户信息 | [auth.md](auth.md) |
| **Base** | 基础接口 | 健康检查、版本信息、主题包管理 | [base.md](base.md) |
| **User** | 用户资料 | 用户信息、点赞状态、访客记录、社交徽章 | [user.md](user.md) |
| **Search** | 搜索发现 | 热门推荐、随机推荐、搜索、物种列表 | [search.md](search.md) |
| **Gathering** | 聚会活动 | 聚会统计、月历视图、附近聚会、报名管理 | [gathering.md](gathering.md) |
| **School** | 学校角色 | 学校搜索、角色管理、用户学校信息 | [school.md](school.md) |

## 快速链接

- [认证 API](auth.md) - 令牌交换、OAuth 授权、令牌刷新
- [基础 API](base.md) - 健康检查、版本管理、主题包
- [用户 API](user.md) - 用户资料、关系、访客、徽章
- [搜索 API](search.md) - 热门推荐、搜索、物种查询
- [聚会 API](gathering.md) - 聚会列表、详情、报名
- [学校 API](school.md) - 学校搜索、角色管理

## 使用示例

```kotlin
// 初始化 SDK
val sdk = FursuitTvSdk(
    appId = "vap_xxxxxxxxxxxxxxxx",
    appSecret = "your-app-secret"
)

// 调用用户 API
val userProfile = sdk.user.getUserProfile("username")

// 调用搜索 API
val popular = sdk.search.getPopular()

// 调用聚会 API
val gatherings = sdk.gathering.getMonthly(2024, 12)
```

## 错误处理

所有 API 调用都可能抛出以下异常：

- `TokenExpiredException` - 令牌过期，SDK 会自动刷新
- `AuthenticationException` - 认证失败
- `NotFoundException` - 资源不存在
- `ValidationException` - 参数错误
- `NetworkException` - 网络连接失败
- `ApiException` - API 错误（包含状态码和错误信息）

详见 [故障排除](../TROUBLESHOOTING.md)。

## 相关文档

- [开发者指南](../DEVELOPER_GUIDE.md) - 快速上手
- [认证与配置](authentication.md) - 详细的认证方式和配置选项
- [最佳实践](../BEST_PRACTICES.md) - API 使用技巧
- [故障排除](../TROUBLESHOOTING.md) - 常见问题
