package com.api.erp.v1.main.shared.common.error;

import com.api.erp.v1.main.shared.domain.exception.BusinessException;
import com.api.erp.v1.main.shared.domain.exception.NotFoundException;
import org.springframework.http.HttpStatus;

/**
 * Standard interface for domain-specific error messages.
 * 
 * All feature-specific ErrorMessage enums must implement this interface
 * to ensure consistent error handling across the system.
 * 
 * @author ERP System
 * @version 1.0
 */
public interface IErrorMessage {

    /**
     * Gets the error message description.
     * @return Descriptive error message
     */
    String getMessage();

    /**
     * Gets the unique error code.
     * Format: DOMAIN_XXX (e.g., TENANT_001, USER_002)
     * @return Unique error code
     */
    String getCode();

    /**
     * Gets the appropriate HTTP status for this error.
     * @return Corresponding HttpStatus
     */
    HttpStatus getStatus();

    /**
     * Creates a BusinessException based on this error.
     * 
     * @return BusinessException with error information
     */
    BusinessException toBusinessException();

    /**
     * Creates a NotFoundException for resource not found errors.
     * Only valid for 404 status errors.
     * 
     * @return NotFoundException with error message
     * @throws IllegalStateException if status is not NOT_FOUND
     */
    NotFoundException toNotFoundException();

    /**
     * Returns formatted error string.
     * Format: [CODE] - MESSAGE
     * 
     * @return Formatted error string
     */
    String toString();
}
