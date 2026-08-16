package com.furrist.rp.furtv.sdk.example

import com.furrist.rp.furtv.sdk.FursuitTvSdkBuilder
import com.furrist.rp.furtv.sdk.model.SdkLogLevel
import com.furrist.rp.furtv.sdk.exception.FursuitTvSdkException
import kotlinx.coroutines.runBlocking

fun main() = runBlocking {
    // v0.3.0+：必须 clientId + clientSecret（apiKey-only 已被禁用）
    val sdk = FursuitTvSdkBuilder()
        .clientId("vap_xxxxxxxxxxxxxxxx")
        .clientSecret("your-client-secret-here")
        .logLevel(SdkLogLevel.INFO)
        .build()  // suspend，在 runBlocking 里

    try {
        val health = sdk.base.health()
        println("Health: ${health.message}")

        val profile = sdk.user.getUserProfile("username")
        println("Username: ${profile.username}")
        println("Nickname: ${profile.nickname}")

        val popular = sdk.search.getPopular()
        println("Popular users: ${popular.users.size}")

        val userId = sdk.user.getUserId("username")
        println("User ID: ${userId.id}")

        val likeStatus = sdk.user.getLikeStatus("username")
        println("Liked: ${likeStatus.isLiked}")

        val nearbyMode = sdk.gathering.getNearbyMode()
        println("Nearby gatherings: ${nearbyMode.gatherings.size}")

        val yearStats = sdk.gathering.getYearStats()
        println("Gathering total: ${yearStats.total}")

        val speciesList = sdk.search.getSpeciesList()
        println("Species count: ${speciesList.species.size}")
    } catch (e: FursuitTvSdkException) {
        println("SDK error: ${e.message}")
    } finally {
        sdk.close()
    }
}
