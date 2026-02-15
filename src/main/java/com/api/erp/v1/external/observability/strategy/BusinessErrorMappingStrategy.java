package com.api.erp.v1.external.observability.strategy;

import com.api.erp.v1.main.shared.domain.exception.BusinessException;
import com.dros.observability.core.mapper.strategy.ErrorMappingStrategy;
import com.dros.observability.domain.FlowStatus;

/**
 * Estratégia para mapeamento de erros de negócio.
 * 
 * Mapeia BusinessException e suas subclasses (como ProdutoException)
 * para FlowStatus.ERROR_VALIDATION, pois erros de negócio são essencialmente
 * violações de regras de domínio que deveriam ter sido validadas.
 * 
 * Prioridade alta pois é comum em operações de domínio.
 */
public class BusinessErrorMappingStrategy implements ErrorMappingStrategy {

    @Override
    public boolean canHandle(Throwable exception) {
        if (exception == null) {
            return false;
        }

        // Verificação com instanceof para type safety
        if (exception instanceof BusinessException) {
            return true;
        }

        // Fallback: verificação por nome de classe
        String className = exception.getClass().getName();
        return className.contains("BusinessException") ||
               className.contains("ProdutoException") ||
               className.contains("ClienteException") ||
               className.contains("DomainException");
    }

    @Override
    public FlowStatus map(Throwable exception) {
        // Erros de negócio são tratados como erros de validação
        // pois violam regras do domínio
        return FlowStatus.ERROR_VALIDATION;
    }

    /**
     * Prioridade alta, logo após validações diretas.
     * Garante que BusinessException seja mapeada antes de estratégias genéricas.
     */
    @Override
    public int getPriority() {
        return 88;
    }
}
