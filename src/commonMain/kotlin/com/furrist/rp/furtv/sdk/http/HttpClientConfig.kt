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

import com.furrist.rp.furtv.sdk.auth.AuthHolder
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
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive

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

    // #25：sso 错误体解析用（与生产 ContentNegotiation 宽容度一致）
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
                // /account/sso/* 端点无需任何平台签名头（vds-docs：VDS账户各篇"无需任何开放平台签名"）
                if (!url.encodedPath.contains("/account/sso/")) {
                    authHolder.auth?.getApiKey()?.let { apiKey ->
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
    private suspend fun validateStatusCode(response: io.ktor.client.statement.HttpResponse) {
        if (response.status.value !in SUCCESS_STATUS_START..SUCCESS_STATUS_END) {
            val errorBody = readErrorBody(response)
            // #25：sso 端点（OAuth token / userinfo）错误体为 {error, error_description}，结构化抛出
            if (response.request.url.encodedPath.contains("/account/sso/")) {
                throwOAuthError(response.status.value, errorBody)
            }
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

    /**
     * #25：解析 sso 端点的 OAuth 错误体 `{error, error_description}`（签名交换端点.md:66-72、
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

    private fun generateRequestId(): String {
        val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"
        return (1..REQUEST_ID_LENGTH)
            .map { chars.random() }
            .joinToString("")
    }
}
