package com.api.erp.v1.external.observability.strategy;

import com.api.erp.v1.main.shared.domain.exception.BusinessException;
import com.dros.observability.core.mapper.strategy.ErrorMappingStrategy;
import com.dros.observability.domain.FlowStatus;

/**
 * Strategy for mapping business errors.
 * 
 * Maps BusinessException and its subclasses (such as ProductException)
 * to FlowStatus.ERROR_VALIDATION, because business errors are essentially
 * domain rule violations that should have been validated.
 * 
 * High priority as it is common in domain operations.
 */
public class BusinessErrorMappingStrategy implements ErrorMappingStrategy {

    @Override
    public boolean canHandle(Throwable exception) {
        if (exception == null) {
            return false;
        }

        // Verificação com instanceof para type safety
        if (exception instanceof BusinessException) {
            return true;
        }

        // Fallback: verificação por nome de classe
        String className = exception.getClass().getName();
        return className.contains("BusinessException") ||
               className.contains("ProductException") ||
               className.contains("CustomerException") ||
               className.contains("DomainException");
    }

    @Override
    public FlowStatus map(Throwable exception) {
        // Business errors are treated as validation errors
        // because they violate domain rules
        return FlowStatus.ERROR_VALIDATION;
    }

    /**
     * High priority, right after direct validations.
     * Ensures BusinessException is mapped before generic strategies.
     */
    @Override
    public int getPriority() {
        return 88;
    }
}
