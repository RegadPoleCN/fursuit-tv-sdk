import com.furrist.rp.furtv.sdk.FursuitTvSdk
import com.furrist.rp.furtv.sdk.exception.*
import com.furrist.rp.furtv.sdk.fursuitTvSdk
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking

/**
 * Fursuit.TV SDK Kotlin/JS (Node.js) 完整示例
 *
 * 演示 SDK 的核心功能：
 * - DSL 初始化方式（推荐）
 * - apiKey 初始化方式
 * - 5 个 API 模块调用（Base/User/Search/Gathering/School）
 * - 完整的错误处理
 * - 并发调用示例
 *
 * 前置要求：
 * - Node.js 16.0.0 或更高版本
 * - Kotlin/JS 编译的 SDK
 */

// ========== 配置常量 ==========
const val CLIENT_ID = "vap_xxxxxxxxxxxxxxxx"
const val CLIENT_SECRET = "your-client-secret-here"
const val API_KEY = "your-api-key-here"

// ========== main 函数 ==========

fun main() = runBlocking {
    println("=== Fursuit.TV SDK Kotlin/JS 示例 ===\n")

    // 演示初始化方式
    println("--- 初始化方式 ---")
    example1_dslTokenExchange()
    example2_apiKey()

    // 创建 SDK 实例进行 API 调用演示
    println("\n--- API 模块调用 ---")
    val sdk = fursuitTvSdk {
        clientId = CLIENT_ID
        clientSecret = CLIENT_SECRET
    }

    try {
        exampleBaseApi(sdk)
        exampleUserApi(sdk)
        exampleSearchApi(sdk)
        exampleGatheringApi(sdk)
        exampleSchoolApi(sdk)

        println("\n--- 高级功能 ---")
        exampleErrorHandling()
        exampleConcurrentCalls(sdk)
    } finally {
        sdk.close()
        println("\n✓ SDK 已关闭")
    }
}

// ========== 初始化方式示例 ==========

/**
 * 方式 1: DSL 签名交换（推荐）
 */
suspend fun example1_dslTokenExchange() {
    println("\n[方式1] DSL 签名交换")

    val sdk = fursuitTvSdk {
        clientId = CLIENT_ID
        clientSecret = CLIENT_SECRET
    }

    try {
        val authenticated = sdk.auth.isAuthenticated()
        println("  认证状态: $authenticated")

        sdk.auth.getApiKey()?.let {
            println("  API Key (前20字符): ${it.take(20)}...")
        }
    } finally {
        sdk.close()
        println("  ✓ SDK 已关闭")
    }
}

/**
 * 方式 2: 已有 apiKey
 */
suspend fun example2_apiKey() {
    println("\n[方式2] 已有 apiKey")
    println("  (需要真实 apiKey 才能运行)")
}

// ========== API 模块调用示例 ==========

/**
 * Base API - 基础接口
 */
suspend fun exampleBaseApi(sdk: FursuitTvSdk) {
    println("\n[Base API] 基础接口")

    try {
        val health = sdk.base.health()
        println("  服务状态: ${health.status}")

        val version = sdk.base.getAndroidVersion()
        println("  最新 Android 版本: ${version.version}")

        val manifest = sdk.base.getThemePacksManifest()
        println("  主题包数量: ${manifest.packs.size}")
    } catch (e: FursuitTvSdkException) {
        println("  ✗ 错误: ${e.message}")
    }
}

/**
 * User API - 用户资料
 */
suspend fun exampleUserApi(sdk: FursuitTvSdk) {
    println("\n[User API] 用户资料")

    try {
        val username = "exampleUser"

        // 获取用户资料
        val profile = sdk.user.getUserProfile(username)
        println("  显示名称: ${profile.displayName}")
        println("  物种: ${profile.species}")

        // 获取用户 ID
        val userIdData = sdk.user.getUserId(username)
        println("  用户 ID: $userIdData")

        // 点赞状态（只传 username）
        if (userIdData != null) {
            val likeStatus = sdk.user.getLikeStatus(username)
            println("  点赞数: ${likeStatus.likeCount}")
        }

        // 用户关系（需要 userId）
        if (userIdData != null) {
            val relationships = sdk.user.getUserRelationships(userIdData.userId)
            println("  关系列表数量: ${relationships.relationships.size}")
        }

        // 访客记录（传 username）
        val visitors = sdk.user.getUserVisitors(username)
        println("  访客数量: ${visitors.visitors.size}")

        // 社交徽章（传 username）
        val badges = sdk.user.getSocialBadges(username)
        println("  徽章数量: ${badges.badges.size}")

        // 商店商品（传 username）
        val products = sdk.user.getStoreProducts(username)
        println("  商品数量: ${products.products.size}")
    } catch (e: FursuitTvSdkException) {
        println("  ✗ 错误: ${e.message}")
    }
}

