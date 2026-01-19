package com.api.erp.v1.observability.application.mapper;

import com.api.erp.v1.observability.application.mapper.strategy.BusinessErrorMappingStrategy;
import com.api.erp.v1.observability.application.mapper.strategy.ConversionErrorMappingStrategy;
import com.api.erp.v1.observability.application.mapper.strategy.DatabaseErrorMappingStrategy;
import com.api.erp.v1.observability.application.mapper.strategy.ErrorMappingStrategy;
import com.api.erp.v1.observability.application.mapper.strategy.ErrorMappingStrategyRegistry;
import com.api.erp.v1.observability.application.mapper.strategy.ExternalErrorMappingStrategy;
import com.api.erp.v1.observability.application.mapper.strategy.IllegalStateErrorMappingStrategy;
import com.api.erp.v1.observability.application.mapper.strategy.SecurityErrorMappingStrategy;
import com.api.erp.v1.observability.application.mapper.strategy.TimeoutErrorMappingStrategy;
import com.api.erp.v1.observability.application.mapper.strategy.ValidationErrorMappingStrategy;
import com.api.erp.v1.observability.domain.FlowStatus;

/**
 * Mapeador de exceções para FlowStatus.
 * 
 * Utiliza o padrão Strategy para mapear diferentes tipos de exceção
 * para seus respectivos FlowStatus, permitindo extensibilidade sem
 * modificação desta classe (Open/Closed Principle).
 * 
 * SOLID:
 * - Open/Closed: Nova estratégia pode ser adicionada sem modificar esta classe
 * - Single Responsibility: Apenas orquestra estratégias
 * - Dependency Inversion: Depende de abstração (ErrorMappingStrategy)
 * 
 * DDD: Representa a política de domínio de classificação de erros.
 * Clean Code: Simples e fácil de entender, sem lógica condicional complexa.
 */
public final class FlowErrorMapper {

    private static final ErrorMappingStrategyRegistry REGISTRY = new ErrorMappingStrategyRegistry();

    static {
        // Inicializa com estratégias padrão em ordem de prioridade
        REGISTRY.register(new ValidationErrorMappingStrategy());
        REGISTRY.register(new BusinessErrorMappingStrategy());
        REGISTRY.register(new IllegalStateErrorMappingStrategy());
        REGISTRY.register(new DatabaseErrorMappingStrategy());
        REGISTRY.register(new ConversionErrorMappingStrategy());
        REGISTRY.register(new SecurityErrorMappingStrategy());
        REGISTRY.register(new TimeoutErrorMappingStrategy());
        REGISTRY.register(new ExternalErrorMappingStrategy());
    }

    private FlowErrorMapper() {
        throw new AssertionError("Classe não deve ser instanciada");
    }

    /**
     * Mapeia uma exceção para seu FlowStatus correspondente.
     * 
     * Utiliza a primeira estratégia que puder tratar a exceção,
     * avaliadas em ordem de prioridade.
     * 
     * @param exception a exceção a mapear
     * @return o FlowStatus correspondente
     */
    public static FlowStatus mapException(Throwable exception) {
        if (exception == null) {
            return FlowStatus.ERROR_UNKNOWN;
        }

        ErrorMappingStrategy strategy = REGISTRY.findStrategy(exception);
        return strategy.map(exception);
    }

    /**
     * Registra uma estratégia customizada.
     * 
     * Útil para extensão em caso de tipos de erro específicos do domínio.
     * 
     * @param strategy a estratégia a registrar
     */
    public static void registerCustomStrategy(ErrorMappingStrategy strategy) {
        REGISTRY.register(strategy);
    }

    /**
     * Retorna o registry de estratégias.
     * 
     * Apenas para testes ou extensão avançada.
     * 
     * @return o registry
     */
    protected static ErrorMappingStrategyRegistry getRegistry() {
        return REGISTRY;
    }
}
