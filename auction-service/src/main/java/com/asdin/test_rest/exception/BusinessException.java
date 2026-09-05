package com.asdin.test_rest.exception;

import org.springframework.http.HttpStatus;

/** Expected business-rule violation with an HTTP status. */
public class BusinessException extends RuntimeException {
    public final HttpStatus status;

    public BusinessException(HttpStatus status, String message) {
        super(message);
        this.status = status;
    }
}
