package com.example.seckill.service.impl;

import com.example.seckill.dto.HealthResponse;
import com.example.seckill.mapper.HealthMapper;
import com.example.seckill.service.HealthService;
import org.springframework.stereotype.Service;

@Service
public class HealthServiceImpl implements HealthService {
    private final HealthMapper healthMapper;

    public HealthServiceImpl(HealthMapper healthMapper) {

        this.healthMapper = healthMapper;
    }

    @Override
    public HealthResponse check() {
        healthMapper.ping();
        return new HealthResponse("seckill-mall", "UP");
    }
}
