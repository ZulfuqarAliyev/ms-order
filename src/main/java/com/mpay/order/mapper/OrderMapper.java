package com.mpay.order.mapper;

import com.mpay.order.dao.entity.OrderEntity;
import com.mpay.order.model.dtos.OrderRequestDto;
import com.mpay.order.model.dtos.OrderResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface OrderMapper {
    OrderMapper INSTANCE = Mappers.getMapper(OrderMapper.class);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    OrderEntity toEntity(OrderRequestDto dto);

    @Mapping(target = "id", expression = "java(entity.getId().toString())")
    OrderResponseDto toDto(OrderEntity entity);
}

