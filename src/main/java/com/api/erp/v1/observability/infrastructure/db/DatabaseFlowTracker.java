package com.api.erp.v1.observability.infrastructure.db;

import com.api.erp.v1.observability.domain.FlowEvent;
import com.api.erp.v1.observability.domain.FlowStatus;
import com.api.erp.v1.observability.domain.FlowStep;
import com.api.erp.v1.observability.domain.FlowTracker;
import com.api.erp.v1.observability.domain.entity.FlowEventEntity;
import com.api.erp.v1.observability.domain.repository.FlowEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * DatabaseFlowTracker implementa FlowTracker persistindo eventos no banco erpapi_logs.
 * 
 * Esta implementação salva todos os eventos de fluxo (START/SUCCESS/ERROR) em um banco
 * de dados dedicado para logs, isolando dados de observabilidade do banco principal.
 * 
 * IMPORTANTE: Usa @Transactional com transactionManager="logsTransactionManager" para
 * garantir que as operações sejam executadas no contexto de transação correto do banco
 * de logs, especialmente quando chamado de forma assíncrona via CompositeFlowTracker.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DatabaseFlowTracker implements FlowTracker {

    private final FlowEventRepository repository;

    @Override
    public void onStart(String traceId, FlowStep step) {
        // Criar evento START e persistir
        FlowEvent event = new FlowEvent(traceId, step, FlowStatus.START, 0);
        persistEvent(event);
    }

    @Override
    public void onFinish(String traceId, FlowStep step, FlowStatus status, int executionTimeMs) {
        // Criar evento FINISH (SUCCESS ou ERROR_*) e persistir
        FlowEvent event = new FlowEvent(traceId, step, status, executionTimeMs);
        persistEvent(event);
    }

    @Transactional(transactionManager = "logsTransactionManager")
    public void persistEvent(FlowEvent event) {
        try {
            FlowEventEntity flowEventEntity = new FlowEventEntity(
                event.getTraceId(),
                event.getStep().getId(),
                event.getStep().getName(),
                event.getStatus().getCode(),
                event.getExecutionTimeMs(),
                event.getTimestamp()
            );

            repository.save(flowEventEntity);

            log.info("[DatabaseFlowTracker] ✓ Evento SALVO com sucesso no banco erpapi_logs: " +
                "traceId={}, step={}, status={}, executionTime={}ms",
                event.getTraceId(),
                event.getStep().getName(),
                event.getStatus(),
                event.getExecutionTimeMs()
            );
        } catch (Exception e) {
            log.error("[DatabaseFlowTracker] ✗ Erro ao persistir evento no banco erpapi_logs para traceId: " +
                event.getTraceId(), e);
            throw new RuntimeException("Falha ao persistir evento de fluxo", e);
        }
    }
}

