package com.api.erp.v1.main.shared.common.error;

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
        ErrorType.NOT_FOUND
    ),

    INVALID_UNIT_CODE(
        "Invalid unit code. Must have at least 2 characters.",
        "UNIDADE_002",
        ErrorType.INVALID_ARGUMENT
    ),

    UNIT_CODE_ALREADY_EXISTS(
        "Unit code already exists.",
        "UNIDADE_003",
        ErrorType.CONFLICT
    ),

    INVALID_UNIT_DATA(
        "Invalid unit of measurement data provided.",
        "UNIDADE_004",
        ErrorType.INVALID_ARGUMENT
    );

    private final String message;
    private final String code;
    private final ErrorType errorType;

    MeasureUnitErrorMessage(String message, String code, ErrorType status) {
        this.message = message;
        this.code = code;
        this.errorType = status;
    }

    @Override
    public String getMessage() { return message; }

    @Override
    public String getCode() { return code; }

    public ErrorType getErrorType() { return errorType; }

    @Override
    public String toString() {
        return "[" + code + "] - " + message;
    }
}
