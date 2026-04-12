package com.avijeet.sprout.dto;

import java.math.BigDecimal;
import java.util.List;

public record CartDto(
        Long userId,
        List<CartItemDto> items,
        BigDecimal totalAmount
) { }
