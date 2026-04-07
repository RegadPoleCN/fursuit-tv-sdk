
# 热门推荐（furtv.discovery.popular）

按热度返回热门档案。

认证与端点统一说明请参考根目录文档：`认证方式与服务器端点`。

## 请求地址

- `GET /api/proxy/furtv/popular`
- 权限节点：`furtv.discovery.popular`

## 查询参数

- `limit`：可选，默认 `10`

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
      "fursuit_species": "狐",
      "fursuit_maker": "Maker A",
      "showcase_portrait": null,
      "introduction": "嗷呜~",
      "view_count": 1824,
      "is_verified": true,
      "like_count": 233,
      "created_at": "2025-12-01T08:00:00.000Z",
      "destination": null,
      "destination_expires_at": null,
      "popularity_score": 2989
    }
  ],
  "requestId": "6b4de084-67e7-47e5-a7ce-bf75976f8ab5"
}
```
