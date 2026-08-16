# Fursuit.TV 用户定位上报 API

本文记录兽频道 App 1.2.1 中实时地图位置上报接口的静态分析结果。

分析目标为接口路径、请求结构、签名和调用条件。本文不包含真实 OAuth token、用户位置、共享密钥或运行时 strong key，也未向线上服务发送定位数据。

## 1. 接口结论

上游接口：

```http
POST https://api.fursuit.tv/api/live-map/locations/report/batch
```

如果后端沿用 `/api/proxy/furtv` 代理前缀，对应代理路由为：

```http
POST /api/proxy/furtv/live-map/locations/report/batch
```

构造签名时必须使用上游路径：

```text
/api/live-map/locations/report/batch
```

不能使用代理路径 `/api/proxy/furtv/live-map/locations/report/batch`。

## 2. 调用前置条件

从 App 控制流和提示文本可以确认：

1. 用户必须已经 OAuth 登录，访客身份不能上报位置。
2. 实时地图功能和法律授权必须有效。
3. 当前设备必须是账号的“首选位置源”。
4. 多台设备在线但没有指定首选设备时，App 会停止更新位置。
5. 非首选设备不会继续上报，原首选设备会在切换后停止上报。
6. group 上报需要有效的 `group_uuid` 和群组共享密钥。
7. direct 上报需要有效的 `target_user_id` 和对应的直接共享会话密钥。

## 3. 批量请求体

最外层固定使用 `reports` 数组：

```json
{
  "reports": [
    {
      "target_type": "group",
      "group_uuid": "<group-uuid>",
      "coord_system": "gcj02",
      "client_timestamp": "<UTC ISO-8601>",
      "expires_in_seconds": 600,
      "e2ee": {
        "algorithm": "aes-256-gcm-bootstrap-v1",
        "key_id": "<key-id>",
        "key_fingerprint": "<key-fingerprint>",
        "nonce": "<base64url-nonce>",
        "ciphertext": "<base64url-ciphertext>",
        "auth_tag": "<base64url-auth-tag>",
        "aad": "<associated-data>"
      },
      "ghost_mode": false,
      "is_mainland_china": true,
      "location_index": 0
    }
  ]
}
```

`client_timestamp` 由 App 使用当前时间转换为 UTC ISO-8601 字符串。`expires_in_seconds` 在分析版本中固定为 `600`。

`location_index`、`is_mainland_china` 等字段存在条件分支，在部分请求中可能省略。实现代理时不应擅自把可选值补为 `null`，因为 JSON 原始字节会参与签名。

## 4. 上报目标

### 4.1 群组共享

```json
{
  "target_type": "group",
  "group_uuid": "<group-uuid>"
}
```

群组加密上下文中可见以下标识：

```text
group:
live-map-group:
```

### 4.2 直接共享

```json
{
  "target_type": "direct",
  "target_user_id": 123456
}
```

直接共享加密上下文中可见以下标识：

```text
direct:
live-map-direct:
```

两种目标可以出现在同一个 `reports` 数组中，由服务器分别统计成功和失败数量。

## 5. 加密前的位置数据

真实位置先被组织为内部对象，然后加密后写入外层 `e2ee` 字段：

```json
{
  "location_text": "当前位置",
  "granularity": "precise",
  "client_timestamp": "<UTC ISO-8601>",
  "lat": 31.2304,
  "lng": 121.4737,
  "ghost_mode": false
}
```

支持的位置粒度：

```text
precise
district
city
province
country
```

坐标系字符串包括：

```text
gcj02
wgs84
```

代码会检查纬度、经度有效范围，并根据中国大陆位置处理 GCJ-02/WGS-84 转换。不要在最终 report 中额外发送明文 `lat`、`lng`；分析版本将位置明文放入 AES-256-GCM 加密内容。

## 6. E2EE 对象

加密对象为 `Map<String, String>`，字段如下：

| 字段 | 含义 |
| --- | --- |
| `algorithm` | 算法版本，当前为 `aes-256-gcm-bootstrap-v1` |
| `key_id` | 共享密钥标识 |
| `key_fingerprint` | 密钥指纹 |
| `nonce` | AES-GCM nonce，使用 URL-safe Base64 表示 |
| `ciphertext` | 加密后的位置数据 |
| `auth_tag` | GCM 认证标签 |
| `aad` | Additional Authenticated Data |

生成 `e2ee` 依赖服务器下发的群组或直接共享密钥。只有接口 URL、OAuth token 和签名头不足以构造有效的位置上报。

## 7. 通用认证与签名

该接口不使用 `X-Api-Key`。它需要 OAuth：

```http
Authorization: Bearer <oauth-access-token>
```

同时需要通用 API 签名头：

```http
x-api-ts: <unix-seconds>
x-api-userid: <actual-user-id>
x-api-params: <base64url-canonical-without-padding>
x-api-sign: <legacy-signature>
x-strong-key-id: <rolling-key-id>
x-strong-sign: <strong-signature>
```

POST 请求的 canonical 为：

```text
POST
/api/live-map/locations/report/batch
SHA256_HEX(rawJsonBodyBytes)
```

第一层签名：

```text
x-api-params = BASE64URL_NO_PADDING(UTF8(canonical))
x-api-sign   = SHA256_HEX(UTF8(canonical + x-api-ts + "fursuittv"))
```

第二层签名：

