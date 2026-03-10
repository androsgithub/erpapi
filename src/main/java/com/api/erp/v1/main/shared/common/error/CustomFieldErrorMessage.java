package com.api.erp.v1.main.shared.common.error;

/**
 * Custom Field specific error messages.
 *
 * Error codes: CUSTOMFIELD_001 to CUSTOMFIELD_099
 */
public enum CustomFieldErrorMessage implements IErrorMessage {

    FIELD_NOT_FOUND(
            "Custom field not found.",
            "CUSTOMFIELD_001",
            ErrorType.NOT_FOUND
    ),

    FIELD_ALREADY_REGISTERED(
            "Field already registered for this table.",
            "CUSTOMFIELD_002",
            ErrorType.CONFLICT
    ),

    INVALID_FIELD_CONFIG(
            "Invalid field configuration provided.",
            "CUSTOMFIELD_003",
            ErrorType.INVALID_ARGUMENT
    ),

    TABLE_NAME_REQUIRED(
            "Table name is required.",
            "CUSTOMFIELD_004",
            ErrorType.INVALID_ARGUMENT
    ),

    FIELD_KEY_REQUIRED(
            "Field key is required.",
            "CUSTOMFIELD_005",
            ErrorType.INVALID_ARGUMENT
    ),

    FIELD_TYPE_REQUIRED(
            "Field type is required.",
            "CUSTOMFIELD_006",
            ErrorType.INVALID_ARGUMENT
    );

    private final String message;
    private final String code;
    private final ErrorType type;

    CustomFieldErrorMessage(String message, String code, ErrorType type) {
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