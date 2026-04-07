
# 主题包清单（furtv.themepacks.manifest）

获取主题包 manifest 清单。

认证与端点统一说明请参考根目录文档：`认证方式与服务器端点`。

## 请求地址

- `GET /api/proxy/furtv/theme-packs/manifest`
- 权限节点：`furtv.themepacks.manifest`

## 成功响应示例

```json
{
  "success": true,
  "data": {
    "version": "2026.03.21",
    "packs": [
      {
        "id": "spring-night",
        "name": "Spring Night",
        "zip_url": "https://example.com/theme-packs/spring-night.zip",
        "updated_at": "2026-03-21T01:10:00.000Z"
      }
    ]
  },
  "requestId": "9c3897fe-e457-4b0e-a2dc-b2ded0fb5485"
}
```
