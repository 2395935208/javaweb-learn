package com.example.seckill.service.impl;

import com.example.seckill.common.BusinessException;
import com.example.seckill.dto.PageResult;
import com.example.seckill.dto.ProductVO;
import com.example.seckill.entity.Product;
import com.example.seckill.mapper.ProductMapper;
import com.example.seckill.service.ProductService;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class ProductServiceImpl implements ProductService {
    private final ProductMapper productMapper;

    public ProductServiceImpl(ProductMapper productMapper) {
        this.productMapper = productMapper;
    }

    @Override
    public PageResult<ProductVO> list(int page, int size) {
        long total = productMapper.count();
        // 在乘法之前转为 long，避免很大的页码使 int 偏移量溢出。
        long offset = ((long) page - 1) * size;
        if (offset >= total) {
            return new PageResult<>(List.of(), total, page, size);
        }
        List<ProductVO> list = productMapper.selectPage(size, offset).stream()
                .map(ProductVO::from)
                .toList();
        return new PageResult<>(list, total, page, size);
    }

    @Override
    public ProductVO getById(Long id) {
        Product product = productMapper.selectById(id);
        if (product == null) {
            throw new BusinessException(404, "商品不存在");
        }
        return ProductVO.from(product);
    }
}
