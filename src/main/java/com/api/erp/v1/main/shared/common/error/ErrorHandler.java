package com.api.erp.v1.main.shared.common.error;

import com.api.erp.v1.main.shared.domain.exception.NotFoundException;

/**
 * Exception handler for centralized error handling.
 * <p>
 * Analyzes the error message and throws the appropriate exception:
 * - NotFoundException for 404 errors
 * - BusinessException for other errors
 * <p>
 * Usage:
 * <code>
 * if (user == null) throw new ErrorHandler(UserErrorMessage.USER_NOT_FOUND);
 * </code>
 *
 * @author ERP System
 * @version 2.0
 */
public class ErrorHandler extends RuntimeException {

    /**
     * Constructor that analyzes the error message and throws the appropriate exception.
     *
     * @param errorMessage Enum with error information
     * @throws com.api.erp.v1.main.shared.domain.exception.NotFoundException if status is 404
     * @throws com.api.erp.v1.main.shared.domain.exception.BusinessException for all other errors
     */

    public ErrorHandler(IErrorMessage errorMessage) throws NotFoundException {
        super(errorMessage.getMessage());

        switch (errorMessage.getErrorType()) {

            case NOT_FOUND:
                throw new NotFoundException(errorMessage.getMessage());

            case ALREADY_EXISTS, DUPLICATE_RESOURCE, CONFLICT:
                throw new IllegalStateException(errorMessage.getMessage());

            case INVALID_ARGUMENT, INVALID_TYPE, INVALID_FORMAT, MISSING_REQUIRED_FIELD:
                throw new IllegalArgumentException(errorMessage.getMessage());

            case UNAUTHORIZED, FORBIDDEN:
                throw new SecurityException(errorMessage.getMessage());

            case INVALID_STATE, OPERATION_NOT_ALLOWED:
                throw new IllegalStateException(errorMessage.getMessage());

            case SERVICE_UNAVAILABLE, TIMEOUT:
                throw new RuntimeException("Service temporarily unavailable: " + errorMessage.getMessage());

            case INTERNAL_ERROR, UNKNOWN_ERROR:
            default:
                throw new RuntimeException(errorMessage.getMessage());
        }
    }
}

