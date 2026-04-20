# 学校 API (School)

学校模块提供学校搜索、详情、用户学校信息、角色列表等功能。

## API 方法

### searchSchools(query, cursor, limit)

搜索学校

- **端点**: `GET /api/proxy/furtv/schools/search`
- **参数**:
  - `query` (String): 关键词
  - `cursor` (String?, 可选): 分页游标
  - `limit` (Int?, 可选): 数量限制
- **返回**: `SchoolSearchData`（schools[], total）

### getSchoolDetail(schoolId)

获取学校详情

- **端点**: `GET /api/proxy/furtv/schools/:schoolId`
- **参数**: `schoolId` (String) - 学校 ID
- **返回**: `SchoolDetail`（schoolId, name, description, location, established, website, logoUrl）

### getUserSchools(userId)

获取用户学校信息

- **端点**: `GET /api/proxy/furtv/schools/user/:userId`
- **参数**: `userId` (String) - 用户 ID
- **返回**: `UserSchoolsData`（userId, username, schools[]）

### getUserCharacters(username)

获取用户角色列表

- **端点**: `GET /api/proxy/furtv/characters/user/:username`
- **参数**: `username` (String) - 用户名
- **返回**: `UserCharactersData`（userId, username, characters[]）

## 数据模型

### SchoolDetail

| 字段 | 类型 | 说明 |
|------|------|------|
| schoolId | String | 学校 ID |
| name | String | 名称 |
| description | String? | 描述 |
| location | String? | 位置 |
| established | Int? | 成立时间 |
| website | String? | 网站 |
| logoUrl | String? | Logo |

### CharacterInfo

| 字段 | 类型 | 说明 |
|------|------|------|
| characterId | String | 角色 ID |
| name | String | 名称 |
| species | String? | 物种 |
| description | String? | 描述 |
| imageUrl | String? | 图片 |

## 相关文档

- [错误处理](../error-handling.md)
