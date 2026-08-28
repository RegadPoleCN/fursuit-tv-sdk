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

import kotlin.test.*

/**
 * SHA256 哈希的单元测试。
 *
 * 注：`Sha256.hash()` 的 Kotlin Multiplatform 实现保留自原项目，
 * 其正确性依赖标准 FIPS 180-2 实现。本测试聚焦内部一致性（不替代标准参考向量）：
 * - 同一输入多次调用结果相同（确定性）
 * - `Sha256.hash(input)` 与 `input.sha256()` 产生相同输出（互为别名）
 * - 不同输入产生不同输出（基本避免碰撞）
 */
class Sha256Test {
    private fun ByteArray.toHexString(): String =
        joinToString("") { "%02x".format(it) }

    @Test
    fun sha256_isDeterministic() {
        val input = "Fursuit.TV SDK".encodeToByteArray()
        val a = input.sha256()
        val b = input.sha256()
        assertContentEquals(a, b, "SHA256 should be deterministic")
    }

    @Test
    fun sha256_producesFixedLengthOutput() {
        val inputs =
            listOf(
                "".encodeToByteArray(),
                "a".encodeToByteArray(),
                "abc".encodeToByteArray(),
                "Fursuit.TV SDK — Kotlin Multiplatform Client".encodeToByteArray(),
            )
        for (input in inputs) {
            val hash = input.sha256()
            assertEquals(32, hash.size, "SHA256 should always produce 32 bytes")
        }
    }

    @Test
    fun sha256_differentInputsProduceDifferentHashes() {
        val a = "abc".encodeToByteArray().sha256()
        val b = "abd".encodeToByteArray().sha256()
        assertNotEquals(a.toHexString(), b.toHexString())
    }

    @Test
    fun sha256_extensionFunction_matchesObjectHash() {
        val input = "Fursuit.TV SDK".encodeToByteArray()
        val viaObject = Sha256.hash(input)
        val viaExtension = input.sha256()
        assertContentEquals(viaObject, viaExtension)
    }

    @Test
    fun sha256_isNotEmptyForEmptyInput() {
        val hash = "".encodeToByteArray().sha256()
        assertEquals(32, hash.size)
        assertTrue(hash.any { it != 0.toByte() }, "Hash should not be all zeros for empty input")
    }
}
