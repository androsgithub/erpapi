package com.api.erp.v1.observability.application.mapper;

import com.api.erp.v1.observability.domain.FlowStatus;

public final class FlowErrorMapper {

    private FlowErrorMapper() {
        throw new AssertionError("Classe não deve ser instanciada");
    }

    public static FlowStatus mapException(Throwable exception) {
        if (exception == null) {
            return FlowStatus.ERROR_UNKNOWN;
        }

        // Validação
        if (isValidationError(exception)) {
            return FlowStatus.ERROR_VALIDATION;
        }

        // Conversão/Serialização
        if (isConversionError(exception)) {
            return FlowStatus.ERROR_CONVERSION;
        }

        // Segurança
        if (isSecurityError(exception)) {
            return FlowStatus.ERROR_SECURITY;
        }

        // Banco de Dados
        if (isDatabaseError(exception)) {
            return FlowStatus.ERROR_DATABASE;
        }

        // Timeout
        if (isTimeoutError(exception)) {
            return FlowStatus.ERROR_TIMEOUT;
        }

        // Erro externo
        if (isExternalError(exception)) {
            return FlowStatus.ERROR_EXTERNAL;
        }

        // Default: erro lançado ou desconhecido
        return FlowStatus.ERROR_THROW;
    }

    private static boolean isValidationError(Throwable exception) {
        String className = exception.getClass().getName();
        return className.contains("ConstraintViolationException") ||
               className.contains("ValidationException") ||
               className.contains("IllegalArgumentException") ||
               exception.getMessage() != null && exception.getMessage().toLowerCase().contains("validation");
    }

    private static boolean isConversionError(Throwable exception) {
        String className = exception.getClass().getName();
        return className.contains("JsonException") ||
               className.contains("JsonMappingException") ||
               className.contains("JsonParseException") ||
               className.contains("ConversionException") ||
               className.contains("TypeMismatchException") ||
               exception instanceof NumberFormatException ||
               exception instanceof ClassCastException;
    }

    private static boolean isSecurityError(Throwable exception) {
        String className = exception.getClass().getName();
        return className.contains("AccessDeniedException") ||
               className.contains("SecurityException") ||
               className.contains("UnauthorizedException") ||
               className.contains("ForbiddenException") ||
               className.contains("AuthenticationException");
    }

    private static boolean isDatabaseError(Throwable exception) {
        String className = exception.getClass().getName();
        return className.contains("DataAccessException") ||
               className.contains("SQLException") ||
               className.contains("PersistenceException") ||
               className.contains("DatabaseException") ||
               className.contains("HibernateException") ||
               className.contains("JpaSystemException") ||
               className.contains("QueryTimeoutException");
    }

    private static boolean isTimeoutError(Throwable exception) {
        String className = exception.getClass().getName();
        return className.contains("TimeoutException") ||
               className.contains("SocketTimeoutException") ||
               className.contains("ConnectTimeoutException") ||
               className.contains("ReadTimeoutException");
    }

    private static boolean isExternalError(Throwable exception) {
        String className = exception.getClass().getName();
        return className.contains("HttpClientErrorException") ||
               className.contains("HttpServerErrorException") ||
               className.contains("RestClientException") ||
               className.contains("WebClientException") ||
               className.contains("FeignException") ||
               className.contains("IOException");
    }
}
