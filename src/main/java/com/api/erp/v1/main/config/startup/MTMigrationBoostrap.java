package com.api.erp.v1.main.config.startup;

import com.api.erp.v1.main.migration.jobs.MigrationJobLauncher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * INFRASTRUCTURE - Executor de Job de Migrações na Inicialização
 * 
 * Responsável pelo disparo do Spring Batch Job de migrações.
 * 
 * Fluxo:
 * 1. Master DataSource é criado (FlywayConfig.java)
 * 2. Migrações Master são executadas (síncrono)
 * 3. ApplicationReadyEvent é disparado via ApplicationStartupListener
 * 4. Este componente dispara o Job de migração de tenants (assíncrono)
 * 5. Job processa tenants em chunks através do Spring Batch
 * 
 * @author ERP System
 * @version 1.0
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class MTMigrationBoostrap {
    
    private final MigrationJobLauncher migrationJobLauncher;

    /**
     * Executa o job de migrações
     * 
     * Chamado por ApplicationStartupListener quando a aplicação está pronta
     */
    public void execute() {
        log.info("");
        log.info("");
        log.info("");
        log.info("╔════════════════════════════════════════════════════════════════╗");
        log.info("║         APLICAÇÃO PRONTA - DISPARANDO JOB DE MIGRAÇÕES         ║");
        log.info("╚════════════════════════════════════════════════════════════════╝");
        log.info("");

        try {
            // Fase 1: Migrações Master já foram executadas sincrônicamente pelo FlywayConfig
            log.info("✅ Migrações Master (DataSource Principal) concluídas");
            log.info("");

            // Fase 2: Dispara o job de migrações de tenants de forma assíncrona
            log.info("📋 Disparando Spring Batch Job para migrações de tenants...");
            log.info("   - O job será executado em background");
            log.info("   - A aplicação continuará recebendo requisições");
            log.info("   - O processamento é em chunks (lotes) para melhor performance");
            log.info("");
            
            migrationJobLauncher.launchMigrationJob();

        } catch (Exception e) {
            log.error("");
            log.error("╔════════════════════════════════════════════════════════════════╗");
            log.error("║      !!!   ERRO AO DISPARAR JOB DE MIGRAÇÕES   !!!             ║");
            log.error("╚════════════════════════════════════════════════════════════════╝");
            log.error("Erro: {}", e.getMessage(), e);

            // Não relança exceção para permitir que a aplicação continue
            // O sistema pode funcionar parcialmente enquanto as migrações são resolvidas
            log.warn("⚠️ A aplicação continuará rodando apesar do erro ao disparar o job");
        }

        log.info("");
    }
}
