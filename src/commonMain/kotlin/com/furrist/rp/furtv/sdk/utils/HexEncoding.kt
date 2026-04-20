@file:JvmName("HexEncoding")

package com.furrist.rp.furtv.sdk.utils

/**
 * 将字节数组转换为小写十六进制字符串
 *
 * @return 十六进制编码的字符串
 */
public fun ByteArray.toHex(): String = joinToString("") { byte ->
    val hex = byte.toInt().and(0xFF).toString(16)
    if (hex.length == 1) "0$hex" else hex
}
