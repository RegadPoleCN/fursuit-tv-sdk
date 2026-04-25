# 更新日志

本文件记录项目的所有重要变更。格式基于 [Keep a Changelog](https://keepachangelog.com/zh-CN/1.1.0/)，
并遵循 [语义化版本](https://semver.org/lang/zh-CN/) 规范。

***

## \[0.2.0] - 2026-04-26

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

## \[0.1.0] - 2026-04-15

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

