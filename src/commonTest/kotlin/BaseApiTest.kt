import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertEquals
import me.regadpole.furtv.sdk.FursuitTvSdk

class BaseApiTest {
    @Test
    fun testBaseApiMethods() {
        val sdk = FursuitTvSdk(apiKey = TestConfig.API_KEY)
        
        // 验证基础 API 方法存在
        assertNotNull(sdk.base::health)
        assertNotNull(sdk.base::getAndroidVersion)
        assertNotNull(sdk.base::checkAndroidVersion)
        assertNotNull(sdk.base::getThemePacksManifest)
        
        sdk.close()
    }
    
    @Test
    fun testBaseApiHelloWorld() {
        // 验证 helloWorld 方法存在
        val sdk = FursuitTvSdk(apiKey = TestConfig.API_KEY)
        assertNotNull(sdk.base::helloWorld)
        sdk.close()
    }
    
    @Test
    fun testBaseApiInstantiation() {
        // 验证 BaseApi 可以直接实例化
        val sdk = FursuitTvSdk(apiKey = TestConfig.API_KEY)
        val baseApi = sdk.base
        assertNotNull(baseApi)
        sdk.close()
    }
}
