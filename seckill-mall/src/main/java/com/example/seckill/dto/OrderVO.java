package com.example.seckill.dto;

import com.example.seckill.entity.Order;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/** productId 为秒杀商品ID（seckill_product.id）。 */
public record OrderVO(Long id, String orderNo, Long userId, Long productId, BigDecimal seckillPrice,
        Integer status, LocalDateTime payExpireAt, LocalDateTime createdAt) {
    public static OrderVO from(Order order) {
        return new OrderVO(order.getId(), order.getOrderNo(), order.getUserId(), order.getProductId(),
                order.getSeckillPrice(), order.getStatus(), order.getPayExpireAt(), order.getCreatedAt());
    }
}
