package com.example.seckill.mapper;

import com.example.seckill.entity.SeckillProduct;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

public interface SeckillProductMapper {
    @Select("SELECT id, product_id, seckill_price, seckill_stock, start_time, end_time, status, created_at FROM `seckill_product` WHERE id = #{id}")
    SeckillProduct selectById(@Param("id") Long id);

    /** 单条 SQL 原子扣减库存；返回 0 表示库存不足（或商品不存在）。 */
    @Update("UPDATE `seckill_product` SET seckill_stock = seckill_stock - 1 WHERE id = #{id} AND seckill_stock > 0")
    int decreaseStock(@Param("id") Long id);
}
