
# 用户基础信息ID查询

通过用户 ID 获取公开基础资料。

权限节点：`furtv.users.id`

## 请求头

- `X-Api-Key: <apiKey>`

## 请求地址

- `GET /api/proxy/furtv/users/id/:id`

## 路径参数

- `id`：用户 ID

## 成功响应示例

```json
{
  "success": true,
  "user": {
    "id": 18,
    "username": "fox_demo",
    "nickname": "狐狐",
    "avatar_url": "https://example.com/u1.jpg",
    "fursuit_species": "狐",
    "location": "江苏 南京"
  },
  "requestId": "2c8f7f89-2ee2-4f1d-b5fc-36ec07ac2497"
}
```
