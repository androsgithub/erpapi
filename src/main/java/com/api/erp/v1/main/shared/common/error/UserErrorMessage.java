package com.api.erp.v1.main.shared.common.error;

/**
 * User-specific error messages for authentication and user management.
 * <p>
 * Error codes: USER_001 to USER_099
 *
 * @author ERP System
 * @version 1.0
 */
public enum UserErrorMessage implements IErrorMessage {

    USER_NOT_FOUND(
            "User not found.",
            "USER_001",
            ErrorType.NOT_FOUND
    ),

    EMAIL_ALREADY_REGISTERED(
            "Email already registered in the system.",
            "USER_002",
            ErrorType.CONFLICT
    ),

    CPF_ALREADY_REGISTERED(
            "CPF already registered in the system.",
            "USER_003",
            ErrorType.CONFLICT
    ),

    INVALID_EMAIL_OR_PASSWORD(
            "Invalid email or password.",
            "USER_004",
            ErrorType.UNAUTHORIZED
    ),

    PASSWORD_REQUIREMENTS_NOT_MET(
            "Password must contain uppercase, lowercase, numbers and special characters.",
            "USER_005",
            ErrorType.INVALID_ARGUMENT
    ),

    PERMISSION_NOT_FOUND(
            "Permission not found.",
            "USER_006",
            ErrorType.NOT_FOUND
    ),

    ROLE_NOT_FOUND(
            "Role not found.",
            "USER_007",
            ErrorType.NOT_FOUND
    ),

    ROLE_NOT_LINKED_TO_USER(
            "Role not linked to user.",
            "USER_008",
            ErrorType.NOT_FOUND
    ),

    USER_NOT_PENDING_APPROVAL(
            "User is not pending approval.",
            "USER_009",
            ErrorType.INVALID_ARGUMENT
    ),

    TENANT_DOES_NOT_REQUIRE_APPROVAL(
            "Tenant does not require manager approval.",
            "USER_010",
            ErrorType.INVALID_ARGUMENT
    ),

    INVALID_NAME(
            "Name is required and must have at least 3 characters.",
            "USER_011",
            ErrorType.INVALID_ARGUMENT
    ),

    INVALID_PASSWORD_LENGTH(
            "Password must have at least 8 characters.",
            "USER_012",
            ErrorType.INVALID_ARGUMENT
    );

    private final String message;
    private final String code;
    private final ErrorType errorType;

    UserErrorMessage(String message, String code, ErrorType status) {
        this.message = message;
        this.code = code;
        this.errorType = status;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public String getCode() {
        return code;
    }

    public ErrorType getErrorType() {
        return errorType;
    }

    @Override
    public String toString() {
        return "[" + code + "] - " + message;
    }
}
