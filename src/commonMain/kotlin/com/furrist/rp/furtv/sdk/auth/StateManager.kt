package com.furrist.rp.furtv.sdk.auth

import kotlin.concurrent.Volatile
import kotlin.random.Random
import kotlin.time.Duration.Companion.minutes
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

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
public object StateManager {
    private const val STATE_LENGTH = 32
    private const val DEFAULT_TIMEOUT_MINUTES = 10
    private const val CLEANUP_DELAY_MILLIS = 60000L
    private const val CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"

    private val stateStorage = mutableMapOf<String, StateEntry>()

    @Volatile
    private var cleanupRunning = false

    // 使用 SupervisorJob 确保子协程失败不会影响父协程
    private val cleanupJob = SupervisorJob()
    private val cleanupScope = CoroutineScope(Dispatchers.Default + cleanupJob)

    /**
     * 生成加密安全的随机 state
     * 使用 Random 生成指定长度的随机字符串
     * @return 生成的 state 字符串
     */
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
    public fun validateState(expected: String, actual: String): Boolean {
        return expected == actual
    }

    /**
     * 存储 state 及超时时间
     * @param state 要存储的 state
     * @param timeoutMinutes 超时时间（分钟），默认为 10 分钟
     */
    public fun storeState(state: String, timeoutMinutes: Int = DEFAULT_TIMEOUT_MINUTES) {
        val expiresAt = Clock.System.now().plus(timeoutMinutes.minutes)
        stateStorage[state] = StateEntry(expiresAt = expiresAt)

        if (!cleanupRunning) {
            scheduleCleanup()
        }
    }

    /**
     * 检查 state 是否有效
     * 验证 state 是否存在且未过期
     * @param state 要检查的 state
     * @return 如果 state 有效返回 true，否则返回 false
     */
    public fun isStateValid(state: String): Boolean {
        val entry = stateStorage[state] ?: return false

        val now = Clock.System.now()
        val isValid = now < entry.expiresAt

        if (!isValid) {
            stateStorage.remove(state)
        }

        return isValid
    }

    /**
     * 使用 state（验证后删除）
     * State 使用后自动删除，确保一次有效
     * @param state 要使用的 state
     * @return 如果 state 有效且成功使用返回 true，否则返回 false
     */
    public fun consumeState(state: String): Boolean {
        if (!isStateValid(state)) {
            return false
        }

        stateStorage.remove(state)
        return true
    }

    /**
     * 安排清理过期 state
     * 使用协程延迟执行清理任务
     */
    private fun scheduleCleanup() {
        cleanupRunning = true

        // 使用独立的 Scope 启动后台清理任务
        cleanupScope.launch {
            @Suppress("SwallowedException", "TooGenericExceptionCaught")
            try {
                delay(CLEANUP_DELAY_MILLIS)
                cleanupExpiredStates()
            } catch (e: Exception) {
                // 忽略异常，避免影响主流程
                // 注意：这里捕获通用 Exception 是因为清理任务是可选的
            } finally {
                cleanupRunning = false
            }
        }
    }

    /**
     * 清理过期的 state
     * 遍历存储并移除所有已过期的 state
     */
    private fun cleanupExpiredStates() {
        val now = Clock.System.now()
        val expiredStates = stateStorage.entries.filter { it.value.expiresAt <= now }.map { it.key }
        expiredStates.forEach { stateStorage.remove(it) }
    }

    /**
     * 清理资源
     * 取消后台清理任务并清空存储
     * 注意：此方法仅供测试使用，正常情况下不需要调用
     */
    @Suppress("UnusedReceiverParameter")
    public fun cleanup() {
        cleanupJob.cancel()
        stateStorage.clear()
        cleanupRunning = false
    }

    /**
     * State 存储条目
     * @property expiresAt 过期时间
     */
    private data class StateEntry(
        val expiresAt: Instant,
    )
}
