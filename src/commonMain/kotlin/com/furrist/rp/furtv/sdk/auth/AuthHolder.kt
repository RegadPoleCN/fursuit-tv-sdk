package com.furrist.rp.furtv.sdk.auth

import kotlin.concurrent.Volatile

/**
 * 跨线程持有 [AuthManager] 引用，供 `HttpClientConfig.defaultRequest` 在每个请求时
 * 通过闭包读取最新 `apiKey`（替代旧的 `config.apiKey` 静态注入）。
 *
 * 设计动机：
 * - `HttpClient` 在 `FursuitTvSdk` 构造时创建（早于 `AuthManager`）
 * - `defaultRequest` 块按 Ktor 默认行为**每个请求都执行**
 * - 通过 `@Volatile var auth` 保证跨线程可见性，主线程设值后，其他线程的请求
 *   能立即看到最新的 `AuthManager` 实例，从而拿到当前 token 的 `apiKey`
 */
public class AuthHolder {
    @Volatile
    public var auth: AuthManager? = null
}