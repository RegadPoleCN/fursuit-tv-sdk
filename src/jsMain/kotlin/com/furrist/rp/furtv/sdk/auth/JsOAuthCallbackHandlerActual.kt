package com.furrist.rp.furtv.sdk.auth

import kotlin.js.JsExport
import kotlin.js.JsName

@JsExport
@JsName("createDefaultOAuthHandler")
public actual fun createDefaultOAuthHandler(
    config: OAuthCallbackServerConfig,
): OAuthCallbackHandler = JsOAuthCallbackHandler(config)
