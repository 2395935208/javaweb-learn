package com.example.seckill;

import com.example.seckill.entity.Product;
import com.example.seckill.mapper.ProductMapper;
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@EnabledIfEnvironmentVariable(named = "SECKILL_DB_TEST", matches = "true")
class ProductDatabaseTest {
    @Autowired private JdbcTemplate jdbc;
    @Autowired private ProductMapper productMapper;
    @Autowired private MockMvc mvc;

    @Test
    void realMysqlMapsDescriptionAndExecutesLimitOffset() throws Exception {
        long originalTotal = productMapper.count();
        long first = insertProduct("第一个商品", "商品介绍");
        long second = insertProduct("第二个商品", null);
        long third = insertProduct("第三个商品", "最后添加");
        assertThat(productMapper.count()).isEqualTo(originalTotal + 3);
        assertThat(productMapper.selectPage(2, 0)).extracting(Product::getId).containsExactly(third, second);
        assertThat(productMapper.selectPage(1, 2)).extracting(Product::getId).containsExactly(first);
        assertThat(productMapper.selectById(first).getDescription()).isEqualTo("商品介绍");
        assertThat(productMapper.selectById(second).getDescription()).isNull();
        mvc.perform(get("/api/products").param("page", "2").param("size", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.total").value(originalTotal + 3))
                .andExpect(jsonPath("$.data.list[0].id").value(first))
                .andExpect(jsonPath("$.data.list[0].description").value("商品介绍"));
        mvc.perform(get("/api/products/" + first))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.description").value("商品介绍"))
                .andExpect(jsonPath("$.data.status").doesNotExist());
    }

    private long insertProduct(String name, String description) {
        GeneratedKeyHolder key = new GeneratedKeyHolder();
        jdbc.update(connection -> {
            PreparedStatement statement = connection.prepareStatement(
                    "INSERT INTO product (name, description, price, stock, status, created_at) VALUES (?, ?, ?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, name);
            statement.setString(2, description);
            statement.setBigDecimal(3, new BigDecimal("99.90"));
            statement.setInt(4, 10);
            statement.setInt(5, 1);
            statement.setObject(6, LocalDateTime.of(2026, 10, 11, 12, 0));
            return statement;
        }, key);
        return key.getKey().longValue();
    }
}
