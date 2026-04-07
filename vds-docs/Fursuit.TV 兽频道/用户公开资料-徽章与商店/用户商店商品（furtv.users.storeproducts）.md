
# 用户商店商品（furtv.users.storeproducts）

获取用户已上架商品列表。

认证与端点统一说明请参考根目录文档：`认证方式与服务器端点`。

## 请求地址

- `GET /api/proxy/furtv/users/:username/store-products`
- 权限节点：`furtv.users.storeproducts`

## 路径参数

- `username`：用户名（也支持传数字 ID）

## 成功响应示例

```json
{
  "success": true,
  "user": {
    "id": 18,
    "username": "fox_demo",
    "nickname": "狐狐"
  },
  "is_owner": false,
  "is_merchant_verified": true,
  "products": [
    {
      "id": 5001,
      "name": "尾巴挂件",
      "price": "129.00",
      "image_url": "https://example.com/products/5001.jpg",
      "external_url": "https://item.taobao.com/item.htm?id=123456",
      "sort_order": 1,
      "created_at": "2026-03-01T03:00:00.000Z",
      "updated_at": "2026-03-10T05:20:00.000Z"
    }
  ],
  "requestId": "34fc36fa-c314-44ed-ae73-fd1721c10e04"
}
```
