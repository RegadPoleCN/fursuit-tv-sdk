
# 用户信息端点（account.sso.userinfo）

用于使用 OAuth `access_token` 读取当前用户信息。  
本页只覆盖“拿到用户信息”为止。

## 请求地址

- `GET /api/proxy/account/sso/userinfo`
- 权限节点：`account.sso.userinfo`

## 认证头

- `Authorization: Bearer <你的开放平台签名>`
- `X-OAuth-Access-Token: <上一步换到的 access_token>`

## 请求示例

```http
GET /api/proxy/account/sso/userinfo HTTP/1.1
Authorization: Bearer <VDP_BEARER>
X-OAuth-Access-Token: <OAUTH_ACCESS_TOKEN>
```

## 响应示例

```json
{
  "sub": "10001",
  "name": "示例用户",
  "nickname": "示例用户",
  "username": "example_user",
  "avatar_url": "https://venusercontents-1301106215.cos.ap-nanjing.myqcloud.com/user-uploads/10001/example-avatar.jpg",
  "updated_at": 1774002667,
  "email": "e***@example.com",
  "phone_number": "+86137****8072",
  "iss": "https://account.vds.pub",
  "aud": 35,
  "requestId": "ab7747a5-9601-49ad-ba58-9111592dc3b0"
}
```

## Scope 对应字段

- `openid`：`sub`
- `profile`：`nickname`、`username`、`avatar_url`、`updated_at` 等基础信息

当前对外开放 `openid`、`profile`，推荐使用 `profile`。

## 常见错误

```json
{
  "error": "令牌无效"
}
```
