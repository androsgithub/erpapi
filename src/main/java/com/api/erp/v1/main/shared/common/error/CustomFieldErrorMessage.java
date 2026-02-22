package com.api.erp.v1.main.shared.common.error;

import com.api.erp.v1.main.shared.domain.exception.BusinessException;
import com.api.erp.v1.main.shared.domain.exception.NotFoundException;
import org.springframework.http.HttpStatus;

/**
 * Custom Field specific error messages.
 * 
 * Error codes: CUSTOMFIELD_001 to CUSTOMFIELD_099
 * 
 * @author ERP System
 * @version 1.0
 */
public enum CustomFieldErrorMessage implements IErrorMessage {

    FIELD_NOT_FOUND(
        "Custom field not found.",
        "CUSTOMFIELD_001",
        HttpStatus.NOT_FOUND
    ),

    FIELD_ALREADY_REGISTERED(
        "Field already registered for this table.",
        "CUSTOMFIELD_002",
        HttpStatus.CONFLICT
    ),

    INVALID_FIELD_CONFIG(
        "Invalid field configuration provided.",
        "CUSTOMFIELD_003",
        HttpStatus.BAD_REQUEST
    ),

    TABLE_NAME_REQUIRED(
        "Table name is required.",
        "CUSTOMFIELD_004",
        HttpStatus.BAD_REQUEST
    ),

    FIELD_KEY_REQUIRED(
        "Field key is required.",
        "CUSTOMFIELD_005",
        HttpStatus.BAD_REQUEST
    ),

    FIELD_TYPE_REQUIRED(
        "Field type is required.",
        "CUSTOMFIELD_006",
        HttpStatus.BAD_REQUEST
    );

    private final String message;
    private final String code;
    private final HttpStatus status;

    CustomFieldErrorMessage(String message, String code, HttpStatus status) {
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
