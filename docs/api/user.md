# 用户 API

用户 API 包含与用户相关的接口，如获取用户资料、点赞状态、关系列表等。

## 方法列表

### `getUserProfile(username: String)`

获取用户资料公开信息。

**参数**:
- `username`: 用户名

**返回类型**: `UserProfile`

**示例**:

```kotlin
val userProfile = sdk.user.getUserProfile("username")
println("用户名: ${userProfile.username}")
println("显示名称: ${userProfile.displayName}")
println("个人简介: ${userProfile.bio}")
println("物种: ${userProfile.species}")
println("位置: ${userProfile.location}")
```

### `getUserById(id: String)`

通过 ID 查询用户基础信息。

**参数**:
- `id`: 用户 ID

**返回类型**: `UserBasicInfo`

**示例**:

```kotlin
val userInfo = sdk.user.getUserById("user-id")
println("用户 ID: ${userInfo.id}")
println("用户名: ${userInfo.username}")
println("显示名称: ${userInfo.displayName}")
```

### `getLikeStatus(username: String)`

获取用户点赞状态。

**参数**:
- `username`: 用户名

**返回类型**: `LikeStatus`

**示例**:

```kotlin
val likeStatus = sdk.user.getLikeStatus("username")
println("是否可以点赞: ${likeStatus.canLike}")
println("是否已点赞: ${likeStatus.isLiked}")
println("点赞数: ${likeStatus.likeCount}")
```

### `getUserRelationships(userId: String)`

获取用户关系公开列表。

**参数**:
- `userId`: 用户 ID

**返回类型**: `RelationshipsData`

**示例**:

```kotlin
val relationships = sdk.user.getUserRelationships("user-id")
println("用户 ID: ${relationships.userId}")
println("关系数量: ${relationships.totalCount}")

relationships.partners.forEach { partner ->
    println("伴侣: ${partner.displayName}")
    println("关系类型: ${partner.relationshipType}")
}
```

### `getUserVisitors(username: String)`

获取用户访客记录。

**参数**:
- `username`: 用户名

**返回类型**: `VisitorsData`

**示例**:

```kotlin
val visitors = sdk.user.getUserVisitors("username")
println("用户名: ${visitors.username}")
println("访客数量: ${visitors.totalCount}")
println("显示访客详情: ${visitors.showVisitorDetails}")

visitors.visitors.forEach { visitor ->
    println("访客: ${visitor.displayName}")
    println("访问时间: ${visitor.visitedAt}")
}
```

### `getUserStoreProducts(username: String)`

获取用户商店商品。

**参数**:
- `username`: 用户名

**返回类型**: `StoreProductsData`

**示例**:

```kotlin
val storeProducts = sdk.user.getUserStoreProducts("username")
println("用户名: ${storeProducts.username}")
println("是否为商家: ${storeProducts.isMerchant}")
println("商品数量: ${storeProducts.products.size}")

storeProducts.products.forEach { product ->
    println("商品名称: ${product.name}")
    println("价格: ${product.price}")
    println("描述: ${product.description}")
}
```

### `getUserSocialBadges(username: String)`

获取用户社交徽章列表。

**参数**:
- `username`: 用户名

**返回类型**: `SocialBadgesData`

**示例**:

```kotlin
val badges = sdk.user.getUserSocialBadges("username")
println("用户名: ${badges.username}")
println("徽章数量: ${badges.badges.size}")

badges.badges.forEach { badge ->
    println("徽章名称: ${badge.name}")
    println("获得时间: ${badge.awardedAt}")
}
```

### `getSocialBadgeDetail(username: String, userBadgeId: String)`

获取用户社交徽章详情。

**参数**:
- `username`: 用户名
- `userBadgeId`: 用户徽章 ID

**返回类型**: `SocialBadgeDetail`

**示例**:

