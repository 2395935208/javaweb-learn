package com.example.seckill.service;

import com.example.seckill.dto.OrderVO;
import com.example.seckill.dto.PageResult;

public interface OrderService {
    OrderVO getById(Long userId, Long id);

    PageResult<OrderVO> list(Long userId, int page, int size);

    OrderVO pay(Long userId, Long id);

    OrderVO cancel(Long userId, Long id);
}
