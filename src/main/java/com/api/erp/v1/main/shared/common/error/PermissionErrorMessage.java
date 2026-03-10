package com.api.erp.v1.main.shared.common.error;

import com.api.erp.v1.main.shared.domain.exception.BusinessException;
import com.api.erp.v1.main.shared.domain.exception.NotFoundException;
import org.springframework.http.HttpStatus;

/**
 * Permission-specific error messages for access control.
 * 
 * Error codes: PERMISSION_001 to PERMISSION_099
 * 
 * @author ERP System
 * @version 1.0
 */
public enum PermissionErrorMessage implements IErrorMessage {

    ACCESS_DENIED(
        "Access denied for the requested resource.",
        "PERMISSION_001",
        ErrorType.FORBIDDEN
    ),

    PERMISSION_NOT_FOUND(
        "Permission not found.",
        "PERMISSION_002",
        ErrorType.NOT_FOUND
    ),

    INVALID_PERMISSION_CONFIG(
        "Invalid permission configuration.",
        "PERMISSION_003",
        ErrorType.INVALID_ARGUMENT
    );

    private final String message;
    private final String code;
    private final ErrorType errorType;

    PermissionErrorMessage(String message, String code, ErrorType errorType) {
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
