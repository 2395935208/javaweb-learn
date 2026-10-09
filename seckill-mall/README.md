# seckill-mall

秒杀商城基础版（骨架与用户注册/登录）：Java 17、Spring Boot 3.5.16、MyBatis 3.0.5、MySQL 8。暂不包含 Redis、MQ、JWT 或秒杀下单业务。

## 目录

```text
src/main/java/com/example/seckill/
├── SeckillMallApplication.java
├── controller/       # 健康检查、用户注册/登录接口
├── service/impl/     # 服务接口及实现
├── mapper/           # 四张表的按 ID 查询，以及数据库连通性检查
├── entity/           # User、Product、SeckillProduct、Order
├── dto/              # 接口响应对象
├── common/           # Result、BusinessException、GlobalExceptionHandler
└── config/           # MyBatis Mapper 扫描与 BCrypt 编码器
sql/schema.sql        # 用户提供的建库建表 SQL，增加 USE seckill_mall
```

实体使用普通 Java Bean；金额对应 BigDecimal，DATETIME 对应 LocalDateTime，TINYINT 对应 Integer。MyBatis 开启下划线转驼峰；SQL 中的 order 使用反引号。User 的 passwordHash 不参与 JSON 序列化。

## 本地启动

首次初始化数据库（已有四张表时不要重复执行）：

```sh
mysql -h 127.0.0.1 -P 3306 -u root -p < sql/schema.sql
```

数据源默认指向 `127.0.0.1:3306/seckill_mall`，用户为 root，密码通过环境变量传入。终端中输入本机数据库密码（zsh）：

```sh
read -s 'DB_PASSWORD?MySQL 密码: '
export DB_PASSWORD
mvn spring-boot:run
```

也可以在 IDE 的运行配置中设置 `DB_PASSWORD` 后运行 SeckillMallApplication。支持 `DB_HOST`、`DB_PORT`、`DB_USERNAME` 和 `SERVER_PORT`；默认服务端口为 8080。

```sh
curl http://localhost:8080/api/health
```

预期返回（会实际查询 MySQL）：

```json
{"code":200,"message":"成功","data":{"application":"seckill-mall","database":"UP"}}
```

HTTP 错误保留相应状态码并使用 Result 包装；未处理的异常返回 HTTP 500，内部异常详情只写服务器日志。

## 验证

```sh
mvn verify
```

默认运行健康接口和用户模块测试，不要求数据库；用户模块使用真实服务与 BCrypt、模拟 Mapper，验证校验、返回字段、重复用户名、登录失败和无默认安全拦截。真实 MySQL 测试默认跳过。四张表初始化完成、数据库环境变量设置后，可以启用它：

```sh
SECKILL_DB_TEST=true mvn verify
```

真实数据库测试覆盖四张表字段映射，以及注册入库、生成 ID、BCrypt 哈希和登录。测试记录使用事务回滚。

打包后运行：

```sh
java -jar target/seckill-mall-0.0.1-SNAPSHOT.jar
```

建表由 SQL 手动执行，应用启动时不会自动修改表结构。

## 用户注册与登录

基础版只核验用户名和密码并返回用户信息，不签发 JWT，也不创建登录会话或添加登录拦截器。Security 默认自动配置已排除，只注册 BCryptPasswordEncoder。

注册：`POST /api/users/register`

```json
{"username":"alice","password":"secret123","phone":"13800000000"}
```

username 必填且长度为 2–20；password 必填且长度为 6–20；phone 可以省略或为 null，提供时最多 20 个字符（与表字段长度一致）。请求通过 `@Valid` 校验。

登录：`POST /api/users/login`

```json
{"username":"alice","password":"secret123"}
```

username 和 password 均不能为空。注册及登录成功都返回 `Result<UserVO>`，data 只有 id、username、phone、createdAt，不包含明文密码或 passwordHash。密码不做 trim，以原始输入参与 BCrypt 哈希和匹配。

| 场景 | HTTP 状态/Result.code | message |
| --- | --- | --- |
| 成功 | 200 | 成功 |
| 参数校验失败 | 400 | 具体校验提示 |
| 用户名重复（含插入时唯一键冲突） | 409 | 用户名已存在 |
| 用户不存在或密码不匹配 | 401 | 用户名或密码错误 |

本次只增加接口与代码，数据库表结构不变，不需要重复执行 schema.sql。IDEA 重新加载 Maven 后，在 seckill-mall 自己的运行配置中设置数据库环境变量，再重启应用。

## 独立配置原则

本项目只使用自己的 application.yml 和运行环境变量，数据库为 seckill_mall。开发、启动和测试均不得读取、提取或复用其他练习项目的配置文件或凭据。

在 IDEA 的 SeckillMallApplication 运行配置中明确设置 DB_HOST、DB_PORT、DB_USERNAME、DB_PASSWORD。这些值应对应本项目确定的数据库连接；不要复制旧练习项目的配置。密码保存在本地运行环境中，不写入受版本管理的文件。当前 application.yml 的 root 是本地默认值；需要专用账号时，在本项目中通过 DB_USERNAME 明确指定。

真实数据库测试也只使用本项目配置。在运行 UserDatabaseTest 和 DatabaseMappingTest 的配置中设置同一组本项目数据库环境变量，并增加 SECKILL_DB_TEST=true；应用运行配置的环境变量不会自动传递给独立的测试运行配置。

终端执行真实数据库测试前，同样需要在该终端设置本项目的数据库环境变量，再运行 SECKILL_DB_TEST=true mvn verify。不从其他项目自动寻找缺失的密码。
