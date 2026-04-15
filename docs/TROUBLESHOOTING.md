# Fursuit.TV SDK 故障排除指南

本文档列出使用 SDK 时可能遇到的常见问题及解决方案。

## 🔍 目录

1. [构建问题](#构建问题)
2. [运行时错误](#运行时错误)
3. [OAuth 相关问题](#oauth-相关问题)
4. [平台特定问题](#平台特定问题)
5. [常见问题 FAQ](#常见问题-faq)

## 构建问题

### 问题 1: Gradle 需要 JDK 17

**错误信息**:
```
FAILURE: Build failed with an exception.
Gradle requires JVM 17 or later to run.
```

**解决方案**:
1. 安装 JDK 17 或更高版本
2. 设置 JAVA_HOME 环境变量
3. 或在 `gradle.properties` 中配置：
   ```properties
   org.gradle.java.home=/path/to/jdk-17
   ```

### 问题 2: 依赖解析失败

**错误信息**:
```
Could not resolve com.furrist.rp:fursuit-tv-sdk:0.1.0
```

**解决方案**:
1. 确保添加了 Maven Central 仓库
2. 检查网络连接
3. 清理 Gradle 缓存：
   ```bash
   ./gradlew clean --refresh-dependencies
   ```

### 问题 3: Kotlin 版本不兼容

**错误信息**:
```
This version of Gradle expects Kotlin compiler version X.X.X
```

**解决方案**:
1. 更新 Gradle 到最新版本
2. 或在 `libs.versions.toml` 中指定正确的 Kotlin 版本

## 运行时错误

### 问题 1: 认证失败 (401)

**错误信息**:
```
AuthenticationException: Invalid credentials
```

**可能原因**:
- appId 或 appSecret 错误
- apiKey 无效或已过期
- OAuth 令牌已失效

**解决方案**:
```kotlin
// 1. 检查凭证是否正确
val sdk = FursuitTvSdk(
    appId = "vap_xxxxxxxxxxxxxxxx",  // 确保格式正确
    appSecret = "your-correct-secret"
)

// 2. 重新获取令牌
runBlocking {
    try {
        sdk.auth.exchangeToken(appId, appSecret)
    } catch (e: AuthenticationException) {
        println("认证失败：${e.message}")
        // 检查 appId 和 appSecret
    }
}

// 3. 使用自动刷新
val token = sdk.auth.getValidAccessToken(appId, appSecret)
```

### 问题 2: 令牌过期

**错误信息**:
```
TokenExpiredException: Access token has expired
```

**解决方案**:
```kotlin
// SDK 已实现自动刷新，确保使用 getValidAccessToken
runBlocking {
    val token = sdk.auth.getValidAccessToken(appId, appSecret)
    // 令牌已自动刷新
}

// 或手动刷新
runBlocking {
    try {
        sdk.auth.refreshToken()
    } catch (e: TokenExpiredException) {
        // 刷新失败，重新获取
        sdk.auth.exchangeToken(appId, appSecret)
    }
}
```

### 问题 3: 资源不存在 (404)

**错误信息**:
```
NotFoundException: User not found
```

**解决方案**:
```kotlin
try {
    val profile = sdk.user.getUserProfile("username")
} catch (e: NotFoundException) {
    println("用户不存在：${e.message}")
    // 检查用户名是否正确
}
```

### 问题 4: 网络错误

**错误信息**:
```
NetworkException: Connection timeout
```

**解决方案**:
```kotlin
// 1. 增加超时时间
val config = SdkConfig.builder()
    .requestTimeout(60000)  // 60 秒
    .connectTimeout(15000)  // 15 秒
    .socketTimeout(45000)   // 45 秒
    .build()

// 2. 启用重试
val config = SdkConfig.builder()
    .enableRetry(true)
    .maxRetries(3)
    .retryInterval(2000)
    .build()

// 3. 检查网络连接
// 确保设备已连接互联网
```

### 问题 5: 请求失败 (500)

**错误信息**:
```
ApiException: Internal server error (500)
```

**解决方案**:
1. 稍后重试（可能是服务端临时问题）
2. 检查请求参数是否正确
3. 查看 requestId 并联系支持：
   ```kotlin
   try {
       // API call
   } catch (e: ApiException) {
       println("错误：${e.statusCode}")
       println("Request ID: ${e.requestId}")  // 用于排查问题
   }
   ```

## OAuth 相关问题

### 问题 1: OAuth 回调失败

**错误信息**:
```
OAuthCallbackException: Callback timeout
```

**可能原因**:
- 回调端口被占用
- 用户未完成授权
- 防火墙阻止本地服务器

**解决方案**:
```kotlin
// 1. 更换端口
val config = OAuthConfig(
    callbackHost = "localhost",
    callbackPort = 9000,  // 使用其他端口
    callbackPath = "/callback"
)

// 2. 增加超时时间
val config = OAuthConfig(
    stateTimeoutMinutes = 15  // 15 分钟
)

// 3. 手动打开授权 URL
val authorizeUrl = sdk.auth.getOAuthAuthorizeUrl(appId, redirectUri)
println("请在浏览器打开：$authorizeUrl")
```

### 问题 2: State 验证失败

**错误信息**:
```
OAuthCallbackException: State mismatch
```

**可能原因**:
- CSRF 攻击（罕见）
- 回调被篡改

**解决方案**:
```kotlin
// 确保使用 SDK 自动生成的 state
val tokenInfo = sdk.auth.initWithOAuth(appId, config)
// SDK 会自动生成和验证 state
```

### 问题 3: OAuth 不支持浏览器环境

**问题描述**: JS 平台在浏览器中无法使用 OAuth

**解决方案**:
1. 使用 Node.js 环境运行 OAuth 流程
2. 或使用后端服务代理 OAuth 流程
3. 或使用签名认证方式（appId + appSecret）

## 平台特定问题

### JVM 平台

#### 问题：无法打开浏览器

**解决方案**:
```kotlin
// 手动打开授权 URL
val authorizeUrl = sdk.auth.getOAuthAuthorizeUrl(appId, redirectUri)
println("请手动打开：$authorizeUrl")

// 或使用 Desktop API
java.awt.Desktop.getDesktop().browse(URI(authorizeUrl))
```

### JS 平台

#### 问题：Node.js 环境无法运行

**解决方案**:
1. 确保使用 Node.js（不是浏览器）
2. 安装 Node.js 16+
3. 使用签名认证代替 OAuth

#### 问题：CORS 错误

**解决方案**:
- 使用后端服务代理 API 请求
- 或配置服务器允许 CORS

### Native 平台 (iOS/macOS)

#### 问题：无法打开授权页面

**解决方案**:
```swift
// iOS: 使用 SFSafariViewController
import SafariServices

let url = URL(string: authorizeUrl)!
let safari = SFSafariViewController(url: url)
present(safari, animated: true)
```

#### 问题：自定义 URL Scheme 不工作

**解决方案**:
1. 在 Info.plist 中配置 URL Types
2. 确保 Scheme 唯一
3. 测试回调是否正确处理

### Android 平台

#### 问题：网络权限错误

**解决方案**:
```xml
<!-- AndroidManifest.xml -->
<uses-permission android:name="android.permission.INTERNET" />
```

#### 问题：主线程异常

**解决方案**:
```kotlin
// ❌ 不推荐：在主线程调用
val profile = sdk.user.getUserProfile("username")

// ✅ 推荐：使用协程
lifecycleScope.launch {
    val profile = sdk.user.getUserProfile("username")
}
```

## 常见问题 FAQ

### Q1: 如何获取 appId 和 appSecret？

**A**: 在 Fursuit.TV 开发者平台注册应用后获得。

### Q2: apiKey 和 accessToken 有什么区别？

**A**: 
- apiKey：用于 `X-Api-Key` 认证头，长期有效
- accessToken：用于 `Authorization: Bearer` 认证头，有有效期
- 两者是签名交换接口返回的不同值

### Q3: 令牌多久过期？

**A**: 通常 1 小时过期，SDK 会在剩余 5 分钟时自动刷新。

### Q4: 如何持久化令牌？

**A**:
```kotlin
// 保存令牌
val tokenInfo = sdk.auth.exchangeToken(appId, appSecret)
saveToSecureStorage(tokenInfo)

// 恢复令牌
val savedToken = loadFromSecureStorage()
if (savedToken != null && !savedToken.isExpired()) {
    sdk.auth.setTokenInfo(savedToken)
}
```

### Q5: 如何在多平台共享代码？

**A**:
```kotlin
// commonMain
class UserRepository(private val sdk: FursuitTvSdk) {
    suspend fun getUser(username: String) = 
        sdk.user.getUserProfile(username)
}

// 各平台共用这个类
```

### Q6: 如何调试 API 请求？

**A**:
```kotlin
// 启用 DEBUG 日志
val config = SdkConfig.builder()
    .logLevel(LogLevel.DEBUG)
    .build()

// 查看详细请求和响应
```

### Q7: SDK 支持哪些平台？

**A**: 
- JVM（Java 8+）
- JS（Node.js 16+）
- iOS（X64, Arm64）
- macOS（X64, Arm64）
- Linux（X64, Arm64）
- Windows（X64）
- Android Native

### Q8: 如何更新 SDK？

**A**:
```kotlin
// 更新版本号
// libs.versions.toml
fursuit-tv-sdk = "1.1.0"  // 更新版本

// 重新构建
./gradlew clean build
```

### Q9: 遇到未列出的问题怎么办？

**A**:
1. 查看 [开发者指南](DEVELOPER_GUIDE.md)
2. 查看 [最佳实践](BEST_PRACTICES.md)
3. 检查 API 文档
4. 提交 Issue 或联系支持

## 调试技巧

### 1. 启用详细日志

```kotlin
val config = SdkConfig.builder()
    .logLevel(LogLevel.DEBUG)
    .build()
```

### 2. 捕获 requestId

```kotlin
try {
    // API call
} catch (e: ApiException) {
    println("Request ID: ${e.requestId}")
    // 提供 requestId 给技术支持
}
```

### 3. 使用网络抓包

```bash
# 使用 Wireshark 或 Charles 抓包分析
# 注意：仅用于开发调试，不要用于生产
```

### 4. 检查令牌状态

```kotlin
val isAuthenticated = sdk.auth.isAuthenticated()
val accessToken = sdk.auth.getAccessToken()
println("已认证：$isAuthenticated, 令牌：${accessToken?.take(20)}...")
```

## 获取帮助

如果问题仍未解决：

1. **查看文档**: 
   - [开发者指南](DEVELOPER_GUIDE.md)
   - [API 文档](api/)
   
2. **搜索 Issue**: 
   - GitHub Issues
   
3. **提交 Issue**:
   - 提供详细的错误信息
   - 包含 requestId
   - 说明复现步骤

4. **联系支持**:
   - 邮箱：support@example.com
   - 开发者论坛
