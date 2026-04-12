package com.avijeet.sprout.services;

import com.avijeet.sprout.dto.CartDto;
import com.avijeet.sprout.dto.CartItemDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CartService {
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    public void addToCart(Long userId, CartItemDto item) {
        String key = "cart:" + userId;

        // 1. Get raw object to avoid casting/typing issues
        Object rawCart = redisTemplate.opsForValue().get(key);

        CartDto cart = null;
        if (rawCart != null) {
            // 2. Explicit conversion
            cart = objectMapper.convertValue(rawCart, CartDto.class);
        }

        List<CartItemDto> items = (cart == null) ? new ArrayList<>() : new ArrayList<>(cart.items());
        items.add(item);

        BigDecimal total = items.stream()
                .map(i -> i.price().multiply(BigDecimal.valueOf(i.quantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        redisTemplate.opsForValue().set(key, new CartDto(userId, items, total));
    }
}
