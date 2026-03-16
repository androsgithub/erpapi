package com.api.erp.v1.main.migration.service;

import com.api.erp.v1.main.migration.domain.TenantMigrationEvent;
import com.api.erp.v1.main.master.tenant.domain.entity.TenantDatasource;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * DOMAIN - Fila Centralizada de Migrações de Tenants
 * 
 * Serviço responsável por gerenciar uma única fila unificada que:
 * - Recebe eventos de migrações da inicialização da aplicação
 * - Recebe eventos de migrações de criação de novos tenants
 * - Fornece acesso aos consumidores de fila
 * - Rastreia o status de todos os eventos
 * 
 * Thread-safe e suporta execução concorrente controlada.
 * 
 * Responsibilities:
 * - Manter fila centralizada (BlockingQueue)
 * - Logsr eventos para rastreamento
 * - Fornecer estatísticas e monitoramento
 * - Managesr retry de eventos com falha
 * - Fornecer histórico de execução
 * 
 * @author ERP System
 * @version 2.0 (Refatorado para Fila Unificada)
 */
@Service
@Slf4j
public class TenantMigrationQueue {
    
    // Fila thread-safe de eventos
    private final BlockingQueue<TenantMigrationEvent> eventQueue = new LinkedBlockingQueue<>();
    
    // Registro de eventos para rastreamento
    private final Map<String, TenantMigrationEvent> eventRegistry = new ConcurrentHashMap<>();
    
    // Histórico de eventos processados
    private final Queue<TenantMigrationEvent> eventHistory = new ConcurrentLinkedQueue<>();
    
    // Configuration de Retry
    @Getter
    private final int maxRetries = 3;
    
    @Getter
    private final long retryDelayMs = 5000; // 5 segundos
    
    /**
     * Verifica se já existe uma migração pendente ou em progresso para um tenant
     * 
     * Isso evita duplicação desnecessária de eventos para o mesmo tenant.
     * 
     * @param tenantId ID do tenant
     * @return true se houver migração pendente/em progresso, false caso contrário
     */
    public boolean hasPendingMigration(Long tenantId) {
        return eventRegistry.values().stream()
                .anyMatch(event -> event.getTenantId().equals(tenantId) &&
                        (event.getStatus() == TenantMigrationEvent.MigrationEventStatus.PENDING ||
                         event.getStatus() == TenantMigrationEvent.MigrationEventStatus.IN_PROGRESS));
    }
    
    /**
     * Gets uma migração pendente ou em progresso para um tenant
     * 
     * @param tenantId ID do tenant
     * @return Evento existente ou Optional.empty() se não houver
     */
    public Optional<TenantMigrationEvent> getPendingMigrationForTenant(Long tenantId) {
        return eventRegistry.values().stream()
                .filter(event -> event.getTenantId().equals(tenantId) &&
                        (event.getStatus().equals(TenantMigrationEvent.MigrationEventStatus.PENDING) ||
                         event.getStatus().equals(TenantMigrationEvent.MigrationEventStatus.IN_PROGRESS)))
                .findFirst();
    }
    
    /**
     * Enfileira um evento de migração
     */
    public void enqueueEvent(TenantMigrationEvent event) {
        try {
            eventQueue.offer(event);
            eventRegistry.put(event.getEventId(), event);
            
            log.info("📥 [{}] Event queued: Tenant {} (Source: {})",
                    event.getEventId(),
                    event.getTenantName(),
                    event.getSource().getLabel());
                    
        } catch (Exception e) {
            log.error("❌ [{}] Error queuing event: {}", 
                    event.getEventId(), e.getMessage(), e);
            throw new RuntimeException("Error queuing migration event", e);
        }
    }
    
