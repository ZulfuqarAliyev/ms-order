package com.mpay.order.model.dtos;

import com.mpay.order.model.enums.OrderStatus;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderResponseDto {
    private String id;
    private String name;
    private int count;
    private BigDecimal price;
    private OrderStatus status;
}
