# 聚会 API

聚会 API 包含与聚会相关的接口，如聚会列表、聚会详情、聚会报名等。

## 方法列表

### `getGatheringStatsThisYear()`

获取聚会年度统计。

**返回类型**: `GatheringStatsData`

**示例**:

```kotlin
val stats = sdk.gathering.getGatheringStatsThisYear()
println("年份: ${stats.year}")
println("总聚会数: ${stats.total}")
println("即将开始: ${stats.upcoming}")
println("进行中: ${stats.ongoing}")
println("已完成: ${stats.completed}")
```

### `getGatheringMonthly(params: GatheringMonthlyParams)`

获取聚会月历。

**参数**:
- `params`: 聚会月历参数
  - `year`: 年份
  - `month`: 月份
  - `lat`: 纬度（用于计算距离，可选）
  - `lng`: 经度（用于计算距离，可选）

**返回类型**: `List<GatheringMonthlyItem>`

**示例**:

```kotlin
val params = GatheringMonthlyParams(
    year = 2024,
    month = 12
)
val gatherings = sdk.gathering.getGatheringMonthly(params)

println("聚会数量: ${gatherings.size}")
gatherings.forEach { gathering ->
    println("聚会名称: ${gathering.name}")
    println("开始时间: ${gathering.startDate}")
    println("状态: ${gathering.status}")
}
```

### `getGatheringMonthly(year: Int, month: Int)`

获取聚会月历（重载方法）。

**参数**:
- `year`: 年份
- `month`: 月份

**返回类型**: `List<GatheringMonthlyItem>`

**示例**:

```kotlin
val gatherings = sdk.gathering.getGatheringMonthly(2024, 12)
println("聚会数量: ${gatherings.size}")
```

### `getGatheringMonthlyDistance(params: GatheringMonthlyParams)`

获取聚会月历（带距离）。

**参数**:
- `params`: 聚会月历参数
  - `year`: 年份
  - `month`: 月份
  - `lat`: 纬度（用于计算距离）
  - `lng`: 经度（用于计算距离）

**返回类型**: `List<GatheringWithDistance>`

**示例**:

```kotlin
val params = GatheringMonthlyParams(
    year = 2024,
    month = 12,
    lat = 39.9042,
    lng = 116.4074
)
val gatherings = sdk.gathering.getGatheringMonthlyDistance(params)

println("聚会数量: ${gatherings.size}")
gatherings.forEach { gathering ->
    println("聚会名称: ${gathering.name}")
    println("开始时间: ${gathering.startDate}")
    println("距离: ${gathering.distance} 米")
    println("状态: ${gathering.status}")
}
```

### `getGatheringMonthlyDistance(year: Int, month: Int, lat: Double? = null, lng: Double? = null)`

获取聚会月历（带距离，重载方法）。

**参数**:
- `year`: 年份
- `month`: 月份
- `lat`: 纬度（用于计算距离，可选）
- `lng`: 经度（用于计算距离，可选）

**返回类型**: `List<GatheringWithDistance>`

**示例**:

```kotlin
val gatherings = sdk.gathering.getGatheringMonthlyDistance(2024, 12, 39.9042, 116.4074)
println("聚会数量: ${gatherings.size}")
```

### `getGatheringNearby(params: GatheringNearbyParams)`

获取附近聚会。

**参数**:
- `params`: 附近聚会参数
  - `lat`: 纬度
  - `lng`: 经度
  - `radius`: 半径（米，可选）

**返回类型**: `List<GatheringNearby>`

**示例**:

```kotlin
val params = GatheringNearbyParams(
    lat = 39.9042,
    lng = 116.4074,
    radius = 50000 // 50公里
)
val nearbyGatherings = sdk.gathering.getGatheringNearby(params)

println("附近聚会数量: ${nearbyGatherings.size}")
nearbyGatherings.forEach { gathering ->
    println("聚会名称: ${gathering.name}")
    println("位置: ${gathering.location}")
    println("距离: ${gathering.distance} 米")
    println("开始时间: ${gathering.startDate}")
}
```

### `getGatheringNearby(lat: Double, lng: Double, radius: Int? = null)`

获取附近聚会（重载方法）。

**参数**:
- `lat`: 纬度
- `lng`: 经度
- `radius`: 半径（米，可选）

**返回类型**: `List<GatheringNearby>`

**示例**:

```kotlin
val nearbyGatherings = sdk.gathering.getGatheringNearby(39.9042, 116.4074, 50000)
println("附近聚会数量: ${nearbyGatherings.size}")
```

### `getGatheringNearbyMode()`

获取聚会附近模式。

**返回类型**: `GatheringNearbyModeData`

**示例**:

```kotlin
val mode = sdk.gathering.getGatheringNearbyMode()
println("模式: ${mode.mode}")
println("意向聚会 ID 列表: ${mode.intentGatheringIds}")
```

### `getGatheringDetail(id: String)`

获取聚会详情。

**参数**:
- `id`: 聚会 ID

**返回类型**: `GatheringDetail`

**示例**:

