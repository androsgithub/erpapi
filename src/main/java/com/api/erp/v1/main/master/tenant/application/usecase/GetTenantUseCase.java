package com.api.erp.v1.main.master.tenant.application.usecase;

import com.api.erp.v1.main.master.tenant.application.dto.response.TenantResponse;
import com.api.erp.v1.main.master.tenant.domain.entity.Tenant;
import com.api.erp.v1.main.master.tenant.domain.service.ITenantService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * UseCase: Get Tenant Details
 * 
 * Responsibility: Retrieve and return tenant information
 * 
 * Flow:
 * 1. Receive tenant ID
 * 2. Call ITenantService.getDadosTenant()
 * 3. Map entity to response DTO
 * 4. Return TenantResponse
 */
@Slf4j
@Service
public class GetTenantUseCase {

    private final ITenantService tenantService;
    private final TenantUseCaseMapper mapper;

    public GetTenantUseCase(
            ITenantService tenantService,
            TenantUseCaseMapper mapper) {
        this.tenantService = tenantService;
        this.mapper = mapper;
    }

    /**
     * Execute: Get tenant details by ID
     * 
     * @param tenantId The tenant ID
     * @return TenantResponse with tenant details
     */
    public TenantResponse execute(Long tenantId) {
        log.info("[GET TENANT UC] Fetching tenant: {}", tenantId);

        // Call domain service
        Tenant tenant = tenantService.getDadosTenant(tenantId);

        log.info("[GET TENANT UC] Tenant retrieved successfully");

        // Map to response DTO
        return mapper.tenantToResponse(tenant);
    }
}
