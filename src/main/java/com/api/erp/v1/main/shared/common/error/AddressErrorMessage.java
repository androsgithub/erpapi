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
        ErrorType.NOT_FOUND
    ),

    INVALID_ADDRESS_DATA(
        "Invalid address data provided.",
        "ADDRESS_002",
        ErrorType.INVALID_ARGUMENT
    ),

    INVALID_POSTAL_CODE(
        "Invalid postal code format.",
        "ADDRESS_003",
        ErrorType.INVALID_ARGUMENT
    );

    private final String message;
    private final String code;
    private final ErrorType errorType;

    AddressErrorMessage(String message, String code, ErrorType errorType) {
        this.message = message;
        this.code = code;
        this.errorType = errorType;
    }

    @Override
    public String getMessage() { return message; }

    @Override
    public String getCode() { return code; }

    @Override
    public ErrorType getErrorType() { return errorType; }

    @Override
    public String toString() {
        return "[" + code + "] - " + message;
    }
}
