package com.example.seckill;

import com.example.seckill.entity.*;
import com.example.seckill.mapper.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@EnabledIfEnvironmentVariable(named = "SECKILL_DB_TEST", matches = "true")
class DatabaseMappingTest {
    @Autowired private JdbcTemplate jdbc;
    @Autowired private UserMapper userMapper;
    @Autowired private ProductMapper productMapper;
    @Autowired private SeckillProductMapper seckillProductMapper;
    @Autowired private OrderMapper orderMapper;
    @Autowired private HealthMapper healthMapper;

    @Test
    void mapsAllFourTablesIncludingMoneyAndDates() {
        long id = -Math.abs(UUID.randomUUID().getMostSignificantBits());
        String unique = UUID.randomUUID().toString();
        LocalDateTime start = LocalDateTime.of(2026, 10, 7, 12, 0);
        LocalDateTime end = start.plusHours(1);
        jdbc.update("INSERT INTO user (id, username, password_hash, phone, created_at) VALUES (?, ?, ?, ?, ?)",
                id, unique, "test-hash", "13800000000", start);
        jdbc.update("INSERT INTO product (id, name, description, price, stock, status, created_at) VALUES (?, ?, ?, ?, ?, ?, ?)",
                id, "测试商品", "商品介绍", new BigDecimal("99.90"), 10, 1, start);
        jdbc.update("INSERT INTO seckill_product (id, product_id, seckill_price, seckill_stock, start_time, end_time, status, created_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?)",
                id, id, new BigDecimal("9.90"), 5, start, end, 0, start);
        jdbc.update("INSERT INTO `order` (id, order_no, user_id, product_id, seckill_price, status, pay_expire_at, created_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?)",
                id, unique, id, id, new BigDecimal("9.90"), 0, end, start);

        User user = userMapper.selectById(id);
        assertThat(user).extracting(User::getId, User::getUsername, User::getPasswordHash,
                User::getPhone, User::getCreatedAt)
                .containsExactly(id, unique, "test-hash", "13800000000", start);
        Product product = productMapper.selectById(id);
        assertThat(product).extracting(Product::getId, Product::getName, Product::getDescription, Product::getPrice,
                Product::getStock, Product::getStatus, Product::getCreatedAt)
                .containsExactly(id, "测试商品", "商品介绍", new BigDecimal("99.90"), 10, 1, start);
        SeckillProduct seckill = seckillProductMapper.selectById(id);
        assertThat(seckill).extracting(SeckillProduct::getId, SeckillProduct::getProductId,
                SeckillProduct::getSeckillPrice, SeckillProduct::getSeckillStock,
                SeckillProduct::getStartTime, SeckillProduct::getEndTime,
                SeckillProduct::getStatus, SeckillProduct::getCreatedAt)
                .containsExactly(id, id, new BigDecimal("9.90"), 5, start, end, 0, start);
        Order order = orderMapper.selectById(id);
        assertThat(order).extracting(Order::getId, Order::getOrderNo, Order::getUserId,
                Order::getProductId, Order::getSeckillPrice, Order::getStatus,
                Order::getPayExpireAt, Order::getCreatedAt)
                .containsExactly(id, unique, id, id, new BigDecimal("9.90"), 0, end, start);
        assertThat(healthMapper.ping()).isEqualTo(1);
    }
}
