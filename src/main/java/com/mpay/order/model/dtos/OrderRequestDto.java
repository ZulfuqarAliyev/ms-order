package com.mpay.order.model.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderRequestDto {
    @NotBlank
    private String name;

    @Positive
    private int count;

    @Positive
    private BigDecimal price;
}
