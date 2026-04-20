# 错误处理

本文档详细说明 Fursuit.TV SDK 的异常类型和错误处理策略。

## 异常层次结构

```
Exception
└── FursuitTvSdkException
    ├── ApiException
    ├── NetworkException
    ├── TokenExpiredException
    ├── AuthenticationException
    ├── ValidationException
    └── NotFoundException
```

## 异常类型详解

### FursuitTvSdkException

所有 SDK 异常的基类。

```kotlin
public open class FursuitTvSdkException(
    message: String,
    cause: Throwable? = null,
) : Exception(message, cause)
```

### ApiException

API 调用错误，包含 HTTP 状态码。

```kotlin
public class ApiException(
    public val statusCode: Int,
    message: String,
    public val errorCode: String? = null,
    cause: Throwable? = null,
) : FursuitTvSdkException(message, cause)
```

**常见状态码**:

| 状态码 | 含义 | 处理建议 |
|--------|------|----------|
| 400 | 请求错误 | 检查请求参数 |
| 401 | 未授权 | 检查认证信息 |
| 403 | 禁止访问 | 检查权限 |
| 404 | 资源不存在 | 检查资源 ID |
| 429 | 请求过多 | 降低频率，稍后重试 |
| 500 | 服务器错误 | 稍后重试 |
| 502 | 网关错误 | 稍后重试 |
| 503 | 服务不可用 | 稍后重试 |

**示例**:

```kotlin
try {
    val profile = sdk.user.getUserProfile("username")
} catch (e: ApiException) {
    when (e.statusCode) {
        400 -> println("请求参数错误")
        401 -> println("未授权")
        404 -> println("用户不存在")
        429 -> println("请求过于频繁")
        in 500..599 -> println("服务器错误")
    }
}
```

### NetworkException

网络连接错误。

```kotlin
public class NetworkException(
    message: String,
    cause: Throwable? = null,
) : FursuitTvSdkException(message, cause)
```

**常见原因**:
- 连接超时
- DNS 解析失败
- SSL 握手失败
- 网络中断

**示例**:

```kotlin
try {
    val profile = sdk.user.getUserProfile("username")
} catch (e: NetworkException) {
    println("网络连接失败：${e.message}")
    // 1. 检查网络连接
    // 2. 增加超时时间
    // 3. 检查防火墙
}
```

### TokenExpiredException

令牌过期。

```kotlin
public class TokenExpiredException(
    message: String = "Token has expired",
    cause: Throwable? = null,
) : FursuitTvSdkException(message, cause)
```

**解决方案**:

```kotlin
try {
    val profile = sdk.user.getUserProfile("username")
} catch (e: TokenExpiredException) {
    // 刷新令牌
    val newToken = sdk.auth.refreshToken()
    // 或重新交换令牌
    val newToken = sdk.auth.exchangeToken(clientId, clientSecret)
}
```

### AuthenticationException

认证失败。

```kotlin
public class AuthenticationException(
    message: String,
    cause: Throwable? = null,
) : FursuitTvSdkException(message, cause)
```

**常见原因**:
- clientId/clientSecret 错误
- apiKey 无效
- 签名错误

### ValidationException

参数验证失败。

```kotlin
public class ValidationException(
    message: String,
    cause: Throwable? = null,
) : FursuitTvSdkException(message, cause)
```

**示例**:

```kotlin
try {
    sdk.user.getUserProfile("")  // 空用户名
} catch (e: ValidationException) {
    println("参数验证失败：${e.message}")
}
```

### NotFoundException

资源不存在。

```kotlin
public class NotFoundException(
    message: String,
    cause: Throwable? = null,
) : FursuitTvSdkException(message, cause)
```

**示例**:

```kotlin
try {
    val profile = sdk.user.getUserProfile("notfound")
} catch (e: NotFoundException) {
    println("用户不存在")
}
```

## 错误处理策略

### 1. 精确捕获异常

```kotlin
suspend fun getUserProfile(username: String): UserProfile? {
    return try {
        sdk.user.getUserProfile(username)
    } catch (e: NotFoundException) {
        logger.warn("用户不存在：$username")
        null
    } catch (e: NetworkException) {
        logger.error("网络错误", e)
        null
    } catch (e: ApiException) {
        logger.error("API 错误 (${e.statusCode})", e)
        null
    }
}
```

### 2. 使用 Result 封装

```kotlin
sealed class ApiResult<out T> {
    data class Success<T>(val data: T) : ApiResult<T>()
    data class Error(val exception: FursuitTvSdkException) : ApiResult<Nothing>()
}

suspend fun <T> runCatchingApi(block: suspend () -> T): ApiResult<T> {
    return try {
        ApiResult.Success(block())
    } catch (e: FursuitTvSdkException) {
        ApiResult.Error(e)
    }
}
```

### 3. 自动重试

```kotlin
suspend fun <T> withRetry(
    maxRetries: Int = 3,
    delayMs: Long = 1000,
    block: suspend () -> T
): T {
    var lastException: Exception? = null
    repeat(maxRetries) { attempt ->
        try {
            return block()
        } catch (e: NetworkException) {
            lastException = e
            delay(delayMs * (attempt + 1))  // 指数退避
        }
    }
    throw lastException!!
}
```

## 最佳实践

1. **精确捕获异常**：不要捕获所有 Exception
2. **记录详细日志**：包含异常堆栈和上下文
3. **友好的错误提示**：向用户显示易懂的消息
4. **适当的错误恢复**：自动重试、降级处理
5. **清理资源**：在 finally 块中关闭连接

## 相关文档

- [故障排除](troubleshooting.md) - 常见问题诊断
- [最佳实践](best-practices.md) - 错误处理最佳实践
