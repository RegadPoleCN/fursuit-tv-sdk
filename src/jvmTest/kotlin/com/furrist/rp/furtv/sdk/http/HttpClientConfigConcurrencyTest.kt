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
import kotlin.test.assertSame
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.runBlocking

/**
 * 验证并发访问下 `HttpClientConfig.getClient` 的行为。
 *
 * 已知限制：缓存使用普通 `MutableMap.getOrPut`，JVM 多线程并发首次访问可能重复构建
 * HttpClient（行为正确，旧实例被覆盖，仅有轻微内存浪费）。同配置必须返回同一个实例。
 */
class HttpClientConfigConcurrencyTest {
    @Test
    fun concurrentFirstTimeAccessReturnsSameInstance() =
        runBlocking {
            val sameConfig = SdkConfig()
            // 缓存键为 Pair<SdkConfig, AuthHolder>；共享 holder 即共享缓存条目，返回同一实例。
            val sharedHolder = com.furrist.rp.furtv.sdk.auth.AuthHolder()
            val n = 100
            val results = mutableListOf<Any>()
            val errors = mutableListOf<Throwable>()
            coroutineScope {
                (1..n).map {
                    async {
                        try {
                            val c = HttpClientConfig.getClient(sameConfig, sharedHolder)
                            synchronized(results) { results.add(c) }
                        } catch (e: Throwable) {
                            synchronized(errors) { errors.add(e) }
                        }
                    }
                }.awaitAll()
            }
            assertEquals(0, errors.size, "no concurrent access should throw: ${errors.map { it.message }}")
            assertEquals(n, results.size, "all $n concurrent calls should return a client")
            assertSame(results.first(), results.last(), "all concurrent callers (same holder) get the same instance")
        }
}
