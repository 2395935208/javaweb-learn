package com.example.seckill.mapper;

import com.example.seckill.entity.Order;
import java.util.List;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

public interface OrderMapper {
    @Select("SELECT id, order_no, user_id, product_id, seckill_price, status, pay_expire_at, created_at FROM `order` WHERE id = #{id}")
    Order selectById(@Param("id") Long id);

    // product_id 存的是 seckill_product.id
    @Insert("INSERT INTO `order` (order_no, user_id, product_id, seckill_price, status, pay_expire_at, created_at) "
            + "VALUES (#{orderNo}, #{userId}, #{productId}, #{seckillPrice}, #{status}, #{payExpireAt}, #{createdAt})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Order order);

    @Select("SELECT COUNT(*) FROM `order` WHERE user_id = #{userId}")
    long countByUserId(@Param("userId") Long userId);

    @Select("SELECT id, order_no, user_id, product_id, seckill_price, status, pay_expire_at, created_at FROM `order` "
            + "WHERE user_id = #{userId} ORDER BY id DESC LIMIT #{limit} OFFSET #{offset}")
    List<Order> selectPageByUserId(@Param("userId") Long userId, @Param("limit") int limit,
            @Param("offset") long offset);

    /** 条件更新防并发：影响行数为 0 说明当前状态已不是 fromStatus，由调用方据此判断冲突原因。 */
    @Update("UPDATE `order` SET status = #{toStatus} WHERE id = #{id} AND status = #{fromStatus}")
    int updateStatus(@Param("id") Long id, @Param("fromStatus") int fromStatus, @Param("toStatus") int toStatus);
}
