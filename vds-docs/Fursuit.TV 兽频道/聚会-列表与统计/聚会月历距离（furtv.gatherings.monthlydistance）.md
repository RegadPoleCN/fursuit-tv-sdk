
# 聚会月历距离（furtv.gatherings.monthlydistance）

按月份返回聚会距离（米）。

认证与端点统一说明请参考根目录文档：`认证方式与服务器端点`。

## 请求地址

- `GET /api/proxy/furtv/gatherings/monthly-distance`
- 权限节点：`furtv.gatherings.monthlydistance`

## 查询参数

- `year`：必填，年份
- `month`：必填，月份（`1-12`）
- `lat`：必填，纬度
- `lng`：必填，经度

## 成功响应示例

```json
{
  "success": true,
  "data": {
    "year": 2026,
    "month": 4,
    "distances": [
      {
        "id": 1001,
        "distance_meters": 1842
      },
      {
        "id": 1008,
        "distance_meters": null
      }
    ]
  },
  "requestId": "3cc35951-ec68-4ed3-a29e-e4ebd0f92563"
}
```
