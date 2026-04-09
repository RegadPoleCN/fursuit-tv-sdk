import me.regadpole.furtv.sdk.FursuitTvSdk
import me.regadpole.furtv.sdk.model.SdkConfig
import kotlinx.coroutines.runBlocking

/**
 * Fursuit.TV SDK JVM 示例项目
 * 
 * 演示如何使用 SDK 的各种功能
 */

// 配置信息 - 请替换为你的实际配置
const val APP_ID = "vap_xxxxxxxxxxxxxxxx"
const val APP_SECRET = "your-app-secret"
const val API_KEY = "your-api-key"

fun main() = runBlocking {
    println("=== Fursuit.TV SDK JVM 示例 ===\n")
    
    // 示例 1: 使用 appId + appSecret（推荐）
    exampleWithAppIdAndSecret()
    
    // 示例 2: 使用 apiKey
    exampleWithApiKey()
    
    // 示例 3: 错误处理
    exampleWithErrorHandling()
    
    // 示例 4: 批量操作
    exampleWithBatchOperations()
    
    println("\n=== 示例完成 ===")
}

/**
 * 示例 1: 使用 appId + appSecret 初始化
 */
suspend fun exampleWithAppIdAndSecret() {
    println("示例 1: 使用 appId + appSecret")
    
    val sdk = FursuitTvSdk(
        appId = APP_ID,
        appSecret = APP_SECRET
    )
    
    try {
        // 获取令牌
        println("正在获取令牌...")
        val tokenInfo = sdk.auth.exchangeToken(APP_ID, APP_SECRET)
        println("✓ 令牌获取成功，有效期：${tokenInfo.expiresAt}")
        
        // 获取用户资料
        println("\n获取用户资料...")
        val profile = sdk.user.getUserProfile("username")
        println("✓ 用户：${profile.displayName} (@${profile.username})")
        
        // 获取热门推荐
        println("\n获取热门推荐...")
        val popular = sdk.search.getPopular()
        println("✓ 热门用户：${popular.users.size} 人")
        
    } catch (e: Exception) {
        println("✗ 错误：${e.message}")
    } finally {
        sdk.close()
    }
}

/**
 * 示例 2: 使用 apiKey 初始化
 */
suspend fun exampleWithApiKey() {
    println("\n示例 2: 使用 apiKey")
    
    val sdk = FursuitTvSdk(apiKey = API_KEY)
    
    try {
        // 直接调用 API
        println("正在获取用户信息...")
        val profile = sdk.user.getUserProfile("username")
        println("✓ 用户：${profile.displayName}")
        
    } catch (e: Exception) {
        println("✗ 错误：${e.message}")
    } finally {
        sdk.close()
    }
}

/**
 * 示例 3: 错误处理
 */
suspend fun exampleWithErrorHandling() {
    println("\n示例 3: 错误处理")
    
    val sdk = FursuitTvSdk(
        appId = APP_ID,
        appSecret = APP_SECRET
    )
    
    try {
        // 尝试获取不存在的用户
        println("尝试获取不存在的用户...")
        val profile = sdk.user.getUserProfile("non_existent_user_12345")
        
    } catch (e: Exception) {
        println("✓ 捕获到错误：${e.javaClass.simpleName}")
        println("  消息：${e.message}")
    } finally {
        sdk.close()
    }
}

/**
 * 示例 4: 批量操作
 */
suspend fun exampleWithBatchOperations() {
    println("\n示例 4: 批量操作")
    
    val sdk = FursuitTvSdk(
        appId = APP_ID,
        appSecret = APP_SECRET
    )
    
    try {
        // 并发获取多个用户
        val usernames = listOf("user1", "user2", "user3")
        
        println("并发获取 ${usernames.size} 个用户...")
        val jobs = usernames.map { username ->
            kotlinx.coroutines.async {
                try {
                    sdk.user.getUserProfile(username)
                } catch (e: Exception) {
                    null
                }
            }
        }
        
        val results = jobs.mapNotNull { it.await() }
        println("✓ 成功获取 ${results.size} 个用户")
        
    } catch (e: Exception) {
        println("✗ 错误：${e.message}")
    } finally {
        sdk.close()
    }
}
