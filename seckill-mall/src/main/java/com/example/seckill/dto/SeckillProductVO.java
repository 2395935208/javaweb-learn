package com.example.seckill.dto;

import com.example.seckill.entity.SeckillProduct;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record SeckillProductVO(Long id, Long productId, String productName, BigDecimal seckillPrice,
        Integer seckillStock, LocalDateTime startTime, LocalDateTime endTime, Integer status) {
    public static SeckillProductVO from(SeckillProduct sp, String productName) {
        return new SeckillProductVO(sp.getId(), sp.getProductId(), productName, sp.getSeckillPrice(),
                sp.getSeckillStock(), sp.getStartTime(), sp.getEndTime(), sp.getStatus());
    }
}
