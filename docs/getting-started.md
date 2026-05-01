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

### npm (JavaScript/TypeScript)

```bash
npm install @regadpole/fursuit-tv-sdk
```

## 2. 初始化 SDK

### 方式 1：签名交换（推荐）

适用于服务端应用，SDK 自动管理令牌刷新。`fursuitTvSdk` 是 `suspend` 函数，提供 `clientId` + `clientSecret` 时自动完成令牌交换。

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

`fursuitTvSdk` 是 `suspend` 函数，提供 `clientId` + `clientSecret` 时自动完成令牌交换。

```kotlin
val sdk = fursuitTvSdk {
    clientId = "vap_xxx"
    clientSecret = "your-secret"
    baseUrl = "https://open-global.vdsentnet.com"
    enableRetry = true
    maxRetries = 3
}
```

### Java 用户

Java 用户无法直接使用 Kotlin DSL，可使用 `JvmFursuitTvSdkBuilder` 进行链式配置。SDK 通过 `kotlin-suspend-transform-compiler-plugin` 自动为 Java 用户生成了 `buildBlocking()` 和 `buildAsync()` 两种构建方式：

```java
import com.furrist.rp.furtv.sdk.factory.JvmFursuitTvSdkBuilder;
import com.furrist.rp.furtv.sdk.model.SdkLogLevel;

// 使用 apiKey 模式 — buildBlocking() 同步构建
FursuitTvSdk sdk = JvmFursuitTvSdkBuilder.create()
    .apiKey("your-api-key")
    .logLevel(SdkLogLevel.INFO)
    .buildBlocking();

// 使用签名交换模式 — buildBlocking() 同步构建
FursuitTvSdk sdk = JvmFursuitTvSdkBuilder.create()
    .clientId("vap_xxx")
    .clientSecret("your-secret")
    .logLevel(SdkLogLevel.INFO)
    .buildBlocking();

// 使用签名交换模式 — buildAsync() 异步构建，返回 CompletableFuture
CompletableFuture<FursuitTvSdk> future = JvmFursuitTvSdkBuilder.create()
    .clientId("vap_xxx")
    .clientSecret("your-secret")
    .logLevel(SdkLogLevel.INFO)
    .buildAsync();

FursuitTvSdk sdk = future.get();
```

> 💡 Java 中调用 SDK 的 `suspend` 函数时，使用 `xxxBlocking()` 同步阻塞或 `xxxAsync()` 返回 `CompletableFuture`。详见 [平台指南 - Java 调用 suspend 函数](platform-guide.md#java-调用-suspend-函数)。

### JavaScript/TypeScript 用户

```typescript
import { fursuitTvSdk, SdkLogLevel } from "@regadpole/fursuit-tv-sdk";

const sdk = await fursuitTvSdk({
    clientId: "vap_xxx",
    clientSecret: "your-secret",
    logLevel: SdkLogLevel.INFO,
});

const profile = sdk.user.getUserProfile("username");
```

## 3. 调用第一个 API

### 获取用户资料

```kotlin
val profile = sdk.user.getUserProfile("username")
println("昵称：${profile.nickname}")
println("物种：${profile.fursuitSpecies}")
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
import kotlinx.coroutines.runBlocking

fun main() = runBlocking {
    // 初始化 SDK（fursuitTvSdk 是 suspend 函数，自动完成令牌交换）
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

### Java 完整示例

```java
import com.furrist.rp.furtv.sdk.FursuitTvSdk;
import com.furrist.rp.furtv.sdk.factory.JvmFursuitTvSdkBuilder;
import com.furrist.rp.furtv.sdk.model.SdkLogLevel;

public class Main {
    public static void main(String[] args) {
        FursuitTvSdk sdk = JvmFursuitTvSdkBuilder.create()
            .apiKey("your-api-key")
            .logLevel(SdkLogLevel.INFO)
            .buildBlocking();

        try {
            var profile = sdk.user.getUserProfileBlocking("username");
            System.out.println("用户：" + profile.getNickname());
        } catch (Exception e) {
            System.out.println("错误：" + e.getMessage());
        } finally {
            sdk.close();
        }
    }
}
```

### JavaScript/TypeScript 完整示例

```typescript
import { fursuitTvSdk, SdkLogLevel } from "@regadpole/fursuit-tv-sdk";

const sdk = await fursuitTvSdk({
    clientId: "vap_xxx",
    clientSecret: "your-secret",
    logLevel: SdkLogLevel.INFO,
});

try {
    const profile = sdk.user.getUserProfile("username");
    console.log(`用户：${profile.displayName}`);
} catch (e) {
    console.error("错误：", e.message);
} finally {
    sdk.close();
}
```

## 5. 下一步

- [认证详解](authentication.md) - 了解签名交换和 OAuth 的区别
- [配置选项](configuration.md) - 查看所有可用的配置参数
- [API 参考](api/README.md) - 完整的 API 文档
- [错误处理](error-handling.md) - 学习如何处理异常情况

## 常见问题

**Q: 在哪里获取 clientId 和 clientSecret？**

A: 在 VDS 开放平台注册应用后获得。注意：`clientId` 即 VDS 文档中的 `appId`。

**Q: apiKey 和 accessToken 有什么区别？**

A: `apiKey` 用于签名交换认证，`accessToken` 用于 OAuth 认证。详见 [认证详解](authentication.md)。

**Q: SDK 支持哪些平台？**

A: JVM、JavaScript（浏览器 + Node.js）和 Kotlin Native（iOS/macOS/Linux/Windows）。详见 [平台指南](platform-guide.md)。

---

**提示**: 生产环境请妥善保管 `clientSecret`，不要提交到版本控制系统。
