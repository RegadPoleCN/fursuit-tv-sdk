# Fursuit.TV SDK Android 集成指南

本文档说明如何在 Android 项目中集成 Fursuit.TV SDK。

## 前置要求

- Android Studio Ladybug (2024.1) 或更高版本
- Android SDK 21+ (Android 5.0)
- Kotlin 2.0+
- Gradle 8.0+

## 步骤 1: 添加依赖

在 `app/build.gradle.kts` 中添加：

```kotlin
android {
    compileSdk = 35
    
    defaultConfig {
        minSdk = 21
        targetSdk = 35
    }
    
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    
    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    // Fursuit.TV SDK
    implementation("com.furrist.rp:fursuit-tv-sdk:{version}")
    
    // Kotlinx Coroutines (Android)
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.10.1")
    
    // Lifecycle (可选，用于 ViewModel)
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.7")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.7")
}
```

## 步骤 2: 添加网络权限

在 `AndroidManifest.xml` 中添加：

```xml
<manifest xmlns:android="http://schemas.android.com/apk/res/android">
    
    <!-- 网络权限 -->
    <uses-permission android:name="android.permission.INTERNET" />
    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
    
    <application>
        <!-- 你的 Application -->
    </application>
</manifest>
```

## 步骤 3: 创建 Repository

```kotlin
// UserRepository.kt
package com.example.app.data

import com.furrist.rp.furtv.sdk.FursuitTvSdk
import com.furrist.rp.furtv.sdk.fursuitTvSdk
import com.furrist.rp.furtv.sdk.model.UserProfile

class UserRepository(
    private val clientId: String,
    private val clientSecret: String
) {
    private var sdk: FursuitTvSdk? = null
    
    suspend fun initialize() {
        sdk = fursuitTvSdk {
            clientId = this@UserRepository.clientId
            clientSecret = this@UserRepository.clientSecret
        }
    }
    
    suspend fun getUserProfile(username: String): UserProfile {
        val currentSdk = sdk ?: throw IllegalStateException("SDK not initialized")
        return currentSdk.user.getUserProfile(username)
    }
    
    fun close() {
        sdk?.close()
        sdk = null
    }
}
```

## 步骤 4: 创建 ViewModel

```kotlin
// UserViewModel.kt
package com.example.app.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class UiState<out T> {
    object Loading : UiState<Nothing>()
    data class Success<T>(val data: T) : UiState<T>()
    data class Error(val message: String) : UiState<Nothing>()
}

class UserViewModel(
    private val userRepository: UserRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow<UiState<String>>(UiState.Loading)
    val uiState: StateFlow<UiState<String>> = _uiState.asStateFlow()
    
    init {
        viewModelScope.launch {
            try {
                userRepository.initialize()
            } catch (e: Exception) {
                _uiState.value = UiState.Error("初始化失败: ${e.message}")
            }
        }
    }
    
    fun loadUser(username: String) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            
            try {
                val profile = userRepository.getUserProfile(username)
                _uiState.value = UiState.Success(profile.displayName)
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.message ?: "未知错误")
            }
        }
    }
    
    override fun onCleared() {
        super.onCleared()
        userRepository.close()
    }
}
```

## 步骤 5: 在 Activity/Fragment 中使用 (Jetpack Compose)

```kotlin
// MainActivity.kt
package com.example.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewmodel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                UserScreen(
                    clientId = "vap_xxxxxxxxxxxxxxxx",
                    clientSecret = "your-client-secret"
                )
            }
        }
    }
}

@Composable
fun UserScreen(
    clientId: String,
    clientSecret: String,
    viewModel: UserViewModel = viewModel {
        val repo = UserRepository(clientId, clientSecret)
        UserViewModel(repo)
    }
) {
    val uiState by viewModel.uiState.collectAsState()
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        when (val state = uiState) {
            is UiState.Loading -> {
                CircularProgressIndicator()
                Text("加载中...")
            }
            is UiState.Success -> {
                Text("用户：${state.data}", style = MaterialTheme.typography.headlineMedium)
            }
            is UiState.Error -> {
                Text("错误：${state.message}", color = MaterialTheme.colorScheme.error)
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Button(
            onClick = { viewModel.loadUser("username") }
        ) {
            Text("加载用户")
        }
    }
}
```

## 步骤 6: OAuth 支持（可选）

### 使用 Custom Tabs 打开授权页面

