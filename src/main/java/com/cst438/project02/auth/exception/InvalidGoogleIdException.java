package com.cst438.project02.auth.exception;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

public class InvalidGoogleIdException extends RuntimeException{
    public InvalidGoogleIdException(String errorMessage, Throwable cause) {
        super(errorMessage);
    }
}
