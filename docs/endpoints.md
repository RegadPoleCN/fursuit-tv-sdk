# 服务器端点

本文档说明 Fursuit.TV SDK 使用的 VDS 平台服务器端点。

## 默认端点

SDK 默认使用 VDS 官方推荐的全球端点：

```
https://open-global.vdsentnet.com
```

**优势**：
- 节点优选自动匹配
- 全球覆盖，智能路由
- 推荐用于大多数应用场景

## 可选端点

根据您所在的地区，可以选择更适合的端点以获得更好的连接性能：

| 端点 | 地区 | 说明 |
|------|------|------|
| `https://open-global.vdsentnet.com` | 全球 | 默认推荐，节点优选自动匹配 |
| `https://open-cn1.vdsentnet.com` | 中国上海 | 适合中国大陆用户 |

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

或者直接在构造函数中指定：

```kotlin
// 在构造函数中直接指定端点
val sdk = FursuitTvSdk(
    apiKey = "your-api-key",
    baseUrl = "https://open-cn1.vdsentnet.com"
)
```

## 端点选择建议

### 选择全球端点（open-global）
- 面向全球用户的应用
- 不确定用户所在地区
- 需要自动故障转移和负载均衡

### 选择地区端点（open-cn1）
- 用户主要集中在中国大陆
- 需要更低的延迟
- 需要符合数据本地化要求

## 端点切换

SDK 支持在运行时切换端点，但建议重新创建 SDK 实例：

```kotlin
// 获取当前配置
val currentConfig = sdk.getConfig()
println("Current base URL: ${currentConfig.baseUrl}")

// 创建使用新端点的新 SDK 实例
val newSdk = FursuitTvSdk(
    apiKey = currentConfig.apiKey,
    baseUrl = "https://open-cn1.vdsentnet.com"
)

// 关闭旧实例
sdk.close()

// 使用新实例
val userProfile = newSdk.user.getUserProfile("username")
```

## 认证方式

所有端点都支持以下两种认证方式：

1. **X-Api-Key**: 用于服务端到服务端的认证
   ```
   X-Api-Key: <apiKey>
   ```

2. **Authorization Bearer**: 用于用户授权的 API 调用
   ```
   Authorization: Bearer <accessToken>
   ```

**注意**：同时传入两种认证头时，服务端优先使用 `X-Api-Key`。

## 相关文档

- [认证方式与服务器端点](../vds-docs/认证方式与服务器端点.md)
- [配置指南](configuration.md)
- [使用示例](examples.md)
