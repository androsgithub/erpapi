package com.api.erp.v1.main.migration.startup;

import com.api.erp.v1.main.migration.domain.TenantMigrationEvent;
import com.api.erp.v1.main.migration.service.TenantMigrationQueueConsumer;
import com.api.erp.v1.main.migration.service.TenantMigrationQueue;
import com.api.erp.v1.main.tenant.domain.repository.TenantDatasourceRepository;
import com.api.erp.v1.main.tenant.domain.repository.TenantRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * INFRASTRUCTURE - Worker de Inicialização para Fila Unificada de Migrações
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
     * Executa a inicialização da fila de migrações
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
            // FASE 1: Enfileirar todos os tenants ativos
            // ────────────────────────────────────────────────────────────
            log.info("📋 Buscando tenants ativos para enfileiramento...");
            
            var tenants = tenantRepository.findAllByAtivaTrue();
            
            if (tenants.isEmpty()) {
                log.warn("⚠️ Nenhum tenant ativo encontrado para migração");
                log.info("✅ Inicialização concluída (sem tenants)");
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
                    var datasource = tenantDatasourceRepository
                            .findByTenantIdAndStatus(tenant.getId(), true);
                    
                    if (datasource == null) {
                        log.warn("⚠️ Tenant {} não possui datasource configurado - pulando", 
                                tenant.getNome());
                        skippedCount++;
                        continue;
                    }
                    
                    // Enfileira o evento de migração
                    TenantMigrationEvent event = migrationQueue.enqueueEvent(
                            tenant.getId(),
                            tenant.getNome(),
                            datasource,
                            TenantMigrationEvent.MigrationEventSource.APPLICATION_STARTUP
                    );
                    
                    enqueuedCount++;
                    log.debug("  ✅ Tenant enfileirado: {} (Event: {})", 
                            tenant.getNome(), event.getEventId());
                    
                } catch (Exception e) {
                    log.error("❌ Erro ao enfileirar tenant {}: {}", tenant.getNome(), e.getMessage(), e);
                    skippedCount++;
                }
            }
            
            log.info("");
            log.info("📊 Resumo de Enfileiramento:");
            log.info("   ✅ Enfileirados: {}", enqueuedCount);
            log.info("   ⚠️ Pulados: {}", skippedCount);
            log.info("   📈 Total: {}", tenants.size());
            
            if (enqueuedCount == 0) {
                log.warn("⚠️ Nenhum tenant foi enfileirado para migração");
                return;
            }
            
            // ────────────────────────────────────────────────────────────
            // FASE 2: Iniciar o consumidor assíncrono
            // ────────────────────────────────────────────────────────────
            log.info("");
            log.info("🚀 Iniciando consumidor assíncrono de fila...");
            log.info("   A aplicação continuará respondendo durante o processamento");
            log.info("");
            
            // Dispara o consumidor de forma assíncrona
            queueConsumer.startConsumer();
            
            log.info("✅ Inicialização concluída com sucesso");
            log.info("   - {} tenants enfileirados para migração", enqueuedCount);
            log.info("   - Consumidor iniciado em background");
            log.info("");
            
        } catch (Exception e) {
            log.error("");
            log.error("╔════════════════════════════════════════════════════════════════╗");
            log.error("║   !!!   ERRO NA INICIALIZAÇÃO DA FILA DE MIGRAÇÕES   !!!       ║");
            log.error("╚════════════════════════════════════════════════════════════════╝");
            log.error("Erro: {}", e.getMessage(), e);
            
            // Não relança exceção para permitir que a aplicação continue
            // A fila pode ser reprocessada manualmente se necessário
            log.warn("⚠️ A aplicação continuará rodando apesar do erro na inicialização");
            log.warn("   Dica: Use endpoints de administração para verificar status e reprocessar");
        }
        
        log.info("");
    }
}
