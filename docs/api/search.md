# 搜索 API (Search)

搜索模块提供热门推荐、随机推荐、搜索、物种查询等发现功能。

## API 方法

### getPopular(limit)

获取热门推荐

- **端点**: `GET /api/proxy/furtv/popular`
- **参数**:
  - `limit` (Int?, 可选): 返回数量
- **返回**: `PopularData`（users[]）

### getRandomFursuit(count, personalized)

获取随机推荐

- **端点**: `GET /api/proxy/furtv/fursuit/random`
- **参数**:
  - `count` (Int?, 可选): 返回数量
  - `personalized` (Boolean?, 可选): 个性化推荐
- **返回**: `RandomFursuitResponse`

### search(query, type, cursor, limit)

搜索

- **端点**: `GET /api/proxy/furtv/search`
- **参数**:
  - `query` (String): 关键词
  - `type` (String?, 可选): 类型
  - `cursor` (String?, 可选): 分页游标
  - `limit` (Int?, 可选): 数量限制
- **返回**: `SearchData`

### getSearchSuggestions(query)

获取搜索建议

- **端点**: `GET /api/proxy/furtv/search/suggestions`
- **参数**: `query` (String) - 关键词
- **返回**: `SearchSuggestionsData`

### searchBySpecies(species, page, limit, cursor)

按物种搜索

- **端点**: `GET /api/proxy/furtv/search/species/:species`
- **参数**:
  - `species` (String): 物种名称
  - `page` (Int?, 可选): 页码
  - `limit` (Int?, 可选): 数量限制
  - `cursor` (String?, 可选): 分页游标
- **返回**: `SpeciesSearchData`

### getSpeciesList()

获取物种列表

- **端点**: `GET /api/proxy/furtv/species`
- **返回**: `SpeciesListData`

### getPopularLocations()

获取热门地区

- **端点**: `GET /api/proxy/furtv/locations/popular`
- **返回**: `PopularLocationsData`

## 数据模型

### PopularData

| 字段 | 类型 | 说明 |
|------|------|------|
| users | List\<PopularUser\> | 热门用户列表 |

### PopularUser

| 字段 | 类型 | 说明 |
|------|------|------|
| id | Int | 用户 ID |
| username | String | 用户名 |
| nickname | String? | 昵称 |
| avatarUrl | String? | 头像 URL |
| fursuitSpecies | String? | 兽装物种 |
| fursuitMaker | String? | 兽装制作者 |
| showcasePortrait | String? | 展示竖图 URL |
| introduction | String? | 自我介绍 |
| viewCount | Int? | 浏览量 |
| isVerified | Boolean? | 是否已认证 |
| likeCount | Int? | 点赞数 |
| createdAt | String? | 注册时间 |
| destination | String? | 当前目的地 |
| destinationExpiresAt | String? | 目的地过期时间 |
| popularityScore | Int? | 热度分数 |

### RandomFursuitResponse

| 字段 | 类型 | 说明 |
|------|------|------|
| success | Boolean | 请求是否成功 |
| fursuit | RandomFursuit? | 单个随机推荐（count=1 时） |
| fursuits | List\<RandomFursuit\>? | 多个随机推荐列表 |
| count | Int? | 实际返回数量 |
| requestedCount | Int? | 请求的数量 |
| debugInfo | RandomDebugInfo? | 调试信息 |
| requestId | String | 请求 ID |

### RandomFursuit

| 字段 | 类型 | 说明 |
|------|------|------|
| id | Int | 用户 ID |
| username | String | 用户名 |
| nickname | String? | 昵称 |
| avatarUrl | String? | 头像 URL |
| fursuitSpecies | String? | 兽装物种 |
| fursuitMaker | String? | 兽装制作者 |
| location | String? | 地理位置 |
| destination | String? | 当前目的地 |
| introduction | String? | 自我介绍 |
| viewCount | Int? | 浏览量 |
| isVerified | Boolean? | 是否已认证 |

