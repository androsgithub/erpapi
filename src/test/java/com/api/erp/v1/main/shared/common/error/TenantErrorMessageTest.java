package com.api.erp.v1.main.shared.common.error;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.assertj.core.api.Assertions.*;

/**
 * TESTES UNITÁRIOS - TenantErrorMessage
 *
 * Valida contrato IErrorMessage para todos os valores do enum.
 *
 * @author Test Suite
 * @version 1.0
 */
@DisplayName("TenantErrorMessage - Testes Unitários")
class TenantErrorMessageTest {

    @ParameterizedTest
    @EnumSource(TenantErrorMessage.class)
    @DisplayName("dado_qualquerTenantErrorMessage_quando_acessarPropriedades_entao_naoSaoNulas")
    void testGivenAnyTenantErrorMessage_WhenAccessProperties_ThenNotNull(TenantErrorMessage errorMessage) {
        assertThat(errorMessage.getMessage()).isNotNull().isNotBlank();
        assertThat(errorMessage.getCode()).isNotNull().isNotBlank();
        assertThat(errorMessage.getErrorType()).isNotNull();
    }

    @ParameterizedTest
    @EnumSource(TenantErrorMessage.class)
    @DisplayName("dado_qualquerTenantErrorMessage_quando_toString_entao_formatoCorreto")
    void testGivenAnyTenantErrorMessage_WhenToString_ThenCorrectFormat(TenantErrorMessage errorMessage) {
        String result = errorMessage.toString();
        // Formato: [CODE] - MESSAGE
        assertThat(result)
                .startsWith("[")
                .contains("] - ")
                .contains(errorMessage.getCode())
                .contains(errorMessage.getMessage());
    }

    @Test
    @DisplayName("dado_enum_quando_verificarQuantidade_entao_possui8Constantes")
    void testGivenEnum_WhenCheckCount_ThenHas8Constants() {
        assertThat(TenantErrorMessage.values()).hasSize(8);
    }

    @Test
    @DisplayName("dado_tenantNotFound_quando_acessar_entao_possuiErrorTypeNotFound")
    void testGivenTenantNotFound_WhenAccess_ThenHasNotFoundErrorType() {
        assertThat(TenantErrorMessage.TENANT_NOT_FOUND.getErrorType()).isEqualTo(ErrorType.NOT_FOUND);
        assertThat(TenantErrorMessage.TENANT_NOT_FOUND.getCode()).isEqualTo("TENANT_001");
    }

    @Test
    @DisplayName("dado_datasourceNotConfigured_quando_acessar_entao_possuiErrorTypeInternalError")
    void testGivenDatasourceNotConfigured_WhenAccess_ThenHasInternalErrorType() {
        assertThat(TenantErrorMessage.DATASOURCE_NOT_CONFIGURED.getErrorType()).isEqualTo(ErrorType.INTERNAL_ERROR);
        assertThat(TenantErrorMessage.DATASOURCE_NOT_CONFIGURED.getCode()).isEqualTo("TENANT_002");
    }

    @Test
    @DisplayName("dado_datasourceConnectionFailed_quando_acessar_entao_possuiServiceUnavailable")
    void testGivenDatasourceConnectionFailed_WhenAccess_ThenHasServiceUnavailable() {
        assertThat(TenantErrorMessage.DATASOURCE_CONNECTION_FAILED.getErrorType()).isEqualTo(ErrorType.SERVICE_UNAVAILABLE);
        assertThat(TenantErrorMessage.DATASOURCE_CONNECTION_FAILED.getCode()).isEqualTo("TENANT_003");
    }

    @Test
    @DisplayName("dado_datasourceAlreadyExists_quando_acessar_entao_possuiConflict")
    void testGivenDatasourceAlreadyExists_WhenAccess_ThenHasConflictType() {
        assertThat(TenantErrorMessage.DATASOURCE_ALREADY_EXISTS.getErrorType()).isEqualTo(ErrorType.CONFLICT);
    }

    @Test
    @DisplayName("dado_tenantGroupNotFound_quando_acessar_entao_possuiNotFound")
    void testGivenTenantGroupNotFound_WhenAccess_ThenHasNotFoundType() {
        assertThat(TenantErrorMessage.TENANT_GROUP_NOT_FOUND.getErrorType()).isEqualTo(ErrorType.NOT_FOUND);
        assertThat(TenantErrorMessage.TENANT_GROUP_NOT_FOUND.getCode()).isEqualTo("TENANT_007");
    }

    @ParameterizedTest
    @EnumSource(TenantErrorMessage.class)
    @DisplayName("dado_qualquerTenantErrorMessage_quando_acessarCode_entao_comecarComTENANT")
    void testGivenAnyTenantErrorMessage_WhenAccessCode_ThenStartsWithTENANT(TenantErrorMessage errorMessage) {
        assertThat(errorMessage.getCode()).startsWith("TENANT_");
    }
}
