@file:Suppress("TooGenericExceptionCaught", "SwallowedException")

package me.regadpole.furtv.sdk.auth

/**
 * JS 平台：检测是否在浏览器中
 * 通过检查 window 和 document 对象判断运行环境
 */
public actual fun isBrowser(): Boolean {
    return try {
        js("typeof window !== 'undefined' && window !== null && typeof window.document !== 'undefined'")
    } catch (e: Throwable) {
        false
    }
}
