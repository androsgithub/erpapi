package com.api.erp.v1.main.migration.jobs;

import com.api.erp.v1.main.config.startup.seed.MainSeed;
import com.api.erp.v1.main.datasource.routing.TenantContext;
import com.api.erp.v1.main.tenant.domain.entity.TenantDatasource;
import com.api.erp.v1.main.tenant.infrastructure.config.TenantMigrationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.infrastructure.item.ItemProcessor;
import org.springframework.stereotype.Component;

/**
 * PROCESSOR - Processamento de Migração de Tenant
 * 
 * Responsável por:
 * 1. Executar migrações Flyway (via TenantMigrationService)
 * 2. Executar seeders (via MainSeed) - OPCIONAL
 * 3. Registrar erros de forma estruturada
 * 
 * Características:
 * - Thread-safe: utiliza TenantContext para isolamento
 * - Tratamento de erro: captura exceções e retorna MigrationResult
 * - Logging detalhado: rastreia cada etapa da migração
 * 
 * @author ERP System
 * @version 1.0
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class TenantMigrationProcessor implements ItemProcessor<TenantDatasource, MigrationResult> {

    private final TenantMigrationService tenantMigrationService;
    private final MainSeed mainSeed;

    /**
     * Processa a migração de um datasource de tenant
     * 
     * Fluxo:
     * 1. Executa Flyway migrations para o tenant
     * 2. Executa MainSeed (seeders) para o tenant
     * 3. Retorna resultado de sucesso ou falha
     */
    @Override
    public MigrationResult process(TenantDatasource datasource) {
        Long tenantId = datasource.getTenant().getId();
        String tenantName = datasource.getTenant().getNome();

        log.info("");
        log.info("▶ [{}] Iniciando migração do tenant: {}", tenantId, tenantName);

        try {
            // ════════════════════════════════════════════════════░
            // FASE 1: EXECUTAR FLYWAY MIGRATIONS
            // ════════════════════════════════════════════════════
            log.info("  📋 [{}] Executando migrações Flyway...", tenantId);

            try {
                tenantMigrationService.migrateTenantById(tenantId);
                log.info("  ✅ [{}] Migrações Flyway concluídas com sucesso", tenantId);
            } catch (Exception e) {
                log.error("  ❌ [{}] Erro durante migrações Flyway: {}", tenantId, e.getMessage(), e);
                return MigrationResult.failure(tenantId, "Flyway migration failed: " + e.getMessage());
            }

            // ════════════════════════════════════════════════════░
            // FASE 2: EXECUTAR SEEDERS (DADOS INICIAIS)
            // ════════════════════════════════════════════════════
            log.info("  🌱 [{}] Executando seeders (dados iniciais)...", tenantId);

            try {
                // Define contexto do tenant para o MainSeed usar o datasource correto
                TenantContext.setTenantId(tenantId);

                // Executa todos os seeders configurados
                mainSeed.executar();

                log.info("  ✅ [{}] Seeders executados com sucesso", tenantId);

            } catch (Exception e) {
                log.error("  ⚠️ [{}] Erro ao executar seeders: {}", tenantId, e.getMessage());
                // Nota: retorna sucesso mesmo com erro no seed
                // Se você preferir falhar, descomente a linha abaixo
                // return MigrationResult.failure(tenantId, "Seed execution failed: " + e.getMessage());

                return MigrationResult.failure(tenantId, "Seed execution failed: " + e.getMessage());

            } finally {
                // Limpa contexto do tenant
                TenantContext.clear();
            }

            // ════════════════════════════════════════════════════░
            // SUCESSO TOTAL
            // ════════════════════════════════════════════════════
            log.info("✅ [{}] Migração COMPLETA: {} (Flyway + Seed)", tenantId, tenantName);
            return MigrationResult.success(tenantId);

        } catch (Exception e) {
            log.error("❌ [{}] Erro crítico na migração do tenant {}: {}",
                    tenantId, tenantName, e.getMessage(), e);
            return MigrationResult.failure(tenantId, "Critical error: " + e.getMessage());
        }
    }
}
