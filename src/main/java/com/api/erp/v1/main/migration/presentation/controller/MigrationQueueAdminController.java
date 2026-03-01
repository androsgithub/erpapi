package com.api.erp.v1.main.migration.presentation.controller;

import com.api.erp.v1.docs.openapi.migration.MigrationQueueAdminOpenApiDocumentation;
import com.api.erp.v1.main.migration.domain.TenantMigrationEvent;
import com.api.erp.v1.main.migration.service.TenantMigrationQueueConsumer;
import com.api.erp.v1.main.migration.service.TenantMigrationQueue;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * PRESENTATION - REST Controller para Administração da Fila Unificada de Migrações
 * 
 * Oferece endpoints para monitoramento, análise e administração
 * da fila de processamento de migrações de tenants.
 * 
 * Features:
 * - Visualizar estatísticas gerais da fila
 * - Listar eventos por status (pendentes, em progresso, completos, falhados)
 * - Consultar detalhes de eventos específicos
 * - Reprocessar eventos que falharam
 * - Monitorar progresso e taxa de sucesso
 * 
 * Endpoints:
 * - GET /api/v1/admin/migrations/queue/stats - Estatísticas da fila
 * - GET /api/v1/admin/migrations/queue/events - Listar todos os eventos
 * - GET /api/v1/admin/migrations/queue/events/{eventId} - Detalhes de um evento
 * - GET /api/v1/admin/migrations/queue/events/pending - Eventos pendentes
 * - GET /api/v1/admin/migrations/queue/events/failed - Eventos com falha
 * - GET /api/v1/admin/migrations/queue/events/completed - Eventos completados
 * - GET /api/v1/admin/migrations/queue/events/in-progress - Eventos em progresso
 * - POST /api/v1/admin/migrations/queue/reprocess/{eventId} - Reprocessar evento
 * 
 * Requer autenticação de administrador.
 * 
 * @author ERP System
 * @version 1.0 (Fila Unificada)
 */
@RestController
@RequestMapping("/api/v1/migrations/queue")
@RequiredArgsConstructor
@Slf4j
public class MigrationQueueAdminController implements MigrationQueueAdminOpenApiDocumentation {
    
    private final TenantMigrationQueue migrationQueue;
    private final TenantMigrationQueueConsumer queueConsumer;
    
    /**
     * GET - Retorna estatísticas da fila
     */
    @Override
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getQueueStats() {
        var stats = migrationQueue.getStats();
        
        Map<String, Object> response = new HashMap<>();
        response.put("totalEvents", stats.getTotalEvents());
        response.put("pendingEvents", stats.getPendingEvents());
        response.put("inProgressEvents", stats.getInProgressEvents());
        response.put("completedEvents", stats.getCompletedEvents());
        response.put("failedEvents", stats.getFailedEvents());
        response.put("queueSize", stats.getQueueSize());
        response.put("progressPercent", stats.getProgress());
        response.put("successRatePercent", stats.getSuccessRate());
        response.put("totalExecutionTimeMs", stats.getTotalExecutionTimeMs());
        response.put("avgExecutionTimeMs", Math.round(stats.getAvgExecutionTimeMs()));
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * GET - Lista todos os eventos
     */
    @Override
    @GetMapping("/events")
    public ResponseEntity<Map<String, Object>> getAllEvents() {
        Collection<TenantMigrationEvent> events = migrationQueue.getAllEvents();
        
        Map<String, Object> response = new HashMap<>();
        response.put("count", events.size());
        response.put("events", events.stream().map(this::eventToMap).toList());
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * GET - Detalhes de um evento específico
     */
    @Override
    @GetMapping("/events/{eventId}")
    public ResponseEntity<?> getEvent(@PathVariable String eventId) {
        return migrationQueue.getEvent(eventId)
                .map(event -> ResponseEntity.ok(eventToMap(event)))
                .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * GET - Eventos pendentes
     */
    @Override
    @GetMapping("/events/pending")
    public ResponseEntity<Map<String, Object>> getPendingEvents() {
        Collection<TenantMigrationEvent> events = migrationQueue.getPendingEvents();
        
        Map<String, Object> response = new HashMap<>();
        response.put("count", events.size());
        response.put("events", events.stream().map(this::eventToMap).toList());
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * GET - Eventos com falha
     */
    @Override
    @GetMapping("/events/failed")
    public ResponseEntity<Map<String, Object>> getFailedEvents() {
        Collection<TenantMigrationEvent> events = migrationQueue.getFailedEvents();
        
        Map<String, Object> response = new HashMap<>();
        response.put("count", events.size());
        response.put("events", events.stream().map(this::eventToMap).toList());
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * GET - Eventos completados
     */
    @Override
    @GetMapping("/events/completed")
    public ResponseEntity<Map<String, Object>> getCompletedEvents() {
        Collection<TenantMigrationEvent> events = migrationQueue.getCompletedEvents();
        
        Map<String, Object> response = new HashMap<>();
        response.put("count", events.size());
        response.put("events", events.stream().map(this::eventToMap).toList());
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * GET - Eventos em progresso
     */
    @Override
    @GetMapping("/events/in-progress")
    public ResponseEntity<Map<String, Object>> getInProgressEvents() {
        Collection<TenantMigrationEvent> events = migrationQueue.getInProgressEvents();
        
        Map<String, Object> response = new HashMap<>();
        response.put("count", events.size());
        response.put("events", events.stream().map(this::eventToMap).toList());
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * POST - Reprocessar um evento específico
     * 
     * Útil para reprocessar eventos que falharam após resolver o problema
     */
    @Override
    @PostMapping("/reprocess/{eventId}")
    public ResponseEntity<Map<String, Object>> reprocessEvent(@PathVariable String eventId) {
        try {
            log.info("🔄 Reprocessando evento: {}", eventId);
            queueConsumer.processEventById(eventId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "Evento reprocessado com sucesso");
            
            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("❌ Erro ao reprocessar evento {}: {}", eventId, e.getMessage(), e);
            
            Map<String, Object> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", "Erro ao reprocessar evento: " + e.getMessage());
            
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * Converte um evento para Map (para resposta JSON)
     */
    private Map<String, Object> eventToMap(TenantMigrationEvent event) {
        Map<String, Object> map = new HashMap<>();
        map.put("eventId", event.getEventId());
        map.put("tenantId", event.getTenantId());
        map.put("tenantName", event.getTenantName());
        map.put("status", event.getStatus().getLabel());
        map.put("source", event.getSource().getLabel());
        map.put("enqueuedAt", event.getEnqueuedAt());
        map.put("startedAt", event.getStartedAt());
        map.put("completedAt", event.getCompletedAt());
        map.put("waitTimeMs", event.getWaitTimeMs());
        map.put("executionTimeMs", event.getExecutionTimeMs());
        map.put("migrationsExecuted", event.getMigrationsExecuted());
        map.put("seedersExecuted", event.getSeedersExecuted());
        map.put("retryCount", event.getRetryCount());
        map.put("errorMessage", event.getErrorMessage());
        map.put("result", event.getResult() != null ? event.getResult().getLabel() : null);
        return map;
    }
}
