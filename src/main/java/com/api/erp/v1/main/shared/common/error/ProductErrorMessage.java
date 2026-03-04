package com.api.erp.v1.main.shared.common.error;

import com.api.erp.v1.main.shared.domain.exception.BusinessException;
import com.api.erp.v1.main.shared.domain.exception.NotFoundException;
import org.springframework.http.HttpStatus;

/**
 * Product-specific error messages for product management operations.
 * 
 * Error codes: PRODUCT_001 to PRODUCT_099
 * 
 * @author ERP System
 * @version 1.0
 */
public enum ProductErrorMessage implements IErrorMessage {

    PRODUCT_NOT_FOUND(
        "Product not found.",
        "PRODUCT_001",
        HttpStatus.NOT_FOUND
    ),

    CODE_ALREADY_EXISTS(
        "A product with this code already exists.",
        "PRODUCT_002",
        HttpStatus.CONFLICT
    ),

    CIRCULAR_COMPOSITION(
        "Circular composition is not allowed.",
        "PRODUCT_003",
        HttpStatus.UNPROCESSABLE_ENTITY
    ),

    PRODUCT_NOT_MANUFACTUREABLE(
        "Only products of type MANUFACTUREABLE can have composition.",
        "PRODUCT_004",
        HttpStatus.BAD_REQUEST
    ),

    INVALID_COMPOSITION_QUANTITY(
        "Composition quantity must be greater than zero.",
        "PRODUCT_005",
        HttpStatus.BAD_REQUEST
    ),

    PRODUCT_NOT_ACTIVE(
        "Product is not active and cannot be used.",
        "PRODUCT_006",
        HttpStatus.BAD_REQUEST
    ),

    INVALID_PRODUCT_CODE(
        "Product code must contain only uppercase letters, numbers, hyphen, period and underscore.",
        "PRODUCT_007",
        HttpStatus.BAD_REQUEST
    ),

    INVALID_PRODUCT_DESCRIPTION(
        "Product description is invalid or too long.",
        "PRODUCT_008",
        HttpStatus.BAD_REQUEST
    ),

    INVALID_NCM(
        "NCM must contain exactly 8 digits.",
        "PRODUCT_009",
        HttpStatus.BAD_REQUEST
    ),

    INVALID_PRODUCT_DATA(
        "Invalid product data provided.",
        "PRODUCT_010",
        HttpStatus.BAD_REQUEST
    );

    private final String message;
    private final String code;
    private final HttpStatus status;

    ProductErrorMessage(String message, String code, HttpStatus status) {
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
