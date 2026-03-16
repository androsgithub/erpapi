package com.api.erp.v1.main.shared.domain.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.Assertions.*;

/**
 * TESTES UNITÁRIOS - ValidationException
 *
 * @author Test Suite
 * @version 1.0
 */
@DisplayName("ValidationException - Testes Unitários")
class ValidationExceptionTest {

    @Test
    @DisplayName("dado_fieldEMensagem_quando_construirBasico_entao_statusDefaultBadRequest")
    void testGivenFieldAndMessage_WhenConstructBasic_ThenDefaultStatusIsBadRequest() {
        ValidationException exception = new ValidationException("email", "Email is invalid");

        assertThat(exception.getField()).isEqualTo("email");
        assertThat(exception.getMessage()).isEqualTo("Email is invalid");
        assertThat(exception.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(exception.getCode()).isEqualTo("VALIDATION_ERROR");
        assertThat(exception).isInstanceOf(RuntimeException.class);
    }

    @Test
    @DisplayName("dado_fieldMensagemEStatus_quando_construirComStatus_entao_usaStatusFornecido")
    void testGivenFieldMessageAndStatus_WhenConstructWithStatus_ThenUsesProvidedStatus() {
        ValidationException exception = new ValidationException("name", "Name required", HttpStatus.UNPROCESSABLE_ENTITY);

        assertThat(exception.getField()).isEqualTo("name");
        assertThat(exception.getMessage()).isEqualTo("Name required");
        assertThat(exception.getStatus()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
        assertThat(exception.getCode()).isEqualTo("VALIDATION_ERROR");
    }

    @Test
    @DisplayName("dado_fieldMensagemCodeEStatus_quando_construirCompleto_entao_todosOsValoresCorretos")
    void testGivenFieldMessageCodeAndStatus_WhenConstructFull_ThenAllValuesCorrect() {
        ValidationException exception = new ValidationException("cpf", "CPF inválido", "CPF_INVALID", HttpStatus.BAD_REQUEST);

        assertThat(exception.getField()).isEqualTo("cpf");
        assertThat(exception.getMessage()).isEqualTo("CPF inválido");
        assertThat(exception.getCode()).isEqualTo("CPF_INVALID");
        assertThat(exception.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
    }
}
