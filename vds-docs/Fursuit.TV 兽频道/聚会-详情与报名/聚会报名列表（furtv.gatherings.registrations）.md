
# 聚会报名列表

**初步测试返回值有大量Buffer字段，不通过**

获取聚会通过审核的报名列表。

权限节点：`furtv.gatherings.registrations`

## 请求头

- `X-Api-Key: <apiKey>`

## 请求地址

- `GET /api/proxy/furtv/gatherings/:id/registrations`

## 路径参数

- `id`：聚会 ID

## 成功响应示例

```json
{
  "success": true,
  "registrations": [
    [
      {
        "id": 3001,
        "status": "approved",
        "registration_time": "2026-03-10T09:20:00.000Z",
        "checked_in": 0,
        "user_id": 18,
        "username": "fox_demo",
        "nickname": "狐狐",
        "avatar_url": "https://example.com/u1.jpg",
        "fursuit_species": "狐"
      }
    ],
    []
  ],
  "requestId": "a88715f9-77ea-4a37-ad9c-f2f7d30b6f8b"
}
```

## 说明

- 该接口当前透传上游原始查询结果结构，`registrations[0]` 为报名行数据数组。
