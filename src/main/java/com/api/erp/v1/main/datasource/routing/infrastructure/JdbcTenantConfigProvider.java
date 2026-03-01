package com.api.erp.v1.main.datasource.routing.infrastructure;

import com.api.erp.v1.main.datasource.routing.domain.ITenantConfigProvider;
import com.api.erp.v1.main.datasource.routing.domain.TenantDSConfig;
import com.api.erp.v1.main.shared.common.error.ErrorHandler;
import com.api.erp.v1.main.shared.common.error.TenantErrorMessage;
import com.api.erp.v1.main.tenant.domain.entity.DBType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.Optional;

/**
 * INFRASTRUCTURE - Implementação de ITenantConfigProvider usando JDBC
 * <p>
 * Recupera configurações de tenant do banco de dados Master.
 * Usa JdbcTemplate para abstração de queries.
 * <p>
 * Responsabilidade: Recuperar dados de tenant persistidos
 *
 * @author ERP System
 * @version 1.0
 */
@Repository
@Slf4j
public class JdbcTenantConfigProvider implements ITenantConfigProvider {

    private static final String QUERY_TENANT_CONFIG = """
            SELECT td.id, td.tenant_id, td.host, td.port, td.database_name, 
                   td.username, td.password, td.db_type
            FROM tb_tenant_datasource td
            WHERE td.tenant_id = ? AND td.is_active = 1
            LIMIT 1
            """;

    private static final String QUERY_TENANT_EXISTS = """
            SELECT 1
            FROM tb_tenant_datasource
            WHERE tenant_id = ? AND is_active = 1
            LIMIT 1
            """;

    private final JdbcTemplate jdbcTemplate;

    public JdbcTenantConfigProvider(@Qualifier("masterDataSource") DataSource masterDataSource) {
        this.jdbcTemplate = new JdbcTemplate(masterDataSource);
    }

    @Override
    public Optional<TenantDSConfig> getTenantConfig(Long tenantId) {
        if (tenantId == null || tenantId <= 0) {
            log.warn("Invalid tenant ID: {}", tenantId);
            throw new IllegalArgumentException("Tenant ID cannot be null or less than 0");
        }

        try {
            return Optional.of(buildTenantDatasourceUrl(tenantId));
        } catch (org.springframework.jdbc.BadSqlGrammarException e) {
            log.error(
                "Error retrieving SQL grammar for tenant {}. " +
                "Please verify that tb_tenant_datasource table exists and has all required columns. " +
                "SQL: {}",
                tenantId, e.getSql(), e
            );
            return Optional.empty();
        } catch (Exception e) {
            log.error("Error retrieving configuration for tenant: {}", tenantId, e);
            return Optional.empty();
        }
    }

    private TenantDSConfig buildTenantDatasourceUrl(Long tenantId) {
        log.debug("Fetching tenant {} configuration from tb_tenant_datasource table", tenantId);
        
        try {
            var results = jdbcTemplate.query(
                    QUERY_TENANT_CONFIG,
                    new Object[]{tenantId},
                    (rs, rowNum) -> {
                        String host = rs.getString("host");
                        int port = rs.getInt("port");
                        String database = rs.getString("database_name");
                        String username = rs.getString("username");
                        String password = rs.getString("password");
                        String dbType = rs.getString("db_type");

                        log.debug(
                            "Tenant {} encontrado: BD {} em {}:{} (tipo: {})",
                            tenantId, database, host, port, dbType
                        );

                        // Construir URL JDBC baseado no tipo de banco
                        String jdbcUrl = buildJdbcUrl(host, port, database, dbType);

                        return new TenantDSConfig(
                                tenantId,
                                jdbcUrl,
                                username,
                                password,
                                DBType.valueOf(dbType)
                        );
                    }
            );

            if (results.isEmpty()) {
                log.warn(
                    "Tenant configuration not found for tenant_id = {} or is not active in tb_tenant_datasource",
                    tenantId
                );
                throw new ErrorHandler(TenantErrorMessage.TENANT_NOT_FOUND);
            }

            log.info("Tenant configuration {} retrieved successfully", tenantId);
            return results.get(0);
        } catch (org.springframework.jdbc.BadSqlGrammarException e) {
            log.error(
                "SQL error when fetching tenant {}. " +
                "Verify if tb_tenant_datasource table exists. SQL: {}",
                tenantId, e.getSql(), e
            );
            throw e;
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            log.error("❌ Erro inesperado ao recuperar configuração de tenant {}: {}", tenantId, e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    private String buildJdbcUrl(String host, int port, String database, String dbType) {
        return switch (dbType.toLowerCase()) {
            case "mysql" -> "jdbc:mysql://" + host + ":" + port + "/" + database;
            case "postgresql" -> "jdbc:postgresql://" + host + ":" + port + "/" + database;
            case "oracle" -> "jdbc:oracle:thin:@" + host + ":" + port + ":" + database;
            case "sqlserver" -> "jdbc:sqlserver://" + host + ":" + port + ";databaseName=" + database;
            case "h2" -> "jdbc:h2:mem:" + database;
            case "mariadb" -> "jdbc:mariadb://" + host + ":" + port + "/" + database;
            default -> "jdbc:" + dbType + "://" + host + ":" + port + "/" + database;
        };
    }

    @Override
    public boolean tenantExists(String tenantId) {
        if (tenantId == null || tenantId.trim().isEmpty()) {
            log.warn("Tenant ID inválido para verificação: null ou vazio");
            return false;
        }

        try {
            Long id = Long.parseLong(tenantId.trim());
            var results = jdbcTemplate.query(
                    QUERY_TENANT_EXISTS,
                    new Object[]{id},
                    (rs, rowNum) -> 1
            );
            boolean exists = !results.isEmpty();
            log.debug("Verificação de tenant {}: {}", tenantId, exists ? "existe" : "não existe");
            return exists;
        } catch (NumberFormatException e) {
            log.warn("Tenant ID não é um número válido: {}", tenantId);
            return false;
        } catch (Exception e) {
            log.error("Erro ao verificar existência de tenant {}: {}", tenantId, e.getMessage());
            return false;
        }
    }
}
