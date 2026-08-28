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

package com.furrist.rp.furtv.sdk.http

import com.furrist.rp.furtv.sdk.auth.AuthHolder
import com.furrist.rp.furtv.sdk.model.SdkConfig
import kotlin.test.Test
import kotlin.test.assertNotSame
import kotlin.test.assertSame

/**
 * Verifies cache behavior: cache is keyed by Pair<SdkConfig, AuthHolder>.
 * Note: SdkConfig is a regular class (not data class), so equality is reference-based.
 * Different SdkConfig instances are never equal, even with identical field values.
 */
class HttpClientConfigEqualsTest {
    @Test
    fun sameConfigSameHolderReturnsSameInstance() {
        val config = SdkConfig(clientId = "test", clientSecret = "secret")
        val holder = AuthHolder()
        val clientA = HttpClientConfig.getClient(config, holder)
        val clientB = HttpClientConfig.getClient(config, holder)
        assertSame(clientA, clientB, "same config + same holder should return the same HttpClient")
    }

    @Test
    fun differentConfigsReturnDistinctInstances() {
        val a = SdkConfig(clientId = "client-a", clientSecret = "secret")
        val b = SdkConfig(clientId = "client-b", clientSecret = "secret")
        val holder = AuthHolder()
        val clientA = HttpClientConfig.getClient(a, holder)
        val clientB = HttpClientConfig.getClient(b, holder)
        assertNotSame(clientA, clientB, "different clientId should map to different HttpClient")
    }
}
