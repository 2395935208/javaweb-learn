# seckill-mall

秒杀商城第一阶段骨架：Java 17、Spring Boot 3.5.16、MyBatis 3.0.5、MySQL 8。暂不包含 Redis、MQ、JWT 或秒杀下单业务。

## 目录

```text
src/main/java/com/example/seckill/
├── SeckillMallApplication.java
├── controller/       # 健康检查接口
├── service/impl/     # 服务接口及实现
├── mapper/           # 四张表的按 ID 查询，以及数据库连通性检查
├── entity/           # User、Product、SeckillProduct、Order
├── dto/              # 接口响应对象
├── common/           # Result、BusinessException、GlobalExceptionHandler
└── config/           # MyBatis Mapper 扫描
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

默认运行三个接口测试，不要求数据库；真实 MySQL 映射测试默认跳过。四张表初始化完成、数据库环境变量设置后，可以启用它：

```sh
SECKILL_DB_TEST=true mvn verify
```

真实数据库测试覆盖四张表全部字段的读取、金额和时间映射，测试记录使用事务回滚。

打包后运行：

```sh
java -jar target/seckill-mall-0.0.1-SNAPSHOT.jar
```

建表由 SQL 手动执行，应用启动时不会自动修改表结构。
