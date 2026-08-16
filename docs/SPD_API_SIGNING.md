# Fursuit.TV API 代理与签名流程

## 1. 开始

App 不使用 `X-Api-Key` 请求头。普通业务请求需要两层签名：

1. `x-api-ts` / `x-api-userid` / `x-api-params` / `x-api-sign`
2. `x-strong-key-id` / `x-strong-sign`

本地代理路径和上游路径的对应关系：

| 用途 | 本地代理路径 | Fursuit.TV 上游路径 | 方法 |
| --- | --- | --- | --- |
| 随机一毛 | `/api/proxy/furtv/fursuit/random` | `/api/fursuit/random` | `GET` |
| 今年聚会总数 | `/api/proxy/furtv/gatherings/stats/this-year` | `/api/gatherings/stats/this-year` | `GET` |

## 2. 固定值

```text
API 域名:       https://api.fursuit.tv
User-Agent:     fursuit_tv_mobile fursuittv/1
签名后缀:      fursuittv
访客用户 ID:    0
GET 空 body:    null
```

`SHA256("null")` 的小写十六进制值：

```text
74234e98afe7498fb5daf1f36ac2d78acc339464f950703b8c019892f982b90b
```

## 3. Canonical 请求串

GET 请求的 canonical 格式：

```text
<HTTP_METHOD>\n<UPSTREAM_PATH_AND_QUERY>\n<SHA256_BODY_HEX>
```

例如随机兽装接口：

```text
GET
/api/fursuit/random
74234e98afe7498fb5daf1f36ac2d78acc339464f950703b8c019892f982b90b
```

注意：

- HTTP 方法使用大写。
- 换行符是单个 `\n`。
- GET 无请求体时按字符串 `null` 计算 body hash，不是空字符串。
- 有 query 时，query 必须包含在 canonical 路径中。

## 4. 第一层签名

### 4.1 `x-api-ts`

当前 Unix 秒级时间戳，以十进制字符串发送。

```bash
timestamp="$(date +%s)"
```

### 4.2 `x-api-params`

```text
x-api-params = Base64URL(UTF8(canonical))
```

需要删除 Base64URL 末尾的 `=` padding。

### 4.3 `x-api-sign`

```text
x-api-sign = SHA256_HEX(
  UTF8(canonical + x-api-ts + "fursuittv")
)
```

canonical、时间戳和 `fursuittv` 之间不额外加换行符。

### 4.4 `x-api-userid`

| 请求类型 | 值 |
| --- | --- |
| 访客业务请求 | `0` |
| OAuth 登录请求 | 实际用户 ID |
| strong key 引导请求 | 空值 |

访客请求如果使用空字符串，强签名会不匹配，服务器返回 `403`。

## 5. 获取滚动 strong key

强签名 key 通过下列接口获取：

```http
GET /api/security/strong-signature/public-key
    ?_frt_now=<Unix秒级时间戳>
    &_frt_nonce=<Unix微秒级时间戳>
```

完整路径示例：

```text
/api/security/strong-signature/public-key?_frt_now=<seconds>&_frt_nonce=<microseconds>
```

该请求使用第一层签名，不携带 `x-strong-*` 请求头。`x-api-userid` 为空值。

响应结构：

```json
{
  "success": true,
  "data": {
    "keyId": "<rolling-key-id>",
    "key": "<rolling-key-value>",
    "keyBits": 64,
    "issuedAtSec": 0,
    "validFromSec": 0,
    "expiresAtSec": 0,
    "graceUntilSec": 0,
    "nextRefreshAtSec": 0,
    "serverNowSec": 0
  }
}
```

后端应缓存 key，并根据 `validFromSec`、`expiresAtSec`、`graceUntilSec` 和 `nextRefreshAtSec` 管理刷新时间。不要把文档或测试时的 key 固化到代码中。

## 6. 第二层强签名

从 strong key 响应中取出 `keyId` 和 `key`。

强签名原始字符串：

