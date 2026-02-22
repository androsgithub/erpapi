package com.api.erp.v1.main.shared.common.error;

import com.api.erp.v1.main.shared.domain.exception.BusinessException;
import com.api.erp.v1.main.shared.domain.exception.NotFoundException;
import org.springframework.http.HttpStatus;

/**
 * Customer-specific error messages for customer management.
 * 
 * Error codes: CUSTOMER_001 to CUSTOMER_099
 * 
 * @author ERP System
 * @version 1.0
 */
public enum CustomerErrorMessage implements IErrorMessage {

    CUSTOMER_NOT_FOUND(
        "Customer not found.",
        "CUSTOMER_001",
        HttpStatus.NOT_FOUND
    ),

    INVALID_CUSTOMER_DATA(
        "Invalid customer data provided.",
        "CUSTOMER_002",
        HttpStatus.BAD_REQUEST
    ),

    CUSTOMER_ALREADY_EXISTS(
        "Customer with this document already exists.",
        "CUSTOMER_003",
        HttpStatus.CONFLICT
    );

    private final String message;
    private final String code;
    private final HttpStatus status;

    CustomerErrorMessage(String message, String code, HttpStatus status) {
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
