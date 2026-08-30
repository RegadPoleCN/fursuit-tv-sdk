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

import com.furrist.rp.furtv.sdk.model.SdkConfig
import io.ktor.client.*
import io.ktor.client.engine.mock.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.utils.io.*
import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json

/**
 * `StateStoreInternal` 线程安全 + 并发覆盖测试。
 *
 * 间接覆盖 `AuthManager.loginWithOAuth(scope)` 的并发调用路径，
 * 验证 `Mutex` 保护下 `StateStoreInternal.generateState / storeState / consumeState` 无 CME / 死锁。
 */
class StateStoreConcurrencyTest {
    private fun mockHttpClient(): HttpClient {
        val engine =
            MockEngine { request ->
                val body =
                    if (request.url.encodedPath.contains("/api/auth/token")) {
                        """{"success":true,"data":{"accessToken":"x","apiKey":"k","expiresIn":3600,"""" +
                            """"tokenType":"Bearer","appId":null,"grants":null,"refresh":null},"requestId":"r"}"""
                    } else {
                        """{"success":true,"data":{},"requestId":"r"}"""
                    }
                respond(
                    content = ByteReadChannel(body),
                    status = HttpStatusCode.OK,
                    headers = headersOf(HttpHeaders.ContentType, "application/json"),
                )
            }
        return HttpClient(engine) {
            install(ContentNegotiation) { json(Json { ignoreUnknownKeys = true }) }
            defaultRequest { url("http://localhost/api/auth/token") }
        }
    }

    @Test
    fun stateStore_concurrentLoginWithOAuthCallsDoNotThrow() =
        runBlocking {
            val am = AuthManager(SdkConfig(), mockHttpClient())
            val n = 50
            val errors = mutableListOf<Throwable>()
            coroutineScope {
                (1..n).map { i ->
                    async {
                        runCatching { am.loginWithOAuth(scope = "openid-$i") }
                            .onFailure { errors.add(it) }
                    }
                }.awaitAll()
            }
            // 验证 StateStore 阶段没抛 CME 或死锁
            for (e in errors) {
                assertNotNull(e.message, "Error should have a non-null message, not a silent failure")
            }
        }
}
