package com.api.erp.v1.main.master.tenant.application.usecase;

import com.api.erp.v1.main.master.tenant.application.dto.request.update.UnifiedTenantConfigRequest;
import com.api.erp.v1.main.master.tenant.application.dto.response.TenantResponse;
import com.api.erp.v1.main.master.tenant.domain.entity.Tenant;
import com.api.erp.v1.main.master.tenant.domain.service.ITenantService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * UseCase: Update Tenant Configuration (Unified)
 * 
 * Responsibility: Orchestrate unified config updates
 * Consolidates 6 legacy methods into a single unified approach
 * 
 * Flow:
 * 1. Receive tenant ID and UnifiedTenantConfigRequest
 * 2. Call ITenantService.updateConfig() (processes only non-null fields)
 * 3. Map updated entity to response DTO
 * 4. Return TenantResponse
 */
@Slf4j
@Service
public class UpdateTenantConfigUseCase {

    private final ITenantService tenantService;
    private final TenantUseCaseMapper mapper;

    public UpdateTenantConfigUseCase(
            ITenantService tenantService,
            TenantUseCaseMapper mapper) {
        this.tenantService = tenantService;
        this.mapper = mapper;
    }

    /**
     * Execute: Update unified tenant configuration
     * 
     * Only non-null fields in the request will be processed
     * 
     * @param tenantId The tenant ID
     * @param request UnifiedTenantConfigRequest with fields to update
     * @return TenantResponse with updated tenant
     */
    @Transactional
    public TenantResponse execute(Long tenantId, UnifiedTenantConfigRequest request) {
        log.info("[UPDATE TENANT CONFIG UC] Updating config for tenant: {}", tenantId);

        // Call domain service - only processes non-null fields
        Tenant tenant = tenantService.updateConfig(tenantId, request);

        log.info("[UPDATE TENANT CONFIG UC] Tenant config updated successfully");

        // Map to response DTO
        return mapper.tenantToResponse(tenant);
    }
}
