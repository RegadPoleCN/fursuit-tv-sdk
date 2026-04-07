
# 热门地区（furtv.discovery.locations.popular）

返回省市维度的热门地区统计。

认证与端点统一说明请参考根目录文档：`认证方式与服务器端点`。

## 请求地址

- `GET /api/proxy/furtv/locations/popular`
- 权限节点：`furtv.discovery.locations.popular`

## 成功响应示例

```json
{
  "success": true,
  "popular_provinces": [
    {
      "province": "江苏省",
      "count": 38
    },
    {
      "province": "广东省",
      "count": 31
    }
  ],
  "popular_cities": [
    {
      "province": "江苏",
      "city": "南京",
      "count": 14
    },
    {
      "province": "广东",
      "city": "广州",
      "count": 12
    }
  ],
  "total_users": 324,
  "requestId": "3f6f4837-2f49-48f8-872e-ab651235cf66"
}
```
