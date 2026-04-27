package com.furrist.rp.furtv.sdk.exception

import kotlin.js.JsExport
import kotlin.js.JsName

/**
 * SDK 基础异常类。
 *
 * 所有 Fursuit.TV SDK 异常的父类。建议在应用层统一捕获此异常，
 * 以便集中处理所有 SDK 相关的错误。
 *
 * ## 使用建议
 * - 在顶层或统一错误处理层 catch 此基类
 * - 通过 `when` 或 `if` 判断具体子类型以采取不同处理策略
 * - 记录完整的异常堆栈以便调试
 *
 * ## 触发场景
 * - 任何 SDK 操作失败时（网络、认证、验证等）
 * - 令牌过期、参数无效、资源不存在等情况
 *
 * ## 处理建议
 * - 记录日志：`logger.error("SDK error", exception)`
 * - 向用户展示友好错误信息（避免暴露技术细节）
 * - 根据具体子类型决定是否重试
 *
 * @param message 异常详情消息
 * @param cause 根本原因（如网络超时、HTTP 错误响应等），可能为 null
 * @see ApiException API 调用相关错误
 * @see NetworkException 网络连接问题
 * @see TokenExpiredException 令牌过期需刷新
 */
@JsExport
@JsName("FursuitTvSdkException")
public open class FursuitTvSdkException(
    message: String,
    cause: Throwable? = null,
) : Exception(message, cause)

/**
 * API 调用异常。
 *
 * 表示 HTTP 请求成功但服务器返回了业务错误（4xx/5xx 状态码）。
 * 包含 HTTP 状态码和可选的业务错误码，便于定位问题。
 *
 * ## 触发场景
 * - **400 Bad Request**: 参数格式错误、必填字段缺失
 * - **401 Unauthorized**: 认证凭证无效或已过期
 * - **403 Forbidden**: 权限不足，无法访问该资源
 * - **404 Not Found**: 请求的资源不存在
 * - **429 Too Many Requests**: 请求频率超限
 * - **500/502/503 Server Error**: 服务端内部错误
 *
 * ## 处理建议
 * - 检查 [statusCode] 确定 HTTP 错误类型
 * - 检查 [errorCode] 获取业务错误码（如有）
 * - 4xx 错误通常是客户端问题，需修正请求参数
 * - 5xx 错误可考虑重试（配合重试机制）
 *
 * @param statusCode HTTP 状态码（如 404, 500）
 * @param message 服务器返回的错误消息
 * @param errorCode 业务错误代码（可选，由服务端定义）
 * @param cause 底层异常原因（如 JSON 解析失败），可能为 null
 * @see FursuitTvSdkException
 */
@JsExport
@JsName("ApiException")
public class ApiException(
    public val statusCode: Int,
    message: String,
    public val errorCode: String? = null,
    cause: Throwable? = null,
) : FursuitTvSdkException(message, cause)

/**
 * 网络连接异常。
 *
 * 表示在 HTTP 请求过程中发生的网络层错误，
 * 如无法连接服务器、DNS 解析失败、SSL 握手问题等。
 *
 * ## 触发场景
 * - **连接超时**: 无法在指定时间内建立 TCP 连接
 * - **DNS 解析失败**: 域名无法解析为 IP 地址
 * - **连接被拒绝**: 目标服务器端口未开放或防火墙拦截
 * - **网络不可用**: 设备断网、飞行模式等
 * - **SSL/TLS 握手失败**: 证书验证不通过或协议版本不匹配
 * - **Socket 重置**: 连接被中间设备或服务器强制关闭
 *
 * ## 处理建议
 * - 检查设备网络连接状态
 * - 验证 DNS 配置是否能解析域名
 * - 检查防火墙/代理设置是否阻止了请求
 * - 对于临时性错误，可启用 SDK 内置重试机制（`enableRetry = true`）
 * - 对于持续性错误，提示用户检查网络环境
 *
 * @param message 错误描述（通常包含底层网络库的详细信息）
 * @param cause 根本原因（如 java.net.UnknownHostException），可能为 null
 * @see FursuitTvSdkException
 */
@JsExport
@JsName("NetworkException")
public class NetworkException(
    message: String,
    cause: Throwable? = null,
) : FursuitTvSdkException(message, cause)

/**
 * 令牌过期异常。
 *
 * 表示访问令牌已过期或即将过期（剩余有效期 ≤ 300秒），
 * 需要刷新令牌或重新进行签名交换。SDK 内置自动刷新机制
 * 会在令牌即将过期时尝试刷新，如刷新失败则抛出此异常。
 *
 * ## 触发场景
 * - 平台签名（accessToken）超过有效期（通常 2 小时）
 * - OAuth access_token 过期且 refresh_token 也无效或已撤销
 * - 令牌被服务端主动撤销（安全原因、用户注销、密码修改）
 * - 自动刷新机制失败且回退到 exchangeToken() 也失败
 * - 多次刷新失败达到最大重试次数
 *
 * ## 处理建议
 * - **优先方案**: 依赖 SDK 自动刷新机制（[AuthManager.getValidAccessToken]）
 *   SDK 会在令牌即将过期（≤300秒）时自动触发刷新
 * - **手动处理**: 如自动刷新失败，捕获后调用 [AuthManager.exchangeToken] 重新获取
 * - **OAuth 场景**: 如 oauthAccessToken 过期，需重新走 OAuth 授权流程
 * - **频繁触发**: 检查网络连接稳定性、客户端时钟是否同步
 *
 * @param message 异常详情消息，默认 "Access token has expired"
 * @param cause 根本原因（如网络超时导致刷新失败、401 响应），可能为 null
 * @see AuthManager.refreshToken
 * @see AuthManager.exchangeToken
 * @see FursuitTvSdkException
 */
