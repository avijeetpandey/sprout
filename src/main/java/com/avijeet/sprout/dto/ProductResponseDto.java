package com.avijeet.sprout.dto;

import com.avijeet.sprout.enums.ProductType;

public record ProductResponseDto(
      Long id,
      String name,
      String description,
      String sku,
      ProductType productType,
      Double price,
      Integer stockQuantity
) { }
