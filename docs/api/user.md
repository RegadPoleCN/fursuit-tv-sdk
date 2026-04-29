# 用户 API (User)

用户模块提供用户资料、关系、访客、徽章、商店等公开信息查询。

## API 方法

### getUserProfile(username)

获取用户资料公开信息

- **端点**: `GET /api/proxy/furtv/users/profile`
- **参数**: `username` (String) - 用户名
- **返回**: `UserProfile`

### getUserId(id)

用户 ID 查询

- **端点**: `GET /api/proxy/furtv/users/id`
- **参数**: `id` (Int) - 用户 ID
- **返回**: `UserIdData`

### getLikeStatus(username)

获取点赞状态

- **端点**: `GET /api/proxy/furtv/fursuit/likestatus`
- **参数**: `username` (String) - 用户名
- **返回**: `LikeStatusData`

### getUserRelationships(userId)

获取用户关系列表

- **端点**: `GET /api/proxy/furtv/relationships/user`
- **参数**: `userId` (Int) - 用户 ID
- **返回**: `UserRelationshipsData`

### getUserVisitors(username)

获取访客记录

- **端点**: `GET /api/proxy/furtv/users/visitors`
- **参数**: `username` (String) - 用户名
- **返回**: `UserVisitorsData`

### getSocialBadges(username)

获取社交徽章列表

- **端点**: `GET /api/proxy/furtv/users/socialbadges`
- **参数**: `username` (String) - 用户名
- **返回**: `SocialBadgesData`

### getSocialBadgeDetail(username, userBadgeId)

获取徽章详情

- **端点**: `GET /api/proxy/furtv/users/socialbadges/detail`
- **参数**:
  - `username` (String) - 用户名
  - `userBadgeId` (Int) - 徽章 ID
- **返回**: `SocialBadgeDetailData`

### getStoreProducts(username)

获取商店商品

- **端点**: `GET /api/proxy/furtv/users/storeproducts`
- **参数**: `username` (String) - 用户名
- **返回**: `StoreProductsData`

## 数据模型

### UserProfile

| 字段 | 类型 | 说明 |
|------|------|------|
| id | Int | 用户 ID |
| username | String | 用户名 |
| nickname | String? | 昵称 |
| avatarUrl | String? | 头像 URL |
| fursuitSpecies | String? | 兽装物种 |
| fursuitBirthday | String? | 兽装生日 |
| fursuitMaker | String? | 兽装制作者 |
| showcasePortrait | String? | 展示竖图 URL |
| showcaseLandscape | String? | 展示横图 URL |
| showcaseOther | String? | 展示其他图 URL |
| introduction | String? | 自我介绍 |
| interests | List\<String\>? | 兴趣标签列表 |
| location | String? | 地理位置 |
| socialLinks | Map\<String, String\>? | 社交链接映射 |
| contactInfo | Map\<String, String\>? | 联系方式映射 |
| privacySettings | UserProfilePrivacySettings? | 隐私设置 |
| characters | List\<UserProfileCharacter\>? | 角色列表 |
| otherVerifiedTypes | List\<String\>? | 其他认证类型 |
| viewCount | Int? | 主页浏览量 |
| isVerified | Boolean? | 是否已认证 |
| createdAt | String? | 注册时间 |
| destinations | List\<UserDestination\>? | 目的地列表 |
| destination | String? | 当前目的地 |
| destinationExpiresAt | String? | 当前目的地过期时间 |

### UserProfilePrivacySettings

| 字段 | 类型 | 说明 |
|------|------|------|
| showEmail | Boolean? | 是否公开邮箱 |
| allowContact | Boolean? | 是否允许联系 |
| showLocation | Boolean? | 是否公开位置 |
| allowMessages | Boolean? | 是否允许私信 |
| allowReturnImages | Boolean? | 是否允许返图 |
| profilePublic | Boolean? | 是否公开资料 |
| showVisitorDetails | Boolean? | 是否显示访客详情 |

### UserProfileCharacter

| 字段 | 类型 | 说明 |
|------|------|------|
| id | String | 角色 ID |
| name | String | 角色名称 |
| species | String? | 物种 |
| gender | String? | 性别 |
| worldview | String? | 世界观 |

### UserDestination

| 字段 | 类型 | 说明 |
|------|------|------|
| id | Int | 目的地 ID |
| name | String | 目的地名称 |
| startDate | String? | 开始日期 |
| endDate | String? | 结束日期 |
| gatheringId | Int? | 关联聚会 ID |

### UserIdData

