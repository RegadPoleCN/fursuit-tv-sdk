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

package com.furrist.rp.furtv.sdk

import kotlin.test.Test
import kotlin.test.assertFailsWith
import kotlinx.coroutines.runBlocking

/**
 * FursuitTvSdkBuilder 校验：apiKey-only init 已被禁用，必须提供 clientId + clientSecret。
 * per `init-builder-refactor` D2.4 约束。
 */
class FursuitTvSdkBuilderTest {
    @Test
    fun `apiKey only without clientId or clientSecret is rejected`() {
        runBlocking<Unit> {
            val ex =
                assertFailsWith<IllegalArgumentException> {
                    FursuitTvSdkBuilder()
                        .apiKey("legacy-api-key")
                        .build()
                }
            assert(ex.message!!.contains("clientId"))
            assert(ex.message!!.contains("clientSecret"))
            assert(ex.message!!.contains("apiKey-only init is forbidden"))
        }
    }

    @Test
    fun `clientId only without clientSecret is rejected`() {
        runBlocking<Unit> {
            assertFailsWith<IllegalArgumentException> {
                FursuitTvSdkBuilder()
                    .clientId("vap_xxx")
                    .build()
            }
        }
    }

    @Test
    fun `clientSecret only without clientId is rejected`() {
        runBlocking<Unit> {
            assertFailsWith<IllegalArgumentException> {
                FursuitTvSdkBuilder()
                    .clientSecret("your-secret")
                    .build()
            }
        }
    }

    @Test
    fun `apiKey and clientId but no clientSecret is rejected`() {
        runBlocking<Unit> {
            assertFailsWith<IllegalArgumentException> {
                FursuitTvSdkBuilder()
                    .apiKey("legacy-api-key")
                    .clientId("vap_xxx")
                    .build()
            }
        }
    }
}
