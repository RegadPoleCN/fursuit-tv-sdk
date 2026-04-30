package com.furrist.rp.furtv.sdk.example;

import com.furrist.rp.furtv.sdk.FursuitTvSdk;
import com.furrist.rp.furtv.sdk.factory.JvmFursuitTvSdkBuilder;
import com.furrist.rp.furtv.sdk.model.SdkLogLevel;
import com.furrist.rp.furtv.sdk.exception.NotFoundException;
import com.furrist.rp.furtv.sdk.exception.ApiException;
import com.furrist.rp.furtv.sdk.exception.FursuitTvSdkException;
import kotlinx.coroutines.BuildersKt;
import kotlinx.coroutines.CoroutineScope;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.EmptyCoroutineContext;
import kotlin.Function2;

public class Main {

    @SuppressWarnings("unchecked")
    private static <T> T await(Function2<CoroutineScope, Continuation<? super T>, Object> block) {
        return (T) BuildersKt.runBlocking(EmptyCoroutineContext.INSTANCE, block);
    }

    public static void main(String[] args) {
        exampleApiKeyMode();
        exampleTokenExchangeMode();
    }

    private static void exampleApiKeyMode() {
        FursuitTvSdk sdk = JvmFursuitTvSdkBuilder.create()
                .apiKey("your-api-key")
                .logLevel(SdkLogLevel.INFO)
                .build();

        try {
            var profile = await((scope, cont) -> sdk.user.getUserProfile("username", cont));
            System.out.println("Username: " + profile.getUsername());
            System.out.println("Nickname: " + profile.getNickname());

            var popular = await((scope, cont) -> sdk.search.getPopular(null, cont));
            System.out.println("Popular users count: " + popular.getUsers().size());

            var health = await((scope, cont) -> sdk.base.health(cont));
            System.out.println("Health: " + health.getMessage());
        } catch (NotFoundException e) {
            System.err.println("Not found: " + e.getMessage());
        } catch (ApiException e) {
            System.err.println("API error (HTTP " + e.getStatusCode() + "): " + e.getMessage());
            if (e.getErrorCode() != null) {
                System.err.println("Error code: " + e.getErrorCode());
            }
        } catch (FursuitTvSdkException e) {
            System.err.println("SDK error: " + e.getMessage());
        } finally {
            sdk.close();
        }
    }

    private static void exampleTokenExchangeMode() {
        FursuitTvSdk sdk = await((scope, cont) ->
                JvmFursuitTvSdkBuilder.create()
                        .clientId("vap_xxx")
                        .clientSecret("your-secret")
                        .logLevel(SdkLogLevel.INFO)
                        .buildAsync(cont)
        );

        try {
            var profile = await((scope, cont) -> sdk.user.getUserProfile("username", cont));
            System.out.println("Username: " + profile.getUsername());
            System.out.println("Nickname: " + profile.getNickname());

            var popular = await((scope, cont) -> sdk.search.getPopular(null, cont));
            System.out.println("Popular users count: " + popular.getUsers().size());

            var health = await((scope, cont) -> sdk.base.health(cont));
            System.out.println("Health: " + health.getMessage());
        } catch (NotFoundException e) {
            System.err.println("Not found: " + e.getMessage());
        } catch (ApiException e) {
            System.err.println("API error (HTTP " + e.getStatusCode() + "): " + e.getMessage());
            if (e.getErrorCode() != null) {
                System.err.println("Error code: " + e.getErrorCode());
            }
        } catch (FursuitTvSdkException e) {
            System.err.println("SDK error: " + e.getMessage());
        } finally {
            sdk.close();
        }
    }
}
