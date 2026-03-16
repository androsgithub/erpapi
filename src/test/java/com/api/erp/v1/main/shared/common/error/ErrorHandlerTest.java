package com.api.erp.v1.main.shared.common.error;

import com.api.erp.v1.main.shared.domain.exception.NotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

/**
 * TESTES UNITÁRIOS - ErrorHandler
 *
 * Valida que o ErrorHandler despacha exceções corretas baseadas no ErrorType.
 *
 * @author Test Suite
 * @version 1.0
 */
@DisplayName("ErrorHandler - Testes Unitários")
class ErrorHandlerTest {

    // ===== NOT_FOUND → NotFoundException =====

    @Test
    @DisplayName("dado_errorTypeNotFound_quando_criarErrorHandler_entao_lancaNotFoundException")
    void testGivenNotFoundErrorType_WhenCreateErrorHandler_ThenThrowsNotFoundException() {
        assertThatThrownBy(() -> new ErrorHandler(TenantErrorMessage.TENANT_NOT_FOUND))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Tenant not found");
    }

    // ===== CONFLICT → IllegalStateException =====

    @Test
    @DisplayName("dado_errorTypeConflict_quando_criarErrorHandler_entao_lancaIllegalStateException")
    void testGivenConflictErrorType_WhenCreateErrorHandler_ThenThrowsIllegalStateException() {
        assertThatThrownBy(() -> new ErrorHandler(TenantErrorMessage.DATASOURCE_ALREADY_EXISTS))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Datasource already configured");
    }

    @Test
    @DisplayName("dado_errorTypeTenantAlreadyExists_quando_criarErrorHandler_entao_lancaIllegalStateException")
    void testGivenTenantAlreadyExistsErrorType_WhenCreateErrorHandler_ThenThrowsIllegalStateException() {
        assertThatThrownBy(() -> new ErrorHandler(TenantErrorMessage.TENANT_ALREADY_EXISTS))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("CNPJ already exists");
    }

    // ===== INTERNAL_ERROR → RuntimeException =====

    @Test
    @DisplayName("dado_errorTypeInternalError_quando_criarErrorHandler_entao_lancaRuntimeException")
    void testGivenInternalErrorType_WhenCreateErrorHandler_ThenThrowsRuntimeException() {
        assertThatThrownBy(() -> new ErrorHandler(TenantErrorMessage.DATASOURCE_NOT_CONFIGURED))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Datasource not configured");
    }

    @Test
    @DisplayName("dado_errorTypeMigrationFailed_quando_criarErrorHandler_entao_lancaRuntimeException")
    void testGivenMigrationFailedErrorType_WhenCreateErrorHandler_ThenThrowsRuntimeException() {
        assertThatThrownBy(() -> new ErrorHandler(TenantErrorMessage.MIGRATION_FAILED))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("migration failed");
    }

    // ===== SERVICE_UNAVAILABLE → RuntimeException =====

    @Test
    @DisplayName("dado_errorTypeServiceUnavailable_quando_criarErrorHandler_entao_lancaRuntimeException")
    void testGivenServiceUnavailableErrorType_WhenCreateErrorHandler_ThenThrowsRuntimeException() {
        assertThatThrownBy(() -> new ErrorHandler(TenantErrorMessage.DATASOURCE_CONNECTION_FAILED))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Service temporarily unavailable");
    }

    // ===== Custom IErrorMessage para cobrir todos os ErrorTypes =====

    @Test
    @DisplayName("dado_errorTypeInvalidArgument_quando_criarErrorHandler_entao_lancaIllegalArgumentException")
    void testGivenInvalidArgumentErrorType_WhenCreateErrorHandler_ThenThrowsIllegalArgumentException() {
        IErrorMessage custom = createCustomError("Invalid argument test", "TEST_001", ErrorType.INVALID_ARGUMENT);
        assertThatThrownBy(() -> new ErrorHandler(custom))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invalid argument test");
    }

    @Test
    @DisplayName("dado_errorTypeInvalidType_quando_criarErrorHandler_entao_lancaIllegalArgumentException")
    void testGivenInvalidTypeErrorType_WhenCreateErrorHandler_ThenThrowsIllegalArgumentException() {
        IErrorMessage custom = createCustomError("Invalid type test", "TEST_002", ErrorType.INVALID_TYPE);
        assertThatThrownBy(() -> new ErrorHandler(custom))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invalid type test");
    }

