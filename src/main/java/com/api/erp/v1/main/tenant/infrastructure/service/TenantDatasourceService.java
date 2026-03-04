package com.api.erp.v1.main.tenant.infrastructure.service;

import com.api.erp.v1.main.datasource.routing.core.DataSourceRouter;
import com.api.erp.v1.main.datasource.routing.domain.TenantDSConfig;
import com.api.erp.v1.main.datasource.routing.infrastructure.HikariDataSourceFactory;
import com.api.erp.v1.main.shared.common.error.TenantErrorMessage;
import com.api.erp.v1.main.shared.common.error.ErrorHandler;
import com.api.erp.v1.main.tenant.application.dto.TenantDatasourceRequest;
import com.api.erp.v1.main.tenant.application.dto.TenantDatasourceResponse;
import com.api.erp.v1.main.tenant.domain.entity.DBType;
import com.api.erp.v1.main.tenant.domain.entity.Tenant;
import com.api.erp.v1.main.tenant.domain.entity.TenantDatasource;
import com.api.erp.v1.main.tenant.domain.repository.TenantDatasourceRepository;
import com.api.erp.v1.main.tenant.domain.repository.TenantRepository;
import com.api.erp.v1.main.tenant.domain.service.ITenantDatasourceService;
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
 * TenantSchemaService
 * <p>
 * Service implementation for managing tenant datasource configurations.
 * <p>
 * Responsibilities:
 * - Configure novo datasource para um tenant
 * - Update datasource configuration
 * - Test database connection
 * - Logsr datasource no DataSourceRouter
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
        if (tenantDatasourceRepository.existsByTenant_IdAndIsActive(tenant.getId(), true)) {
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
            .host(request.host())
            .port(request.port())
            .databaseName(request.databaseName())
            .username(request.username())
            .password(request.password())
            .dbType(DBType.fromNome(request.dbType()))
            .isActive(true)
            .testStatus(TenantDatasource.TestStatus.SUCCESS)
            .testedAt(LocalDateTime.now())
            .build();

        tenantDatasource = tenantDatasourceRepository.save(tenantDatasource);
        log.info("Datasource successfully configured for tenant: {} | ID: {}", tenantId, tenant.getId());

        // 5. Register datasource in DataSourceRouter
        TenantDSConfig config = new TenantDSConfig(tenantDatasource.getTenant().getId(), tenantDatasource.getJdbcUrl(), tenantDatasource.getUsername(), tenantDatasource.getPassword(), tenantDatasource.getDbType());
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
            .flatMap(tenant -> tenantDatasourceRepository.findByTenant_IdAndIsActive(tenant.getId(), true))
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
            .findByTenant_IdAndIsActive(tenant.getId(), true)
            .orElseThrow(() -> new ErrorHandler(TenantErrorMessage.DATASOURCE_NOT_CONFIGURED));

        // 3. Test new connection
        if (!testarConexao(tenantId, request)) {
            log.error("Failed to test new datasource configuration for tenant: {}", tenantId);
            throw new ErrorHandler(TenantErrorMessage.DATASOURCE_CONNECTION_FAILED);
        }

        // 4. Update entity
        tenantDatasource.setHost(request.host());
        tenantDatasource.setPort(request.port());
        tenantDatasource.setDatabaseName(request.databaseName());
        tenantDatasource.setUsername(request.username());
        tenantDatasource.setPassword(request.password());
        tenantDatasource.setDbType(DBType.fromNome(request.dbType()));
        tenantDatasource.setTestStatus(TenantDatasource.TestStatus.SUCCESS);
        tenantDatasource.setTestedAt(LocalDateTime.now());

        tenantDatasource = tenantDatasourceRepository.save(tenantDatasource);
        log.info("Datasource successfully updated for tenant: {}", tenantId);

        // 5. Update datasource in DataSourceRouter
        TenantDSConfig config = new TenantDSConfig(tenantDatasource.getTenant().getId(), tenantDatasource.getJdbcUrl(), tenantDatasource.getUsername(), tenantDatasource.getPassword(), tenantDatasource.getDbType());
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
            TenantDatasource tempConfig = TenantDatasource.builder()
                .host(request.host())
                .port(request.port())
                .databaseName(request.databaseName())
                .username(request.username())
                .password(request.password())
                .dbType(DBType.fromNome(request.dbType()))
                .build();

            // Use tenantId directly instead of trying to get it from temporary object
            DataSource testDataSource = hikariDataSourceFactory.createDataSource(
                new TenantDSConfig(
                    tenantId,
                    tempConfig.getJdbcUrl(),
                    tempConfig.getUsername(),
                    tempConfig.getPassword(),
                    tempConfig.getDbType()
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
     * Converte TenantDatasource para TenantDatasourceResponse
     */
    private TenantDatasourceResponse toResponse(TenantDatasource tenantDatasource) {
        return new TenantDatasourceResponse(tenantDatasource.getId(), tenantDatasource.getTenant().getId(), tenantDatasource.getHost(), tenantDatasource.getPort(), tenantDatasource.getDatabaseName(), tenantDatasource.getUsername(), tenantDatasource.getDbType(), tenantDatasource.getIsActive(), tenantDatasource.getCreatedAt(), tenantDatasource.getUpdatedAt());
    }
}
