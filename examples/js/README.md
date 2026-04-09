# Fursuit.TV SDK JS/Node.js 示例

这个示例项目演示如何在 Node.js 环境中使用 Fursuit.TV SDK。

## ⚠️ 重要说明

**这个示例只能在 Node.js 环境中运行，不支持浏览器环境！**

原因：
- OAuth 回调需要本地 HTTP 服务器
- 浏览器环境有 CORS 限制
- 某些 Node.js 特定的 API

## 前置要求

- Node.js 16.0.0 或更高版本
- npm 或 yarn
- Fursuit.TV 开发者账号

## 安装

```bash
# 安装依赖
npm install

# 或使用 yarn
yarn install
```

## 配置

编辑 `src/index.js`，替换配置：

```javascript
const APP_ID = "vap_xxxxxxxxxxxxxxxx";  // 你的 appId
const APP_SECRET = "your-app-secret";    // 你的 appSecret
```

## 运行

```bash
# 开发模式
npm run dev

# 构建并运行
npm run build
npm start
```

## 使用示例

### 基本使用

```javascript
// 初始化 SDK
const sdk = new FursuitTvSdk({
    appId: APP_ID,
    appSecret: APP_SECRET
});

try {
    // 获取令牌
    const tokenInfo = await sdk.auth.exchangeToken(APP_ID, APP_SECRET);
    
    // 获取用户资料
    const profile = await sdk.user.getUserProfile("username");
    console.log("用户:", profile.displayName);
    
} catch (error) {
    console.error("错误:", error);
} finally {
    sdk.close();
}
```

### 错误处理

```javascript
try {
    const profile = await sdk.user.getUserProfile("username");
} catch (error) {
    if (error instanceof NotFoundException) {
        console.log("用户不存在");
    } else if (error instanceof AuthenticationException) {
        console.log("认证失败");
    } else {
        console.log("其他错误:", error.message);
    }
}
```

## 浏览器环境替代方案

如果需要在浏览器中使用，建议：

1. **使用后端服务代理**
   ```javascript
   // 前端调用自己的后端
   const response = await fetch('/api/user/profile', {
       method: 'POST',
       body: JSON.stringify({ username: 'user' })
   });
   
   // 后端调用 Fursuit.TV API
   ```

2. **使用签名认证**
   - 在后端完成 OAuth 流程
   - 前端使用 accessToken 调用

## 项目结构

```
examples/js/
├── package.json          # npm 配置
├── src/
│   └── index.js          # 示例代码
└── README.md
```

## 注意事项

1. **仅支持 Node.js**
   - 不支持浏览器环境
   - 需要 Node.js 16+

2. **异步编程**
   - 使用 async/await
   - 或使用 Promise

3. **错误处理**
   - 始终使用 try-catch
   - 检查错误类型

4. **资源释放**
   - 使用完毕后调用 `sdk.close()`

## 更多信息

- [开发者指南](../../docs/DEVELOPER_GUIDE.md)
- [平台指南](../../docs/PLATFORM_GUIDE.md)
- [故障排除](../../docs/TROUBLESHOOTING.md)

## 许可证

MIT License
