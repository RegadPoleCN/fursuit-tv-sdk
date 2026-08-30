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

import com.furrist.rp.furtv.sdk.model.SdkConfig
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotSame
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.runBlocking

/**
 * Verifies HttpClientConfig.getClient returns distinct HttpClient instances
 * for distinct SdkConfig (different config => different cache key => different instance).
 */
class HttpClientConfigDifferentConfigsTest {
    @Test
    fun differentConfigsReturnDistinctInstances() =
        runBlocking {
            val n = 50
            val results = mutableListOf<Any>()
            val errors = mutableListOf<Throwable>()
            coroutineScope {
                (1..n).map { i ->
                    async {
                        try {
                            val c =
                                HttpClientConfig.getClient(
                                    SdkConfig(clientId = "client-$i"),
                                    com.furrist.rp.furtv.sdk.auth.AuthHolder(),
                                )
                            synchronized(results) { results.add(c) }
                        } catch (e: Throwable) {
                            synchronized(errors) { errors.add(e) }
                        }
                    }
                }.awaitAll()
            }
            assertEquals(0, errors.size)
            assertEquals(n, results.size)
            // All should be unique instances (different cache keys)
            val unique = results.toSet()
            assertEquals(n, unique.size, "expected $n distinct instances, got ${unique.size}")
            // Spot-check: first and last are not the same
            assertNotSame(results.first(), results.last())
        }
}
