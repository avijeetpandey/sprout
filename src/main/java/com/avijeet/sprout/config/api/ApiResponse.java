package com.avijeet.sprout.config.api;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class ApiResponse <T> {
    public boolean isError;
    public String message;
    public T data;

    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(false,message,data);
    }

    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(true, message, null);
    }
}
