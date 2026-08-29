# 更新日志

本文件记录项目的所有重要变更。格式基于 [Keep a Changelog](https://keepachangelog.com/zh-CN/1.1.0/)，
并遵循 [语义化版本](https://semver.org/lang/zh-CN/) 规范。

## [Unreleased]

### 重大变更 (BREAKING)

- **`PopularUser.popularityScore` 类型 `Int?` → `String?`**：vds-docs 热门推荐响应中 `popularity_score` 为字符串（如 `"2989"`），原 `Int?` 与文档不符（审计项 #12）
- **`BaseApi.checkAndroidVersion` 的 `currentVersionCode` 改为必填**：方法参数与 `AndroidVersionCheckRequest.currentVersionCode` 由 `Int?` 改为非空 `Int`；vds-docs 示例恒携带该参数（审计项 #40）
- **PKCE 支持整体删除**：`loginWithOAuth` 不再自动生成/发送 code_challenge；`getOAuthAuthorizeUrl` 移除 `enablePkce`/`codeChallenge` 参数；`exchangeOAuthToken` 移除 `codeVerifier` 参数；`OAuthConfig.enablePkce` 删除；`Sha256`/`toHex` 工具删除（vds-docs 未记载 PKCE，属文档外扩展）（审计项 #6）

### 变更

- `loginWithOAuth` scope 默认值改为 `"profile"`（审计项 #24）
- `/account/sso/*` 请求不再注入 `X-Api-Key`（审计项 #23）
- sso 端点错误响应结构化为 `OAuthException(errorCode)`（审计项 #25）
- OAuth 授权 URL 的 query 参数值（`client_id`/`redirect_uri`/`scope`/`state` 等）按文档要求 URL 编码（审计项 #1）
- 修复 `loginWithOAuth` 双重启动回调服务器（审计项 #2）；Native `startListening` 幂等守卫（审计项 #3）；Native 回调读取改头结束符检测，浏览器 GET 保持连接不再挂起（审计项 #4）；Node.js 实现真实本地 OAuth 回调服务器并补浏览器中继页接入约定（审计项 #5）

### 新增

- `ContactRequestState` 补 `can_request` / `requires_auth` 映射（审计项 #7）
- `UserProfile` 补 `contact_reputation_level`（审计项 #9）
- `SearchUser` / `SpeciesSearchUser` 补 `like_count` / `is_liked`（审计项 #10）
- `SearchResponse` / `SpeciesSearchResponse` 补顶层 `total_is_estimate`（审计项 #11）
- `CharacterInfo` 补 `images` / `birthday` / `created_at` / `updated_at`（审计项 #16）
- `TokenData` / `OAuthTokenData` / `UserInfoData` 补 `requestId`（审计项 #29）

### 内部改进

- **文档清理**：`docs/authentication.md` + `docs/MIGRATION.md` 重写以匹配 `init-builder-refactor` 新模型（2 种初始化写法 + apiKey-only 禁用 + `socialLinks` 迁移示例）
- **examples 同步**：`examples/java/Main.java` 用 `FursuitTvSdkBuilder`（替换 `JvmFursuitTvSdkBuilder`）；`examples/jvm/Main.kt` 用 builder 写法
- **契约测试**：~32 个 vds-docs JSON fixture + 6 个 capability 测试类（auth / base / discovery / user / school / gathering + user-characters cross-cutting），验证前 5 个 change 重塑的 DTO 反序列化兼容性

**最新版本：0.3.0**（2026-07-22）

***

## [0.3.0] - 2026-07-22

### 重大变更 (BREAKING)

- **Auth 重构**：`TokenInfo` 改 `sealed class`（`Platform` / `OAuth` 子类）；`getAccessToken()` / `refreshOAuthToken()` 删除；`getUserInfo()` 改用 `Authorization: Bearer <oauthToken>`；`exchangeOAuthToken()` 不发送 platform Bearer 头；`expiresAt` 减 30s skew 缓冲；`RefreshTooEarly` 自动 fallback 到 `exchangeToken()`
- **SDK 初始化重构**：新增 `FursuitTvSdkBuilder`（chainable 跨语言入口）；删除 `JvmFursuitTvSdkBuilder` / `fursuitTvSdkBlocking` / 3 个平台文件 `FursuitTvSdkJvm/Js/Native.kt`；apiKey-only init 禁用（强制 `clientId` + `clientSecret`）；`HttpClientConfig.getClient` 改 2 参数 + `Pair<SdkConfig, AuthHolder>` 缓存键
- **DTO 响应结构重塑**：22 个 `*Response` 重塑为 flat / typed-wrapped / no-success-field；4 个 auth 包装（`TokenExchangeResponse` / `TokenRefreshResponse` / `OAuthTokenResponse` / `UserInfoResponse`）+ 19 个 data wrapper 删除
- **`UserProfile.socialLinks` / `contactInfo` 类型重构**：`Map<String, String>?` → 带自定义 `KSerializer` 的嵌套结构（`entries: Map<String, String>` + `custom: List<CustomLink>`）
- **`ThemePacksManifestData` 重构**：删除 `packs`，新增 `themes` + 嵌套 `ThemePackMetadata`
- **`VisitorInfo` 新增 `visitId`**（`@SerialName("visit_id")`）作为访问记录主键

### 新增

- `AuthHolder` 类（`@Volatile var auth: AuthManager?`）
- `AuthManager.withFreshToken { ... }`（reactive re-exchange；check-then-call pattern 避开 `Mutex` 不可重入）
- `HttpClientConfig.defaultRequest` 自动注入 `X-Api-Key`（从 `authHolder.auth?.getApiKey()` 按请求读取）
- 8 个新 DTO 类：`ContactRequestState` / `CustomLink` / `UserProfileSocialLinks` / `UserProfileContactInfo` / `ThemePackMetadata` / `ThemePackAuthor` / `ThemePackHomeBackground` / `ThemePackPreview`
- 26+ 个新 nullable 字段（`GatheringDetailData` / `UserProfile` / `UserVisitorsResponse` / `SearchPagination` 等）
- **平台编译恢复**：JS target + 9 个 Native target 现在可正常编译（之前阻塞 npm 包和 Native 发版）

### 删除

- DTO 包装类 19 个（`PopularData` / `SearchData` / `UserCharactersData` 等）
- 平台文件 `JvmFursuitTvSdkBuilder.kt` + 3 个 `FursuitTvSdkXxx.kt`
- `expect/actual fursuitTvSdkBlocking` 整链（4 个声明位置）

### 修复

- **`HttpClientConfig` 修复 JVM-only `synchronized`**：JS + Native 编译恢复（改为无锁 `getOrPut`；JVM 多线程并发首次访问有已知轻微 race）
- **`TokenInfo.Platform` / `TokenInfo.OAuth` 嵌套 `@JsExport` 删除**：JS 编译恢复
- `HttpClientConfig` 内部化（移除 `@JsExport` / `@PublishedApi` / `@Suppress`）
- `OAuthCallbackResult` 外层 `@JsExport` 删除（内部 flow 类型）
- `HttpClientConfig` 错误响应 body 写入异常 message（原硬编码空字符串）
- `StateStoreInternal` 用 `Mutex` 保护（KMP 线程安全）

### 内部改进

- `TokenInfo` 字段加 `@Volatile`（跨线程可见性）
- `MutableSdkConfig.toImmutable()` 显式 `apiKey = null`（丢弃 caller 误设）
- 5 个 `*Api` 类构造器增加 `auth: AuthManager` 参数；业务方法 wrap 在 `auth.withFreshToken { ... }`（省去 60+ 行 `header("X-Api-Key", apiKey)` boilerplate）

***

## [0.2.2] - 2026-05-02

***

## [0.2.1] - 2026-04-29

***

## [0.2.0] - 2026-04-26

### 新增

- **NPM 发布支持**: 集成 `org.jetbrains.kotlin.npm-publish` 插件，支持将 JS 目标打包为 npm 包
- **CI/CD 双平台发布**: GitHub Actions 工作流同时支持 Maven Central 和 NPM 发布
- **包名**: `@regadpole/fursuit-tv-sdk`
- **本地打包命令**: `./gradlew assembleJsPackage`

### 变更

- **版本号统一管理**: 所有文档中的版本号使用 `{version}` 占位符，唯一事实来源为 `gradle/libs.versions.toml`
- **README 精简**: 从 \~190 行精简至 \~70 行

### 修复

- **Multiplatform 兼容性**:
  - 移除 commonMain 中对 JVM 特定类 `java.net.BindException` 的引用
  - 改为通过异常消息检测端口绑定错误，实现跨平台兼容
- **代码质量**:
  - 重构过长方法 `startAndGetCallback`（61 行 → 多个小方法）
  - 提取 Magic Number 为命名常量（MAX\_PORT\_NUMBER、LOG\_PREFIX\_LENGTH 等）
  - 修复 MaxLineLength 违规

### 安全

- npmToken 通过 `project.findProperty()` 从 gradle.properties 读取，不硬编码到源码中
- `.gitignore` 已排除 `gradle.properties.local`

***

## [0.1.0] - 2026-04-15

### 新增

- **初始版本发布**
- **多平台支持**: JVM (Java 17+)、JavaScript (Node.js 16+)
  - Native: iOS、macOS、Linux、Windows、Android
- **完整 API 覆盖**:
  - Base — 健康检查、基础服务
    — User — 用户资料、关注系统
  - Search — 热门推荐、搜索发现
    — Gathering — 聚会活动管理
    — School — 学校角色系统
- **认证方式**:
  - OAuth 2.0 授权码模式
  - 签名交换（Signature Exchange）
  - API Key 直接使用
- **自动令牌刷新机制**
- **完整的文档体系**
  - 快速开始指南
  - 认证详解
  - 平台指南（JVM / JS / Native / Android / iOS）
  - 错误处理与故障排除
  - 发布指南（Maven Central + NPM）

### 基础设施

- GitHub Actions CI/CD 流水线（构建、测试、发布）
- 代码质量工具: detekt、ktlint、dokka
- 二进制兼容性验证器
- 多平台示例项目

***

## 变更类型说明

| 类型     | 说明              |
| ------ | --------------- |
| **新增** | 新功能、新 API、新平台支持 |
| **变更** | 现有功能的行为或 API 变更 |
| **弃用** | 即将移除的功能         |
| **移除** | 已删除的功能          |
| **修复** | Bug 修复          |
| **安全** | 安全改进            |

## 版本规范

本项目遵循语义化版本（SemVer）：

- **主版本号（MAJOR）**: 不兼容的 API 变更
- **次版本号（MINOR）**: 向后兼容的功能新增
- **修订号（PATCH）**: 向后兼容的问题修复

## 链接

- [GitHub Releases](https://github.com/RegadPoleCN/fursuit-tv-sdk/releases)
- [Maven Central](https://central.sonatype.com/search?q=fursuit-tv-sdk)
- [npm](https://www.npmjs.com/package/@regadpole/fursuit-tv-sdk)
