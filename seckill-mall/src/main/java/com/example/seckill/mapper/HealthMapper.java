package com.example.seckill.mapper;

import org.apache.ibatis.annotations.Select;

public interface HealthMapper {
    @Select("SELECT 1")
    int ping();
}
