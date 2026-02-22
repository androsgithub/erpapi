package com.api.erp.v1.main.tenant.domain.service;

import com.api.erp.v1.main.tenant.application.dto.*;
import com.api.erp.v1.main.tenant.domain.entity.Tenant;
import com.api.erp.v1.main.tenant.domain.entity.configs.TenantConfig;

public interface ITenantService {
    Tenant getDadosTenant(Long tenantId);

    TenantConfig getTenantConfig(Long tenantId);

    boolean isTenantAtiva(Long tenantId);

    Tenant criarTenant(CriarTenantRequest request);

    java.util.List<Tenant> listarTenants();

    Tenant updateDadosTenant(Long tenantId, TenantRequest empresaRequest);

    Tenant updateCustomerConfig(Long tenantId, CustomerConfigRequest customerConfigRequest);

    Tenant updateContactConfig(Long tenantId, ContactConfigRequest contactConfigRequest);

    Tenant updateAddressConfig(Long tenantId, AddressConfigRequest addressConfigRequest);

    Tenant updatePermissionConfig(Long tenantId, PermissionConfigRequest permissionConfigRequest);

    Tenant updateInternalTenantConfig(Long tenantId, InternalTenantConfigRequest internalTenantConfigRequest);

    Tenant updateUserConfig(Long tenantId, UserConfigRequest userConfigRequest);

    // DELETE Operations
    void deletarTenant(Long tenantId);
}
