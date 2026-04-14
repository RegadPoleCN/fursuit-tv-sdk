import me.regadpole.furtv.sdk.FursuitTvSdk
import me.regadpole.furtv.sdk.exception.*
import kotlinx.coroutines.*

/**
 * Fursuit.TV SDK Kotlin/JS 示例
 * 
 * 注意：这个示例需要在 Node.js 环境中运行，不支持浏览器环境
 * 
 * 前置要求：
 * - Node.js 16.0.0 或更高版本
 * - Kotlin/JS 编译的 SDK
 */

// 配置信息 - 请替换为你的实际配置
const val APP_ID = "vap_xxxxxxxxxxxxxxxx"
const val APP_SECRET = "your-app-secret"
const val API_KEY = "your-api-key"

suspend fun main() {
    println("=== Fursuit.TV SDK Kotlin/JS 示例 ===\n")
    
    // ========== 初始化方式示例 ==========
    println("=== 初始化方式 ===\n")
    
    // 方式 1: 使用 appId + appSecret（推荐）
    exampleWithAppIdAndSecret()
    
    // 方式 2: 使用 apiKey
    exampleWithApiKey()
    
    // ========== API 模块调用示例 ==========
    println("\n=== API 模块调用 ===\n")
    
    val sdk = FursuitTvSdk(
        appId = APP_ID,
        appSecret = APP_SECRET
    )
    
    try {
        // 获取令牌
        println("正在获取令牌...")
        sdk.auth.exchangeToken(APP_ID, APP_SECRET)
        println("✓ 令牌获取成功\n")
        
        // 1. Base API - 基础接口
        exampleBaseApi(sdk)
        
        // 2. User API - 用户资料
        exampleUserApi(sdk)
        
        // 3. Search API - 搜索发现
        exampleSearchApi(sdk)
        
        // ========== 高级功能 ==========
        println("\n=== 高级功能 ===\n")
        
        // 错误处理示例
        exampleErrorHandling()
        
        // 并发调用示例
        exampleConcurrentCalls(sdk)
        
    } finally {
        sdk.close()
    }
    
    println("\n=== 所有示例完成 ===")
}

/**
 * 方式 1: 使用 appId + appSecret（推荐）
 * 适用于服务端应用，SDK 自动管理令牌刷新
 */
suspend fun exampleWithAppIdAndSecret() {
    println("方式 1: 使用 appId + appSecret（推荐）")
    
    val sdk = FursuitTvSdk(
        appId = APP_ID,
        appSecret = APP_SECRET
    )
    
    try {
        // 获取令牌
        val tokenInfo = sdk.auth.exchangeToken(APP_ID, APP_SECRET)
        println("✓ 令牌获取成功，有效期：${tokenInfo.expiresAt}")
        
        // SDK 会自动管理令牌刷新（当剩余有效期 <= 300 秒时）
        println("✓ SDK 会自动管理令牌刷新\n")
        
    } catch (e: Exception) {
        println("✗ 错误：${e.message}\n")
    } finally {
        sdk.close()
    }
}

/**
 * 方式 2: 使用 apiKey
 * 适用于已有 apiKey 的简单调用
 */
suspend fun exampleWithApiKey() {
    println("方式 2: 使用 apiKey")
    
    val sdk = FursuitTvSdk(apiKey = API_KEY)
    
    try {
        // 直接调用 API
        println("✓ SDK 初始化成功，可直接调用 API\n")
        
    } catch (e: Exception) {
        println("✗ 错误：${e.message}\n")
    } finally {
        sdk.close()
    }
}

/**
 * Base API 示例 - 基础接口
 */
suspend fun exampleBaseApi(sdk: FursuitTvSdk) {
    println("1. Base API - 基础接口")
    
    try {
        // 健康检查
        val health = sdk.base.health()
        println("✓ 服务状态：${health.status}")
        
        // 获取 Android 版本
        val version = sdk.base.getAndroidVersion()
        println("✓ Android 最新版本：${version.version}")
        
        // 获取主题包清单
        val manifest = sdk.base.getThemePacksManifest()
        println("✓ 主题包版本：${manifest.version}")
        println("✓ 主题包数量：${manifest.packs.size}")
        
    } catch (e: Exception) {
        println("✗ Base API 错误：${e.message}")
    }
    
    println()
}

/**
 * User API 示例 - 用户资料
 */
