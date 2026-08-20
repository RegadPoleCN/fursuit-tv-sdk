package com.furrist.rp.furtv.sdk.contract

/** Shared test helpers for contract regression tests. */
internal object ContractFixture {
    fun readFixture(path: String): String =
        checkNotNull(javaClass.classLoader.getResourceAsStream(path)) {
            "Fixture not found: $path"
        }.bufferedReader().use { it.readText() }
}
