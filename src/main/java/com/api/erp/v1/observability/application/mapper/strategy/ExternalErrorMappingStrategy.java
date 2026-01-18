package com.api.erp.v1.observability.application.mapper.strategy;

import com.api.erp.v1.observability.domain.FlowStatus;

import java.io.IOException;

/**
 * Estratégia para mapeamento de erros externos.
 * 
 * Mapeia HttpClientErrorException, HttpServerErrorException,
 * RestClientException, WebClientException, FeignException,
 * IOException para FlowStatus.ERROR_EXTERNAL.
 */
public class ExternalErrorMappingStrategy implements ErrorMappingStrategy {

    @Override
    public boolean canHandle(Throwable exception) {
        if (exception == null) {
            return false;
        }

        // IOException é mais específico
        if (exception instanceof IOException) {
            return true;
        }

        String className = exception.getClass().getName();
        return className.contains("HttpClientErrorException") ||
               className.contains("HttpServerErrorException") ||
               className.contains("RestClientException") ||
               className.contains("WebClientException") ||
               className.contains("FeignException");
    }

    @Override
    public FlowStatus map(Throwable exception) {
        return FlowStatus.ERROR_EXTERNAL;
    }

    @Override
    public int getPriority() {
        return 65;
    }
}
