# 聚会 API (Gathering)

聚会模块提供聚会列表、统计、详情、报名等功能。

## API 方法

### getYearStats()

获取年度统计

- **端点**: `GET /api/proxy/furtv/gatherings/stats/this-year`
- **返回**: `GatheringYearStatsData`（total）

### getMonthly(year, month)

获取月历聚会

- **端点**: `GET /api/proxy/furtv/gatherings/monthly`
- **参数**:
  - `year` (Int): 年份
  - `month` (Int): 月份
- **返回**: `List<GatheringMonthlyItem>`

### getMonthlyDistance(year, month, lat, lng)

获取带距离的月历聚会

- **端点**: `GET /api/proxy/furtv/gatherings/monthly-distance`
- **参数**:
  - `year` (Int): 年份
  - `month` (Int): 月份
  - `lat` (Double?, 可选): 纬度
  - `lng` (Double?, 可选): 经度
- **返回**: `List<GatheringMonthlyDistanceItem>`

### getNearby(lat, lng, radius)

获取附近聚会

- **端点**: `GET /api/proxy/furtv/gatherings/nearby`
- **参数**:
  - `lat` (Double?, 可选): 纬度
  - `lng` (Double?, 可选): 经度
  - `radius` (Int?, 可选): 半径（米）
- **返回**: `List<GatheringNearbyItem>`

### getNearbyMode()

获取附近模式聚会（含意图聚会 ID）

- **端点**: `GET /api/proxy/furtv/gatherings/nearby-mode`
- **参数**: 无
- **返回**: `GatheringNearbyModeData`

### getGatheringDetail(id)

获取聚会详情

- **端点**: `GET /api/proxy/furtv/gatherings/:id`
- **参数**: `id` (Int) - 聚会 ID
- **返回**: `GatheringDetailData`

### getRegistrations(id, status, cursor, limit)

获取报名列表

- **端点**: `GET /api/proxy/furtv/gatherings/:id/registrations`
- **参数**:
  - `id` (Int): 聚会 ID
  - `status` (String?, 可选): 状态筛选
  - `cursor` (String?, 可选): 分页游标
  - `limit` (Int?, 可选): 数量限制
- **返回**: `GatheringRegistrationsData`

## 数据模型

### GatheringYearStatsData

| 字段 | 类型 | 说明 |
|------|------|------|
| total | Int | 聚会总数 |

### GatheringMonthlyItem

| 字段 | 类型 | 说明 |
|------|------|------|
| id | Int | 聚会 ID |
| title | String | 聚会标题 |
| description | String? | 描述 |
| type | String? | 聚会类型 |
| typeClass | String? | 聚会类型分类 |
| contentSource | String? | 内容来源 |
| day | String? | 日期 |
| weekday | String? | 星期 |
| time | String? | 开始时间 |
| endTime | String? | 结束时间 |
| location | String? | 地点 |
| locationPublic | String? | 公开地点 |
| participants | String? | 参与人数描述 |
| logo | String? | Logo URL |
| status | String? | 状态 |
| badges | List\<GatheringBadge\>? | 徽章列表 |
| isFurtvCoopDriven | Boolean? | 是否为 Fursuit.TV 合作驱动 |
| sourceCount | Int? | 数据来源数量 |
| initialSource | String? | 初始来源 |
| dataSources | List\<DataSource\>? | 数据来源列表 |
| organizer | String? | 组织者名称 |
| organizerAvatar | String? | 组织者头像 URL |
| feeType | String? | 费用类型 |
| feeAmount | String? | 费用金额 |
| registrationStatus | String? | 报名状态 |
| requiresApproval | Boolean? | 是否需要审核 |

### GatheringBadge

| 字段 | 类型 | 说明 |
|------|------|------|
| code | String? | 徽章代码 |
| title | String? | 徽章标题 |

### DataSource

| 字段 | 类型 | 说明 |
|------|------|------|
| sourceCode | String? | 来源代码 |
| sourceUrl | String? | 来源 URL |
| name | String? | 来源名称 |
| logoUrl | String? | 来源 Logo URL |

### GatheringMonthlyDistanceItem

