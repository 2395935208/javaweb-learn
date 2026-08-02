package com.example.springbootdemo.mapper;

import com.example.springbootdemo.entity.User;
import org.apache.ibatis.annotations.*;

import java.util.List;

//
@Mapper
public interface UserMapper {
    @Select("SELECT id, username, age FROM `user` WHERE id = #{id}")
    User selectById(@Param("id") Long id);
    @Select("SELECT id, username, age FROM `user` ORDER BY id ASC")
    List<User> selectAll();
    @Insert("""
    INSERT INTO `user` (username, age)
    VALUES (#{username}, #{age})
    """)
    @Options(
            useGeneratedKeys = true,
            keyProperty = "id",
            keyColumn = "id"
    )
    int insert(User user);

    //更新
    @Update("""
    UPDATE `user`
    SET username = #{username},
        age = #{age}
    WHERE id = #{id}
    """)
    int update(User user);

    //删除操作
    @Delete("""
    DELETE FROM `user`
    WHERE id = #{id}
    """)
    int deleteById(@Param("id") Long id);
}
