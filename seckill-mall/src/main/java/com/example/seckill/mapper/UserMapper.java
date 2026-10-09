package com.example.seckill.mapper;

import com.example.seckill.entity.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

public interface UserMapper {
    @Select("SELECT id, username, password_hash, phone, created_at FROM `user` WHERE id = #{id}")
    User selectById(@Param("id") Long id);

    @Select("SELECT id, username, password_hash, phone, created_at FROM `user` WHERE username = #{username}")
    User selectByUsername(@Param("username") String username);

    @Insert("INSERT INTO `user` (username, password_hash, phone, created_at) "
            + "VALUES (#{username}, #{passwordHash}, #{phone}, #{createdAt})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(User user);
}
