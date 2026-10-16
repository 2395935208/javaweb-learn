package com.example.seckill.controller;

import com.example.seckill.common.Result;
import com.example.seckill.dto.OrderVO;
import com.example.seckill.dto.SeckillProductVO;
import com.example.seckill.service.SeckillService;
import jakarta.validation.constraints.Min;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/seckill")
@Validated
public class SeckillController {
    private final SeckillService seckillService;

    public SeckillController(SeckillService seckillService) {
        this.seckillService = seckillService;
    }

    /**
     * 参与秒杀下单。
     * TODO(JWT): userId 暂由调用方传入，仅为临时方案；11 月 JWT 登录改造后改为从登录态获取，并移除该参数。
     */
    @PostMapping("/{seckillProductId}")
    public Result<OrderVO> seckill(
            @PathVariable("seckillProductId") @Min(value = 1, message = "seckillProductId必须大于等于1") Long seckillProductId,
            @RequestParam("userId") @Min(value = 1, message = "userId必须大于等于1") Long userId) {
        return Result.success(seckillService.seckill(userId, seckillProductId));
    }

    @GetMapping("/{id}")
    public Result<SeckillProductVO> getById(
            @PathVariable("id") @Min(value = 1, message = "id必须大于等于1") Long id) {
        return Result.success(seckillService.getById(id));
    }
}
