# 用户 API (User)

用户模块提供用户资料公开信息的访问接口，包括用户基础信息、关系、访客、社交徽章、商店商品等。

## API 方法

### getUserProfile(username)

**获取用户资料公开信息** - 获取指定用户的公开资料

- **端点**: `GET /api/proxy/furtv/users/:username`
- **方法**: `suspend fun`
- **参数**: `username` (String) - 用户名
- **返回**: `UserProfile` - 用户资料公开信息
- **响应字段**:
  - `userId`: 用户 ID
  - `username`: 用户名
  - `displayName`: 显示名称
  - `avatarUrl`: 头像 URL
  - `bio`: 简介
  - `species`: 物种
  - `gender`: 性别
  - `createdAt`: 创建时间

**示例**:

```kotlin
val profile = sdk.user.getUserProfile("username")
println("昵称：${profile.displayName}")
println("物种：${profile.species}")
```

### getUserId(id)

**用户基础信息 ID 查询** - 通过用户标识符查询用户 ID

- **端点**: `GET /api/proxy/furtv/users/id/:id`
- **方法**: `suspend fun`
- **参数**: `id` (String) - 用户 ID
- **返回**: `UserIdData` - 用户基础信息
- **响应字段**:
  - `userId`: 用户 ID
  - `username`: 用户名

**示例**:

```kotlin
val userIdData = sdk.user.getUserId("userId")
println("用户 ID: ${userIdData.userId}")
```

### getLikeStatus(username)

**获取用户点赞状态** - 获取当前用户对指定用户的点赞状态

- **端点**: `GET /api/proxy/furtv/fursuit/like-status/:username`
- **方法**: `suspend fun`
- **参数**: `username` (String) - 用户名
- **返回**: `LikeStatusData` - 点赞状态数据
- **响应字段**:
  - `likeCount`: 点赞总数
  - `isLiked`: 是否已点赞
  - `canLike`: 是否可以点赞

**示例**:

```kotlin
val likeStatus = sdk.user.getLikeStatus("username")
println("点赞数：${likeStatus.likeCount}")
println("已点赞：${likeStatus.isLiked}")
```

### getUserRelationships(userId)

**获取用户关系公开列表** - 获取指定用户的关系公开列表

- **端点**: `GET /api/proxy/furtv/relationships/user/:userId`
- **方法**: `suspend fun`
- **参数**: `userId` (String) - 用户 ID
- **返回**: `UserRelationshipsData` - 用户关系数据
- **响应字段**:
  - `userId`: 用户 ID
  - `relationships`: 关系列表
    - `type`: 关系类型
    - `partner`: 伴侣信息

**示例**:

```kotlin
val relationships = sdk.user.getUserRelationships("userId")
relationships.relationships.forEach { rel ->
    println("关系类型：${rel.type}")
}
```

### getUserVisitors(username)

**获取用户访客记录** - 获取指定用户的访客记录列表

- **端点**: `GET /api/proxy/furtv/users/:username/visitors`
- **方法**: `suspend fun`
- **参数**: `username` (String) - 用户名
- **返回**: `UserVisitorsData` - 用户访客数据
- **响应字段**:
  - `visitors`: 访客列表
    - `userId`: 访客用户 ID
    - `username`: 访客用户名
    - `visitTime`: 访问时间

**示例**:

```kotlin
val visitors = sdk.user.getUserVisitors("username")
visitors.visitors.forEach { visitor ->
    println("访客：${visitor.username}")
    println("访问时间：${visitor.visitTime}")
}
```

### getSocialBadges(username)

**获取用户社交徽章列表** - 获取指定用户的社交徽章列表

- **端点**: `GET /api/proxy/furtv/users/:username/social-badges`
- **方法**: `suspend fun`
- **参数**: `username` (String) - 用户名
- **返回**: `SocialBadgesData` - 用户社交徽章数据
- **响应字段**:
  - `badges`: 徽章列表
    - `badgeId`: 徽章 ID
    - `name`: 徽章名称
    - `description`: 徽章描述
    - `iconUrl`: 徽章图标 URL

