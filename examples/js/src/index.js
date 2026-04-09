/**
 * Fursuit.TV SDK JS/Node.js 示例
 * 
 * 注意：这个示例需要在 Node.js 环境中运行，不支持浏览器环境
 */

// 由于 JS 示例需要使用 Kotlin/JS 编译的 SDK
// 这里提供概念性示例代码

// 假设 SDK 已经通过 Kotlin/JS 编译并可用
// import { FursuitTvSdk } from 'fursuit-tv-sdk'

const APP_ID = "vap_xxxxxxxxxxxxxxxx";
const APP_SECRET = "your-app-secret";

async function main() {
    console.log("=== Fursuit.TV SDK JS 示例 ===\n");
    
    // 示例 1: 使用 appId + appSecret
    await exampleWithAppIdAndSecret();
    
    // 示例 2: 错误处理
    await exampleWithErrorHandling();
    
    console.log("\n=== 示例完成 ===");
}

async function exampleWithAppIdAndSecret() {
    console.log("示例 1: 使用 appId + appSecret");
    
    try {
        // 初始化 SDK
        // const sdk = new FursuitTvSdk({
        //     appId: APP_ID,
        //     appSecret: APP_SECRET
        // });
        
        console.log("✓ SDK 初始化成功");
        
        // 获取令牌
        // const tokenInfo = await sdk.auth.exchangeToken(APP_ID, APP_SECRET);
        // console.log("✓ 令牌获取成功");
        
        // 获取用户资料
        // const profile = await sdk.user.getUserProfile("username");
        // console.log(`✓ 用户：${profile.displayName}`);
        
        // 关闭 SDK
        // sdk.close();
        
    } catch (error) {
        console.error("✗ 错误:", error.message);
    }
}

async function exampleWithErrorHandling() {
    console.log("\n示例 2: 错误处理");
    
    try {
        // 尝试获取不存在的用户
        // const profile = await sdk.user.getUserProfile("non_existent_user");
        
    } catch (error) {
        console.log("✓ 捕获到错误:", error.name);
        console.log("  消息:", error.message);
    }
}

// 运行示例
main().catch(console.error);
