package com.example.springbootdemo.mapper;

import com.example.springbootdemo.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Service;

//
@Mapper
public interface UserMapper {
    @Select("SELECT id, username, age FROM `user` WHERE id = #{id}")
    User selectById(@Param("id") Long id);
}
