package com.avijeet.sprout.controllers;

import com.avijeet.sprout.config.api.ApiResponse;
import com.avijeet.sprout.config.controller.BaseController;
import com.avijeet.sprout.constants.ApiConstants;
import com.avijeet.sprout.dto.ProductRequestDto;
import com.avijeet.sprout.dto.ProductResponseDto;
import com.avijeet.sprout.services.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@Slf4j
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController extends BaseController {
    private final ProductService productService;

    @PostMapping("/add")
    public ResponseEntity<ApiResponse<ProductResponseDto>> addProduct(@RequestBody @Valid ProductRequestDto dto) {
        return ok(ApiConstants.DONE_MESSAGE, productService.addProduct(dto));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<ProductResponseDto>>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        log.info("Fetching products for page {} and size {} ", page, size);
        return ok(ApiConstants.DONE_MESSAGE, productService.getAllProducts(page, size));
    }
}
