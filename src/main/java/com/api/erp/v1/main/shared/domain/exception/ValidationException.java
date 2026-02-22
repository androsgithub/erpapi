package com.api.erp.v1.main.shared.domain.exception;

import org.springframework.http.HttpStatus;

/**
 * Exception thrown when business validations fail.
 * 
 * Implements DDD by representing validation failures with domain context.
 * Uses Strategy pattern to allow different validation types.
 * Follows Clean Code with single responsibility.
 */
public class ValidationException extends RuntimeException {
    private final String field;
    private final HttpStatus status;
    private final String code;

    /**
     * Basic constructor with field and message.
     * Uses BAD_REQUEST as default status.
     */
    public ValidationException(String field, String message) {
        super(message);
        this.field = field;
        this.status = HttpStatus.BAD_REQUEST;
        this.code = "VALIDATION_ERROR";
    }

    /**
     * Full constructor with HTTP status control.
     */
    public ValidationException(String field, String message, HttpStatus status) {
        super(message);
        this.field = field;
        this.status = status;
        this.code = "VALIDATION_ERROR";
    }

    /**
     * Constructor with custom error code for better identification.
     */
    public ValidationException(String field, String message, String code, HttpStatus status) {
        super(message);
        this.field = field;
        this.status = status;
        this.code = code;
    }

    public String getField() {
        return field;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public String getCode() {
        return code;
    }
}
