package com.api.erp.v1.main.shared.common.error;

import org.springframework.http.HttpStatus;

/**
 * Exception handler for centralized error handling.
 * 
 * Analyzes the error message and throws the appropriate exception:
 * - NotFoundException for 404 errors
 * - BusinessException for other errors
 * 
 * Usage:
 * <code>
 *   if (user == null) throw new ErrorHandler(UserErrorMessage.USER_NOT_FOUND);
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
    public ErrorHandler(IErrorMessage errorMessage) {
        if (errorMessage.getStatus().equals(HttpStatus.NOT_FOUND)) {
            throw errorMessage.toNotFoundException();
        } else {
            throw errorMessage.toBusinessException();
        }
    }
}

