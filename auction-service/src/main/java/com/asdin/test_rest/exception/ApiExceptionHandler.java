package com.asdin.test_rest.exception;

import org.springframework.http.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import java.time.Instant;
import java.util.*;

/** Consistent JSON errors without leaking implementation details. */
@RestControllerAdvice
public class ApiExceptionHandler {
    @ExceptionHandler(BusinessException.class)
    ResponseEntity<Map<String, Object>> business(BusinessException e) {
        return response(e.status, e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<Map<String, Object>> invalid(MethodArgumentNotValidException e) {
        String msg = e.getBindingResult().getFieldErrors().stream().findFirst()
                .map(x -> x.getField() + ": " + x.getDefaultMessage()).orElse("Invalid request");
        return response(HttpStatus.BAD_REQUEST, msg);
    }

    @ExceptionHandler(Exception.class)
    ResponseEntity<Map<String, Object>> generic(Exception e) {
        return response(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected server error");
    }

    private ResponseEntity<Map<String, Object>> response(HttpStatus s, String m) {
        return ResponseEntity.status(s)
                .body(Map.of("timestamp", Instant.now().toString(), "status", s.value(), "message", m));
    }
}
