package com.example.springbootdemo.mapper;

import com.example.springbootdemo.entity.User;
import org.apache.ibatis.annotations.*;

import java.util.List;
import com.example.springbootdemo.dto.UserOrderDTO;

//
@Mapper
public interface UserMapper {
    List<UserOrderDTO> selectUserOrders();

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
    //动态sql查询
    List<User> search(
      @Param("keyword") String keyword,
      @Param("minAge") Integer minAge
    );
    //分页查询
    List<User> selectPage(
            @Param("offset") Integer offset,
            @Param("pageSize") Integer pageSize
    );
    //统计用户总数
    long count();

    //批量查询
    List<User> selectByIds(@Param("ids") List<Long> ids);

}
