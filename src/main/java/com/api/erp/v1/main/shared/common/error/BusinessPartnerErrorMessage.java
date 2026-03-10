package com.api.erp.v1.main.shared.common.error;

import com.api.erp.v1.main.shared.domain.exception.BusinessException;
import com.api.erp.v1.main.shared.domain.exception.NotFoundException;

/**
 * BusinessPartner-specific error messages for businesspartner management.
 *
 * Error codes: BUSINESSPARTNER_001 to BUSINESSPARTNER_099
 */
public enum BusinessPartnerErrorMessage implements IErrorMessage {

    BUSINESSPARTNER_NOT_FOUND(
            "BusinessPartner not found.",
            "BUSINESSPARTNER_001",
            ErrorType.NOT_FOUND
    ),

    INVALID_BUSINESSPARTNER_DATA(
            "Invalid businesspartner data provided.",
            "BUSINESSPARTNER_002",
            ErrorType.INVALID_ARGUMENT
    ),

    BUSINESSPARTNER_ALREADY_EXISTS(
            "BusinessPartner with this document already exists.",
            "BUSINESSPARTNER_003",
            ErrorType.CONFLICT
    );

    private final String message;
    private final String code;
    private final ErrorType type;

    BusinessPartnerErrorMessage(String message, String code, ErrorType type) {
        this.message = message;
        this.code = code;
        this.type = type;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public ErrorType getErrorType() {
        return type;
    }

    @Override
    public String toString() {
        return "[" + code + "] - " + message;
    }
}