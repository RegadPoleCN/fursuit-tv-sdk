import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import me.regadpole.furtv.sdk.FursuitTvSdk
import me.regadpole.furtv.sdk.model.SdkConfig

class FursuitTvSdkTest {
    @Test
    fun testSdkInitialization() {
        // 测试使用 API 密钥初始化 SDK
        val sdk1 = FursuitTvSdk(apiKey = TestConfig.API_KEY)
        assertNotNull(sdk1)
        assertNotNull(sdk1.base)
        assertNotNull(sdk1.user)
        assertNotNull(sdk1.search)
        assertNotNull(sdk1.gathering)
        assertNotNull(sdk1.school)
        sdk1.close()
    }
    
    @Test
    fun testSdkInitializationWithCustomConfig() {
        // 测试使用自定义配置初始化 SDK
        val config = SdkConfig.builder()
            .apiKey(TestConfig.API_KEY)
            .baseUrl(TestConfig.BASE_URL)
            .requestTimeout(TestConfig.DEFAULT_TIMEOUT)
            .build()
        val sdk = FursuitTvSdk(config)
        assertNotNull(sdk)
        assertEquals(TestConfig.BASE_URL, sdk.getConfig().baseUrl)
        sdk.close()
    }
    
    @Test
    fun testSdkInitializationWithTokenInfo() {
        // 测试使用 TokenInfo 初始化 SDK
        val sdk = FursuitTvSdk(
            apiKey = TestConfig.API_KEY,
            baseUrl = TestConfig.BASE_URL,
            tokenInfo = null
        )
        assertNotNull(sdk)
        sdk.close()
    }
    
    @Test
    fun testSdkGetConfig() {
        // 测试获取 SDK 配置
        val sdk = FursuitTvSdk(apiKey = TestConfig.API_KEY)
        val config = sdk.getConfig()
        assertNotNull(config)
        assertEquals(TestConfig.API_KEY, config.apiKey)
        sdk.close()
    }
}