### RandomDebugInfo

| 字段 | 类型 | 说明 |
|------|------|------|
| isPersonalized | Boolean? | 是否个性化推荐 |
| cacheHitCount | Int? | 缓存命中数 |
| responseMs | Int? | 响应耗时（毫秒） |

### SearchData

| 字段 | 类型 | 说明 |
|------|------|------|
| users | List\<SearchUser\> | 搜索结果用户列表 |
| searchType | String? | 搜索类型 |
| searchKeywords | List\<String\>? | 搜索关键词列表 |
| pagination | SearchPagination? | 分页信息 |
| hasMore | Boolean | 是否有更多结果 |
| total | Int? | 总数 |
| nextCursor | String? | 下一页游标 |

### SearchUser

| 字段 | 类型 | 说明 |
|------|------|------|
| id | Int? | 用户 ID |
| username | String? | 用户名 |
| nickname | String? | 昵称 |
| avatarUrl | String? | 头像 URL |
| showcasePortrait | String? | 展示竖图 URL |
| fursuitSpecies | String? | 兽装物种 |
| fursuitMaker | String? | 兽装制作者 |
| location | String? | 地理位置 |
| destinations | List\<String\>? | 目的地列表 |
| destination | String? | 当前目的地 |
| destinationExpiresAt | String? | 目的地过期时间 |
| introduction | String? | 自我介绍 |
| viewCount | Int? | 浏览量 |
| isVerified | Boolean? | 是否已认证 |
| createdAt | String? | 注册时间 |

### SearchPagination

| 字段 | 类型 | 说明 |
|------|------|------|
| page | Int? | 当前页码 |
| limit | Int? | 每页数量 |
| total | Int? | 总数 |
| totalPages | Int? | 总页数 |
| nextCursor | String? | 下一页游标 |

### SearchSuggestionsData

| 字段 | 类型 | 说明 |
|------|------|------|
| suggestions | List\<String\> | 建议列表 |

### SpeciesSearchData

| 字段 | 类型 | 说明 |
|------|------|------|
| species | String | 搜索的物种名称 |
| users | List\<SpeciesSearchUser\> | 搜索结果用户列表 |
| pagination | SearchPagination? | 分页信息 |
| hasMore | Boolean | 是否有更多结果 |
| total | Int? | 总数 |
| nextCursor | String? | 下一页游标 |

### SpeciesSearchUser

| 字段 | 类型 | 说明 |
|------|------|------|
| id | Int? | 用户 ID |
| username | String? | 用户名 |
| nickname | String? | 昵称 |
| avatarUrl | String? | 头像 URL |
| showcasePortrait | String? | 展示竖图 URL |
| fursuitSpecies | String? | 兽装物种 |
| fursuitMaker | String? | 兽装制作者 |
| introduction | String? | 自我介绍 |
| viewCount | Int? | 浏览量 |
| isVerified | Boolean? | 是否已认证 |
| createdAt | String? | 注册时间 |

### SpeciesListData

| 字段 | 类型 | 说明 |
|------|------|------|
| species | List\<SpeciesInfo\> | 物种列表 |
| total | Int | 物种总数 |

### SpeciesInfo

| 字段 | 类型 | 说明 |
|------|------|------|
| species | String | 物种名称 |
| count | Int | 用户数量 |

### PopularLocationsData

| 字段 | 类型 | 说明 |
|------|------|------|
| popularProvinces | List\<ProvinceInfo\> | 热门省份列表 |
| popularCities | List\<CityInfo\> | 热门城市列表 |
| totalUsers | Int? | 用户总数 |

### ProvinceInfo

| 字段 | 类型 | 说明 |
|------|------|------|
| province | String | 省份名称 |
| count | Int | 用户数量 |

### CityInfo

| 字段 | 类型 | 说明 |
|------|------|------|
| province | String | 所属省份 |
| city | String | 城市名称 |
| count | Int | 用户数量 |

## 相关文档

- [错误处理](../error-handling.md)
