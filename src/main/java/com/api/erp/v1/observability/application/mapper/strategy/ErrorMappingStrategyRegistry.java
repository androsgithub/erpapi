package com.api.erp.v1.observability.application.mapper.strategy;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/**
 * Registry de estratégias de mapeamento de erro.
 * 
 * Mantém registro de todas as estratégias disponíveis e permite
 * encontrar a estratégia mais apropriada para uma exceção.
 * 
 * SOLID:
 * - Dependency Inversion: Depende de abstração (ErrorMappingStrategy)
 * - Single Responsibility: Apenas gerencia estratégias
 * 
 * DDD: Valor agregado que gerencia políticas de erro do domínio.
 */
public class ErrorMappingStrategyRegistry {

    private final List<ErrorMappingStrategy> strategies;

    public ErrorMappingStrategyRegistry() {
        this.strategies = new ArrayList<>();
    }

    /**
     * Registra uma nova estratégia.
     * Mantém lista ordenada por prioridade.
     * 
     * @param strategy a estratégia a registrar
     */
    public void register(ErrorMappingStrategy strategy) {
        Objects.requireNonNull(strategy, "Strategy não pode ser nula");
        this.strategies.add(strategy);
        this.strategies.sort(Comparator.comparingInt(ErrorMappingStrategy::getPriority).reversed());
    }

    /**
     * Encontra a primeira estratégia que possa tratar a exceção.
     * 
     * Estratégias são avaliadas em ordem de prioridade (decrescente).
     * A primeira estratégia que retornar true em canHandle() é retornada.
     * 
     * @param exception a exceção a tratar
     * @return a estratégia apropriada ou uma estratégia padrão
     */
    public ErrorMappingStrategy findStrategy(Throwable exception) {
        return this.strategies.stream()
            .filter(strategy -> strategy.canHandle(exception))
            .findFirst()
            .orElse(new DefaultErrorMappingStrategy());
    }

    /**
     * Retorna a quantidade de estratégias registradas.
     * 
     * @return quantidade de estratégias
     */
    public int getStrategyCount() {
        return this.strategies.size();
    }

    /**
     * Limpa todas as estratégias registradas.
     * Útil para testes ou reinicialização.
     */
    public void clear() {
        this.strategies.clear();
    }
}
