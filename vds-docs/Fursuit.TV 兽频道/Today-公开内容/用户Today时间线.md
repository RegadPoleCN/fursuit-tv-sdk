
# 用户 Today 时间线

分页查询指定用户公开的 Today 历史。

权限节点：`furtv.today.users.timeline`

## 请求头

- `X-Api-Key: <apiKey>`

## 请求地址

- `GET /api/proxy/furtv/today/users/:userIdentifier`

## 路径参数

- `userIdentifier`：用户 ID 或用户名

## 查询参数

- `before_today_id` / `before`：可选，上一页游标，可以是 Today ID 或 UUID
- `limit`：可选，返回数量，上游最大值为 30

## 成功响应示例

```json

```
