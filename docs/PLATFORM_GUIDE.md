# Fursuit.TV SDK 平台指南

本文档说明各平台的特定配置和注意事项。

## 📋 目录

1. [JVM 平台](#jvm-平台)
2. [JS 平台](#js-平台)
3. [Native 平台](#native-平台)
4. [Android 集成](#android-集成)
5. [iOS 集成](#ios-集成)
6. [跨平台开发](#跨平台开发)

## JVM 平台

### 配置

```kotlin
// build.gradle.kts
kotlin {
    jvm {
        testRuns["test"].executionTask.configure {
            useJUnitPlatform()
        }
    }
    
    jvmToolchain(17)
}

dependencies {
    implementation(libs.fursuit.tv.sdk)
    implementation(libs.ktor.client.java)  // JVM HTTP 引擎
}
```

### 使用示例

```kotlin
// src/jvmMain/kotlin/Main.kt
import com.furrist.rp.furtv.sdk.FursuitTvSdk
import kotlinx.coroutines.runBlocking
import java.awt.Desktop
import java.net.URI

fun main() = runBlocking {
    val sdk = FursuitTvSdk(
        appId = "vap_xxxxxxxxxxxxxxxx",
        appSecret = "your-app-secret"
    )
    
    try {
        // 签名认证
        sdk.auth.exchangeToken(appId, appSecret)
        
        // 调用 API
        val profile = sdk.user.getUserProfile("username")
        println("用户：${profile.displayName}")
        
    } catch (e: Exception) {
        e.printStackTrace()
    } finally {
        sdk.close()
    }
}
```

### OAuth 支持

JVM 平台支持完整的 OAuth 流程，自动打开系统浏览器：

```kotlin
val config = OAuthConfig(
    callbackHost = "localhost",
    callbackPort = 8080
)

val sdk = FursuitTvSdk.initWithOAuth(appId, config)
// 自动打开浏览器并完成授权
```

### 注意事项

- ✅ 需要 JDK 17 或更高版本
- ✅ 支持所有 OAuth 功能
- ✅ 自动打开系统浏览器
- ⚠️ 确保防火墙允许本地回环连接

## JS 平台

### 配置

```kotlin
// build.gradle.kts
kotlin {
    js {
        nodejs {
            testTask {
                useMocha()
            }
        }
        binaries.library()
    }
}

dependencies {
    implementation(libs.fursuit.tv.sdk)
    implementation(libs.ktor.client.js)  // JS HTTP 引擎
}
```

### Node.js 使用示例

```kotlin
// src/jsMain/kotlin/main.kt
import com.furrist.rp.furtv.sdk.FursuitTvSdk
import kotlinx.coroutines.*

fun main() = runBlocking {
    val sdk = FursuitTvSdk(
        appId = "vap_xxxxxxxxxxxxxxxx",
        appSecret = "your-app-secret"
    )
    
    try {
        sdk.auth.exchangeToken(appId, appSecret)
        
        val profile = sdk.user.getUserProfile("username")
        println("用户：${profile.displayName}")
        
    } catch (e: Exception) {
        console.log("错误：${e.message}")
    } finally {
        sdk.close()
    }
}
```

### OAuth 限制

**重要**: JS 平台在浏览器环境中不支持 OAuth！

**解决方案**:
1. 在 Node.js 环境中使用 OAuth
2. 使用签名认证（推荐）
3. 使用后端服务代理 OAuth

```kotlin
// Node.js 环境可以使用 OAuth
val config = OAuthConfig(
    callbackHost = "127.0.0.1",
    callbackPort = 3000
)

val sdk = FursuitTvSdk.initWithOAuth(appId, config)
```

### 打包示例

```javascript
// webpack.config.js
module.exports = {
    target: 'node',  // 或 'web'（但 OAuth 不支持）
    entry: './dist/js/main.js',
    output: {
        filename: 'bundle.js',
        path: __dirname
    }
};
```

### 注意事项

- ✅ Node.js 16+ 支持完整功能
- ⚠️ Browser 环境不支持 OAuth
- ⚠️ 需要处理 CORS（浏览器环境）
- ✅ 支持所有 API 调用

## Native 平台

### 配置

```kotlin
// build.gradle.kts
kotlin {
    // iOS
    iosX64()
    iosArm64()
    iosSimulatorArm64()
    
    // macOS
    macosX64()
    macosArm64()
    
    // Linux
    linuxX64()
    linuxArm64()
    
    // Windows
    mingwX64()
    
    sourceSets {
        commonMain {
            dependencies {
                implementation(libs.fursuit.tv.sdk)
            }
        }
    }
}
```

### macOS 使用示例

```kotlin
// src/macosMain/kotlin/main.kt
import com.furrist.rp.furtv.sdk.FursuitTvSdk
import kotlinx.coroutines.*
import platform.Foundation.*

fun main() = runBlocking {
    val sdk = FursuitTvSdk(
        appId = "vap_xxxxxxxxxxxxxxxx",
        appSecret = "your-app-secret"
    )
    
    try {
        sdk.auth.exchangeToken(appId, appSecret)
        
        val profile = sdk.user.getUserProfile("username")
        println("用户：${profile.displayName}")
        
    } catch (e: Exception) {
        println("错误：${e.message}")
    } finally {
        sdk.close()
    }
}
```

### OAuth 支持

Native 平台支持 OAuth，但需要手动打开浏览器：

```kotlin
// macOS
import AppKit.NSWorkspace
import Foundation.NSURL

fun openBrowser(url: String) {
    NSWorkspace.sharedWorkspace.openURL(NSURL.URLWithString(url)!!)
}

// iOS
import SafariServices

func openSafari(url: String) {
    let safari = SFSafariViewController(url: URL(string: url)!)
    present(safari, animated: true)
}
```

### 注意事项

- ✅ 支持所有 API 功能
- ⚠️ OAuth 需要手动打开浏览器
- ⚠️ iOS 需要配置 ATS（App Transport Security）
- ✅ 支持后台运行

## Android 集成

### 步骤 1: 添加依赖

```kotlin
// app/build.gradle.kts
dependencies {
    implementation("com.furrist.rp:fursuit-tv-sdk:1.0-SNAPSHOT")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.10.1")
}
```

### 步骤 2: 添加权限

```xml
<!-- AndroidManifest.xml -->
<uses-permission android:name="android.permission.INTERNET" />
```

### 步骤 3: 创建 ViewModel

```kotlin
class UserViewModel(
    private val appId: String,
    private val appSecret: String
) : ViewModel() {
    
    private val sdk = FursuitTvSdk(appId, appSecret)
    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()
    
    fun loadUser(username: String) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            
            try {
                // 确保令牌有效
                sdk.auth.getValidAccessToken(appId, appSecret)
                
                // 获取用户资料
                val profile = sdk.user.getUserProfile(username)
                _uiState.value = UiState.Success(profile)
                
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.message)
            }
        }
    }
    
    override fun onCleared() {
        super.onCleared()
        sdk.close()
    }
}

sealed class UiState {
    object Loading : UiState()
    data class Success(val profile: UserProfile) : UiState()
    data class Error(val message: String) : UiState()
}
```

### 步骤 4: 在 Compose 中使用

```kotlin
@Composable
fun UserScreen(viewModel: UserViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    
    when (val state = uiState) {
        is UiState.Loading -> CircularProgressIndicator()
        is UiState.Success -> {
            Text("用户：${state.profile.displayName}")
        }
        is UiState.Error -> {
            Text("错误：${state.message}")
        }
    }
}
```

### 步骤 5: OAuth 支持（可选）

```kotlin
// 使用 Custom Tabs 打开授权页面
val customTabsIntent = CustomTabsIntent.Builder().build()
customTabsIntent.launchUrl(context, Uri.parse(authorizeUrl))

// 配置 Deep Link 接收回调
// AndroidManifest.xml
<activity android:name=".OAuthCallbackActivity">
    <intent-filter>
        <action android:name="android.intent.action.VIEW" />
        <category android:name="android.intent.category.DEFAULT" />
        <category android:name="android.intent.category.BROWSABLE" />
        <data android:scheme="myapp" android:host="oauth/callback" />
    </intent-filter>
</activity>
```

### 注意事项

- ✅ 完整支持所有功能
- ⚠️ 需要在主线程外调用 API
- ⚠️ 正确处理生命周期
- ✅ 推荐使用 ViewModel

## iOS 集成

### 步骤 1: 使用 SPM 或 CocoaPods

**Swift Package Manager**:
```swift
// Package.swift
dependencies: [
    .package(url: "https://github.com/RegadPoleCN/fursuit-tv-sdk", from: "1.0.0")
]
```

**CocoaPods**:
```ruby
# Podfile
pod 'FursuitTvSdk', '~> 1.0'
```

### 步骤 2: 创建 Swift 包装器

```swift
import FursuitTvSdk
import Combine

class UserService: ObservableObject {
    private let sdk: FursuitTvSdk
    @Published var userProfile: UserProfile?
    @Published var isLoading = false
    @Published var errorMessage: String?
    
    init(appId: String, appSecret: String) {
        self.sdk = FursuitTvSdk(appId: appId, appSecret: appSecret)
    }
    
    func loadUser(username: String) async {
        await MainActor.run {
            isLoading = true
            errorMessage = nil
        }
        
        do {
            // 确保令牌有效
            try await sdk.auth.getValidAccessToken(
                appId: appId,
                appSecret: appSecret
            )
            
            // 获取用户资料
            let profile = try await sdk.user.getUserProfile(username: username)
            
            await MainActor.run {
                self.userProfile = profile
                self.isLoading = false
            }
        } catch {
            await MainActor.run {
                self.errorMessage = error.localizedDescription
                self.isLoading = false
            }
        }
    }
    
    deinit {
        sdk.close()
    }
}
```

### 步骤 3: 在 SwiftUI 中使用

```swift
struct UserView: View {
    @StateObject private var userService = UserService(
        appId: "vap_xxxxxxxxxxxxxxxx",
        appSecret: "secret"
    )
    
    var body: some View {
        VStack {
            if userService.isLoading {
                ProgressView()
            } else if let error = userService.errorMessage {
                Text("错误：\(error)")
            } else if let profile = userService.userProfile {
                Text("用户：\(profile.displayName)")
            }
        }
        .onAppear {
            Task {
                await userService.loadUser(username: "username")
            }
        }
    }
}
```

### 步骤 4: OAuth 支持

```swift
import SafariServices
import AuthenticationServices

// 使用 ASWebAuthenticationSession（推荐）
func presentOAuth(url: String, callbackUrlScheme: String) async throws -> String {
    return try await withCheckedThrowingContinuation { continuation in
        let session = ASWebAuthenticationSession(
            url: URL(string: url)!,
            callbackURLScheme: callbackUrlScheme
        ) { callbackURL, error in
            if let error = error {
                continuation.resume(throwing: error)
            } else if let url = callbackURL {
                // 从 URL 中提取授权码
                continuation.resume(returning: url.absoluteString)
            }
        }
        session.start()
    }
}
```

### 注意事项

- ✅ 完整支持所有功能
- ⚠️ iOS 13+ 支持 ASWebAuthenticationSession
- ⚠️ 需要配置 Info.plist
- ✅ 推荐使用 Swift Concurrency

## 跨平台开发

### 共享代码结构

```
src/
├── commonMain/
│   ├── kotlin/
│   │   ├── common/
│   │   │   ├── UserRepository.kt
│   │   │   ├── ApiClient.kt
│   │   │   └── Models.kt
│   │   └── di/
│   │       └── DependencyInjection.kt
├── jvmMain/
│   └── kotlin/
│       └── Platform.jvm.kt
├── jsMain/
│   └── kotlin/
│       └── Platform.js.kt
├── iosMain/
│   └── kotlin/
│       └── Platform.ios.kt
└── androidMain/
    └── kotlin/
        └── Platform.android.kt
```

### 使用 expect/actual

```kotlin
// commonMain
expect class PlatformTokenStorage() {
    fun saveToken(token: TokenInfo)
    fun loadToken(): TokenInfo?
}

// jvmMain
actual class PlatformTokenStorage actual constructor() {
    actual fun saveToken(token: TokenInfo) {
        // 保存到文件
    }
    
    actual fun loadToken(): TokenInfo? {
        // 从文件加载
    }
}

// jsMain
actual class PlatformTokenStorage actual constructor() {
    actual fun saveToken(token: TokenInfo) {
        // 保存到 localStorage
    }
    
    actual fun loadToken(): TokenInfo? {
        // 从 localStorage 加载
    }
}
```

### 平台兼容性检查

```kotlin
// 使用 @OptIn 标记平台特定 API
@OptIn(ExperimentalNativeApi::class)
fun getPlatformName(): String {
    return when {
        Platform.isAndroid -> "Android"
        Platform.isIOS -> "iOS"
        Platform.isJvm -> "JVM"
        Platform.isJs -> "JS"
        else -> "Unknown"
    }
}
```

## 总结

### 平台功能对比

| 功能 | JVM | JS (Node) | JS (Browser) | iOS | Android |
|------|-----|-----------|--------------|-----|---------|
| API 调用 | ✅ | ✅ | ✅ | ✅ | ✅ |
| OAuth | ✅ | ✅ | ❌ | ✅ | ✅ |
| 自动令牌刷新 | ✅ | ✅ | ✅ | ✅ | ✅ |
| 后台运行 | ✅ | ✅ | ⚠️ | ✅ | ✅ |
| 自动打开浏览器 | ✅ | ⚠️ | ❌ | ⚠️ | ⚠️ |

### 推荐实践

1. **共享业务逻辑**: 将核心业务逻辑放在 commonMain
2. **平台特定实现**: 使用 expect/actual 处理平台差异
3. **统一依赖管理**: 使用版本目录管理所有平台依赖
4. **测试覆盖**: 在 commonTest 中编写共享测试
5. **文档化**: 记录平台特定的配置和限制

继续学习：
- [开发者指南](DEVELOPER_GUIDE.md)
- [最佳实践](BEST_PRACTICES.md)
- [故障排除](TROUBLESHOOTING.md)
