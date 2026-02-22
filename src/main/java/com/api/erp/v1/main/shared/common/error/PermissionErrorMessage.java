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
        HttpStatus.FORBIDDEN
    ),

    PERMISSION_NOT_FOUND(
        "Permission not found.",
        "PERMISSION_002",
        HttpStatus.NOT_FOUND
    ),

    INVALID_PERMISSION_CONFIG(
        "Invalid permission configuration.",
        "PERMISSION_003",
        HttpStatus.BAD_REQUEST
    );

    private final String message;
    private final String code;
    private final HttpStatus status;

    PermissionErrorMessage(String message, String code, HttpStatus status) {
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
