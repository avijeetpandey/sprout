package com.avijeet.sprout.exceptions;

import jakarta.validation.constraints.NotBlank;

public class ProductAlreadyExistsException extends RuntimeException {
    public ProductAlreadyExistsException(String message) {
        super(message);
    }
}