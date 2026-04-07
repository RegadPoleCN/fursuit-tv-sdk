import me.regadpole.furtv.sdk.FursuitTvSdk;
import me.regadpole.furtv.sdk.model.SdkConfig;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class FursuitTvSdkJavaTest {
    
    // 测试配置常量
    private static final String API_KEY = "test-api-key";
    private static final String BASE_URL = "https://open-global.vdsentnet.com";
    private static final long DEFAULT_TIMEOUT = 30000L;
    
    @Test
    public void testSdkInitialization() {
        // 测试使用 API 密钥初始化 SDK
        FursuitTvSdk sdk = new FursuitTvSdk(API_KEY);
        assertNotNull(sdk);
        assertNotNull(sdk.getAuth());
        assertNotNull(sdk.getBase());
        assertNotNull(sdk.getUser());
        assertNotNull(sdk.getSearch());
        assertNotNull(sdk.getGathering());
        assertNotNull(sdk.getSchool());
        sdk.close();
    }
    
    @Test
    public void testSdkInitializationWithCustomConfig() {
        // 测试使用自定义配置初始化 SDK
        SdkConfig config = SdkConfig.builder()
            .baseUrl(BASE_URL)
            .apiKey(API_KEY)
            .requestTimeout(DEFAULT_TIMEOUT)
            .build();
        FursuitTvSdk sdk = new FursuitTvSdk(config, null);
        assertNotNull(sdk);
        assertEquals(BASE_URL, sdk.getConfig().getBaseUrl());
        assertEquals(DEFAULT_TIMEOUT, sdk.getConfig().getRequestTimeout());
        sdk.close();
    }
    
    @Test
    public void testSdkGetConfig() {
        // 测试获取 SDK 配置
        FursuitTvSdk sdk = new FursuitTvSdk(API_KEY);
        SdkConfig config = sdk.getConfig();
        assertNotNull(config);
        assertEquals(API_KEY, config.getApiKey());
        sdk.close();
    }
    
    @Test
    public void testBaseApiMethods() {
        // 测试 BaseApi 方法
        FursuitTvSdk sdk = new FursuitTvSdk(API_KEY);
        assertNotNull(sdk.getBase());
        sdk.close();
    }
    
    @Test
    public void testUserApiMethods() {
        // 测试 UserApi 方法
        FursuitTvSdk sdk = new FursuitTvSdk(API_KEY);
        assertNotNull(sdk.getUser());
        sdk.close();
    }
    
    @Test
    public void testSearchApiMethods() {
        // 测试 SearchApi 方法
        FursuitTvSdk sdk = new FursuitTvSdk(API_KEY);
        assertNotNull(sdk.getSearch());
        sdk.close();
    }
    
    @Test
    public void testGatheringApiMethods() {
        // 测试 GatheringApi 方法
        FursuitTvSdk sdk = new FursuitTvSdk(API_KEY);
        assertNotNull(sdk.getGathering());
        sdk.close();
    }
    
    @Test
    public void testSchoolApiMethods() {
        // 测试 SchoolApi 方法
        FursuitTvSdk sdk = new FursuitTvSdk(API_KEY);
        assertNotNull(sdk.getSchool());
        sdk.close();
    }
    
    @Test
    public void testSdkClose() {
        // 测试 SDK 关闭
        FursuitTvSdk sdk = new FursuitTvSdk(API_KEY);
        assertNotNull(sdk);
        sdk.close();
    }
}
