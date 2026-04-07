
# 用户社交徽章详情（furtv.users.socialbadges.detail）

获取单个社交徽章详情。

认证与端点统一说明请参考根目录文档：`认证方式与服务器端点`。

## 请求地址

- `GET /api/proxy/furtv/users/:username/social-badges/:userBadgeId`
- 权限节点：`furtv.users.socialbadges.detail`

## 路径参数

- `username`：用户名
- `userBadgeId`：用户徽章绑定 ID

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
  "badge": {
    "user_badge_id": 901,
    "badge_id": 18,
    "title": "认证装师",
    "glb_url": "https://example.com/badges/maker.glb",
    "awarded_at": "2026-02-12T06:00:00.000Z",
    "expires_at": null,
    "detail_text": "已通过平台认证"
  },
  "requestId": "2dc2363c-81f4-4f57-bf3c-d51cb687a9b8"
}
```
