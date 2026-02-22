package com.api.erp.v1.main.config.startup;

import com.api.erp.v1.main.migration.async.service.MigrationQueueService;
import com.api.erp.v1.main.tenant.domain.repository.TenantDatasourceRepository;
import com.api.erp.v1.main.tenant.infrastructure.config.TenantMigrationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * INFRASTRUCTURE - Listener de Startup da Aplicação
 * 
 * Responsável pela inicialização da fila de migrações.
 * 
 * Fluxo:
 * 1. Master DataSource é criado (FlywayConfig.java)
 * 2. Migrações Master são executadas (síncrono)
 * 3. ApplicationReadyEvent é disparado
 * 4. Este listener enfileira migrações de tenants (assíncrono)
 * 5. Fila processa tenants em background
 * 
 * @author ERP System
 * @version 1.0
 */
@Component
@Slf4j
public class ApplicationStartupListener {
    private final TenantMigrationService tenantMigrationService;
    private final TenantDatasourceRepository tenantDatasourceRepository;
    private final MigrationQueueService migrationQueueService;

    public ApplicationStartupListener(TenantMigrationService tenantMigrationService, 
                                      TenantDatasourceRepository tenantDatasourceRepository,
                                      MigrationQueueService migrationQueueService) {
        this.tenantMigrationService = tenantMigrationService;
        this.tenantDatasourceRepository = tenantDatasourceRepository;
        this.migrationQueueService = migrationQueueService;
    }

    /**
     * Executa na inicialização da aplicação (após Spring Boot estar pronto)
     * 
     * Estratégia de duas fases:
     * - Fase 1: Migrações Master (síncrono) - já executadas pelo FlywayConfig
     * - Fase 2: Migrações de Tenants (assíncrono) - enfileiradas neste método
     */
    @EventListener(ApplicationReadyEvent.class)
    public void runMigrationsOnStartup() {
        log.info("");
        log.info("");
        log.info("");
        log.info("╔════════════════════════════════════════════════════════════════╗");
        log.info("║         APLICAÇÃO PRONTA - INICIANDO FILA DE MIGRAÇÕES        ║");
        log.info("╚════════════════════════════════════════════════════════════════╝");
        log.info("");

        try {
            // Fase 1: Migrações Master já foram executadas sincrônicamente pelo FlywayConfig
            log.info("✅ Migrações Master concluídas");
            log.info("");
            
            // Fase 2: Enfileira migrações de tenants para execução assíncrona
            log.info("📋 Iniciando enfileiramento de migrações de tenants...");
            migrationQueueService.enqueueAllTenantMigrations();
            
            log.info("");
            log.info("╔════════════════════════════════════════════════════════════════╗");
            log.info("║          FILA DE MIGRAÇÕES INICIADA COM SUCESSO               ║");
            log.info("║     As migrações dos tenants serão executadas em background    ║");
            log.info("╚════════════════════════════════════════════════════════════════╝");
            log.info("");
            
            // Inicia o processamento assíncrono da fila
            migrationQueueService.processMigrationQueue();
            
        } catch (Exception e) {
            log.error("");
            log.error("╔════════════════════════════════════════════════════════════════╗");
            log.error("║      !!!   ERRO AO INICIALIZAR FILA DE MIGRAÇÕES   !!!         ║");
            log.error("╚════════════════════════════════════════════════════════════════╝");
            log.error("Erro: {}", e.getMessage(), e);
            
            // Não relança exceção para permitir que a aplicação continue
            // O sistema pode funcionar parcialmente enquanto as migrações são resolvidas
            log.warn("⚠️ A aplicação continuará rodando apesar do erro ao inicializar a fila");
        }
        
        log.info("");
    }
}
