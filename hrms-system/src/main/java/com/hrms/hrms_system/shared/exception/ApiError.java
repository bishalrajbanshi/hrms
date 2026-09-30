package com.hrms.hrms_system.shared.exception;

import java.time.LocalDateTime;
import java.util.Map;

public record ApiError(
        int status,
        String message,
        String path,
        LocalDateTime timestamp,
        Map<String, String> errors
) {

    public ApiError(int status, String message, String path, LocalDateTime timestamp) {
        this(status, message, path, timestamp, null);
    }
}
