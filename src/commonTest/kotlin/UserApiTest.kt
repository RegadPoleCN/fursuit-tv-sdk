import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertEquals
import me.regadpole.furtv.sdk.FursuitTvSdk

class UserApiTest {
    @Test
    fun testUserApiMethods() {
        val sdk = FursuitTvSdk(apiKey = TestConfig.API_KEY)
        
        // 验证用户 API 方法存在
        assertNotNull(sdk.user::getUserProfile)
        assertNotNull(sdk.user::getUserById)
        assertNotNull(sdk.user::getLikeStatus)
        assertNotNull(sdk.user::getUserRelationships)
        assertNotNull(sdk.user::getUserVisitors)
        assertNotNull(sdk.user::getUserStoreProducts)
        assertNotNull(sdk.user::getUserSocialBadges)
        assertNotNull(sdk.user::getSocialBadgeDetail)
        
        sdk.close()
    }
    
    @Test
    fun testUserApiInstantiation() {
        // 验证 UserApi 可以直接实例化
        val sdk = FursuitTvSdk(apiKey = TestConfig.API_KEY)
        val userApi = sdk.user
        assertNotNull(userApi)
        sdk.close()
    }
    
    @Test
    fun testUserProfileMethod() {
        // 验证 getUserProfile 方法签名
        val sdk = FursuitTvSdk(apiKey = TestConfig.API_KEY)
        val method = sdk.user::getUserProfile
        assertNotNull(method)
        assertEquals("getUserProfile", method.name)
        sdk.close()
    }
    
    @Test
    fun testGetUserByIdMethod() {
        // 验证 getUserById 方法签名
        val sdk = FursuitTvSdk(apiKey = TestConfig.API_KEY)
        val method = sdk.user::getUserById
        assertNotNull(method)
        sdk.close()
    }
    
    @Test
    fun testGetLikeStatusMethod() {
        // 验证 getLikeStatus 方法签名
        val sdk = FursuitTvSdk(apiKey = TestConfig.API_KEY)
        val method = sdk.user::getLikeStatus
        assertNotNull(method)
        sdk.close()
    }
    
    @Test
    fun testGetUserRelationshipsMethod() {
        // 验证 getUserRelationships 方法签名
        val sdk = FursuitTvSdk(apiKey = TestConfig.API_KEY)
        val method = sdk.user::getUserRelationships
        assertNotNull(method)
        sdk.close()
    }
    
    @Test
    fun testGetUserVisitorsMethod() {
        // 验证 getUserVisitors 方法签名
        val sdk = FursuitTvSdk(apiKey = TestConfig.API_KEY)
        val method = sdk.user::getUserVisitors
        assertNotNull(method)
        sdk.close()
    }
    
    @Test
    fun testGetUserStoreProductsMethod() {
        // 验证 getUserStoreProducts 方法签名
        val sdk = FursuitTvSdk(apiKey = TestConfig.API_KEY)
        val method = sdk.user::getUserStoreProducts
        assertNotNull(method)
        sdk.close()
    }
    
    @Test
    fun testGetUserSocialBadgesMethod() {
        // 验证 getUserSocialBadges 方法签名
        val sdk = FursuitTvSdk(apiKey = TestConfig.API_KEY)
        val method = sdk.user::getUserSocialBadges
        assertNotNull(method)
        sdk.close()
    }
    
    @Test
    fun testGetSocialBadgeDetailMethod() {
        // 验证 getSocialBadgeDetail 方法签名
        val sdk = FursuitTvSdk(apiKey = TestConfig.API_KEY)
        val method = sdk.user::getSocialBadgeDetail
        assertNotNull(method)
        sdk.close()
    }
}
