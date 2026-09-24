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
import kotlin.test.assertNotNull
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.runBlocking

/**
 * 验证无状态工厂 `HttpClientConfig.createClient` 在高并发下的线程安全性。
 */
class HttpClientConfigConcurrencyTest {
    @Test
    fun concurrentClientCreationSucceeds() =
        runBlocking {
            val sameConfig = SdkConfig()
            val n = 50
            val results = mutableListOf<Any>()
            val errors = mutableListOf<Throwable>()
            coroutineScope {
                (1..n).map {
                    async {
                        try {
                            val c = HttpClientConfig.createClient(sameConfig) { "mock-key" }
                            synchronized(results) { results.add(c) }
                        } catch (e: Throwable) {
                            synchronized(errors) { errors.add(e) }
                        }
                    }
                }.awaitAll()
            }
            assertEquals(0, errors.size, "no concurrent creation should throw: ${errors.map { it.message }}")
            assertEquals(n, results.size, "all $n concurrent calls should return a client")
            for (c in results) {
                assertNotNull(c)
                (c as io.ktor.client.HttpClient).close()
            }
        }
}
