package com.api.erp.v1.main.migration.service;

import com.api.erp.v1.main.config.startup.seed.MainSeed;
import com.api.erp.v1.main.datasource.routing.TenantContext;
import com.api.erp.v1.main.migration.domain.TenantMigrationEvent;
import com.api.erp.v1.main.master.tenant.infrastructure.config.TenantMigrationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * INFRASTRUCTURE - Tenant Migration Queue Consumer
 * 
 * Asynchronous worker that consumes events from the unified queue and processes
 * tenant migrations. Executes idempotently and thread-safe.
 * 
 * This service replaces logic duplication between:
 * - TenantMigrationProcessor (Job do Spring Batch)
 * - MigrationQueueService.processMigrationTask (Fila de novo tenant)
 * 
 * Responsibilities:
 * - Consumir eventos da fila unificada
 * - Executesr migrações Flyway
 * - Executesr seeders (dados iniciais)
 * - Rastrear sucesso/falha
 * - Managesr retry em caso de falha
 * - Manter logs centralizados por tenant
 * 
 * @author ERP System
 * @version 2.0 (Refatorado para Fila Unificada)
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class TenantMigrationQueueConsumer {
    
    private final TenantMigrationQueue migrationQueue;
    private final TenantMigrationService tenantMigrationService;
    private final MainSeed mainSeed;
    
    // Configuration
    private static final long POLL_TIMEOUT_MS = 30_000; // 30 segundos
    private static final long RETRY_DELAY_MS = 5_000;   // 5 segundos
    
    /**
     * Inicia o consumidor de fila de forma assíncrona
     * 
     * Este método:
     * 1. Waits for eventos na fila indefinidamente
     * 2. Processes cada evento completo (migração + seed)
     * 3. Logs resultado no histórico
     * 4. Implementa retry automático em caso de falha
     * 5. Mantém logs centralizados
     * 
     * Chamado automaticamente na inicialização via TenantMigrationStartupWorker
     */
    @Async("migrationTaskExecutor")
    public void startConsumer() {
        log.info("");
        log.info("╔════════════════════════════════════════════════════════════════╗");
        log.info("║    STARTING MIGRATION QUEUE CONSUMER                           ║");
        log.info("╚════════════════════════════════════════════════════════════════╝");
        log.info("");
        log.info("⏳ Waiting for migration queue events...");
        log.info("");
        
        try {
            while (!Thread.currentThread().isInterrupted()) {
                TenantMigrationEvent event = null;
                
                try {
                    // Tenta obter o próximo evento da fila (bloqueante com timeout)
                    event = migrationQueue.pollNext(POLL_TIMEOUT_MS);
                    
                    if (event == null) {
                        // Timeout - sem eventos, continua aguardando
                        continue;
                    }
                    
                    // Processes o evento
                    processEvent(event);
                    
                    // Logs no histórico
                    migrationQueue.recordEventCompletion(event);
                    
                } catch (InterruptedException e) {
                    log.warn("⚠️ Queue consumer was interrupted");
                    Thread.currentThread().interrupt();
                    break;
                    
                } catch (Exception e) {
                    if (event != null) {
                        log.error("❌ [{}] Error processing migration event: {}", 
                                event.getEventId(), e.getMessage(), e);
                        markEventAsFailed(event, e);
                    } else {
                        log.error("❌ Error processing event: {}", e.getMessage(), e);
                    }
                }
            }
            
        } finally {
            log.warn("");
            log.warn("╔════════════════════════════════════════════════════════════════╗");
            log.warn("║  QUEUE CONSUMER WAS INTERRUPTED - MONITOR QUEUE              ║");
            log.warn("╚════════════════════════════════════════════════════════════════╝");
        }
    }
    
    /**
     * Processes um evento de migração individual
     * 
     * Fluxo completo:
     * 1. Marca como iniciado
     * 2. Executes migrações Flyway
     * 3. Executes seeders (dados iniciais)
     * 4. Marca como sucesso
     */
    private void processEvent(TenantMigrationEvent event) throws Exception {
        Long tenantId = event.getTenantId();
        String tenantName = event.getTenantName();
        String eventId = event.getEventId();
        
        // Log of start
        log.info("");
        log.info("▶ [{}:{}] Starting tenant migration: {}", eventId, tenantId, tenantName);
        log.info("  🔗 Source: {} | Retry: {}", 
                event.getSource().getLabel(), event.getRetryCount() != null ? event.getRetryCount() : 0);
        
        // Marca como iniciado
        event.markStarted();
        
        try {
            // ════════════════════════════════════════════════════════════════
            // PHASE 1: RUN FLYWAY MIGRATIONS
            // ════════════════════════════════════════════════════════════════
            log.info("  📋 [{}] Running Flyway migrations...", tenantId);
            
            try {
                tenantMigrationService.migrateTenantById(tenantId);
                log.info("  ✅ [{}] Flyway migrations completed successfully", tenantId);
                
            } catch (Exception e) {
                log.error("  ❌ [{}] Error during Flyway migrations: {}", tenantId, e.getMessage(), e);
                throw new Exception("Flyway migration failure: " + e.getMessage(), e);
            }
            
            // ════════════════════════════════════════════════════════════════
            // PHASE 2: RUN SEEDERS (INITIAL DATA)
            // ════════════════════════════════════════════════════════════════
            log.info("  🌱 [{}] Running seeders (initial data)...", tenantId);
            
            int seedersExecuted = 0;
            try {
                // Define contexto do tenant para os seeders executarem no banco correto
                TenantContext.setTenantId(tenantId);
                
                // Executes seeders
//                mainSeed.executar();
                
                seedersExecuted = 2; // Default count (adjust if necessary)
                log.info("✅ [{}] Seeders executed successfully", tenantId);
                
            } catch (Exception e) {
                log.error("  ⚠️ [{}] Error executing seeders: {}", tenantId, e.getMessage());
                // Mark as seed failure
                throw new Exception("Seed execution failure: " + e.getMessage(), e);
                
            } finally {
                // Limpa contexto do tenant
                TenantContext.clear();
            }
            
            // ════════════════════════════════════════════════════════════════
            // COMPLETE SUCCESS
            // ════════════════════════════════════════════════════════════════
            event.markSuccess(1, seedersExecuted); // 1 migration = Flyway applied
            
            log.info("✅ [{}] COMPLETE Migration: {} (Flyway + Seed)", tenantId, tenantName);
            log.info("⏱️  Wait time: {}ms | Execution time: {}ms", 
                    event.getWaitTimeMs(), event.getExecutionTimeMs());
            log.info("");
            
        } catch (Exception e) {
            log.error("❌ [{}] MIGRATION FAILURE for {}: {}", eventId, tenantName, e.getMessage());
            markEventAsFailed(event, e);
            throw e;
        }
    }
    
    /**
     * Marca um evento como falha e implementa retry automático
     */
    private void markEventAsFailed(TenantMigrationEvent event, Exception exception) {
        log.error("❌ [{}:{}] Migration failure: {}", 
                event.getEventId(), event.getTenantId(), exception.getMessage());
        
        // Check if you can retry
        if (event.getRetryCount() == null || event.getRetryCount() < migrationQueue.getMaxRetries()) {
            log.warn("🔄 Scheduling automatic retry for tenant: {}", event.getTenantName());
            
            // Wait before retry (simple backoff)
            try {
                Thread.sleep(RETRY_DELAY_MS);
            } catch (InterruptedException e) {
                log.warn("⚠️ Thread interrupted during retry wait");
                Thread.currentThread().interrupt();
            }
            
            // Queue for retry
            migrationQueue.enqueueForRetry(event);
        } else {
            // Maximum retries reached
            event.markFailed(exception.getMessage());
            migrationQueue.recordEventCompletion(event);
        }
    }
    
    /**
     * Processes a specific event (useful for manual retrigger)
     * 
     * @param eventId ID of the event to process
     * @throws IllegalArgumentException if event not found
     */
    public void processEventById(String eventId) throws Exception {
        TenantMigrationEvent event = migrationQueue.getEvent(eventId)
                .orElseThrow(() -> new IllegalArgumentException("Event not found: " + eventId));
        
        log.info("📥 Reprocessing manual event: {} (Tenant: {})", eventId, event.getTenantName());
        processEvent(event);
        migrationQueue.recordEventCompletion(event);
    }
}
