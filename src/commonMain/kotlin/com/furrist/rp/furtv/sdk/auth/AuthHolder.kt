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
