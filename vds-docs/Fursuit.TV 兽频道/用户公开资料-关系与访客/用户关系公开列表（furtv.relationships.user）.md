
# 用户关系公开列表（furtv.relationships.user）

获取指定用户已建立关系的公开列表。

认证与端点统一说明请参考根目录文档：`认证方式与服务器端点`。

## 请求地址

- `GET /api/proxy/furtv/relationships/user/:userId`
- 权限节点：`furtv.relationships.user`

## 路径参数

- `userId`：用户 ID

## 成功响应示例

```json
{
  "relationships": [
    {
      "id": 31,
      "relationship_type": "couple",
      "created_at": "2026-03-10T08:12:00.000Z",
      "partner_id": 1024,
      "partner_username": "fox_demo",
      "partner_nickname": "狐狐",
      "partner_avatar": "https://example.com/avatar-1024.jpg",
      "partner_species": "狐"
    }
  ],
  "requestId": "63f0fe8e-8f43-495f-9304-9759b538df9d"
}
```
