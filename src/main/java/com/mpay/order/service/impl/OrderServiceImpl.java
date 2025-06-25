package com.mpay.order.service.impl;

import com.mpay.order.dao.entity.OrderEntity;
import com.mpay.order.dao.repository.OrderRepository;
import com.mpay.order.dao.repository.OutboxRepository;
import com.mpay.order.mapper.OrderMapper;
import com.mpay.order.mapper.OutboxMapper;
import com.mpay.order.model.dtos.OrderRequestDto;
import com.mpay.order.model.dtos.OrderResponseDto;
import com.mpay.order.model.enums.OrderStatus;
import com.mpay.order.service.OrderService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final OutboxRepository outboxRepository;
    private final OutboxMapper outboxMapper;

    @Override

    @Transactional
    public void createOrder(OrderRequestDto orderRequestDto) {
        OrderEntity orderEntity = orderMapper.toEntity(orderRequestDto);
        orderEntity.setStatus(OrderStatus.PENDING);
        System.out.println(orderEntity);
        orderRepository.save(orderEntity);
        var orderResponseDto = orderMapper.toDto(orderEntity);
        var outboxEntity = outboxMapper.toEntity(orderResponseDto);
        System.out.println(outboxEntity);
        outboxRepository.save(outboxEntity);
    }

    @Override
    @Cacheable(value = "orders", key = "#id")
    public OrderResponseDto getOrderById(String id) {
        System.out.println("Fetching from db for id: " + id);
        OrderEntity orderEntity = orderRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + id));
        return orderMapper.toDto(orderEntity);
    }
}
