package com.api.erp.v1.main.shared.common.error;

import com.api.erp.v1.main.shared.domain.exception.BusinessException;
import com.api.erp.v1.main.shared.domain.exception.NotFoundException;
import org.springframework.http.HttpStatus;

/**
 * Address-specific error messages for address management.
 * 
 * Error codes: ADDRESS_001 to ADDRESS_099
 * 
 * @author ERP System
 * @version 1.0
 */
public enum AddressErrorMessage implements IErrorMessage {

    ADDRESS_NOT_FOUND(
        "Address not found.",
        "ADDRESS_001",
        HttpStatus.NOT_FOUND
    ),

    INVALID_ADDRESS_DATA(
        "Invalid address data provided.",
        "ADDRESS_002",
        HttpStatus.BAD_REQUEST
    ),

    INVALID_POSTAL_CODE(
        "Invalid postal code format.",
        "ADDRESS_003",
        HttpStatus.BAD_REQUEST
    );

    private final String message;
    private final String code;
    private final HttpStatus status;

    AddressErrorMessage(String message, String code, HttpStatus status) {
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
