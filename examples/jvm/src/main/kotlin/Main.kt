package com.furrist.rp.furtv.sdk.example

import com.furrist.rp.furtv.sdk.FursuitTvSdk
import com.furrist.rp.furtv.sdk.model.SdkLogLevel
import com.furrist.rp.furtv.sdk.exception.FursuitTvSdkException

suspend fun main() {
    val sdk = FursuitTvSdk.create {
        apiKey = "your-api-key"
        logLevel = SdkLogLevel.INFO
    }

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
