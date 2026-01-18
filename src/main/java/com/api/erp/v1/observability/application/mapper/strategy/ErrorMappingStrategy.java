package com.api.erp.v1.observability.application.mapper.strategy;

import com.api.erp.v1.observability.domain.FlowStatus;

/**
 * Strategy para mapeamento de exceções para FlowStatus.
 * 
 * Implementa o padrão Strategy do GoF para permitir diferentes
 * estratégias de mapeamento de erros, respeitando o Open/Closed Principle.
 * 
 * DDD: Permite diferentes contextos de validação de erro.
 * SOLID: Single Responsibility - cada estratégia trata um tipo específico.
 * Clean Code: Interface clara com responsabilidade única.
 */
public interface ErrorMappingStrategy {

    /**
     * Verifica se a estratégia é aplicável para a exceção fornecida.
     * 
     * @param exception a exceção a ser verificada
     * @return true se a estratégia pode mapear esta exceção
     */
    boolean canHandle(Throwable exception);

    /**
     * Mapeia a exceção para um FlowStatus.
     * 
     * @param exception a exceção a ser mapeada
     * @return o FlowStatus correspondente
     */
    FlowStatus map(Throwable exception);

    /**
     * Retorna a prioridade da estratégia (maior = mais alta prioridade).
     * Permite que estratégias mais específicas sejam avaliadas primeiro.
     * 
     * @return a prioridade (0-100)
     */
    default int getPriority() {
        return 50;
    }
}
