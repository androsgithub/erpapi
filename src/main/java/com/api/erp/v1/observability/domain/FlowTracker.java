package com.api.erp.v1.observability.domain;


public interface FlowTracker {

    /**
     * Emitido ao iniciar a execução de um passo.
     *
     * @param traceId identificador único do request
     * @param step    passo sendo rastreado
     */
    void onStart(String traceId, FlowStep step);

    /**
     * Emitido ao finalizar a execução de um passo.
     *
     * @param traceId         identificador único do request
     * @param step            passo rastreado
     * @param status          status da execução (sucesso ou erro)
     * @param executionTimeMs tempo de execução em milissegundos
     */
    void onFinish(String traceId, FlowStep step, FlowStatus status, int executionTimeMs);
}
