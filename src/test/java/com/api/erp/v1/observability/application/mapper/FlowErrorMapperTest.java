package com.api.erp.v1.observability.application.mapper;

import com.api.erp.v1.observability.domain.FlowStatus;
import com.api.erp.v1.shared.domain.exception.ValidationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("FlowErrorMapper Tests")
class FlowErrorMapperTest {

    @Test
    @DisplayName("Deve mapear ValidationException para ERROR_VALIDATION")
    void shouldMapValidationExceptionToErrorValidation() {
        // Arrange
        var exception = new ValidationException("email", "Email inválido");

        // Act
        FlowStatus status = FlowErrorMapper.mapException(exception);

        // Assert
        assertThat(status).isEqualTo(FlowStatus.ERROR_VALIDATION);
    }

    @Test
    @DisplayName("Deve mapear IllegalArgumentException para ERROR_VALIDATION")
    void shouldMapIllegalArgumentExceptionToErrorValidation() {
        // Arrange
        var exception = new IllegalArgumentException("Argumento inválido");

        // Act
        FlowStatus status = FlowErrorMapper.mapException(exception);

        // Assert
        assertThat(status).isEqualTo(FlowStatus.ERROR_VALIDATION);
    }

    @Test
    @DisplayName("Deve mapear NumberFormatException para ERROR_CONVERSION")
    void shouldMapNumberFormatExceptionToErrorConversion() {
        // Arrange
        var exception = new NumberFormatException("Número inválido");

        // Act
        FlowStatus status = FlowErrorMapper.mapException(exception);

        // Assert
        assertThat(status).isEqualTo(FlowStatus.ERROR_CONVERSION);
    }

    @Test
    @DisplayName("Deve mapear ClassCastException para ERROR_CONVERSION")
    void shouldMapClassCastExceptionToErrorConversion() {
        // Arrange
        var exception = new ClassCastException("Tipo inválido");

        // Act
        FlowStatus status = FlowErrorMapper.mapException(exception);

        // Assert
        assertThat(status).isEqualTo(FlowStatus.ERROR_CONVERSION);
    }

    @Test
    @DisplayName("Deve mapear IOException para ERROR_EXTERNAL")
    void shouldMapIOExceptionToErrorExternal() {
        // Arrange
        var exception = new java.io.IOException("Erro de I/O");

        // Act
        FlowStatus status = FlowErrorMapper.mapException(exception);

        // Assert
        assertThat(status).isEqualTo(FlowStatus.ERROR_EXTERNAL);
    }

    @Test
    @DisplayName("Deve mapear exceção null para ERROR_UNKNOWN")
    void shouldMapNullExceptionToErrorUnknown() {
        // Act
        FlowStatus status = FlowErrorMapper.mapException(null);

        // Assert
        assertThat(status).isEqualTo(FlowStatus.ERROR_UNKNOWN);
    }

    @Test
    @DisplayName("Deve mapear exceção desconhecida para ERROR_THROW")
    void shouldMapUnknownExceptionToErrorThrow() {
        // Arrange
        var exception = new RuntimeException("Erro desconhecido");

        // Act
        FlowStatus status = FlowErrorMapper.mapException(exception);

        // Assert
        assertThat(status).isEqualTo(FlowStatus.ERROR_THROW);
    }

    @Test
    @DisplayName("Deve ser impossível instanciar FlowErrorMapper")
    void shouldNotInstantiateFlowErrorMapper() {
        // Act & Assert
        assertThat(() -> {
            var constructor = FlowErrorMapper.class.getDeclaredConstructor();
            constructor.setAccessible(true);
            constructor.newInstance();
        }).isInstanceOf(AssertionError.class);
    }
}
