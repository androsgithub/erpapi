package com.api.erp.v1.observability.application.mapper.strategy;

import com.api.erp.v1.observability.domain.FlowStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("ErrorMappingStrategyRegistry Tests")
class ErrorMappingStrategyRegistryTest {

    private ErrorMappingStrategyRegistry registry;

    @BeforeEach
    void setUp() {
        registry = new ErrorMappingStrategyRegistry();
    }

    @Test
    @DisplayName("Deve registrar estratégia")
    void shouldRegisterStrategy() {
        // Arrange
        var strategy = new ValidationErrorMappingStrategy();

        // Act
        registry.register(strategy);

        // Assert
        assertThat(registry.getStrategyCount()).isEqualTo(1);
    }

    @Test
    @DisplayName("Deve encontrar estratégia apropriada")
    void shouldFindAppropriateStrategy() {
        // Arrange
        registry.register(new ValidationErrorMappingStrategy());
        registry.register(new DefaultErrorMappingStrategy());
        var exception = new ValidationException("field", "Erro");

        // Act
        ErrorMappingStrategy strategy = registry.findStrategy(exception);

        // Assert
        assertThat(strategy).isInstanceOf(ValidationErrorMappingStrategy.class);
    }

    @Test
    @DisplayName("Deve retornar estratégia padrão para exceção desconhecida")
    void shouldReturnDefaultStrategyForUnknownException() {
        // Arrange
        registry.register(new ValidationErrorMappingStrategy());
        var exception = new RuntimeException("Desconhecida");

        // Act
        ErrorMappingStrategy strategy = registry.findStrategy(exception);

        // Assert
        assertThat(strategy).isInstanceOf(DefaultErrorMappingStrategy.class);
    }

    @Test
    @DisplayName("Deve respeitar prioridade ao selecionar estratégia")
    void shouldRespectPriorityWhenSelectingStrategy() {
        // Arrange - Estratégias serão reordenadas por prioridade
        registry.register(new DefaultErrorMappingStrategy()); // 0
        registry.register(new ValidationErrorMappingStrategy()); // 90
        registry.register(new ConversionErrorMappingStrategy()); // 80

        var exception = new ValidationException("field", "Erro");

        // Act
        ErrorMappingStrategy strategy = registry.findStrategy(exception);

        // Assert
        assertThat(strategy).isInstanceOf(ValidationErrorMappingStrategy.class);
        assertThat(registry.getStrategyCount()).isEqualTo(3);
    }

    @Test
    @DisplayName("Deve lançar exceção ao registrar strategy nula")
    void shouldThrowExceptionWhenRegisteringNullStrategy() {
        // Act & Assert
        assertThat(() -> registry.register(null))
            .isInstanceOf(NullPointerException.class);
    }

    @Test
    @DisplayName("Deve limpar estratégias")
    void shouldClearStrategies() {
        // Arrange
        registry.register(new ValidationErrorMappingStrategy());
        registry.register(new DefaultErrorMappingStrategy());

        // Act
        registry.clear();

        // Assert
        assertThat(registry.getStrategyCount()).isZero();
    }

    @Test
    @DisplayName("Deve contar estratégias registradas")
    void shouldCountRegisteredStrategies() {
        // Arrange
        registry.register(new ValidationErrorMappingStrategy());
        registry.register(new DatabaseErrorMappingStrategy());
        registry.register(new SecurityErrorMappingStrategy());

        // Act
        int count = registry.getStrategyCount();

        // Assert
        assertThat(count).isEqualTo(3);
    }
}