| 字段 | 类型 | 说明 |
|------|------|------|
| id | Int | 用户 ID |
| username | String | 用户名 |
| nickname | String? | 昵称 |
| avatarUrl | String? | 头像 URL |
| fursuitSpecies | String? | 兽装物种 |
| location | String? | 地理位置 |

### LikeStatusData

| 字段 | 类型 | 说明 |
|------|------|------|
| likeCount | Int | 点赞总数 |
| isLiked | Boolean | 当前用户是否已点赞 |
| canLike | Boolean | 当前用户是否可以点赞 |
| daysUntilCanLike | Int? | 距离可点赞的天数 |

### UserRelationshipsData

| 字段 | 类型 | 说明 |
|------|------|------|
| relationships | List\<RelationshipInfo\> | 关系列表 |

### RelationshipInfo

| 字段 | 类型 | 说明 |
|------|------|------|
| id | Int | 关系记录 ID |
| relationshipType | String | 关系类型 |
| createdAt | String? | 关系建立时间 |
| partnerId | Int | 伴侣用户 ID |
| partnerUsername | String | 伴侣用户名 |
| partnerNickname | String? | 伴侣昵称 |
| partnerAvatar | String? | 伴侣头像 URL |
| partnerSpecies | String? | 伴侣物种 |

### UserVisitorsData

| 字段 | 类型 | 说明 |
|------|------|------|
| visitors | List\<VisitorInfo\> | 访客列表 |
| isOwner | Boolean? | 当前用户是否为资料所有者 |
| message | String? | 条件性提示消息 |

### VisitorInfo

| 字段 | 类型 | 说明 |
|------|------|------|
| visitorId | Int | 访客用户 ID |
| visitorUsername | String | 访客用户名 |
| visitorNickname | String? | 访客昵称 |
| visitorAvatar | String? | 访客头像 URL |
| createdAt | String | 访问时间 |

### SocialBadgesData

| 字段 | 类型 | 说明 |
|------|------|------|
| user | SocialBadgeUser? | 徽章所属用户 |
| isOwner | Boolean? | 当前用户是否为资料所有者 |
| totalCount | Int? | 徽章总数 |
| badges | List\<SocialBadge\> | 徽章列表 |

### SocialBadgeUser

| 字段 | 类型 | 说明 |
|------|------|------|
| id | Int | 用户 ID |
| username | String | 用户名 |
| nickname | String? | 昵称 |

### SocialBadge

| 字段 | 类型 | 说明 |
|------|------|------|
| userBadgeId | Int | 用户徽章记录 ID |
| badgeId | Int | 徽章模板 ID |
| title | String | 徽章标题 |
| glbUrl | String? | 3D 模型 URL（GLB 格式） |
| awardedAt | String | 授予时间 |
| expiresAt | String? | 过期时间 |
| detailText | String? | 详情文本 |

### SocialBadgeDetailData

| 字段 | 类型 | 说明 |
|------|------|------|
| user | SocialBadgeUser? | 徽章所属用户 |
| isOwner | Boolean? | 当前用户是否为资料所有者 |
| badge | SocialBadgeDetail | 徽章详情 |

### SocialBadgeDetail

| 字段 | 类型 | 说明 |
|------|------|------|
| userBadgeId | Int | 用户徽章记录 ID |
| badgeId | Int | 徽章模板 ID |
| title | String | 徽章标题 |
| glbUrl | String? | 3D 模型 URL（GLB 格式） |
| awardedAt | String | 授予时间 |
| expiresAt | String? | 过期时间 |
| detailText | String? | 详情文本 |

### StoreProductsData

| 字段 | 类型 | 说明 |
|------|------|------|
| user | StoreUser? | 商店所属用户 |
| isOwner | Boolean? | 当前用户是否为资料所有者 |
| isMerchantVerified | Boolean? | 是否已认证商家 |
| products | List\<StoreProduct\> | 商品列表 |

### StoreUser

| 字段 | 类型 | 说明 |
|------|------|------|
| id | Int | 用户 ID |
| username | String | 用户名 |
| nickname | String? | 昵称 |

### StoreProduct

| 字段 | 类型 | 说明 |
|------|------|------|
| id | Int | 商品 ID |
| name | String | 商品名称 |
| price | String? | 价格 |
| imageUrl | String? | 商品图片 URL |
| externalUrl | String? | 外部购买链接 |
| sortOrder | Int? | 排序权重 |
| createdAt | String? | 创建时间 |
| updatedAt | String? | 更新时间 |

## 相关文档

- [错误处理](../error-handling.md)
