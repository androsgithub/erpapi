package com.api.erp.v1.observability.application.mapper.strategy;

import com.api.erp.v1.observability.domain.FlowStatus;

/**
 * Estratégia para mapeamento de erros de timeout.
 * 
 * Mapeia TimeoutException, SocketTimeoutException, ConnectTimeoutException,
 * ReadTimeoutException para FlowStatus.ERROR_TIMEOUT.
 */
public class TimeoutErrorMappingStrategy implements ErrorMappingStrategy {

    @Override
    public boolean canHandle(Throwable exception) {
        if (exception == null) {
            return false;
        }

        String className = exception.getClass().getName();
        return className.contains("TimeoutException") ||
               className.contains("SocketTimeoutException") ||
               className.contains("ConnectTimeoutException") ||
               className.contains("ReadTimeoutException");
    }

    @Override
    public FlowStatus map(Throwable exception) {
        return FlowStatus.ERROR_TIMEOUT;
    }

    @Override
    public int getPriority() {
        return 70;
    }
}
