package com.api.erp.v1.main.shared.common.error;

import com.api.erp.v1.main.shared.domain.exception.BusinessException;
import com.api.erp.v1.main.shared.domain.exception.NotFoundException;
import org.springframework.http.HttpStatus;

/**
 * Contact-specific error messages for contact management.
 * 
 * Error codes: CONTACT_001 to CONTACT_099
 * 
 * @author ERP System
 * @version 1.0
 */
public enum ContactErrorMessage implements IErrorMessage {

    CONTACT_NOT_FOUND(
        "Contact not found.",
        "CONTACT_001",
        HttpStatus.NOT_FOUND
    ),

    INVALID_CONTACT_TYPE(
        "Invalid contact type.",
        "CONTACT_002",
        HttpStatus.BAD_REQUEST
    ),

    INVALID_CONTACT_VALUE(
        "Invalid contact value. Maximum 255 characters allowed.",
        "CONTACT_003",
        HttpStatus.BAD_REQUEST
    ),

    INVALID_CONTACT_DESCRIPTION(
        "Invalid contact description. Maximum 255 characters allowed.",
        "CONTACT_004",
        HttpStatus.BAD_REQUEST
    );

    private final String message;
    private final String code;
    private final HttpStatus status;

    ContactErrorMessage(String message, String code, HttpStatus status) {
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
