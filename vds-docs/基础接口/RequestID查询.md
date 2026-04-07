
# RequestID 查询

## 返回位置

每次请求都会附带 `requestId`：

- Response Header: `x-request-id`
- JSON 响应体字段: `requestId`

## 查询接口

- `GET /api/vds-auth/request-logs/search?q=<requestId>`
- `GET /api/vds-auth/request-logs/:requestId`

## 说明

- `requestId` 使用 UUID 格式。
- 在接入日志中保留 `requestId` 可以帮助定位问题。
