MySQL + SQL（必须扎实）
HTTP + Tomcat + Servlet（理解原理，不必死抠）
Maven
Spring Boot
Spring MVC
MyBatis / MyBatis-Plus
JWT 登录认证
Vue + Axios
TLIAS 项目实战
再去阅读 RuoYi 源码
# mysql数据库
## 什么是数据库？
一般就是指的存放数据的地方
## 常见的术语
- 主键：唯一标识
- 外键：关联两个表
- 索引：快速查询
## mysql用户设置
常见的用户设置操作
- 创建用户

CREATE USER'username'@'host'IDENTIFIED BY'psssword';
其中username：用户名
host：主机名，可以是localhost（仅允许本地连接），也可以是192.168.1.1
- 授权权限
创建用户后需要授予权限，用GRANT命令授予权限
```sql
GRANT privileges ON database_name.* TO 'username'@'host';
```
privileges：所需要的权限
database_name.*:数据库的名字，表示对数据库的所有表授予权限，如果是database_name.table_name,表示的是数据库中的某个表
TO 'username'@'host':表示授予权限的用户和主机
- 刷新权限
```sql
FLUSH PRIVILEGES;
```
- 查看用户权限
```sql
SHOW GRANTS FOR 'username'@'host';
```
- 撤销权限
使用REVOKE命令
```sql
REVOKE privileges ON database_name.* FROM 'username'@'host':
```
- 删除用户
```sql
DROP USER 'username'@'host';
```
- 修改用户密码
使用ALTER USER命令
```sql
ALTER USER 'username'@'host' IDENTIFIED BY 'new_password';
```
- 修改用户主机
可以先删除用户，再创建一个新的用户
```sql
-- 删除旧用户
DROP USER 'john'@'localhost';

-- 重新创建用户并指定新的主机
CREATE USER 'john'@'%' IDENTIFIED BY 'password123';
```
- 创建用户时指定权限
```sql
CREATE USER 'john'@'localhost' IDENTIFIED BY 'password123' WITH GRANT OPTION;
GRANT ALL PRIVILEGES ON test_db.* TO 'john'@'localhost';
```
## /etc/my.cnf 文件配置