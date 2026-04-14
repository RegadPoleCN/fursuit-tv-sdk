# 基础 API (Base)

基础接口模块提供 VDS 平台的基础功能访问，包括健康检查、版本管理、主题包等。

## API 方法

### helloWorld()

**请求验证接口** - 验证 API 连通性和请求格式

- **端点**: `GET /api/proxy/base/hello-world`
- **方法**: `suspend fun`
- **参数**: 无
- **返回**: `HelloWorldResponse` - 包含欢迎消息和验证信息
- **响应字段**:
  - `message`: 欢迎消息
  - `timestamp`: 时间戳
  - `appId`: 应用 ID
  - `requestId`: 请求 ID

**示例**:

```kotlin
val response = sdk.base.helloWorld()
println("消息：${response.message}")
println("requestId: ${response.requestId}")
```

### health()

**健康检查** - 检查 Fursuit.TV 服务的可用性

- **端点**: `GET /api/proxy/furtv/health`
- **方法**: `suspend fun`
- **参数**: 无
- **返回**: `HealthResponse` - 包含服务状态
- **响应字段**:
  - `status`: 服务状态消息
  - `timestamp`: 时间戳
  - `requestId`: 请求 ID

**示例**:

```kotlin
val health = sdk.base.health()
println("服务状态：${health.status}")
```

### getAndroidVersion()

**获取 Android 版本信息** - 获取当前最新的 Android 客户端版本

- **端点**: `GET /api/proxy/furtv/version/android`
- **方法**: `suspend fun`
- **参数**: 无
- **返回**: `AndroidVersionData` - 包含版本信息
- **响应字段**:
  - `version`: 版本号
  - `versionCode`: 版本代码
  - `downloadUrl`: 下载链接
  - `releaseNotes`: 更新日志

**示例**:

```kotlin
val version = sdk.base.getAndroidVersion()
println("最新版本：${version.version}")
```

### checkAndroidVersion(currentVersion, currentVersionCode)

**检查 Android 版本更新** - 根据当前版本检查是否需要更新

- **端点**: `POST /api/proxy/furtv/version/android/check`
- **方法**: `suspend fun`
- **参数**:
  - `currentVersion` (String): 当前应用版本
  - `currentVersionCode` (Int?): 当前应用版本号（可选）
- **返回**: `AndroidVersionCheckData` - 包含更新检查信息
- **响应字段**:
  - `needUpdate`: 是否需要更新
  - `forceUpdate`: 是否强制更新
  - `version`: 最新版本信息

**示例**:

```kotlin
val checkResult = sdk.base.checkAndroidVersion("1.0.0", 1)
if (checkResult.needUpdate) {
    println("需要更新到版本：${checkResult.version}")
}
```

### getThemePacksManifest()

**获取主题包清单** - 获取所有可用主题包的列表和下载信息

- **端点**: `GET /api/proxy/furtv/theme-packs/manifest`
- **方法**: `suspend fun`
- **参数**: 无
- **返回**: `ThemePacksManifestData` - 包含主题包列表
- **响应字段**:
  - `version`: 主题包版本
  - `packs`: 主题包列表
    - `name`: 主题包名称
    - `url`: 下载链接
    - `checksum`: 校验和

**示例**:

```kotlin
val manifest = sdk.base.getThemePacksManifest()
manifest.packs.forEach { pack ->
    println("主题包：${pack.name}")
    println("下载链接：${pack.url}")
}
```

## 数据模型

### HelloWorldResponse

```kotlin
public data class HelloWorldResponse(
    public val message: String,
    public val timestamp: Long,
    public val appId: String,
    public val requestId: String
)
```

### HealthResponse

```kotlin
public data class HealthResponse(
    public val status: String,
    public val timestamp: Long,
    public val requestId: String
)
```

### AndroidVersionData

```kotlin
public data class AndroidVersionData(
    public val version: String,
    public val versionCode: Int,
    public val downloadUrl: String,
    public val releaseNotes: String
)
```

### ThemePacksManifestData

```kotlin
public data class ThemePacksManifestData(
    public val version: String,
    public val packs: List<ThemePackInfo>
)

public data class ThemePackInfo(
    public val name: String,
    public val url: String,
    public val checksum: String
)
```

## 使用场景

### 1. 服务健康检查

```kotlin
try {
    val health = sdk.base.health()
    if (health.status == "ok") {
        println("服务正常")
    }
} catch (e: Exception) {
    println("服务异常：${e.message}")
}
```

### 2. 版本检查与更新

```kotlin
val currentVersion = "1.0.0"
val checkResult = sdk.base.checkAndroidVersion(currentVersion)

if (checkResult.forceUpdate) {
    // 强制更新
    println("必须更新到最新版本")
} else if (checkResult.needUpdate) {
    // 建议更新
    println("建议更新到最新版本")
}
```

### 3. 主题包管理

```kotlin
val manifest = sdk.base.getThemePacksManifest()
println("当前主题包版本：${manifest.version}")

manifest.packs.forEach { pack ->
    // 下载主题包
    downloadThemePack(pack.url, pack.checksum)
}
```

## 相关文档

- [健康检查](../../vds-docs/Fursuit.TV 兽频道/基础能力 - 健康与版本/健康检查（furtv.health）.md)
- [Android 版本信息](../../vds-docs/Fursuit.TV 兽频道/基础能力 - 健康与版本/安卓版本信息（furtv.version.android）.md)
- [主题包清单](../../vds-docs/Fursuit.TV 兽频道/基础能力 - 主题资源/主题包清单（furtv.themepacks.manifest）.md)
