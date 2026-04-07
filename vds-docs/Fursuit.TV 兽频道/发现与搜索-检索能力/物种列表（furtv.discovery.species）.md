
# 物种列表（furtv.discovery.species）

获取公开档案中的物种统计。

认证与端点统一说明请参考根目录文档：`认证方式与服务器端点`。

## 请求地址

- `GET /api/proxy/furtv/species`
- 权限节点：`furtv.discovery.species`

## 成功响应示例

```json
{
  "success": true,
  "species": [
    {
      "species": "狐",
      "count": 128
    },
    {
      "species": "狼",
      "count": 91
    }
  ],
  "total": 324,
  "requestId": "e9c5e5f0-cbf2-4618-977a-5e663c76737a"
}
```
