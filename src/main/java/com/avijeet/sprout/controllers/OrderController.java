package com.avijeet.sprout.controllers;

import com.avijeet.sprout.config.api.ApiResponse;
import com.avijeet.sprout.config.controller.BaseController;
import com.avijeet.sprout.dto.CartItemDto;
import com.avijeet.sprout.dto.OrderRequestDto;
import com.avijeet.sprout.dto.OrderResponseDto;
import com.avijeet.sprout.services.CartService;
import com.avijeet.sprout.services.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController extends BaseController {
    private final OrderService orderService;
    private final CartService cartService;

    @PostMapping("/cart/add/{userId}")
    public ResponseEntity<ApiResponse<String>> addToCart(@PathVariable Long userId, @RequestBody CartItemDto item) {
        cartService.addToCart(userId, item);
        return ok("Item added to cart", "SUCCESS");
    }

    @PostMapping("/checkout")
    public ResponseEntity<ApiResponse<OrderResponseDto>> checkout(@RequestBody OrderRequestDto request) {
        return ok("Order placed successfully", orderService.placeOrder(request));
    }

    @GetMapping("/track/{orderNumber}")
    public ResponseEntity<ApiResponse<OrderResponseDto>> track(@PathVariable String orderNumber) {
        return ok("Order details fetched", orderService.trackOrder(orderNumber));
    }
}
