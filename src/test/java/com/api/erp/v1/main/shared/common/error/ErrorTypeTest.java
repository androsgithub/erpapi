package com.api.erp.v1.main.shared.common.error;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.*;

/**
 * TESTES UNITÁRIOS - ErrorType
 *
 * @author Test Suite
 * @version 1.0
 */
@DisplayName("ErrorType - Testes Unitários")
class ErrorTypeTest {

    @Test
    @DisplayName("dado_enum_quando_verificarQuantidade_entao_possui15Valores")
    void testGivenEnum_WhenCheckCount_ThenHas15Values() {
        assertThat(ErrorType.values()).hasSize(15);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "NOT_FOUND", "ALREADY_EXISTS", "INVALID_ARGUMENT", "INVALID_TYPE",
            "INVALID_FORMAT", "MISSING_REQUIRED_FIELD", "UNAUTHORIZED", "FORBIDDEN",
            "CONFLICT", "DUPLICATE_RESOURCE", "INVALID_STATE", "OPERATION_NOT_ALLOWED",
            "INTERNAL_ERROR", "SERVICE_UNAVAILABLE", "TIMEOUT", "UNKNOWN_ERROR"
    })
    @DisplayName("dado_nomeDeErrorType_quando_valueOf_entao_retornaEnumCorreto")
    void testGivenErrorTypeName_WhenValueOf_ThenReturnsCorrectEnum(String name) {
        assertThat(ErrorType.valueOf(name)).isNotNull();
    }

    @Test
    @DisplayName("dado_nomeInvalido_quando_valueOf_entao_lancaIllegalArgumentException")
    void testGivenInvalidName_WhenValueOf_ThenThrowsIllegalArgumentException() {
        assertThatThrownBy(() -> ErrorType.valueOf("INVALID_NAME"))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
