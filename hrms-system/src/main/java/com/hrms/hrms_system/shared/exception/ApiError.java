package com.hrms.hrms_system.shared.exception;

import java.time.LocalDateTime;

public record ApiError(
        int status,
        String message,
        String path,
        LocalDateTime timestamp
) {
}