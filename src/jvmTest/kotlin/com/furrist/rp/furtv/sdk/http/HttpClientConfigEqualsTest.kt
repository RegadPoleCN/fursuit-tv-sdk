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
