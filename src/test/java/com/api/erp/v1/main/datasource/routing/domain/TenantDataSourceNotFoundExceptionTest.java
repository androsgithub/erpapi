package com.api.erp.v1.main.datasource.routing.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

/**
 * TESTES UNITÁRIOS - TenantDataSourceNotFoundException
 *
 * @author Test Suite
 * @version 1.0
 */
@DisplayName("TenantDataSourceNotFoundException - Testes Unitários")
class TenantDataSourceNotFoundExceptionTest {

    @Test
    @DisplayName("dado_tenantId_quando_construirComTenantId_entao_mensagemContemTenantId")
    void testGivenTenantId_WhenConstructWithTenantId_ThenMessageContainsTenantId() {
        Long tenantId = 42L;
        TenantDataSourceNotFoundException exception = new TenantDataSourceNotFoundException(tenantId);

        assertThat(exception.getMessage()).contains("42");
        assertThat(exception.getTenantId()).isEqualTo(tenantId);
        assertThat(exception).isInstanceOf(RuntimeException.class);
    }

    @Test
    @DisplayName("dado_tenantIdEMensagem_quando_construirComMensagemCustom_entao_usaMensagemCustom")
    void testGivenTenantIdAndMessage_WhenConstructWithCustomMessage_ThenUsesCustomMessage() {
        Long tenantId = 99L;
        String message = "Custom error for tenant";

        TenantDataSourceNotFoundException exception = new TenantDataSourceNotFoundException(tenantId, message);

        assertThat(exception.getMessage()).isEqualTo(message);
        assertThat(exception.getTenantId()).isEqualTo(tenantId);
    }

    @Test
    @DisplayName("dado_tenantIdMensagemECausa_quando_construirComCausa_entao_propagaCausa")
    void testGivenTenantIdMessageAndCause_WhenConstructWithCause_ThenPropagatesCause() {
        Long tenantId = 7L;
        String message = "DS not found";
        Throwable cause = new RuntimeException("root cause");

        TenantDataSourceNotFoundException exception = new TenantDataSourceNotFoundException(tenantId, message, cause);

        assertThat(exception.getMessage()).isEqualTo(message);
        assertThat(exception.getTenantId()).isEqualTo(tenantId);
        assertThat(exception.getCause()).isEqualTo(cause);
    }

    @Test
    @DisplayName("dado_tenantIdNull_quando_construir_entao_getTenantIdRetornaNull")
    void testGivenNullTenantId_WhenConstruct_ThenGetTenantIdReturnsNull() {
        TenantDataSourceNotFoundException exception = new TenantDataSourceNotFoundException(null);
        assertThat(exception.getTenantId()).isNull();
    }
}
