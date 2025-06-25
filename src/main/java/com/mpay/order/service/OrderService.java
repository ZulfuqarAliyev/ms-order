package com.mpay.order.service;

import com.mpay.order.model.dtos.OrderRequestDto;
import com.mpay.order.model.dtos.OrderResponseDto;

public interface OrderService {
    void createOrder(OrderRequestDto orderRequestDto);
    OrderResponseDto getOrderById(String id);
}
