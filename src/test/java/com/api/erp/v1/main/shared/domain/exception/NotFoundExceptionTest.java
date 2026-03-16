package com.api.erp.v1.main.shared.domain.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

/**
 * TESTES UNITÁRIOS - NotFoundException
 *
 * @author Test Suite
 * @version 1.0
 */
@DisplayName("NotFoundException - Testes Unitários")
class NotFoundExceptionTest {

    @Test
    @DisplayName("dado_mensagem_quando_construir_entao_mensagemCorreta")
    void testGivenMessage_WhenConstruct_ThenMessageIsCorrect() {
        NotFoundException exception = new NotFoundException("Resource not found");

        assertThat(exception.getMessage()).isEqualTo("Resource not found");
        assertThat(exception).isInstanceOf(RuntimeException.class);
    }

    @Test
    @DisplayName("dado_mensagemVazia_quando_construir_entao_mensagemVazia")
    void testGivenEmptyMessage_WhenConstruct_ThenEmptyMessage() {
        NotFoundException exception = new NotFoundException("");
        assertThat(exception.getMessage()).isEmpty();
    }
}
