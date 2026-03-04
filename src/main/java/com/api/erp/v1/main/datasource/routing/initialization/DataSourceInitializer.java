package com.api.erp.v1.main.datasource.routing.initialization;

import com.api.erp.v1.main.datasource.routing.core.DataSourceRouter;
import com.api.erp.v1.main.datasource.routing.domain.TenantDataSourceNotFoundException;
import com.api.erp.v1.main.shared.domain.exception.NotFoundException;
import com.api.erp.v1.main.tenant.domain.entity.Tenant;
import com.api.erp.v1.main.tenant.domain.repository.TenantRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationContextInitializedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * INFRASTRUCTURE - Tenant DataSources Initializer
 * 
 * Pre-loads datasource configurations for all active tenants during application startup.
 * 
 * Benefits:
 * - Avoids "datasource not found" errors during bootstrap
 * - Detects configuration issues early
 * - Improves performance by caching datasources before use
 * 
 * Responsibilities:
 * - Retrieve all tenant configurations
 * - Pre-load datasources in cache
 * - Register warnings for problematic tenants
 * 
 * @author ERP System
 * @version 1.0
 */
@Component
@Slf4j
public class DataSourceInitializer {

    private final DataSourceRouter dataSourceRouter;
    private final TenantRepository tenantRepository;

    @Autowired
    public DataSourceInitializer(DataSourceRouter dataSourceRouter, TenantRepository tenantRepository) {
        this.dataSourceRouter = dataSourceRouter;
        this.tenantRepository = tenantRepository;
    }

    /**
     * Initializes datasources for all active tenants during startup.
     * This method is executed after the application context is initialized.
     */
    @EventListener(ApplicationContextInitializedEvent.class)
    public void initializeTenantDataSources() {
        log.info("Starting pre-loading of Tenant DataSources...");
        
        try {
            List<Tenant> activeTenants = tenantRepository.findAllByAtivaTrue();
            
            if (activeTenants.isEmpty()) {
                log.warn("No active tenants found for pre-loading datasources");
                return;
            }

            log.info("Found {} active tenants", activeTenants.size());

            int successCount = 0;
            int errorCount = 0;

            for (Tenant tenant : activeTenants) {
                try {
                    // Try to pre-load the datasource for the tenant
                    dataSourceRouter.getDataSource(tenant.getId());
                    log.info("DataSource pre-loaded for tenant: {}", tenant.getId());
                    successCount++;
                } catch (NotFoundException e) {
                    log.warn("DataSource not configured for tenant {} - will be created on demand", tenant.getId());
                    errorCount++;
                } catch (Exception e) {
                    log.error("Error pre-loading DataSource for tenant {}: {}", tenant.getId(), e.getMessage());
                    errorCount++;
                }
            }

            if (errorCount > 0) {
                log.warn("Pre-loading completed: {} success(es), {} error(s)", successCount, errorCount);
            } else {
                log.info("All {} DataSources were pre-loaded successfully", successCount);
            }

        } catch (Exception e) {
            log.error("Error during DataSources pre-loading: {}", e.getMessage(), e);
            // Do not throw exception to avoid interrupting application startup
        }
    }
}
