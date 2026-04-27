package com.furrist.rp.furtv.sdk.auth

/**
 * Native 实现的 OAuth 回调处理器
 * 注意：Native 平台暂不支持自动 OAuth 回调，需手动处理
 */
public class NativeOAuthCallbackHandler(
    @Suppress("UNUSED_PARAMETER")
    private val config: OAuthCallbackServerConfig,
) : OAuthCallbackHandler {
    override val callbackUrl: String = buildNativeCallbackUrl(config)

    @Suppress("ReturnCount")
    override suspend fun startAndGetCallback(authorizeUrl: String): OAuthCallbackResult {
        return OAuthCallbackResult.Error(
            "Native automatic OAuth callback is not yet implemented. Please handle callback manually.",
        )
    }

    override suspend fun stop() {
        // No-op on Native platform
    }
}

private const val NATIVE_HTTP_PORT: Int = 80
private const val NATIVE_HTTPS_PORT: Int = 443

private fun buildNativeCallbackUrl(config: OAuthCallbackServerConfig): String {
    val portSuffix =
        if (config.callbackPort != NATIVE_HTTP_PORT && config.callbackPort != NATIVE_HTTPS_PORT) {
            ":${config.callbackPort}"
        } else {
            ""
        }
    return "https://${config.callbackHost}$portSuffix${config.callbackPath}"
}
