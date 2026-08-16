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
