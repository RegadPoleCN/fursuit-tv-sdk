
# Today 信息流

按时间和推荐权重返回公开 Today 信息流。

权限节点：`furtv.today.feed`

## 请求头

- `X-Api-Key: <apiKey>`

## 请求地址

- `GET /api/proxy/furtv/today/feed`

## 查询参数

- `scope`：可选，信息流范围。默认 timeline
- `cursor_date` / `date`：可选，分页日期游标，格式 YYYY-MM-DD
- `cursor_offset` / `offset`：可选，分页偏移
- `limit`：可选，返回数量，上游最大值为 20

## 成功响应示例

```json

```
