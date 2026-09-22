package com.hrms.hrms_system.shared.response;

import java.time.LocalDateTime;

public record ApiResponse<T>(
        int status,
        boolean success,
        String message,
        T data,
        LocalDateTime timestamp
) {

    public static <T> ApiResponse<T> success(
            int status,
            String message,
            T data
    ) {
        return new ApiResponse<>(
                status,
                true,
                message,
                data,
                LocalDateTime.now()
        );
    }

    public static <T> ApiResponse<T> success(
            int status,
            String message
    ) {
        return new ApiResponse<>(
                status,
                true,
                message,
                null,
                LocalDateTime.now()
        );
    }
}