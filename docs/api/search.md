# 搜索 API (Search)

搜索模块提供热门推荐、随机推荐、搜索、物种查询等发现功能。

## API 方法

### getPopular()

获取热门推荐

- **端点**: `GET /api/proxy/furtv/popular`
- **返回**: `PopularData`（users[]）

### getRandomFursuit(count, personalized)

获取随机推荐

- **端点**: `GET /api/proxy/furtv/fursuit/random`
- **参数**:
  - `count` (Int?, 可选): 返回数量
  - `personalized` (Boolean?, 可选): 个性化推荐
- **返回**: `List<RandomFursuit>`

### search(query, type, cursor, limit)

搜索

- **端点**: `GET /api/proxy/furtv/search`
- **参数**:
  - `query` (String): 关键词
  - `type` (String?, 可选): 类型
  - `cursor` (String?, 可选): 分页游标
  - `limit` (Int?, 可选): 数量限制
- **返回**: `SearchData`（results[], cursor）

### getSearchSuggestions(query)

获取搜索建议

- **端点**: `GET /api/proxy/furtv/search/suggestions`
- **参数**: `query` (String) - 关键词
- **返回**: `List<String>` - 建议列表

### searchBySpecies(species)

按物种搜索

- **端点**: `GET /api/proxy/furtv/search/species/:species`
- **参数**: `species` (String) - 物种名称
- **返回**: `SpeciesSearchData`（users[]）

### getSpeciesList()

获取物种列表

- **端点**: `GET /api/proxy/furtv/species`
- **返回**: `SpeciesListData`（species[]）

### getPopularLocations()

获取热门地区

- **端点**: `GET /api/proxy/furtv/locations/popular`
- **返回**: `PopularLocationsData`（locations[]）

## 数据模型

### PopularData

| 字段 | 类型 | 说明 |
|------|------|------|
| users | List | 热门用户列表 |

### SearchData

| 字段 | 类型 | 说明 |
|------|------|------|
| results | List | 搜索结果 |
| cursor | String? | 下一页游标 |

## 相关文档

- [错误处理](../error-handling.md)
