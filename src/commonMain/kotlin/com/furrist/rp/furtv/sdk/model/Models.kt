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

package com.furrist.rp.furtv.sdk.model

import kotlin.js.JsExport
import kotlin.js.JsName

/**
 * [REFACTORED in v0.5.0]
 * 原 1700 行单文件 Models.kt 已按领域子系统拆分为以下独立文件：
 * - AuthModels.kt        (认证与令牌模型)
 * - BaseModels.kt        (基础、版本、主题与 RequestLog 模型)
 * - UserModels.kt        (用户资料、关系、访客、徽章与商店商品模型)
 * - SearchModels.kt      (搜索、推荐、物种与地点模型)
 * - GatheringModels.kt   (聚会统计、月历、附近与详情模型)
 * - SchoolModels.kt      (学校与角色模型)
 * - CommonModels.kt      (通用响应、地理坐标与序列化器)
 *
 * 此对象保留用于记录重构架构说明，所有模型在当前包同级直接可见。
 */
@Deprecated(
    message = "Models.kt is refactored into domain-specific files. All models remain accessible in this package.",
    level = DeprecationLevel.WARNING,
)
@JsExport
@JsName("Models")
public object Models
