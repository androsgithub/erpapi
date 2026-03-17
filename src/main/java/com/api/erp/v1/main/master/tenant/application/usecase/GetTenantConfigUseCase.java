package com.api.erp.v1.main.master.tenant.application.usecase;

import com.api.erp.v1.main.master.tenant.application.dto.response.TenantConfigResponse;
import com.api.erp.v1.main.master.tenant.domain.entity.TenantConfig;
import com.api.erp.v1.main.master.tenant.domain.service.ITenantService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * UseCase: Get Tenant Configuration
 * 
 * Responsibility: Retrieve tenant configuration
 * 
 * Flow:
 * 1. Receive tenant ID
 * 2. Call ITenantService.getTenantConfig()
 * 3. Map to response DTO
 * 4. Return TenantConfigResponse
 */
@Slf4j
@Service
public class GetTenantConfigUseCase {

    private final ITenantService tenantService;
    private final TenantUseCaseMapper mapper;

    public GetTenantConfigUseCase(
            ITenantService tenantService,
            TenantUseCaseMapper mapper) {
        this.tenantService = tenantService;
        this.mapper = mapper;
    }

    /**
     * Execute: Get tenant configuration
     * 
     * @param tenantId The tenant ID
     * @return TenantConfigResponse with tenant config
     */
    public TenantConfigResponse execute(Long tenantId) {
        log.info("[GET TENANT CONFIG UC] Fetching config for tenant: {}", tenantId);

        // Call domain service
        TenantConfig config = tenantService.getTenantConfig(tenantId);

        log.info("[GET TENANT CONFIG UC] Config retrieved successfully");

        // Map to response DTO
        return mapper.tenantConfigToResponse(config);
    }
}