    /**
     * Enfileira um evento criado a partir dos dados do tenant
     * 
     * Verifica se já existe uma migração pendente/em progresso para evitar duplicação.
     * Se houver, retorna o evento existente sem enfileirar novamente.
     */
    public TenantMigrationEvent enqueueEvent(Long tenantId, String tenantName, 
                                             TenantDatasource datasource,
                                             TenantMigrationEvent.MigrationEventSource source) {
        // Check if there is already a pending/in-progress migration
        Optional<TenantMigrationEvent> existingMigration = getPendingMigrationForTenant(tenantId);
        if (existingMigration.isPresent()) {
            TenantMigrationEvent existing = existingMigration.get();
            log.warn("⚠️ There is already a pending migration for tenant {} (EventID: {}). " +
                    "Returning existing event without duplicating.",
                    tenantId, existing.getEventId());
            return existing;
        }

        TenantMigrationEvent event = TenantMigrationEvent.create(tenantId, tenantName, datasource, source);
        enqueueEvent(event);
        return event;
    }
    
    /**
     * Gets o próximo evento da fila (bloqueante)
     * Usado pelo consumidor de fila para processar eventos
     * 
     * @param timeout Timeout em ms
     * @return Evento ou null se timeout
     */
    public TenantMigrationEvent pollNext(long timeout) throws InterruptedException {
        return eventQueue.poll(timeout, TimeUnit.MILLISECONDS);
    }
    
    /**
     * Tenta obter o próximo evento sem bloquear
     */
    public TenantMigrationEvent pollNextNonBlocking() {
        return eventQueue.poll();
    }
    
    /**
     * Logs um evento como processado no histórico
     */
    public void recordEventCompletion(TenantMigrationEvent event) {
        eventHistory.offer(event);
        log.info("📊 [{}] Event recorded in history: Tenant {} (Status: {})",
                event.getEventId(),
                event.getTenantName(),
                event.getStatus().getLabel());
    }
    
    /**
     * Enfileira um evento para retry
     */
    public void enqueueForRetry(TenantMigrationEvent event) {
        if (event.getRetryCount() != null && event.getRetryCount() >= maxRetries) {
            log.error("❌ [{}] Maximum retries reached for tenant {}", 
                    event.getEventId(), event.getTenantName());
            event.markFailed("Maximum attempts reached: " + event.getErrorMessage());
            recordEventCompletion(event);
            return;
        }
        
        event.markForRetry();
        log.warn("🔄 [{}] Event queued for retry: Tenant {} (Attempt: {})",
                event.getEventId(),
                event.getTenantName(),
                event.getRetryCount());
        
        enqueueEvent(event);
    }
    
    /**
     * Gets um evento pelo ID
     */
    public Optional<TenantMigrationEvent> getEvent(String eventId) {
        return Optional.ofNullable(eventRegistry.get(eventId));
    }
    
    /**
     * Lista eventos pendentes/em progresso para um tenant específico
     */
    public Collection<TenantMigrationEvent> getEventsByTenant(Long tenantId) {
        return eventRegistry.values().stream()
                .filter(event -> event.getTenantId().equals(tenantId))
                .collect(Collectors.toList());
    }
    
    /**
     * Gets todos os eventos registrados (incluindo histórico)
     */
    public Collection<TenantMigrationEvent> getAllEvents() {
        Set<TenantMigrationEvent> allEvents = new HashSet<>(eventRegistry.values());
        allEvents.addAll(eventHistory);
        return Collections.unmodifiableCollection(allEvents);
    }
    
    /**
     * Gets eventos pendentes
     */
    public Collection<TenantMigrationEvent> getPendingEvents() {
        return eventRegistry.values().stream()
                .filter(e -> e.getStatus() == TenantMigrationEvent.MigrationEventStatus.PENDING)
                .collect(Collectors.toList());
    }
    
    /**
     * Gets eventos com falha
     */
    public Collection<TenantMigrationEvent> getFailedEvents() {
        return getAllEvents().stream()
                .filter(e -> e.getStatus() == TenantMigrationEvent.MigrationEventStatus.FAILED)
                .collect(Collectors.toList());
    }
    
