package com.example.seckill.mapper;

import com.example.seckill.entity.User;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

public interface UserMapper {
    @Select("SELECT id, username, password_hash, phone, created_at FROM `user` WHERE id = #{id}")
    User selectById(@Param("id") Long id);
}
