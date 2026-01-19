package com.api.erp.v1.observability.application.mapper.strategy;

import com.api.erp.v1.observability.domain.FlowStatus;

/**
 * Estratégia para mapeamento de erros de estado inválido.
 * 
 * Mapeia IllegalStateException para FlowStatus.ERROR_VALIDATION.
 * Erros de estado inválido indicam que uma operação foi tentada em um
 * contexto ou estado incompatível, frequentemente violando precondições.
 * 
 * Exemplo:
 * - Tentar migrar tenant sem datasource configurado
 * - Tentar autenticar sem usuário válido no contexto
 * - Tentar operar em recurso em estado bloqueado
 */
public class IllegalStateErrorMappingStrategy implements ErrorMappingStrategy {

    @Override
    public boolean canHandle(Throwable exception) {
        if (exception == null) {
            return false;
        }

        return exception instanceof IllegalStateException;
    }

    @Override
    public FlowStatus map(Throwable exception) {
        return FlowStatus.ERROR_VALIDATION;
    }

    /**
     * Prioridade logo após BusinessException.
     * Estados inválidos são um tipo comum de validação em tempo de execução.
     */
    @Override
    public int getPriority() {
        return 87;
    }
}
