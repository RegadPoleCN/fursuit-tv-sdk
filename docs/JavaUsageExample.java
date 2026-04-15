import com.furrist.rp.furtv.sdk.FursuitTvSdk;
import com.furrist.rp.furtv.sdk.model.SdkConfig;
import com.furrist.rp.furtv.sdk.exception.FursuitTvSdkException;

import java.util.concurrent.CompletableFuture;

public class JavaUsageExample {

    public static void main(String[] args) {
        // 初始化 SDK
        FursuitTvSdk sdk = new FursuitTvSdk("your-api-key");

        try {
            // 认证 - 使用 CompletableFuture 处理异步操作
            CompletableFuture.runAsync(() -> {
                try {
                    // 交换令牌
                    var tokenInfo = sdk.getAuth().exchangeToken("client-id", "client-secret");
                    System.out.println("认证成功: " + tokenInfo.getAccessToken());

                    // 获取用户资料
                    var userProfile = sdk.getUser().getUserProfile("username");
                    System.out.println("用户资料: " + userProfile.getDisplayName());

                    // 获取热门推荐
                    var popular = sdk.getSearch().getPopular();
                    System.out.println("热门用户数量: " + popular.getUsers().size());

                    // 获取聚会列表
                    var gatherings = sdk.getGathering().getGatheringMonthly(2024, 12);
                    System.out.println("聚会数量: " + gatherings.size());

                    // 搜索学校
                    var schools = sdk.getSchool().searchSchools("北京大学");
                    System.out.println("学校搜索结果数量: " + schools.getSchools().size());

                } catch (FursuitTvSdkException e) {
                    System.err.println("错误: " + e.getMessage());
                }
            }).join();

        } finally {
            // 关闭 SDK
            sdk.close();
        }
    }

    public static void withCustomConfig() {
        // 使用自定义配置初始化 SDK
        SdkConfig config = new SdkConfig(
            "https://api.fursuit.tv",
            "your-api-key",
            60000, // 请求超时时间（毫秒）
            10000, // 连接超时时间（毫秒）
            30000, // 套接字超时时间（毫秒）
            io.ktor.client.plugins.logging.LogLevel.DEBUG, // 日志级别
            true,  // 启用重试
            3,     // 最大重试次数
            1000   // 重试间隔（毫秒）
        );

        FursuitTvSdk sdk = new FursuitTvSdk(config);
        // 使用 SDK...
        sdk.close();
    }
}
