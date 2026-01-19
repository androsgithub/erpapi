package com.api.erp.v1.observability.presentation.dto;

import com.dros.observability.domain.FlowStatus;

import java.time.Instant;

/**
 * DTO para representação de evento de fluxo.
 * 
 * Transfere dados de FlowEventEntity para cliente
 * sem expor detalhes da entidade JPA.
 */
public record FlowEventDto(
    Long id,
    String traceId,
    String stepName,
    String status,
    int executionTimeMs,
    Instant timestamp
) {
    
    /**
     * Cria DTO a partir de status code.
     */
    public static FlowEventDto of(
        Long id,
        String traceId,
        String stepName,
        Integer statusCode,
        int executionTimeMs,
        Instant timestamp
    ) {
        String statusDescription = FlowStatus.fromCode(statusCode).getDescription();
        
        return new FlowEventDto(
            id,
            traceId,
            stepName,
            statusDescription,
            executionTimeMs,
            timestamp
        );
    }
}
