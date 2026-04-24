package com.furrist.rp.furtv.sdk.http

import com.furrist.rp.furtv.sdk.exception.ApiException
import com.furrist.rp.furtv.sdk.exception.AuthenticationException
import com.furrist.rp.furtv.sdk.exception.NetworkException
import com.furrist.rp.furtv.sdk.exception.NotFoundException
import com.furrist.rp.furtv.sdk.exception.TokenExpiredException
import com.furrist.rp.furtv.sdk.exception.ValidationException
import com.furrist.rp.furtv.sdk.model.SdkConfig
import io.ktor.client.HttpClient
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.HttpRequestRetry
import io.ktor.client.plugins.HttpResponseValidator
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.Logging
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

/**
 * HTTP 客户端配置，提供 Ktor 客户端的创建和配置功能。
 */
public object HttpClientConfig {
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

    // Chrome User-Agent 字符串，用于模拟浏览器请求
    private const val USER_AGENT_CHROME =
        "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 " +
            "(KHTML, like Gecko) Chrome/131.0.0.0 Safari/537.36"

    /**
     * 创建配置好的 HTTP 客户端。
     *
     * @param config SDK 配置
     * @param accessToken 可选的访问令牌
     * @param requestIdGenerator 请求 ID 生成器，默认为随机生成
     * @return 配置好的 HttpClient 实例
     */
    public fun createClient(
        config: SdkConfig,
        accessToken: String? = null,
        requestIdGenerator: () -> String = { generateRequestId() },
    ): HttpClient {
        return HttpClient {
            install(ContentNegotiation) {
                json(
                    Json {
                        ignoreUnknownKeys = true
                        prettyPrint = true
                        isLenient = true
                    },
                )
            }

            install(Logging) {
                level = config.logLevel
            }

            install(DefaultRequest) {
                headers {
                    /**
                     * 认证头自动选择逻辑：
                     *
                     * 模式 1: config.apiKey 存在且非空
                     *   → 使用 X-Api-Key 头（适用于用户直接提供 apiKey 的场景）
                     *
                     * 模式 2: accessToken 存在（apiKey 为空时）
                     *   → 使用 Authorization: Bearer 头
                     *   （适用于签名交换后或 OAuth 后的场景）
                     *
                     * 模式 3: 都为空
                     *   → 不设置认证头（适用于未认证状态，如签名交换接口本身）
                     *
                     * 依据：官方文档《认证方式与服务器端点》说明：
                     *   "同时传入 X-Api-Key 和 Authorization Bearer 时，
                     *    服务端优先使用 X-Api-Key"
                     */
                    when {
                        config.apiKey != null && config.apiKey.isNotEmpty() -> {
                            append("X-Api-Key", config.apiKey)
                        }
                        accessToken != null -> {
                            append("Authorization", "Bearer $accessToken")
                        }
                        else -> {
                            // 无认证头，用于未认证状态（如签名交换接口）
                        }
                    }

                    append("X-Request-ID", requestIdGenerator())
                    contentType(ContentType.Application.Json)
                    append("Accept", "application/json")
                    // 强制使用 Chrome UA，确保服务端兼容性
                    append("User-Agent", USER_AGENT_CHROME)
                }
            }

            HttpResponseValidator {
                validateResponse { response ->
                    validateStatusCode(response.status.value)
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
    }

    /**
     * 验证 HTTP 状态码是否在成功范围内（200-299）。
     *
     * @param statusCode HTTP 响应状态码
     * @throws ApiException 当状态码表示错误时
     */
    private fun validateStatusCode(statusCode: Int) {
        if (statusCode !in SUCCESS_STATUS_START..SUCCESS_STATUS_END) {
            val errorBody = getErrorBody()
            throwExceptionForStatusCode(statusCode, errorBody)
        }
    }

    /**
     * 获取错误响应体。
     *
     * @return 错误响应体内容，当前实现返回空字符串
     */
    private fun getErrorBody(): String? = ERROR_BODY_EMPTY

    /**
     * 根据 HTTP 状态码抛出相应异常。
     *
     * @param statusCode HTTP 响应状态码
     * @param errorBody 错误响应体内容
     * @throws TokenExpiredException 当状态码为 401 时
     * @throws AuthenticationException 当状态码为 403 时
     * @throws NotFoundException 当状态码为 404 时
     * @throws ValidationException 当状态码为 400 时
     * @throws ApiException 当状态码为 5xx 或其他错误码时
     */
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

    /**
     * 处理响应异常，将 Ktor 底层异常转换为 SDK 定义的异常类型。
     *
     * @param cause 原始异常
     * @throws NetworkException 当遇到未知异常时
     */
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

    /**
     * 生成 16 位随机字符串作为请求 ID。
     *
     * @return 随机生成的请求 ID，由大小写字母和数字组成
     */
    private fun generateRequestId(): String {
        val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"
        return (1..REQUEST_ID_LENGTH)
            .map { chars.random() }
            .joinToString("")
    }
}
