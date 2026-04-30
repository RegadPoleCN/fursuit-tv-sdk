import com.furrist.rp.furtv.sdk.FursuitTvSdk
import com.furrist.rp.furtv.sdk.exception.ApiException
import com.furrist.rp.furtv.sdk.exception.AuthenticationException
import com.furrist.rp.furtv.sdk.exception.FursuitTvSdkException
import com.furrist.rp.furtv.sdk.exception.NetworkException
import com.furrist.rp.furtv.sdk.exception.NotFoundException
import com.furrist.rp.furtv.sdk.exception.TokenExpiredException
import com.furrist.rp.furtv.sdk.exception.ValidationException
import com.furrist.rp.furtv.sdk.model.SdkLogLevel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

const val CLIENT_ID = "vap_xxxxxxxxxxxxxxxx"
const val CLIENT_SECRET = "your-client-secret-here"
const val API_KEY = "your-api-key-here"

fun main() {
    println("=== Fursuit.TV SDK Kotlin/JS 示例 ===\n")

    GlobalScope.launch {
        example1_tokenExchange()
        example2_apiKey()
        example3_errorHandling()
    }
}

suspend fun example1_tokenExchange() {
    println("\n[方式1] DSL 签名交换")

    val sdk = FursuitTvSdk.create {
        clientId = CLIENT_ID
        clientSecret = CLIENT_SECRET
        logLevel = SdkLogLevel.INFO
    }

    try {
        val authenticated = sdk.auth.isAuthenticated()
        println("  认证状态: $authenticated")

        sdk.auth.getApiKey()?.let {
            println("  API Key (前20字符): ${it.take(20)}...")
        }

        val health = sdk.base.health()
        println("  服务状态: ${health.message}")

        val profile = sdk.user.getUserProfile("username")
        println("  用户名: ${profile.username}")
        println("  昵称: ${profile.nickname}")

        val popular = sdk.search.getPopular()
        println("  热门用户数量: ${popular.users.size}")

        val yearStats = sdk.gathering.getYearStats()
        println("  聚会总数: ${yearStats.total}")

        val nearbyMode = sdk.gathering.getNearbyMode()
        println("  附近聚会数量: ${nearbyMode.gatherings.size}")

        val speciesList = sdk.search.getSpeciesList()
        println("  物种数量: ${speciesList.species.size}")

        val schoolSearch = sdk.school.searchSchools(query = "大学", limit = 10)
        println("  学校搜索结果: ${schoolSearch.schools.size}")

        println("\n--- 并发调用 ---")
        exampleConcurrentCalls(sdk)
    } finally {
        sdk.close()
        println("\n✓ SDK 已关闭")
    }
}

suspend fun example2_apiKey() {
    println("\n[方式2] 已有 apiKey")

    val sdk = FursuitTvSdk.create(API_KEY)

    try {
        val health = sdk.base.health()
        println("  服务状态: ${health.message}")
    } finally {
        sdk.close()
    }
}

suspend fun example3_errorHandling() {
    println("\n[错误处理] 异常捕获示例")

    val sdk = FursuitTvSdk.create {
        clientId = CLIENT_ID
        clientSecret = CLIENT_SECRET
    }

    try {
        sdk.user.getUserProfile("this_user_definitely_does_not_exist_12345")
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
        println("  ✓ 其他 SDK 异常: ${e.message}")
    } finally {
        sdk.close()
    }
}

suspend fun exampleConcurrentCalls(sdk: FursuitTvSdk) {
    println("\n[并发调用] async/awaitAll 示例")

    val usernames = listOf("user1", "user2", "user3")

    val results = coroutineScope {
        usernames.map { username ->
            async {
                try {
                    sdk.user.getUserProfile(username)
                } catch (e: FursuitTvSdkException) {
                    println("  查询 $username 失败: ${e.message}")
                    null
                }
            }
        }.awaitAll()
    }

    val successCount = results.filterNotNull().size
    println("  成功: $successCount/${usernames.size}")
}
