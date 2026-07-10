# Maven是什么
Maven负责管理项目，是java项目的构建工具，依赖管理工具，项目管理工具
它主要帮你做三件事：
1.管理第三方jar包
2.同一项目结构
3.编译，测试，打包，运行项目
## 为什么需要Maven
当你做一个java后端项目时，可能需要：
Spring Boot
MyBatis
MySQL 驱动
Lombok
JWT
Hutool
Swagger
Redis
这些，如果没有Maven，需要手动到官网下载jar文件，导入到IDEA中，同时还有很多问题：
版本不好管理
依赖之间可能冲突
项目换一台电脑就跑不起来
团队协作非常麻烦
有了Maven后，只需要在pom.xml中添加依赖就可以，它会自动帮你下载依赖
## 1.pom.xml是什么
POM全称是Project Object Model，可以理解为是这个项目的说明书
里面会写：
项目叫什么
项目版本是多少
用什么 Java 版本
依赖了哪些库
用什么插件打包
父项目是谁
模块有哪些
一个典型的spring boot项目的pom.xml如下：
```XML
<project>
    <modelVersion>4.0.0</modelVersion>

    <groupId>com.example</groupId>
    <artifactId>demo</artifactId>
    <version>0.0.1-SNAPSHOT</version>

    <dependencies>
        <!-- 依赖写这里 -->
    </dependencies>

</project>
``` 
## 2. groupId、artifactId、version 是什么？
Maven坐标，用来唯一确定一个jar包或者项目的
- groupId：一般表示公司，组织，个人域名的到写
例如：
com.alibaba
org.springframework.boot
com.example
- artifactId；表示项目名或模块名
例如：
```XML
<artifactId>health-check-system</artifactId>
```
- version：表示项目版本
例如：<version>1.0.0</version>
## 3.dependency是什么？
dependency就是依赖，也就是你要用别人写好的工具包
例如你要用Spring Web：
```XML
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
```
## 4.Maven仓库是什么？
Maven下载依赖时会去仓库找，Maven仓库分为三类：
本地仓库，远程仓库，中央仓库
- 本地仓库：
  在你电脑上的Maven依赖缓存目录中，windows中一般在C:\Users\你的用户名\.m2\repository
- 中央仓库：
  Maven官方仓库，你的项目里写了依赖，Maven回去中央仓库下载
- 镜像仓库：
国内访问中央仓库比较慢，所以经常用阿里云的镜像仓库
很多教程会教你配置：
```XML
<mirror>
    <id>aliyunmaven</id>
    <mirrorOf>*</mirrorOf>
    <name>阿里云公共仓库</name>
    <url>https://maven.aliyun.com/repository/public</url>
</mirror>
```
这个配置通常写在：
Maven安装目录/conf/settings.xml
## 5.Maven项目标准目录
demo
├── pom.xml
└── src
    ├── main
    │   ├── java
    │   │   └── com/example/demo
    │   │       └── DemoApplication.java
    │   └── resources
    │       └── application.yml
    └── test
        └── java  
重点记：
src/main/java       放 Java 源代码
src/main/resources  放配置文件
src/test/java       放测试代码
pom.xml             Maven 配置文件
## 6.Maven生命周期

