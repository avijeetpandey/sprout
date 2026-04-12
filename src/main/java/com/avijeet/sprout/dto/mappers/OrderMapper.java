package com.avijeet.sprout.dto.mappers;

import com.avijeet.sprout.dto.OrderItemResponseDto;
import com.avijeet.sprout.dto.OrderRequestDto;
import com.avijeet.sprout.dto.OrderResponseDto;
import com.avijeet.sprout.entities.Order;
import com.avijeet.sprout.entities.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderMapper {
    OrderResponseDto toResponseDto(Order order);
    OrderItemResponseDto toItemResponseDto(OrderItem orderItem);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "items", ignore = true)
    @Mapping(target = "totalAmount", ignore = true)
    @Mapping(target = "status", constant = "PENDING")
    Order toEntity(OrderRequestDto dto);
}
