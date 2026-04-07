
# 用户学校信息（furtv.schools.user）

获取指定用户公开学校信息列表。

认证与端点统一说明请参考根目录文档：`认证方式与服务器端点`。

## 请求地址

- `GET /api/proxy/furtv/schools/user/:userId`
- 权限节点：`furtv.schools.user`

## 路径参数

- `userId`：用户 ID

## 成功响应示例

```json
{
  "schools": [
    {
      "user_school_id": 99,
      "class_name": "2023级",
      "enrollment_year": 2023,
      "graduation_year": 2027,
      "is_current": 1,
      "is_public": 1,
      "school_id": 12,
      "school_name": "南京示例大学",
      "short_name": "示大",
      "location": "江苏 南京",
      "type": "university",
      "logo_url": "https://example.com/school-12.png",
      "student_count": 120
    }
  ],
  "requestId": "9f575f62-de06-4fdf-8751-a4051d643f53"
}
```
