
# 授权端点（account.sso.authorize）

用于把用户拉起到 VDS 账户授权页，授权成功后回跳并带回 `code`。

## 请求地址

推荐前端直跳：

- `GET https://account.vds.pub/authorize`

开放平台镜像地址（用于统一路由场景）：

- `GET /api/proxy/account/sso/authorize`
- 权限节点：`account.sso.authorize`

## Query 参数（逐项填写）

- `client_id`：应用 ID（`vap_xxxx`）
- `redirect_uri`：回调地址（必须在应用重定向 URL 列表中）
- `response_type`：固定 `code`（当前仅支持该值）
- `scope`：`openid` 或 `profile`（推荐 `profile`）
- `state`：可选，建议生成随机串

## 组装授权链接

可按下面顺序组装：

1. 固定地址：`https://account.vds.pub/authorize`
2. 拼接 `client_id=<你的 vap_xxxx>`
3. 拼接 `redirect_uri=<URL 编码后的回调地址>`
4. 拼接 `response_type=code`（固定）
5. 拼接 `scope=openid` 或 `scope=profile`
6. 可选拼接 `state=<随机串>`

示例：

```text
https://account.vds.pub/authorize?client_id=vap_inzba7z2qbhgsk2y&redirect_uri=https%3A%2F%2Fdsv.pub&response_type=code&scope=openid&state=cS2SxD0tcxj4nxNbFlp95dZBIWDKrbMV
```

## 成功返回

浏览器回跳到：

```text
<redirect_uri>?code=AUTH_CODE&state=YOUR_STATE
```

## 失败返回

浏览器回跳到：

```text
<redirect_uri>?error=invalid_request&error_description=...&state=YOUR_STATE
```
