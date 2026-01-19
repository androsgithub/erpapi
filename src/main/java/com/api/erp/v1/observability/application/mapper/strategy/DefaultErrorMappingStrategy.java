package com.api.erp.v1.observability.application.mapper.strategy;

import com.api.erp.v1.observability.domain.FlowStatus;

/**
 * Estratégia padrão para erros não classificados.
 * 
 * Sempre aceita exceções que não foram mapeadas por outras estratégias.
 * Retorna FlowStatus.ERROR_THROW para indicar exceção desconhecida.
 */
public class DefaultErrorMappingStrategy implements ErrorMappingStrategy {

    @Override
    public boolean canHandle(Throwable exception) {
        // Estratégia padrão sempre aceita
        return true;
    }

    @Override
    public FlowStatus map(Throwable exception) {
        return FlowStatus.ERROR_THROW;
    }

    /**
     * Prioridade mínima para garantir que seja a última a ser avaliada.
     */
    @Override
    public int getPriority() {
        return 0;
    }
}
