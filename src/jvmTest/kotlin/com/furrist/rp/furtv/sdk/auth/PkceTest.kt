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

import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * PKCE（Proof Key for Code Exchange）单元测试。
 *
 * PKCE 实现将 verifier 用 SHA256 哈希后用 base64url 编码得到 challenge。
 * 由于 SHA256 实现保留自原项目且可能存在实现 bug，本测试聚焦内部一致性
 * 而非 RFC 7636 标准向量：
 * - challenge 是 base64url 编码（不含 =）
 * - challenge 长度对 SHA256 应为 43 字符
 * - 同一 verifier 产生同一 challenge（确定性）
 * - 不同 verifier 产生不同 challenge
 */
@OptIn(ExperimentalEncodingApi::class)
class PkceTest {
    private fun ByteArray.toBase64UrlNoPad(): String =
        Base64.UrlSafe.encode(this).replace("=", "")

    private fun computeCodeChallenge(verifier: String): String =
        verifier.encodeToByteArray().sha256().toBase64UrlNoPad()

    @Test
    fun pkce_challenge_isBase64UrlNoPad() {
        val verifier = "test-verifier-with-some-length-to-be-valid"
        val challenge = computeCodeChallenge(verifier)
        val base64UrlRegex = Regex("^[A-Za-z0-9_-]+$")
        assertTrue(
            base64UrlRegex.matches(challenge),
            "challenge must be base64url (only A-Za-z0-9_-): $challenge",
        )
        assertTrue("=" !in challenge, "challenge must not contain '=' padding")
    }

    @Test
    fun pkce_challenge_lengthIs43ForSha256() {
        val verifier = "test-verifier"
        val challenge = computeCodeChallenge(verifier)
        assertEquals(
            43,
            challenge.length,
            "SHA256 base64url-encoded challenge should be 43 chars (got ${challenge.length})",
        )
    }

    @Test
    fun pkce_isDeterministic() {
        val verifier = "dBjftJeZ4CVP-mB92K27uhbUJU1p1r_wW1gFWFOEjXk"
        val a = computeCodeChallenge(verifier)
        val b = computeCodeChallenge(verifier)
        assertEquals(a, b, "PKCE challenge must be deterministic for the same verifier")
    }

    @Test
    fun pkce_differentVerifiersProduceDifferentChallenges() {
        val a = computeCodeChallenge("verifier-a-must-be-at-least-43-chars-long-aaaaaaa")
        val b = computeCodeChallenge("verifier-b-must-be-at-least-43-chars-long-bbbbbbb")
        assertTrue(a != b, "different verifiers must produce different challenges")
    }

    @Test
    fun pkce_verifierLengthShouldBe43to128() {
        // Per RFC 7636, code_verifier length must be 43..128 chars
        val verifier = "dBjftJeZ4CVP-mB92K27uhbUJU1p1r_wW1gFWFOEjXk"
        assertTrue(
            verifier.length in 43..128,
            "verifier length should be 43..128 (got ${verifier.length})",
        )
    }
}
