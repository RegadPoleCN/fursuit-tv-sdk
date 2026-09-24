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
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

/**
 * vds-docs 服务端在多个端点用 `0`/`1` 整数或 `true`/`false` 布尔表示布尔值。
 * 解码时两者都接受：`0`=false，`1`=true，`true`=true，`false`=false。
 * 序列化时统一输出整数（`true`=1，`false`=0）。
 */
public object BooleanAsIntSerializer : KSerializer<Boolean> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("BooleanAsInt", PrimitiveKind.BOOLEAN)

    override fun serialize(encoder: Encoder, value: Boolean) {
        encoder.encodeInt(if (value) 1 else 0)
    }

    override fun deserialize(decoder: Decoder): Boolean {
        val raw = decoder.decodeString()
        return when (raw.lowercase()) {
            "true", "1" -> true
            "false", "0" -> false
            else -> raw.toBooleanStrictOrNull() ?: (raw.toIntOrNull()?.let { it != 0 } ?: false)
        }
    }
}

/**
 * 通用 API 响应包装（顶层 success/data/requestId）。
 * @param T 响应数据类型
 */
@JsExport
@JsName("ApiResponse")
@Serializable
public data class ApiResponse<T>(
    public val success: Boolean,
    public val data: T,
    public val requestId: String,
)

/**
 * [DEAD CODE / DEPRECATED in v0.5.0]
 * 分页请求参数。该类在 SDK 全局未被任何 API 端点使用，保留仅作向后兼容，计划于 v0.6.0 移除。
 */
@Deprecated("Unused across SDK, scheduled for removal in v0.6.0", level = DeprecationLevel.WARNING)
@JsExport
@JsName("PaginationParams")
@Serializable
public data class PaginationParams(
    public val cursor: String? = null,
    public val limit: Int? = null,
)

/** 分页响应数据。 */
@JsExport
@JsName("PaginatedResponse")
@Serializable
public data class PaginatedResponse<T>(
    public val items: List<T>,
    public val nextCursor: String? = null,
    public val hasMore: Boolean = false,
)

/** 地理位置坐标。 */
@JsExport
@JsName("GeoLocation")
@Serializable
public data class GeoLocation(
    public val lat: Double,
    public val lng: Double,
)

/** 图片资源。 */
@JsExport
@JsName("ImageResource")
@Serializable
public data class ImageResource(
    public val url: String,
    public val width: Int? = null,
    public val height: Int? = null,
)

/** 时间范围（ISO 8601 格式）。 */
@JsExport
@JsName("TimeRange")
@Serializable
public data class TimeRange(
    public val start: String,
    public val end: String,
)
