package com.api.erp.v1.main.migration.startup;

import com.api.erp.v1.main.migration.domain.TenantMigrationEvent;
import com.api.erp.v1.main.migration.service.TenantMigrationQueueConsumer;
import com.api.erp.v1.main.migration.service.TenantMigrationQueue;
import com.api.erp.v1.main.master.tenant.domain.entity.TenantDatasource;
import com.api.erp.v1.main.master.tenant.domain.repository.TenantDatasourceRepository;
import com.api.erp.v1.main.master.tenant.domain.repository.TenantRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * INFRASTRUCTURE - Worker de Initialization para Fila Unificada de Migrações
 * 
 * Responsável por:
 * 1. Enfileirar TODOS os tenants ativos ao iniciar a aplicação
 * 2. Iniciar o consumidor de fila que processará continuamente
 * 
 * Este componente é chamado por ApplicationStartupListener e unifica:
 * - O comportamento de MTMigrationBoostrap + MigrationJobLauncher
 * - O comportamento de MigrationQueueService.enqueueAllTenantMigrations
 * 
 * Vantagens da Nova Abordagem:
 * ✅ Um único fluxo de processamento
 * ✅ Sem duplicação de lógica
 * ✅ Logs centralizados
 * ✅ Fácil rastreabilidade por tenant
 * ✅ Controle de execução centralizado
 * ✅ Suporte a retry automático
 * 
 * @author ERP System
 * @version 2.0 (Novo - Fila Unificada)
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class TenantMigrationStartupWorker {
    
    private final TenantMigrationQueue migrationQueue;
    private final TenantMigrationQueueConsumer queueConsumer;
    private final TenantRepository tenantRepository;
    private final TenantDatasourceRepository tenantDatasourceRepository;
    
    /**
     * Executes a inicialização da fila de migrações
     * 
     * Fluxo:
     * 1. Busca todos os tenants ativos
     * 2. Enfileira cada um para migração
     * 3. Inicializa o consumidor que processará a fila continuamente
     * 
     * Chamado por ApplicationStartupListener
     */
    public void initializeAndStart() {
        log.info("");
        log.info("");
        log.info("");
        log.info("╔════════════════════════════════════════════════════════════════╗");
        log.info("║    INICIALIZANDO FILA UNIFICADA DE MIGRAÇÕES DE TENANTS        ║");
        log.info("╚════════════════════════════════════════════════════════════════╝");
        log.info("");
        
        try {
            // ────────────────────────────────────────────────────────────
            // PHASE 1: Enfileirar todos os tenants ativos
            // ────────────────────────────────────────────────────────────
            log.info("📋 Buscando tenants ativos para enfileiramento...");
            
            var tenants = tenantRepository.findAllByActiveTrue();
            
            if (tenants.isEmpty()) {
                log.warn("⚠️ No active tenant found for migration");
                log.info("✅ Initialization completed (no tenants)");
                return;
            }
            
            log.info("✅ Encontrados {} tenants ativos", tenants.size());
            log.info("");
            log.info("📥 Enfileirando tenants...");
            
            int enqueuedCount = 0;
            int skippedCount = 0;
            
            for (var tenant : tenants) {
                try {
                    // Busca datasource do tenant
                    TenantDatasource datasource = tenantDatasourceRepository
                            .findByTenantIdAndActiveTrue(tenant.getId()).orElse(null);
                    
                    if (datasource == null) {
                        log.warn("⚠️ Tenant {} does not have datasource configured - skipping", 
                                tenant.getName());
                        skippedCount++;
                        continue;
                    }
                    
                    // Enfileira o evento de migração
                    TenantMigrationEvent event = migrationQueue.enqueueEvent(
                            tenant.getId(),
                            tenant.getName(),
                            datasource,
                            TenantMigrationEvent.MigrationEventSource.APPLICATION_STARTUP
                    );
                    
                    enqueuedCount++;
                    log.debug("  ✅ Tenant enfileirado: {} (Event: {})", 
                            tenant.getName(), event.getEventId());
                    
                } catch (Exception e) {
                    log.error("❌ Erro ao enfileirar tenant {}: {}", tenant.getName(), e.getMessage(), e);
                    skippedCount++;
                }
            }
            
            log.info("");
            log.info("📊 Resumo de Enfileiramento:");
            log.info("   ✅ Enfileirados: {}", enqueuedCount);
            log.info("   ⚠️ Pulados: {}", skippedCount);
            log.info("   📈 Total: {}", tenants.size());
            
            if (enqueuedCount == 0) {
                log.warn("⚠️ No tenant was enqueued for migration");
                return;
            }
            
            // ────────────────────────────────────────────────────────────
            // PHASE 2: Iniciar o consumidor assíncrono
            // ────────────────────────────────────────────────────────────
            log.info("");
            log.info("🚀 Starting asynchronous queue consumer...");
            log.info("   Application will continue responding during processing");
            log.info("");
            
            // Dispara o consumidor de forma assíncrona
            queueConsumer.startConsumer();
            
            log.info("✅ Initialization completed successfully");
            log.info("   - {} tenants enqueued for migration", enqueuedCount);
            log.info("   - Consumidor iniciado em background");
            log.info("");
            
        } catch (Exception e) {
            log.error("");
            log.error("╔════════════════════════════════════════════════════════════════╗");
            log.error("║   !!!   ERROR INITIALIZING MIGRATION QUEUE   !!!       ║");
            log.error("╚════════════════════════════════════════════════════════════════╝");
            log.error("Erro: {}", e.getMessage(), e);
            
            // Não relança exceção para permitir que a aplicação continue
            // A fila pode ser reprocessada manualmente se necessário
            log.warn("⚠️ Application will continue running despite initialization error");
            log.warn("   Tip: Use admin endpoints to check status and reprocess");
        }
        
        log.info("");
    }
}
