package com.api.erp.v1.main.shared.common.error;

/**
 * Product-specific error messages for product management operations.
 * <p>
 * Error codes: PRODUCT_001 to PRODUCT_099
 *
 * @author ERP System
 * @version 1.0
 */
public enum ProductErrorMessage implements IErrorMessage {

    PRODUCT_NOT_FOUND(
            "Product not found.",
            "PRODUCT_001",
            ErrorType.NOT_FOUND
    ),

    CODE_ALREADY_EXISTS(
            "A product with this code already exists.",
            "PRODUCT_002",
            ErrorType.CONFLICT
    ),

    CIRCULAR_COMPOSITION(
            "Circular composition is not allowed.",
            "PRODUCT_003",
            ErrorType.INTERNAL_ERROR
    ),

    PRODUCT_NOT_MANUFACTUREABLE(
            "Only products of type MANUFACTUREABLE can have composition.",
            "PRODUCT_004",
            ErrorType.INTERNAL_ERROR
    ),

    INVALID_COMPOSITION_QUANTITY(
            "Composition quantity must be greater than zero.",
            "PRODUCT_005",
            ErrorType.INTERNAL_ERROR
    ),

    PRODUCT_NOT_ACTIVE(
            "Product is not active and cannot be used.",
            "PRODUCT_006",
            ErrorType.INTERNAL_ERROR
    ),

    INVALID_PRODUCT_CODE(
            "Product code must contain only uppercase letters, numbers, hyphen, period and underscore.",
            "PRODUCT_007",
            ErrorType.INTERNAL_ERROR
    ),

    INVALID_PRODUCT_DESCRIPTION(
            "Product description is invalid or too long.",
            "PRODUCT_008",
            ErrorType.INTERNAL_ERROR
    ),

    INVALID_NCM(
            "NCM must contain exactly 8 digits.",
            "PRODUCT_009",
            ErrorType.INTERNAL_ERROR
    ),

    INVALID_PRODUCT_DATA(
            "Invalid product data provided.",
            "PRODUCT_010",
            ErrorType.INTERNAL_ERROR
    );

    private final String message;
    private final String code;
    private final ErrorType errorType;

    ProductErrorMessage(String message, String code, ErrorType status) {
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
