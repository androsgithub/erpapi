package com.api.erp.v1.main.datasource.routing.core;

import com.api.erp.v1.main.datasource.routing.cache.DSCache;
import com.api.erp.v1.main.datasource.routing.domain.IDataSourceRouter;
import com.api.erp.v1.main.datasource.routing.domain.ITenantConfigProvider;
import com.api.erp.v1.main.datasource.routing.domain.TenantDataSourceNotFoundException;
import com.api.erp.v1.main.datasource.routing.infrastructure.HikariDataSourceFactory;
import com.api.erp.v1.main.shared.common.error.TenantErrorMessage;
import com.api.erp.v1.main.shared.common.error.ErrorHandler;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * INFRASTRUCTURE - IDataSourceRouter implementation
 * <p>
 * Manages and routes connections to different tenants dynamically.
 * Maintains cache of DataSources for performance.
 * Retrieves tenant configurations via ITenantConfigProvider.
 * <p>
 * Responsibilities:
 * - Register tenant DataSources in cache
 * - Route requests to the correct DataSource
 * - Retrieve and create DataSources on demand
 *
 * @author ERP System
 * @version 1.0
 */
@Component
@Slf4j
public class DataSourceRouter implements IDataSourceRouter {

    private final DataSource masterDataSource;
    private final ITenantConfigProvider tenantConfigProvider;
    private final HikariDataSourceFactory dataSourceFactory;
    private final DSCache dsCache;

    public DataSourceRouter(
            @Qualifier("masterDataSource") DataSource masterDataSource,
            ITenantConfigProvider tenantConfigProvider,
            HikariDataSourceFactory dataSourceFactory, DSCache dsCache) {
        this.masterDataSource = masterDataSource;
        this.tenantConfigProvider = tenantConfigProvider;
        this.dataSourceFactory = dataSourceFactory;
        this.dsCache = dsCache;
    }

    @Override
    public void registerDataSource(Long tenantId, DataSource dataSource) {
        if (tenantId == null || tenantId <= 0) {
            throw new IllegalArgumentException("Tenant ID cannot be null or less than 1");
        }
        if (dataSource == null) {
            throw new IllegalArgumentException("DataSource cannot be null");
        }

        dsCache.put(tenantId, dataSource);
        log.info("DataSource registered in cache for tenant: {}", tenantId);
    }

    @Override
    public DataSource getDataSource(Long tenantId) {
        if (tenantId == null || tenantId <= 0) {
            throw new IllegalArgumentException("Tenant ID cannot be null or less than 1");
        }

        // 1. Check cache
        if (isDataSourceCached(tenantId)) {
            log.debug("DataSource found in cache for tenant: {}", tenantId);
            return dsCache.get(tenantId);
        }

        // 2. Retrieve tenant configuration
        Long finalTenantId = tenantId;
        var tenantConfig = tenantConfigProvider.getTenantConfig(tenantId)
                .orElseThrow(() -> {
                    log.error("Tenant configuration not found: {}", finalTenantId);
                    throw new ErrorHandler(TenantErrorMessage.DATASOURCE_NOT_CONFIGURED);
                });

        // 3. Create and register DataSource
        try {
            DataSource dataSource = dataSourceFactory.createDataSource(tenantConfig);
            registerDataSource(finalTenantId, dataSource);
            log.info("DataSource created and cached for tenant: {}", finalTenantId);
            return dataSource;
        } catch (Exception e) {
            log.error("Error creating DataSource for tenant: {}", finalTenantId, e);
            throw new ErrorHandler(TenantErrorMessage.DATASOURCE_CONNECTION_FAILED);
        }
    }

    @Override
    public boolean isDataSourceCached(Long tenantId) {
        return dsCache.contains(tenantId);
    }

    @Override
    public void invalidateDataSourceCache(Long tenantId) {
        if (tenantId == null || tenantId <= 0) {
            throw new IllegalArgumentException("Tenant ID cannot be null or less than 1");
        }

        DataSource ds = dsCache.remove(tenantId);
        if (ds instanceof HikariDataSource) {
            ((HikariDataSource) ds).close();
            log.info("DataSource removed from cache and closed for tenant: {}", tenantId);
        }
    }

    @Override
    public DataSource getMasterDataSource() {
        return masterDataSource;
    }

    /**
     * Retrieves all registered datasources (for DEBUG)
     */
    public Map<Long, DataSource> getAllDataSources() {
        return new ConcurrentHashMap<>(dsCache.getAll());
    }
}
