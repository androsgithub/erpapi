package com.api.erp.v1.main.shared.common.error;

/**
 * Contact-specific error messages for contact management.
 *
 * Error codes: CONTACT_001 to CONTACT_099
 */
public enum ContactErrorMessage implements IErrorMessage {

    CONTACT_NOT_FOUND(
            "Contact not found.",
            "CONTACT_001",
            ErrorType.NOT_FOUND
    ),

    INVALID_CONTACT_TYPE(
            "Invalid contact type.",
            "CONTACT_002",
            ErrorType.INVALID_ARGUMENT
    ),

    INVALID_CONTACT_VALUE(
            "Invalid contact value. Maximum 255 characters allowed.",
            "CONTACT_003",
            ErrorType.INVALID_ARGUMENT
    ),

    INVALID_CONTACT_DESCRIPTION(
            "Invalid contact description. Maximum 255 characters allowed.",
            "CONTACT_004",
            ErrorType.INVALID_ARGUMENT
    );

    private final String message;
    private final String code;
    private final ErrorType type;

    ContactErrorMessage(String message, String code, ErrorType type) {
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