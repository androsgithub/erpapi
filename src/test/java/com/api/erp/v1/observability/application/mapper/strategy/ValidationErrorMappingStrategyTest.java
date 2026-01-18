package com.api.erp.v1.observability.application.mapper.strategy;

import com.api.erp.v1.observability.domain.FlowStatus;
import com.api.erp.v1.shared.domain.exception.ValidationException;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("ValidationErrorMappingStrategy Tests")
class ValidationErrorMappingStrategyTest {

    private ValidationErrorMappingStrategy strategy;

    @BeforeEach
    void setUp() {
        strategy = new ValidationErrorMappingStrategy();
    }

    @Test
    @DisplayName("Deve reconhecer ValidationException")
    void shouldRecognizeValidationException() {
        // Arrange
        var exception = new ValidationException("email", "Email inválido");

        // Act
        boolean canHandle = strategy.canHandle(exception);

        // Assert
        assertThat(canHandle).isTrue();
    }

    @Test
    @DisplayName("Deve reconhecer IllegalArgumentException")
    void shouldRecognizeIllegalArgumentException() {
        // Arrange
        var exception = new IllegalArgumentException("Argumento inválido");

        // Act
        boolean canHandle = strategy.canHandle(exception);

        // Assert
        assertThat(canHandle).isTrue();
    }

    @Test
    @DisplayName("Deve mapear para ERROR_VALIDATION")
    void shouldMapToErrorValidation() {
        // Arrange
        var exception = new ValidationException("field", "Erro");

        // Act
        FlowStatus status = strategy.map(exception);

        // Assert
        assertThat(status).isEqualTo(FlowStatus.ERROR_VALIDATION);
    }

    @Test
    @DisplayName("Deve rejeitar exceções não relacionadas")
    void shouldRejectUnrelatedExceptions() {
        // Arrange
        var exception = new RuntimeException("Algo deu errado");

        // Act
        boolean canHandle = strategy.canHandle(exception);

        // Assert
        assertThat(canHandle).isFalse();
    }

    @Test
    @DisplayName("Deve ter alta prioridade")
    void shouldHaveHighPriority() {
        // Act
        int priority = strategy.getPriority();

        // Assert
        assertThat(priority).isGreaterThan(50);
    }

    @Test
    @DisplayName("Deve rejeitar exceção null")
    void shouldRejectNullException() {
        // Act
        boolean canHandle = strategy.canHandle(null);

        // Assert
        assertThat(canHandle).isFalse();
    }
}
