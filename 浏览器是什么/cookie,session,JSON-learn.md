# cookie是什么
浏览器保存的一小段数据
当你第一次登陆这个网站，服务器验证成功后，浏览器会保存这个cookie，下次访问这个网站，浏览器会自动带上这个cookie，服务器看到这个cookie，就会认为你是登陆的
## cookie的特点
- 保存在浏览器
- 自动发送给同一个网站
- 大小一般只有几 KB
- 可以设置过期时间
# session是什么
服务器保存用户信息的一块内存（或数据库）空间。
服务器里保存的是具体用户信息（Session），浏览器只需要保存Cookie：SessionId=A123
# cookie和session的区别
| Cookie   | Session         |
| -------- | --------------- |
| 保存在浏览器   | 保存在服务器          |
| 容量小      | 容量较大            |
| 用户可以删除   | 用户不能直接修改服务器中的内容 |
| 每次请求都会发送 | 根据 SessionID 查找 |
| 保存少量数据   | 保存用户登录状态等信息     |
# JSON是什么
一种数据格式。用于前端和后端交换数据
# 三者的关系
登录页面
      │
      │ JSON
      ▼
服务器验证账号密码
      │
      ▼
创建 Session
(sessionId = ABC123)
      │
      ▼
返回 Cookie
(sessionId=ABC123)
      │
      ▼
浏览器保存 Cookie
      │
      ▼
以后每次请求自动带 Cookie
      │
      ▼
服务器根据 Cookie 找到 Session
      │
      ▼
确认用户身份
学习 Spring Boot 时会发现：JSON 几乎用于所有接口的数据传输；
Cookie 和 Session 是传统登录方案的核心；
而现代项目（如 Spring Boot + Vue）更多会使用 JWT 替代 Session，但理解 Cookie 和 Session 仍然是掌握登录认证机制的基础。