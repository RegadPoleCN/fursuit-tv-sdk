
# 话题 Today

分页查询指定话题圈下的公开 Today。

权限节点：`furtv.today.topics`

## 请求头

- `X-Api-Key: <apiKey>`

## 请求地址

- `GET /api/proxy/furtv/today/topics/:topicIdentifier`

## 路径参数

- `topicIdentifier`：话题 ID 或 slug

## 查询参数

- `before_today_id` / `before`：可选，上一页游标，可以是 Today ID 或 UUID
- `limit`：可选，返回数量，上游最大值为 30

## 成功响应示例

```json

```
