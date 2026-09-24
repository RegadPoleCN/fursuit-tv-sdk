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
import kotlin.js.JsExport
import kotlin.js.JsName

/**
 * [OBSOLETE / DEPRECATED in v0.5.0]
 * 旧版用于在全局单例 HttpClient 与 AuthManager 之间传递晚绑定引用的凭证持有器。
 *
 * 在 v0.5.0 中，HttpClient 生命周期已收敛由 [com.furrist.rp.furtv.sdk.FursuitTvSdk] 实例直接管理，
 * 不再使用全局缓存与晚绑定，此类已废弃并计划在 v0.6.0 完全移除。
 */
@Deprecated(
    message = "AuthHolder is obsolete and no longer used internally. It will be removed in v0.6.0.",
    level = DeprecationLevel.WARNING,
)
@JsExport
@JsName("AuthHolder")
public class AuthHolder {
    @Volatile
    public var auth: AuthManager? = null
}
