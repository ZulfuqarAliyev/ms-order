package com.mpay.order.controller;

import com.mpay.order.model.dtos.OrderRequestDto;
import com.mpay.order.model.dtos.OrderResponseDto;
import com.mpay.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public void createOrder(@RequestBody @Validated OrderRequestDto orderRequestDto) {
        orderService.createOrder(orderRequestDto);
    }

    @GetMapping("/{id}")
    public OrderResponseDto getOrderById(@PathVariable String id) {
        return orderService.getOrderById(id);
    }
}
