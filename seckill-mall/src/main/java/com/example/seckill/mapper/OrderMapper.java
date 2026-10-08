package com.example.seckill.mapper;

import com.example.seckill.entity.Order;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

public interface OrderMapper {
    @Select("SELECT id, order_no, user_id, product_id, seckill_price, status, pay_expire_at, created_at FROM `order` WHERE id = #{id}")
    Order selectById(@Param("id") Long id);
}
