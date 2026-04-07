
# 学校搜索（furtv.schools.search）

按名称搜索学校。

认证与端点统一说明请参考根目录文档：`认证方式与服务器端点`。

## 请求地址

- `GET /api/proxy/furtv/schools/search`
- 权限节点：`furtv.schools.search`

## 查询参数

- `query`：必填，搜索关键词

## 成功响应示例

```json
{
  "schools": [
    {
      "id": 12,
      "name": "南京示例大学",
      "short_name": "示大",
      "location": "江苏 南京",
      "type": "university",
      "logo_url": "https://example.com/school-12.png",
      "student_count": 120
    }
  ],
  "requestId": "4fa6f58f-70bd-4a35-b852-ea79f4542391"
}
```
