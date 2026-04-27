package com.furrist.rp.furtv.sdk.auth

public actual fun createDefaultOAuthHandler(
    config: OAuthCallbackServerConfig,
): OAuthCallbackHandler = JsOAuthCallbackHandler(config)
