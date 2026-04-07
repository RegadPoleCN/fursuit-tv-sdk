
# 安卓版本检查（furtv.version.android.check）

用于检查当前安卓版本是否需要更新。

认证与端点统一说明请参考根目录文档：`认证方式与服务器端点`。

## 请求地址

- `POST /api/proxy/furtv/version/android/check`
- 权限节点：`furtv.version.android.check`

## 请求体参数

- `currentVersion`：当前版本号（如 `2.3.0`）
- `currentVersionCode`：当前版本代码（如 `230`）

请求示例：

```json
{
  "currentVersion": "2.3.0",
  "currentVersionCode": 230
}
```

## 成功响应示例

```json
{
  "success": true,
  "data": {
    "needUpdate": true,
    "forceUpdate": false,
    "currentVersion": {
      "version": "2.3.0",
      "versionCode": 230
    },
    "latestVersion": {
      "version": "2.4.1",
      "versionCode": 241,
      "description": "修复若干已知问题并优化性能",
      "forceUpdate": false,
      "downloadUrl": "https://example.com/furtv/android-latest.apk",
      "updateTime": "2026-03-19T08:30:00.000Z",
      "minSupportedVersion": "2.2.0",
      "changelog": ["优化首页加载", "修复已知崩溃"]
    }
  },
  "requestId": "ef739ad8-3072-4df2-a1f5-a0cf6578bfd0"
}
```
