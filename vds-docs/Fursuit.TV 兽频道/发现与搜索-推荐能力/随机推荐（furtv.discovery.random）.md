
# 随机推荐（furtv.discovery.random）

获取个性化/随机推荐档案。

认证与端点统一说明请参考根目录文档：`认证方式与服务器端点`。

## 请求地址

- `GET /api/proxy/furtv/fursuit/random`
- 权限节点：`furtv.discovery.random`

## 查询参数

- `count`：可选，返回数量，范围 `1-20`，默认 `1`

## 成功响应示例（count=1）

```json
{
  "success": true,
  "fursuit": {
    "id": 1024,
    "username": "fox_demo",
    "nickname": "狐狐",
    "avatar_url": "https://example.com/avatar.jpg",
    "fursuit_species": "狐",
    "fursuit_maker": "Maker A",
    "location": "江苏 南京",
    "destination": null,
    "introduction": "嗷呜~",
    "view_count": 1824,
    "is_verified": true
  },
  "debug_info": {
    "is_personalized": true,
    "cache_hit_count": 1,
    "response_ms": 42
  },
  "requestId": "d917ec82-df76-43cb-a047-f79f7f8ea6fd"
}
```

## 成功响应示例（count>1）

```json
{
  "success": true,
  "fursuits": [
    {
      "id": 1024,
      "username": "fox_demo",
      "nickname": "狐狐"
    },
    {
      "id": 2048,
      "username": "wolf_demo",
      "nickname": "狼狼"
    }
  ],
  "count": 2,
  "requested_count": 2,
  "debug_info": {
    "is_personalized": true,
    "cache_hit_count": 1,
    "response_ms": 57
  },
  "requestId": "e96d7074-3b53-4ecc-96d8-f8a43060e4f1"
}
```
