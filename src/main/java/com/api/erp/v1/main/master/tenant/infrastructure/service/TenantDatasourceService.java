package com.api.erp.v1.main.master.tenant.infrastructure.service;

import com.api.erp.v1.main.datasource.routing.core.DataSourceRouter;
import com.api.erp.v1.main.datasource.routing.domain.TenantDSConfig;
import com.api.erp.v1.main.datasource.routing.infrastructure.HikariDataSourceFactory;
import com.api.erp.v1.main.master.tenant.application.dto.request.update.UpdateTenantDatasourceWithPasswordRequest;
import com.api.erp.v1.main.shared.common.error.ErrorHandler;
import com.api.erp.v1.main.shared.common.error.TenantErrorMessage;
import com.api.erp.v1.main.master.tenant.application.dto.request.create.TenantDatasourceRequest;
import com.api.erp.v1.main.master.tenant.application.dto.response.TenantDatasourceResponse;
import com.api.erp.v1.main.master.tenant.domain.entity.DBType;
import com.api.erp.v1.main.master.tenant.domain.entity.Tenant;
import com.api.erp.v1.main.master.tenant.domain.entity.TenantDatasource;
import com.api.erp.v1.main.master.tenant.domain.repository.TenantDatasourceRepository;
import com.api.erp.v1.main.master.tenant.domain.repository.TenantRepository;
import com.api.erp.v1.main.master.tenant.domain.service.ITenantDatasourceService;
import com.zaxxer.hikari.HikariDataSource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Optional;

