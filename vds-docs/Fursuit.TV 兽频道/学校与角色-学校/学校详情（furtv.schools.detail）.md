
# 学校详情（furtv.schools.detail）

获取学校详情。

认证与端点统一说明请参考根目录文档：`认证方式与服务器端点`。

## 请求地址

- `GET /api/proxy/furtv/schools/:schoolId`
- 权限节点：`furtv.schools.detail`

## 路径参数

- `schoolId`：学校 ID

## 成功响应示例

```json
{
  "school": {
    "id": 12,
    "name": "南京示例大学",
    "short_name": "示大",
    "location": "江苏 南京",
    "type": "university",
    "logo_url": "https://example.com/school-12.png",
    "student_count": 120
  },
  "requestId": "7ddaf837-af6e-4f78-b7fd-4e397314a876"
}
```