```text
canonical + "\n" +
x-api-ts + "\n" +
x-api-userid + "\n" +
keyId + "\n" +
key + "\n" +
"fursuittv"
```

计算请求头：

```text
x-strong-key-id = keyId
x-strong-sign   = SHA256_HEX(UTF8(强签名原始字符串))
```

这不是 HMAC，也没有 RSA/Ed25519 运算，而是按固定顺序拼接后计算 SHA-256。

## 7. 最终请求头

访客请求：

```http
Accept: application/json
User-Agent: fursuit_tv_mobile fursuittv/1
x-api-ts: <timestamp>
x-api-userid: 0
x-api-params: <base64url-canonical>
x-api-sign: <legacy-signature>
x-strong-key-id: <key-id>
x-strong-sign: <strong-signature>
```

OAuth 请求额外添加：

```http
Authorization: Bearer <oauth-access-token>
```

同时必须将 `x-api-userid` 替换为实际用户 ID，并用该 ID 重新计算 `x-strong-sign`。`x-api-userid` 不参与第一层 `x-api-sign` 计算。

## 8. 已验证接口

### 8.1 随机一毛

```http
GET https://api.fursuit.tv/api/fursuit/random
```

Canonical：

```text
GET
/api/fursuit/random
74234e98afe7498fb5daf1f36ac2d78acc339464f950703b8c019892f982b90b
```

固定 `x-api-params`：

```text
R0VUCi9hcGkvZnVyc3VpdC9yYW5kb20KNzQyMzRlOThhZmU3NDk4ZmI1ZGFmMWYzNmFjMmQ3OGFjYzMzOTQ2NGY5NTA3MDNiOGMwMTk4OTJmOTgyYjkwYg
```

响应概要：

```json
{
  "success": true,
  "fursuit": {
    "id": 0,
    "nickname": "<nickname>",
    "fursuit_species": "<species>"
  },
  "count": 1
}
```

### 8.2 今年聚会总数

```http
GET https://api.fursuit.tv/api/gatherings/stats/this-year
```

Canonical：

```text
GET
/api/gatherings/stats/this-year
74234e98afe7498fb5daf1f36ac2d78acc339464f950703b8c019892f982b90b
```

固定 `x-api-params`：

```text
R0VUCi9hcGkvZ2F0aGVyaW5ncy9zdGF0cy90aGlzLXllYXIKNzQyMzRlOThhZmU3NDk4ZmI1ZGFmMWYzNmFjMmQ3OGFjYzMzOTQ2NGY5NTA3MDNiOGMwMTk4OTJmOTgyYjkwYg
```

2026-08-16 实际测试响应：

```json
{
  "success": true,
  "total": 171
}
```
`total` 为本年度聚会总数。

## 10. 代理实现建议

1. 前端只请求本地 `/api/proxy/furtv/...` 路由，不向前端暴露签名过程。
2. 后端把代理路径转换为 Fursuit.TV 上游 `/api/...` 路径后，再构建 canonical。
3. 缓存 strong key，避免每次业务请求都调用 public-key 接口。
4. 每次请求都生成新的 `x-api-ts`、`x-api-sign` 和 `x-strong-sign`，不要缓存最终签名。
5. 如果收到 `403`，强制刷新 strong key 并且最多重试一次。
6. 返回 `468` 时表示服务器要求人机验证，不应无限重试。
7. 返回 `462` 时表示当前账号没有测试版本访问权限。

## 11. 实测结果

2026-08-16 已完成以下实际网络验证：

| 请求 | 身份 | 结果 |
| --- | --- | --- |
| strong key | 引导请求 | `HTTP 200` |
| `/api/fursuit/random` | 访客 `0` | `HTTP 200`, `success: true` |
| `/api/gatherings/stats/this-year` | 访客 `0` | `HTTP 200`, `success: true`, `total: 171` |

未签名、只有第一层签名，或访客用户 ID 使用空值时，业务接口均返回 `HTTP 403`。

