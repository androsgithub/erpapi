package com.api.erp.v1.main.shared.common.error;

import com.api.erp.v1.main.shared.domain.exception.BusinessException;
import com.api.erp.v1.main.shared.domain.exception.NotFoundException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;

/**
 * Utility for centralized handling of generic errors.
 * 
 * Provides helper methods to throw exceptions based on the ErrorMessage enum,
 * facilitating consistent error message usage throughout the system.
 * 
 * @author ERP System
 * @version 1.0
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ErrorHandler {

    /**
     * Throws a BusinessException based on the defined error message.
     * 
     * Usage:
     * <code>
     *   throw ErrorHandler.throwBusinessException(TenantErrorMessage.TENANT_NOT_FOUND);
     * </code>
     * 
     * @param errorMessage Enum with error information
     * @throws BusinessException always, with the message and HTTP status from the enum
     */
    public static void throwBusinessException(IErrorMessage errorMessage) {
        throw errorMessage.toBusinessException();
    }

    /**
     * Throws a BusinessException with a custom message.
     * 
     * Usage:
     * <code>
     *   throw ErrorHandler.throwBusinessException(
     *       TenantErrorMessage.DATASOURCE_CONNECTION_FAILED, 
     *       "Failed to connect to database"
     *   );
     * </code>
     * 
     * @param errorMessage Enum with HTTP status and code
     * @param customMessage Custom message to override the default
     * @throws BusinessException always
     */
    public static void throwBusinessException(IErrorMessage errorMessage, String customMessage) {
        throw new BusinessException(errorMessage.getStatus(), customMessage);
    }

    /**
     * Throws a NotFoundException for resource not found errors.
     * 
     * Usage:
     * <code>
     *   throw ErrorHandler.throwNotFoundException(TenantErrorMessage.TENANT_NOT_FOUND);
     * </code>
     * 
     * @param errorMessage Enum with NOT_FOUND status
     * @throws NotFoundException always
     * @throws IllegalStateException if the enum is not NOT_FOUND
     */
    public static void throwNotFoundException(IErrorMessage errorMessage) {
        throw errorMessage.toNotFoundException();
    }

    /**
     * Logs the error and throws the corresponding exception.
     * 
     * Usage:
     * <code>
     *   ErrorHandler.logAndThrow(
     *       logger, 
     *       TenantErrorMessage.DATASOURCE_CONNECTION_FAILED, 
     *       "Error creating DataSource"
     *   );
     * </code>
     * 
     * @param logger Logger to record the error message
     * @param errorMessage Enum with error information
     * @param details Additional details for the log
     * @throws BusinessException always
     */
    public static void logAndThrow(Logger logger, IErrorMessage errorMessage, String details) {
        String logMessage = String.format("%s - %s | Details: %s", 
            errorMessage.getCode(), 
            errorMessage.getMessage(), 
            details
        );
        
        if (errorMessage.getStatus().is5xxServerError()) {
            logger.error(logMessage);
        } else {
            logger.warn(logMessage);
        }
        
        throw errorMessage.toBusinessException();
    }

    /**
     * Executes an action and handles generic database exceptions.
     * 
     * Usage:
     * <code>
     *   return ErrorHandler.executeWithDatabaseErrorHandling(
     *       () -> repository.findById(id),
     *       logger
     *   );
     * </code>
     * 
     * @param action Action to execute
     * @param logger Logger to record errors
     * @param <T> Return type
     * @return Result of the action
     * @throws BusinessException if database exception occurs
     */
    public static <T> T executeWithDatabaseErrorHandling(
            DatabaseAction<T> action,
            Logger logger) {
        try {
            return action.execute();
        } catch (Exception e) {
            String errorDetails = e.getMessage() != null ? e.getMessage() : e.getClass().getSimpleName();
            logAndThrow(logger, CommonErrorMessage.DATABASE_ERROR, errorDetails);
            return null; // never reachable
        }
    }

    /**
     * Functional interface for actions that can throw checked exceptions.
     */
    @FunctionalInterface
    public interface DatabaseAction<T> {
        T execute() throws Exception;
    }
}
