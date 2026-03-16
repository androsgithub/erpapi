package com.api.erp.v1.main.shared.domain.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.Assertions.*;

/**
 * TESTES UNITÁRIOS - BusinessException
 *
 * @author Test Suite
 * @version 1.0
 */
@DisplayName("BusinessException - Testes Unitários")
class BusinessExceptionTest {

    @Test
    @DisplayName("dado_mensagem_quando_construirComMensagem_entao_statusDefaultUnprocessableEntity")
    void testGivenMessage_WhenConstructWithMessage_ThenDefaultStatusIsUnprocessableEntity() {
        BusinessException exception = new BusinessException("Business rule violated");

        assertThat(exception.getMessage()).isEqualTo("Business rule violated");
        assertThat(exception.getStatus()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
        assertThat(exception).isInstanceOf(RuntimeException.class);
    }

    @Test
    @DisplayName("dado_statusEMensagem_quando_construirComStatus_entao_usaStatusFornecido")
    void testGivenStatusAndMessage_WhenConstructWithStatus_ThenUsesProvidedStatus() {
        BusinessException exception = new BusinessException(HttpStatus.CONFLICT, "Resource conflict");

        assertThat(exception.getMessage()).isEqualTo("Resource conflict");
        assertThat(exception.getStatus()).isEqualTo(HttpStatus.CONFLICT);
    }

    @Test
    @DisplayName("dado_statusBadRequest_quando_construir_entao_retornaBadRequest")
    void testGivenBadRequestStatus_WhenConstruct_ThenReturnsBadRequest() {
        BusinessException exception = new BusinessException(HttpStatus.BAD_REQUEST, "Invalid input");

        assertThat(exception.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
    }
}
