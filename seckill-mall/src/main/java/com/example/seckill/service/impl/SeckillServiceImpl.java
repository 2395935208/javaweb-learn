package com.example.seckill.service.impl;

import com.example.seckill.common.BusinessException;
import com.example.seckill.dto.OrderVO;
import com.example.seckill.dto.SeckillProductVO;
import com.example.seckill.entity.Order;
import com.example.seckill.entity.Product;
import com.example.seckill.entity.SeckillProduct;
import com.example.seckill.mapper.OrderMapper;
import com.example.seckill.mapper.ProductMapper;
import com.example.seckill.mapper.SeckillProductMapper;
import com.example.seckill.service.SeckillService;
import java.time.LocalDateTime;
import java.util.UUID;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SeckillServiceImpl implements SeckillService {
    private static final int STATUS_UNPAID = 0;
    private static final long PAY_EXPIRE_MINUTES = 15;

    /** seckill_product.status：人工维护的场次开关，0未开始 1进行中 2已结束。 */
    private static final int SECKILL_ONGOING = 1;

    private final SeckillProductMapper seckillProductMapper;
    private final ProductMapper productMapper;
    private final OrderMapper orderMapper;

    public SeckillServiceImpl(SeckillProductMapper seckillProductMapper, ProductMapper productMapper,
            OrderMapper orderMapper) {
        this.seckillProductMapper = seckillProductMapper;
        this.productMapper = productMapper;
        this.orderMapper = orderMapper;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public OrderVO seckill(Long userId, Long seckillProductId) {
        // 1. 先查秒杀商品并校验场次，不满足直接拒绝，避免无谓的库存扣减。
        SeckillProduct seckillProduct = seckillProductMapper.selectById(seckillProductId);
        if (seckillProduct == null) {
            throw new BusinessException(404, "秒杀商品不存在");
        }
        LocalDateTime now = LocalDateTime.now().withNano(0);
        // 状态是人工在后台维护的场次开关：非 1 一律不放行。
        Integer status = seckillProduct.getStatus();
        if (status == null || status != SECKILL_ONGOING) {
            throw new BusinessException(400, status != null && status > SECKILL_ONGOING ? "秒杀已结束" : "秒杀未开始");
        }
        if (seckillProduct.getStartTime() != null && now.isBefore(seckillProduct.getStartTime())) {
            throw new BusinessException(400, "秒杀未开始");
        }
        if (seckillProduct.getEndTime() != null && now.isAfter(seckillProduct.getEndTime())) {
            throw new BusinessException(400, "秒杀已结束");
        }

        // 2. 校内校验通过后单条 SQL 原子扣库存：不 select-then-update，避免并发超卖。
        if (seckillProductMapper.decreaseStock(seckillProductId) == 0) {
            throw new BusinessException(409, "库存不足");
        }

        // 3. 创建订单，order.product_id 存 seckill_product.id。
        Order order = new Order();
        order.setOrderNo(UUID.randomUUID().toString().replace("-", ""));
        order.setUserId(userId);
        order.setProductId(seckillProductId);
        order.setSeckillPrice(seckillProduct.getSeckillPrice());
        order.setStatus(STATUS_UNPAID);
        order.setPayExpireAt(now.plusMinutes(PAY_EXPIRE_MINUTES));
        order.setCreatedAt(now);
        try {
            orderMapper.insert(order);
        } catch (DuplicateKeyException exception) {
            // 唯一约束 uk_user_product 是防一人多单的最后一道门；抛异常使事务回滚，库存扣减随之撤销。
            throw new BusinessException(409, "已参与过秒杀");
        }
        return OrderVO.from(order);
    }

    @Override
    public SeckillProductVO getById(Long id) {
        SeckillProduct seckillProduct = seckillProductMapper.selectById(id);
        if (seckillProduct == null) {
            throw new BusinessException(404, "秒杀商品不存在");
        }
        Product product = productMapper.selectById(seckillProduct.getProductId());
        return SeckillProductVO.from(seckillProduct, product == null ? null : product.getName());
    }
}
