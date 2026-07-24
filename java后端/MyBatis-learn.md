# MyBatis是什么
MyBatis是用来连接起来java和MySQL的框架
他的底层仍然使用了JDBC，只不过封装了JDBC的操作，使得使用更加方便
# 一次完整的执行流程
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
# Mapper接口和动态代理
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
# 参数绑定、@Param、#{}
## 9.@Param("id")的作用
```java
User selectById(@Param("id") Long id);
```
传入的java参数是Long id，但SQL中参数是#{id}，那么@Param("id")的作用是：
把传入的Java参数注册到MyBatis中，并给它起名叫 id。
MyBatis内部可以近似理解成保存了：
参数名称：id
参数值：1
如果改成：
```java
User selectById(@Param("userId") Long id);
```
也是可以的，只不过SQL也必须改成：
```java
WHERE id = #{userId}
```
**重点：**
@Param中的名称
        ↕
#{}中的名称
二者的内容必须保持一致，java变量名可以保持不同，但为了可读性通常保持一致
## 10.#{id}不是简单的字符串替换
可以近似理解为：
```java
PreparedStatement statement =
        connection.prepareStatement(
                "SELECT id, username, age FROM `user` WHERE id = ?"
        );

statement.setLong(1, 1L);
```
最终发送给数据库的是：
SQL结构
+
经过类型处理的参数
不是把用户输入直接拼进SQL文本。
## 11。为什么要使用问号占位符
### （1）防止SQL注入
用户输入只会作为一个“值”传递，不会被当成SQL命令的一部分。
### （2）自动处理Java类型
MyBatis会根据Java类型选择合适的JDBC处理方式。
例如：
Long    → BIGINT
String  → VARCHAR
Integer → INTEGER
这部分由MyBatis的TypeHandler协助处理
### （3）自动处理字符串引号
正确写法：
```sql
WHERE username = #{username}
```
错误写法：
```sql
WHERE username = '#{username}'
```
## 12.#{}和${}的区别
#{}：参数绑定
```sql
WHERE username = #{username}
```
经过处理后类似于：
```sql
WHERE username = ?
```
特点：
使用预编译
防止sql注入
自动处理数据类型
自动处理字符串
查询条件几乎都用他

${}：字符串替换
```sql
ORDER BY ${sortField}
```
MyBatis会把内容直接放入SQL文本。
假设：
sortField = age
处理后：
```sql
ORDER BY age
```
特点:
直接修改sql文本
不进行安全的参数绑定
可能发生sql注入
## 13.多个参数怎样传递
例如根据用户名和年龄查询：
```java
User selectByUsernameAndAge(
        @Param("username") String username,
        @Param("age") Integer age
);
```
SQL：
```java
@Select("""
    SELECT id, username, age
    FROM `user`
    WHERE username = #{username}
      AND age = #{age}
    """)
```
调用：
```java
userMapper.selectByUsernameAndAge("zhangsan", 20);
```
## 14.传递对象时怎样取值
新增用户时，Mapper可能写成：
```java
int insert(User user);
```
调用：
```java
User user = new User();
user.setUsername("zhangsan");
user.setAge(20);

userMapper.insert(user);
```
SQL可以写：
```java
@Insert("""
    INSERT INTO `user` (username, age)
    VALUES (#{username}, #{age})
    """)
int insert(User user);
```
传递对象时，#{username}表示读取user.getUsername()
当传入的是对象时，#{}中通常写对象的属性名。
## 15.返回值int表示什么
新增功能：
```java
int insert(User user);
```
返回值int表示受影响的行数。
# 结果映射、User和List<User>
## 16.数据库结果的结构
SQL：
```sql
SELECT id, username, age
FROM `user`;
```
可能返回：
id | username | age
---+----------+----
1  | zhangsan | 20
2  | lisi     | 21
3  | wangwu   | 22
它包含：
3行数据
每行3列
列名分别是 id、username、age
Java中的User：
```java
public class User {

    private Long id;
    private String username;
    private Integer age;

    // Getter、Setter
}
```
MyBatis根据列名和属性名建立对应关系：
数据库列       Java属性
id       →    id
username →    username
age      →    age
每一行数据都可以创建一个User。
## 17.查询一行：返回User
Mapper方法:
```java
User selectById(Long id);
```
返回类型是：
User
MyBatis就认为这个查询最多应该得到一条记录。
例如sql返回：
id | username | age
1  | zhangsan | 20
那么MyBatis会创建一个User对象，并把结果中的值给User的属性。
```java
User user = new User();

user.setId(1L);
user.setUsername("zhangsan");
user.setAge(20);

return user;
```
## 18.查询多行：返回List<User>
查询全部用户的方法应该声明：
```java
List<User> selectAll();
```
SQL:
```sql
SELECT id, username, age
FROM `user`
ORDER BY id;
```
数据库返回：
id | username | age
1  | zhangsan | 20
2  | lisi     | 21
3  | wangwu   | 22
MyBatis会逐行处理：
第一行：
```java
User user1 = new User();
user1.setId(1L);
user1.setUsername("zhangsan");
user1.setAge(20);
```
第二行:
```java
User user2 = new User();
user2.setId(2L);
user2.setUsername("lisi");
user2.setAge(21);
```
第三行：
```java
User user3 = new User();
user3.setId(3L);
user3.setUsername("wangwu");
user3.setAge(22);
```
然后放进集合:
```java
List<User> users = new ArrayList<>();

users.add(user1);
users.add(user2);
users.add(user3);

return users;
```
对应关系则是：
数据库一行   → 一个User
数据库多行   → 多个User
多个User     → List<User>
## 19.没有数据时，集合返回什么
如果方法返回：
```java
List<User> selectAll();
```
但是数据库没有任何用户，MyBatis通常返回：
[]，而不是null
因此如果：
```java
List<User> users = userMapper.selectAll();
```
一般可以判断：
```java
users.isEmpty()
```
而不是先判断：
```java
users == null
```
**对比：**
返回单个User，查询不到 → 通常是null
返回List<User>，查询不到 → 通常是空List
## 20.MyBatis怎样知道返回什么类型
关键是Mapper方法的签名：
```java
User selectById(Long id);
```
MyBatis看到返回类型是User，就按照单个对象处理

