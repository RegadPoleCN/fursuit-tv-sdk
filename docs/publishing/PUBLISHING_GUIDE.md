# Maven Central 发布操作指南

本指南将帮助您将 fursuit-tv-sdk 发布到 Maven Central。请按照以下步骤逐步操作。

## 目录

- [第一章：准备工作](#第一章准备工作)
- [第二章：GPG 密钥配置](#第二章 gpg-密钥配置)
- [第三章：Sonatype API Token 获取](#第三章 sonatype-api-token-获取)
- [第四章：本地环境配置](#第四章本地环境配置)
- [第五章：测试发布流程](#第五章测试发布流程)
- [第六章：正式发布流程](#第六章正式发布流程)
- [第七章：自动化发布配置](#第七章自动化发布配置)
- [第八章：常见问题排查](#第八章常见问题排查)
- [第九章：发布清单](#第九章发布清单)
- [第十章：安全最佳实践](#第十章安全最佳实践)

---

## 第一章准备工作

### 1.1 注册 Sonatype Central Portal 账户

1. 访问 [https://central.sonatype.com/](https://central.sonatype.com/)
2. 点击页面右上角的 **"Sign Up"** 按钮
3. 建议使用 **GitHub 账号登录**（推荐）或使用邮箱注册
4. 验证邮箱地址（如使用邮箱注册）
5. 完成账户设置

![截图位置：Sonatype Central Portal 登录页面]

### 1.2 创建 Namespace

Namespace 是您在 Maven Central 上的唯一标识，格式通常为 `me.regadpole` 或 `com.github.username`。

1. 登录 [https://central.sonatype.com/](https://central.sonatype.com/)
2. 点击左侧导航栏的 **"Namespaces"**
3. 点击 **"Add Namespace"** 按钮
4. 输入 Namespace：`me.regadpole`
5. 验证所有权（二选一）：
   
   **方式一：GitHub 验证（推荐）**
   - 选择 "GitHub" 选项
   - 系统会显示一个验证文件或 TXT 记录值
   - 在您的 GitHub 仓库 `https://github.com/RegadPoleCN/fursuit-tv-sdk` 中添加验证文件
   - 或在 DNS 中添加 TXT 记录（如果您有自己的域名）
   
   **方式二：域名验证**
   - 选择 "Domain" 选项
   - 在您的域名 DNS 中添加 TXT 记录
   - 等待 DNS 生效（通常几分钟到几小时）

6. 点击 **"Verify"** 验证
7. 等待验证通过（通常几分钟到几小时）

![截图位置：Namespace 创建页面]

---

## 第二章 GPG 密钥配置

GPG（GNU Privacy Guard）用于对发布的构件进行数字签名，确保其真实性和完整性。

### 2.1 安装 GPG（Windows）

**方式一：使用 Gpg4win（推荐）**

1. 访问 [https://gpg4win.org/](https://gpg4win.org/)
2. 下载最新版本的 Gpg4win 安装包
3. 运行安装程序，按照向导完成安装
4. 安装完成后，重启终端

**方式二：使用 Chocolatey 包管理器**

```bash
choco install gpg4win
```

### 2.2 生成 GPG 密钥对

1. 打开终端（PowerShell 或 CMD）

2. 运行以下命令生成密钥：
   ```bash
   gpg --full-generate-key
   ```

3. 按照提示操作：
   ```
   Please select what kind of key you want:
   (1) RSA and RSA (default)
   (2) DSA and Elgamal
   (3) DSA (sign only)
   (4) RSA (sign only)
   (9) ECC (sign and encrypt) *default*
   (10) ECC (sign only)
   (14) Existing key from card
   Your selection? 1
   
   What keysize do you want? (2048) 4096
   Requested keysize is 4096 bits
   Please specify how long the key should be valid.
   Key is valid for? (0) 1y
   Is this correct? (y/N) y
   ```

4. 输入用户信息：
   ```
   Real name: Your Name
   Email address: your-email@example.com
   Comment: fursuit-tv-sdk publishing key
   ```

5. 设置密码（Passphrase）：
   - 输入一个强密码（至少 12 位，包含大小写字母、数字和特殊字符）
   - **重要**：将此密码保存在安全的地方，后续发布时需要使用

### 2.3 查看生成的密钥

```bash
gpg --list-secret-keys --keyid-format LONG
```

输出示例：
```
sec   rsa4096/ABC123DEF456 2024-01-01 [SC] [expires: 2025-01-01]
      1234567890ABCDEF1234567890ABCDEF12345678
uid                 [ultimate] Your Name <your-email@example.com>
ssb   rsa4096/XYZ789GHI012 2024-01-01 [E] [expires: 2025-01-01]
```

**记录以下信息：**
- 密钥 ID：`ABC123DEF456`（最后 8 位）
- 密钥指纹：`1234567890ABCDEF1234567890ABCDEF12345678`（完整 40 位）

### 2.4 导出公钥

```bash
gpg --armor --export ABC123DEF456 > public-key.asc
```

### 2.5 导出私钥（用于配置）

```bash
gpg --armor --export-secret-keys ABC123DEF456 > private-key.asc
```

**重要**：私钥文件 `private-key.asc` 必须妥善保管，不要提交到版本控制！

### 2.6 （推荐）创建子密钥用于 CI/CD

为了提高安全性，建议创建子密钥用于自动化发布：

```bash
gpg --edit-key ABC123DEF456
```

在 GPG 交互界面中：
```
gpg> addkey
```

选择：
- 密钥类型：RSA (sign only)
- 密钥长度：4096
- 有效期：1y

保存后，只导出子密钥用于 GitHub Actions。

### 2.7 上传公钥到密钥服务器

```bash
# 上传到 keyserver.ubuntu.com
gpg --keyserver keyserver.ubuntu.com --send-keys ABC123DEF456

# 上传到 pgp.mit.edu（推荐）
gpg --keyserver pgp.mit.edu --send-keys ABC123DEF456
```

验证上传成功：
```bash
gpg --keyserver keyserver.ubuntu.com --search-keys your-email@example.com
```

---

## 第三章 Sonatype API Token 获取

### 3.1 生成 API Token

1. 登录 [https://central.sonatype.com/](https://central.sonatype.com/)
2. 点击右上角头像 → **"Account Settings"**
3. 滚动到 **"API Tokens"** 部分
4. 点击 **"Generate Token"** 按钮
5. 选择权限范围：
   - **本地开发**：选择 "Deploy" 权限
   - **CI/CD**：选择 "Deploy" 权限，可限制到特定 Namespace（推荐）
6. 输入 Token 描述（例如："fursuit-tv-sdk publishing"）
7. 点击 **"Generate Token"**

### 3.2 保存 Token

系统会显示：
- **Username**：格式为 `username-abc123`
- **Token**：一长串随机字符

**重要**：
- 立即复制并保存到安全的地方
- 关闭页面后无法再次查看 Token
- 如果 Token 泄露，需要立即撤销并重新生成

![截图位置：API Token 生成页面]

---

## 第四章 本地环境配置

### 4.1 配置 Gradle 属性

1. 在项目根目录复制模板文件：
   ```bash
   copy gradle.properties.example gradle.properties
   ```

2. 编辑 `gradle.properties` 文件，填入真实值：

```properties
# ==================== Sonatype Central Portal 凭证 ====================
sonatypeUsername=your-username-token  # 从 Sonatype 获取的用户名
sonatypePassword=your-api-token       # 从 Sonatype 获取的 Token

# ==================== GPG 签名配置 ====================
signing.keyId=ABC123DEF456           # GPG 密钥 ID（最后 8 位）
signing.password=your-gpg-passphrase # GPG 密钥密码

# ==================== GPG 私钥（可选，用于 CI/CD） ====================
# 导出命令：gpg --armor --export-secret-keys ABC123DEF456
# 将输出内容复制到这里，换行符替换为 \n
# signing.inMemoryKey=-----BEGIN PGP PRIVATE KEY BLOCK-----\n...\n-----END PGP PRIVATE KEY BLOCK-----
```

### 4.2 验证配置

运行以下命令验证配置：
```bash
./gradlew properties | grep -E "(sonatype|signing)"
```

### 4.3 配置环境变量（可选）

您也可以使用环境变量代替 `gradle.properties`：

```bash
# Windows PowerShell
$env:ORG_GRADLE_PROJECT_sonatypeUsername="your-username-token"
$env:ORG_GRADLE_PROJECT_sonatypePassword="your-api-token"
$env:ORG_GRADLE_PROJECT_signingKeyId="ABC123DEF456"
$env:ORG_GRADLE_PROJECT_signingPassword="your-gpg-passphrase"
```

---

## 第五章 测试发布流程

### 5.1 配置 SNAPSHOT 版本

1. 编辑 `gradle/libs.versions.toml` 文件：
   ```toml
   [versions]
   fursuit-tv-sdk = "0.1.0-SNAPSHOT"
   ```

2. 验证版本号格式：
   ```bash
   ./gradlew properties | grep "^version"
   ```

### 5.2 构建项目

```bash
# 清理并构建
./gradlew clean build

# 运行所有代码质量检查
./gradlew checkAll
```

### 5.3 发布到本地 Maven 仓库

```bash
./gradlew publishToMavenLocal
```

验证发布结果：
```bash
ls ~/.m2/repository/me/regadpole/fursuit-tv-sdk/
```

应该包含以下文件：
- `fursuit-tv-sdk-0.1.0-SNAPSHOT.jar`
- `fursuit-tv-sdk-0.1.0-SNAPSHOT-sources.jar`
- `fursuit-tv-sdk-0.1.0-SNAPSHOT-javadoc.jar`
- `fursuit-tv-sdk-0.1.0-SNAPSHOT.pom`
- `fursuit-tv-sdk-0.1.0-SNAPSHOT.module`
- `*.asc`（签名文件）

### 5.4 发布到 Sonatype Staging

```bash
./gradlew publishAllPublicationsToSonatypeRepository
```

### 5.5 验证发布

1. 登录 [https://central.sonatype.com/](https://central.sonatype.com/)
2. 点击 **"Components"**
3. 搜索 `me.regadpole:fursuit-tv-sdk`
4. 检查：
   - 构件信息是否正确
   - 签名文件是否存在
   - POM 元数据是否完整

---

## 第六章 正式发布流程

### 6.1 准备发布版本

1. 更新版本号（移除 `-SNAPSHOT`）：
   
   编辑 `gradle/libs.versions.toml`：
   ```toml
   [versions]
   fursuit-tv-sdk = "0.1.0"
   ```

2. 更新 `CHANGELOG.md`：
   ```markdown
   ## [0.1.0] - 2024-01-15
   
   ### Added
   - 初始版本发布
   - 支持 Kotlin Multiplatform
   - 完整的 Fursuit.TV API 实现
   
   ### Changed
   - 无
   
   ### Fixed
   - 无
   ```

3. 更新 `README.md`（如需要）：
   - 更新版本号
   - 更新安装说明

### 6.2 提交并创建 Git Tag

```bash
# 提交更改
git add gradle/libs.versions.toml CHANGELOG.md README.md
git commit -m "Release version 0.1.0"

# 创建 Tag
git tag -a v0.1.0 -m "Version 0.1.0"

# 推送 Tag
git push origin v0.1.0
```

### 6.3 执行正式发布

```bash
# 清理并构建
./gradlew clean build

# 发布到 Sonatype Central Portal
./gradlew publishAllPublicationsToSonatypeRepository
```

### 6.4 在 Central Portal 发布

1. 登录 [https://central.sonatype.com/](https://central.sonatype.com/)
2. 点击 **"Components"**
3. 找到刚发布的构件（状态为 "Pending"）
4. 点击构件名称查看详情
5. 确认信息无误后，点击 **"Publish"** 按钮
6. 确认发布信息
7. 等待发布完成（通常 10-30 分钟）

![截图位置：Central Portal 发布页面]

### 6.5 验证 Maven Central

1. 等待 10-30 分钟（同步到 Maven Central）
2. 访问 [https://search.maven.org/](https://search.maven.org/)
3. 搜索 `me.regadpole:fursuit-tv-sdk`
4. 确认：
   - 版本号正确
   - 所有平台的构件都存在
   - POM 文件正确显示
   - 签名文件存在

### 6.6 测试依赖

在新项目中测试使用发布的 SDK：

**Kotlin (Gradle Kotlin DSL):**
```kotlin
dependencies {
    implementation("me.regadpole:fursuit-tv-sdk:0.1.0")
}
```

**Kotlin (Gradle Groovy):**
```groovy
dependencies {
    implementation 'me.regadpole:fursuit-tv-sdk:0.1.0'
}
```

**Java (Maven):**
```xml
<dependency>
    <groupId>me.regadpole</groupId>
    <artifactId>fursuit-tv-sdk</artifactId>
    <version>0.1.0</version>
</dependency>
```

---

## 第七章 自动化发布配置

### 7.1 配置 GitHub Secrets

1. 进入 GitHub 仓库页面
2. 点击 **"Settings"** → **"Secrets and variables"** → **"Actions"**
3. 点击 **"New repository secret"**
4. 添加以下 Secrets：

```
Name: ORG_GRADLE_PROJECT_sonatypeUsername
Value: your-username-token
```

```
Name: ORG_GRADLE_PROJECT_sonatypePassword
Value: your-api-token
```

```
Name: ORG_GRADLE_PROJECT_signingKeyId
Value: ABC123DEF456
```

```
Name: ORG_GRADLE_PROJECT_signingPassword
Value: your-gpg-passphrase
```

```
Name: ORG_GRADLE_PROJECT_signingInMemoryKey
Value: -----BEGIN PGP PRIVATE KEY BLOCK-----
      (粘贴私钥内容，换行符替换为 \n)
      -----END PGP PRIVATE KEY BLOCK-----
```

![截图位置：GitHub Secrets 配置页面]

### 7.2 配置 GitHub Actions 工作流

编辑 `.github/workflows/publish.yml`（如已存在则更新）：

```yaml
name: Publish to Maven Central

on:
  push:
    tags:
      - 'v*'  # 以 v 开头的 Tag 触发发布

jobs:
  publish:
    runs-on: ubuntu-latest
    
    steps:
      - name: Checkout code
        uses: actions/checkout@v4
      
      - name: Setup JDK
        uses: actions/setup-java@v4
        with:
          java-version: '17'
          distribution: 'temurin'
      
      - name: Publish to Maven Central
        run: ./gradlew publishAllPublicationsToSonatypeRepository
        env:
          ORG_GRADLE_PROJECT_sonatypeUsername: ${{ secrets.ORG_GRADLE_PROJECT_sonatypeUsername }}
          ORG_GRADLE_PROJECT_sonatypePassword: ${{ secrets.ORG_GRADLE_PROJECT_sonatypePassword }}
          ORG_GRADLE_PROJECT_signingKeyId: ${{ secrets.ORG_GRADLE_PROJECT_signingKeyId }}
          ORG_GRADLE_PROJECT_signingPassword: ${{ secrets.ORG_GRADLE_PROJECT_signingPassword }}
          ORG_GRADLE_PROJECT_signingInMemoryKey: ${{ secrets.ORG_GRADLE_PROJECT_signingInMemoryKey }}
```

### 7.3 触发自动发布

```bash
# 创建并推送 Tag
git tag -a v0.1.0 -m "Version 0.1.0"
git push origin v0.1.0
```

GitHub Actions 将自动：
1. 检出代码
2. 设置 JDK 环境
3. 执行发布任务
4. 发布到 Sonatype Central Portal

---

## 第八章 常见问题排查

### Q1: GPG 签名失败

**错误信息：**
```
Execution failed for task ':signKotlinMultiplatformPublication'.
> GPG signing failed
```

**解决方案：**

1. 检查 GPG 密钥 ID 是否正确（最后 8 位）
   ```bash
   gpg --list-secret-keys --keyid-format LONG
   ```

2. 确认 GPG 密码正确
   - 尝试手动签名测试：`echo "test" | gpg --sign`

3. Windows 用户确保 GPG 已添加到 PATH
   ```bash
   gpg --version
   ```

4. 检查 `signing.keyId` 和 `signing.password` 配置

### Q2: Sonatype 认证失败

**错误信息：**
```
HTTP 401 Unauthorized
```

**解决方案：**

1. 检查 `sonatypeUsername` 和 `sonatypePassword` 是否正确
   - 确认使用的是 API Token 而非账户密码
   - Username 格式：`username-abc123`

2. 验证 Token 未过期且有 Deploy 权限
   - 登录 Central Portal → Account Settings → API Tokens

3. 检查 Token 权限范围是否包含目标 Namespace

### Q3: Namespace 验证失败

**错误信息：**
```
Namespace verification failed
```

**解决方案：**

1. 确认 Namespace 已通过验证
   - 登录 Central Portal → Namespaces

2. 检查 GroupId 与 Namespace 匹配
   - GroupId: `me.regadpole`
   - Namespace: `me.regadpole`

3. 等待 DNS 验证生效（可能需要几小时）

### Q4: 发布后 Maven Central 搜索不到

**解决方案：**

1. 正式发布后需要 10 分钟到数小时同步到 search.maven.org
   - 耐心等待同步完成

2. SNAPSHOT 版本不会出现在搜索中
   - 只有正式版本（如 0.1.0）才会显示

3. 直接在 Sonatype Central Portal 验证
   - [https://central.sonatype.com/](https://central.sonatype.com/)

### Q5: Windows 上 Native 编译失败

**错误信息：**
```
Kotlin/Native compilation failed on Windows
```

**解决方案：**

1. 在 `gradle.properties` 添加：
   ```properties
   kotlin.native.disableCompilerDaemon=true
   ```

2. 或设置跳过 Native 平台：
   ```properties
   skipNative=true
   ```

3. 使用管理员权限运行终端

### Q6: 依赖版本冲突

**错误信息：**
```
Conflict found for module: kotlinx.coroutines
```

**解决方案：**

1. 检查项目依赖树：
   ```bash
   ./gradlew dependencies
   ```

2. 统一依赖版本：
   - 使用 `libs.versions.toml` 管理版本
   - 避免硬编码版本号

### Q7: POM 文件缺少信息

**解决方案：**

1. 检查 `build.gradle.kts` 中的 `mavenPublishing` 配置
   - 确保所有 POM 字段都已设置

2. 验证发布的 POM 文件：
   ```bash
   cat build/publications/mavenJava/pom-default.xml
   ```

---

## 第九章 发布清单

在每次发布前，请检查以下项目：

### 代码质量检查
- [ ] 代码已通过所有测试（`./gradlew check`）
- [ ] 代码风格检查通过（`./gradlew ktlintCheck`）
- [ ] API 兼容性检查通过（`./gradlew apiCheck`）
- [ ] Detekt 静态分析通过（`./gradlew detekt`）

### 版本管理
- [ ] 版本号已更新（移除 `-SNAPSHOT`）
- [ ] CHANGELOG.md 已更新
- [ ] README.md 已更新（如需要）
- [ ] Git Tag 已创建并推送

### 配置验证
- [ ] GPG 密钥配置正确
- [ ] Sonatype 凭证配置正确
- [ ] `gradle.properties` 文件存在
- [ ] `.gitignore` 包含敏感文件

### 发布验证
- [ ] 本地测试发布成功（`./gradlew publishToMavenLocal`）
- [ ] Sonatype Staging 发布成功
- [ ] 所有 Kotlin Multiplatform 平台都包含
- [ ] POM 元数据完整

### 发布后验证
- [ ] Central Portal 显示构件
- [ ] Maven Central 可搜索到（正式版本）
- [ ] 签名文件存在
- [ ] 在新项目中测试依赖解析

---

## 第十章 安全最佳实践

### 1. GPG 密钥安全

**推荐做法：**
- ✅ 使用子密钥进行自动化发布
- ✅ 设置密钥有效期（1 年）并定期轮换
- ✅ 主密钥离线存储（硬件钱包或加密 USB）
- ✅ 备份密钥到安全位置
- ✅ 使用强密码保护密钥

**禁止做法：**
- ❌ 不要将私钥提交到版本控制
- ❌ 不要在公共场合分享私钥密码
- ❌ 不要使用过期密钥
- ❌ 不要在日志中输出密钥信息

### 2. Token 管理

**推荐做法：**
- ✅ 为不同环境创建不同 Token（本地、CI/CD）
- ✅ 设置 Token 权限范围（最小权限原则）
- ✅ 定期轮换 Token（每 90 天）
- ✅ 使用 GitHub Secrets 存储 Token
- ✅ 为 Token 添加描述便于管理

**禁止做法：**
- ❌ 不要在代码中硬编码 Token
- ❌ 不要在日志中输出 Token
- ❌ 不要共享 Token
- ❌ 不要使用过期的 Token

### 3. 发布验证

**推荐做法：**
- ✅ 发布前在本地完整测试
- ✅ 使用 SNAPSHOT 版本验证流程
- ✅ 检查发布构件的完整性
- ✅ 验证 GPG 签名
- ✅ 确认 POM 元数据正确
- ✅ 在所有支持的平台测试

**禁止做法：**
- ❌ 不要跳过测试直接发布
- ❌ 不要发布未经验证的版本
- ❌ 不要发布包含敏感信息的版本

### 4. 版本控制安全

**推荐做法：**
- ✅ 使用 `.gitignore` 排除敏感文件
- ✅ 使用配置模板文件（`.example` 后缀）
- ✅ 定期审查 Git 历史中的敏感信息
- ✅ 使用 Git hooks 防止敏感信息提交

**禁止做法：**
- ❌ 不要提交 `gradle.properties`（包含真实凭证）
- ❌ 不要提交 `.asc`、`.key` 等密钥文件
- ❌ 不要提交包含 Token 的配置文件

---

## 附录

### A. 有用的命令

```bash
# 查看 Gradle 属性
./gradlew properties | grep -E "(version|group)"

# 查看可发布的出版物
./gradlew tasks --all | grep publish

# 发布到本地
./gradlew publishToMavenLocal

# 发布到 Sonatype
./gradlew publishAllPublicationsToSonatypeRepository

# 查看依赖树
./gradlew dependencies

# 清理构建
./gradlew clean

# 运行所有检查
./gradlew checkAll
```

### B. 相关资源

- [Sonatype Central Portal](https://central.sonatype.com/)
- [Maven Central 搜索](https://search.maven.org/)
- [vanniktech/gradle-maven-publish-plugin](https://github.com/vanniktech/gradle-maven-publish-plugin)
- [GPG 官方文档](https://gnupg.org/)
- [Kotlin Multiplatform 发布指南](https://kotlinlang.org/docs/multiplatform/multiplatform-publish-libraries.html)

### C. 联系支持

如遇到问题，请：
1. 首先查阅本指南的 [常见问题排查](#第八章常见问题排查) 章节
2. 检查 [Sonatype 官方文档](https://central.sonatype.org/publish/publish-guide/)
3. 在 GitHub Issues 中提问

---

**文档版本**: 1.0  
**最后更新**: 2024-01-15  
**维护者**: RegadPole
