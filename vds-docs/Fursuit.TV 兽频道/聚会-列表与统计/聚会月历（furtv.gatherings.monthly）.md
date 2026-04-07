
# 聚会月历（furtv.gatherings.monthly）

按月份返回聚会列表。

认证与端点统一说明请参考根目录文档：`认证方式与服务器端点`。

## 请求地址

- `GET /api/proxy/furtv/gatherings/monthly`
- 权限节点：`furtv.gatherings.monthly`

## 查询参数

- `year`：必填，年份
- `month`：必填，月份（`1-12`）

## 成功响应示例

```json
{
  "success": true,
  "data": {
    "year": 2026,
    "month": 4,
    "gatherings": [
      {
        "id": 1001,
        "title": "南京兽聚",
        "description": "春季线下交流活动",
        "type": "小聚",
        "typeClass": "micro",
        "content_source": "ugc",
        "day": "02",
        "weekday": "周四",
        "time": "14:00",
        "endTime": "18:00",
        "location": "南京 鼓楼区",
        "locationPublic": "江苏省南京市鼓楼区",
        "participants": "86/120",
        "logo": "https://example.com/g1.jpg",
        "status": "published",
        "badges": [],
        "is_furtv_coop_driven": false,
        "sourceCount": 0,
        "initialSource": null,
        "dataSources": [],
        "organizer": "狐狐",
        "organizerAvatar": "https://example.com/u1.jpg",
        "feeType": "free",
        "feeAmount": null,
        "registrationStatus": "open",
        "requiresApproval": false
      }
    ],
    "total": 1
  },
  "requestId": "2f28c35b-24f5-47fe-aaba-7aa16dbf4efc"
}
```
