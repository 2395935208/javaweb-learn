package com.example.seckill.service.impl;

import com.example.seckill.common.BusinessException;
import com.example.seckill.dto.OrderVO;
import com.example.seckill.dto.PageResult;
import com.example.seckill.entity.Order;
import com.example.seckill.mapper.OrderMapper;
import com.example.seckill.service.OrderService;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderServiceImpl implements OrderService {
    private static final int STATUS_UNPAID = 0;
    private static final int STATUS_PAID = 1;
    private static final int STATUS_CANCELED = 2;

    private final OrderMapper orderMapper;

    public OrderServiceImpl(OrderMapper orderMapper) {
        this.orderMapper = orderMapper;
    }

    @Override
    public OrderVO getById(Long userId, Long id) {
        return OrderVO.from(loadOwnedOrder(id, userId));
    }

    @Override
    public PageResult<OrderVO> list(Long userId, int page, int size) {
        long total = orderMapper.countByUserId(userId);
        // 在乘法之前转为 long，避免很大的页码使 int 偏移量溢出。
        long offset = ((long) page - 1) * size;
        if (offset >= total) {
            return new PageResult<>(List.of(), total, page, size);
        }
        List<OrderVO> list = orderMapper.selectPageByUserId(userId, size, offset).stream()
                .map(OrderVO::from)
                .toList();
        return new PageResult<>(list, total, page, size);
    }

    @Override
    // noRollbackFor：过期分支里"置 2"这次落库必须保留，哪怕紧接着要抛 409——
    // 否则事务回滚会撤销这次更新，订单永远停在"待支付"，下次 pay 还会重复触发同样的过期判断。
    @Transactional(rollbackFor = Exception.class, noRollbackFor = BusinessException.class)
    public OrderVO pay(Long userId, Long id) {
        Order order = loadOwnedOrder(id, userId);
        if (order.getStatus() != STATUS_UNPAID) {
            throw new BusinessException(409, "订单状态不允许支付");
        }
        LocalDateTime now = LocalDateTime.now().withNano(0);
        if (order.getPayExpireAt() != null && now.isAfter(order.getPayExpireAt())) {
            // 懒关单：还没有延迟队列（W9 再做），支付时顺手把过期订单置为已取消。
            if (orderMapper.updateStatus(id, STATUS_UNPAID, STATUS_CANCELED) == 0) {
                // 条件更新 0 行：说明校验之后、落库之前，订单已被别的请求改变状态。
                throw new BusinessException(409, "订单状态不允许支付");
            }
            throw new BusinessException(409, "订单已过期");
        }
        // 条件更新防并发：影响行数为 0 说明状态已被别的并发请求改变，不先查后改。
        if (orderMapper.updateStatus(id, STATUS_UNPAID, STATUS_PAID) == 0) {
            throw new BusinessException(409, "订单状态不允许支付");
        }
        order.setStatus(STATUS_PAID);
        return OrderVO.from(order);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public OrderVO cancel(Long userId, Long id) {
        Order order = loadOwnedOrder(id, userId);
        if (orderMapper.updateStatus(id, STATUS_UNPAID, STATUS_CANCELED) == 0) {
            throw new BusinessException(409, "订单状态不允许取消");
        }
        order.setStatus(STATUS_CANCELED);
        return OrderVO.from(order);
    }

    /** 查订单并校验归属；不存在或不是自己的订单，统一报 404，不暴露他人订单的存在。 */
    private Order loadOwnedOrder(Long id, Long userId) {
        Order order = orderMapper.selectById(id);
        if (order == null || !order.getUserId().equals(userId)) {
            throw new BusinessException(404, "订单不存在");
        }
        return order;
    }
}
