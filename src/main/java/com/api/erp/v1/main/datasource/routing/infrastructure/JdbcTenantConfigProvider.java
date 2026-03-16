package com.api.erp.v1.main.datasource.routing.infrastructure;

import com.api.erp.v1.main.datasource.routing.domain.ITenantConfigProvider;
import com.api.erp.v1.main.datasource.routing.domain.TenantDSConfig;
import com.api.erp.v1.main.shared.common.error.ErrorHandler;
import com.api.erp.v1.main.shared.common.error.TenantErrorMessage;
import com.api.erp.v1.main.master.tenant.domain.entity.DBType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.Base64;
import java.util.Optional;

/**
 * INFRASTRUCTURE - Implementação de ITenantConfigProvider usando JDBC
 * <p>
 * Retrieves configurações de tenant do banco de dados Master usando tb_tnt_datasource.
 * Usa JdbcTemplate para abstração de queries.
 * <p>
 * Responsibility: Recuperar dados de configuração de tenant persistidos
 * - Busca na tabela tb_tnt_datasource por tenant_id
 * - Desencripta senha (Base64 placeholder, usar AES em produção)
 * - Converte dbType/driverClass para DBType enum
 * - Retorna TenantDSConfig pronto para uso
 *
 * @author ERP System
 * @version 2.0
 */
@Repository
@Slf4j
public class JdbcTenantConfigProvider implements ITenantConfigProvider {

    private static final String QUERY_TENANT_CONFIG = """
            SELECT td.id, td.tenant_id, td.url, td.driver_class, td.schema_name,
                   td.username, td.password_encrypted, td.db_type, 
                   td.pool_min, td.pool_max, td.connection_timeout, td.idle_timeout
            FROM tb_tnt_datasource td
            WHERE td.tenant_id = ? AND td.active = true
            LIMIT 1
            """;

    private static final String QUERY_TENANT_EXISTS = """
            SELECT 1
            FROM tb_tnt_datasource
            WHERE tenant_id = ? AND active = true
            LIMIT 1
            """;

    private final JdbcTemplate jdbcTemplate;

    public JdbcTenantConfigProvider(@Qualifier("masterDataSource") DataSource masterDataSource) {
        this.jdbcTemplate = new JdbcTemplate(masterDataSource);
    }

    @Override
    public Optional<TenantDSConfig> getTenantConfig(Long tenantId) {
        if (tenantId == null || tenantId <= 0) {
            log.warn("[JDBC CONFIG PROVIDER] Invalid tenant ID: {}", tenantId);
            throw new IllegalArgumentException("Tenant ID cannot be null or less than 0");
        }

        try {
            return buildTenantDatasourceConfig(tenantId);
        } catch (org.springframework.jdbc.BadSqlGrammarException e) {
            log.error(
                "[JDBC CONFIG PROVIDER] SQL grammar error for tenant {}. " +
                "Verify tb_tnt_datasource table exists with columns: " +
                "url, driver_class, schema_name, username, password_encrypted, db_type, " +
                "pool_min, pool_max, connection_timeout, idle_timeout. SQL: {}",
                tenantId, e.getSql(), e
            );
            return Optional.empty();
        } catch (Exception e) {
            log.error("[JDBC CONFIG PROVIDER] Error retrieving configuration for tenant: {}", tenantId, e);
            return Optional.empty();
        }
    }

