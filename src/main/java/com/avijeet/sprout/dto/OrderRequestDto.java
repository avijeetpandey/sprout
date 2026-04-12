package com.avijeet.sprout.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record OrderRequestDto(
        @NotNull Long userId,
        @NotBlank String email,
        @NotNull Long shippingAddressId,
        String paymentMethod
) { }
