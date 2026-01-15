package com.api.erp.v1.shared.infrastructure.migration;

import com.api.erp.v1.shared.infrastructure.migration.FlywayMigrationStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.sql.SQLException;

@Service
public class TenantSchemaMigrationService {

    private static final Logger logger = LoggerFactory.getLogger(TenantSchemaMigrationService.class);
    private final FlywayMigrationStrategy flywayMigrationStrategy;

    public TenantSchemaMigrationService(FlywayMigrationStrategy flywayMigrationStrategy) {
        this.flywayMigrationStrategy = flywayMigrationStrategy;
    }

    public boolean provisioning(String tenantId, String schemaName) {
        logger.info("Iniciando provisionamento do tenant: {} com schema: {}", tenantId, schemaName);

        try {
            flywayMigrationStrategy.migrateSchema(schemaName);
            logger.info("Provisionamento concluído com sucesso para tenant: {}", tenantId);
            return true;

        } catch (SQLException e) {
            logger.error("Erro durante o provisionamento do tenant: {}", tenantId, e);
            return false;
        }
    }

    public String getStatus(String tenantId, String schemaName) {
        logger.debug("Verificando status de migração para tenant: {} (schema: {})", tenantId, schemaName);
        return flywayMigrationStrategy.getMigrationStatus(schemaName);
    }

    public boolean cleanTenant(String tenantId, String schemaName) {
        logger.warn("Limpando histórico de migrações para tenant: {} (schema: {}) - OPERAÇÃO DESTRUTIVA", tenantId, schemaName);

        try {
            flywayMigrationStrategy.cleanSchema(schemaName);
            logger.info("Limpeza concluída para tenant: {}", tenantId);
            return true;

        } catch (SQLException e) {
            logger.error("Erro ao limpar tenant: {}", tenantId, e);
            return false;
        }
    }
}
