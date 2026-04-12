package com.avijeet.sprout.dto;

import com.avijeet.sprout.enums.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record OrderResponseDto(
        String orderNumber,
        BigDecimal totalAmount,
        OrderStatus status,
        LocalDateTime createdAt
) {}
