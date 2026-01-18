package com.api.erp.v1.observability.infrastructure.messaging;

import com.api.erp.v1.observability.domain.FlowEvent;
import com.api.erp.v1.observability.domain.FlowStep;
import com.api.erp.v1.observability.domain.FlowStatus;
import com.api.erp.v1.observability.domain.FlowTracker;
import org.springframework.stereotype.Component;
@Component
public class KafkaFlowTracker implements FlowTracker {
    // private final KafkaTemplate<String, String> kafkaTemplate; // Injetar quando necessário
    // private final ObjectMapper objectMapper; // Para serializar eventos

    private static final String TOPIC_FLOW_EVENTS = "observability.flow-events";

    @Override
    public void onStart(String traceId, FlowStep step) {
        // Criar evento START e publicar
        FlowEvent event = new FlowEvent(traceId, step, FlowStatus.START, 0);
        publishEvent(event);
    }

    @Override
    public void onFinish(String traceId, FlowStep step, FlowStatus status, int executionTimeMs) {
        // Criar evento FINISH (SUCCESS ou ERROR_*) e publicar
        FlowEvent event = new FlowEvent(traceId, step, status, executionTimeMs);
        publishEvent(event);
    }

    private void publishEvent(FlowEvent event) {
        // Exemplo:
        // String payload = objectMapper.writeValueAsString(event);
        // kafkaTemplate.send(TOPIC_FLOW_EVENTS, event.getTraceId(), payload);
        
        // Por enquanto, apenas log para demonstração
//        System.out.println("[KafkaFlowTracker] Publicando evento no tópico: " + event);
    }
}
