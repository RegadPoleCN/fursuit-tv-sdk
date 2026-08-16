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
 * Verifies HttpClientConfig.getClient is thread-safe under concurrent access.
 * Same config (equals) must return the same HttpClient (single instance per cache miss).
 */
class HttpClientConfigConcurrencyTest {
    @Test
    fun concurrentFirstTimeAccessReturnsSameInstance() = runBlocking {
        val sameConfig = SdkConfig()
        // Per init-builder-refactor: cache key is Pair<SdkConfig, AuthHolder>;
        // shared holder => shared cache entry => same HttpClient instance.
        val sharedHolder = com.furrist.rp.furtv.sdk.auth.AuthHolder()
        val n = 100
        val results = mutableListOf<Any>()
        val errors = mutableListOf<Throwable>()
        coroutineScope {
            (1..n).map { i ->
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
