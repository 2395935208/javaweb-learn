package com.example.seckill;

import com.example.seckill.entity.Product;
import com.example.seckill.mapper.ProductMapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ProductModuleTest {
    @Autowired private MockMvc mvc;
    @Autowired private ObjectMapper json;
    @MockitoBean private ProductMapper productMapper;

    @Test
    void defaultsToFirstPageAndReturnsOnlyProductVOFields() throws Exception {
        when(productMapper.count()).thenReturn(2L);
        when(productMapper.selectPage(10, 0L)).thenReturn(List.of(product(2L), product(1L)));
        String response = mvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.total").value(2))
                .andExpect(jsonPath("$.data.page").value(1))
                .andExpect(jsonPath("$.data.size").value(10))
                .andExpect(jsonPath("$.data.list.length()").value(2))
                .andExpect(jsonPath("$.data.list[0].description").value("商品介绍"))
                .andExpect(jsonPath("$.data.list[0].price").value(99.90))
                .andExpect(jsonPath("$.data.list[0].stock").value(10))
                .andExpect(jsonPath("$.data.list[0].createdAt").value("2026-10-11T12:00:00"))
                .andReturn().getResponse().getContentAsString();
        JsonNode item = json.readTree(response).get("data").get("list").get(0);
        assertThat(item.size()).isEqualTo(6);
        assertThat(item.has("status")).isFalse();
        InOrder order = inOrder(productMapper);
        order.verify(productMapper).count();
        order.verify(productMapper).selectPage(10, 0L);
    }

    @Test
    void totalIsDatabaseCountRatherThanCurrentPageLength() throws Exception {
        when(productMapper.count()).thenReturn(12L);
        when(productMapper.selectPage(10, 10L)).thenReturn(List.of(product(2L), product(1L)));
        mvc.perform(get("/api/products").param("page", "2").param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.total").value(12))
                .andExpect(jsonPath("$.data.list.length()").value(2))
                .andExpect(jsonPath("$.data.page").value(2))
                .andExpect(jsonPath("$.data.size").value(10));
        InOrder order = inOrder(productMapper);
        order.verify(productMapper).count();
        order.verify(productMapper).selectPage(10, 10L);
    }

    @Test
    void emptyTableReturnsEmptyList() throws Exception {
        mvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.total").value(0))
                .andExpect(jsonPath("$.data.list").isArray())
                .andExpect(jsonPath("$.data.list").isEmpty());
        verify(productMapper, never()).selectPage(anyInt(), anyLong());
    }

    @Test
    void pageBeyondTotalReturnsEmptyListWithOriginalTotal() throws Exception {
        when(productMapper.count()).thenReturn(12L);
        mvc.perform(get("/api/products").param("page", "3").param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.total").value(12))
                .andExpect(jsonPath("$.data.list").isEmpty());
        verify(productMapper, never()).selectPage(anyInt(), anyLong());
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 100})
    void sizeBoundariesAreAccepted(int size) throws Exception {
        when(productMapper.count()).thenReturn(200L);
        when(productMapper.selectPage(size, 0L)).thenReturn(List.of(product(1L)));
        mvc.perform(get("/api/products").param("size", Integer.toString(size)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.size").value(size));
        verify(productMapper).selectPage(size, 0L);
    }

    @Test
    void offsetUsesLongBeforeMultiplication() throws Exception {
        long offset = ((long) Integer.MAX_VALUE - 1) * 100;
        when(productMapper.count()).thenReturn(offset + 1);
        when(productMapper.selectPage(100, offset)).thenReturn(List.of(product(1L)));
        mvc.perform(get("/api/products").param("page", Integer.toString(Integer.MAX_VALUE)).param("size", "100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.page").value(Integer.MAX_VALUE));
        verify(productMapper).selectPage(100, offset);
    }

    @ParameterizedTest
    @MethodSource("invalidPageParameters")
    void invalidParametersReturn400BeforeQuery(String parameter, String value) throws Exception {
        mvc.perform(get("/api/products").param(parameter, value))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.data").isEmpty());
        verifyNoInteractions(productMapper);
    }

    static Stream<Arguments> invalidPageParameters() {
        return Stream.of(Arguments.of("page", "0"), Arguments.of("page", "-1"),
                Arguments.of("size", "0"), Arguments.of("size", "-1"),
                Arguments.of("size", "101"), Arguments.of("size", "1000000"),
                Arguments.of("page", "abc"), Arguments.of("size", "abc"),
                Arguments.of("page", "1.5"), Arguments.of("size", "1.5"),
                Arguments.of("page", "2147483648"), Arguments.of("size", "2147483648"));
    }

    @Test
    void paginationValidationHasSpecificMessage() throws Exception {
        mvc.perform(get("/api/products").param("size", "101"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("size不能超过100"));
        verifyNoInteractions(productMapper);
    }

    @Test
    void detailReturnsProductVOWithoutInternalStatus() throws Exception {
        when(productMapper.selectById(7L)).thenReturn(product(7L));
        String response = mvc.perform(get("/api/products/7"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(7))
                .andExpect(jsonPath("$.data.name").value("测试商品"))
                .andExpect(jsonPath("$.data.description").value("商品介绍"))
                .andExpect(jsonPath("$.data.status").doesNotExist())
                .andReturn().getResponse().getContentAsString();
        assertThat(json.readTree(response).get("data").size()).isEqualTo(6);
        verify(productMapper).selectById(7L);
    }

    @Test
    void existingProductMayHaveNullDescription() throws Exception {
        Product product = product(8L);
        product.setDescription(null);
        when(productMapper.selectById(8L)).thenReturn(product);
        String response = mvc.perform(get("/api/products/8"))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        assertThat(json.readTree(response).get("data").get("description").isNull()).isTrue();
    }

    @Test
    void missingProductReturnsBusiness404() throws Exception {
        mvc.perform(get("/api/products/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(404))
                .andExpect(jsonPath("$.message").value("商品不存在"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"abc", "9223372036854775808"})
    void invalidIdTypeReturns400BeforeQuery(String id) throws Exception {
        mvc.perform(get("/api/products/" + id))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400));
        verifyNoInteractions(productMapper);
    }

    private Product product(Long id) {
        Product product = new Product();
        product.setId(id);
        product.setName("测试商品");
        product.setDescription("商品介绍");
        product.setPrice(new BigDecimal("99.90"));
        product.setStock(10);
        product.setStatus(1);
        product.setCreatedAt(LocalDateTime.of(2026, 10, 11, 12, 0));
        return product;
    }
}
