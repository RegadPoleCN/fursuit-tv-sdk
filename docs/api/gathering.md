# 聚会 API (Gathering)

聚会模块提供聚会列表、统计、详情、报名等功能。

## API 方法

### getYearStats()

获取年度统计

- **端点**: `GET /api/proxy/furtv/gatherings/stats/this-year`
- **返回**: `GatheringYearStatsData`（year, totalGatherings, totalParticipants）

### getMonthly(year, month)

获取月历聚会

- **端点**: `GET /api/proxy/furtv/gatherings/monthly`
- **参数**:
  - `year` (Int): 年份
  - `month` (Int): 月份
- **返回**: `List<GatheringMonthlyItem>`（gatheringId, name, startDate, endDate, location）

### getMonthlyDistance(year, month, lat, lng)

获取带距离的月历聚会

- **端点**: `GET /api/proxy/furtv/gatherings/monthlydistance`
- **参数**:
  - `year` (Int): 年份
  - `month` (Int): 月份
  - `lat` (Double?, 可选): 纬度
  - `lng` (Double?, 可选): 经度
- **返回**: `List<GatheringMonthlyDistanceItem>`（带 distance 字段）

### getNearby(lat, lng, radius)

获取附近聚会

- **端点**: `GET /api/proxy/furtv/gatherings/nearby`
- **参数**:
  - `lat` (Double): 纬度
  - `lng` (Double): 经度
  - `radius` (Int?, 可选): 半径（米）
- **返回**: `List<GatheringNearbyItem>`（gatheringId, name, distance）

### getGatheringDetail(id)

获取聚会详情

- **端点**: `GET /api/proxy/furtv/gatherings/:id`
- **参数**: `id` (String) - 聚会 ID
- **返回**: `GatheringDetailData`（gatheringId, name, description, startDate, endDate, location, registrationCount, agenda[]）

### getRegistrations(id, status, cursor, limit)

获取报名列表

- **端点**: `GET /api/proxy/furtv/gatherings/:id/registrations`
- **参数**:
  - `id` (String): 聚会 ID
  - `status` (String?, 可选): 状态筛选
  - `cursor` (String?, 可选): 分页游标
  - `limit` (Int?, 可选): 数量限制
- **返回**: `GatheringRegistrationsData`（registrations[], total）

## 数据模型

### GatheringDetailData

| 字段 | 类型 | 说明 |
|------|------|------|
| gatheringId | String | 聚会 ID |
| name | String | 名称 |
| description | String? | 描述 |
| startDate | String | 开始日期 |
| endDate | String | 结束日期 |
| location | Location | 地点详情 |
| registrationCount | Int | 报名人数 |
| agenda | List | 议程列表 |

## 相关文档

- [错误处理](../error-handling.md)
