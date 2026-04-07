
# 聚会报名列表（furtv.gatherings.registrations）

获取聚会通过审核的报名列表。

认证与端点统一说明请参考根目录文档：`认证方式与服务器端点`。

## 请求地址

- `GET /api/proxy/furtv/gatherings/:id/registrations`
- 权限节点：`furtv.gatherings.registrations`

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