```kotlin
val detail = sdk.gathering.getGatheringDetail("gathering-id")
println("聚会名称: ${detail.name}")
println("描述: ${detail.description}")
println("开始时间: ${detail.startDate}")
println("结束时间: ${detail.endDate}")
println("位置: ${detail.location}")
println("状态: ${detail.status}")
println("组织者: ${detail.organizer.displayName}")

println("报名统计:")
println("总报名数: ${detail.registrationStats.total}")
println("已批准: ${detail.registrationStats.approved}")
println("待审批: ${detail.registrationStats.pending}")
println("已签到: ${detail.registrationStats.checkedIn}")
```

### `getGatheringRegistrations(params: GatheringRegistrationsParams)`

获取聚会报名列表。

**参数**:
- `params`: 聚会报名列表参数
  - `gatheringId`: 聚会 ID
  - `status`: 报名状态筛选（可选）
  - `cursor`: 分页游标（可选）
  - `limit`: 返回数量限制（可选）

**返回类型**: `GatheringRegistrationsData`

**示例**:

```kotlin
val params = GatheringRegistrationsParams(
    gatheringId = "gathering-id",
    status = "approved",
    limit = 20
)
val registrations = sdk.gathering.getGatheringRegistrations(params)

println("聚会 ID: ${registrations.gatheringId}")
println("报名总数: ${registrations.totalCount}")
println("报名列表数量: ${registrations.registrations.size}")

registrations.registrations.forEach { registration ->
    println("用户名: ${registration.username}")
    println("显示名称: ${registration.displayName}")
    println("状态: ${registration.status}")
    println("是否签到: ${registration.checkedIn}")
    println("报名时间: ${registration.registeredAt}")
}
```

### `getGatheringRegistrations(id: String, status: String? = null, cursor: String? = null, limit: Int? = null)`

获取聚会报名列表（重载方法）。

**参数**:
- `id`: 聚会 ID
- `status`: 报名状态筛选（可选）
- `cursor`: 分页游标（可选）
- `limit`: 返回数量限制（可选）

**返回类型**: `GatheringRegistrationsData`

**示例**:

```kotlin
val registrations = sdk.gathering.getGatheringRegistrations("gathering-id", "approved", limit = 20)
println("报名总数: ${registrations.totalCount}")
```

## 数据结构

### GatheringStatsData

```kotlin
data class GatheringStatsData(
    val year: Int,         // 年份
    val total: Int,        // 总聚会数
    val upcoming: Int,     // 即将开始
    val ongoing: Int,      // 进行中
    val completed: Int     // 已完成
)
```

### GatheringMonthlyItem

```kotlin
data class GatheringMonthlyItem(
    val id: String,             // 聚会 ID
    val name: String,           // 聚会名称
    val startDate: String,      // 开始时间
    val endDate: String? = null, // 结束时间
    val location: String? = null, // 位置
    val status: String          // 状态
)
```

### GatheringWithDistance

```kotlin
data class GatheringWithDistance(
    val id: String,             // 聚会 ID
    val name: String,           // 聚会名称
    val startDate: String,      // 开始时间
    val location: String? = null, // 位置
    val distance: Double? = null, // 距离
    val status: String          // 状态
)
```

### GatheringNearby

```kotlin
data class GatheringNearby(
    val id: String,        // 聚会 ID
    val name: String,      // 聚会名称
    val location: String,  // 位置
    val lat: Double,       // 纬度
    val lng: Double,       // 经度
    val distance: Double,  // 距离
    val startDate: String  // 开始时间
)
```

### GatheringNearbyModeData

```kotlin
data class GatheringNearbyModeData(
    val mode: String,                  // 模式
    val intentGatheringIds: List<String> // 意向聚会 ID 列表
)
```

### GatheringDetail

```kotlin
data class GatheringDetail(
    val id: String,                  // 聚会 ID
    val name: String,                // 聚会名称
    val description: String? = null,  // 描述
    val startDate: String,           // 开始时间
    val endDate: String? = null,      // 结束时间
    val location: String,            // 位置
    val lat: Double? = null,          // 纬度
    val lng: Double? = null,          // 经度
    val agenda: List<AgendaItem>? = null, // 议程
    val tags: List<String>? = null,   // 标签
    val registrationStats: RegistrationStats, // 报名统计
    val status: String,               // 状态
    val organizer: OrganizerInfo      // 组织者信息
)

data class AgendaItem(
    val time: String,                // 时间
    val title: String,               // 标题
    val description: String? = null  // 描述
)

data class RegistrationStats(
    val total: Int,                  // 总报名数
    val approved: Int,               // 已批准
    val pending: Int,                // 待审批
    val checkedIn: Int,              // 已签到
    val capacity: Int? = null        // 容量
)

data class OrganizerInfo(
    val userId: String,              // 组织者用户 ID
    val username: String,            // 组织者用户名
    val displayName: String,         // 组织者显示名称
    val avatarUrl: String? = null    // 组织者头像 URL
)
```

### GatheringRegistrationsData

```kotlin
data class GatheringRegistrationsData(
    val gatheringId: String,              // 聚会 ID
    val registrations: List<Registration>, // 报名列表
    val totalCount: Int                   // 总数量
)

data class Registration(
    val id: String,                  // 报名 ID
    val userId: String,              // 用户 ID
    val username: String,            // 用户名
    val displayName: String,         // 显示名称
    val avatarUrl: String? = null,   // 头像 URL
    val status: String,              // 状态
    val checkedIn: Boolean,          // 是否已签到
    val registeredAt: String         // 报名时间
)
```
