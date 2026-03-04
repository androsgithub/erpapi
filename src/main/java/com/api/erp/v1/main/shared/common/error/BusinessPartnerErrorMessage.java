package com.api.erp.v1.main.shared.common.error;

import com.api.erp.v1.main.shared.domain.exception.BusinessException;
import com.api.erp.v1.main.shared.domain.exception.NotFoundException;
import org.springframework.http.HttpStatus;

/**
 * BusinessPartner-specific error messages for businesspartner management.
 * 
 * Error codes: BUSINESSPARTNER_001 to BUSINESSPARTNER_099
 * 
 * @author ERP System
 * @version 1.0
 */
public enum BusinessPartnerErrorMessage implements IErrorMessage {

    BUSINESSPARTNER_NOT_FOUND(
        "BusinessPartner not found.",
        "BUSINESSPARTNER_001",
        HttpStatus.NOT_FOUND
    ),

    INVALID_BUSINESSPARTNER_DATA(
        "Invalid businesspartner data provided.",
        "BUSINESSPARTNER_002",
        HttpStatus.BAD_REQUEST
    ),

    BUSINESSPARTNER_ALREADY_EXISTS(
        "BusinessPartner with this document already exists.",
        "BUSINESSPARTNER_003",
        HttpStatus.CONFLICT
    );

    private final String message;
    private final String code;
    private final HttpStatus status;

    BusinessPartnerErrorMessage(String message, String code, HttpStatus status) {
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
