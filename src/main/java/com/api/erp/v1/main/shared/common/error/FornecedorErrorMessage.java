package com.api.erp.v1.main.shared.common.error;

import com.api.erp.v1.main.shared.domain.exception.BusinessException;

/**
 * Fornecedor (Supplier) specific error messages.
 * 
 * Error codes: FORNECEDOR_001 to FORNECEDOR_099
 * 
 * @author ERP System
 * @version 1.0
 */
public enum FornecedorErrorMessage implements IErrorMessage {

    SUPPLIER_NOT_FOUND(
        "Supplier not found.",
        "FORNECEDOR_001",
        ErrorType.NOT_FOUND
    ),

    SUPPLIER_ALREADY_REGISTERED(
        "Supplier with this document already registered.",
        "FORNECEDOR_002",
        ErrorType.CONFLICT
    ),

    INVALID_SUPPLIER_DATA(
        "Invalid supplier data provided.",
        "FORNECEDOR_003",
        ErrorType.INVALID_ARGUMENT
    );

    private final String message;
    private final String code;
    private final ErrorType errorType;

    FornecedorErrorMessage(String message, String code, ErrorType status) {
        this.message = message;
        this.code = code;
        this.errorType = status;
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
