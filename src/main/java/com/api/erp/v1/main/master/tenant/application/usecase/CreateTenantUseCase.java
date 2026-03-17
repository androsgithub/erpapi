package com.api.erp.v1.main.master.tenant.application.usecase;

import com.api.erp.v1.main.master.tenant.application.dto.request.create.ProvisionTenantRequest;
import com.api.erp.v1.main.master.tenant.application.dto.response.TenantResponse;
import com.api.erp.v1.main.master.tenant.domain.entity.Tenant;
import com.api.erp.v1.main.master.tenant.domain.service.ITenantService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * UseCase: Create a new Tenant
 * 
 * Responsibility: Orchestrate tenant creation via domain service
 * 
 * Flow:
 * 1. Receive ProvisionTenantRequest
 * 2. Delegate to ITenantService.criarTenant()
 * 3. Map entity to response DTO
 * 4. Return TenantResponse
 * 
 * NO Spring dependencies in domain layer
 * NO HTTP logic
 * NO direct EntityManager access
 */
@Slf4j
@Service
public class CreateTenantUseCase {

    private final ITenantService tenantService;
    private final TenantUseCaseMapper mapper;

    public CreateTenantUseCase(
            ITenantService tenantService,
            TenantUseCaseMapper mapper) {
        this.tenantService = tenantService;
        this.mapper = mapper;
    }

    /**
     * Execute: Create new tenant
     * 
     * @param request ProvisionTenantRequest with tenant data
     * @return TenantResponse with created tenant details
     */
    @Transactional
    public TenantResponse execute(ProvisionTenantRequest request) {
        log.info("[CREATE TENANT UC] Creating new tenant: {}", request.name());

        // Delegate to domain service
        Tenant tenant = tenantService.criarTenant(request);

        log.info("[CREATE TENANT UC] Tenant created successfully with ID: {}", tenant.getId());

        // Map to response DTO
        return mapper.tenantToResponse(tenant);
    }
}
