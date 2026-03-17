package com.api.erp.v1.main.master.tenant.application.usecase;

import com.api.erp.v1.main.master.tenant.application.dto.response.TenantDatasourceResponse;
import com.api.erp.v1.main.master.tenant.domain.service.ITenantDatasourceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * UseCase: Get Tenant Datasource Configuration
 * 
 * Responsibility: Retrieve datasource configuration for tenant
 * 
 * Flow:
 * 1. Receive tenant ID
 * 2. Call ITenantDatasourceService.obterDatasource()
 * 3. Return TenantDatasourceResponse (if found)
 */
@Slf4j
@Service
public class GetTenantDatasourceUseCase {

    private final ITenantDatasourceService datasourceService;

    public GetTenantDatasourceUseCase(
            ITenantDatasourceService datasourceService) {
        this.datasourceService = datasourceService;
    }

    /**
     * Execute: Get datasource configuration for tenant
     * 
     * @param tenantId The tenant ID
     * @return TenantDatasourceResponse with datasource details
     */
    public TenantDatasourceResponse execute(Long tenantId) {
        log.info("[GET DATASOURCE UC] Fetching datasource for tenant: {}", tenantId);

        // Call domain service - returns Optional
        TenantDatasourceResponse response = datasourceService.obterDatasource(tenantId)
                .orElseThrow(() -> new IllegalArgumentException(
                        String.format("Datasource not found for tenant: %d", tenantId)
                ));

        log.info("[GET DATASOURCE UC] Datasource retrieved successfully");

        return response;
    }
}
