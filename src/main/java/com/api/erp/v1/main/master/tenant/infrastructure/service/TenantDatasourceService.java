package com.api.erp.v1.main.master.tenant.infrastructure.service;

import com.api.erp.v1.main.datasource.routing.core.DataSourceRouter;
import com.api.erp.v1.main.datasource.routing.domain.TenantDSConfig;
import com.api.erp.v1.main.datasource.routing.infrastructure.HikariDataSourceFactory;
import com.api.erp.v1.main.shared.common.error.ErrorHandler;
import com.api.erp.v1.main.shared.common.error.TenantErrorMessage;
import com.api.erp.v1.main.master.tenant.application.dto.TenantDatasourceRequest;
import com.api.erp.v1.main.master.tenant.application.dto.TenantDatasourceResponse;
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
        String dbType = request.dbType().toUpperCase();
        String host = request.host();
        int port = request.port();
        String database = request.databaseName();

        return switch (dbType) {
            case "POSTGRESQL" -> String.format("jdbc:postgresql://%s:%d/%s", host, port, database);
            case "MYSQL", "MARIADB" -> String.format("jdbc:mysql://%s:%d/%s", host, port, database);
            case "ORACLE" -> String.format("jdbc:oracle:thin:@%s:%d:%s", host, port, database);
            case "SQL_SERVER" -> String.format("jdbc:sqlserver://%s:%d;databaseName=%s", host, port, database);
            case "H2" -> String.format("jdbc:h2:mem:%s", database);
            default -> String.format("jdbc:%s://%s:%d/%s", dbType.toLowerCase(), host, port, database);
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
                tenantDatasource.getId(),
                tenantDatasource.getTenant().getId(),
                tenantDatasource.getUrl(),  // Agora usa URL completa
                tenantDatasource.getDbType().getNome(),
                tenantDatasource.getUsername(),
                tenantDatasource.getDbType(),
                tenantDatasource.getActive(),
                tenantDatasource.getCreatedAt(),
                tenantDatasource.getUpdatedAt()
        );
    }
}
