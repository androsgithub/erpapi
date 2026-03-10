package com.api.erp.v1.main.shared.common.error;

/**
 * Common/Generic error messages used across the entire system.
 *
 * Error codes: COMMON_001 to COMMON_099
 */
public enum CommonErrorMessage implements IErrorMessage {

    DATABASE_ERROR(
            "Error accessing database. Please try again later.",
            "COMMON_001",
            ErrorType.INTERNAL_ERROR
    ),

    DATABASE_CONNECTION_FAILED(
            "Failed to connect to database.",
            "COMMON_002",
            ErrorType.SERVICE_UNAVAILABLE
    ),

    CONFIGURATION_ERROR(
            "Error in system configuration.",
            "COMMON_003",
            ErrorType.INTERNAL_ERROR
    ),

    VALIDATION_ERROR(
            "Validation error in provided data.",
            "COMMON_004",
            ErrorType.INVALID_ARGUMENT
    ),

    RESOURCE_NOT_FOUND(
            "Requested resource not found.",
            "COMMON_005",
            ErrorType.NOT_FOUND
    ),

    INVALID_REQUEST_DATA(
            "Invalid request data provided.",
            "COMMON_006",
            ErrorType.INVALID_ARGUMENT
    ),

    INTERNAL_SERVER_ERROR(
            "Internal server error. Please contact the administrator.",
            "COMMON_999",
            ErrorType.INTERNAL_ERROR
    );

    private final String message;
    private final String code;
    private final ErrorType type;

    CommonErrorMessage(String message, String code, ErrorType type) {
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