而：
```java
List<User> selectAll();
```
MyBatis看到的返回类型是List，集合元素类型是User，就按照集合处理。
## 21.列名与属性名相同：自动映射
当前数据库列：
id
username
age
Java属性：
id
username
age
名称完全一致，所以MyBatis可以自动映射：
id       → setId()
username → setUsername()
age      → setAge()
这也是你现在的查询能够正常工作的原因。
## 22.列名与属性名不同怎么办
假设数据库字段是：
user_name
Java属性是：
private String username;
二者不相同：
user_name ≠ username
MyBatis可能无法自动把它们对应起来，导致：
user.getUsername()
得到：
null
方法一：使用SQL别名
SELECT
    id,
    user_name AS username,
    age
FROM `user`;
返回结果中的列名就变成：
id
username
age
这样可以映射到：
private String username;
SQL别名的含义是：
数据库真正字段：user_name
查询结果使用名称：username
这是最直观的方法。
## 23.下划线转驼峰
更常见的情况是：
数据库字段：user_name
Java属性：userName
数据库喜欢下划线命名：
user_name
created_time
phone_number
Java喜欢驼峰命名：
userName
createdTime
phoneNumber
可以配置MyBatis自动转换：
mybatis:
  configuration:
    map-underscore-to-camel-case: true
开启后：
user_name    → userName
created_time → createdTime
phone_number → phoneNumber
注意，你当前的Java属性是：
username
不是：
userName
因此 user_name → username 不属于标准的下划线转驼峰对应关系，仍然更适合使用别名：
user_name AS username
## 24.为什么使用Long和Integer
包装类型可以表示null：
新增前ID还没有生成
数据库中的某个字段允许为空
SQL没有查询某个字段
包装类型区分：
0    → 确实是数字0
null → 没有值
而基本类型：
long
int
不能表示 null，默认值会是0。
在Entity、DTO等数据对象中，包装类型通常更方便表达数据库中的空值。

## 25.MyBatis映射与Jackson转换不是一回事
需要再次区分：
MyBatis
数据库记录 → User对象
例如：
id=1, username=zhangsan, age=20
        ↓
new User(1, "zhangsan", 20)
Jackson
User对象 → JSON
例如：
User
转换为：
{
  "id": 1,
  "username": "zhangsan",
  "age": 20
}
完整过程：
MySQL记录
    ↓ MyBatis
User对象
    ↓ Jackson
JSON
不要把这两个框架的作用混在一起。
# 系统学习@Select
@Select是MyBatis提供的查询注解，用于把一条SELECT语句绑定到Mapper方法。
最基本结构：
@Select("SELECT ...")
返回类型 方法名(参数);
例如：
@Select("""
    SELECT id, username, age
    FROM `user`
    WHERE id = #{id}
    """)
