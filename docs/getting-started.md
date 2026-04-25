# 快速开始

本指南将帮助你在 5 分钟内完成 Fursuit.TV SDK 的集成和第一个 API 调用。

## 1. 安装依赖

### Gradle (Kotlin DSL)

```kotlin
dependencies {
    implementation("com.furrist.rp:fursuit-tv-sdk:{version}")
}
```

### Gradle (Groovy)

```groovy
dependencies {
    implementation 'com.furrist.rp:fursuit-tv-sdk:{version}'
}
```

### Maven

```xml
<dependency>
    <groupId>com.furrist.rp</groupId>
    <artifactId>fursuit-tv-sdk</artifactId>
    <version>{version}</version>
</dependency>
```

## 2. 初始化 SDK

### 方式 1：签名交换（推荐）

适用于服务端应用，SDK 自动管理令牌刷新。

```kotlin
val sdk = fursuitTvSdk {
    clientId = "vap_xxxxxxxxxxxxxxxx"
    clientSecret = "your-app-secret"
}
```

### 方式 2：使用已有 apiKey

适用于已有 VDS 颁发的 API 密钥。

```kotlin
val sdk = fursuitTvSdk {
    apiKey = "your-api-key"
}
```

### 方式 3：使用 DSL 灵活配置

```kotlin
val sdk = fursuitTvSdk {
    clientId = "vap_xxx"
    clientSecret = "your-secret"
    baseUrl = "https://open-global.vdsentnet.com"
    enableRetry = true
    maxRetries = 3
}
```

## 3. 调用第一个 API

### 获取用户资料

```kotlin
val profile = sdk.user.getUserProfile("username")
println("昵称：${profile.displayName}")
println("物种：${profile.species}")
```

### 获取热门推荐

```kotlin
val popular = sdk.search.getPopular()
println("热门用户数：${popular.users.size}")
```

### 健康检查

```kotlin
val health = sdk.base.health()
println("服务状态：${health.status}")
```

## 4. 完整示例

```kotlin
import com.furrist.rp.furtv.sdk.fursuitTvSdk
import com.furrist.rp.furtv.sdk.exception.*

fun main() {
    // 初始化 SDK
    val sdk = fursuitTvSdk {
        clientId = "vap_xxx"
        clientSecret = "your-secret"
    }

    try {
        // 调用 API
        val profile = sdk.user.getUserProfile("username")
        println("用户：${profile.displayName}")
    } catch (e: NotFoundException) {
        println("用户不存在")
    } catch (e: ApiException) {
        println("API 错误：${e.message}")
    } finally {
        sdk.close()
    }
}
```

## 5. 下一步

- [认证详解](authentication.md) - 了解签名交换和 OAuth 的区别
- [配置选项](configuration.md) - 查看所有可用的配置参数
- [API 参考](api/README.md) - 完整的 API 文档
- [错误处理](error-handling.md) - 学习如何处理异常情况

## 常见问题

**Q: 在哪里获取 clientId 和 clientSecret？**

A: 在 VDS 开放平台注册应用后获得。

**Q: apiKey 和 accessToken 有什么区别？**

A: `apiKey` 用于签名交换认证，`accessToken` 用于 OAuth 认证。详见 [认证详解](authentication.md)。

**Q: SDK 支持哪些平台？**

A: JVM、JavaScript 和 Kotlin Native（iOS/macOS/Linux/Windows）。详见 [平台指南](platform-guide.md)。

---

**提示**: 生产环境请妥善保管 `clientSecret`，不要提交到版本控制系统。
