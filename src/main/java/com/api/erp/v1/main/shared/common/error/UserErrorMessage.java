package com.api.erp.v1.main.shared.common.error;

import com.api.erp.v1.main.shared.domain.exception.BusinessException;
import com.api.erp.v1.main.shared.domain.exception.NotFoundException;
import org.springframework.http.HttpStatus;

/**
 * User-specific error messages for authentication and user management.
 * 
 * Error codes: USER_001 to USER_099
 * 
 * @author ERP System
 * @version 1.0
 */
public enum UserErrorMessage implements IErrorMessage {

    USER_NOT_FOUND(
        "User not found.",
        "USER_001",
        HttpStatus.NOT_FOUND
    ),

    EMAIL_ALREADY_REGISTERED(
        "Email already registered in the system.",
        "USER_002",
        HttpStatus.CONFLICT
    ),

    CPF_ALREADY_REGISTERED(
        "CPF already registered in the system.",
        "USER_003",
        HttpStatus.CONFLICT
    ),

    INVALID_EMAIL_OR_PASSWORD(
        "Invalid email or password.",
        "USER_004",
        HttpStatus.UNAUTHORIZED
    ),

    PASSWORD_REQUIREMENTS_NOT_MET(
        "Password must contain uppercase, lowercase, numbers and special characters.",
        "USER_005",
        HttpStatus.BAD_REQUEST
    ),

    PERMISSION_NOT_FOUND(
        "Permission not found.",
        "USER_006",
        HttpStatus.NOT_FOUND
    ),

    ROLE_NOT_FOUND(
        "Role not found.",
        "USER_007",
        HttpStatus.NOT_FOUND
    ),

    ROLE_NOT_LINKED_TO_USER(
        "Role not linked to user.",
        "USER_008",
        HttpStatus.NOT_FOUND
    ),

    USER_NOT_PENDING_APPROVAL(
        "User is not pending approval.",
        "USER_009",
        HttpStatus.BAD_REQUEST
    ),

    TENANT_DOES_NOT_REQUIRE_APPROVAL(
        "Tenant does not require manager approval.",
        "USER_010",
        HttpStatus.BAD_REQUEST
    ),

    INVALID_NAME(
        "Name is required and must have at least 3 characters.",
        "USER_011",
        HttpStatus.BAD_REQUEST
    ),

    INVALID_PASSWORD_LENGTH(
        "Password must have at least 8 characters.",
        "USER_012",
        HttpStatus.BAD_REQUEST
    );

    private final String message;
    private final String code;
    private final HttpStatus status;

    UserErrorMessage(String message, String code, HttpStatus status) {
        this.message = message;
        this.code = code;
        this.status = status;
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
    public HttpStatus getStatus() {
        return status;
    }

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
