package com.api.erp.v1.main.shared.common.error;

import com.api.erp.v1.main.shared.domain.exception.BusinessException;
import com.api.erp.v1.main.shared.domain.exception.NotFoundException;
import org.springframework.http.HttpStatus;

/**
 * Unit of Measurement (Unidade de Medida) specific error messages.
 * 
 * Error codes: UNIDADE_001 to UNIDADE_099
 * 
 * @author ERP System
 * @version 1.0
 */
public enum MeasureUnitErrorMessage implements IErrorMessage {

    UNIT_NOT_FOUND(
        "Unit of measurement not found.",
        "UNIDADE_001",
        HttpStatus.NOT_FOUND
    ),

    INVALID_UNIT_CODE(
        "Invalid unit code. Must have at least 2 characters.",
        "UNIDADE_002",
        HttpStatus.BAD_REQUEST
    ),

    UNIT_CODE_ALREADY_EXISTS(
        "Unit code already exists.",
        "UNIDADE_003",
        HttpStatus.CONFLICT
    ),

    INVALID_UNIT_DATA(
        "Invalid unit of measurement data provided.",
        "UNIDADE_004",
        HttpStatus.BAD_REQUEST
    );

    private final String message;
    private final String code;
    private final HttpStatus status;

    MeasureUnitErrorMessage(String message, String code, HttpStatus status) {
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
