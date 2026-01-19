package com.api.erp.v1.observability.application.context;

import java.util.UUID;

/**
 * Gerenciador de contexto de rastreamento por thread.
 * Mantém o traceId associado à requisição atual.
 */
public final class TraceContext {
    private static final ThreadLocal<String> TRACE_ID = ThreadLocal.withInitial(() -> UUID.randomUUID().toString());

    private TraceContext() {
        throw new AssertionError("Classe não deve ser instanciada");
    }

    /**
     * Obtém o traceId da thread atual.
     * Cria um novo se não existir.
     */
    public static String getTraceId() {
        return TRACE_ID.get();
    }

    /**
     * Define um traceId específico para a thread.
     * Útil para propagação entre threads ou em testes.
     */
    public static void setTraceId(String traceId) {
        TRACE_ID.set(traceId);
    }

    /**
     * Limpa o traceId da thread.
     * Deve ser chamado ao final do processamento (em filtro ou interceptor).
     */
    public static void clear() {
        TRACE_ID.remove();
    }

    /**
     * Reseta o contexto com um novo traceId.
     */
    public static void reset() {
        clear();
        getTraceId(); // garante uma nova inicialização
    }
}
