# 搜索 API (Search)

搜索模块提供热门推荐、随机推荐、搜索、物种查询等发现功能的访问接口。

## API 方法

### getPopular()

**获取热门推荐** - 获取当前热门用户列表

- **端点**: `GET /api/proxy/furtv/popular`
- **方法**: `suspend fun`
- **参数**: 无
- **返回**: `PopularData` - 包含热门用户列表
- **响应字段**:
  - `users`: 热门用户列表

**示例**:

```kotlin
val popular = sdk.search.getPopular()
println("热门用户数：${popular.users.size}")
popular.users.forEach { user ->
    println("- ${user.displayName}")
}
```

### getRandomFursuit(count, personalized)

**获取随机推荐** - 获取随机推荐的用户列表

- **端点**: `GET /api/proxy/furtv/fursuit/random`
- **方法**: `suspend fun`
- **参数**:
  - `count` (Int?): 返回数量（可选）
  - `personalized` (Boolean?): 是否个性化推荐（可选）
- **返回**: `List<RandomFursuit>` - 随机推荐的用户列表

**示例**:

```kotlin
val randomUsers = sdk.search.getRandomFursuit(count = 10, personalized = true)
randomUsers.forEach { user ->
    println("- ${user.displayName}")
}
```

### search(query, type, cursor, limit)

**搜索** - 执行搜索操作，支持多种类型和分页

- **端点**: `GET /api/proxy/furtv/search`
- **方法**: `suspend fun`
- **参数**:
  - `query` (String): 搜索关键词
  - `type` (String?): 搜索类型（可选）
  - `cursor` (String?): 分页游标（可选）
  - `limit` (Int?): 返回数量限制（可选）
- **返回**: `SearchData` - 搜索结果和分页信息

**示例**:

```kotlin
val results = sdk.search.search("fox", type = "user", limit = 20)
println("搜索结果：${results.results.size}")
```

### getSearchSuggestions(query)

**获取搜索建议** - 根据关键词获取搜索建议（自动补全）

- **端点**: `GET /api/proxy/furtv/search/suggestions`
- **方法**: `suspend fun`
- **参数**: `query` (String) - 搜索关键词
- **返回**: `List<String>` - 搜索建议列表

**示例**:

```kotlin
val suggestions = sdk.search.getSearchSuggestions("fox")
suggestions.forEach { suggestion ->
    println("- $suggestion")
}
```

### searchBySpecies(species)

**按物种搜索** - 根据物种名称搜索用户

- **端点**: `GET /api/proxy/furtv/search/species/:species`
- **方法**: `suspend fun`
- **参数**: `species` (String) - 物种名称
- **返回**: `SpeciesSearchData` - 搜索结果

**示例**:

```kotlin
val speciesResults = sdk.search.searchBySpecies("Red Fox")
println("搜索结果：${speciesResults.users.size}")
```

### getSpeciesList()

**获取物种列表** - 获取所有物种及相关统计信息

- **端点**: `GET /api/proxy/furtv/species`
- **方法**: `suspend fun`
- **参数**: 无
- **返回**: `SpeciesListData` - 物种列表和统计

**示例**:

```kotlin
val speciesList = sdk.search.getSpeciesList()
println("物种总数：${speciesList.species.size}")
```

### getPopularLocations()

**获取热门地区** - 获取用户数量最多的地区列表

- **端点**: `GET /api/proxy/furtv/locations/popular`
- **方法**: `suspend fun`
- **参数**: 无
- **返回**: `PopularLocationsData` - 热门地区列表

**示例**:

```kotlin
val locations = sdk.search.getPopularLocations()
locations.locations.forEach { location ->
    println("- ${location.name}: ${location.count} 用户")
}
```

## Discovery 系列 API

### getPopularDiscovery()

**获取热门推荐（Discovery）**

- **端点**: `GET /api/proxy/furtv/discovery/popular`
- **返回**: `DiscoveryPopularData` - 包含热门用户列表

### getRandomDiscovery(count, personalized)

**获取随机推荐（Discovery）**

- **端点**: `GET /api/proxy/furtv/discovery/random`
- **参数**:
  - `count` (Int?): 返回数量
  - `personalized` (Boolean?): 是否个性化推荐
- **返回**: `List<DiscoveryRandomUser>` - 随机推荐的用户列表

### searchDiscovery(query, page, pageSize, type)

**搜索（Discovery）**

- **端点**: `GET /api/proxy/furtv/discovery/search`
- **参数**:
  - `query` (String): 搜索关键词
  - `page` (Int?): 页码
  - `pageSize` (Int?): 每页数量
  - `type` (String?): 搜索类型
- **返回**: `DiscoverySearchData` - 搜索结果和分页信息

### searchBySpeciesDiscovery(speciesId, page, pageSize)

**按物种搜索（Discovery）**

- **端点**: `GET /api/proxy/furtv/discovery/species/search`
- **参数**:
  - `speciesId` (String): 物种 ID
  - `page` (Int?): 页码
  - `pageSize` (Int?): 每页数量
- **返回**: `DiscoverySpeciesSearchData` - 搜索结果和分页信息

## 使用场景

### 1. 获取热门推荐

```kotlin
val popular = sdk.search.getPopular()
println("=== 热门用户 ===")
popular.users.take(5).forEach { user ->
    println("- ${user.displayName} (@${user.username})")
}
```

### 2. 随机发现用户

```kotlin
// 获取 10 个个性化推荐用户
val randomUsers = sdk.search.getRandomFursuit(count = 10, personalized = true)
println("为你推荐的用戶：")
randomUsers.forEach { user ->
    println("- ${user.displayName}")
}
```

### 3. 搜索用户

```kotlin
// 搜索包含 "fox" 的用户
val results = sdk.search.search("fox", type = "user", limit = 20)
println("找到 ${results.results.size} 个用户")
results.results.forEach { user ->
    println("- ${user.displayName}")
}
```

### 4. 获取搜索建议

```kotlin
// 获取自动补全建议
val suggestions = sdk.search.getSearchSuggestions("fox")
println("搜索建议：")
suggestions.forEach { suggestion ->
    println("- $suggestion")
}
```

### 5. 按物种查找用户

```kotlin
// 获取物种列表
val speciesList = sdk.search.getSpeciesList()
println("物种列表：")
speciesList.species.forEach { species ->
    println("- ${species.name}: ${species.count} 用户")
}

// 按物种搜索
val speciesResults = sdk.search.searchBySpecies("Red Fox")
println("Red Fox 用户：${speciesResults.users.size}")
```

## 相关文档

- [热门推荐](../../vds-docs/Fursuit.TV 兽频道/发现与搜索 - 推荐能力/热门推荐（furtv.discovery.popular）.md)
- [随机推荐](../../vds-docs/Fursuit.TV 兽频道/发现与搜索 - 推荐能力/随机推荐（furtv.discovery.random）.md)
- [搜索](../../vds-docs/Fursuit.TV 兽频道/发现与搜索 - 检索能力/搜索（furtv.discovery.search）.md)
- [物种列表](../../vds-docs/Fursuit.TV 兽频道/发现与搜索 - 检索能力/物种列表（furtv.discovery.species）.md)
