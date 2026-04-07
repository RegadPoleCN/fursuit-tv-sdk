
# 聚会附近模式（furtv.gatherings.nearbymode）

返回附近聚会，并附带当前用户“想去/去向”聚会 ID 列表。

认证与端点统一说明请参考根目录文档：`认证方式与服务器端点`。

## 请求地址

- `GET /api/proxy/furtv/gatherings/nearby-mode`
- 权限节点：`furtv.gatherings.nearbymode`

## 成功响应示例

```json
{
  "success": true,
  "data": {
    "gatherings": [
      {
        "id": 1001,
        "title": "南京兽聚",
        "event_date": "2026-04-02",
        "end_date": "2026-04-03",
        "avatar_url": "https://example.com/gathering-logo.jpg",
        "address": "江苏省南京市鼓楼区",
        "city": "南京",
        "lat": 32.0617,
        "lng": 118.7778,
        "badges": [],
        "is_furtv_coop_driven": false
      }
    ],
    "intent_gathering_ids": [1001, 1008]
  },
  "requestId": "857a95e3-0ef8-47b4-be9b-7e7e50eca5cb"
}
```
