package com.furrist.rp.furtv.sdk.exception

import kotlin.js.JsExport
import kotlin.js.JsName

/**
 * SDK 基础异常类，所有 Fursuit.TV SDK 异常的父类。
 *
 * @param message 异常详情消息
 * @param cause 根本原因，可能为 null
 */
@JsExport
@JsName("FursuitTvSdkException")
public open class FursuitTvSdkException(
    message: String,
    cause: Throwable? = null,
) : Exception(message, cause)

/**
 * API 调用异常，表示服务器返回了 4xx/5xx 业务错误。
 *
 * @param statusCode HTTP 状态码
 * @param message 服务器返回的错误消息
 * @param errorCode 业务错误代码（可选）
 * @param cause 底层异常原因，可能为 null
 */
@JsExport
@JsName("ApiException")
public class ApiException(
    @JsName("statusCode") public val statusCode: Int,
    message: String,
    @JsName("errorCode") public val errorCode: String? = null,
    cause: Throwable? = null,
) : FursuitTvSdkException(message, cause)

/**
 * 网络连接异常，表示无法建立或维持与服务器的网络连接。
 *
 * @param message 错误描述
 * @param cause 根本原因，可能为 null
 */
@JsExport
@JsName("NetworkException")
public class NetworkException(
    message: String,
    cause: Throwable? = null,
) : FursuitTvSdkException(message, cause)

/**
 * 令牌过期异常，表示访问令牌已过期且自动刷新失败。
 *
 * @param message 异常详情消息
 * @param cause 根本原因，可能为 null
 */
@JsExport
@JsName("TokenExpiredException")
public class TokenExpiredException(
    message: String = "Access token has expired",
    cause: Throwable? = null,
) : FursuitTvSdkException(message, cause)

/**
 * 认证异常，表示凭证无效或权限不足（非令牌过期）。
 *
 * @param message 认证失败的详细描述
 * @param cause 底层原因，可能为 null
 */
@JsExport
@JsName("AuthenticationException")
public class AuthenticationException(
    message: String,
    cause: Throwable? = null,
) : FursuitTvSdkException(message, cause)

/**
 * 参数验证异常，表示请求参数未通过验证规则。
 *
 * @param message 验证失败的详细说明
 * @param cause 底层原因，可能为 null
 */
@JsExport
@JsName("ValidationException")
public class ValidationException(
    message: String,
    cause: Throwable? = null,
) : FursuitTvSdkException(message, cause)

/**
 * 未找到资源异常，表示请求的资源在服务器上不存在。
 *
 * @param message 资源未找到的详细说明
 * @param cause 底层原因，可能为 null
 */
@JsExport
@JsName("NotFoundException")
public class NotFoundException(
    message: String,
    cause: Throwable? = null,
) : FursuitTvSdkException(message, cause)

/**
 * OAuth 流程异常，表示授权码无效、令牌交换失败或用户拒绝授权等。
 *
 * @param message 异常详情消息
 * @param errorCode OAuth 错误代码（如 "invalid_grant"、"access_denied"），可能为 null
 * @param cause 根本原因，可能为 null
 */
@JsExport
@JsName("OAuthException")
public class OAuthException(
    message: String,
    @JsName("errorCode") public val errorCode: String? = null,
    cause: Throwable? = null,
) : FursuitTvSdkException(message, cause)
