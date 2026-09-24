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

import com.furrist.rp.furtv.sdk.FursuitTvSdk
import com.furrist.rp.furtv.sdk.model.SdkConfig
import kotlin.test.Test
import kotlin.test.assertNotSame

/**
 * 验证生命周期隔离性：不同的 FursuitTvSdk 实例拥有各自独立的 HttpClient 实例，避免全局静态共享带来的状态污染与内存泄漏。
 */
class HttpClientConfigEqualsTest {
    @Test
    fun differentSdkInstancesHaveDistinctHttpClients() {
        val configA = SdkConfig(clientId = "client-a", clientSecret = "secret-a")
        val configB = SdkConfig(clientId = "client-b", clientSecret = "secret-b")

        val sdkA = FursuitTvSdk(configA)
        val sdkB = FursuitTvSdk(configB)

        try {
            val clientAField = FursuitTvSdk::class.java.getDeclaredField("httpClient").apply { isAccessible = true }
            val clientA = clientAField.get(sdkA)
            val clientB = clientAField.get(sdkB)
            assertNotSame(clientA, clientB, "different SDK instances must own distinct HttpClient instances")
        } finally {
            sdkA.close()
            sdkB.close()
        }
    }
}