/**
 * TenantDatasourceService
 * <p>
 * Service implementation for managing tenant datasource configurations.
 * <p>
 * Responsibilities:
 * - Configure novo datasource para um tenant
 * - Update datasource configuration
 * - Test database connection
 * - Registrar datasource no DataSourceRouter
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TenantDatasourceService implements ITenantDatasourceService {

    private final TenantRepository tenantRepository;
    private final TenantDatasourceRepository tenantDatasourceRepository;
    private final HikariDataSourceFactory hikariDataSourceFactory;
    private final DataSourceRouter dataSourceRouter;

    /**
     * Configures new datasource for a tenant
     */
    @Override
    @Transactional(transactionManager = "masterTransactionManager")
    public TenantDatasourceResponse configurarDatasource(Long tenantId, TenantDatasourceRequest request) {
        log.info("Configuring datasource for tenant: {}", tenantId);

        // 1. Get tenant
        Tenant tenant = tenantRepository.findById(tenantId)
                .orElseThrow(() -> new ErrorHandler(TenantErrorMessage.TENANT_NOT_FOUND));

        // 2. Validate if datasource is already configured
        if (tenantDatasourceRepository.existsByTenantIdAndActiveTrue(tenant.getId())) {
            throw new ErrorHandler(TenantErrorMessage.DATASOURCE_ALREADY_EXISTS);
        }

        // 3. Test connection before saving
        if (!testarConexao(tenantId, request)) {
            log.error("Failed to test database connection for tenant: {}", tenantId);
            throw new ErrorHandler(TenantErrorMessage.DATASOURCE_CONNECTION_FAILED);
        }

        // 4. Create and save TenantDatasource
        TenantDatasource tenantDatasource = TenantDatasource.builder()
                .tenant(tenant)
                .driverClass(getDriverClass(request.dbType()))
                .url(buildJdbcUrl(request))
                .username(request.username())
                .dbType(DBType.fromNome(request.dbType()))
                .schemaName(request.databaseName())
                .poolMin(5)
                .poolMax(20)
                .connectionTimeout(30000)
                .idleTimeout(600000)
                .active(true)
                .testStatus(TenantDatasource.TestStatus.SUCCESS)
                .testedAt(LocalDateTime.now())
                .build();

        // Encriptografar senha (será feito via setPassword())
        tenantDatasource.setPassword(request.password());

        tenantDatasource = tenantDatasourceRepository.save(tenantDatasource);
        log.info("Datasource successfully configured for tenant: {} | ID: {}", tenantId, tenant.getId());

        // 5. Register datasource in DataSourceRouter
        TenantDSConfig config = new TenantDSConfig(
                tenantDatasource.getTenant().getId(),
                tenantDatasource.getJdbcUrl(),
                tenantDatasource.getUsername(),
                tenantDatasource.getPassword(),
                tenantDatasource.getDbType()
        );
        DataSource dataSource = hikariDataSourceFactory.createDataSource(config);
        dataSourceRouter.registerDataSource(tenantId, dataSource);

        return toResponse(tenantDatasource);
    }

    /**
     * Gets datasource configuration for a tenant
     */
    @Override
    @Transactional(transactionManager = "masterTransactionManager", readOnly = true)
    public Optional<TenantDatasourceResponse> obterDatasource(Long tenantId) {
        log.debug("Fetching datasource for tenant: {}", tenantId);

        return tenantRepository.findById(tenantId)
                .flatMap(tenant -> tenantDatasourceRepository.findByTenantIdAndActiveTrue(tenant.getId()))
                .map(this::toResponse);
    }

    /**
     * Updates datasource configuration for a tenant
     */
    @Override
    @Transactional(transactionManager = "masterTransactionManager")
    public TenantDatasourceResponse atualizarDatasource(Long tenantId, TenantDatasourceRequest request) {
        log.info("Updating datasource for tenant: {}", tenantId);

        // 1. Get tenant
        Tenant tenant = tenantRepository.findById(tenantId)
                .orElseThrow(() -> new ErrorHandler(TenantErrorMessage.TENANT_NOT_FOUND));

        // 2. Get active datasource
        TenantDatasource tenantDatasource = tenantDatasourceRepository
                .findByTenantIdAndActiveTrue(tenant.getId())
                .orElseThrow(() -> new ErrorHandler(TenantErrorMessage.DATASOURCE_NOT_CONFIGURED));

        // 3. Test new connection
        if (!testarConexao(tenantId, request)) {
            log.error("Failed to test new datasource configuration for tenant: {}", tenantId);
            throw new ErrorHandler(TenantErrorMessage.DATASOURCE_CONNECTION_FAILED);
        }

        // 4. Update entity
        tenantDatasource.setDriverClass(getDriverClass(request.dbType()));
        tenantDatasource.setUrl(buildJdbcUrl(request));
        tenantDatasource.setUsername(request.username());
        tenantDatasource.setPassword(request.password());  // Encriptografa automaticamente
        tenantDatasource.setDbType(DBType.fromNome(request.dbType()));
        tenantDatasource.setSchemaName(request.databaseName());
        tenantDatasource.setTestStatus(TenantDatasource.TestStatus.SUCCESS);
        tenantDatasource.setTestedAt(LocalDateTime.now());

        tenantDatasource = tenantDatasourceRepository.save(tenantDatasource);
        log.info("Datasource successfully updated for tenant: {}", tenantId);

        // 5. Update datasource in DataSourceRouter
        TenantDSConfig config = new TenantDSConfig(
                tenantDatasource.getTenant().getId(),
                tenantDatasource.getJdbcUrl(),
                tenantDatasource.getUsername(),
                tenantDatasource.getPassword(),
                tenantDatasource.getDbType()
        );
        DataSource dataSource = hikariDataSourceFactory.createDataSource(config);
        dataSourceRouter.registerDataSource(tenantId, dataSource);

        return toResponse(tenantDatasource);
    }

    /**
     * Tests database connection with tenantId (internal use)
     */
    private boolean testarConexao(Long tenantId, TenantDatasourceRequest request) {
        log.debug("Testing database connection: {}:{}/{}", request.host(), request.port(), request.databaseName());

        try {
            // Create temporary datasource for testing
            String jdbcUrl = buildJdbcUrl(request);
            String driverClass = getDriverClass(request.dbType());

            // Use tenantId directly instead of trying to get it from temporary object
            DataSource testDataSource = hikariDataSourceFactory.createDataSource(
                    new TenantDSConfig(
                            tenantId,
                            jdbcUrl,
                            request.username(),
                            request.password(),
                            DBType.fromNome(request.dbType())
                    )
            );

            // Try to get connection
            try (Connection connection = testDataSource.getConnection()) {
                log.debug("Database connection tested successfully");
                // Fechar datasource se for HikariCP
                if (testDataSource instanceof HikariDataSource) {
                    ((HikariDataSource) testDataSource).close();
                }
                return true;
            }
        } catch (SQLException e) {
            log.error("Error testing database connection", e);
            return false;
        }
    }

    /**
     * Tests database connection without tenant context (for frontend testing)
     * Uses a dummy tenantId for the DataSourceRouter
     */
    @Override
    public boolean testarConexao(TenantDatasourceRequest request) {
        log.debug("Testing database connection (frontend): {}:{}/{}", request.host(), request.port(), request.databaseName());
        return testarConexao(-1L, request);
    }

    /**
     * Builds JDBC URL from request components
     */
    private String buildJdbcUrl(TenantDatasourceRequest request) {
        return buildJdbcUrl(request.host(), request.port(), request.databaseName(), request.dbType());
    }

    /**
     * Builds JDBC URL from individual components
     */
    private String buildJdbcUrl(String host, Integer port, String database, String dbType) {
        String type = dbType.toUpperCase();

        return switch (type) {
            case "POSTGRESQL" -> String.format("jdbc:postgresql://%s:%d/%s", host, port, database);
            case "MYSQL", "MARIADB" -> String.format("jdbc:mysql://%s:%d/%s", host, port, database);
            case "ORACLE" -> String.format("jdbc:oracle:thin:@%s:%d:%s", host, port, database);
            case "SQL_SERVER" -> String.format("jdbc:sqlserver://%s:%d;databaseName=%s", host, port, database);
            case "H2" -> String.format("jdbc:h2:mem:%s", database);
            default -> String.format("jdbc:%s://%s:%d/%s", type.toLowerCase(), host, port, database);
        };
    }

    /**
     * Gets JDBC driver class from dbType
     */
    private String getDriverClass(String dbType) {
        return switch (dbType.toUpperCase()) {
            case "POSTGRESQL" -> "org.postgresql.Driver";
            case "MYSQL" -> "com.mysql.cj.jdbc.Driver";
            case "MARIADB" -> "org.mariadb.jdbc.Driver";
            case "ORACLE" -> "oracle.jdbc.driver.OracleDriver";
            case "SQL_SERVER" -> "com.microsoft.sqlserver.jdbc.SQLServerDriver";
            case "H2" -> "org.h2.Driver";
            default -> "org.postgresql.Driver"; // Default to PostgreSQL
        };
    }

    /**
     * Converte TenantDatasource para TenantDatasourceResponse
     */
    private TenantDatasourceResponse toResponse(TenantDatasource tenantDatasource) {
        return new TenantDatasourceResponse(
                extractHost(tenantDatasource.getUrl()),
                extractDatabase(tenantDatasource.getUrl()),
                tenantDatasource.getUsername(),
                tenantDatasource.getDbType().name(),
                tenantDatasource.getCreatedAt(),
                tenantDatasource.getUpdatedAt()
        );
    }

    /**
     * Extract database name from JDBC URL
     */
    private String extractDatabase(String url) {
        if (url == null || url.isEmpty()) return null;
        try {
            // Parse JDBC URL format: jdbc:postgresql://host:port/database
            String[] parts = url.split("/");
            if (parts.length > 0) {
                return parts[parts.length - 1];
            }
        } catch (Exception e) {
            log.warn("Failed to extract database from URL: {}", url);
        }
        return null;
    }

    /**
     * Extract host from connection URL
     */
    private String extractHost(String url) {
        if (url == null || url.isEmpty()) return null;
        try {
            // Parse JDBC URL format: jdbc:postgresql://host:port/database
            String[] parts = url.split("://");
            if (parts.length > 1) {
                String hostPart = parts[1].split("/")[0];
                return hostPart.split(":")[0];
            }
        } catch (Exception e) {
            log.warn("Failed to extract host from URL: {}", url);
        }
        return url;
    }

    /**
     * Updates datasource configuration with password verification.
     * 
     * Security Flow:
     * 1. Fetch current datasource
     * 2. Decrypt stored password
     * 3. Compare with currentPassword provided by user
     * 4. If match: proceed with update using newPassword (or currentPassword if newPassword empty)
     * 5. If no match: throw InvalidPasswordVerificationException
     * 
     * @param tenantId The tenant ID
     * @param request Request with currentPassword verification + new config
     * @return Updated TenantDatasourceResponse
     * @throws ErrorHandler if tenant or datasource not found
     * @throws InvalidPasswordVerificationException if currentPassword doesn't match
     */
    @Override
    @Transactional(transactionManager = "masterTransactionManager")
    public TenantDatasourceResponse atualizarDatasourceComVerificacao(
            Long tenantId,
            UpdateTenantDatasourceWithPasswordRequest request) {
        
        log.info("[UPDATE DATASOURCE WITH VERIFICATION] Updating datasource for tenant: {} with password verification", tenantId);

        // 1. Get tenant
        Tenant tenant = tenantRepository.findById(tenantId)
                .orElseThrow(() -> new ErrorHandler(TenantErrorMessage.TENANT_NOT_FOUND));

        // 2. Get active datasource
        TenantDatasource tenantDatasource = tenantDatasourceRepository
                .findByTenantIdAndActiveTrue(tenant.getId())
                .orElseThrow(() -> new ErrorHandler(TenantErrorMessage.DATASOURCE_NOT_CONFIGURED));

        // 3. CRITICAL: Verify current password
        String storedPassword = tenantDatasource.getPassword(); // Automatically decrypted via @PostLoad
        if (storedPassword == null || !storedPassword.equals(request.currentPassword())) {
            log.warn("[UPDATE DATASOURCE WITH VERIFICATION] ❌ Password verification FAILED for tenant: {}", tenantId);
            throw new com.api.erp.v1.main.shared.common.error.InvalidPasswordVerificationException(
                    "Current password is invalid. Please verify and try again."
            );
        }
        log.info("[UPDATE DATASOURCE WITH VERIFICATION] ✅ Password verification PASSED for tenant: {}", tenantId);

        // 4. Determine which password to use for update
        String passwordToUse = request.getPasswordToUpdate();
        log.info("[UPDATE DATASOURCE WITH VERIFICATION] {} Password: {}", 
                request.newPassword() != null && !request.newPassword().isEmpty() ? "New" : "Keeping",
                "***");

        // 5. Test new connection with new password
        var tempRequest = new TenantDatasourceRequest(
                request.host(),
                request.port(),
                request.databaseName(),
                request.username(),
                passwordToUse,
                request.dbType(),
                false
        );

        if (!testarConexao(tenantId, tempRequest)) {
            log.error("[UPDATE DATASOURCE WITH VERIFICATION] Failed to test new datasource configuration for tenant: {}", tenantId);
            throw new ErrorHandler(TenantErrorMessage.DATASOURCE_CONNECTION_FAILED);
        }
        log.info("[UPDATE DATASOURCE WITH VERIFICATION] ✅ Connection test PASSED");

        // 6. Update entity with verified password
        tenantDatasource.setDriverClass(getDriverClass(request.dbType()));
        tenantDatasource.setUrl(buildJdbcUrl(request.host(), request.port(), request.databaseName(), request.dbType()));
        tenantDatasource.setUsername(request.username());
        tenantDatasource.setPassword(passwordToUse);  // Encriptografa automaticamente via setPassword()
        tenantDatasource.setDbType(DBType.fromNome(request.dbType()));
        tenantDatasource.setSchemaName(request.databaseName());
        tenantDatasource.setTestStatus(TenantDatasource.TestStatus.SUCCESS);
        tenantDatasource.setTestedAt(LocalDateTime.now());

        tenantDatasource = tenantDatasourceRepository.save(tenantDatasource);
        log.info("[UPDATE DATASOURCE WITH VERIFICATION] ✅ Datasource successfully updated for tenant: {}", tenantId);

        // 7. Update datasource in DataSourceRouter
        TenantDSConfig config = new TenantDSConfig(
                tenantDatasource.getTenant().getId(),
                tenantDatasource.getJdbcUrl(),
                tenantDatasource.getUsername(),
                tenantDatasource.getPassword(),
                tenantDatasource.getDbType()
        );
        DataSource dataSource = hikariDataSourceFactory.createDataSource(config);
        dataSourceRouter.registerDataSource(tenantId, dataSource);
        log.info("[UPDATE DATASOURCE WITH VERIFICATION] ✅ DataSource router updated");

        return toResponse(tenantDatasource);
    }
}
