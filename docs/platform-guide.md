# 平台指南

本文档介绍 Fursuit.TV SDK 在不同平台（JVM、JS、Native）上的使用方法和注意事项。

## 目录

1. [JVM 平台](#jvm-平台)
2. [JavaScript 平台](#javascript-平台)
3. [Native 平台](#native-平台)
4. [Android 平台](#android-平台)
5. [iOS 平台](#ios-平台)

## JVM 平台

### 环境要求

- **Java 版本**: Java 17 或更高
- **Kotlin 版本**: 2.1.20+
- **构建工具**: Gradle 8.0+ 或 Maven 3.6+

### 添加依赖

```kotlin
// build.gradle.kts
dependencies {
    implementation("com.furrist.rp:fursuit-tv-sdk:{version}")
}
```

### 快速开始

> `fursuitTvSdk` 是 `suspend` 函数，提供 `clientId` + `clientSecret` 时自动完成令牌交换。`clientId` 即 VDS 文档中的 `appId`。

```kotlin
import com.furrist.rp.furtv.sdk.fursuitTvSdk
import kotlinx.coroutines.runBlocking

fun main() = runBlocking {
    val sdk = fursuitTvSdk {
        clientId = "vap_xxx"
        clientSecret = "your-secret"
    }
    
    try {
        val profile = sdk.user.getUserProfile("username")
        println("用户：${profile.displayName}")
    } finally {
        sdk.close()
    }
}
```

### 构建命令

```bash
# 编译 JVM 版本
./gradlew jvmJar

# 运行示例
./gradlew :examples:jvm:run
```

### 日志配置（Logback）

```kotlin
// src/main/resources/logback.xml
<configuration>
    <logger name="io.ktor" level="DEBUG"/>
    <logger name="com.furrist.rp.furtv.sdk" level="INFO"/>
</configuration>
```

### OAuth 支持

JVM 平台通过 `JvmOAuthCallbackHandler` 提供 OAuth 支持：

- 自动启动本地 HTTP 服务器（基于 Ktor CIO）监听回调
- 自动打开系统默认浏览器引导用户授权
- 支持 `loginWithOAuth()` 一站式 OAuth 流程

```kotlin
val sdk = fursuitTvSdk {
    clientId = "vap_xxx"
    clientSecret = "your-secret"
}

// loginWithOAuth() 默认使用 JvmOAuthCallbackHandler
val tokenInfo = sdk.auth.loginWithOAuth()
val userInfo = sdk.auth.getUserInfo()
sdk.close()
```

### Java 用户：JvmFursuitTvSdkBuilder

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

> 💡 也可使用 `JvmFursuitTvSdkFactory.createDsl()` 通过 `Consumer<MutableSdkConfig>` 配置（仅 API Key 模式）。

#### Java 调用 suspend 函数

SDK 的所有 API 方法均为 Kotlin `suspend` 函数。通过 `kotlin-suspend-transform-compiler-plugin`，SDK 自动为每个 `suspend` 函数生成了 Java 友好的变体：

- **`xxxBlocking()`** — 同步阻塞调用，直接返回结果。适用于简单脚本或不关心线程阻塞的场景。
- **`xxxAsync()`** — 异步调用，返回 `CompletableFuture<T>`。适用于需要非阻塞并发的场景。

```java
// 同步阻塞调用
var profile = sdk.user.getUserProfileBlocking("username");
var popular = sdk.search.getPopularBlocking(null);
var health  = sdk.base.healthBlocking();

// 异步 CompletableFuture 调用
CompletableFuture<UserProfile> profileFuture = sdk.user.getUserProfileAsync("username");
CompletableFuture<PopularResult> popularFuture = sdk.search.getPopularAsync(null);
CompletableFuture<HealthResult> healthFuture = sdk.base.healthAsync();

// 组合多个异步调用
profileFuture.thenAccept(p -> System.out.println("Username: " + p.getUsername()));
```

> ⚠️ 带有默认参数的方法（如 `getPopular(limit: Int? = null)`），Java 中需显式传参（如 `null`）。`xxxBlocking()` 和 `xxxAsync()` 变体同样需要显式传参。

#### Java 完整示例

```java
FursuitTvSdk sdk = JvmFursuitTvSdkBuilder.create()
        .apiKey("your-api-key")
        .logLevel(SdkLogLevel.INFO)
        .buildBlocking();

try {
    var profile = sdk.user.getUserProfileBlocking("username");
    System.out.println("Username: " + profile.getUsername());

    var popular = sdk.search.getPopularBlocking(null);
    System.out.println("Popular users: " + popular.getUsers().size());

    var health = sdk.base.healthBlocking();
    System.out.println("Health: " + health.getMessage());
} catch (NotFoundException e) {
    System.err.println("Not found: " + e.getMessage());
} catch (ApiException e) {
    System.err.println("API error (HTTP " + e.getStatusCode() + "): " + e.getMessage());
} catch (FursuitTvSdkException e) {
    System.err.println("SDK error: " + e.getMessage());
} finally {
    sdk.close();
}
```

### Kotlin 用户

Kotlin 用户无需任何额外适配，直接使用 `suspend` 函数即可。SDK 的所有 API 方法均为 `suspend` 函数，可在协程中直接调用：

```kotlin
val sdk = fursuitTvSdk {
    clientId = "vap_xxx"
    clientSecret = "your-secret"
}

val profile = sdk.user.getUserProfile("username")
```

> 💡 Java 的 `xxxBlocking()` / `xxxAsync()` 和 JS 的 `xxxAsync()` 变体均由 `kotlin-suspend-transform-compiler-plugin` 自动生成，Kotlin 侧无需使用这些变体。

## JavaScript 平台

### 环境要求

- **Node.js**: 16.0 或更高（Node.js 环境）
- **包管理器**: npm 8+ 或 yarn 1.22+
- **Kotlin/JS**: 2.1.20+

### 添加依赖

```kotlin
// build.gradle.kts
kotlin {
    js {
        browser()
        nodejs()
    }
    
    sourceSets {
        val jsMain by getting {
            dependencies {
                implementation("com.furrist.rp:fursuit-tv-sdk:{version}")
            }
        }
    }
}
```

### 浏览器环境

```kotlin
import com.furrist.rp.furtv.sdk.fursuitTvSdk
import kotlinx.browser.document
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

fun main() {
    val sdk = fursuitTvSdk {
        clientId = "vap_xxx"
        clientSecret = "your-secret"
    }
    
    MainScope().launch {
        val profile = sdk.user.getUserProfile("username")
        document.getElementById("output")?.textContent = "用户：${profile.displayName}"
    }
}
```

### 纯 JavaScript/TypeScript 调用

#### npm 安装

```bash
npm install @regadpole/fursuit-tv-sdk
```

#### 浏览器环境（ESM）

```typescript
import { fursuitTvSdk } from "@regadpole/fursuit-tv-sdk";

const sdk = await fursuitTvSdk({
    clientId: "vap_xxx",
    clientSecret: "your-secret",
});

const profile = sdk.user.getUserProfile("username");
console.log(`用户：${profile.displayName}`);
sdk.close();
```

#### Node.js 环境（ESM）

```typescript
import { fursuitTvSdk } from "@regadpole/fursuit-tv-sdk";

const sdk = await fursuitTvSdk({
    clientId: "vap_xxx",
    clientSecret: "your-secret",
});

try {
    const profile = sdk.user.getUserProfile("username");
    console.log(`用户：${profile.displayName}`);
} finally {
    sdk.close();
}
```

### Node.js 环境

```kotlin
import com.furrist.rp.furtv.sdk.fursuitTvSdk
import kotlinx.coroutines.*

fun main() = runBlocking {
    val sdk = fursuitTvSdk {
        clientId = "vap_xxx"
        clientSecret = "your-secret"
        baseUrl = "https://open-global.vdsentnet.com"
    }
    
    val profile = sdk.user.getUserProfile("username")
    println("用户：${profile.displayName}")
    
    sdk.close()
}
```

### 浏览器特定配置

```kotlin
// 配置 CORS 和 Fetch API
val sdk = fursuitTvSdk {
    // 浏览器环境自动使用 Fetch API
    // 无需额外配置
}
```

### OAuth 支持

JS 平台通过 `JsOAuthCallbackHandler` 提供 OAuth 支持，根据运行环境自动选择实现：

- **浏览器环境**：使用 `postMessage` 机制监听回调
- **Node.js 环境**：使用 Node.js `http` 模块创建本地服务器

```kotlin
// Node.js 环境 OAuth 示例
val sdk = fursuitTvSdk {
    clientId = "vap_xxx"
    clientSecret = "your-secret"
}

val tokenInfo = sdk.auth.loginWithOAuth()
val userInfo = sdk.auth.getUserInfo()
sdk.close()
```

## Native 平台

### 环境要求

- **Kotlin/Native**: 2.1.20+
- **目标平台**: macOS, Linux, Windows, iOS

### macOS

```kotlin
// build.gradle.kts
kotlin {
    macosX64()
    macosArm64()
    
    sourceSets {
        val macosMain by getting {
            dependencies {
                implementation("com.furrist.rp:fursuit-tv-sdk:{version}")
            }
        }
    }
}
```

### Linux

```kotlin
// build.gradle.kts
kotlin {
    linuxX64()
    
    sourceSets {
        val linuxMain by getting {
            dependencies {
                implementation("com.furrist.rp:fursuit-tv-sdk:{version}")
            }
        }
    }
}
```

### Windows

```kotlin
// build.gradle.kts
kotlin {
    mingwX64()
    
    sourceSets {
        val mingwMain by getting {
            dependencies {
                implementation("com.furrist.rp:fursuit-tv-sdk:{version}")
            }
        }
    }
}
```

### OAuth 支持

Native 平台通过 `NativeOAuthCallbackHandler` 提供完整的 OAuth 支持：

- 基于 Ktor CIO 启动本地 HTTP 服务器接收回调
- 适用于 iOS、macOS、Linux、Windows 等 Kotlin/Native 平台
- 支持 `loginWithOAuth()` 一站式 OAuth 流程

```kotlin
val sdk = fursuitTvSdk {
    clientId = "vap_xxx"
    clientSecret = "your-secret"
}

val tokenInfo = sdk.auth.loginWithOAuth()
val userInfo = sdk.auth.getUserInfo()
sdk.close()
```

> 💡 iOS 平台建议使用 `ASWebAuthenticationSession` 实现自定义 `OAuthCallbackHandler`，提供更好的用户体验。详见 [iOS 示例](../examples/ios/README.md)。

## Android 平台

### 环境要求

- **Android API**: 21 或更高
- **Kotlin**: 2.1.20 或更高
- **Gradle**: 8.0+

### 添加依赖

```kotlin
// app/build.gradle.kts
dependencies {
    implementation("com.furrist.rp:fursuit-tv-sdk:{version}")
    implementation("io.ktor:ktor-client-android:2.3.7")
}
```

### 网络配置

```kotlin
// AndroidManifest.xml
<uses-permission android:name="android.permission.INTERNET" />

<application>
    <!-- 配置网络 -->
    <meta-data
        android:name="io.ktor.networking"
        android:value="android" />
</application>
```

### 使用示例

```kotlin
// Android KTX + Coroutines
class MainActivity : AppCompatActivity() {
    private val sdk by lazy {
        fursuitTvSdk {
            clientId = BuildConfig.CLIENT_ID
            clientSecret = BuildConfig.CLIENT_SECRET
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        lifecycleScope.launch {
            try {
                val profile = sdk.user.getUserProfile("username")
                textView.text = "用户：${profile.displayName}"
            } catch (e: Exception) {
                textView.text = "错误：${e.message}"
            }
        }
    }
    
    override fun onDestroy() {
        super.onDestroy()
        sdk.close()
    }
}
```

### ProGuard 配置

```proguard
# 保留 Ktor 和 SDK 相关类
-keep class io.ktor.** { *; }
-keep class com.furrist.rp.furtv.sdk.** { *; }
```

## iOS 平台

### 环境要求

- **Xcode**: 14.0 或更高
- **iOS 版本**: 13.0 或更高
- **Kotlin/Native**: 2.1.20+

### 添加依赖

```kotlin
// build.gradle.kts
kotlin {
    iosX64()
    iosArm64()
    iosSimulatorArm64()
    
    sourceSets {
        val iosMain by getting {
            dependencies {
                implementation("com.furrist.rp:fursuit-tv-sdk:{version}")
            }
        }
    }
}
```

### 使用示例（Swift）

> ⚠️ `FursuitTvSdk` 构造函数为 `internal`，Swift 代码应使用工厂方法创建实例。

```swift
import FursuitTvSdk
import Combine

class UserService: ObservableObject {
    private let sdk: FursuitTvSdk
    
    init() async throws {
        // 使用工厂方法创建（构造函数为 internal）
        sdk = try await FursuitTvSdk.Companion().createForTokenExchange(
            clientId: "vap_xxx",
            clientSecret: "your-secret"
        )
    }
    
    func getUserProfile(username: String) async throws -> UserProfile {
        return try await sdk.user.getUserProfile(username)
    }
    
    deinit {
        sdk.close()
    }
}
```

### SwiftUI 集成

```swift
struct ContentView: View {
    @StateObject private var userService = UserService()
    @State private var profile: UserProfile?
    
    var body: some View {
        VStack {
            if let profile = profile {
                Text("用户：\(profile.displayName)")
            } else {
                ProgressView()
                    .onAppear {
                        Task {
                            profile = try? await userService.getUserProfile(
                                username: "username"
                            )
                        }
                    }
            }
        }
    }
}
```

### OAuth 支持（iOS）

iOS 平台支持通过 `NativeOAuthCallbackHandler` 进行 OAuth 授权，也可使用 `ASWebAuthenticationSession` 实现自定义 `OAuthCallbackHandler`：

```swift
// 使用 ASWebAuthenticationSession 实现自定义回调处理器
class iOSOAuthHandler: OAuthCallbackHandler {
    func startListening() { /* ... */ }
    func waitForCallback() -> OAuthCallbackResult { /* ... */ }
    func startAndGetCallback(authorizeUrl: String) -> OAuthCallbackResult { /* ... */ }
    func stop() { /* ... */ }
}

// 设置自定义处理器
sdk.auth.setOAuthCallbackHandler(iOSOAuthHandler())
let tokenInfo = try await sdk.auth.loginWithOAuth()
```

## 跨平台开发

### 共享代码

```kotlin
// commonMain/kotlin/UserService.kt
expect class PlatformUserService() {
    suspend fun getUserProfile(username: String): UserProfile?
}

// jvmMain/kotlin/UserService.kt
actual class PlatformUserService {
    actual suspend fun getUserProfile(username: String): UserProfile? {
        val sdk = fursuitTvSdk { /* JVM 配置 */ }
        return sdk.user.getUserProfile(username)
    }
}

// jsMain/kotlin/UserService.kt
actual class PlatformUserService {
    actual suspend fun getUserProfile(username: String): UserProfile? {
        val sdk = fursuitTvSdk { /* JS 配置 */ }
        return sdk.user.getUserProfile(username)
    }
}
```

### 平台检测

```kotlin
// 使用 expect/actual 进行平台检测
// commonMain
expect val currentPlatform: String

// jvmMain
actual val currentPlatform: String = "JVM"

// jsMain
actual val currentPlatform: String = "JS"

// nativeMain
actual val currentPlatform: String = "Native"

val sdk = fursuitTvSdk {
    // 根据平台调整配置
    connectTimeout = if (currentPlatform == "JS") 15000 else 10000
}
```

## OAuth 支持总览

| 平台 | OAuth 实现 | 回调方式 | loginWithOAuth() 支持 |
|------|-----------|---------|---------------------|
| **JVM** | `JvmOAuthCallbackHandler` | 本地 HTTP 服务器 + 浏览器 | ✅ 完整支持 |
| **JS (浏览器)** | `JsOAuthCallbackHandler` | postMessage 机制 | ✅ 完整支持 |
| **JS (Node.js)** | `JsOAuthCallbackHandler` | Node.js http 服务器 | ✅ 完整支持 |
| **Native** | `NativeOAuthCallbackHandler` | Ktor CIO 本地服务器 | ✅ 完整支持 |
| **iOS** | `NativeOAuthCallbackHandler` / 自定义 | 本地服务器 / ASWebAuthenticationSession | ✅ 完整支持 |
| **Android** | JVM 实现 | 本地 HTTP 服务器 + 浏览器 | ✅ 完整支持 |

## 相关文档

- [配置选项](configuration.md) - 各平台配置详解
- [最佳实践](best-practices.md) - 跨平台最佳实践
- [OAuth 指南](oauth-guide.md) - OAuth 回调处理器详解
