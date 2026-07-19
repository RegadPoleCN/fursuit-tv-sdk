package com.furrist.rp.furtv.sdk.http

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
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlin.js.JsExport
import kotlin.js.JsName
import kotlinx.serialization.json.Json

/**
 * HTTP 客户端配置，提供 Ktor 客户端的创建和配置功能。
 *
 * 单例化：同 [SdkConfig] 共享一个 [HttpClient] 实例，通过 Ktor `defaultRequest { header(...) }`
 * 在每个请求上决定认证头：
 *
 * 1. `config.apiKey` 非空 → `X-Api-Key: <config.apiKey>`
 * 2. 否则当 AuthManager 当前 `TokenInfo.apiKey` 非空 → `X-Api-Key: <tokenInfo.apiKey>`
 * 3. 否则当 `TokenInfo.accessToken` 非空 → `Authorization: Bearer <tokenInfo.accessToken>`
 * 4. 全部为空 → 不发送认证头（用于 `/api/auth/token` 等未认证场景）
 *
 * 头 (2)(3) 由调用方在每个 API 请求间使用 [applyAuthHeaders] 显式注入；
 * 调用方应使用 [FursuitTvSdk.auth] 的当前 TokenInfo 进行请求头注入。
 */
@JsExport
@JsName("HttpClientConfig")
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
     * SdkConfig → HttpClient 单例映射。
     */
    private val instance: MutableMap<SdkConfig, HttpClient> = mutableMapOf()

    /**
     * 单例工厂：为同一 [SdkConfig] 始终返回同一个 [HttpClient] 实例。
     *
     * 第一次调用该方法创建新实例；后续调用直接返回缓存的客户端。
     *
     * @param config SDK 配置
     * @return 配置好的 HttpClient 单例
     */
    @JsName("getClient")
    @Suppress("NON_EXPORTABLE_TYPE")
    public fun getClient(config: SdkConfig): HttpClient {
        return instance.getOrPut(config) { buildClient(config) }
    }

    @PublishedApi
    internal fun buildClient(config: SdkConfig): HttpClient =
        HttpClient {
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
                level = config.logLevel.toKtorLogLevel()
            }

            defaultRequest {
                contentType(ContentType.Application.Json)
                header("Accept", "application/json")
                header("X-Request-ID", generateRequestId())
                header("User-Agent", USER_AGENT_CHROME)
                if (!config.apiKey.isNullOrEmpty()) {
                    header("X-Api-Key", config.apiKey)
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

    /**
     * 验证 HTTP 状态码是否在成功范围内（200-299），否则抛出对应异常。
     */
    private fun validateStatusCode(statusCode: Int) {
        if (statusCode !in SUCCESS_STATUS_START..SUCCESS_STATUS_END) {
            val errorBody = getErrorBody()
            throwExceptionForStatusCode(statusCode, errorBody)
        }
    }

    private fun getErrorBody(): String? = ERROR_BODY_EMPTY

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
