
# VDS账户快速接入（OAuth）

本页给出一套从“拉起授权”到“拿到用户信息”的最短流程。  
拿到用户信息后，如何创建本地会话或绑定账号，可按你的业务自行处理。

## 1. 接入前准备

- `client_id`：你的应用 ID（格式 `vap_xxxx`）
- `client_secret`：你的应用 AK（在控制台可查看/刷新）
- `redirect_uri`：必须在应用安全设置的“重定向 URL”里有做填写和设置
- 开放平台签名：调用开放平台接口时，统一使用  
  `Authorization: Bearer <你的开放平台签名>`
- 别忘记在能力中添加 VDS账户SSO ，并在权限管理中开启相应权限。

说明：

- 在 OAuth 语义里：
  - 应用 ID = `client_id`
  - AK = `client_secret`

## 2. Scope 说明

当前对外开放：

- `openid`
- `profile`（推荐）

推荐直接使用 `profile`，可一次拿到更完整的基础用户信息。

## 3. 前端拉起授权

前端把用户跳转到授权链接（`state` 可选，建议生成）。

参数填写说明：

- `client_id`：填你的应用 ID（`vap_xxxx`）
- `redirect_uri`：填你在应用安全设置里配置过的回调地址（需要 URL 编码）
- `response_type`：固定 `code`（当前只支持 `code`）
- `scope`：`openid` 或 `profile`（推荐 `profile`）
- `state`：可选，建议传随机串用于防重放/回跳校验

按上面参数组装后示例：

```text
https://account.vds.pub/authorize?client_id=vap_xxxxxxxxxxxxxxxx&redirect_uri=https%3A%2F%2Fdsv.pub&response_type=code&scope=openid&state=YOUR_STATE
```

参数需要与后端换取 token 时保持一致，尤其是 `redirect_uri`。

## 4. 后端回调处理（拿 token + userinfo）

拿到 `code` 后，后端调用开放平台接口：

1. `POST /api/proxy/account/sso/token` 交换 access token
2. `GET /api/proxy/account/sso/userinfo` 获取用户信息

到这里就完成接入闭环。

## 5. Node.js 快速示例

```js
import axios from "axios";

const VDP_BASE_URL = "https://open-global.vdsentnet.com";
const OAUTH_CLIENT_ID = process.env.OAUTH_CLIENT_ID; // vap_xxxx
const OAUTH_CLIENT_SECRET = process.env.OAUTH_CLIENT_SECRET; // 应用 AK
const VDP_BEARER = process.env.VDP_BEARER; // 开放平台签名（Bearer）
const OAUTH_PROVIDER = "vds_account";

function normalizeOAuthUserInfo(data = {}) {
  return {
    sub: data.sub || null,
    username: data.username || "",
    nickname: data.nickname || data.username || "",
    avatar_url: data.avatar_url || "",
    email: data.email || "",
  };
}

async function handleOAuthCallback(req, res, fallbackRedirect) {
  const { code, redirect_uri } = req.body || {};
  if (!code) return res.status(400).json({ error: "code required" });

  const redirectUri = redirect_uri || fallbackRedirect;

  const params = new URLSearchParams();
  params.append("grant_type", "authorization_code");
  params.append("code", code);
  if (redirectUri) params.append("redirect_uri", redirectUri);
  params.append("client_id", OAUTH_CLIENT_ID);
  params.append("client_secret", OAUTH_CLIENT_SECRET);

  const tokenResponse = await axios.post(
    `${VDP_BASE_URL}/api/proxy/account/sso/token`,
    params,
    {
      headers: {
        Authorization: `Bearer ${VDP_BEARER}`,
        "Content-Type": "application/x-www-form-urlencoded",
      },
      timeout: 10000,
    },
  );

  const accessToken = tokenResponse.data?.access_token;
  if (!accessToken) return res.status(500).json({ error: "token_exchange_failed" });

  const userInfoResponse = await axios.get(
    `${VDP_BASE_URL}/api/proxy/account/sso/userinfo`,
    {
      headers: {
        Authorization: `Bearer ${VDP_BEARER}`,
        "X-OAuth-Access-Token": accessToken,
      },
      timeout: 10000,
    },
  );

  const info = normalizeOAuthUserInfo(userInfoResponse.data);
  return res.json({
    success: true,
    oauth_provider: OAUTH_PROVIDER,
    user: info,
  });
}
```

## 6. 对应接口文档

- `VDS账户/授权端点（account.sso.authorize）`
- `VDS账户/签名交换端点（account.sso.token）`
- `VDS账户/用户信息端点（account.sso.userinfo）`
