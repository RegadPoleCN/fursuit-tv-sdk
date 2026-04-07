
# 用户访客记录（furtv.users.visitors）

获取指定用户的访客记录。

认证与端点统一说明请参考根目录文档：`认证方式与服务器端点`。

## 请求地址

- `GET /api/proxy/furtv/users/:username/visitors`
- 权限节点：`furtv.users.visitors`

## 路径参数

- `username`：用户名

## 成功响应示例（开启访客详情）

```json
{
  "success": true,
  "visitors": [
    {
      "visitor_id": 66,
      "visitor_username": "wolf_demo",
      "visitor_nickname": "狼狼",
      "visitor_avatar": "https://example.com/avatar-66.jpg",
      "created_at": "2026-03-21T09:20:12.000Z"
    }
  ],
  "isOwner": false,
  "requestId": "d1aaac11-2b06-4f66-a31d-fad9c0f15670"
}
```

## 成功响应示例（未开启访客详情）

```json
{
  "success": true,
  "visitors": [],
  "message": "访客详情未开启",
  "isOwner": false,
  "requestId": "7fc214f6-d0e0-4ae9-aaf3-0f421775d88a"
}
```
