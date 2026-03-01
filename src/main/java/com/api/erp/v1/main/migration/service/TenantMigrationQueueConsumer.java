package com.api.erp.v1.main.migration.service;

import com.api.erp.v1.main.config.startup.seed.MainSeed;
import com.api.erp.v1.main.datasource.routing.TenantContext;
import com.api.erp.v1.main.migration.domain.TenantMigrationEvent;
import com.api.erp.v1.main.tenant.infrastructure.config.TenantMigrationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * INFRASTRUCTURE - Consumidor de Fila de Migrações de Tenants
 * 
 * Worker assíncrono que consome eventos da fila unificada e processa
 * migrações de tenants. Executa de forma idempotente e thread-safe.
 * 
 * Este serviço substitui a duplicação de lógica entre:
 * - TenantMigrationProcessor (Job do Spring Batch)
 * - MigrationQueueService.processMigrationTask (Fila de novo tenant)
 * 
 * Responsabilidades:
 * - Consumir eventos da fila unificada
 * - Executar migrações Flyway
 * - Executar seeders (dados iniciais)
 * - Rastrear sucesso/falha
 * - Gerenciar retry em caso de falha
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
    
    // Configuração
    private static final long POLL_TIMEOUT_MS = 30_000; // 30 segundos
    private static final long RETRY_DELAY_MS = 5_000;   // 5 segundos
    
    /**
     * Inicia o consumidor de fila de forma assíncrona
     * 
     * Este método:
     * 1. Aguarda eventos na fila indefinidamente
     * 2. Processa cada evento completo (migração + seed)
     * 3. Registra resultado no histórico
     * 4. Implementa retry automático em caso de falha
     * 5. Mantém logs centralizados
     * 
     * Chamado automaticamente na inicialização via TenantMigrationStartupWorker
     */
    @Async("migrationTaskExecutor")
    public void startConsumer() {
        log.info("");
        log.info("╔════════════════════════════════════════════════════════════════╗");
        log.info("║    INICIANDO CONSUMIDOR DE FILA DE MIGRAÇÕES DE TENANTS        ║");
        log.info("╚════════════════════════════════════════════════════════════════╝");
        log.info("");
        log.info("⏳ Aguardando eventos na fila de migrações...");
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
                    
                    // Processa o evento
                    processEvent(event);
                    
                    // Registra no histórico
                    migrationQueue.recordEventCompletion(event);
                    
                } catch (InterruptedException e) {
                    log.warn("⚠️ Consumidor de fila foi interrompido");
                    Thread.currentThread().interrupt();
                    break;
                    
                } catch (Exception e) {
                    if (event != null) {
                        log.error("❌ [{}] Erro ao processar evento de migração: {}", 
                                event.getEventId(), e.getMessage(), e);
                        markEventAsFailed(event, e);
                    } else {
                        log.error("❌ Erro ao processar evento: {}", e.getMessage(), e);
                    }
                }
            }
            
        } finally {
            log.warn("");
            log.warn("╔════════════════════════════════════════════════════════════════╗");
            log.warn("║  CONSUMIDOR DE FILA FOI INTERROMPIDO - MONITORAR FILA         ║");
            log.warn("╚════════════════════════════════════════════════════════════════╝");
        }
    }
    
    /**
     * Processa um evento de migração individual
     * 
     * Fluxo completo:
     * 1. Marca como iniciado
     * 2. Executa migrações Flyway
     * 3. Executa seeders (dados iniciais)
     * 4. Marca como sucesso
     */
    private void processEvent(TenantMigrationEvent event) throws Exception {
        Long tenantId = event.getTenantId();
        String tenantName = event.getTenantName();
        String eventId = event.getEventId();
        
        // Log de início
        log.info("");
        log.info("▶ [{}:{}] Iniciando migração do tenant: {}", eventId, tenantId, tenantName);
        log.info("  🔗 Origem: {} | Retry: {}", 
                event.getSource().getLabel(), event.getRetryCount() != null ? event.getRetryCount() : 0);
        
        // Marca como iniciado
        event.markStarted();
        
        try {
            // ════════════════════════════════════════════════════════════════
            // FASE 1: EXECUTAR MIGRAÇÕES FLYWAY
            // ════════════════════════════════════════════════════════════════
            log.info("  📋 [{}] Executando migrações Flyway...", tenantId);
            
            try {
                tenantMigrationService.migrateTenantById(tenantId);
                log.info("  ✅ [{}] Migrações Flyway concluídas com sucesso", tenantId);
                
            } catch (Exception e) {
                log.error("  ❌ [{}] Erro durante migrações Flyway: {}", tenantId, e.getMessage(), e);
                throw new Exception("Falha na migração Flyway: " + e.getMessage(), e);
            }
            
            // ════════════════════════════════════════════════════════════════
            // FASE 2: EXECUTAR SEEDERS (DADOS INICIAIS)
            // ════════════════════════════════════════════════════════════════
            log.info("  🌱 [{}] Executando seeders (dados iniciais)...", tenantId);
            
            int seedersExecuted = 0;
            try {
                // Define contexto do tenant para os seeders executarem no banco correto
                TenantContext.setTenantId(tenantId);
                
                // Executa seeders
                mainSeed.executar();
                
                seedersExecuted = 2; // Contagem padrão (ajustar se necessário)
                log.info("  ✅ [{}] Seeders executados com sucesso", tenantId);
                
            } catch (Exception e) {
                log.error("  ⚠️ [{}] Erro ao executar seeders: {}", tenantId, e.getMessage());
                // Marca como falha no seeder
                throw new Exception("Falha na execução de seeders: " + e.getMessage(), e);
                
            } finally {
                // Limpa contexto do tenant
                TenantContext.clear();
            }
            
            // ════════════════════════════════════════════════════════════════
            // SUCESSO TOTAL
            // ════════════════════════════════════════════════════════════════
            event.markSuccess(1, seedersExecuted); // 1 migração = Flyway aplicado
            
            log.info("✅ [{}] Migração COMPLETA: {} (Flyway + Seed)", tenantId, tenantName);
            log.info("⏱️  Tempo de espera: {}ms | Tempo de execução: {}ms", 
                    event.getWaitTimeMs(), event.getExecutionTimeMs());
            log.info("");
            
        } catch (Exception e) {
            log.error("❌ [{}] FALHA NA MIGRAÇÃO de {}: {}", eventId, tenantName, e.getMessage());
            markEventAsFailed(event, e);
            throw e;
        }
    }
    
    /**
     * Marca um evento como falha e implementa retry automático
     */
    private void markEventAsFailed(TenantMigrationEvent event, Exception exception) {
        log.error("❌ [{}:{}] Falha na migração: {}", 
                event.getEventId(), event.getTenantId(), exception.getMessage());
        
        // Verifica se pode fazer retry
        if (event.getRetryCount() == null || event.getRetryCount() < migrationQueue.getMaxRetries()) {
            log.warn("🔄 Agendando retry automático para tenant: {}", event.getTenantName());
            
            // Aguarda antes de fazer retry (backoff simples)
            try {
                Thread.sleep(RETRY_DELAY_MS);
            } catch (InterruptedException e) {
                log.warn("⚠️ Thread interrompida durante espera de retry");
                Thread.currentThread().interrupt();
            }
            
            // Enfileira para retry
            migrationQueue.enqueueForRetry(event);
        } else {
            // Máximo de retries atingido
            event.markFailed(exception.getMessage());
            migrationQueue.recordEventCompletion(event);
        }
    }
    
    /**
     * Processa um evento específico (útil para retrigger manual)
     * 
     * @param eventId ID do evento a processar
     * @throws IllegalArgumentException se evento não encontrado
     */
    public void processEventById(String eventId) throws Exception {
        TenantMigrationEvent event = migrationQueue.getEvent(eventId)
                .orElseThrow(() -> new IllegalArgumentException("Evento não encontrado: " + eventId));
        
        log.info("📥 Reprocessando evento manual: {} (Tenant: {})", eventId, event.getTenantName());
        processEvent(event);
        migrationQueue.recordEventCompletion(event);
    }
}
