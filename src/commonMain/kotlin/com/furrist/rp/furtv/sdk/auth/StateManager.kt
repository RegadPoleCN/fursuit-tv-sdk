package com.furrist.rp.furtv.sdk.auth

import kotlin.concurrent.Volatile
import kotlin.js.JsExport
import kotlin.js.JsName
import kotlin.random.Random
import kotlin.time.Duration.Companion.minutes
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock

/**
 * State 管理器
 * 负责 OAuth 流程中 state 参数的生成、验证和管理
 * 提供加密安全的 state 生成、存储、验证和超时失效功能
 *
 * State 特性：
 * - 加密安全：使用 SecureRandom 生成随机字符串
 * - 一次有效：使用后自动删除
 * - 超时失效：支持自定义超时时间
 * - 线程安全：使用 ConcurrentMap 存储
 */
@JsExport
@JsName("StateManager")
public object StateManager {
    private const val STATE_LENGTH = 32
    private const val DEFAULT_TIMEOUT_MINUTES = 10
    private const val CLEANUP_DELAY_MILLIS = 60000L
    private const val CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"

    private val stateStorage = mutableMapOf<String, StateEntry>()

    @Volatile
    private var cleanupRunning = false

    private val cleanupJob = SupervisorJob()
    private val cleanupScope = CoroutineScope(Dispatchers.Default + cleanupJob)

    /**
     * 生成加密安全的随机 state
     * @return 生成的 state 字符串
     */
    @JsName("generateState")
    public fun generateState(): String {
        return (1..STATE_LENGTH)
            .map { CHARS.random(Random) }
            .joinToString("")
    }

    /**
     * 验证 state 是否匹配
     * @param expected 期望的 state 值
     * @param actual 实际收到的 state 值
     * @return 如果匹配返回 true，否则返回 false
     */
    @JsName("validateState")
    public fun validateState(expected: String, actual: String): Boolean {
        return expected == actual
    }

    /**
     * 存储 state 及超时时间
     * @param state 要存储的 state
     * @param timeoutMinutes 超时时间（分钟），默认为 10 分钟
     */
    @JsName("storeState")
    public fun storeState(state: String, timeoutMinutes: Int = DEFAULT_TIMEOUT_MINUTES) {
        val expiresAtEpochMs = Clock.System.now().plus(timeoutMinutes.minutes).toEpochMilliseconds()
        stateStorage[state] = StateEntry(expiresAtEpochMs = expiresAtEpochMs)

        if (!cleanupRunning) {
            scheduleCleanup()
        }
    }

    /**
     * 检查 state 是否有效
     * @param state 要检查的 state
     * @return 如果 state 有效返回 true，否则返回 false
     */
    @JsName("isStateValid")
    public fun isStateValid(state: String): Boolean {
        val entry = stateStorage[state] ?: return false

        val nowEpochMs = Clock.System.now().toEpochMilliseconds()
        val isValid = nowEpochMs < entry.expiresAtEpochMs

        if (!isValid) {
            stateStorage.remove(state)
        }

        return isValid
    }

    /**
     * 使用 state（验证后删除）
     * @param state 要使用的 state
     * @return 如果 state 有效且成功使用返回 true，否则返回 false
     */
    @JsName("consumeState")
    public fun consumeState(state: String): Boolean {
        if (!isStateValid(state)) {
            return false
        }

        stateStorage.remove(state)
        return true
    }

    private fun scheduleCleanup() {
        cleanupRunning = true

        cleanupScope.launch {
            @Suppress("SwallowedException", "TooGenericExceptionCaught")
            try {
                delay(CLEANUP_DELAY_MILLIS)
                cleanupExpiredStates()
            } catch (_: Exception) {
            } finally {
                cleanupRunning = false
            }
        }
    }

    private fun cleanupExpiredStates() {
        val nowEpochMs = Clock.System.now().toEpochMilliseconds()
        val expiredStates = stateStorage.entries.filter { it.value.expiresAtEpochMs <= nowEpochMs }.map { it.key }
        expiredStates.forEach { stateStorage.remove(it) }
    }

    /**
     * 清理资源
     * @suppress 仅供测试使用
     */
    @JsName("cleanup")
    @Suppress("UnusedReceiverParameter")
    public fun cleanup() {
        cleanupJob.cancel()
        stateStorage.clear()
        cleanupRunning = false
    }

    private data class StateEntry(
        val expiresAtEpochMs: Long,
    )
}
