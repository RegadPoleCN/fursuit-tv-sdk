# 基础 API (Base)

基础接口提供健康检查、版本管理、主题包等功能。

## API 方法

### helloWorld()

请求验证接口

- **端点**: `GET /api/proxy/base/hello-world`
- **返回**: `HelloWorldResponse`（success, message, verify, appId, requestId）

### health()

健康检查

- **端点**: `GET /api/proxy/furtv/health`
- **返回**: `HealthResponse`（success, message, timestamp, requestId）

### getAndroidVersion()

获取 Android 版本

- **端点**: `GET /api/proxy/furtv/version/android`
- **返回**: `AndroidVersionData`（version, versionCode, description, forceUpdate, downloadUrl, updateTime, minSupportedVersion, changelog）

### checkAndroidVersion(currentVersion, currentVersionCode)

检查版本更新

- **端点**: `POST /api/proxy/furtv/version/android/check`
- **参数**:
  - `currentVersion` (String): 当前版本
  - `currentVersionCode` (Int?, 可选): 当前版本号
- **返回**: `AndroidVersionCheckData`（needUpdate, forceUpdate, currentVersion, latestVersion）

### getThemePacksManifest()

获取主题包清单

- **端点**: `GET /api/proxy/furtv/theme-packs/manifest`
- **返回**: `ThemePacksManifestData`（version, packs[]）

## 数据模型

### HelloWorldResponse

| 字段 | 类型 | 说明 |
|------|------|------|
| success | Boolean | 请求是否成功 |
| message | String | 响应消息 |
| verify | String | 验证状态 |
| appId | String | 应用 ID |
| requestId | String | 请求 ID |

### HealthResponse

| 字段 | 类型 | 说明 |
|------|------|------|
| success | Boolean | 请求是否成功 |
| message | String | 健康状态消息 |
| timestamp | String | 时间戳 |
| requestId | String | 请求 ID |

### AndroidVersionData

| 字段 | 类型 | 说明 |
|------|------|------|
| version | String | 版本号 |
| versionCode | Int | 版本代码 |
| description | String | 版本描述 |
| forceUpdate | Boolean | 是否强制更新 |
| downloadUrl | String | 下载链接 |
| updateTime | String | 更新时间 |
| minSupportedVersion | String | 最低支持版本 |
| changelog | List\<String\> | 更新日志列表 |

### AndroidVersionCheckData

| 字段 | 类型 | 说明 |
|------|------|------|
| needUpdate | Boolean | 是否需要更新 |
| forceUpdate | Boolean | 是否强制更新 |
| currentVersion | VersionInfo | 当前版本信息 |
| latestVersion | AndroidVersionData | 最新版本信息 |

### VersionInfo

| 字段 | 类型 | 说明 |
|------|------|------|
| version | String | 版本字符串 |
| versionCode | Int | 版本代码 |

### ThemePacksManifestData

| 字段 | 类型 | 说明 |
|------|------|------|
| version | String | 主题包版本 |
| packs | List\<ThemePack\> | 主题包列表 |

### ThemePack

| 字段 | 类型 | 说明 |
|------|------|------|
| id | String | 主题包 ID |
| name | String | 主题包名称 |
| zipUrl | String | 下载链接 |
| updatedAt | String | 更新时间 |

## 相关文档

- [错误处理](../error-handling.md)
