package com.avijeet.sprout.dto;

import com.avijeet.sprout.enums.ProductType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ProductRequestDto(
        String name,
        String description,
        Double price,
        Integer stockQuantity,
        String sku,
        ProductType productType
) { }