    /**
     * Gets eventos concluídos
     */
    public Collection<TenantMigrationEvent> getCompletedEvents() {
        return getAllEvents().stream()
                .filter(e -> e.getStatus() == TenantMigrationEvent.MigrationEventStatus.COMPLETED)
                .collect(Collectors.toList());
    }
    
    /**
     * Gets eventos em progresso
     */
    public Collection<TenantMigrationEvent> getInProgressEvents() {
        return eventRegistry.values().stream()
                .filter(e -> e.getStatus() == TenantMigrationEvent.MigrationEventStatus.IN_PROGRESS)
                .collect(Collectors.toList());
    }
    
    /**
     * Returns o tamanho da fila
     */
    public int getQueueSize() {
        return eventQueue.size();
    }
    
    /**
     * Verifica se a fila está vazia
     */
    public boolean isQueueEmpty() {
        return eventQueue.isEmpty();
    }
    
    /**
     * Returns estatísticas da fila
     */
    public MigrationQueueStats getStats() {
        Collection<TenantMigrationEvent> allEvents = getAllEvents();
        
        long totalEvents = allEvents.size();
        long pendingEvents = allEvents.stream()
                .filter(e -> e.getStatus() == TenantMigrationEvent.MigrationEventStatus.PENDING)
                .count();
        long inProgressEvents = allEvents.stream()
                .filter(e -> e.getStatus() == TenantMigrationEvent.MigrationEventStatus.IN_PROGRESS)
                .count();
        long completedEvents = allEvents.stream()
                .filter(e -> e.getStatus() == TenantMigrationEvent.MigrationEventStatus.COMPLETED)
                .count();
        long failedEvents = allEvents.stream()
                .filter(e -> e.getStatus() == TenantMigrationEvent.MigrationEventStatus.FAILED)
                .count();
        
        long totalExecutionTime = allEvents.stream()
                .mapToLong(TenantMigrationEvent::getExecutionTimeMs)
                .sum();
        
        double avgExecutionTime = totalEvents > 0 ? 
                (double) totalExecutionTime / allEvents.stream()
                    .filter(e -> e.getExecutionTimeMs() > 0)
                    .count() : 0;
        
        return new MigrationQueueStats(
                totalEvents, pendingEvents, inProgressEvents, 
                completedEvents, failedEvents, 
                eventQueue.size(), totalExecutionTime, avgExecutionTime
        );
    }
    
    /**
     * Clears the queue (for testing only!)
     */
    public void clear() {
        eventQueue.clear();
        eventRegistry.clear();
        log.warn("⚠️ Migration queue was completely cleared!");
    }
    
    /**
     * DTO para estatísticas da fila
     */
    @lombok.Getter
    public static class MigrationQueueStats {
        private final long totalEvents;
        private final long pendingEvents;
        private final long inProgressEvents;
        private final long completedEvents;
        private final long failedEvents;
        private final int queueSize;
        private final long totalExecutionTimeMs;
        private final double avgExecutionTimeMs;
        
        public MigrationQueueStats(long totalEvents, long pendingEvents, long inProgressEvents,
                                   long completedEvents, long failedEvents, int queueSize,
                                   long totalExecutionTimeMs, double avgExecutionTimeMs) {
            this.totalEvents = totalEvents;
            this.pendingEvents = pendingEvents;
            this.inProgressEvents = inProgressEvents;
            this.completedEvents = completedEvents;
            this.failedEvents = failedEvents;
            this.queueSize = queueSize;
            this.totalExecutionTimeMs = totalExecutionTimeMs;
            this.avgExecutionTimeMs = avgExecutionTimeMs;
        }
        
        public double getProgress() {
            if (totalEvents == 0) return 100.0;
            return ((double) (completedEvents + failedEvents) / totalEvents) * 100.0;
        }
        
        public double getSuccessRate() {
            long processedEvents = completedEvents + failedEvents;
            if (processedEvents == 0) return 100.0;
            return ((double) completedEvents / processedEvents) * 100.0;
        }
    }
}
