# 搜索 API

搜索 API 包含与搜索和发现相关的接口，如热门推荐、随机推荐、搜索等。

## 方法列表

### `getPopular()`

获取热门推荐。

**返回类型**: `PopularData`

**示例**:

```kotlin
val popular = sdk.search.getPopular()
println("热门用户数量: ${popular.users.size}")

popular.users.forEach { user ->
    println("用户名: ${user.username}")
    println("显示名称: ${user.displayName}")
    println("热度: ${user.popularity}")
}
```

### `getRandomFursuit(params: RandomFursuitParams)`

获取随机推荐。

**参数**:
- `params`: 随机推荐参数
  - `count`: 返回数量（可选）
  - `personalized`: 是否个性化推荐（可选）

**返回类型**: `List<RandomFursuit>`

**示例**:

```kotlin
val params = RandomFursuitParams(
    count = 5,
    personalized = true
)
val randomFursuits = sdk.search.getRandomFursuit(params)

randomFursuits.forEach { fursuit ->
    println("用户名: ${fursuit.username}")
    println("显示名称: ${fursuit.displayName}")
    println("物种: ${fursuit.species}")
}
```

### `getRandomFursuit(count: Int? = null, personalized: Boolean? = null)`

获取随机推荐（重载方法）。

**参数**:
- `count`: 返回数量（可选）
- `personalized`: 是否个性化推荐（可选）

**返回类型**: `List<RandomFursuit>`

**示例**:

```kotlin
val randomFursuits = sdk.search.getRandomFursuit(5, true)

randomFursuits.forEach { fursuit ->
    println("用户名: ${fursuit.username}")
    println("显示名称: ${fursuit.displayName}")
}
```

### `search(params: SearchParams)`

搜索。

**参数**:
- `params`: 搜索参数
  - `query`: 搜索关键词
  - `type`: 搜索类型（可选）
  - `cursor`: 分页游标（可选）
  - `limit`: 返回数量限制（可选）

**返回类型**: `SearchData`

**示例**:

```kotlin
val params = SearchParams(
    query = "fox",
    type = "user",
    limit = 10
)
val searchResult = sdk.search.search(params)

println("搜索结果数量: ${searchResult.results.size}")
println("是否有更多: ${searchResult.hasMore}")

searchResult.results.forEach { result ->
    println("类型: ${result.type}")
    println("用户名: ${result.username}")
    println("显示名称: ${result.displayName}")
}
```

### `search(query: String, type: String? = null, cursor: String? = null, limit: Int? = null)`

搜索（重载方法）。

**参数**:
- `query`: 搜索关键词
- `type`: 搜索类型（可选）
- `cursor`: 分页游标（可选）
- `limit`: 返回数量限制（可选）

**返回类型**: `SearchData`

**示例**:

```kotlin
val searchResult = sdk.search.search("fox", "user", limit = 10)

println("搜索结果数量: ${searchResult.results.size}")
```

### `getSearchSuggestions(query: String)`

获取搜索建议。

**参数**:
- `query`: 搜索关键词

**返回类型**: `List<String>`

**示例**:

```kotlin
val suggestions = sdk.search.getSearchSuggestions("fo")
println("搜索建议数量: ${suggestions.size}")
suggestions.forEach { suggestion ->
    println("建议: $suggestion")
}
```

### `searchBySpecies(species: String)`

按物种搜索。

**参数**:
- `species`: 物种名称

**返回类型**: `SpeciesSearchData`

**示例**:

```kotlin
val speciesResult = sdk.search.searchBySpecies("fox")
println("物种: ${speciesResult.species}")
println("用户数量: ${speciesResult.totalCount}")

speciesResult.users.forEach { user ->
    println("用户名: ${user.username}")
    println("显示名称: ${user.displayName}")
}
```

### `getSpeciesList()`

获取物种列表。

**返回类型**: `SpeciesListData`

**示例**:

```kotlin
val speciesList = sdk.search.getSpeciesList()
println("物种数量: ${speciesList.totalCount}")

speciesList.species.forEach { species ->
    println("物种: ${species.name}")
    println("数量: ${species.count}")
}
```

### `getPopularLocations()`

获取热门地区。

**返回类型**: `PopularLocationsData`

**示例**:

```kotlin
val locations = sdk.search.getPopularLocations()
println("热门地区数量: ${locations.locations.size}")

locations.locations.forEach { location ->
    println("省份: ${location.province}")
    println("城市: ${location.city ?: "全国"}")
    println("数量: ${location.count}")
}
```

## 数据结构

### PopularData

```kotlin
data class PopularData(
    val users: List<PopularUser> // 热门用户列表
)

data class PopularUser(
    val username: String,        // 用户名
    val displayName: String,     // 显示名称
    val avatarUrl: String? = null, // 头像 URL
    val popularity: Int          // 热度
)
```

### RandomFursuit

```kotlin
data class RandomFursuit(
    val username: String,        // 用户名
    val displayName: String,     // 显示名称
    val avatarUrl: String? = null, // 头像 URL
    val species: String? = null  // 物种
)
```

### SearchData

```kotlin
data class SearchData(
    val results: List<SearchResult>, // 搜索结果列表
    val nextCursor: String? = null,  // 下一页游标
    val hasMore: Boolean             // 是否有更多
)

data class SearchResult(
    val type: String,              // 类型
    val username: String? = null,  // 用户名
    val displayName: String? = null, // 显示名称
    val avatarUrl: String? = null, // 头像 URL
    val description: String? = null // 描述
)
```

### SpeciesSearchData

```kotlin
data class SpeciesSearchData(
    val species: String,         // 物种名称
    val users: List<SpeciesUser>, // 用户列表
    val totalCount: Int          // 总数量
)

data class SpeciesUser(
    val username: String,        // 用户名
    val displayName: String,     // 显示名称
    val avatarUrl: String? = null // 头像 URL
)
```

### SpeciesListData

```kotlin
data class SpeciesListData(
    val species: List<SpeciesInfo>, // 物种列表
    val totalCount: Int            // 总数量
)

data class SpeciesInfo(
    val name: String,             // 物种名称
    val count: Int                // 数量
)
```

### PopularLocationsData

```kotlin
data class PopularLocationsData(
    val locations: List<LocationInfo> // 地区列表
)

data class LocationInfo(
    val province: String,        // 省份
    val city: String? = null,    // 城市
    val count: Int               // 数量
)
```