**示例**:

```kotlin
val badges = sdk.user.getSocialBadges("username")
badges.badges.forEach { badge ->
    println("徽章：${badge.name}")
    println("描述：${badge.description}")
}
```

### getSocialBadgeDetail(username, userBadgeId)

**获取用户社交徽章详情** - 获取指定徽章的详细信息

- **端点**: `GET /api/proxy/furtv/users/:username/social-badges/:userBadgeId`
- **方法**: `suspend fun`
- **参数**:
  - `username` (String) - 用户名
  - `userBadgeId` (String) - 用户徽章 ID
- **返回**: `SocialBadgeDetailData` - 社交徽章详情数据
- **响应字段**:
  - `badgeId`: 徽章 ID
  - `name`: 徽章名称
  - `description`: 徽章描述
  - `grantedBy`: 授予者
  - `grantedAt`: 授予时间

**示例**:

```kotlin
val badgeDetail = sdk.user.getSocialBadgeDetail("username", "userBadgeId")
println("徽章：${badgeDetail.name}")
println("授予者：${badgeDetail.grantedBy}")
```

### getStoreProducts(username)

**获取用户商店商品** - 获取指定用户的商店商品列表

- **端点**: `GET /api/proxy/furtv/users/:username/store-products`
- **方法**: `suspend fun`
- **参数**: `username` (String) - 用户名
- **返回**: `StoreProductsData` - 用户商店商品数据
- **响应字段**:
  - `products`: 商品列表
    - `productId`: 商品 ID
    - `name`: 商品名称
    - `price`: 价格
    - `description`: 商品描述
    - `imageUrl`: 商品图片 URL

**示例**:

```kotlin
val products = sdk.user.getStoreProducts("username")
products.products.forEach { product ->
    println("商品：${product.name}")
    println("价格：${product.price}")
}
```

## 数据模型

### UserProfile

```kotlin
public data class UserProfile(
    public val userId: String,
    public val username: String,
    public val displayName: String,
    public val avatarUrl: String?,
    public val bio: String?,
    public val species: String?,
    public val gender: String?,
    public val createdAt: Long
)
```

### LikeStatusData

```kotlin
public data class LikeStatusData(
    public val likeCount: Int,
    public val isLiked: Boolean,
    public val canLike: Boolean
)
```

## 使用场景

### 1. 获取用户完整资料

```kotlin
val profile = sdk.user.getUserProfile("username")
println("=== ${profile.displayName} ===")
println("用户名：@${profile.username}")
println("物种：${profile.species}")
println("简介：${profile.bio}")
```

### 2. 检查点赞状态

```kotlin
val likeStatus = sdk.user.getLikeStatus("username")
if (likeStatus.canLike && !likeStatus.isLiked) {
    println("可以点赞")
} else if (likeStatus.isLiked) {
    println("已点赞")
}
```

### 3. 查看用户社交徽章

```kotlin
val badges = sdk.user.getSocialBadges("username")
println("=== 社交徽章 ===")
badges.badges.forEach { badge ->
    println("- ${badge.name}: ${badge.description}")
}
```

## 相关文档

- [用户资料公开信息](../../vds-docs/Fursuit.TV 兽频道/用户公开资料 - 基础信息/用户资料公开信息（furtv.users.profile）.md)
- [用户点赞状态](../../vds-docs/Fursuit.TV 兽频道/用户公开资料 - 基础信息/用户点赞状态（furtv.fursuit.likestatus）.md)
- [用户关系公开列表](../../vds-docs/Fursuit.TV 兽频道/用户公开资料 - 关系与访客/用户关系公开列表（furtv.relationships.user）.md)
- [用户访客记录](../../vds-docs/Fursuit.TV 兽频道/用户公开资料 - 关系与访客/用户访客记录（furtv.users.visitors）.md)
- [用户社交徽章列表](../../vds-docs/Fursuit.TV 兽频道/用户公开资料 - 徽章与商店/用户社交徽章列表（furtv.users.socialbadges）.md)
