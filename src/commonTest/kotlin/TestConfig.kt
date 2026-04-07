/**
 * 测试配置对象
 * 统一管理所有测试使用的配置参数
 */
object TestConfig {
    /**
     * 测试用 API 密钥
     * 所有测试共享同一个 API key
     */
    const val API_KEY = "test-api-key"
    
    /**
     * 测试用基础 URL
     */
    const val BASE_URL = "https://open-global.vdsentnet.com"
    
    /**
     * 默认请求超时时间 (毫秒)
     */
    const val DEFAULT_TIMEOUT = 30000L
}