User selectById(@Param("id") Long id);
它同时描述了四件事：
@Select中的SQL  → 执行什么查询
方法参数         → 查询需要什么条件
方法名称         → 这个数据库操作叫什么
返回类型         → 怎样组织查询结果
## 1.SQL查询的基本组成
常见查询结构：
```sql
SELECT 字段
FROM 表
WHERE 条件
ORDER BY 排序字段
LIMIT 数量;
```
例如：
```sql
SELECT id, username, age
FROM `user`
WHERE age >= 18
ORDER BY id
LIMIT 10;
```
含义：
SELECT   → 查询哪些字段
FROM     → 从哪张表查询
WHERE    → 筛选哪些记录
ORDER BY → 按什么顺序排列
LIMIT    → 最多返回多少条
## 2.为什么查询全部最好添加排序
如果只写：
```sql
SELECT id, username, age
FROM `user`
```
数据库没有承诺返回顺序永远固定。
如果需要稳定顺序，应明确写：
```sql
ORDER BY id
```
默认是升序：
```sql
ORDER BY id ASC
```
降序：
```sql
ORDER BY id DESC
```
例如最新ID优先：
```sql
ORDER BY id DESC
```
##  3.精确查询与模糊查询
- 精确查询：
```sql
WHERE username = #{username}
```
传入:
zhang
只能匹配用户名完全等于：
zhang
不能匹配：
zhangsan
xiaozhang
- 模糊查询：
MySQL使用：
```sql
LIKE
```
例如：
```java
@Select("""
    SELECT id, username, age
    FROM `user`
    WHERE username LIKE CONCAT('%', #{keyword}, '%')
    ORDER BY id
    """)
List<User> searchByUsername(
        @Param("keyword") String keyword
);
```
传入：
zhang
实际匹配模式：
%zhang%
其中：
%zhang% → 任意位置包含zhang
zhang%  → 以zhang开头
%zhang  → 以zhang结尾
这里使用：

CONCAT('%', #{keyword}, '%')
是因为 #{keyword}属于参数占位符，不能简单写成：
LIKE '%#{keyword}%'
正确思路是让MySQL把三个值连接起来：
%
+
参数keyword
+
%
## 4.范围查询
查询年龄不低于某个值：
```java
@Select("""
    SELECT id, username, age
    FROM `user`
    WHERE age >= #{minAge}
    ORDER BY age
    """)
List<User> selectByMinAge(
        @Param("minAge") Integer minAge
);
```
范围查询：
```java
@Select("""
    SELECT id, username, age
    FROM `user`
    WHERE age BETWEEN #{minAge} AND #{maxAge}
    ORDER BY age
    """)
List<User> selectByAgeRange(
        @Param("minAge") Integer minAge,
        @Param("maxAge") Integer maxAge
);
```
## 5.统计查询
@Select不只能返回User，还能返回数字。
统计用户数量：
```java
@Select("SELECT COUNT(*) FROM `user`")
Long countUsers();
```
数据库返回一行一列：
COUNT(*)
3
所以返回类型可以是：
Long
返回类型取决于查询结果，不是所有 @Select 都必须返回Entity。
## 6.限制返回数量
查询前5个用户：
```java
@Select("""
    SELECT id, username, age
    FROM `user`
    ORDER BY id
    LIMIT 5
    """)
List<User> selectFirstFive();
```
使用参数：
```java
@Select("""
    SELECT id, username, age
    FROM `user`
    ORDER BY id
    LIMIT #{limit}
    """)
List<User> selectLimited(
        @Param("limit") Integer limit
);
```
# MyBatis新增数据
## 1.查询与新增的区别
查询使用：
SELECT
目的：
从数据库读取数据
返回值通常是：
User
List<User>
Long
新增使用：
INSERT
目的：
向数据库写入一条新记录
返回值通常是：
int
表示影响了多少行。
## 2.INSERT基本语法
向 user 表新增用户：
```sql
INSERT INTO `user` (username, age)
VALUES ('lisi', 21);
```
结构：
INSERT INTO `user`  → 向哪张表新增
(username, age)     → 给哪些列提供值
VALUES (...)        → 实际写入的值
## 3.使用@Insert
Mapper可以写：
```java
@Insert("""
    INSERT INTO `user` (username, age)
    VALUES (#{username}, #{age})
    """)
int insert(User user);
```
需要导入：
import org.apache.ibatis.annotations.Insert;
方法含义：
接收一个User对象
→ 读取username和age属性
→ 执行INSERT
→ 返回影响行数
## 4.对象参数怎样进入SQL
假设：
```java
User user = new User();
user.setUsername("lisi");
user.setAge(21);
调用：
userMapper.insert(user);
```
MyBatis读取：
#{username}
近似理解为：
user.getUsername()
得到：
lisi
读取：
#{age}
近似理解为：
user.getAge()
得到：
21
SQL经过预编译：
```sql
INSERT INTO `user` (username, age)
VALUES (?, ?)
```
参数绑定：
第1个? → "lisi"
第2个? → 21
因此仍然使用安全的 #{} 参数绑定。
## 5.为什么不需要@Param
当前方法：
```java
int insert(User user);
```
MyBatis接收到的根参数就是User，因此可以直接读取：
#{username}
#{age}
如果写成：
```java
int insert(@Param("user") User user);
```
SQL才需要写：
#{user.username}
#{user.age}
对于只传一个User对象的方法，直接写：
int insert(User user);
更加简洁。

可以把 #{} 想象成从一个文件夹中找数据。

不写 @Param：

根目录
├── username
└── age

所以直接写：

#{username}
#{age}

写了 @Param("user")：

根目录
└── user
    ├── username
    └── age

所以必须写：

#{user.username}
#{user.age}

核心区别就是：属性外面是否多包了一层参数名称 user。
## 6.为什么返回int
