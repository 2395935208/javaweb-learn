## Spring Boot是什么
Spring Boot是一个用于快速开发java后端项目的框架
## 为什么需要 Spring Boot
和Spring相比，前者需要配置大量的xml配置
spring boot可以自动配置，并且可以集成SpringMVC、SpringData、SpringSecurity、SpringCloud等等，它可以：
自动配置
内置 Tomcat
快速启动
依赖简化
配置文件统一
适合微服务和前后端分离
## spring boot项目基本结构
一个简单的Spring Boot项目结构如下：
demo
├── pom.xml
└── src/main
    ├── java
    │   └── com/example/demo
    │       ├── DemoApplication.java
    │       ├── controller
    │       │   └── UserController.java
    │       ├── service
    │       │   └── UserService.java
    │       ├── mapper
    │       │   └── UserMapper.java
    │       └── entity
    │           └── User.java
    └── resources
        ├── application.yml
        └── mapper
            └── UserMapper.xml
重点的分层：
controller  接收前端请求
service     处理业务逻辑
mapper      操作数据库
entity      实体类，对应数据库表
## 1.启动类
Spring Boot项目一定有一个启动类
```java
@SpringBootApplication
public class DemoApplication {
    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }
}
```
这就是项目入口，运行它后端就可以启动了
## 2.@SpringBootApplication
这个注解表示：这是一个Spring Boot应用
它包含三个重要作用：
@SpringBootConfiguration
@EnableAutoConfiguration
@ComponentScan
他的作用是：
1. 标记当前类是配置类
2. 开启自动配置
3. 扫描当前包及子包下的组件
所以启动类一般放在最外层例如:com.dege.health
然后你的Controller，Service,Mapper都放在他的下面：
com.dege.health.controller
com.dege.health.service
com.dege.health.mapper
这样才能被扫描到
## 3.Controller是什么
Controller是控制层，作用是：
接收前端请求，返回数据给前端。
例如：
@RestController
@RequestMapping("/users")
public class UserController {

    @GetMapping("/hello")
    public String hello() {
        return "hello spring boot";
    }
}
## 4.@RestController
表示这个类是一个接口控制器
它相当于
```java
@Controller
@ResponseBody
```
表示这个类返回的不是页面，而是直接返回数据
## 5.@RequestMapping
用于设置请求路径
```java
@RequestMapping("/users")
```
表示这个 Controller 下的接口统一以 /users 开头。
本质是告诉Spring Boot MVC，所有访问路径以 /users 开头的请求，都交给这个 Controller 处理。”
## 6.@GetMapping / @PostMapping
常见的HTTP请求方式有：
GET     查询数据
POST    新增数据 / 登录
PUT     修改数据
DELETE  删除数据
对应的注解有：
@GetMapping
@PostMapping
@PutMapping
@DeleteMapping
## 7. 参数接收
Spring Boot常见接收参数的方法有三种：
方式一：@RequestParam
适合接收 URL 参数。
例如请求：
/users?name=Tom
代码：
```java
@GetMapping
public String getUser(@RequestParam String name) {
    return name;
}
```
条件查询，参数在？后面
方式二：@PathVariable
适合接收路径参数。
请求：
/users/1
代码：
```java
@GetMapping("/{id}")
public String getUser(@PathVariable Long id) {
    return "用户ID：" + id;
}
```
方式三：@RequestBody
适合接收 JSON 请求体。
前端发送：
{
  "username": "admin",
  "password": "123456"
}
后端接收：
```java

@PostMapping("/login")
public String login(@RequestBody LoginDTO loginDTO) {
    return loginDTO.getUsername();
}
```
## 8. JSON 返回

Spring Boot 默认可以把 Java 对象转换成 JSON。
例如：
```java
@GetMapping("/user")
public User getUser() {
    User user = new User();
    user.setId(1L);
    user.setName("Tom");
    return user;
}
```
前端接收到的是：
```json
{
  "id": 1,
  "name": "Tom"
}
```
## 9.Service 层是什么？
