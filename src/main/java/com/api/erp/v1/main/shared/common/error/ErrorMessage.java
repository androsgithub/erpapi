package com.api.erp.v1.main.shared.common.error;

import com.api.erp.v1.main.shared.domain.exception.BusinessException;
import com.api.erp.v1.main.shared.domain.exception.NotFoundException;
import org.springframework.http.HttpStatus;

/**
 * Centralized enum for generic error messages in the ERP system.
 * 
 * DEPRECATED: Use specific feature enums instead:
 * - TenantErrorMessage for tenant-related errors
 * - UserErrorMessage for user-related errors
 * - ProductErrorMessage for product errors
 * - etc.
 * 
 * This enum now implements IErrorMessage and serves as a fallback
 * for truly generic system errors only.
 * 
 * Defines the main error categories with their respective:
 * - Unique Code: Error identification (ex: COMMON_001)
 * - Message: Clear error description
 * - HTTP Status: Appropriate HTTP response
 * 
 * @author ERP System
 * @version 2.0
 * @deprecated Use specific ErrorMessage enums by feature instead
 */
@Deprecated(since = "2.0", forRemoval = false)
public enum ErrorMessage implements IErrorMessage {

    // Datasource/Tenant Errors - DEPRECATED: Use TenantErrorMessage instead
    @Deprecated(since = "2.0", forRemoval = false)
    DATASOURCE_NOT_FOUND("Datasource not found for the specified tenant.", "COMMON_001", HttpStatus.SERVICE_UNAVAILABLE),
    
    // Tenant Errors - DEPRECATED: Use TenantErrorMessage instead
    @Deprecated(since = "2.0", forRemoval = false)
    TENANT_NOT_EXISTS("Tenant does not exist or is not active.", "COMMON_002", HttpStatus.NOT_FOUND),
    
    // Database Errors - Use CommonErrorMessage instead
    @Deprecated(since = "2.0", forRemoval = false)
    DATABASE_ERROR("Error accessing database. Please try again later.", "COMMON_003", HttpStatus.INTERNAL_SERVER_ERROR),
    
    @Deprecated(since = "2.0", forRemoval = false)
    DATABASE_CONNECTION_FAILED("Failed to connect to database.", "COMMON_004", HttpStatus.SERVICE_UNAVAILABLE),
    
    // Configuration Errors - Use CommonErrorMessage instead
    @Deprecated(since = "2.0", forRemoval = false)
    CONFIGURATION_ERROR("Error in system configuration.", "COMMON_005", HttpStatus.INTERNAL_SERVER_ERROR),
    
    // Generic Errors - Use CommonErrorMessage instead
    @Deprecated(since = "2.0", forRemoval = false)
    INTERNAL_SERVER_ERROR("Internal server error. Please contact the administrator.", "COMMON_999", HttpStatus.INTERNAL_SERVER_ERROR);

    private final String message;
    private final String code;
    private final HttpStatus status;

    ErrorMessage(String message, String code, HttpStatus status) {
        this.message = message;
        this.code = code;
        this.status = status;
    }

    /**
     * Gets the error message.
     * @return Descriptive error message
     */
    @Override
    public String getMessage() {
        return message;
    }

    /**
     * Gets the unique error code.
     * @return Error code (ex: COMMON_001)
     */
    @Override
    public String getCode() {
        return code;
    }

    /**
     * Gets the appropriate HTTP status for the error.
     * @return Corresponding HttpStatus
     */
    @Override
    public HttpStatus getStatus() {
        return status;
    }

    /**
     * Creates a BusinessException based on this error.
     * Uses the HTTP status defined in the enum.
     * 
     * @return BusinessException with error context
     */
    @Override
    public BusinessException toBusinessException() {
        return new BusinessException(this.status, this.message);
    }

    /**
     * Creates a NotFoundException for resource not found errors.
     * Used for DATASOURCE_NOT_FOUND and TENANT_NOT_EXISTS.
     * 
     * @return NotFoundException with error message
     * @throws IllegalStateException if the enum is not NOT_FOUND
     */
    public NotFoundException toNotFoundException() {
        if (!this.status.equals(HttpStatus.NOT_FOUND)) {
            throw new IllegalStateException("This error is not of type NOT_FOUND: " + this.code);
        }
        return new NotFoundException(this.message);
    }

    /**
     * Returns the string representation of the error.
     * Format: [CODE] - MESSAGE
     * 
     * @return Formatted error string
     */
    @Override
    public String toString() {
        return "[" + code + "] - " + message;
    }
}
