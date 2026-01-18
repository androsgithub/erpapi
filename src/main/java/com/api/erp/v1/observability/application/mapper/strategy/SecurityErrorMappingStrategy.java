package com.api.erp.v1.observability.application.mapper.strategy;

import com.api.erp.v1.observability.domain.FlowStatus;

/**
 * Estratégia para mapeamento de erros de segurança.
 * 
 * Mapeia AccessDeniedException, SecurityException, UnauthorizedException,
 * ForbiddenException, AuthenticationException para FlowStatus.ERROR_SECURITY.
 */
public class SecurityErrorMappingStrategy implements ErrorMappingStrategy {

    @Override
    public boolean canHandle(Throwable exception) {
        if (exception == null) {
            return false;
        }

        String className = exception.getClass().getName();
        return className.contains("AccessDeniedException") ||
               className.contains("SecurityException") ||
               className.contains("UnauthorizedException") ||
               className.contains("ForbiddenException") ||
               className.contains("AuthenticationException");
    }

    @Override
    public FlowStatus map(Throwable exception) {
        return FlowStatus.ERROR_SECURITY;
    }

    @Override
    public int getPriority() {
        return 75;
    }
}
