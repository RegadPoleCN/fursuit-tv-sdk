/*
 *   Copyright 2026 RegadPoleCN
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.furrist.rp.furtv.sdk.http

import com.furrist.rp.furtv.sdk.exception.*
import com.furrist.rp.furtv.sdk.model.SdkConfig
import io.ktor.client.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive

/**
 * 通用服务端错误响应体结构，用于结构化提取错误码与请求追踪 ID。
 */
@Serializable
internal data class ApiErrorResponse(
    val success: Boolean = false,
    val message: String? = null,
    val errorCode: String? = null,
    val code: String? = null,
    val requestId: String? = null,
)

/**
 * HTTP 客户端工厂，提供 Ktor 客户端的创建和通用配置。
 *
 * [REFACTORED in v0.5.0]
 * 旧代码通过静态 Map (instance) 尝试进行跨实例缓存，导致伪单例与不可逆的内存泄漏。
 * 现已重构为无状态工厂：每个 [com.furrist.rp.furtv.sdk.FursuitTvSdk] 实例独立持有自身的 [HttpClient] 并管控其生命周期。
 */
internal object HttpClientConfig {
    private val errorJson =
        Json {
            ignoreUnknownKeys = true
            isLenient = true
        }
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
     * 构建由 SDK 实例专属持有的 [HttpClient]。
     *
     * @param config SDK 配置
     * @param apiKeyProvider 动态提供当前最新 platform apiKey 的回调函数
     * @return 配置好的 HttpClient
     */
    internal fun createClient(
        config: SdkConfig,
        apiKeyProvider: () -> String?,
    ): HttpClient =
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
                header("User-Agent", USER_AGENT_CHROME)

                // 每个请求从 apiKeyProvider 读取最新 apiKey，实现认证头的按请求自动注入
                // /account/sso/* 端点无需任何平台签名头（vds-docs：VDS账户各篇"无需任何开放平台签名"）
                if (!url.encodedPath.contains("/account/sso/")) {
                    apiKeyProvider()?.let { apiKey ->
                        header("X-Api-Key", apiKey)
                    }
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
    private suspend fun validateStatusCode(response: HttpResponse) {
        if (response.status.value !in SUCCESS_STATUS_START..SUCCESS_STATUS_END) {
            val errorBody = readErrorBody(response)
            val path = response.request.url.encodedPath
            // sso 端点（OAuth token / userinfo）错误体为 {error, error_description}，结构化抛出
            if (path.contains("/account/sso/")) {
                throwOAuthError(response.status.value, errorBody)
            }
            throwExceptionForStatusCode(response.status.value, errorBody, path)
        }
    }

    private suspend fun readErrorBody(response: HttpResponse): String? =
        try {
            response.bodyAsText().take(MAX_ERROR_BODY_LENGTH)
        } catch (_: Exception) {
            ERROR_BODY_EMPTY
        }

    private fun throwExceptionForStatusCode(
        statusCode: Int,
        errorBody: String?,
        requestPath: String,
    ) {
        val errorDto =
            errorBody?.let { body ->
                runCatching { errorJson.decodeFromString<ApiErrorResponse>(body) }.getOrNull()
            }
        val errorMessage = errorDto?.message ?: errorBody ?: "Unknown error"
        val errorCode = errorDto?.errorCode ?: errorDto?.code

        val exception =
            when (statusCode) {
                UNAUTHORIZED -> {
                    // 若是签名交换接口本身报 401，表明凭证错误，必须抛 AuthenticationException 阻断重试循环！
                    if (requestPath.endsWith("/api/auth/token")) {
                        AuthenticationException("Authentication failed (Invalid clientId or clientSecret): $errorMessage")
                    } else {
                        TokenExpiredException("Authentication failed: $errorMessage")
                    }
                }
                FORBIDDEN -> AuthenticationException("Access forbidden: $errorMessage")
                NOT_FOUND -> NotFoundException("Resource not found: $errorMessage")
                BAD_REQUEST -> ValidationException("Invalid request: $errorMessage")
                in SERVER_ERROR_START..SERVER_ERROR_END ->
                    ApiException(
                        statusCode,
                        "Server error: $errorMessage",
                        errorCode = errorCode,
                    )
                else ->
                    ApiException(
                        statusCode,
                        "HTTP error $statusCode: $errorMessage",
                        errorCode = errorCode,
                    )
            }
        throw exception
    }

    /**
     * 解析 sso 端点的 OAuth 错误体 `{error, error_description}`（签名交换端点.md:66-72、
     * 用户信息端点.md:70-75），结构化抛出 [OAuthException]；解析失败回落为通用消息。
     */
    private fun throwOAuthError(statusCode: Int, errorBody: String?): Nothing {
        val element = errorBody?.let { body -> runCatching { errorJson.parseToJsonElement(body) }.getOrNull() }
        val obj = element as? JsonObject
        val errorCode = (obj?.get("error") as? JsonPrimitive)?.content
        val errorDescription = (obj?.get("error_description") as? JsonPrimitive)?.content
        throw OAuthException(
            message = errorDescription ?: "OAuth request failed (HTTP $statusCode): ${errorBody ?: "Unknown error"}",
            errorCode = errorCode,
        )
    }

    private fun handleResponseException(cause: Throwable): Nothing {
        when (cause) {
            is TokenExpiredException,
            is AuthenticationException,
            is NotFoundException,
            is ValidationException,
            is ApiException,
            is OAuthException,
            -> throw cause
            else -> throw NetworkException("Network error: ${cause.message}", cause)
        }
    }
}
