package com.avijeet.sprout.dto;

public record UserResponseDto(
        Long id,
        String email,
        String role,
        boolean isAccountNonLocked
) { }
