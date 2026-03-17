package com.api.erp.v1.main.master.tenant.application.usecase;

import com.api.erp.v1.main.master.tenant.application.dto.response.TenantResponse;
import com.api.erp.v1.main.master.tenant.domain.entity.Tenant;
import com.api.erp.v1.main.master.tenant.domain.service.ITenantService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * UseCase: List All Tenants
 * 
 * Responsibility: Retrieve list of all tenants
 * 
 * Flow:
 * 1. Call ITenantService.listarTenants()
 * 2. Map entity list to response DTOs
 * 3. Return list of TenantResponse
 */
@Slf4j
@Service
public class ListTenantsUseCase {

    private final ITenantService tenantService;
    private final TenantUseCaseMapper mapper;

    public ListTenantsUseCase(
            ITenantService tenantService,
            TenantUseCaseMapper mapper) {
        this.tenantService = tenantService;
        this.mapper = mapper;
    }

    /**
     * Execute: List all tenants
     * 
     * @return List of TenantResponse with all tenants
     */
    public List<TenantResponse> execute() {
        log.info("[LIST TENANTS UC] Listing all tenants");

        // Call domain service
        List<Tenant> tenants = tenantService.listarTenants();

        log.info("[LIST TENANTS UC] Found {} tenants", tenants.size());

        // Map to response DTOs
        return tenants.stream()
                .map(mapper::tenantToResponse)
                .toList();
    }
}
