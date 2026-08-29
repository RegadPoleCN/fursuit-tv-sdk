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
import io.ktor.client.HttpClient
import kotlin.test.Test
import kotlin.test.assertTrue

/**
 * 审计项 #1：授权 URL 的 query 参数必须 URL 编码（vds-docs《VDS账户快速接入（OAuth）》:54、
 * 《授权端点》:36 要求 redirect_uri 等参数编码）。
 */
class OAuthAuthorizeUrlEncodingTest {

    @Test
    fun `authorize url encodes query parameter values`() {
        val config = SdkConfig(clientId = "vap_test", clientSecret = "test-secret")
        val auth = AuthManager(config, HttpClient())

        val url = auth.getOAuthAuthorizeUrl(
            redirectUri = "https://example.com/cb?x=1",
            scope = "profile",
            state = "a&b=c",
            enablePkce = false,
        )

        assertTrue(url.startsWith("https://open-global.vdsentnet.com/api/proxy/account/sso/authorize?"))
        assertTrue(url.contains("client_id=vap_test"))
        assertTrue(url.contains("redirect_uri=https%3A%2F%2Fexample.com%2Fcb%3Fx%3D1"))
        assertTrue(url.contains("scope=profile"))
        assertTrue(url.contains("state=a%26b%3Dc"))
    }
}
