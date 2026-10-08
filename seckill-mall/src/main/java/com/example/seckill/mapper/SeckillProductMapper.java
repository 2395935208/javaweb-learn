package com.example.seckill.mapper;

import com.example.seckill.entity.SeckillProduct;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

public interface SeckillProductMapper {
    @Select("SELECT id, product_id, seckill_price, seckill_stock, start_time, end_time, status, created_at FROM `seckill_product` WHERE id = #{id}")
    SeckillProduct selectById(@Param("id") Long id);
}
