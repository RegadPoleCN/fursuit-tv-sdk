# 用户 API (User)

用户模块提供用户资料、关系、访客、徽章、商店等公开信息查询。

## API 方法

### getUserProfile(username)

获取用户资料公开信息

- **端点**: `GET /api/proxy/furtv/users/:username`
- **参数**: `username` (String) - 用户名
- **返回**: `UserProfile`（userId, username, displayName, avatarUrl, bio, species, gender, createdAt）

### getUserId(id)

用户 ID 查询

- **端点**: `GET /api/proxy/furtv/users/id/:id`
- **参数**: `id` (String) - 用户 ID
- **返回**: `UserIdData`（userId, username）

### getLikeStatus(username)

获取点赞状态

- **端点**: `GET /api/proxy/furtv/fursuit/like-status/:username`
- **参数**: `username` (String) - 用户名
- **返回**: `LikeStatusData`（likeCount, isLiked, canLike）

### getUserRelationships(userId)

获取用户关系列表

- **端点**: `GET /api/proxy/furtv/relationships/user/:userId`
- **参数**: `userId` (String) - 用户 ID
- **返回**: `UserRelationshipsData`（userId, relationships[]）

### getUserVisitors(username)

获取访客记录

- **端点**: `GET /api/proxy/furtv/users/:username/visitors`
- **参数**: `username` (String) - 用户名
- **返回**: `UserVisitorsData`（visitors[]）

### getSocialBadges(username)

获取社交徽章列表

- **端点**: `GET /api/proxy/furtv/users/:username/social-badges`
- **参数**: `username` (String) - 用户名
- **返回**: `SocialBadgesData`（badges[]）

### getSocialBadgeDetail(username, userBadgeId)

获取徽章详情

- **端点**: `GET /api/proxy/furtv/users/:username/social-badges/:userBadgeId`
- **参数**: 
  - `username` (String) - 用户名
  - `userBadgeId` (String) - 徽章 ID
- **返回**: `SocialBadgeDetailData`（badgeId, name, description, grantedBy, grantedAt）

### getStoreProducts(username)

获取商店商品

- **端点**: `GET /api/proxy/furtv/users/:username/store-products`
- **参数**: `username` (String) - 用户名
- **返回**: `StoreProductsData`（products[]）

## 数据模型

### UserProfile

| 字段 | 类型 | 说明 |
|------|------|------|
| userId | String | 用户 ID |
| username | String | 用户名 |
| displayName | String | 显示名 |
| avatarUrl | String? | 头像 |
| bio | String? | 简介 |
| species | String? | 物种 |
| gender | String? | 性别 |
| createdAt | Long | 创建时间 |

### LikeStatusData

| 字段 | 类型 | 说明 |
|------|------|------|
| likeCount | Int | 点赞数 |
| isLiked | Boolean | 是否已点赞 |
| canLike | Boolean | 是否可点赞 |

## 相关文档

- [错误处理](../error-handling.md)