    @Test
    @DisplayName("dado_errorTypeInvalidFormat_quando_criarErrorHandler_entao_lancaIllegalArgumentException")
    void testGivenInvalidFormatErrorType_WhenCreateErrorHandler_ThenThrowsIllegalArgumentException() {
        IErrorMessage custom = createCustomError("Invalid format test", "TEST_003", ErrorType.INVALID_FORMAT);
        assertThatThrownBy(() -> new ErrorHandler(custom))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("dado_errorTypeMissingRequiredField_quando_criarErrorHandler_entao_lancaIllegalArgumentException")
    void testGivenMissingRequiredFieldErrorType_WhenCreateErrorHandler_ThenThrowsIllegalArgumentException() {
        IErrorMessage custom = createCustomError("Missing field", "TEST_004", ErrorType.MISSING_REQUIRED_FIELD);
        assertThatThrownBy(() -> new ErrorHandler(custom))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("dado_errorTypeUnauthorized_quando_criarErrorHandler_entao_lancaSecurityException")
    void testGivenUnauthorizedErrorType_WhenCreateErrorHandler_ThenThrowsSecurityException() {
        IErrorMessage custom = createCustomError("Unauthorized", "TEST_005", ErrorType.UNAUTHORIZED);
        assertThatThrownBy(() -> new ErrorHandler(custom))
                .isInstanceOf(SecurityException.class)
                .hasMessage("Unauthorized");
    }

    @Test
    @DisplayName("dado_errorTypeForbidden_quando_criarErrorHandler_entao_lancaSecurityException")
    void testGivenForbiddenErrorType_WhenCreateErrorHandler_ThenThrowsSecurityException() {
        IErrorMessage custom = createCustomError("Forbidden access", "TEST_006", ErrorType.FORBIDDEN);
        assertThatThrownBy(() -> new ErrorHandler(custom))
                .isInstanceOf(SecurityException.class);
    }

    @Test
    @DisplayName("dado_errorTypeAlreadyExists_quando_criarErrorHandler_entao_lancaIllegalStateException")
    void testGivenAlreadyExistsErrorType_WhenCreateErrorHandler_ThenThrowsIllegalStateException() {
        IErrorMessage custom = createCustomError("Already exists", "TEST_007", ErrorType.ALREADY_EXISTS);
        assertThatThrownBy(() -> new ErrorHandler(custom))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    @DisplayName("dado_errorTypeDuplicateResource_quando_criarErrorHandler_entao_lancaIllegalStateException")
    void testGivenDuplicateResourceErrorType_WhenCreateErrorHandler_ThenThrowsIllegalStateException() {
        IErrorMessage custom = createCustomError("Duplicate", "TEST_008", ErrorType.DUPLICATE_RESOURCE);
        assertThatThrownBy(() -> new ErrorHandler(custom))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    @DisplayName("dado_errorTypeInvalidState_quando_criarErrorHandler_entao_lancaIllegalStateException")
    void testGivenInvalidStateErrorType_WhenCreateErrorHandler_ThenThrowsIllegalStateException() {
        IErrorMessage custom = createCustomError("Invalid state", "TEST_009", ErrorType.INVALID_STATE);
        assertThatThrownBy(() -> new ErrorHandler(custom))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    @DisplayName("dado_errorTypeOperationNotAllowed_quando_criarErrorHandler_entao_lancaIllegalStateException")
    void testGivenOperationNotAllowedErrorType_WhenCreateErrorHandler_ThenThrowsIllegalStateException() {
        IErrorMessage custom = createCustomError("Not allowed", "TEST_010", ErrorType.OPERATION_NOT_ALLOWED);
        assertThatThrownBy(() -> new ErrorHandler(custom))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    @DisplayName("dado_errorTypeTimeout_quando_criarErrorHandler_entao_lancaRuntimeComServiceUnavailable")
    void testGivenTimeoutErrorType_WhenCreateErrorHandler_ThenThrowsRuntimeException() {
        IErrorMessage custom = createCustomError("Timeout", "TEST_011", ErrorType.TIMEOUT);
        assertThatThrownBy(() -> new ErrorHandler(custom))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Service temporarily unavailable");
    }

    @Test
    @DisplayName("dado_errorTypeUnknownError_quando_criarErrorHandler_entao_lancaRuntimeException")
    void testGivenUnknownErrorType_WhenCreateErrorHandler_ThenThrowsRuntimeException() {
        IErrorMessage custom = createCustomError("Unknown", "TEST_012", ErrorType.UNKNOWN_ERROR);
        assertThatThrownBy(() -> new ErrorHandler(custom))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Unknown");
    }

    // ===== Helpers =====

    private IErrorMessage createCustomError(String message, String code, ErrorType errorType) {
        return new IErrorMessage() {
            @Override public String getMessage() { return message; }
            @Override public String getCode() { return code; }
            @Override public ErrorType getErrorType() { return errorType; }
        };
    }
}
