# Fursuit.TV SDK JavaScript/TypeScript 示例

本示例项目演示如何在浏览器和 Node.js 环境中使用 Fursuit.TV SDK。

> Kotlin/JS 示例已移至 [../kotlin-js/](../kotlin-js/README.md)

## 目录结构

```
examples/js/
├── package.json
├── README.md
├── src/
│   ├── browser/
│   │   ├── index.html         # 浏览器 HTML 入口
│   │   └── app.ts             # 浏览器 TypeScript 示例
│   └── node/
│       └── index.ts           # Node.js TypeScript 示例
```

## 前置要求

- Node.js 16.0.0 或更高版本
- npm 或 yarn
- Fursuit.TV 开发者账号

## 快速开始

### 1. 安装依赖

```bash
npm install
```

### 2. 配置凭证

编辑对应示例文件，替换以下占位符：

- `"vap_xxxxxxxxxxxxxxxx"` — 替换为你的 clientId（即 VDS 文档中的 appId）
- `"your-client-secret-here"` — 替换为你的 clientSecret

### 3. 运行示例

#### Node.js TypeScript 示例

```bash
npm run dev:node
```

#### 浏览器 TypeScript 示例

```bash
npm run dev:browser
```

## 纯 JavaScript/TypeScript 调用

### 初始化 SDK

```typescript
import { fursuitTvSdk, SdkLogLevel } from "@regadpole/fursuit-tv-sdk";

const sdk = await fursuitTvSdk((config) => {
  config.clientId = "vap_xxxxxxxxxxxxxxxx";
  config.clientSecret = "your-client-secret-here";
  config.logLevel = SdkLogLevel.INFO;
});
```

### API 调用

```typescript
const health = await sdk.base.health();
const profile = await sdk.user.getUserProfile("username");
const popular = await sdk.search.getPopular();
```

### 错误处理

```typescript
try {
  const profile = await sdk.user.getUserProfile("username");
} catch (error) {
  if (error instanceof Error) {
    console.error(error.message);
  }
} finally {
  sdk.close();
}
```

## 注意事项

### 1. ESM 模块

SDK 以 ES Module (ESM) 格式发布，项目 `package.json` 中需设置 `"type": "module"`。

### 2. 异步调用

所有 API 方法均为异步函数，返回 `Promise`，需使用 `await` 调用。

### 3. 资源释放

使用完毕后必须调用 `sdk.close()` 释放资源。

### 4. 浏览器环境

浏览器示例需要通过打包工具（如 Vite、Webpack）处理 TypeScript 和 SDK 依赖。

### 5. Kotlin List 访问

SDK 返回的列表类型为 Kotlin `List`，在 Node.js 中需使用 `.asJsReadonlyArrayView()` 转换后访问 `.length` 等属性；浏览器中可直接使用 `.length`。

## 更多信息

- [认证文档](../../docs/authentication.md)
- [Kotlin/JS 示例](../kotlin-js/README.md)
- [Java 示例](../java/README.md)
- [JVM 示例](../jvm/README.md)
- [平台指南](../../docs/platform-guide.md)

## 许可证

MIT License
