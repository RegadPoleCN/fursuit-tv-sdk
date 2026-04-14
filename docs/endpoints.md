# 服务器端点

本文档说明 Fursuit.TV SDK 使用的 VDS 平台服务器端点。

## 默认端点

SDK 默认使用 VDS 官方推荐的全球端点：

```
https://open-global.vdsentnet.com
```

## 可选端点

| 端点 | 地区 | 服务提供商 |
|------|------|------------|
| `https://open-global.vdsentnet.com` | 全球 | 默认推荐，节点优选自动匹配 |
| `https://open-cn1.vdsentnet.com` | 中国上海 | Harry |

## 使用方式

### 使用默认端点

```kotlin
// 无需指定 baseUrl，使用默认值
val sdk = FursuitTvSdk(apiKey = "your-api-key")
```

### 自定义端点

```kotlin
// 使用 Builder 模式自定义端点
val config = SdkConfig.builder()
    .apiKey("your-api-key")
    .baseUrl("https://open-cn1.vdsentnet.com")  // 选择更适合的端点
    .build()
val sdk = FursuitTvSdk(config)
```

## 认证方式

所有端点都支持以下两种认证方式：

1. `X-Api-Key: <apiKey>`
2. `Authorization: Bearer <accessToken>`

**说明**：
- 两者二选一
- 同时传入时，服务端优先使用 `X-Api-Key`

## 相关文档

- [认证方式与服务器端点](../vds-docs/认证方式与服务器端点.md)
- [配置指南](configuration.md)
