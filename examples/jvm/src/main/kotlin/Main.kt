/**
 * Fursuit.TV SDK JVM 完整示例
 *
 * 演示 SDK 的所有功能：
 * - 4 种初始化方式（签名交换、apiKey、显式 tokenExchange、OAuth）
 * - 6 个 API 模块调用（Auth/Base/User/Search/Gathering/School）
 * - 完整的错误处理（覆盖所有异常子类）
 * - 并发调用最佳实践（async/awaitAll）
 */

import com.furrist.rp.furtv.sdk.FursuitTvSdk
import com.furrist.rp.furtv.sdk.auth.OAuthConfig
import com.furrist.rp.furtv.sdk.exception.*
import com.furrist.rp.furtv.sdk.fursuitTvSdk
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking

// ========== 配置常量 ==========
const val CLIENT_ID = "vap_xxxxxxxxxxxxxxxx"
const val CLIENT_SECRET = "your-client-secret-here"
const val API_KEY = "your-api-key-here"

// ========== main 函数 ==========

fun main() = runBlocking {
    println("=== Fursuit.TV SDK JVM 完整示例 ===\n")

    // 演示 4 种初始化方式
    println("--- 初始化方式示例 ---")
    example1_dslTokenExchange()
    example2_apiKey()
    example3_explicitTokenExchange()
    // 注意: example4_oauth() 需要用户交互，已注释
    // example4_oauth()

    // 创建 SDK 实例进行 API 调用演示
    println("\n--- API 模块调用示例 ---")
    val sdk = fursuitTvSdk {
        clientId = CLIENT_ID
        clientSecret = CLIENT_SECRET
    }

    try {
        // 调用 6 个 API 模块
        exampleAuthApi(sdk)
        exampleBaseApi(sdk)
        exampleUserApi(sdk)
        exampleSearchApi(sdk)
        exampleGatheringApi(sdk)
        exampleSchoolApi(sdk)

        // 高级功能示例
        println("\n--- 高级功能示例 ---")
        exampleErrorHandling()
        exampleConcurrentCalls(sdk)
    } finally {
        sdk.close()
        println("\n✓ SDK 已关闭")
    }
}

// ========== 初始化方式示例（4种）==========

/**
 * 方式 1: DSL 签名交换（推荐）
 *
 * 使用 fursuitTvSdk {} DSL 构建器初始化 SDK。
 * SDK 会自动使用 clientId 和 clientSecret 完成签名交换。
 */
