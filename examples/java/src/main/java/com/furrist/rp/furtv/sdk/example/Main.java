package com.furrist.rp.furtv.sdk.example;

import com.furrist.rp.furtv.sdk.FursuitTvSdk;
import com.furrist.rp.furtv.sdk.factory.JvmFursuitTvSdkBuilder;
import com.furrist.rp.furtv.sdk.model.SdkLogLevel;
import com.furrist.rp.furtv.sdk.exception.NotFoundException;
import com.furrist.rp.furtv.sdk.exception.ApiException;
import com.furrist.rp.furtv.sdk.exception.FursuitTvSdkException;

public class Main {

    public static void main(String[] args) {
        exampleApiKeyMode();
        exampleTokenExchangeMode();
    }

    private static void exampleApiKeyMode() {
        FursuitTvSdk sdk = JvmFursuitTvSdkBuilder.create()
                .apiKey("your-api-key")
                .logLevel(SdkLogLevel.INFO)
                .buildBlocking();

        try {
            var profile = sdk.user.getUserProfileBlocking("username");
            System.out.println("Username: " + profile.getUsername());
            System.out.println("Nickname: " + profile.getNickname());

            var popular = sdk.search.getPopularBlocking();
            System.out.println("Popular users count: " + popular.getUsers().size());

            var health = sdk.base.healthBlocking();
            System.out.println("Health: " + health.getMessage());
        } catch (NotFoundException e) {
            System.err.println("Not found: " + e.getMessage());
        } catch (ApiException e) {
            System.err.println("API error (HTTP " + e.getStatusCode() + "): " + e.getMessage());
        } catch (FursuitTvSdkException e) {
            System.err.println("SDK error: " + e.getMessage());
        } finally {
            sdk.close();
        }
    }

    private static void exampleTokenExchangeMode() {
        FursuitTvSdk sdk = JvmFursuitTvSdkBuilder.create()
                .clientId("vap_xxxxxxxxxxxxxxxx")
                .clientSecret("your-client-secret-here")
                .logLevel(SdkLogLevel.INFO)
                .buildBlocking();

        try {
            var profile = sdk.user.getUserProfileBlocking("username");
            System.out.println("Username: " + profile.getUsername());
            System.out.println("Nickname: " + profile.getNickname());

            var popular = sdk.search.getPopularBlocking();
            System.out.println("Popular users count: " + popular.getUsers().size());

            var health = sdk.base.healthBlocking();
            System.out.println("Health: " + health.getMessage());
        } catch (NotFoundException e) {
            System.err.println("Not found: " + e.getMessage());
        } catch (ApiException e) {
            System.err.println("API error (HTTP " + e.getStatusCode() + "): " + e.getMessage());
        } catch (FursuitTvSdkException e) {
            System.err.println("SDK error: " + e.getMessage());
        } finally {
            sdk.close();
        }
    }
}
