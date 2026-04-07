
# 请求验证接口（HelloWorld）

这个接口用于快速验证：

- 请求链路是否正常
- 接口网关是否可用
- `requestId` 是否正常返回

认证与端点统一说明请参考根目录文档：`认证方式与服务器端点`。

## 请求信息

- 方法：`GET`
- 路径：`/api/proxy/base/hello-world`

## 成功响应示例

```json
{
  "success": true,
  "message": "helloworld",
  "verify": "request_normal",
  "appId": "vap_xxxxxxxxxxxxxxxx",
  "requestId": "6ff8d966-b3f6-46a6-9fe3-24fd6553ef52"
}
```

## 说明

- 响应头会带 `x-request-id`
- 响应体会带 `requestId`
- 该接口属于基础接口，默认所有已签名应用可调用
