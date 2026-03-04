package com.api.erp.v1.main.tenant.domain.service;

import com.api.erp.v1.main.tenant.application.dto.*;
import com.api.erp.v1.main.tenant.domain.entity.Tenant;
import com.api.erp.v1.main.tenant.domain.entity.configs.TenantConfig;

public interface ITenantService {
    Tenant getDadosTenant(Long tenantId);

    TenantConfig getTenantConfig(Long tenantId);

    boolean isTenantAtiva(Long tenantId);

    Tenant criarTenant(CreateTenantRequest request);

    java.util.List<Tenant> listarTenants();

    Tenant updateDadosTenant(Long tenantId, TenantRequest empresaRequest);

    /**
     * UNIFIED CONFIG UPDATE - Consolidação de todos os 6 métodos antigos de config em 1
     * Processes apenas os campos que foram preenchidos (não-null)
     */
    Tenant updateConfig(Long tenantId, UnifiedTenantConfigRequest request);

    // DELETE Operations
    void deletarTenant(Long tenantId);
}
