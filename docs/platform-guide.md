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

## JavaScript 平台

### 环境要求

- **Node.js**: 16.0 或更高
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

## Android 平台

### 环境要求

- **Android API**: 21 或更高
- **Kotlin**: 1.9.0 或更高
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

```swift
import FursuitTvSdk
import Combine

class UserService: ObservableObject {
    private let sdk: FursuitTvSdk
    
    init() {
        sdk = FursuitTvSdk(
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
// 使用 Kotlin 平台检测
val platform = when {
    org.khronos.webgl.Int8Array != null -> "JS"
    else -> "Native"
}

val sdk = fursuitTvSdk {
    // 根据平台调整配置
    connectTimeout = if (platform == "JS") 15000 else 10000
}
```

## 相关文档

- [配置选项](configuration.md) - 各平台配置详解
- [最佳实践](best-practices.md) - 跨平台最佳实践
