package com.avijeet.sprout.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record OrderItemRequestDto(
        @NotNull Long userId,
        @NotBlank Integer quantity
) { }
