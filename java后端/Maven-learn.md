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
