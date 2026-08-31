# Module fursuit-tv-sdk

Fursuit.TV 跨平台 SDK（Kotlin Multiplatform），为 Fursuit.TV（兽频道）与 VDS 账户系统提供完整的 API 访问能力。本仓库为第三方 SDK，与兽频道及 VDS 官方无关。

## 入口

- `fursuitTvSdk { }` DSL 或 `FursuitTvSdkBuilder`：创建 SDK 实例（必须提供 `clientId` + `clientSecret`，platform apiKey 由签名交换自动获取）
- `FursuitTvSdk`：主客户端，按域访问各 API 模块

## API 模块

| 属性 | 类 | 能力 |
|------|----|------|
| `base` | `BaseApi` | 基础接口（HelloWorld、健康检查、版本、主题包） |
| `user` | `UserApi` | 用户公开资料、关系、访客、徽章、商店 |
| `search` | `SearchApi` | 热门/随机推荐、关键词搜索、物种检索、热门地区 |
| `gathering` | `GatheringApi` | 聚会年度统计、月历、附近模式、详情、报名 |
| `school` | `SchoolApi` | 学校搜索/详情、用户学校、用户角色 |

## 认证

- 签名交换（`exchangeToken`）为业务 API 的前置步骤，`X-Api-Key` 由 SDK 自动注入
- `refreshToken`：签名换新（过期窗口内优先换新，失败回落签名交换）
- `loginWithOAuth(scope = "profile")`：完整 OAuth 授权码流程，获取用户令牌后访问 userinfo

完整文档见仓库 `docs/` 目录（getting-started / authentication / error-handling / api / MIGRATION）。
