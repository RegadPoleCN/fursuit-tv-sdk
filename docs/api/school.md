# 学校 API

学校 API 包含与学校和角色相关的接口，如学校详情、学校搜索、用户学校信息等。

## 方法列表

### `getSchoolDetail(schoolId: String)`

获取学校详情。

**参数**:
- `schoolId`: 学校 ID

**返回类型**: `SchoolDetail`

**示例**:

```kotlin
val schoolDetail = sdk.school.getSchoolDetail("school-id")
println("学校名称: ${schoolDetail.name}")
println("学校 ID: ${schoolDetail.id}")
println("学校 Logo: ${schoolDetail.logoUrl}")
println("学校描述: ${schoolDetail.description}")
println("学校位置: ${schoolDetail.location}")
println("学生数量: ${schoolDetail.studentCount}")
println("创建时间: ${schoolDetail.createdAt}")
```

### `searchSchools(params: SchoolSearchParams)`

搜索学校。

**参数**:
- `params`: 学校搜索参数
  - `query`: 搜索关键词
  - `cursor`: 分页游标（可选）
  - `limit`: 返回数量限制（可选）

**返回类型**: `SchoolSearchData`

**示例**:

```kotlin
val params = SchoolSearchParams(
    query = "北京大学",
    limit = 10
)
val searchResult = sdk.school.searchSchools(params)

println("搜索结果数量: ${searchResult.schools.size}")
println("总数量: ${searchResult.totalCount}")

searchResult.schools.forEach { school ->
    println("学校名称: ${school.name}")
    println("学校 ID: ${school.id}")
    println("学校位置: ${school.location}")
}
```

### `searchSchools(query: String, cursor: String? = null, limit: Int? = null)`

搜索学校（重载方法）。

**参数**:
- `query`: 搜索关键词
- `cursor`: 分页游标（可选）
- `limit`: 返回数量限制（可选）

**返回类型**: `SchoolSearchData`

**示例**:

```kotlin
val searchResult = sdk.school.searchSchools("北京大学", limit = 10)
println("搜索结果数量: ${searchResult.schools.size}")
```

### `getUserSchool(userId: String)`

获取用户学校信息。

**参数**:
- `userId`: 用户 ID

**返回类型**: `UserSchoolData`

**示例**:

```kotlin
val userSchool = sdk.school.getUserSchool("user-id")
println("用户 ID: ${userSchool.userId}")

if (userSchool.school != null) {
    println("学校名称: ${userSchool.school.name}")
    println("班级: ${userSchool.className}")
    println("毕业年份: ${userSchool.graduationYear}")
    println("入学年份: ${userSchool.enrollmentYear}")
} else {
    println("用户未关联学校")
}
```

### `getUserCharacters(username: String)`

获取用户角色列表。

**参数**:
- `username`: 用户名

**返回类型**: `UserCharactersData`

**示例**:

```kotlin
val characters = sdk.school.getUserCharacters("username")
println("用户名: ${characters.username}")
println("角色数量: ${characters.characters.size}")

characters.characters.forEach { character ->
    println("角色名称: ${character.name}")
    println("角色 ID: ${character.id}")
    println("物种: ${character.species}")
    println("是否主要角色: ${character.isPrimary}")
}
```

## 数据结构

### SchoolDetail

```kotlin
data class SchoolDetail(
    val id: String,                // 学校 ID
    val name: String,              // 学校名称
    val logoUrl: String? = null,    // 学校 Logo URL
    val description: String? = null, // 学校描述
    val location: String? = null,    // 学校位置
    val studentCount: Int,          // 学生数量
    val createdAt: String           // 创建时间
)
```

### SchoolSearchData

```kotlin
data class SchoolSearchData(
    val schools: List<SchoolInfo>, // 学校列表
    val totalCount: Int           // 总数量
)

data class SchoolInfo(
    val id: String,                // 学校 ID
    val name: String,              // 学校名称
    val logoUrl: String? = null,    // 学校 Logo URL
    val location: String? = null    // 学校位置
)
```

### UserSchoolData

```kotlin
data class UserSchoolData(
    val userId: String,            // 用户 ID
    val school: SchoolDetail? = null, // 学校信息
    val className: String? = null,  // 班级
    val graduationYear: Int? = null, // 毕业年份
    val enrollmentYear: Int? = null // 入学年份
)
```

### UserCharactersData

```kotlin
data class UserCharactersData(
    val username: String,              // 用户名
    val characters: List<CharacterInfo> // 角色列表
)

data class CharacterInfo(
    val id: String,                    // 角色 ID
    val name: String,                  // 角色名称
    val species: String? = null,       // 物种
    val avatarUrl: String? = null,     // 头像 URL
    val description: String? = null,   // 描述
    val isPrimary: Boolean = false     // 是否主要角色
)
```
