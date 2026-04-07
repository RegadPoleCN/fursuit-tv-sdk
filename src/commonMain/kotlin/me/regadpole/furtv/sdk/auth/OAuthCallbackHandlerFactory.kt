package me.regadpole.furtv.sdk.auth

/**
 * 创建 OAuth 回调处理器
 */
public expect fun createOAuthCallbackHandler(config: OAuthCallbackServerConfig): OAuthCallbackHandler
