package com.api.erp.v1.main.shared.common.error;

import com.api.erp.v1.main.shared.domain.exception.BusinessException;
import com.api.erp.v1.main.shared.domain.exception.NotFoundException;
import org.springframework.http.HttpStatus;

/**
 * Common/Generic error messages used across the entire system.
 * 
 * Error codes: COMMON_001 to COMMON_099
 * 
 * @author ERP System
 * @version 1.0
 */
public enum CommonErrorMessage implements IErrorMessage {

    DATABASE_ERROR(
        "Error accessing database. Please try again later.",
        "COMMON_001",
        HttpStatus.INTERNAL_SERVER_ERROR
    ),

    DATABASE_CONNECTION_FAILED(
        "Failed to connect to database.",
        "COMMON_002",
        HttpStatus.SERVICE_UNAVAILABLE
    ),

    CONFIGURATION_ERROR(
        "Error in system configuration.",
        "COMMON_003",
        HttpStatus.INTERNAL_SERVER_ERROR
    ),

    INTERNAL_SERVER_ERROR(
        "Internal server error. Please contact the administrator.",
        "COMMON_999",
        HttpStatus.INTERNAL_SERVER_ERROR
    ),

    VALIDATION_ERROR(
        "Validation error in provided data.",
        "COMMON_004",
        HttpStatus.BAD_REQUEST
    ),

    RESOURCE_NOT_FOUND(
        "Requested resource not found.",
        "COMMON_005",
        HttpStatus.NOT_FOUND
    ),

    INVALID_REQUEST_DATA(
        "Invalid request data provided.",
        "COMMON_006",
        HttpStatus.BAD_REQUEST
    );

    private final String message;
    private final String code;
    private final HttpStatus status;

    CommonErrorMessage(String message, String code, HttpStatus status) {
        this.message = message;
        this.code = code;
        this.status = status;
    }

    @Override
    public String getMessage() { return message; }

    @Override
    public String getCode() { return code; }

    @Override
    public HttpStatus getStatus() { return status; }

    @Override
    public BusinessException toBusinessException() {
        return new BusinessException(this.status, this.message);
    }

    @Override
    public NotFoundException toNotFoundException() {
        if (!this.status.equals(HttpStatus.NOT_FOUND)) {
            throw new IllegalStateException("This error is not of type NOT_FOUND: " + this.code);
        }
        return new NotFoundException(this.message);
    }

    @Override
    public String toString() {
        return "[" + code + "] - " + message;
    }
}
