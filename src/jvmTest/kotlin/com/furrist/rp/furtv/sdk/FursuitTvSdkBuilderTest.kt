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
