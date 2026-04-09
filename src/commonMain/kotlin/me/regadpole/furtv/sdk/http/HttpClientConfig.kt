package me.regadpole.furtv.sdk.http

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
import me.regadpole.furtv.sdk.exception.ApiException
import me.regadpole.furtv.sdk.exception.AuthenticationException
import me.regadpole.furtv.sdk.exception.NetworkException
import me.regadpole.furtv.sdk.exception.NotFoundException
import me.regadpole.furtv.sdk.exception.TokenExpiredException
import me.regadpole.furtv.sdk.exception.ValidationException
import me.regadpole.furtv.sdk.model.SdkConfig

/**
 * HTTP 客户端配置
 * 负责创建和配置 Ktor HTTP 客户端，包括序列化、日志、认证、重试等功能
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

    /**
     * 创建配置好的 HTTP 客户端
     * 配置包括：JSON 序列化、日志、默认请求头、响应验证、超时、重试等
     * @param config SDK 配置对象
     * @param accessToken 可选的访问令牌，用于认证请求
     * @param useApiKeyOnly 是否仅使用 X-Api-Key 认证（true=仅 X-Api-Key，false=使用 Authorization Bearer）
     * @param requestIdGenerator 请求 ID 生成器，默认为随机生成
     * @return 配置好的 HttpClient 实例
     */
    public fun createClient(
        config: SdkConfig,
        accessToken: String? = null,
        useApiKeyOnly: Boolean = true,
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
                    // 认证头选择逻辑：
                    // 1. useApiKeyOnly=true（默认）：仅使用 X-Api-Key（签名交换/换新场景）
                    // 2. useApiKeyOnly=false 且有 accessToken：使用 Authorization Bearer（OAuth 场景）
                    // 3. 两者都有：优先使用 X-Api-Key（服务端行为）
                    if (useApiKeyOnly || accessToken == null) {
                        // 使用 X-Api-Key（签名交换/换新）
                        append("X-Api-Key", config.apiKey)
                    } else {
                        // 使用 Authorization Bearer（OAuth/accessToken）
                        append("Authorization", "Bearer $accessToken")
                    }

                    append("X-Request-ID", requestIdGenerator())
                    contentType(ContentType.Application.Json)
                    append("Accept", "application/json")
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
     * 验证 HTTP 状态码
     * 检查响应状态码是否在成功范围内（200-299），如果不是则抛出相应异常
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
     * 获取错误响应体（简化处理）
     * 当前实现返回空字符串，实际项目中可从响应中解析错误信息
     * @return 错误响应体内容，当前为空字符串
     */
    private fun getErrorBody(): String? = ERROR_BODY_EMPTY

    /**
     * 根据状态码抛出相应异常
     * 将 HTTP 状态码映射为具体的异常类型，便于上层处理
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
     * 处理响应异常
     * 将 Ktor 的底层网络异常转换为 SDK 定义的异常类型
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
     * 生成请求 ID
     * 生成 16 位随机字符串作为请求 ID，用于日志排查和请求追踪
     * @return 随机生成的请求 ID，由大小写字母和数字组成
     */
    private fun generateRequestId(): String {
        val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"
        return (1..REQUEST_ID_LENGTH)
            .map { chars.random() }
            .joinToString("")
    }
}
