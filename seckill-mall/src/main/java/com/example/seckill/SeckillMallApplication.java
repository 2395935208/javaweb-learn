package com.example.seckill;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;

// 基础版只使用 BCrypt，不启用默认登录拦截或默认内存用户。
@SpringBootApplication(exclude = {SecurityAutoConfiguration.class, UserDetailsServiceAutoConfiguration.class})
public class SeckillMallApplication {
    public static void main(String[] args) {
        SpringApplication.run(SeckillMallApplication.class, args);
    }
}
