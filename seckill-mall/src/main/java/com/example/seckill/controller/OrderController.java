package com.example.seckill.controller;

import com.example.seckill.common.Result;
import com.example.seckill.dto.OrderVO;
import com.example.seckill.dto.PageResult;
import com.example.seckill.service.OrderService;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * TODO(JWT): userId 暂由调用方传入，仅为临时方案；11 月 JWT 登录改造后改为从登录态获取，并移除该参数。
 */
@RestController
@RequestMapping("/api/orders")
@Validated
public class OrderController {
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/{id}")
    public Result<OrderVO> getById(
            @PathVariable("id") @Min(value = 1, message = "id必须大于等于1") Long id,
            @RequestParam("userId") @Min(value = 1, message = "userId必须大于等于1") Long userId) {
        return Result.success(orderService.getById(userId, id));
    }

    @GetMapping
    public Result<PageResult<OrderVO>> list(
            @RequestParam("userId") @Min(value = 1, message = "userId必须大于等于1") Long userId,
            @RequestParam(name = "page", defaultValue = "1")
            @Min(value = 1, message = "page必须大于等于1") int page,
            @RequestParam(name = "size", defaultValue = "10")
            @Min(value = 1, message = "size必须大于等于1")
            @Max(value = 100, message = "size不能超过100") int size) {
        return Result.success(orderService.list(userId, page, size));
    }

    @PostMapping("/{id}/pay")
    public Result<OrderVO> pay(
            @PathVariable("id") @Min(value = 1, message = "id必须大于等于1") Long id,
            @RequestParam("userId") @Min(value = 1, message = "userId必须大于等于1") Long userId) {
        return Result.success(orderService.pay(userId, id));
    }

    @PostMapping("/{id}/cancel")
    public Result<OrderVO> cancel(
            @PathVariable("id") @Min(value = 1, message = "id必须大于等于1") Long id,
            @RequestParam("userId") @Min(value = 1, message = "userId必须大于等于1") Long userId) {
        return Result.success(orderService.cancel(userId, id));
    }
}
