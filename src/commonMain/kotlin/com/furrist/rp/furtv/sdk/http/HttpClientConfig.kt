package com.furrist.rp.furtv.sdk.http

import com.furrist.rp.furtv.sdk.auth.AuthHolder
import com.furrist.rp.furtv.sdk.exception.ApiException
import com.furrist.rp.furtv.sdk.exception.AuthenticationException
import com.furrist.rp.furtv.sdk.exception.NetworkException
import com.furrist.rp.furtv.sdk.exception.NotFoundException
import com.furrist.rp.furtv.sdk.exception.TokenExpiredException
import com.furrist.rp.furtv.sdk.exception.ValidationException
import com.furrist.rp.furtv.sdk.model.SdkConfig
import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpRequestRetry
import io.ktor.client.plugins.HttpResponseValidator
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest

import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.header
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

/**
 * HTTP 客户端配置（内部缓存工厂），提供 Ktor 客户端的创建和配置功能。
 *
 * 单例化：同 `(SdkConfig, AuthHolder)` 共享一个 [HttpClient] 实例，通过 Ktor `defaultRequest { header(...) }`
 * 在每个请求上决定认证头：
 *
 * 1. `authHolder.auth?.getApiKey()` 非空 → `X-Api-Key: <apiKey>`
 * 2. 全部为空 → 不发送认证头（用于 `/api/auth/token` 等未认证场景）
 *
 * 头 (1) 由 `defaultRequest` 通过 `AuthHolder.auth?.getApiKey()` 按请求自动注入（per `init-builder-refactor` D8）。
 * 调用方应使用 [FursuitTvSdk.auth] 的当前 TokenInfo 进行请求头注入。
 */
internal object HttpClientConfig {
    private const val REQUEST_ID_LENGTH = 16
    private const val SUCCESS_STATUS_START = 200
    private const val SUCCESS_STATUS_END = 299
    private const val SERVER_ERROR_START = 500
    private const val SERVER_ERROR_END = 599
    private const val UNAUTHORIZED = 401
    private const val FORBIDDEN = 403
    private const val NOT_FOUND = 404
    private const val BAD_REQUEST = 400
    private const val ERROR_BODY_EMPTY = ""
    private const val MAX_ERROR_BODY_LENGTH = 4096

    // Chrome User-Agent 字符串，用于模拟浏览器请求
    private const val USER_AGENT_CHROME =
        "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 " +
            "(KHTML, like Gecko) Chrome/131.0.0.0 Safari/537.36"

    /**
     * (SdkConfig, AuthHolder) → HttpClient 单例映射。
     * AuthHolder 加入缓存键——每个 SDK 实例独占自己的 HttpClient。
     */
    private val instance: MutableMap<Pair<SdkConfig, AuthHolder>, HttpClient> = mutableMapOf()

    /**
     * 单例工厂：为同一 `(SdkConfig, AuthHolder)` 始终返回同一个 [HttpClient] 实例。
     *
     * **已知限制**（fix-js-compile 简化）：不再加锁。JVM 上多线程并发首次访问同一 key 时
     * 可能并发调用 `buildClient`，导致多构造一个 HttpClient 但旧实例仍在缓存中被覆盖——
     * 行为正确，仅有轻微内存浪费。实际应用通常单 SDK 实例单线程，无影响。
     * JS / Native 单线程，无此问题。
     *
     * @param config SDK 配置
     * @param authHolder AuthHolder 引用（供 defaultRequest 闭包读取）
     * @return 配置好的 HttpClient 单例
     */
    internal fun getClient(config: SdkConfig, authHolder: AuthHolder): HttpClient =
        instance.getOrPut(config to authHolder) { buildClient(config, authHolder) }

    internal fun buildClient(config: SdkConfig, authHolder: AuthHolder): HttpClient =
        HttpClient {
            install(ContentNegotiation) {
                json(
                    Json {
                        ignoreUnknownKeys = true
                        prettyPrint = false
                        isLenient = true
                    },
                )
            }

            install(Logging) {
                level = config.logLevel.toKtorLogLevel()
            }

            defaultRequest {
                contentType(ContentType.Application.Json)
                header("Accept", "application/json")
                header("X-Request-ID", generateRequestId())
                header("User-Agent", USER_AGENT_CHROME)

                // ✅ 每个请求从 AuthHolder 读取最新 apiKey（init-builder-refactor D8）
                authHolder.auth?.getApiKey()?.let { apiKey ->
                    header("X-Api-Key", apiKey)
                }
            }

            HttpResponseValidator {
                validateResponse { response ->
                    validateStatusCode(response)
                }

                handleResponseExceptionWithRequest { cause, _ ->
                    handleResponseException(cause)
                }
            }

            install(HttpTimeout) {
                requestTimeoutMillis = config.requestTimeout
                connectTimeoutMillis = config.connectTimeout
                socketTimeoutMillis = config.socketTimeout
            }

            if (config.enableRetry) {
                install(HttpRequestRetry) {
                    maxRetries = config.maxRetries
                    retryOnExceptionOrServerErrors()
                    delayMillis { attempt ->
                        config.retryInterval * attempt
                    }
                }
            }
        }

    /**
     * 验证 HTTP 状态码是否在成功范围内（200-299），否则抛出对应异常。
     */
    private suspend fun validateStatusCode(response: io.ktor.client.statement.HttpResponse) {
        if (response.status.value !in SUCCESS_STATUS_START..SUCCESS_STATUS_END) {
            val errorBody = readErrorBody(response)
            throwExceptionForStatusCode(response.status.value, errorBody)
        }
    }

    private suspend fun readErrorBody(response: io.ktor.client.statement.HttpResponse): String? =
        try {
            response.bodyAsText().take(MAX_ERROR_BODY_LENGTH)
        } catch (_: Exception) {
            ERROR_BODY_EMPTY
        }

    private fun throwExceptionForStatusCode(statusCode: Int, errorBody: String?) {
        val errorMessage = errorBody ?: "Unknown error"
        val exception =
            when (statusCode) {
                UNAUTHORIZED -> TokenExpiredException("Authentication failed: $errorMessage")
                FORBIDDEN -> AuthenticationException("Access forbidden: $errorMessage")
                NOT_FOUND -> NotFoundException("Resource not found: $errorMessage")
                BAD_REQUEST -> ValidationException("Invalid request: $errorMessage")
                in SERVER_ERROR_START..SERVER_ERROR_END ->
                    ApiException(
                        statusCode,
                        "Server error: $errorMessage",
                    )
                else ->
                    ApiException(
                        statusCode,
                        "HTTP error $statusCode: $errorMessage",
                    )
            }
        throw exception
    }

    private fun handleResponseException(cause: Throwable): Nothing {
        when (cause) {
            is TokenExpiredException,
            is AuthenticationException,
            is NotFoundException,
            is ValidationException,
            is ApiException,
            -> throw cause
            else -> throw NetworkException("Network error: ${cause.message}", cause)
        }
    }

    private fun generateRequestId(): String {
        val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"
        return (1..REQUEST_ID_LENGTH)
            .map { chars.random() }
            .joinToString("")
    }
}