suspend fun example1_dslTokenExchange() {
    println("\n[方式1] DSL 签名交换（推荐）")
    
    val sdk = fursuitTvSdk {
        clientId = CLIENT_ID
        clientSecret = CLIENT_SECRET
    }

    try {
        val authenticated = sdk.auth.isAuthenticated()
        println("  认证状态: $authenticated")
        
        sdk.auth.getApiKey()?.let { 
            println("  API Key (前20字符): ${it.take(20)}..." 
        }
    } finally {
        sdk.close()
        println("  ✓ SDK 已关闭")
    }
}

/**
 * 方式 2: 已有 apiKey
 *
 * 如果已经从 VDS 开发者控制台获取了 apiKey，
 * 可以直接使用 FursuitTvSdk.create() 初始化。
 */
suspend fun example2_apiKey() {
    println("\n[方式2] 已有 apiKey")
    
    // 注意: 这里使用占位符，实际使用时替换为真实的 apiKey
    // val sdk = FursuitTvSdk.create(apiKey = API_KEY)
    println("  (需要真实 apiKey 才能运行此示例)")
}

/**
 * 方式 3: 显式 createForTokenExchange
 *
 * 显式创建 SDK 并完成签名交换，
 * 可以在交换后查看返回的 TokenInfo 详情。
 */
suspend fun example3_explicitTokenExchange() {
    println("\n[方式3] 显式 createForTokenExchange")
    
    val sdk = FursuitTvSdk.createForTokenExchange(
        clientId = CLIENT_ID,
        clientSecret = CLIENT_SECRET
    )

    try {
        val accessToken = sdk.auth.getAccessToken()
        val apiKey = sdk.auth.getApiKey()
        
        println("  Access Token (前20字符): ${accessToken?.take(20)}...")
        println("  API Key (前20字符): ${apiKey?.take(20)}...")
        println("  是否已认证: ${sdk.auth.isAuthenticated()}")
    } finally {
        sdk.close()
        println("  ✓ SDK 已关闭")
    }
}

/**
 * 方式 4: OAuth 2.0 授权码流程
 *
 * 使用 OAuth 获取用户授权并查询用户信息。
 * ⚠️ 此方式需要用户交互（打开浏览器登录授权），已注释。
 */
suspend fun example4_oauth() {
    println("\n[方式4] OAuth 2.0 授权码流程")
    
    /*
    val sdk = fursuitTvSdk {
        clientId = CLIENT_ID
        clientSecret = CLIENT_SECRET
    }

    try {
        val oauthConfig = OAuthConfig(
            callbackHost = "localhost",
            callbackPort = 8080,
            callbackPath = "/callback",
            enablePkce = true  // 推荐！启用 PKCE 安全增强
        )

        // 启动 OAuth 流程（自动打开浏览器、处理回调、交换令牌）
        val tokenInfo = sdk.auth.initOAuth(oauthConfig)
        println("  OAuth 成功！Token 类型: ${tokenInfo.tokenType}")

        // 使用双认证头获取用户信息
        val userInfo = sdk.auth.getUserInfo()
        println("  用户昵称: ${userInfo.nickname}")
        println("  用户名: @${userInfo.username}")
        println("  用户 ID: ${userInfo.sub}")
    } finally {
        sdk.close()
        println("  ✓ SDK 已关闭")
    }
    */
    println("  (已注释 - 需要用户交互)")
}

// ========== API 模块调用示例（6个模块）==========

/**
 * Auth API - 认证模块示例
 *
 * 展示认证状态检查和令牌信息获取。
 */
suspend fun exampleAuthApi(sdk: FursuitTvSdk) {
    println("\n[Auth API] 认证模块")
    
    try {
        val isAuthenticated = sdk.auth.isAuthenticated()
        println("  是否已认证: $isAuthenticated")

        val accessToken = sdk.auth.getAccessToken()
        accessToken?.let { println("  Access Token (前20字符): ${it.take(20)}...") }

        val apiKey = sdk.auth.getApiKey()
        apiKey?.let { println("  API Key (前20字符): ${it.take(20)}...") }
    } catch (e: FursuitTvSdkException) {
        println("  ✗ Auth API 错误: ${e.message}")
    }
}

/**
 * Base API - 基础接口示例
 *
 * 展示健康检查、版本查询和主题包清单。
 */
suspend fun exampleBaseApi(sdk: FursuitTvSdk) {
    println("\n[Base API] 基础接口")
    
    try {
        // 健康检查
        val health = sdk.base.health()
        println("  服务状态: ${health.status}")

        // Android 版本信息
        val version = sdk.base.getAndroidVersion()
        println("  最新 Android 版本: ${version.version}")

        // 主题包清单
        val manifest = sdk.base.getThemePacksManifest()
        println("  主题包数量: ${manifest.packs.size}")
    } catch (e: FursuitTvSdkException) {
        println("  ✗ Base API 错误: ${e.message}")
    }
}

/**
 * User API - 用户资料示例
 *
 * 展示用户资料查询、点赞状态、关系列表等功能。
 */
suspend fun exampleUserApi(sdk: FursuitTvSdk) {
    println("\n[User API] 用户资料")
    
    try {
        // 查询用户资料
        val profile = sdk.user.getUserProfile("exampleUser")
        println("  显示名称: ${profile.displayName}")
        println("  物种: ${profile.species}")

        // 获取用户 ID
        val userId = sdk.user.getUserId("exampleUser")
        println("  用户 ID: $userId")

        // 查询点赞状态
        if (userId != null) {
            val likeStatus = sdk.user.getLikeStatus(userId, "user", userId)
            println("  点赞数: ${likeStatus.likeCount}")
        }

        // 查询用户关系
        if (userId != null) {
            val relationships = sdk.user.getUserRelationships(userId)
            println("  关系列表数量: ${relationships.relationships.size}")
        }

        // 查询访客记录
        if (userId != null) {
            val visitors = sdk.user.getUserVisitors(userId)
            println("  访客数量: ${visitors.visitors.size}")
        }

        // 查询社交徽章
        if (userId != null) {
            val badges = sdk.user.getSocialBadges(userId)
            println("  徽章数量: ${badges.badges.size}")
        }

        // 查询商店商品
        if (userId != null) {
            val products = sdk.user.getStoreProducts(userId)
            println("  商品数量: ${products.products.size}")
        }
    } catch (e: FursuitTvSdkException) {
        println("  ✗ User API 错误: ${e.message}")
    }
}

/**
 * Search API - 搜索发现示例
 *
 * 展示热门推荐、随机推荐、搜索等功能。
 */
suspend fun exampleSearchApi(sdk: FursuitTvSdk) {
    println("\n[Search API] 搜索发现")
    
    try {
        // 热门推荐
        val popular = sdk.search.getPopular()
        println("  热门用户数量: ${popular.users.size}")

        // 随机推荐（个性化）
        val randomFursuits = sdk.search.getRandomFursuit(count = 5, personalized = true)
        println("  随机兽装数量: ${randomFursuits.size}")

        // 搜索用户
        val searchResult = sdk.search.search(query = "fox", type = "user", limit = 10)
        println("  搜索结果数量: ${searchResult.results.size}")
        searchResult.cursor?.let { println("  下一页游标: $it") }

        // 搜索建议
        val suggestions = sdk.search.getSearchSuggestions("fox")
        println("  搜索建议数量: ${suggestions.size}")

        // 物种列表
        val speciesList = sdk.search.getSpeciesList()
        println("  物种数量: ${speciesList.species.size}")

        // 热门地区
        val locations = sdk.search.getPopularLocations()
        println("  热门地区数量: ${locations.locations.size}")
    } catch (e: FursuitTvSdkException) {
        println("  ✗ Search API 错误: ${e.message}")
    }
}

/**
 * Gathering API - 聚会活动示例
 *
 * 展示聚会统计、月历、附近聚会查询。
 */
suspend fun exampleGatheringApi(sdk: FursuitTvSdk) {
    println("\n[Gathering API] 聚会活动")
    
    try {
        // 年度统计
        val yearStats = sdk.gathering.getYearStats()
        println("  统计年份: ${yearStats.year}")
        println("  总聚会数: ${yearStats.totalGatherings}")

        // 月历查询
        val monthlyGatherings = sdk.gathering.getMonthly(year = 2024, month = 12)
        println("  2024年12月聚会数量: ${monthlyGatherings.size}")

        // 附近聚会（旧金山为例）
        val nearbyGatherings = sdk.gathering.getNearby(
            lat = 37.7749,
            lng = -122.4194,
            radius = 10000  // 10公里
        )
        println("  附近聚会数量: ${nearbyGatherings.size}")
    } catch (e: FursuitTvSdkException) {
        println("  ✗ Gathering API 错误: ${e.message}")
    }
}

/**
 * School API - 学校角色示例
 *
 * 展示学校搜索、用户学校、角色列表查询。
 */
suspend fun exampleSchoolApi(sdk: FursuitTvSdk) {
    println("\n[School API] 学校角色")
    
    try {
        // 搜索学校
        val schoolSearchResult = sdk.school.searchSchools(query = "大学", limit = 10)
        println("  搜索结果总数: ${schoolSearchResult.total}")

        // 查询用户的学校
        val userSchools = sdk.school.getUserSchools(username = "exampleUser")
        println("  用户学校数量: ${userSchools.schools.size}")

        // 查询用户的角色
        val userCharacters = sdk.school.getUserCharacters(username = "exampleUser")
        println("  用户角色数量: ${userCharacters.characters.size}")
    } catch (e: FursuitTvSdkException) {
        println("  ✗ School API 错误: ${e.message}")
    }
}

// ========== 高级功能示例 ==========

/**
 * 错误处理示例
 *
 * 演示如何捕获和处理 SDK 的各种异常类型。
 */
suspend fun exampleErrorHandling() {
    println("\n[错误处理] 异常捕获示例")
    
    val sdk = fursuitTvSdk {
        clientId = CLIENT_ID
        clientSecret = CLIENT_SECRET
    }

    try {
        // 尝试获取不存在的用户（可能触发异常）
        sdk.user.getUserId("this_user_definitely_does_not_exist_12345")
    } catch (e: TokenExpiredException) {
        println("  ✓ 令牌过期异常: ${e.message}")
        println("    建议: 让SDK自动刷新或手动调用 exchangeToken()")
    } catch (e: AuthenticationException) {
        println("  ✓ 认证失败异常: ${e.message}")
        println("    建议: 检查凭证是否正确或已过期")
    } catch (e: NotFoundException) {
        println("  ✓ 资源未找到异常: ${e.message}")
        println("    建议: 确认资源ID是否正确或资源是否存在")
    } catch (e: ValidationException) {
        println("  ✓ 参数验证异常: ${e.message}")
        println("    建议: 检查参数是否符合API要求")
    } catch (e: NetworkException) {
        println("  ✓ 网络连接异常: ${e.message}")
        println("    建议: 检查网络连接或DNS配置")
    } catch (e: ApiException) {
        println("  ✓ API 错误 (HTTP ${e.statusCode}): ${e.message}")
        println("    错误码: ${e.errorCode}")
    } catch (e: FursuitTvSdkException) {
        println("  ✓ 其他 SDK 异常: ${e.javaClass.simpleName} - ${e.message}")
    } finally {
        sdk.close()
    }
}

/**
 * 并发调用示例
 *
 * 演示如何使用 Kotlin 协程并发调用多个 API。
 */
suspend fun exampleConcurrentCalls(sdk: FursuitTvSdk) {
    println("\n[并发调用] async/awaitAll 示例")
    
    val usernames = listOf("user1", "user2", "user3")
    
    val startTime = System.currentTimeMillis()
    
    // 并发获取多个用户资料
    val results = usernames.map { username ->
        async {
            try {
                sdk.user.getUserProfile(username)
            } catch (e: FursuitTvSdkException) {
                println("  查询 $username 失败: ${e.message}")
                null
            }
        }
    }.awaitAll()

    val elapsed = System.currentTimeMillis() - startTime
    
    val successCount = results.filterNotNull().size
    println("  成功: $successCount/${usernames.size}")
    println("  耗时: ${elapsed}ms")
}
