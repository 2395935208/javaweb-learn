package com.example.seckill.controller;

import com.example.seckill.common.Result;
import com.example.seckill.dto.HealthResponse;
import com.example.seckill.service.HealthService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/health")
public class HealthController {
    private final HealthService healthService;

    public HealthController(HealthService healthService) {

        this.healthService = healthService;
    }

    @GetMapping
    public Result<HealthResponse> health() {

        return Result.success(healthService.check());
    }
}