    private Optional<TenantDSConfig> buildTenantDatasourceConfig(Long tenantId) {
        log.debug("[JDBC CONFIG PROVIDER] Fetching tenant {} configuration", tenantId);

        try {
            var results = jdbcTemplate.query(
                    QUERY_TENANT_CONFIG,
                    new Object[]{tenantId},
                    (rs, rowNum) -> {
                        String url = rs.getString("url");
                        String driverClass = rs.getString("driver_class");
                        String schemaName = rs.getString("schema_name");
                        String username = rs.getString("username");
                        String passwordEncrypted = rs.getString("password_encrypted");
                        String dbTypeStr = rs.getString("db_type");
                        int poolMin = rs.getInt("pool_min");
                        int poolMax = rs.getInt("pool_max");
                        int connectionTimeout = rs.getInt("connection_timeout");
                        int idleTimeout = rs.getInt("idle_timeout");

                        // Desencriptar senha (usando Base64 placeholder, trocar por AES em prod)
                        String password = decryptPassword(passwordEncrypted);

                        log.debug(
                            "[JDBC CONFIG PROVIDER] Tenant {} found: URL={}, Schema={}, Driver={}, DBType={}",
                            tenantId, url, schemaName, driverClass, dbTypeStr
                        );

                        try {
                            DBType dbType = DBType.fromNome(dbTypeStr);
                            return new TenantDSConfig(tenantId, url, username, password, dbType);
                        } catch (IllegalArgumentException e) {
                            log.error("[JDBC CONFIG PROVIDER] Invalid DBType for tenant {}: {}", tenantId, dbTypeStr, e);
                            throw e;
                        }
                    }
            );

            if (results.isEmpty()) {
                log.warn(
                    "[JDBC CONFIG PROVIDER] Tenant configuration not found for tenant_id={} " +
                    "or is not active in tb_tnt_datasource",
                    tenantId
                );
                throw new ErrorHandler(TenantErrorMessage.TENANT_NOT_FOUND);
            }

            log.info("[JDBC CONFIG PROVIDER] Tenant configuration {} retrieved successfully", tenantId);
            return Optional.of(results.get(0));

        } catch (org.springframework.jdbc.BadSqlGrammarException e) {
            log.error(
                "[JDBC CONFIG PROVIDER] SQL error when fetching tenant {}. " +
                "Verify tb_tnt_datasource table structure. SQL: {}",
                tenantId, e.getSql(), e
            );
            throw e;
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            log.error("[JDBC CONFIG PROVIDER] Unexpected error retrieving tenant configuration {}: {}", 
                    tenantId, e.getMessage(), e);
            throw new IllegalStateException("Failed to retrieve tenant configuration", e);
        }
    }

    /**
     * Desencripta senha armazenada
     * 
     * SECURITY NOTE: Current implementation uses Base64 (development only).
     * PRODUCTION: Replace with AES-256 encryption using SecureRandom IV.
     * 
     * Implementation example for production:
     * <pre>
     *   Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
     *   cipher.init(Cipher.DECRYPT_MODE, secretKey, gcmParameterSpec);
     *   return new String(cipher.doFinal(encryptedBytes));
     * </pre>
     */
    private String decryptPassword(String encryptedPassword) {
        if (encryptedPassword == null || encryptedPassword.trim().isEmpty()) {
            return "";
        }

        try {
            // Placeholder: Base64 decode (trocar por AES em produção)
            byte[] decodedBytes = Base64.getDecoder().decode(encryptedPassword);
            return new String(decodedBytes);
        } catch (IllegalArgumentException e) {
            log.warn("[JDBC CONFIG PROVIDER] Failed to decrypt password (invalid Base64): {}", e.getMessage());
            return encryptedPassword; // Retorna como está se falhar
        }
    }

    @Override
    public boolean tenantExists(String tenantId) {
        if (tenantId == null || tenantId.trim().isEmpty()) {
            log.warn("[JDBC CONFIG PROVIDER] Invalid Tenant ID for verification: null or empty");
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
            log.debug("[JDBC CONFIG PROVIDER] Tenant verification {}: {}",
                    tenantId, exists ? "exists" : "does not exist");
            return exists;
        } catch (NumberFormatException e) {
            log.warn("[JDBC CONFIG PROVIDER] Tenant ID is not a valid number: {}", tenantId);
            return false;
        } catch (Exception e) {
            log.error("[JDBC CONFIG PROVIDER] Error verifying tenant existence {}: {}", 
                    tenantId, e.getMessage());
            return false;
        }
    }
}
