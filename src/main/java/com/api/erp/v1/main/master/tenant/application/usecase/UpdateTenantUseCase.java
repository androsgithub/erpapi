package com.api.erp.v1.main.master.tenant.application.usecase;

import com.api.erp.v1.main.master.tenant.application.dto.request.create.TenantRequest;
import com.api.erp.v1.main.master.tenant.application.dto.response.TenantResponse;
import com.api.erp.v1.main.master.tenant.domain.entity.Tenant;
import com.api.erp.v1.main.master.tenant.domain.service.ITenantService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * UseCase: Update Tenant Basic Information
 * 
 * Responsibility: Orchestrate tenant data updates (name, email, phone)
 * 
 * Flow:
 * 1. Receive tenant ID and TenantRequest
 * 2. Call ITenantService.updateDadosTenant()
 * 3. Map updated entity to response DTO
 * 4. Return TenantResponse
 */
@Slf4j
@Service
public class UpdateTenantUseCase {

    private final ITenantService tenantService;
    private final TenantUseCaseMapper mapper;

    public UpdateTenantUseCase(
            ITenantService tenantService,
            TenantUseCaseMapper mapper) {
        this.tenantService = tenantService;
        this.mapper = mapper;
    }

    /**
     * Execute: Update tenant basic data
     * 
     * @param tenantId The tenant ID
     * @param request TenantRequest with updated data
     * @return TenantResponse with updated tenant
     */
    @Transactional
    public TenantResponse execute(Long tenantId, TenantRequest request) {
        log.info("[UPDATE TENANT UC] Updating tenant: {}", tenantId);

        // Call domain service
        Tenant tenant = tenantService.updateDadosTenant(tenantId, request);

        log.info("[UPDATE TENANT UC] Tenant updated successfully");

        // Map to response DTO
        return mapper.tenantToResponse(tenant);
    }
}
