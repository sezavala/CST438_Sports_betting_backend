package com.cst438.project02.auth.exception;

import com.cst438.project02.auth.exception.InvalidGoogleIdException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(InvalidGoogleIdException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public Map<String, Object> handleInvalidId(InvalidGoogleIdException ex) {
        return Map.of(
                "error", "invalid_token",
                "message", ex.getMessage()
        );
    }
}