```text
x-strong-sign = SHA256_HEX(
  UTF8(
    canonical + "\n" +
    x-api-ts + "\n" +
    userId + "\n" +
    keyId + "\n" +
    rollingKey + "\n" +
    "fursuittv"
  )
)
```

strong key 通过以下接口获取并按有效期缓存：

```http
GET /api/security/strong-signature/public-key?_frt_now=<seconds>&_frt_nonce=<microseconds>
```

OAuth 模式必须在 `x-api-userid` 和 strong 签名原始串中使用实际用户 ID，不能使用访客值 `0`。

## 8. Live-map 附加签名

所有 `/api/live-map` 请求还会增加一组专用头：

```http
x-frt-ts: <decimal-timestamp>
x-frt-nonce: <32-character-lowercase-hex>
x-frt-sign: <hmac-sha256-hex>
```

`x-frt-nonce` 由 32 个随机十六进制字符组成。

附加签名 canonical：

```text
METHOD + "\n" +
PATH + "\n" +
x-frt-ts + "\n" +
x-frt-nonce + "\n" +
SHA256_HEX(rawBodyBytes)
```

本接口对应：

```text
POST
/api/live-map/locations/report/batch
<x-frt-ts>
<x-frt-nonce>
<SHA256 of the exact JSON body bytes>
```

最终计算：

```text
x-frt-sign = HMAC_SHA256_HEX(embeddedLiveMapKey, frtCanonical)
```

App 内嵌 HMAC key：

```text
b8QV4WWJhzfWTgdwrGoGjCF7tuHpd96xAb7sMYmMtDCNkQexhkQ9Wx3D2iFwJfmadhYVvkBuGcXVptc7PecGeQpoDJwz93vdNxJi
```

该值可在 `pp.txt` 第 17109 行定位。它属于客户端可提取常量，不应被后端当作可以保密的长期服务端凭据；如用于生产环境，应支持版本化和轮换。

静态分析确认 `x-frt-ts` 会先转换为十进制字符串，但当前反编译结果未完整恢复调用方的时间单位。正式复刻前应通过已登录测试设备抓包确认其为秒还是毫秒，不能直接与 `x-api-ts` 混用。

## 9. JSON 字节一致性

同一份原始 JSON 字节同时影响：

1. 通用 canonical 中的 body hash。
2. `x-api-sign`。
3. `x-strong-sign`。
4. live-map canonical 中的 body hash。
5. `x-frt-sign`。

因此必须先完成 JSON 序列化，再基于同一字节数组计算全部签名并发送。签名后不能重新格式化 JSON、调整键顺序、改变转义方式或增加换行。

## 10. 响应结构

反编译模型中可见以下响应字段：

```json
{
  "success": true,
  "reported_count": 1,
  "failed_count": 0,
  "message": null
}
```

批量请求可能出现部分成功，应同时检查 `reported_count` 和 `failed_count`，不能只检查 HTTP 状态码或 `success`。

## 11. 建议的后端流程

1. 验证当前 OAuth 用户和用户 ID。
2. 验证当前设备会话是否为首选位置源。
3. 获取并缓存 rolling strong key。
4. 读取 group/direct 共享关系和对应 E2EE 密钥。
5. 构造内部位置对象并生成 AES-256-GCM `e2ee` 对象。
6. 生成完整 `reports` 请求体，并且只序列化一次。
7. 使用最终 JSON 字节计算通用 body hash 和 `x-frt` body hash。
8. 计算两层通用签名和 live-map HMAC 签名。
9. 发送 POST 请求并检查批量计数。
10. 收到 `403` 时只刷新 strong key 并最多重试一次，不要无限重放位置请求。

## 12. 分析依据

| 内容 | 位置 |
| --- | --- |
| 批量上报路径 | [pp.txt](./pp.txt) 第 169 行 |
| `reported_count` / `failed_count` | [pp.txt](./pp.txt) 第 158、162 行 |
| 位置及外层 report 字段 | [pp.txt](./pp.txt) 第 832-845 行 |
| AES-GCM E2EE 字段 | [pp.txt](./pp.txt) 第 875-884 行 |
| group/direct 标识 | [pp.txt](./pp.txt) 第 942-946 行 |
| `x-frt-*` 头及 HMAC key | [pp.txt](./pp.txt) 第 17106-17111 行 |
| 实时地图调用日志 | [pp.txt](./pp.txt) 第 50014-50017 行 |
| 首选位置源限制 | [pp.txt](./pp.txt) 第 68354-68357 行 |
| HTTP 请求函数 | [libapp.so](./libapp.so) `0x9cc918` |
| group report 序列化 | [libapp.so](./libapp.so) `0x9ccc58` |
| direct report 序列化 | [libapp.so](./libapp.so) `0x9cd8f4` |
| live-map 附加签名 | [libapp.so](./libapp.so) `0x994488` |
| nonce 生成函数 | [libapp.so](./libapp.so) `0x994c24` |

## 13. 验证状态

- 已静态确认接口路径、HTTP 方法、请求外层结构和响应计数字段。
- 已静态确认 group/direct 两类 report 序列化路径。
- 已静态确认 AES-256-GCM E2EE 字段和 live-map HMAC canonical 顺序。
- 未使用真实账号发送定位上报。
- `x-frt-ts` 的时间单位和 E2EE AAD 的精确拼接值仍需通过授权测试环境动态确认。
