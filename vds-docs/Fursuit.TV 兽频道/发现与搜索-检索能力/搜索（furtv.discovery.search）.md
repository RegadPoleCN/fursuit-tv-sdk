
# 搜索（furtv.discovery.search）

按关键词搜索公开档案。

认证与端点统一说明请参考根目录文档：`认证方式与服务器端点`。

## 请求地址

- `GET /api/proxy/furtv/search`
- 权限节点：`furtv.discovery.search`

## 查询参数

- `q`：必填，搜索关键词
- `type`：可选，默认 `all`，支持 `all` / `interest` / `destination` / `location`
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
      "location": "江苏 南京",
      "destinations": [],
      "destination": null,
      "destination_expires_at": null,
      "introduction": "嗷呜~",
      "view_count": 1824,
      "is_verified": true,
      "created_at": "2025-12-01T08:00:00.000Z"
    }
  ],
  "search_type": "general",
  "search_keywords": ["狐"],
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
  "requestId": "1e81a5e8-501f-42bc-8e19-e2ddceaf0f8f"
}
```