suspend fun exampleUserApi(sdk: FursuitTvSdk) {
    println("2. User API - 用户资料")
    
    try {
        // 获取用户资料
        val profile = sdk.user.getUserProfile("username")
        println("✓ 用户：${profile.displayName} (@${profile.username})")
        println("  物种：${profile.species}")
        println("  性别：${profile.gender}")
        
        // 获取用户 ID
        val userIdData = sdk.user.getUserId("username")
        println("✓ 用户 ID: ${userIdData.userId}")
        
        // 获取点赞状态
        val likeStatus = sdk.user.getLikeStatus(userIdData.userId, "user", userIdData.userId)
        println("✓ 点赞数：${likeStatus.likeCount}")
        
        // 获取用户关系
        val relationships = sdk.user.getUserRelationships(userIdData.userId)
        println("✓ 关系数量：${relationships.relationships.size}")
        
        // 获取用户访客
        val visitors = sdk.user.getUserVisitors(userIdData.userId)
        println("✓ 访客数量：${visitors.visitors.size}")
        
        // 获取用户徽章
        val badges = sdk.user.getSocialBadges(userIdData.userId)
        println("✓ 徽章数量：${badges.badges.size}")
        
        // 获取用户商店商品
        val products = sdk.user.getStoreProducts(userIdData.userId)
        println("✓ 商品数量：${products.products.size}")
        
    } catch (e: Exception) {
        println("✗ User API 错误：${e.message}")
    }
    
    println()
}

/**
 * Search API 示例 - 搜索发现
 */
suspend fun exampleSearchApi(sdk: FursuitTvSdk) {
    println("3. Search API - 搜索发现")
    
    try {
        // 获取热门推荐
        val popular = sdk.search.getPopular()
        println("✓ 热门用户：${popular.users.size} 人")
        
        // 获取随机推荐
        val randomUsers = sdk.search.getRandomFursuit(count = 5, personalized = true)
        println("✓ 随机推荐：${randomUsers.size} 人")
        
        // 搜索用户
        val searchResults = sdk.search.search("fox", type = "user", limit = 10)
        println("✓ 搜索结果：${searchResults.results.size} 条")
        
        // 获取搜索建议
        val suggestions = sdk.search.getSearchSuggestions("fox")
        println("✓ 搜索建议：${suggestions.size} 条")
        
        // 获取物种列表
        val speciesList = sdk.search.getSpeciesList()
        println("✓ 物种数量：${speciesList.species.size}")
        
        // 获取热门地区
        val locations = sdk.search.getPopularLocations()
        println("✓ 热门地区：${locations.locations.size} 个")
        
    } catch (e: Exception) {
        println("✗ Search API 错误：${e.message}")
    }
    
    println()
}

/**
 * 错误处理示例
 */
suspend fun exampleErrorHandling() {
    println("错误处理示例")
    
    val sdk = FursuitTvSdk(
        appId = APP_ID,
        appSecret = APP_SECRET
    )
    
    try {
        // 尝试获取不存在的用户
        sdk.user.getUserProfile("non_existent_user_12345")
        
    } catch (e: TokenExpiredException) {
        println("✓ 令牌过期（会自动刷新）")
    } catch (e: AuthenticationException) {
        println("✓ 认证失败：${e.message}")
    } catch (e: NotFoundException) {
        println("✓ 资源不存在：${e.message}")
    } catch (e: ValidationException) {
        println("✓ 参数错误：${e.message}")
    } catch (e: NetworkException) {
        println("✓ 网络连接失败：${e.message}")
    } catch (e: ApiException) {
        println("✓ API 错误 (${e.statusCode}): ${e.message}")
    } catch (e: Exception) {
        println("✓ 未知错误：${e.message}")
    } finally {
        sdk.close()
    }
    
    println()
}

/**
 * 并发调用示例
 */
suspend fun exampleConcurrentCalls(sdk: FursuitTvSdk) {
    println("并发调用示例")
    
    val usernames = listOf("user1", "user2", "user3")
    
    // 并发获取多个用户
    val jobs = usernames.map { username ->
        GlobalScope.async {
            try {
                val profile = sdk.user.getUserProfile(username)
                println("✓ 获取用户：${profile.displayName}")
                profile
            } catch (e: Exception) {
                println("✗ 获取用户 $username 失败：${e.message}")
                null
            }
        }
    }
    
    val results = jobs.awaitAll().filterNotNull()
    println("✓ 成功获取 ${results.size}/${usernames.size} 个用户\n")
}
