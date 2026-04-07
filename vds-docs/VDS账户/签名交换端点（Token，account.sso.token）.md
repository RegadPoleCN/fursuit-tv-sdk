
# 签名交换端点（account.sso.token）

用于把授权码 `code` 换成 OAuth `access_token`。

## 请求地址

- `POST /api/proxy/account/sso/token`
- 权限节点：`account.sso.token`

## 认证头

- `Authorization: Bearer <你的开放平台签名>`

## 请求体参数

- `grant_type`：固定 `authorization_code`
- `code`：授权阶段返回的授权码
- `redirect_uri`：授权时使用的回调地址（需保持一致）
- `client_id`：应用 ID（`vap_xxxx`）
- `client_secret`：应用 AK

`application/x-www-form-urlencoded` 示例：

```text
grant_type=authorization_code
code=CODE_FROM_CALLBACK
redirect_uri=https%3A%2F%2Fdsv.pub
client_id=vap_inzba7z2qbhgsk2y
client_secret=YOUR_APP_AK
```

## 响应示例

```json
{
  "access_token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJjbGllbnRJZCI6MzUsInVzZXJJZCI6MTAwMDEsInNjb3BlIjoib3BlbmlkIHByb2ZpbGUiLCJ0eXBlIjoib2F1dGgiLCJqdGkiOiIqKioqKiIsImlhdCI6MTc3NDA1NTc3OSwiZXhwIjoxNzc0MDU1Nzg5fQ.<SIGNATURE>",
  "token_type": "Bearer",
  "expires_in": 31556952,
  "refresh_token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJjbGllbnRJZCI6MzUsInVzZXJJZCI6MTAwMDEsInR5cGUiOiJyZWZyZXNoIiwianRpIjoiKioqKioiLCJpYXQiOjE3NzQwNTU3NzksImV4cCI6MTc3NjY0Nzc3OX0.<SIGNATURE>",
  "scope": "openid profile",
  "requestId": "435e1d38-e4b6-4224-a629-8927b81c96cc"
}
```

## 常见错误

```json
{
  "error": "invalid_grant",
  "error_description": "无效的授权码或重定向URI不匹配"
}
```