/**
 * Search API - 搜索发现
 */
suspend fun exampleSearchApi(sdk: FursuitTvSdk) {
    println("\n[Search API] 搜索发现")

    try {
        val popular = sdk.search.getPopular()
        println("  热门用户数量: ${popular.users.size}")

        val randomFursuits = sdk.search.getRandomFursuit(count = 5, personalized = true)
        println("  随机兽装数量: ${randomFursuits.size}")

        val searchResult = sdk.search.search(query = "fox", type = "user", limit = 10)
        println("  搜索结果数量: ${searchResult.results.size}")
        searchResult.cursor?.let { println("  下一页游标: $it") }

        val suggestions = sdk.search.getSearchSuggestions("fox")
        println("  搜索建议数量: ${suggestions.size}")

        val speciesList = sdk.search.getSpeciesList()
        println("  物种数量: ${speciesList.species.size}")

        val locations = sdk.search.getPopularLocations()
        println("  热门地区数量: ${locations.locations.size}")
    } catch (e: FursuitTvSdkException) {
        println("  ✗ 错误: ${e.message}")
    }
}

/**
 * Gathering API - 聚会活动
 */
suspend fun exampleGatheringApi(sdk: FursuitTvSdk) {
    println("\n[Gathering API] 聚会活动")

    try {
        val yearStats = sdk.gathering.getYearStats()
        println("  统计年份: ${yearStats.year}")
        println("  总聚会数: ${yearStats.totalGatherings}")

        val monthlyGatherings = sdk.gathering.getMonthly(year = 2025, month = 4)
        println("  2025年4月聚会数量: ${monthlyGatherings.size}")

        val nearbyGatherings = sdk.gathering.getNearby(
            lat = 37.7749,
            lng = -122.4194,
            radius = 10000
        )
        println("  附近聚会数量: ${nearbyGatherings.size}")
    } catch (e: FursuitTvSdkException) {
        println("  ✗ 错误: ${e.message}")
    }
}

/**
 * School API - 学校角色
 */
suspend fun exampleSchoolApi(sdk: FursuitTvSdk) {
    println("\n[School API] 学校角色")

    try {
        val schoolSearchResult = sdk.school.searchSchools(query = "大学", limit = 10)
        println("  搜索结果总数: ${schoolSearchResult.total}")

        val userSchools = sdk.school.getUserSchools(username = "exampleUser")
        println("  用户学校数量: ${userSchools.schools.size}")

        val userCharacters = sdk.school.getUserCharacters(username = "exampleUser")
        println("  用户角色数量: ${userCharacters.characters.size}")
    } catch (e: FursuitTvSdkException) {
        println("  ✗ 错误: ${e.message}")
    }
}

// ========== 高级功能示例 ==========

/**
 * 错误处理示例
 */
suspend fun exampleErrorHandling() {
    println("\n[错误处理] 异常捕获示例")

    val sdk = fursuitTvSdk {
        clientId = CLIENT_ID
        clientSecret = CLIENT_SECRET
    }

    try {
        sdk.user.getUserId("this_user_definitely_does_not_exist_12345")
    } catch (e: TokenExpiredException) {
        println("  ✓ 令牌过期异常: ${e.message}")
    } catch (e: AuthenticationException) {
        println("  ✓ 认证失败异常: ${e.message}")
    } catch (e: NotFoundException) {
        println("  ✓ 资源未找到异常: ${e.message}")
    } catch (e: ValidationException) {
        println("  ✓ 参数验证异常: ${e.message}")
    } catch (e: NetworkException) {
        println("  ✓ 网络连接异常: ${e.message}")
    } catch (e: ApiException) {
        println("  ✓ API 错误 (HTTP ${e.statusCode}): ${e.message}")
    } catch (e: FursuitTvSdkException) {
        println("  ✓ 其他 SDK 异常: ${e.javaClass.simpleName} - ${e.message}")
    } finally {
        sdk.close()
    }
}

/**
 * 并发调用示例
 */
suspend fun exampleConcurrentCalls(sdk: FursuitTvSdk) {
    println("\n[并发调用] async/awaitAll 示例")

    val usernames = listOf("user1", "user2", "user3")

    val startTime = System.currentTimeMillis()

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
