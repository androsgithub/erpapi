package com.api.erp.v1.main.shared.common.error;

import com.api.erp.v1.main.shared.domain.exception.BusinessException;
import com.api.erp.v1.main.shared.domain.exception.NotFoundException;
import org.springframework.http.HttpStatus;

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
        HttpStatus.NOT_FOUND
    ),

    SUPPLIER_ALREADY_REGISTERED(
        "Supplier with this document already registered.",
        "FORNECEDOR_002",
        HttpStatus.CONFLICT
    ),

    INVALID_SUPPLIER_DATA(
        "Invalid supplier data provided.",
        "FORNECEDOR_003",
        HttpStatus.BAD_REQUEST
    );

    private final String message;
    private final String code;
    private final HttpStatus status;

    FornecedorErrorMessage(String message, String code, HttpStatus status) {
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
