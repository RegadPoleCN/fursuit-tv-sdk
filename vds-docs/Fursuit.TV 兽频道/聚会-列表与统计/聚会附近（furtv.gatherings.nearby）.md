
# 聚会附近（furtv.gatherings.nearby）

返回时间窗口内可展示定位的聚会坐标。

认证与端点统一说明请参考根目录文档：`认证方式与服务器端点`。

## 请求地址

- `GET /api/proxy/furtv/gatherings/nearby`
- 权限节点：`furtv.gatherings.nearby`

## 成功响应示例

```json
{
  "success": true,
  "data": [
    {
      "id": 1001,
      "title": "南京兽聚",
      "event_date": "2026-04-02",
      "end_date": "2026-04-03",
      "address": "江苏省南京市鼓楼区",
      "city": "南京",
      "lat": 32.0617,
      "lng": 118.7778,
      "badges": [
        {
          "code": "furtv_coop_driven",
          "title": "由兽频道合作驱动的兽聚"
        }
      ],
      "is_furtv_coop_driven": true
    }
  ],
  "requestId": "62a0f3b3-a7fc-4f73-b3aa-3100f2f9e6bc"
}
```
