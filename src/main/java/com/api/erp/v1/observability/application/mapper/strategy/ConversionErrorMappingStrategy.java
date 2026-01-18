package com.api.erp.v1.observability.application.mapper.strategy;

import com.api.erp.v1.observability.domain.FlowStatus;

/**
 * Estratégia para mapeamento de erros de conversão/serialização.
 * 
 * Mapeia JsonException, JsonMappingException, JsonParseException,
 * NumberFormatException, ClassCastException para FlowStatus.ERROR_CONVERSION.
 */
public class ConversionErrorMappingStrategy implements ErrorMappingStrategy {

    @Override
    public boolean canHandle(Throwable exception) {
        if (exception == null) {
            return false;
        }

        // Verificação com instanceof para type safety
        if (exception instanceof NumberFormatException ||
            exception instanceof ClassCastException) {
            return true;
        }

        // Verificação por nome de classe
        String className = exception.getClass().getName();
        return className.contains("JsonException") ||
               className.contains("JsonMappingException") ||
               className.contains("JsonParseException") ||
               className.contains("ConversionException") ||
               className.contains("TypeMismatchException");
    }

    @Override
    public FlowStatus map(Throwable exception) {
        return FlowStatus.ERROR_CONVERSION;
    }

    @Override
    public int getPriority() {
        return 80;
    }
}
