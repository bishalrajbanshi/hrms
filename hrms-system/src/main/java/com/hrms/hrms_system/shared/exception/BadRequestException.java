package com.hrms.hrms_system.shared.exception;

public class BadRequestException extends RuntimeException {
    
    public BadRequestException(String message){
        super(message);
    }

}
