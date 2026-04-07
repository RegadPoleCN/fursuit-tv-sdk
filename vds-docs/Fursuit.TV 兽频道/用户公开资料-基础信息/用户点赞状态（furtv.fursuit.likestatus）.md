
# 用户点赞状态（furtv.fursuit.likestatus）

查询当前账号对目标用户的点赞状态与可点赞信息。

认证与端点统一说明请参考根目录文档：`认证方式与服务器端点`。

## 请求地址

- `GET /api/proxy/furtv/fursuit/like-status/:username`
- 权限节点：`furtv.fursuit.likestatus`

## 路径参数

- `username`：目标用户名

## 成功响应示例

```json
{
  "success": true,
  "like_count": 233,
  "is_liked": false,
  "can_like": true,
  "days_until_can_like": 0,
  "requestId": "27643bc0-5001-4f5a-ab05-8ef4f752fd35"
}
```