| 字段 | 类型 | 说明 |
|------|------|------|
| id | Int | 聚会 ID |
| distanceMeters | Double? | 距离（米） |

### GatheringNearbyItem

| 字段 | 类型 | 说明 |
|------|------|------|
| id | Int | 聚会 ID |
| title | String | 聚会标题 |
| eventDate | String? | 活动日期 |
| endDate | String? | 结束日期 |
| address | String? | 地址 |
| city | String? | 城市 |
| lat | Double? | 纬度 |
| lng | Double? | 经度 |
| badges | List\<GatheringBadge\>? | 徽章列表 |
| isFurtvCoopDriven | Boolean? | 是否为 Fursuit.TV 合作驱动 |

### GatheringNearbyModeData

| 字段 | 类型 | 说明 |
|------|------|------|
| gatherings | List\<GatheringNearbyItem\> | 附近聚会列表 |
| intentGatheringIds | List\<Int\> | 意图聚会 ID 列表 |

### GatheringDetailData

| 字段 | 类型 | 说明 |
|------|------|------|
| id | Int | 聚会 ID |
| title | String | 聚会标题 |
| description | String? | 描述 |
| eventDate | String? | 活动日期 |
| endDate | String? | 结束日期 |
| eventTime | String? | 活动时间 |
| endTime | String? | 结束时间 |
| type | String? | 聚会类型 |
| typeClass | String? | 聚会类型分类 |
| typeDisplay | String? | 聚会类型显示名称 |
| status | String? | 状态 |
| locationPublic | String? | 公开地点 |
| locationCity | String? | 所在城市 |
| locationLat | Double? | 纬度 |
| locationLng | Double? | 经度 |
| logoUrl | String? | Logo URL |
| bannerUrl | String? | Banner URL |
| organizerId | Int? | 组织者用户 ID |
| organizerUsername | String? | 组织者用户名 |
| organizerNickname | String? | 组织者昵称 |
| organizerAvatar | String? | 组织者头像 URL |
| coOrganizers | List\<CoOrganizer\>? | 协办者列表 |
| agenda | List\<AgendaItem\>? | 议程列表 |
| tags | List\<String\>? | 标签列表 |
| sourceCount | Int? | 数据来源数量 |
| dataSources | List\<DataSource\>? | 数据来源列表 |
| badges | List\<GatheringBadge\>? | 徽章列表 |
| isFurtvCoopDriven | Boolean? | 是否为 Fursuit.TV 合作驱动 |
| interestedCount | Int? | 感兴趣人数 |
| isInterested | Boolean? | 当前用户是否感兴趣 |
| goingFriendsCount | Int? | 参与的好友数 |
| registrationStats | GatheringRegistrationStats? | 报名统计 |
| viewCount | Int? | 浏览次数 |

### CoOrganizer

| 字段 | 类型 | 说明 |
|------|------|------|
| userId | Int? | 用户 ID |
| username | String? | 用户名 |
| nickname | String? | 昵称 |
| avatar | String? | 头像 URL |

### AgendaItem

| 字段 | 类型 | 说明 |
|------|------|------|
| time | String | 时间 |
| title | String | 标题 |
| description | String? | 描述 |

### GatheringRegistrationStats

| 字段 | 类型 | 说明 |
|------|------|------|
| totalRegistrations | Int? | 总报名人数 |
| approvedCount | Int? | 已批准人数 |
| pendingCount | Int? | 待审核人数 |

### GatheringRegistrationsData

| 字段 | 类型 | 说明 |
|------|------|------|
| registrations | List\<List\<RegistrationItem\>\> | 分组报名列表（二维数组） |

### RegistrationItem

| 字段 | 类型 | 说明 |
|------|------|------|
| id | Int | 报名 ID |
| status | String? | 报名状态 |
| registrationTime | String? | 报名时间 |
| checkedIn | Int? | 签到状态 |
| userId | Int | 用户 ID |
| username | String | 用户名 |
| nickname | String? | 昵称 |
| avatarUrl | String? | 头像 URL |
| fursuitSpecies | String? | 兽装物种 |

## 相关文档

- [错误处理](../error-handling.md)
