package com.example.seckill.controller;

import com.example.seckill.common.Result;
import com.example.seckill.dto.PageResult;
import com.example.seckill.dto.ProductVO;
import com.example.seckill.service.ProductService;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/products")
@Validated
public class ProductController {
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public Result<PageResult<ProductVO>> list(
            @RequestParam(name = "page", defaultValue = "1")
            @Min(value = 1, message = "page必须大于等于1") int page,
            @RequestParam(name = "size", defaultValue = "10")
            @Min(value = 1, message = "size必须大于等于1")
            @Max(value = 100, message = "size不能超过100") int size) {
        return Result.success(productService.list(page, size));
    }

    @GetMapping("/{id}")
    public Result<ProductVO> getById(@PathVariable("id") Long id) {
        return Result.success(productService.getById(id));
    }
}
