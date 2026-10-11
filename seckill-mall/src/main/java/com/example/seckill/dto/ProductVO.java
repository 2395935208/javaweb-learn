package com.example.seckill.dto;

import com.example.seckill.entity.Product;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ProductVO(Long id, String name, String description, BigDecimal price,
        Integer stock, LocalDateTime createdAt) {
    public static ProductVO from(Product product) {
        return new ProductVO(product.getId(), product.getName(), product.getDescription(),
                product.getPrice(), product.getStock(), product.getCreatedAt());
    }
}
