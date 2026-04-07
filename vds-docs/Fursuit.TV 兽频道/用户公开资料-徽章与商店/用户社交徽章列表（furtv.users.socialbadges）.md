
# 用户社交徽章列表（furtv.users.socialbadges）

获取用户社交徽章列表。

认证与端点统一说明请参考根目录文档：`认证方式与服务器端点`。

## 请求地址

- `GET /api/proxy/furtv/users/:username/social-badges`
- 权限节点：`furtv.users.socialbadges`

## 路径参数

- `username`：用户名

## 查询参数

- `limit`：可选，最大 `50`

## 成功响应示例

```json
{
  "success": true,
  "user": {
    "id": 18,
    "username": "fox_demo",
    "nickname": "狐狐"
  },
  "is_owner": false,
  "total_count": 2,
  "badges": [
    {
      "user_badge_id": 901,
      "badge_id": 18,
      "title": "认证装师",
      "glb_url": "https://example.com/badges/maker.glb",
      "awarded_at": "2026-02-12T06:00:00.000Z",
      "expires_at": null,
      "detail_text": "已通过平台认证"
    }
  ],
  "requestId": "a36a338e-b7f6-4b8b-97bf-9742b9727063"
}
```
