import kotlin.test.Test
import kotlin.test.assertNotNull
import me.regadpole.furtv.sdk.FursuitTvSdk

class SchoolApiTest {
    @Test
    fun testSchoolApiMethods() {
        val sdk = FursuitTvSdk(apiKey = TestConfig.API_KEY)
        
        // 验证学校 API 对象存在
        assertNotNull(sdk.school)
        
        sdk.close()
    }
    
    @Test
    fun testSchoolApiInstantiation() {
        // 验证 SchoolApi 可以直接实例化
        val sdk = FursuitTvSdk(apiKey = TestConfig.API_KEY)
        val schoolApi = sdk.school
        assertNotNull(schoolApi)
        sdk.close()
    }
}
