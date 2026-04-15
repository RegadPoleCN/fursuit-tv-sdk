# Contributing to Fursuit.TV SDK

首先，感谢您考虑为 Fursuit.TV SDK 做出贡献！

## 📋 目录

- [行为准则](#行为准则)
- [如何贡献](#如何贡献)
- [开发环境设置](#开发环境设置)
- [提交流程](#提交流程)
- [代码风格](#代码风格)
- [测试](#测试)
- [问题报告](#问题报告)
- [功能建议](#功能建议)

## 行为准则

本项目采用 [Contributor Covenant 行为准则](CODE_OF_CONDUCT.md)。参与本项目即表示您同意遵守该准则。

## 如何贡献

### 1. 报告 Bug

如果您发现了 Bug，请创建一个 Issue 并包含以下信息：

- 清晰的标题和描述
- 复现步骤
- 预期行为和实际行为
- 环境信息（操作系统、Kotlin 版本等）
- 如果可能，提供最小复现代码

### 2. 提交代码

#### 简单修复

对于简单的修复（如拼写错误、文档更新），可以直接：

1. Fork 本仓库
2. 创建新分支
3. 提交更改
4. 创建 Pull Request

#### 新功能或重大改动

对于新功能或重大改动，请：

1. 先查看现有的 Issue 或创建新 Issue 讨论
2. 等待确认后再开始开发
3. 按照流程提交代码

### 3. 审查代码

欢迎帮助审查 Pull Request！您的反馈对项目很有价值。

## 开发环境设置

### 1. 安装依赖

- JDK 17 或更高版本
- Gradle 8.0+（项目已包含 wrapper）
- Kotlin 2.3.0+

### 2. 克隆项目

```bash
git clone https://github.com/RegadPoleCN/fursuit-tv-sdk.git
cd fursuit-tv-sdk
```

### 3. 构建项目

```bash
# 完整构建
./gradlew build

# 快速构建（仅 JVM 和 JS）
./gradlew quickBuild

# 运行测试
./gradlew allTests

# 运行代码质量检查
./gradlew checkAll
```

## 提交流程

### 1. 创建分支

```bash
# 功能分支
git checkout -b feature/your-feature-name

# 修复分支
git checkout -b fix/issue-123
```

### 2. 提交规范

遵循 [Conventional Commits](https://www.conventionalcommits.org/) 规范：

```
feat: 添加新功能
fix: 修复 Bug
docs: 更新文档
style: 代码格式调整
refactor: 代码重构
test: 添加测试
chore: 构建/工具相关
```

示例：

```bash
git commit -m "feat: 添加用户资料缓存功能"
git commit -m "fix: 修复 OAuth 回调处理问题"
```

### 3. 推送并创建 PR

```bash
git push origin feature/your-feature-name
```

然后在 GitHub 上创建 Pull Request。

### 4. PR 要求

- 标题清晰描述更改
- 描述中说明更改内容和原因
- 关联相关 Issue
- 确保所有检查通过
- 代码经过测试

## 代码风格

### Kotlin 代码

遵循 [Kotlin 编码规范](https://kotlinlang.org/docs/coding-conventions.html)：

```kotlin
// ✅ 推荐
class UserService(private val repository: UserRepository) {
    fun getUserProfile(username: String): UserProfile {
        return repository.findByUsername(username)
    }
}

// ❌ 不推荐
class user_service(var repo: UserRepository) {
    fun getuserprofile(username: String): UserProfile {
        return repo.findByUsername(username)
    }
}
```

项目已配置 ktlint 自动检查代码风格。

### 命名规范

- 类名：PascalCase（如 `UserService`）
- 函数和变量：camelCase（如 `getUserProfile`）
- 常量：UPPER_SNAKE_CASE（如 `MAX_RETRIES`）
- 包名：小写（如 `com.furrist.rp.furtv.sdk`）

### 文档注释

公共 API 必须有 KDoc 注释：

```kotlin
/**
 * 获取用户资料
 *
 * @param username 用户名
 * @return 用户资料
 * @throws NotFoundException 用户不存在时抛出
 */
suspend fun getUserProfile(username: String): UserProfile
```

## 测试

### 运行测试

```bash
# 运行所有测试
./gradlew allTests

# 运行 JVM 测试
./gradlew jvmTest

# 运行 JS 测试
./gradlew jsTest

# 运行特定测试
./gradlew test --tests "com.example.MyTest"
```

### 编写测试

```kotlin
class UserServiceTest {
    @Test
    fun testGetUserProfile() = runTest {
        // 准备测试数据
        val username = "test_user"
        
        // 执行测试
        val profile = userService.getUserProfile(username)
        
        // 验证结果
        assertNotNull(profile)
        assertEquals(username, profile.username)
    }
}
```

### 测试覆盖率

```bash
# 生成覆盖率报告
./gradlew koverHtmlReport

# 查看覆盖率
open build/reports/kover/coverage/html/index.html
```

## 问题报告

### Bug 报告模板

```markdown
### 描述
简要描述 Bug

### 复现步骤
1. 第一步
2. 第二步
3. ...

### 预期行为
应该发生什么

### 实际行为
实际发生了什么

### 环境信息
- OS: [e.g. Windows 11]
- Kotlin: [e.g. 2.3.0]
- SDK Version: [e.g. 1.0.0]

### 日志/截图
如有，请提供相关日志或截图
```

## 功能建议

### 功能建议模板

```markdown
### 功能描述
简要描述建议的功能

### 使用场景
这个功能会在什么场景下使用

### 实现建议
如果有，提供实现思路

### 替代方案
是否考虑过其他解决方案
```

## 代码审查清单

提交前请检查：

- [ ] 代码通过编译
- [ ] 所有测试通过
- [ ] 代码通过 ktlint 检查
- [ ] 代码通过 detekt 检查
- [ ] 添加了必要的测试
- [ ] 更新了文档
- [ ] 提交了 CHANGELOG 更新（如适用）

## 许可证

通过贡献代码，您同意您的贡献遵循本项目的 MIT 许可证。

## 需要帮助？

如有任何问题，请：

1. 查看 [文档](docs/)
2. 搜索现有 Issue
3. 创建新 Issue
4. 联系维护者

感谢您的贡献！🎉
