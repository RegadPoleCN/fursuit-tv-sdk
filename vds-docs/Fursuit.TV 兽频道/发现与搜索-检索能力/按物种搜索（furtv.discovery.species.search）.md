
# 按物种搜索（furtv.discovery.species.search）

按指定物种查询公开档案。

认证与端点统一说明请参考根目录文档：`认证方式与服务器端点`。

## 请求地址

- `GET /api/proxy/furtv/search/species/:species`
- 权限节点：`furtv.discovery.species.search`

## 路径参数

- `species`：物种名称（如 `狐`）

## 查询参数

- `page`：可选，默认 `1`
- `limit`：可选，默认 `20`
- `cursor`：可选，游标分页

## 成功响应示例

```json
{
  "success": true,
  "users": [
    {
      "id": 1024,
      "username": "fox_demo",
      "nickname": "狐狐",
      "avatar_url": "https://example.com/avatar.jpg",
      "showcase_portrait": null,
      "fursuit_species": "狐",
      "fursuit_maker": "Maker A",
      "introduction": "嗷呜~",
      "view_count": 1824,
      "is_verified": true,
      "created_at": "2025-12-01T08:00:00.000Z"
    }
  ],
  "species": "狐",
  "pagination": {
    "page": 1,
    "limit": 20,
    "total": 1,
    "total_pages": 1,
    "next_cursor": null
  },
  "has_more": false,
  "total": 1,
  "next_cursor": null,
  "requestId": "0f5df7c5-7f4f-4d9c-b7ab-c287259fb74c"
}
```
