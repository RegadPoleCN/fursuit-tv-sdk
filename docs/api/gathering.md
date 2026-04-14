# 聚会 API (Gathering)

聚会模块提供聚会列表、统计、详情、报名等聚会相关功能的访问接口。

## API 方法

### getYearStats()

**获取聚会年度统计** - 获取当前年度的聚会统计数据

- **端点**: `GET /api/proxy/furtv/gatherings/stats/thisyear`
- **方法**: `suspend fun`
- **参数**: 无
- **返回**: `GatheringYearStatsData` - 聚会年度统计数据
- **响应字段**:
  - `year`: 年份
  - `totalGatherings`: 聚会总数
  - `totalParticipants`: 参与人数统计

**示例**:

```kotlin
val stats = sdk.gathering.getYearStats()
println("${stats.year}年聚会统计")
println("总聚会数：${stats.totalGatherings}")
```

### getMonthly(year, month)

**获取聚会月历** - 获取指定年月的聚会列表

- **端点**: `GET /api/proxy/furtv/gatherings/monthly`
- **方法**: `suspend fun`
- **参数**:
  - `year` (Int): 年份
  - `month` (Int): 月份
- **返回**: `List<GatheringMonthlyItem>` - 聚会列表
- **响应字段**:
  - `gatheringId`: 聚会 ID
  - `name`: 聚会名称
  - `startDate`: 开始日期
  - `endDate`: 结束日期
  - `location`: 地点

**示例**:

```kotlin
val gatherings = sdk.gathering.getMonthly(2024, 12)
println("12 月聚会：")
gatherings.forEach { gathering ->
    println("- ${gathering.name} (${gathering.startDate})")
}
```

### getMonthlyDistance(year, month, lat, lng)

**获取聚会月历距离** - 获取指定年月的聚会列表，并计算与指定位置的距离

- **端点**: `GET /api/proxy/furtv/gatherings/monthlydistance`
- **方法**: `suspend fun`
- **参数**:
  - `year` (Int): 年份
  - `month` (Int): 月份
  - `lat` (Double?): 纬度（可选）
  - `lng` (Double?): 经度（可选）
- **返回**: `List<GatheringMonthlyDistanceItem>` - 带距离的聚会列表

**示例**:

```kotlin
val gatherings = sdk.gathering.getMonthlyDistance(2024, 12, 37.7749, -122.4194)
gatherings.forEach { gathering ->
    println("- ${gathering.name}: ${gathering.distance} 米")
}
```

### getNearby(lat, lng, radius)

**获取附近聚会** - 根据地理位置获取附近的聚会

- **端点**: `GET /api/proxy/furtv/gatherings/nearby`
- **方法**: `suspend fun`
- **参数**:
  - `lat` (Double): 纬度
  - `lng` (Double): 经度
  - `radius` (Int?): 搜索半径（米，可选）
- **返回**: `List<GatheringNearbyItem>` - 附近聚会列表

**示例**:

```kotlin
val nearbyGatherings = sdk.gathering.getNearby(37.7749, -122.4194, radius = 10000)
println("附近聚会：")
nearbyGatherings.forEach { gathering ->
    println("- ${gathering.name} (${gathering.distance} 米)")
}
```

### getGatheringDetail(gatheringId)

**获取聚会详情** - 获取聚会的详细信息

- **端点**: `GET /api/proxy/furtv/gatherings/detail`
- **方法**: `suspend fun`
- **参数**: `gatheringId` (String) - 聚会 ID
- **返回**: `GatheringDetailData` - 聚会详情数据
- **响应字段**:
  - `gatheringId`: 聚会 ID
  - `name`: 聚会名称
  - `description`: 聚会描述
  - `startDate`: 开始日期
  - `endDate`: 结束日期
  - `location`: 地点详情
  - `registrationCount`: 报名人数
  - `agenda`: 议程列表

**示例**:

```kotlin
val detail = sdk.gathering.getGatheringDetail("gathering-id")
println("聚会：${detail.name}")
println("时间：${detail.startDate} - ${detail.endDate}")
println("地点：${detail.location.name}")
println("报名人数：${detail.registrationCount}")
```

### getRegistrations(gatheringId, status, cursor, limit)

**获取聚会报名列表** - 获取聚会的报名人员列表

- **端点**: `GET /api/proxy/furtv/gatherings/registrations`
- **方法**: `suspend fun`
- **参数**:
  - `gatheringId` (String): 聚会 ID
  - `status` (String?): 报名状态筛选（可选）
  - `cursor` (String?): 分页游标（可选）
  - `limit` (Int?): 返回数量限制（可选）
- **返回**: `GatheringRegistrationsData` - 报名列表数据

**示例**:

```kotlin
val registrations = sdk.gathering.getRegistrations("gathering-id")
println("报名人数：${registrations.total}")
registrations.registrations.forEach { registration ->
    println("- ${registration.username}")
}
```

## 使用场景

### 1. 查看年度聚会统计

```kotlin
val stats = sdk.gathering.getYearStats()
println("${stats.year}年共有 ${stats.totalGatherings} 场聚会")
```

### 2. 查看本月聚会

```kotlin
val now = Clock.System.now()
val year = now.year
val month = now.monthNumber

val gatherings = sdk.gathering.getMonthly(year, month)
println("本月聚会：")
gatherings.forEach { gathering ->
    println("- ${gathering.name} (${gathering.startDate})")
}
```

### 3. 查找附近聚会

```kotlin
// 查找距离当前位置 10 公里内的聚会
val nearby = sdk.gathering.getNearby(
    lat = 37.7749,
    lng = -122.4194,
    radius = 10000
)

nearby.forEach { gathering ->
    println("${gathering.name} - ${gathering.distance} 米")
}
```

### 4. 查看聚会详情和报名

```kotlin
val detail = sdk.gathering.getGatheringDetail("gathering-id")
println("=== ${detail.name} ===")
println("时间：${detail.startDate} - ${detail.endDate}")
println("地点：${detail.location.address}")
println("报名人数：${detail.registrationCount}")

// 获取报名列表
val registrations = sdk.gathering.getRegistrations("gathering-id")
println("报名人员：")
registrations.registrations.forEach { reg ->
    println("- ${reg.username}")
}
```

## 相关文档

- [聚会年度统计](../../vds-docs/Fursuit.TV 兽频道/聚会 - 列表与统计/聚会年度统计（furtv.gatherings.stats.thisyear）.md)
- [聚会月历](../../vds-docs/Fursuit.TV 兽频道/聚会 - 列表与统计/聚会月历（furtv.gatherings.monthly）.md)
- [聚会附近](../../vds-docs/Fursuit.TV 兽频道/聚会 - 列表与统计/聚会附近（furtv.gatherings.nearby）.md)
- [聚会详情](../../vds-docs/Fursuit.TV 兽频道/聚会 - 详情与报名/聚会详情（furtv.gatherings.detail）.md)
- [聚会报名列表](../../vds-docs/Fursuit.TV 兽频道/聚会 - 详情与报名/聚会报名列表（furtv.gatherings.registrations）.md)
