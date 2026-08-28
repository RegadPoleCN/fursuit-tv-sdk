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

@file:JvmName("HexEncoding")

package com.furrist.rp.furtv.sdk.utils

import kotlin.js.JsExport
import kotlin.js.JsName
import kotlin.jvm.JvmName

/**
 * 将字节数组转换为小写十六进制字符串
 *
 * @return 十六进制编码的字符串
 */
@JsExport
@JsName("toHex")
public fun ByteArray.toHex(): String =
    joinToString("") { byte ->
        val hex = byte.toInt().and(0xFF).toString(16)
        if (hex.length == 1) "0$hex" else hex
    }
