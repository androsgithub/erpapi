package com.api.erp.v1.main.shared.common.error;

public enum ErrorType {

    // Recursos
    NOT_FOUND,
    ALREADY_EXISTS,

    // Validação
    INVALID_ARGUMENT,
    INVALID_TYPE,
    INVALID_FORMAT,
    MISSING_REQUIRED_FIELD,

    // Autenticação / Autorização
    UNAUTHORIZED,
    FORBIDDEN,

    // Conflitos
    CONFLICT,
    DUPLICATE_RESOURCE,

    // Estado
    INVALID_STATE,
    OPERATION_NOT_ALLOWED,

    // Sistema
    INTERNAL_ERROR,
    SERVICE_UNAVAILABLE,
    TIMEOUT,

    // Outros
    UNKNOWN_ERROR
}
