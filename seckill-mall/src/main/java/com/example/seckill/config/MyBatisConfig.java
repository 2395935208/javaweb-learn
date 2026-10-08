package com.example.seckill.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan("com.example.seckill.mapper")
public class MyBatisConfig {
}
