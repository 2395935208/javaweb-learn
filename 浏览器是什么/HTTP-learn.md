# HTTP是什么
是客户端和服务器之间传输数据的一个协议
请求-响应模型，无状态，基于TCP/IP
## 报文结构
- 一个http请求大致是：
GET /api/user?id=1 HTTP/1.1
Host: example.com
Content-Type: application/json
Authorization: Bearer xxx

（请求体，GET一般没有，POST常有）
- 响应：
HTTP/1.1 200 OK
Content-Type: application/json

{"name": "张三"}
## 常见方法
- GET：获取资源
- POST：创建资源 / 提交数据
- PUT/PATCH：更新资源
- DELETE：删除资源
## 状态码
- 2xx：成功（200 OK、201 Created）
- 3xx：重定向（301、302）
- 4xx：客户端错误（400 参数错误、401 未认证、403 无权限、404 找不到）
- 5xx：服务端错误（500 内部错误、502 网关错误）