```kotlin
// 添加依赖
implementation("androidx.browser:browser:1.8.0")

// OAuthHelper.kt
import android.content.Context
import androidx.browser.customtabs.CustomTabsIntent
import android.net.Uri

object OAuthHelper {
    fun openOAuthPage(context: Context, authorizeUrl: String) {
        val customTabsIntent = CustomTabsIntent.Builder()
            .setShowTitle(true)
            .build()
        
        customTabsIntent.launchUrl(context, Uri.parse(authorizeUrl))
    }
}
```

在 `AndroidManifest.xml` 中配置 Deep Link：

```xml
<activity android:name=".OAuthCallbackActivity"
    android:exported="true">
    <intent-filter>
        <action android:name="android.intent.action.VIEW" />
        <category android:name="android.intent.category.DEFAULT" />
        <category android:name="android.intent.category.BROWSABLE" />
        <data
            android:scheme="myapp"
            android:host="oauth/callback" />
    </intent-filter>
</activity>
```

## 完整示例项目结构

```
app/
├── src/main/
│   ├── AndroidManifest.xml
│   ├── java/com/example/app/
│   │   ├── data/
│   │   │   └── UserRepository.kt
│   │   ├── ui/
│   │   │   ├── MainActivity.kt
│   │   │   └── UserViewModel.kt
│   │   └── OAuthCallbackActivity.kt
│   └── res/
└── build.gradle.kts
```

## 注意事项

### 1. 网络配置

确保在 `AndroidManifest.xml` 中添加了网络权限。

### 2. 主线程安全

**不要在主线程中调用 API！** 必须使用协程：

```kotlin
// ❌ 错误：在主线程调用（会崩溃）
val profile = sdk.user.getUserProfile("username")

// ✅ 正确：使用协程
lifecycleScope.launch {
    val profile = sdk.user.getUserProfile("username")
}
```

### 3. 生命周期管理

在 ViewModel 中管理 SDK 生命周期：

```kotlin
class UserViewModel : ViewModel() {
    private lateinit var sdk: FursuitTvSdk
    
    init {
        viewModelScope.launch {
            sdk = fursuitTvSdk { ... }
        }
    }
    
    override fun onCleared() {
        super.onCleared()
        if (::sdk.isInitialized) {
            sdk.close()  // 释放资源
        }
    }
}
```

### 4. 错误处理

始终处理可能的异常：

```kotlin
try {
    val profile = sdk.user.getUserProfile(username)
} catch (e: NotFoundException) {
    // 用户不存在 - 显示友好提示
} catch (e: AuthenticationException) {
    // 认证失败 - 引导用户重新登录
} catch (e: TokenExpiredException) {
    // 令牌过期 - SDK 会自动刷新
} catch (e: NetworkException) {
    // 网络错误 - 提示检查网络连接
} catch (e: ApiException) {
    // API 业务错误 - 显示错误信息
}
```

### 5. ProGuard/R8 配置

如果使用代码混淆，添加规则：

```proguard
# Fursuit.TV SDK
-keep class com.furrist.rp.furtv.sdk.** { *; }
-keepclassmembers class com.furrist.rp.furtv.sdk.** { *; }
```

### 6. 凭证安全

⚠️ **不要将凭证硬编码在代码中！**

推荐做法：
- 使用 BuildConfig 字段（从环境变量注入）
- 使用 Android Keystore System 加密存储
- 从安全的后端服务获取临时令牌

```kotlin
// build.gradle.kts
android {
    defaultConfig {
        buildConfigField("String", "CLIENT_ID", "\"${project.findProperty(\"CLIENT_ID\")}\"")
        buildConfigField("String", "CLIENT_SECRET", "\"${project.findProperty(\"CLIENT_SECRET\")}\"")
    }
}

// 使用时
val sdk = fursuitTvSdk {
    clientId = BuildConfig.CLIENT_ID
    clientSecret = BuildConfig.CLIENT_SECRET
}
```

## 测试

```kotlin
// UserViewModelTest.kt
@Test
fun testLoadUser() = runTest {
    val repository = UserRepository("test_id", "test_secret")
    val viewModel = UserViewModel(repository)
    
    viewModel.loadUser("testuser")
    
    val state = viewModel.uiState.value
    assertTrue(state is UiState.Success)
}
```

## 更多信息

- [认证文档](../../docs/authentication.md)
- [JVM 示例](../jvm/README.md) - 包含完整的 API 调用示例
- [API 参考](../../docs/API_REFERENCE.md)

## 许可证

MIT License
