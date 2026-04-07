import kotlin.test.Test
import kotlin.test.assertNotNull
import me.regadpole.furtv.sdk.FursuitTvSdk

class GatheringApiTest {
    @Test
    fun testGatheringApiMethods() {
        val sdk = FursuitTvSdk(apiKey = TestConfig.API_KEY)
        
        // 验证聚会 API 对象存在
        assertNotNull(sdk.gathering)
        
        sdk.close()
    }
    
    @Test
    fun testGatheringApiInstantiation() {
        // 验证 GatheringApi 可以直接实例化
        val sdk = FursuitTvSdk(apiKey = TestConfig.API_KEY)
        val gatheringApi = sdk.gathering
        assertNotNull(gatheringApi)
        sdk.close()
    }
}
