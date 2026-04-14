# 学校 API (School)

学校模块提供学校信息、角色管理等学校和角色相关功能的访问接口。

## API 方法

### searchSchools(query, cursor, limit)

**搜索学校** - 根据关键词搜索学校，支持分页

- **端点**: `GET /api/proxy/furtv/schools/search`
- **方法**: `suspend fun`
- **参数**:
  - `query` (String): 搜索关键词
  - `cursor` (String?): 分页游标（可选）
  - `limit` (Int?): 返回数量限制（可选）
- **返回**: `SchoolSearchData` - 学校搜索结果
- **响应字段**:
  - `schools`: 学校列表
    - `schoolId`: 学校 ID
    - `name`: 学校名称
    - `location`: 学校位置
    - `established`: 成立时间
  - `total`: 总数

**示例**:

```kotlin
val results = sdk.school.searchSchools("北京大学", limit = 20)
println("找到 ${results.total} 所学校")
results.schools.forEach { school ->
    println("- ${school.name} (${school.location})")
}
```

### getSchoolDetail(schoolId)

**获取学校详情** - 根据学校 ID 获取学校的详细信息

- **端点**: `GET /api/proxy/furtv/schools/detail`
- **方法**: `suspend fun`
- **参数**: `schoolId` (String) - 学校 ID
- **返回**: `SchoolDetail` - 学校详情
- **响应字段**:
  - `schoolId`: 学校 ID
  - `name`: 学校名称
  - `description`: 学校描述
  - `location`: 学校位置
  - `established`: 成立时间
  - `website`: 学校网站
  - `logoUrl`: 学校 Logo URL

**示例**:

```kotlin
val detail = sdk.school.getSchoolDetail("school-id")
println("学校：${detail.name}")
println("描述：${detail.description}")
println("位置：${detail.location}")
```

### getUserSchools(userId)

**获取用户学校信息** - 获取指定用户的学校信息列表

- **端点**: `GET /api/proxy/furtv/schools/user`
- **方法**: `suspend fun`
- **参数**: `userId` (String) - 用户 ID
- **返回**: `UserSchoolsData` - 用户学校信息
- **响应字段**:
  - `userId`: 用户 ID
  - `username`: 用户名
  - `schools`: 学校列表
    - `schoolId`: 学校 ID
    - `schoolName`: 学校名称
    - `enrolledYear`: 入学年份
    - `graduatedYear`: 毕业年份

**示例**:

```kotlin
val userSchools = sdk.school.getUserSchools("user-id")
println("用户 ${userSchools.username} 的学校：")
userSchools.schools.forEach { school ->
    println("- ${school.schoolName} (${school.enrolledYear}-${school.graduatedYear})")
}
```

### getUserCharacters(userId)

**获取用户角色列表** - 获取指定用户的角色列表

- **端点**: `GET /api/proxy/furtv/characters/user`
- **方法**: `suspend fun`
- **参数**: `userId` (String) - 用户 ID
- **返回**: `UserCharactersData` - 用户角色数据
- **响应字段**:
  - `userId`: 用户 ID
  - `username`: 用户名
  - `characters`: 角色列表
    - `characterId`: 角色 ID
    - `name`: 角色名称
    - `species`: 物种
    - `description`: 角色描述
    - `imageUrl`: 角色图片 URL

**示例**:

```kotlin
val characters = sdk.school.getUserCharacters("user-id")
println("用户 ${characters.username} 的角色：")
characters.characters.forEach { character ->
    println("- ${character.name} (${character.species})")
}
```

## 数据模型

### SchoolDetail

```kotlin
public data class SchoolDetail(
    public val schoolId: String,
    public val name: String,
    public val description: String?,
    public val location: String?,
    public val established: Int?,
    public val website: String?,
    public val logoUrl: String?
)
```

### UserCharactersData

```kotlin
public data class UserCharactersData(
    public val userId: String,
    public val username: String,
    public val characters: List<CharacterInfo>
)

public data class CharacterInfo(
    public val characterId: String,
    public val name: String,
    public val species: String?,
    public val description: String?,
    public val imageUrl: String?
)
```

## 使用场景

### 1. 搜索学校

```kotlin
// 搜索包含 "大学" 的学校
val results = sdk.school.searchSchools("大学", limit = 20)
println("找到 ${results.total} 所学校")

results.schools.forEach { school ->
    println("- ${school.name}")
    println("  位置：${school.location}")
}
```

### 2. 查看学校详情

```kotlin
val schoolId = "school-123"
val detail = sdk.school.getSchoolDetail(schoolId)

println("=== ${detail.name} ===")
println("描述：${detail.description}")
println("位置：${detail.location}")
println("成立时间：${detail.established}")
println("网站：${detail.website}")
```

### 3. 查看用户的学校信息

```kotlin
val userId = "user-123"
val userSchools = sdk.school.getUserSchools(userId)

println("=== ${userSchools.username} 的学校 ===")
userSchools.schools.forEach { school ->
    println("- ${school.schoolName}")
    println("  入学：${school.enrolledYear}")
    println("  毕业：${school.graduatedYear}")
}
```

### 4. 查看用户角色列表

```kotlin
val userId = "user-123"
val characters = sdk.school.getUserCharacters(userId)

println("=== ${characters.username} 的角色 ===")
characters.characters.forEach { character ->
    println("- ${character.name}")
    println("  物种：${character.species}")
    println("  描述：${character.description}")
}
```

## 相关文档

- [学校搜索](../../vds-docs/Fursuit.TV 兽频道/学校与角色 - 学校/学校搜索（furtv.schools.search）.md)
- [学校详情](../../vds-docs/Fursuit.TV 兽频道/学校与角色 - 学校/学校详情（furtv.schools.detail）.md)
- [用户学校信息](../../vds-docs/Fursuit.TV 兽频道/学校与角色 - 学校/用户学校信息（furtv.schools.user）.md)
- [用户角色列表](../../vds-docs/Fursuit.TV 兽频道/学校与角色 - 角色/用户角色列表（furtv.characters.user）.md)