@JsExport
@JsName("TokenExpiredException")
public class TokenExpiredException(
    message: String = "Access token has expired",
    cause: Throwable? = null,
) : FursuitTvSdkException(message, cause)

/**
 * 认证异常。
 *
 * 表示认证或授权过程中发生的错误，通常与凭证有效性或权限有关。
 * 与 TokenExpiredException 不同，此异常表示凭证本身有问题而非仅是过期。
 *
 * ## 触发场景
 * - **401 Unauthorized**: clientId/clientSecret 组合无效或已被禁用
 * - **403 Forbidden**: 凭证有效但权限不足（如尝试访问受限 API）
 * - **凭证格式错误**: clientId 不符合 `vap_xxxxxxxx` 格式
 * - **缺少认证头**: 请求未携带必要的 Authorization 或 X-Api-Key 头
 * - **OAuth 前置条件未满足**: 未完成签名交换就调用 initOAuth()
 * - **apiKey 无效**: apiKey 格式错误或在 VDS 控制台已撤销
 *
 * ## 处理建议
 * - 检查 clientId 和 clientSecret 是否正确且未被撤销
 * - 确认已完成签名交换（exchangeToken）或设置了有效的 apiKey
 * - 验证凭证来源（VDS 开发者控制台 vs 环境变量）
 * - 如使用 OAuth，确认先完成了签名交换步骤
 * - 在开发环境中打印完整错误信息以便调试
 *
 * @param message 认证失败的详细描述
 * @param cause 底层原因（如 HTTP 401/403 响应体），可能为 null
 * @see AuthManager.exchangeToken
 * @see TokenExpiredException
 * @see FursuitTvSdkException
 */
@JsExport
@JsName("AuthenticationException")
public class AuthenticationException(
    message: String,
    cause: Throwable? = null,
) : FursuitTvSdkException(message, cause)

/**
 * 参数验证异常。
 *
 * 表示请求参数未通过服务端或客户端的验证规则。
 * 通常是由于调用 API 时传入了不符合要求的参数值。
 *
 * ## 触发场景
 * - **必填参数缺失**: 未提供必需的 query、userId 等参数
 * - **参数格式错误**: userId 不是有效的 ID 格式、日期格式不正确
 * - **参数超出范围**: lat/lng 超出有效范围（-90~90/-180~180）、limit < 1
 * - **枚举值无效**: type 参数不是允许的值（如 "user"/"fursuit" 以外的值）
 * - **字符串长度超限**: search query 超过最大长度限制
 * - **类型不匹配**: 期望数字但传入字符串等
 *
 * ## 处理建议
 * - 仔细检查 API 文档中该方法的参数要求
 * - 验证所有必填参数都已提供且非空
 * - 检查参数值是否符合约束（范围、格式、枚举值）
 * - 使用参数校验工具类在调用前预验证（如有提供）
 * - 查看异常消息中的具体字段名和错误描述
 *
 * @param message 验证失败的详细说明（通常包含哪个参数出错及原因）
 * @param cause 底层原因（较少见，可能为 JSON 序列化错误），可能为 null
 * @see FursuitTvSdkException
 */
@JsExport
@JsName("ValidationException")
public class ValidationException(
    message: String,
    cause: Throwable? = null,
) : FursuitTvSdkException(message, cause)

/**
 * 未找到资源异常。
 *
 * 表示请求的资源在服务器上不存在。可能是由于资源 ID 错误、
 * 资源已被删除、或端点路径拼写错误。
 *
 * ## 触发场景
 * - **HTTP 404 Not Found**: 用户不存在（username/userId 错误）、聚会已删除
 * - **学校/角色不存在**: schoolId 或 characterId 无效
 * - **API 端点路径错误**: URL 拼写错误或版本号不匹配
 * - **资源已删除**: 该用户/聚会/学校曾经存在但已被删除
 * - **ID 格式错误**: 传入的 ID 字符串不是有效的 UUID 或数字格式
 *
 * ## 处理建议
 * - 确认资源 ID 是否正确（从可靠的来源获取，而非硬编码）
 * - 检查资源是否已被其他操作删除
 * - 验证 API 路径拼写是否正确（参考官方文档）
 * - 对于用户输入的查询，展示"未找到结果"而非错误页面
 * - 考虑实现缓存或预校验来避免无效查询
 *
 * @param message 资源未找到的详细说明（通常包含资源类型和 ID）
 * @param cause 底层原因（较少见），可能为 null
 * @see FursuitTvSdkException
 */
@JsExport
@JsName("NotFoundException")
public class NotFoundException(
    message: String,
    cause: Throwable? = null,
) : FursuitTvSdkException(message, cause)
