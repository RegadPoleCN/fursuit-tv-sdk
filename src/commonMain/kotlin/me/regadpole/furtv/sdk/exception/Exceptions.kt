package me.regadpole.furtv.sdk.exception

/**
 * SDK 基础异常类
 * 所有 SDK 相关的父类
 * @param message 异常消息
 * @param cause 异常原因
 */
public open class FursuitTvSdkException(
    message: String,
    cause: Throwable? = null,
) : Exception(message, cause)

/**
 * API 调用异常
 * 表示 API 调用过程中发生的错误
 * @param statusCode HTTP 状态码
 * @param message 错误消息
 * @param errorCode 错误代码（可选）
 * @param cause 异常原因
 */
public class ApiException(
    public val statusCode: Int,
    message: String,
    public val errorCode: String? = null,
    cause: Throwable? = null,
) : FursuitTvSdkException(message, cause)

/**
 * 网络连接异常
 * 表示网络连接或通信过程中发生的错误
 * @param message 错误消息
 * @param cause 异常原因
 */
public class NetworkException(
    message: String,
    cause: Throwable? = null,
) : FursuitTvSdkException(message, cause)

/**
 * 令牌过期异常
 * 表示访问令牌已过期，需要刷新或重新认证
 * @param message 错误消息
 * @param cause 异常原因
 */
public class TokenExpiredException(
    message: String = "Access token has expired",
    cause: Throwable? = null,
) : FursuitTvSdkException(message, cause)

/**
 * 认证异常
 * 表示认证或授权过程中发生的错误
 * @param message 错误消息
 * @param cause 异常原因
 */
public class AuthenticationException(
    message: String,
    cause: Throwable? = null,
) : FursuitTvSdkException(message, cause)

/**
 * 参数验证异常
 * 表示请求参数验证失败
 * @param message 错误消息
 * @param cause 异常原因
 */
public class ValidationException(
    message: String,
    cause: Throwable? = null,
) : FursuitTvSdkException(message, cause)

/**
 * 未找到资源异常
 * 表示请求的资源不存在
 * @param message 错误消息
 * @param cause 异常原因
 */
public class NotFoundException(
    message: String,
    cause: Throwable? = null,
) : FursuitTvSdkException(message, cause)
