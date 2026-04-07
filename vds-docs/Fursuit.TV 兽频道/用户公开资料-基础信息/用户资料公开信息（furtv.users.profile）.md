
# 用户资料公开信息（furtv.users.profile）

按用户名获取公开资料页面数据。

认证与端点统一说明请参考根目录文档：`认证方式与服务器端点`。

## 请求地址

- `GET /api/proxy/furtv/users/:username`
- 权限节点：`furtv.users.profile`

## 路径参数

- `username`：用户名

## 成功响应示例

```json
{
  "success": true,
  "user": {
    "id": 1024,
    "username": "fox_demo",
    "nickname": "狐狐",
    "avatar_url": "https://example.com/avatar-1024.jpg",
    "fursuit_species": "狐",
    "fursuit_birthday": "2022-06-01",
    "fursuit_maker": "Maker A",
    "showcase_portrait": "https://example.com/portrait.jpg",
    "showcase_landscape": "https://example.com/landscape.jpg",
    "showcase_other": "https://example.com/other.jpg",
    "introduction": "嗷呜~",
    "interests": ["摄影", "线下聚会"],
    "location": "江苏 南京",
    "social_links": {
      "weibo": "https://weibo.com/example"
    },
    "contact_info": {
      "qq": "12345678"
    },
    "privacy_settings": {
      "showEmail": false,
      "allowContact": true,
      "showLocation": true,
      "allow_messages": true,
      "allow_return_images": true,
      "profile_public": true,
      "show_visitor_details": true
    },
    "characters": [],
    "other_verified_types": [],
    "view_count": 1824,
    "is_verified": true,
    "created_at": "2025-12-01T08:00:00.000Z",
    "destinations": [
      {
        "id": 98,
        "name": "南京兽聚",
        "start_date": "2026-04-02",
        "end_date": "2026-04-03",
        "gathering_id": 1001
      }
    ],
    "destination": "南京兽聚",
    "destination_expires_at": "2026-04-03"
  },
  "requestId": "6a1e2637-19a5-48fd-acf1-16275f300994"
}
```
