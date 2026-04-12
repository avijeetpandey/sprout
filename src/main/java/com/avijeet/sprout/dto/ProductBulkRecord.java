package com.avijeet.sprout.dto;

public record ProductBulkRecord(
        String name,
        String description,
        Double price,
        Integer stockQuantity,
        String sku,
        String productType
) { }
