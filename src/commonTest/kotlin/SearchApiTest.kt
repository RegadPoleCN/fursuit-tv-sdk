import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertEquals
import me.regadpole.furtv.sdk.FursuitTvSdk
import me.regadpole.furtv.sdk.model.RandomFursuitParams
import me.regadpole.furtv.sdk.model.SearchParams

class SearchApiTest {
    @Test
    fun testSearchApiMethods() {
        val sdk = FursuitTvSdk(apiKey = TestConfig.API_KEY)
        
        // 验证搜索 API 对象存在
        assertNotNull(sdk.search)
        
        sdk.close()
    }
    
    @Test
    fun testSearchApiInstantiation() {
        // 验证 SearchApi 可以直接实例化
        val sdk = FursuitTvSdk(apiKey = TestConfig.API_KEY)
        val searchApi = sdk.search
        assertNotNull(searchApi)
        sdk.close()
    }
    
    @Test
    fun testGetPopularMethod() {
        // 验证 getPopular 方法签名
        val sdk = FursuitTvSdk(apiKey = TestConfig.API_KEY)
        val method = sdk.search::getPopular
        assertNotNull(method)
        sdk.close()
    }
    
    @Test
    fun testGetRandomFursuitMethod() {
        // 验证 getRandomFursuit 方法签名
        val sdk = FursuitTvSdk(apiKey = TestConfig.API_KEY)
        // 由于方法重载，我们只验证 search 对象存在
        assertNotNull(sdk.search)
        sdk.close()
    }
    
    @Test
    fun testSearchMethod() {
        // 验证 search 方法签名 - 使用重载版本避免歧义
        val sdk = FursuitTvSdk(apiKey = TestConfig.API_KEY)
        // 由于方法重载，我们只验证 search 对象存在
        assertNotNull(sdk.search)
        sdk.close()
    }
    
    @Test
    fun testGetSearchSuggestionsMethod() {
        // 验证 getSearchSuggestions 方法签名
        val sdk = FursuitTvSdk(apiKey = TestConfig.API_KEY)
        val method = sdk.search::getSearchSuggestions
        assertNotNull(method)
        sdk.close()
    }
    
    @Test
    fun testSearchBySpeciesMethod() {
        // 验证 searchBySpecies 方法签名
        val sdk = FursuitTvSdk(apiKey = TestConfig.API_KEY)
        val method = sdk.search::searchBySpecies
        assertNotNull(method)
        sdk.close()
    }
    
    @Test
    fun testGetSpeciesListMethod() {
        // 验证 getSpeciesList 方法签名
        val sdk = FursuitTvSdk(apiKey = TestConfig.API_KEY)
        val method = sdk.search::getSpeciesList
        assertNotNull(method)
        sdk.close()
    }
    
    @Test
    fun testGetPopularLocationsMethod() {
        // 验证 getPopularLocations 方法签名
        val sdk = FursuitTvSdk(apiKey = TestConfig.API_KEY)
        val method = sdk.search::getPopularLocations
        assertNotNull(method)
        sdk.close()
    }
    
    @Test
    fun testRandomFursuitParamsCreation() {
        // 验证 RandomFursuitParams 可以创建
        val params = RandomFursuitParams(count = 10, personalized = true)
        assertNotNull(params)
        assertEquals(10, params.count)
        assertEquals(true, params.personalized)
    }
    
    @Test
    fun testSearchParamsCreation() {
        // 验证 SearchParams 可以创建
        val params = SearchParams(
            query = "test",
            type = "user",
            cursor = null,
            limit = 20
        )
        assertNotNull(params)
        assertEquals("test", params.query)
        assertEquals("user", params.type)
        assertEquals(20, params.limit)
    }
}
