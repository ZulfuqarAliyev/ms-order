package com.mpay.order.mapper;

import com.mpay.order.dao.entity.OutboxEntity;
import com.mpay.order.model.dtos.OrderResponseDto;
import com.mpay.order.model.enums.OutboxStatus;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.LocalDateTime;

@Mapper(componentModel = "spring", imports = {LocalDateTime.class, OutboxStatus.class})
public interface OutboxMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "aggregateType", constant = "Order")
    @Mapping(target = "aggregateId", source = "dto.id")
    @Mapping(target = "eventType", constant = "OrderCreated")
    @Mapping(target = "payload", expression = "java(toJson(dto))")
    @Mapping(target = "status", expression = "java(OutboxStatus.PENDING)")
    @Mapping(target = "createdAt", expression = "java(LocalDateTime.now())")
    @Mapping(target = "updatedAt", expression = "java(LocalDateTime.now())")
    OutboxEntity toEntity(OrderResponseDto dto);

    default String toJson(OrderResponseDto dto) {
        try {
            return new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(dto);
        } catch (Exception e) {
            throw new RuntimeException("JSON serialization failed", e);
        }
    }
}
