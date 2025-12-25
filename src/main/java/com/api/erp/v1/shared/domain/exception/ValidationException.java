package com.api.erp.v1.shared.domain.exception;

public class ValidationException extends RuntimeException {
    private final String field;

    public ValidationException(String field, String message) {
        super(message);
        this.field = field;
    }

    public String getField() {
        return field;
    }
}
