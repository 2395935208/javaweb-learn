package com.example.seckill.mapper;

import com.example.seckill.entity.Product;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

public interface ProductMapper {
    @Select("SELECT id, name, price, stock, status, created_at FROM `product` WHERE id = #{id}")
    Product selectById(@Param("id") Long id);
}
