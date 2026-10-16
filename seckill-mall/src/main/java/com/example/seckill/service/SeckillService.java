package com.example.seckill.service;

import com.example.seckill.dto.OrderVO;
import com.example.seckill.dto.SeckillProductVO;

public interface SeckillService {
    OrderVO seckill(Long userId, Long seckillProductId);
    SeckillProductVO getById(Long id);
}
