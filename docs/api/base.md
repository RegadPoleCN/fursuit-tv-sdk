# 基础 API (Base)

基础接口提供健康检查、版本管理、主题包等功能。

## API 方法

### helloWorld()

请求验证接口

- **端点**: `GET /api/proxy/base/hello-world`
- **返回**: `HelloWorldResponse`（message, timestamp, appId, requestId）

### health()

健康检查

- **端点**: `GET /api/proxy/furtv/health`
- **返回**: `HealthResponse`（status, timestamp, requestId）

### getAndroidVersion()

获取 Android 版本

- **端点**: `GET /api/proxy/furtv/version/android`
- **返回**: `AndroidVersionData`（version, versionCode, downloadUrl, releaseNotes）

### checkAndroidVersion(currentVersion, currentVersionCode)

检查版本更新

- **端点**: `POST /api/proxy/furtv/version/android/check`
- **参数**:
  - `currentVersion` (String): 当前版本
  - `currentVersionCode` (Int?, 可选): 当前版本号
- **返回**: `AndroidVersionCheckData`（needUpdate, forceUpdate, version）

### getThemePacksManifest()

获取主题包清单

- **端点**: `GET /api/proxy/furtv/theme-packs/manifest`
- **返回**: `ThemePacksManifestData`（version, packs[]）

## 数据模型

### HelloWorldResponse

| 字段 | 类型 | 说明 |
|------|------|------|
| message | String | 欢迎消息 |
| timestamp | Long | 时间戳 |
| appId | String | 应用 ID |
| requestId | String | 请求 ID |

### HealthResponse

| 字段 | 类型 | 说明 |
|------|------|------|
| status | String | 服务状态 |
| timestamp | Long | 时间戳 |
| requestId | String | 请求 ID |

### AndroidVersionData

| 字段 | 类型 | 说明 |
|------|------|------|
| version | String | 版本号 |
| versionCode | Int | 版本代码 |
| downloadUrl | String | 下载链接 |
| releaseNotes | String | 更新日志 |

### ThemePacksManifestData

| 字段 | 类型 | 说明 |
|------|------|------|
| version | String | 主题包版本 |
| packs | List | 主题包列表 |

## 相关文档

- [错误处理](../error-handling.md)
