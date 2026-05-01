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
- `"vap_xxxxxxxxxxxxxxxx"` — 替换为你的 clientId（即 VDS 文档中的 appId）
- `"your-client-secret-here"` — 替换为你的 clientSecret

### 3. 运行

```bash
./gradlew run
```

## 初始化方式

### 方式一：API Key 模式

使用 API Key 直接构建，`buildBlocking()` 为阻塞调用：

```java
FursuitTvSdk sdk = JvmFursuitTvSdkBuilder.create()
        .apiKey("your-api-key")
        .logLevel(SdkLogLevel.INFO)
        .buildBlocking();
```

### 方式二：签名交换模式

使用 clientId + clientSecret 自动完成签名交换，`buildBlocking()` 为阻塞调用：

```java
FursuitTvSdk sdk = JvmFursuitTvSdkBuilder.create()
        .clientId("vap_xxxxxxxxxxxxxxxx")
        .clientSecret("your-client-secret-here")
        .logLevel(SdkLogLevel.INFO)
        .buildBlocking();
```

## 调用 API

SDK 通过 suspend-transform 插件为所有 `suspend` 函数自动生成 `xxxBlocking()` 方法，Java 中可直接调用，无需手动处理 `Continuation`：

```java
var profile = sdk.user.getUserProfileBlocking("username");
var popular = sdk.search.getPopularBlocking();
var health  = sdk.base.healthBlocking();
```

> ⚠️ 注意：`getPopularBlocking()` 等带有默认参数的方法，`xxxBlocking()` 版本已自动填充默认值，无需显式传参。

## 错误处理

SDK 提供完整的异常层次结构，建议按从具体到通用的顺序捕获：

```java
try {
    var profile = sdk.user.getUserProfileBlocking("username");
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
- 通过 suspend-transform 插件，每个 `suspend` 函数自动生成对应的 `xxxBlocking()` 方法，Java 中可直接同步调用
- `buildBlocking()` 为阻塞方法，适用于 API Key 模式和签名交换模式
- 无需手动引入 `kotlinx.coroutines` 或编写 `await` 辅助方法
- 也可使用 `JvmFursuitTvSdkFactory.createDsl()` 通过 `Consumer<MutableSdkConfig>` 配置（仅 API Key 模式）
