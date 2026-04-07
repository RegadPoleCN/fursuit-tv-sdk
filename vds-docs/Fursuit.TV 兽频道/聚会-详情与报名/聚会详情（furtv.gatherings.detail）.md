
# 聚会详情（furtv.gatherings.detail）

获取单个聚会的完整详情。

认证与端点统一说明请参考根目录文档：`认证方式与服务器端点`。

## 请求地址

- `GET /api/proxy/furtv/gatherings/:id`
- 权限节点：`furtv.gatherings.detail`

## 路径参数

- `id`：聚会 ID

## 成功响应示例

```json
{
  "success": true,
  "gathering": {
    "id": 1001,
    "title": "南京兽聚",
    "description": "春季线下交流活动",
    "event_date": "2026-04-02",
    "end_date": "2026-04-03",
    "event_time": "14:00:00",
    "end_time": "18:00:00",
    "type": "聚会",
    "type_class": "micro",
    "type_display": "小聚",
    "status": "published",
    "location_public": "江苏省南京市鼓楼区",
    "location_city": "南京",
    "location_lat": 32.0617,
    "location_lng": 118.7778,
    "logo_url": "https://example.com/g1.jpg",
    "banner_url": "https://example.com/g1-banner.jpg",
    "organizer_id": 18,
    "organizer_username": "fox_org",
    "organizer_nickname": "狐狐主办",
    "organizer_avatar": "https://example.com/u1.jpg",
    "co_organizers": [],
    "agenda": [],
    "tags": ["线下", "南京"],
    "source_count": 1,
    "data_sources": [
      {
        "source_code": "official",
        "source_url": "https://example.com/gathering/1001",
        "name": "官方",
        "logo_url": "https://example.com/logo.png"
      }
    ],
    "badges": [],
    "is_furtv_coop_driven": false,
    "interested_count": 23,
    "is_interested": false,
    "interested_friends": [],
    "going_friends": [],
    "going_friends_count": 0,
    "registration_stats": {
      "total_registrations": 86,
      "approved_count": 80,
      "pending_count": 6
    },
    "view_count": 143
  },
  "requestId": "6862cc95-0a5e-4ec0-a07c-bae493f1d277"
}
```
