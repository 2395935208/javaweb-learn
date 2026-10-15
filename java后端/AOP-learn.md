# AOP 学习笔记

## 1. 是什么？

**AOP：把多个方法都需要的公共逻辑集中写一次，在方法调用时统一执行。**

例如商城的注册、下单、查询商品都需要记录耗时，使用 AOP 就不用在每个方法里重复写计时代码。

## 2. 怎么实现？

Spring AOP 通常通过**代理对象**实现：

```text
Controller 调用 Service 代理
    ↓
公共逻辑：开始计时
    ↓
真正的 Service：执行业务
    ↓
公共逻辑：记录耗时
    ↓
返回结果
```

业务方法专心处理业务，代理负责安排额外功能。

## 3. 代码示例

```java
@Aspect
@Component
public class TimeAspect {

    @Around("execution(* com.example.seckill.service..*(..))")
    public Object recordTime(ProceedingJoinPoint point)
            throws Throwable {

        long start = System.currentTimeMillis();

        try {
            return point.proceed();
        } finally {
            System.out.println(
                "耗时：" + (System.currentTimeMillis() - start)
            );
        }
    }
}
```

- `@Component`：交给 Spring 管理。
- `@Aspect`：声明这是一个切面。
- `@Around`：在匹配的方法调用前后执行逻辑。
- `execution(...)`：这里匹配 service 包及其子包中的方法。
- `point.proceed()`：继续执行被包装的调用，最终执行业务方法。
- `finally`：正常返回或抛异常时，都记录耗时。

## 4. 常见术语

| 术语 | 大白话 |
|---|---|
| 切面 | 公共逻辑与匹配规则的集合 |
| 切点 | 哪些方法需要增强 |
| 通知 | 具体执行什么增强逻辑 |
| 目标对象 | 真正执行业务的对象 |
| 代理对象 | 把增强和业务调用串起来的对象 |

## 5. 常见用途

- 记录日志、耗时。
- 方法级权限检查。
- 事务管理：常见的 `@Transactional` 用法通过代理安排事务。

## 6. 两个常见坑

**同一对象内部调用通常绕过代理：**

```java
public void methodA() {
    this.methodB(); // 通常不会触发 methodB 的 AOP 增强
}
```

**自己 new 的对象通常没有 Spring AOP 增强：**

```java
UserService service = new UserServiceImpl(...);
```

需要使用 Spring 管理并提供的代理对象。

## 7. 与 IoC、DI 的区别

- **IoC**：Spring 负责创建、组装和管理对象。
- **DI**：Spring 把需要的对象传进来。
- **AOP**：通过代理给方法调用增加公共逻辑。

## 一句话面试答法

AOP 将日志、事务等公共逻辑集中处理，Spring AOP 通常通过代理对匹配的方法调用进行增强，减少重复代码，但同一对象内部调用通常会绕过代理。