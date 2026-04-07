package com.avijeet.sprout.config.controller;

import com.avijeet.sprout.config.api.ApiResponse;
import org.springframework.http.ResponseEntity;

public abstract class BaseController {
    protected <T>ResponseEntity<ApiResponse<T>> ok(String message, T data) {
        return ResponseEntity.ok(ApiResponse.success(message,data));
    }
}
