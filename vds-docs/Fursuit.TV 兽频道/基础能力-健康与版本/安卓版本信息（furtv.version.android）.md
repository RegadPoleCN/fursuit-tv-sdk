
# 安卓版本信息（furtv.version.android）

获取安卓端最新版本信息。

认证与端点统一说明请参考根目录文档：`认证方式与服务器端点`。

## 请求地址

- `GET /api/proxy/furtv/version/android`
- 权限节点：`furtv.version.android`

## 成功响应示例

```json
{
  "success": true,
  "data": {
    "version": "2.4.1",
    "versionCode": 241,
    "description": "修复若干已知问题并优化性能",
    "forceUpdate": false,
    "downloadUrl": "https://example.com/furtv/android-latest.apk",
    "updateTime": "2026-03-19T08:30:00.000Z",
    "minSupportedVersion": "2.2.0",
    "changelog": ["优化首页加载", "修复已知崩溃"]
  },
  "requestId": "cd34f4ba-8f8f-4569-b547-446f6640b267"
}
```
