# MyBatis是什么
MyBatis是用来连接起来java和MySQL的框架
他的底层仍然使用了JDBC，只不过封装了JDBC的操作，使得使用更加方便
## 1.一次完整的查询
请求进入：
浏览器 → Tomcat → Spring MVC → Controller → Service → Mapper → MyBatis → JDBC → MySQL

结果返回：
MySQL → JDBC → MyBatis → User → Service → Controller → JSON → Tomcat → 浏览器
完整流程：
1. 客户端发送 GET /users/1
2. Tomcat接收HTTP请求
3. DispatcherServlet寻找对应Controller
4. Spring MVC从路径提取id并转换为Long
5. Controller调用Service
6. Service调用Mapper
7. MyBatis Mapper代理找到@Select
8. MyBatis绑定id参数
9. MyBatis通过JDBC和MySQL驱动发送SQL
10. MySQL执行查询并返回记录
11. MyBatis把记录映射成User
12. User逐层返回Controller
13. Jackson把User转换成JSON
14. Tomcat把HTTP响应发回客户端
## 2.Mapper为什么没有实现类也能运行
UserMapper只是接口，selectById()也没有方法体，谁在真正执行这段代码？
答案是：
MyBatis在程序运行时创建了一个代理对象，由代理对象接收方法调用并执行SQL。
### 3.普通接口不能直接用
以为普通接口不能直接创建对象，一般需要写实现类
而MyBatis代替你生成了类似的实现对象
## 4.创建代理对象
MyBatis不会真的在项目目录中生成一个：
UserMapperImpl.java
它是在程序运行期间，动态创建一个对象。
这个对象：
实现了 UserMapper 接口
可以作为 UserMapper 使用
能拦截 selectById() 调用
知道怎样找到并执行对应SQL
这种对象叫：
动态代理对象。

“动态”表示它不是提前写在 .java 文件中，而是程序运行时创建的。
“代理”表示你调用它时，它会替你处理真正的工作。

Mapper接口：声明需要什么数据库操作
MyBatis代理：真正处理方法调用
## 5.@Mapper有什么作用
它的核心作用是告诉MyBatis和Spring：
这是一个Mapper接口，请为它创建代理对象，并交给Spring容器管理。
Spring Boot启动时大致发生：
扫描项目中的类和接口
        ↓
发现UserMapper上有@Mapper
        ↓
MyBatis为UserMapper创建代理对象
        ↓
代理对象放入Spring容器
        ↓
UserService需要UserMapper
        ↓
Spring把代理对象注入UserService
## 6.代理怎样找到SQL
- 第一种：注解方式
```java
@Mapper
public interface UserMapper {


    @Select(
      "SELECT id,username,age FROM user WHERE id=#{id}"
    )
    User selectById(Long id);


}
```
这里：
selectById方法
        |
        |
        ↓
@Select里面的SQL

MyBatis启动时扫描 Mapper：
发现：
User selectById()
上面有：
@Select(...)
于是记录：
方法：
UserMapper.selectById

对应SQL：
SELECT id,username,age FROM user WHERE id=#{id}

以后调用：
userMapper.selectById(1);

MyBatis：
找到方法
    ↓
发现绑定了@Select
    ↓
执行SQL
- 第二种：XML方式
企业项目更常见。
接口：
```java
@Mapper
public interface UserMapper {

    User selectById(Long id);

}
```
注意：
这里没有 SQL。
那么 MyBatis 去哪里找？
去 XML。
比如：
项目结构：
resources
 └── mapper
      └── UserMapper.xml
```XML
<mapper 
namespace="com.example.mapper.UserMapper">


<select id="selectById">

    SELECT *
    FROM user
    WHERE id=#{id}

</select>


</mapper>
```
这里两个东西非常重要：
namespace
namespace="com.example.mapper.UserMapper"
表示：
这个 XML 属于哪个接口。
也就是：
XML
 |
 |
 ↓

UserMapper接口
id
```XML
<select id="selectById">
```
表示：
对应接口里面哪个方法。
所以：
接口：
User selectById(Long id);
对应：
```XML
<select id="selectById">
```
完整对应关系：
Mapper接口

com.example.mapper.UserMapper
             |
             |
             ↓
XML namespace

com.example.mapper.UserMapper


方法

selectById
             |
             |
             ↓

XML id

selectById
## 7.为什么Mapper方法不建议重载
普通Java允许：
```java
User select(Long id);

User select(String username);
```
两个方法名称都是 select，只是参数不同，这叫方法重载。
但MyBatis定位XML语句时，核心标识通常是：
Mapper完整名称 + 方法名
例如：
UserMapper.select
它不会像Java编译器一样，仅凭不同参数方便地区分多个同名SQL映射。
因此Mapper方法应使用明确、不同的名称：
```java
User selectById(Long id);

User selectByUsername(String username);
```
这样代码表达更清楚，也避免映射冲突。
## 8.@Mapper和@MapperScan
以后还可能看到在启动类上配置：
```java
@MapperScan("com.example.springbootdemo.mapper")
```
它表示：
扫描这个包中的所有Mapper接口。
启动类使用 @MapperScan,不用每个接口都写@Mapper
## 9.删除 @Mapper 会怎样