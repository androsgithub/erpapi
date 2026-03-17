package com.api.erp.v1.main.master.tenant.application.usecase;

import com.api.erp.v1.main.master.tenant.application.dto.request.create.TenantDatasourceRequest;
import com.api.erp.v1.main.master.tenant.application.dto.response.TenantDatasourceResponse;
import com.api.erp.v1.main.master.tenant.domain.service.ITenantDatasourceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * UseCase: Update Tenant Datasource Configuration
 * 
 * Responsibility: Update datasource settings for existing tenant
 * Optionally enqueues migrations based on flag
 * 
 * Flow:
 * 1. Receive tenant ID and TenantDatasourceRequest
 * 2. Call ITenantDatasourceService.atualizarDatasource()
 * 3. Return TenantDatasourceResponse
 * 
 * Note: Migration queuing is handled in the controller
 */
@Slf4j
@Service
public class UpdateTenantDatasourceUseCase {

    private final ITenantDatasourceService datasourceService;

    public UpdateTenantDatasourceUseCase(
            ITenantDatasourceService datasourceService) {
        this.datasourceService = datasourceService;
    }

    /**
     * Execute: Update datasource configuration
     * 
     * Does NOT enqueue migrations - that's handled separately in the controller
     * 
     * @param tenantId The tenant ID
     * @param request TenantDatasourceRequest with updated datasource settings
     * @return TenantDatasourceResponse with updated datasource details
     */
    @Transactional
    public TenantDatasourceResponse execute(Long tenantId, TenantDatasourceRequest request) {
        log.info("[UPDATE DATASOURCE UC] Updating datasource for tenant: {}", tenantId);

        // Call domain service
        TenantDatasourceResponse response = datasourceService.atualizarDatasource(tenantId, request);

        log.info("[UPDATE DATASOURCE UC] Datasource updated successfully");

        return response;
    }
}
