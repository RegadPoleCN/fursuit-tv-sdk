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
- **返回**: `SchoolSearchData`

### getSchoolDetail(schoolId)

获取学校详情

- **端点**: `GET /api/proxy/furtv/schools/:schoolId`
- **参数**: `schoolId` (Int) - 学校 ID
- **返回**: `SchoolDetailResponse`（school）

### getUserSchools(userId)

获取用户学校信息

- **端点**: `GET /api/proxy/furtv/schools/user/:userId`
- **参数**: `userId` (Int) - 用户 ID
- **返回**: `UserSchoolsData`

### getUserCharacters(username)

获取用户角色列表

- **端点**: `GET /api/proxy/furtv/characters/user/:username`
- **参数**: `username` (String) - 用户名
- **返回**: `UserCharactersData`

## 数据模型

### SchoolInfo

| 字段 | 类型 | 说明 |
|------|------|------|
| id | Int | 学校 ID |
| name | String | 学校全称 |
| shortName | String? | 学校简称 |
| location | String? | 学校位置 |
| type | String? | 学校类型 |
| logoUrl | String? | 学校 Logo URL |
| studentCount | Int? | 学生数量 |

### SchoolSearchData

| 字段 | 类型 | 说明 |
|------|------|------|
| schools | List\<SchoolInfo\> | 匹配的学校列表 |

### SchoolDetail

| 字段 | 类型 | 说明 |
|------|------|------|
| id | Int | 学校 ID |
| name | String | 学校全称 |
| shortName | String? | 学校简称 |
| location | String? | 学校位置 |
| type | String? | 学校类型 |
| logoUrl | String? | 学校 Logo URL |
| studentCount | Int? | 学生数量 |

### SchoolDetailResponse

| 字段 | 类型 | 说明 |
|------|------|------|
| success | Boolean | 请求是否成功 |
| school | SchoolDetail | 学校详情 |
| requestId | String | 请求 ID |

### UserSchoolsData

| 字段 | 类型 | 说明 |
|------|------|------|
| schools | List\<UserSchoolInfo\> | 用户关联的学校列表 |

### UserSchoolInfo

| 字段 | 类型 | 说明 |
|------|------|------|
| userSchoolId | Int? | 用户学校关联 ID |
| className | String? | 班级名称 |
| enrollmentYear | Int? | 入学年份 |
| graduationYear | Int? | 毕业年份 |
| isCurrent | Int? | 是否在读（1=在读） |
| isPublic | Int? | 是否公开（1=公开） |
| schoolId | Int | 学校 ID |
| schoolName | String | 学校全称 |
| shortName | String? | 学校简称 |
| location | String? | 学校位置 |
| type | String? | 学校类型 |
| logoUrl | String? | 学校 Logo URL |
| studentCount | Int? | 学生数量 |

### UserCharactersData

| 字段 | 类型 | 说明 |
|------|------|------|
| userId | String | 用户 ID |
| username | String | 用户名 |
| characters | List\<CharacterInfo\> | 角色列表 |
| totalCount | Int | 角色总数 |

### CharacterInfo

| 字段 | 类型 | 说明 |
|------|------|------|
| id | String | 角色 ID |
| name | String | 角色名称 |
| species | String? | 物种 |
| gender | String? | 性别 |
| worldview | String? | 世界观 |

## 相关文档

- [错误处理](../error-handling.md)
