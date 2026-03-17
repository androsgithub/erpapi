package com.api.erp.v1.main.master.tenant.application.usecase;

import com.api.erp.v1.main.master.tenant.application.dto.request.create.TenantDatasourceRequest;
import com.api.erp.v1.main.master.tenant.application.dto.response.TenantDatasourceResponse;
import com.api.erp.v1.main.master.tenant.domain.service.ITenantDatasourceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * UseCase: Configure Tenant Datasource
 * 
 * Responsibility: Configure datasource without testing connection yet
 * Used during initial tenant setup
 * 
 * Flow:
 * 1. Receive tenant ID and TenantDatasourceRequest
 * 2. Call ITenantDatasourceService.configurarDatasource()
 * 3. Return TenantDatasourceResponse
 */
@Slf4j
@Service
public class ConfigureTenantDatasourceUseCase {

    private final ITenantDatasourceService datasourceService;

    public ConfigureTenantDatasourceUseCase(
            ITenantDatasourceService datasourceService) {
        this.datasourceService = datasourceService;
    }

    /**
     * Execute: Configure datasource for tenant
     * 
     * @param tenantId The tenant ID
     * @param request TenantDatasourceRequest with datasource settings
     * @return TenantDatasourceResponse with configured datasource details
     */
    @Transactional
    public TenantDatasourceResponse execute(Long tenantId, TenantDatasourceRequest request) {
        log.info("[CONFIGURE DATASOURCE UC] Configuring datasource for tenant: {}", tenantId);

        // Call domain service
        TenantDatasourceResponse response = datasourceService.configurarDatasource(tenantId, request);

        log.info("[CONFIGURE DATASOURCE UC] Datasource configured successfully");

        return response;
    }
}
