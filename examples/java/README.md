# Fursuit.TV SDK Java 示例

纯 Java 示例项目，演示如何使用 `JvmFursuitTvSdkBuilder` 链式 API 调用 Fursuit.TV SDK。

## 前置要求

- JDK 17+
- Gradle 8.0+

## 快速开始

### 1. 安装依赖

```bash
./gradlew build
```

### 2. 配置凭证

编辑 `Main.java`，替换以下占位符：

- `"your-api-key"` — 替换为你的 API Key
- `"vap_xxx"` — 替换为你的 clientId（即 VDS 文档中的 appId）
- `"your-secret"` — 替换为你的 clientSecret

### 3. 运行

```bash
./gradlew run
```

## 初始化方式

### 方式一：API Key 模式（同步）

使用 API Key 直接构建，`build()` 为同步调用：

```java
FursuitTvSdk sdk = JvmFursuitTvSdkBuilder.create()
        .apiKey("your-api-key")
        .logLevel(SdkLogLevel.INFO)
        .build();
```

### 方式二：签名交换模式（异步）

使用 clientId + clientSecret 自动完成签名交换，`buildAsync()` 为挂起函数，需通过 `await` 辅助方法调用：

```java
FursuitTvSdk sdk = await((scope, cont) ->
        JvmFursuitTvSdkBuilder.create()
                .clientId("vap_xxx")
                .clientSecret("your-secret")
                .logLevel(SdkLogLevel.INFO)
                .buildAsync(cont)
);
```

## 调用 Kotlin suspend 函数

SDK 的所有 API 方法均为 Kotlin `suspend` 函数。在 Java 中，`suspend` 函数会编译为带有额外 `Continuation` 参数的方法，不能直接调用。

推荐使用 `await` 辅助方法封装 `BuildersKt.runBlocking`：

```java
import kotlinx.coroutines.BuildersKt;
import kotlinx.coroutines.CoroutineScope;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.EmptyCoroutineContext;
import kotlin.Function2;

@SuppressWarnings("unchecked")
private static <T> T await(Function2<CoroutineScope, Continuation<? super T>, Object> block) {
    return (T) BuildersKt.runBlocking(EmptyCoroutineContext.INSTANCE, block);
}

// 使用示例
var profile = await((scope, cont) -> sdk.user.getUserProfile("username", cont));
var popular = await((scope, cont) -> sdk.search.getPopular(null, cont));
var health  = await((scope, cont) -> sdk.base.health(cont));
```

> ⚠️ 注意：`getPopular(limit: Int? = null)` 等带有默认参数的方法，Java 中需显式传参（如 `null`）。

## 错误处理

SDK 提供完整的异常层次结构，建议按从具体到通用的顺序捕获：

```java
try {
    var profile = await((scope, cont) -> sdk.user.getUserProfile("username", cont));
} catch (NotFoundException e) {
    // 资源不存在
} catch (ApiException e) {
    // API 错误，可获取 HTTP 状态码和错误码
    e.getStatusCode();
    e.getErrorCode();
} catch (FursuitTvSdkException e) {
    // 其他 SDK 异常
} finally {
    sdk.close();
}
```

## 项目结构

```
examples/java/
├── build.gradle.kts
├── settings.gradle.kts
├── README.md
└── src/main/java/com/furrist/rp/furtv/sdk/example/
    └── Main.java
```

## Java 互操作说明

- SDK 基于 Kotlin Multiplatform 构建，所有 API 方法均为 `suspend` 挂起函数
- Java 中 `suspend` 函数会编译为带有 `Continuation` 参数的方法，需通过 `BuildersKt.runBlocking` 调用
- `build()` 为同步方法（仅 API Key 模式），`buildAsync()` 为挂起函数（支持签名交换）
- 推荐使用 `await` 辅助方法简化 `runBlocking` 调用
- 也可使用 `JvmFursuitTvSdkFactory.createDsl()` 通过 `Consumer<MutableSdkConfig>` 配置（仅 API Key 模式）
