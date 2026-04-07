
# 健康检查（furtv.health）

用于确认兽频道服务可用。

认证与端点统一说明请参考根目录文档：`认证方式与服务器端点`。

## 请求地址

- `GET /api/proxy/furtv/health`
- 权限节点：`furtv.health`

## 成功响应示例

```json
{
  "success": true,
  "message": "Fursuit.TV API is running",
  "timestamp": "2026-03-21T10:00:00.000Z",
  "requestId": "6afcc971-8ddb-4f85-be39-10667f6d1607"
}
```
