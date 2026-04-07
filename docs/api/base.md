# 基础 API

基础 API 包含一些通用的接口，如健康检查、版本信息等。

## 方法列表

### `health()`

健康检查接口，用于验证 API 服务是否正常。

**返回类型**: `HealthData`

**示例**:

```kotlin
val healthStatus = sdk.base.health()
println("服务状态: ${healthStatus.status}")
println("服务版本: ${healthStatus.version}")
```

### `getAndroidVersion()`

获取 Android 版本信息。

**返回类型**: `AndroidVersionData`

**示例**:

```kotlin
val versionInfo = sdk.base.getAndroidVersion()
println("最新版本: ${versionInfo.version}")
println("版本代码: ${versionInfo.versionCode}")
println("下载链接: ${versionInfo.downloadUrl}")
```

### `checkAndroidVersion(currentVersion: String, currentVersionCode: Int? = null)`

检查 Android 版本更新。

**参数**:
- `currentVersion`: 当前版本号
- `currentVersionCode`: 当前版本代码（可选）

**返回类型**: `AndroidVersionCheckData`

**示例**:

```kotlin
val updateInfo = sdk.base.checkAndroidVersion("1.0.0", 1)
if (updateInfo.needUpdate) {
    println("需要更新到版本: ${updateInfo.latestVersion.version}")
    if (updateInfo.forceUpdate) {
        println("强制更新")
    }
}
```

### `getThemePacksManifest()`

获取主题包清单。

**返回类型**: `ThemePacksManifestData`

**示例**:

```kotlin
val manifest = sdk.base.getThemePacksManifest()
println("清单版本: ${manifest.version}")
println("主题包数量: ${manifest.packs.size}")

manifest.packs.forEach { pack ->
    println("主题包: ${pack.name}")
    println("下载链接: ${pack.zipUrl}")
}
```

## 数据结构

### HealthData

```kotlin
data class HealthData(
    val status: String,       // 服务状态
    val version: String,      // 服务版本
    val uptime: Long          // 服务运行时间（毫秒）
)
```

### AndroidVersionData

```kotlin
data class AndroidVersionData(
    val version: String,              // 版本号
    val versionCode: Int,             // 版本代码
    val downloadUrl: String,          // 下载链接
    val updateTime: String,           // 更新时间
    val minSupportedVersion: String,  // 最低支持版本
    val changelog: List<String>       // 更新日志
)
```

### AndroidVersionCheckData

```kotlin
data class AndroidVersionCheckData(
    val needUpdate: Boolean,         // 是否需要更新
    val forceUpdate: Boolean,        // 是否强制更新
    val currentVersion: VersionInfo, // 当前版本信息
    val latestVersion: AndroidVersionData // 最新版本信息
)
```

### ThemePacksManifestData

```kotlin
data class ThemePacksManifestData(
    val version: String,   // 清单版本
    val packs: List<ThemePack> // 主题包列表
)

data class ThemePack(
    val id: String,       // 主题包 ID
    val name: String,     // 主题包名称
    val zipUrl: String,   // 下载链接
    val updatedAt: String // 更新时间
)
```
