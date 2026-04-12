package com.avijeet.sprout.dto;

import java.math.BigDecimal;

public record CartItemDto(
        Long productId,
        String productName,
        Integer quantity,
        BigDecimal price
) { }
