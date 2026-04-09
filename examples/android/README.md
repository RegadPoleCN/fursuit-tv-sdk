# Fursuit.TV SDK Android 集成指南

本文档说明如何在 Android 项目中集成 Fursuit.TV SDK。

## 前置要求

- Android Studio Arctic Fox (2020.3.1) 或更高版本
- Android SDK 21+ (Android 5.0)
- Kotlin 1.8+
- Gradle 8.0+

## 步骤 1: 添加依赖

在 `app/build.gradle.kts` 中添加：

```kotlin
android {
    compileSdk = 34
    
    defaultConfig {
        minSdk = 21
        targetSdk = 34
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
    implementation("me.regadpole:fursuit-tv-sdk:1.0-SNAPSHOT")
    
    // Kotlinx Coroutines (Android)
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.10.1")
    
    // Lifecycle (可选，用于 ViewModel)
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.2")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.2")
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

import me.regadpole.furtv.sdk.FursuitTvSdk
import me.regadpole.furtv.sdk.model.UserProfile

class UserRepository(
    private val appId: String,
    private val appSecret: String
) {
    private val sdk = FursuitTvSdk(appId, appSecret)
    
    suspend fun getUserProfile(username: String): UserProfile {
        // 确保令牌有效
        sdk.auth.getValidAccessToken(appId, appSecret)
        
        // 获取用户资料
        return sdk.user.getUserProfile(username)
    }
    
    suspend fun getUserVisitors(username: String): List<String> {
        sdk.auth.getValidAccessToken(appId, appSecret)
        return sdk.user.getUserVisitors(username).visitors
    }
    
    fun close() {
        sdk.close()
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
    private val appId: String,
    private val appSecret: String
) : ViewModel() {
    
    private val userRepository = UserRepository(appId, appSecret)
    
    private val _uiState = MutableStateFlow<UiState<String>>(UiState.Loading)
    val uiState: StateFlow<UiState<String>> = _uiState.asStateFlow()
    
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

## 步骤 5: 在 Activity/Fragment 中使用

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
import androidx.lifecycle.viewmodel.compose.viewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                UserScreen(
                    appId = "vap_xxxxxxxxxxxxxxxx",
                    appSecret = "your-app-secret"
                )
            }
        }
    }
}

@Composable
fun UserScreen(
    appId: String,
    appSecret: String,
    viewModel: UserViewModel = viewModel {
        UserViewModel(appId, appSecret)
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

```kotlin
// OAuthCallbackActivity.kt
package com.example.app

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import me.regadpole.furtv.sdk.auth.OAuthConfig

class OAuthCallbackActivity : ComponentActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // 处理 OAuth 回调
        intent.data?.let { uri ->
            handleOAuthCallback(uri)
        }
    }
    
    private fun handleOAuthCallback(uri: Uri) {
        val code = uri.getQueryParameter("code")
        val state = uri.getQueryParameter("state")
        
        // 使用授权码换取令牌
        // ...
    }
}
```

在 `AndroidManifest.xml` 中配置 Deep Link：

```xml
<activity android:name=".OAuthCallbackActivity">
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

## 步骤 7: 使用 Custom Tabs 打开授权页面

```kotlin
// 使用 AndroidX Browser 库
implementation("androidx.browser:browser:1.6.0")

fun openOAuthPage(context: Context, authorizeUrl: String) {
    val customTabsIntent = CustomTabsIntent.Builder()
        .setShowTitle(true)
        .build()
    
    customTabsIntent.launchUrl(context, Uri.parse(authorizeUrl))
}
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

### 2. 主线程

不要在主线程中调用 API，使用协程：

```kotlin
// ❌ 错误：在主线程调用
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
    private val sdk = FursuitTvSdk(appId, appSecret)
    
    override fun onCleared() {
        super.onCleared()
        sdk.close()  // 释放资源
    }
}
```

### 4. 错误处理

始终处理可能的异常：

```kotlin
try {
    val profile = sdk.user.getUserProfile(username)
} catch (e: NotFoundException) {
    // 用户不存在
} catch (e: AuthenticationException) {
    // 认证失败
} catch (e: NetworkException) {
    // 网络错误
} catch (e: Exception) {
    // 其他错误
}
```

### 5. ProGuard/R8 配置

如果使用代码混淆，添加规则：

```proguard
# Fursuit.TV SDK
-keep class me.regadpole.furtv.sdk.** { *; }
-keepclassmembers class me.regadpole.furtv.sdk.** { *; }
```

## 测试

```kotlin
// UserViewModelTest.kt
@Test
fun testLoadUser() = runTest {
    val viewModel = UserViewModel(appId, appSecret)
    viewModel.loadUser("username")
    
    val state = viewModel.uiState.value
    assertTrue(state is UiState.Success)
}
```

## 更多信息

- [开发者指南](../../docs/DEVELOPER_GUIDE.md)
- [平台指南](../../docs/PLATFORM_GUIDE.md)
- [故障排除](../../docs/TROUBLESHOOTING.md)

## 许可证

MIT License
