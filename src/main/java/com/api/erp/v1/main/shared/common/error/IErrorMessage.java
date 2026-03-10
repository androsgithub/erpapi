package com.api.erp.v1.main.shared.common.error;

/**
 * Standard interface for domain-specific error messages.
 * <p>
 * All feature-specific ErrorMessage enums must implement this interface
 * to ensure consistent error handling across the system.
 *
 * @author ERP System
 * @version 1.0
 */
public interface IErrorMessage {

    /**
     * Gets the error message description.
     *
     * @return Descriptive error message
     */
    String getMessage();

    /**
     * Gets the unique error code.
     * Format: DOMAIN_XXX (e.g., TENANT_001, USER_002)
     *
     * @return Unique error code
     */
    String getCode();

    ErrorType getErrorType();

    /**
     * Returns formatted error string.
     * Format: [CODE] - MESSAGE
     *
     * @return Formatted error string
     */
    String toString();
}
