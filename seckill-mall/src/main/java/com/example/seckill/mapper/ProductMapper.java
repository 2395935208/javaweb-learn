package com.example.seckill.mapper;

import com.example.seckill.entity.Product;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

public interface ProductMapper {
    @Select("SELECT id, name, description, price, stock, status, created_at FROM `product` WHERE id = #{id}")
    Product selectById(@Param("id") Long id);

    @Select("SELECT COUNT(*) FROM `product`")
    long count();

    @Select("SELECT id, name, description, price, stock, status, created_at FROM `product` "
            + "ORDER BY id DESC LIMIT #{limit} OFFSET #{offset}")
    List<Product> selectPage(@Param("limit") int limit, @Param("offset") long offset);
}
