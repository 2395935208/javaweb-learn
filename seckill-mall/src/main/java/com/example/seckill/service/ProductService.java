package com.example.seckill.service;

import com.example.seckill.dto.PageResult;
import com.example.seckill.dto.ProductVO;

public interface ProductService {
    PageResult<ProductVO> list(int page, int size);
    ProductVO getById(Long id);
}
