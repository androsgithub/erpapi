package com.api.erp.v1.external.observability.strategy;

import com.api.erp.v1.main.shared.domain.exception.ValidationException;
import com.dros.observability.core.mapper.strategy.ErrorMappingStrategy;
import com.dros.observability.domain.FlowStatus;
import jakarta.validation.ConstraintViolationException;

/**
 * Strategy for mapping validation errors.
 * 
 * Maps ValidationException, ConstraintViolationException and IllegalArgumentException
 * to FlowStatus.ERROR_VALIDATION.
 */
public class ValidationErrorMappingStrategy implements ErrorMappingStrategy {

    @Override
    public boolean canHandle(Throwable exception) {
        if (exception == null) {
            return false;
        }

        // Verifica tipos específicos com instanceof para melhor type safety
        if (exception instanceof ValidationException ||
            exception instanceof ConstraintViolationException ||
            exception instanceof IllegalArgumentException) {
            return true;
        }

        // Fallback: verificação por nome de classe (para compatibilidade)
        String className = exception.getClass().getName();
        return className.contains("ConstraintViolationException") ||
               className.contains("ValidationException");
    }

    @Override
    public FlowStatus map(Throwable exception) {
        return FlowStatus.ERROR_VALIDATION;
    }

    /**
     * Validation strategy has high priority as it is very common.
     */
    @Override
    public int getPriority() {
        return 90;
    }
}
