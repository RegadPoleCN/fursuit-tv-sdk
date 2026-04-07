
# 搜索建议（furtv.discovery.search.suggestions）

获取自动补全建议。

认证与端点统一说明请参考根目录文档：`认证方式与服务器端点`。

## 请求地址

- `GET /api/proxy/furtv/search/suggestions`
- 权限节点：`furtv.discovery.search.suggestions`

## 查询参数

- `q`：必填，至少 2 个字符

## 成功响应示例

```json
{
  "success": true,
  "suggestions": ["fox_demo", "狐狐", "foxmaker"],
  "requestId": "011f90f4-95a4-44c5-9bb5-5f9fd7e492be"
}
```
