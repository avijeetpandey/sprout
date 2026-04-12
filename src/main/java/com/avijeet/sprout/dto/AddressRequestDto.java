package com.avijeet.sprout.dto;

import com.avijeet.sprout.enums.AddressType;

public record AddressRequestDto(
        String street,
        String city,
        String zipCode,
        String country,
        AddressType addressType
) {
}