```kotlin
val badgeDetail = sdk.user.getSocialBadgeDetail("username", "badge-id")
println("徽章名称: ${badgeDetail.name}")
println("描述: ${badgeDetail.description}")
println("获得时间: ${badgeDetail.awardedAt}")
println("授予者: ${badgeDetail.awardedBy}")
```

## 数据结构

### UserProfile

```kotlin
data class UserProfile(
    val username: String,             // 用户名
    val displayName: String,          // 显示名称
    val avatarUrl: String? = null,     // 头像 URL
    val bio: String? = null,           // 个人简介
    val species: String? = null,       // 物种
    val location: String? = null,      // 位置
    val destinations: List<String>? = null, // 目的地
    val privacy: UserPrivacySettings   // 隐私设置
)

data class UserPrivacySettings(
    val showVisitorDetails: Boolean,   // 是否显示访客详情
    val showRelationships: Boolean     // 是否显示关系
)
```

### UserBasicInfo

```kotlin
data class UserBasicInfo(
    val id: String,                    // 用户 ID
    val username: String,              // 用户名
    val displayName: String,           // 显示名称
    val avatarUrl: String? = null      // 头像 URL
)
```

### LikeStatus

```kotlin
data class LikeStatus(
    val canLike: Boolean,              // 是否可以点赞
    val isLiked: Boolean,              // 是否已点赞
    val likeCount: Int                 // 点赞数
)
```

### RelationshipsData

```kotlin
data class RelationshipsData(
    val userId: String,                // 用户 ID
    val partners: List<RelationshipPartner>, // 伴侣列表
    val totalCount: Int                // 总数量
)

data class RelationshipPartner(
    val userId: String,                // 伴侣用户 ID
    val username: String,              // 伴侣用户名
    val displayName: String,           // 伴侣显示名称
    val avatarUrl: String? = null,     // 伴侣头像 URL
    val relationshipType: String       // 关系类型
)
```

### VisitorsData

```kotlin
data class VisitorsData(
    val username: String,              // 用户名
    val visitors: List<Visitor>,       // 访客列表
    val totalCount: Int,               // 总数量
    val showVisitorDetails: Boolean    // 是否显示访客详情
)

data class Visitor(
    val userId: String,                // 访客用户 ID
    val username: String,              // 访客用户名
    val displayName: String,           // 访客显示名称
    val avatarUrl: String? = null,     // 访客头像 URL
    val visitedAt: String              // 访问时间
)
```

### StoreProductsData

```kotlin
data class StoreProductsData(
    val username: String,              // 用户名
    val isMerchant: Boolean,           // 是否为商家
    val products: List<StoreProduct>   // 商品列表
)

data class StoreProduct(
    val id: String,                    // 商品 ID
    val name: String,                  // 商品名称
    val description: String? = null,   // 商品描述
    val price: Double? = null,         // 价格
    val currency: String? = null,      // 货币
    val externalUrl: String? = null,   // 外部链接
    val imageUrl: String? = null       // 图片 URL
)
```

### SocialBadgesData

```kotlin
data class SocialBadgesData(
    val username: String,              // 用户名
    val badges: List<SocialBadge>      // 徽章列表
)

data class SocialBadge(
    val id: String,                    // 徽章 ID
    val name: String,                  // 徽章名称
    val description: String? = null,   // 徽章描述
    val iconUrl: String? = null,       // 图标 URL
    val glbUrl: String? = null,        // GLB 模型 URL
    val awardedAt: String              // 获得时间
)
```

### SocialBadgeDetail

```kotlin
data class SocialBadgeDetail(
    val id: String,                    // 徽章 ID
    val userBadgeId: String,           // 用户徽章 ID
    val name: String,                  // 徽章名称
    val description: String? = null,   // 徽章描述
    val iconUrl: String? = null,       // 图标 URL
    val glbUrl: String? = null,        // GLB 模型 URL
    val awardedAt: String,             // 获得时间
    val awardedBy: String? = null      // 授予者
)
```
