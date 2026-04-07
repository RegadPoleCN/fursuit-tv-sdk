
# 用户角色列表（furtv.characters.user）

获取指定用户的角色（崽崽）列表。

认证与端点统一说明请参考根目录文档：`认证方式与服务器端点`。

## 请求地址

- `GET /api/proxy/furtv/characters/user/:username`
- 权限节点：`furtv.characters.user`

## 路径参数

- `username`：用户名

## 成功响应示例

```json
{
  "success": true,
  "characters": [
    {
      "id": "char_001",
      "name": "小狐",
      "species": "狐",
      "gender": "unknown",
      "worldview": "现代"
    }
  ],
  "requestId": "9251b052-b8da-42f2-a1af-6be203bc95ab"
}
```
