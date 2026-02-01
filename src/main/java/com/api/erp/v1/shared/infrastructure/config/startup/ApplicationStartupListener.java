package com.api.erp.v1.shared.infrastructure.config.startup;

import com.api.erp.v1.tenant.domain.repository.TenantDatasourceRepository;
import com.api.erp.v1.tenant.infrastructure.config.TenantMigrationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ApplicationStartupListener {
    private final TenantMigrationService tenantMigrationService;
    private final TenantDatasourceRepository tenantDatasourceRepository;

    public ApplicationStartupListener(TenantMigrationService tenantMigrationService, TenantDatasourceRepository tenantDatasourceRepository) {
        this.tenantMigrationService = tenantMigrationService;
        this.tenantDatasourceRepository = tenantDatasourceRepository;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void runMigrationsOnStartup() {
        log.info("");
        log.info("");
        log.info("");
        log.info("╔════════════════════════════════════════════════════════════════╗");
        log.info("║    INICIANDO MIGRAÇÕES DE TENANTS NA STARTUP DA APLICAÇÃO      ║");
        log.info("╚════════════════════════════════════════════════════════════════╝");
        log.info("");
        log.info("");
        log.info("");

        try {
            // Executa migrações de todos os tenants
            var report = tenantMigrationService.migrateAllTenants();
            
            log.info("");
            log.info("╔════════════════════════════════════════════════════════════════╗");
            log.info("║                   MIGRAÇÕES CONCLUÍDAS COM SUCESSO             ║");
            log.info("╚════════════════════════════════════════════════════════════════╝");
            
            if (report.hasFailures()) {
                log.warn("⚠️ Algumas migrações falharam, verifique os logs acima");
            } else {
                log.info("✅ Todos os tenants foram migrados com sucesso!");
            }
            
        } catch (Exception e) {
            log.error("");
            log.error("╔════════════════════════════════════════════════════════════════╗");
            log.error("║      !!!   ERRO CRÍTICO AO EXECUTAR MIGRAÇÕES   !!!            ║");
            log.error("╚════════════════════════════════════════════════════════════════╝");
            log.error("Erro: {}", e.getMessage(), e);
            
            // Não relança exceção para permitir que a aplicação continue
            // Se isso é crítico para seu caso de uso, mude este comportamento
            log.warn("⚠️ A aplicação continuará rodando apesar do erro de migração");
        }
        
        log.info("");
    }
}
