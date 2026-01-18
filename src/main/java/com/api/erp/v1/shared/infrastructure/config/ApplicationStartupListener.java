package com.api.erp.v1.shared.infrastructure.config;

import com.api.erp.v1.tenant.infrastructure.config.TenantMigrationService;
import lombok.extern.slf4j.Slf4j;
import org.flywaydb.core.Flyway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.sql.SQLException;

/**
 * Listener que executa migrações de tenant quando a aplicação está pronta.
 * 
 * Este component:
 * 1. Aguarda a aplicação Spring Boot estar completamente inicializada
 * 2. Executa migrações master (se não feitas ainda via FlywayConfig)
 * 3. Executa migrações de TODOS os tenants ativos
 * 
 * Fluxo de inicialização:
 * 1. FlywayConfig.flywayMaster() - Executa migrations do master
 * 2. ApplicationStartupListener (this) - Executa migrations dos tenants
 * 
 * Ordem é importante:
 * - Master DEVE ser migrado primeiro (contém tabela tenant_datasource)
 * - Depois todos os tenants são migrados
 */
@Component
@Slf4j
public class ApplicationStartupListener {
    private final TenantMigrationService tenantMigrationService;

    public ApplicationStartupListener(TenantMigrationService tenantMigrationService) {
        this.tenantMigrationService = tenantMigrationService;
    }

    /**
     * Executado automaticamente quando ApplicationReadyEvent é publicado.
     * Este evento é disparado quando a aplicação Spring Boot está totalmente inicializada.
     * 
     * Neste ponto:
     * - DataSources estão configurados
     * - Banco master foi migrado
     * - Repositórios estão disponíveis
     * - JPA está pronto
     */
    @EventListener(ApplicationReadyEvent.class)
    public void runMigrationsOnStartup() {
        log.info("");
        log.info("╔════════════════════════════════════════════════════════════════╗");
        log.info("║      INICIANDO MIGRAÇÕES DE TENANTS NA STARTUP DA APLICAÇÃO      ║");
        log.info("╚════════════════════════════════════════════════════════════════╝");
        log.info("");

        try {
            // Executa migrações de todos os tenants
            var report = tenantMigrationService.migrateAllTenants();
            
            log.info("");
            log.info("╔════════════════════════════════════════════════════════════════╗");
            log.info("║                   MIGRAÇÕES CONCLUÍDAS COM SUCESSO              ║");
            log.info("╚════════════════════════════════════════════════════════════════╝");
            
            if (report.hasFailures()) {
                log.warn("⚠️ Algumas migrações falharam, verifique os logs acima");
            } else {
                log.info("✅ Todos os tenants foram migrados com sucesso!");
            }
            
        } catch (Exception e) {
            log.error("");
            log.error("╔════════════════════════════════════════════════════════════════╗");
            log.error("║           ❌ ERRO CRÍTICO AO EXECUTAR MIGRAÇÕES                ║");
            log.error("╚════════════════════════════════════════════════════════════════╝");
            log.error("Erro: {}", e.getMessage(), e);
            
            // Não relança exceção para permitir que a aplicação continue
            // Se isso é crítico para seu caso de uso, mude este comportamento
            log.warn("⚠️ A aplicação continuará rodando apesar do erro de migração");
        }
        
        log.info("");
    }
}
