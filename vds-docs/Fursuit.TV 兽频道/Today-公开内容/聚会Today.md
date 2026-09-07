
# 聚会 Today

分页查询指定聚会下的公开 Today 社区内容。

权限节点：`furtv.today.gatherings`

## 请求头

- `X-Api-Key: <apiKey>`

## 请求地址

- `GET /api/proxy/furtv/today/gatherings/:gatheringId`

## 路径参数

- `gatheringId`：聚会 ID

## 查询参数

- `before_today_id` / `before`：可选，上一页游标，可以是 Today ID 或 UUID
- `limit`：可选，返回数量，上游最大值为 30

## 成功响应示例

```json

```
