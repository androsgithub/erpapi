package com.api.erp.v1.features.tenant.domain.service;

import com.api.erp.v1.features.tenant.application.dto.*;
import com.api.erp.v1.features.tenant.domain.entity.Tenant;
import com.api.erp.v1.features.tenant.domain.entity.TenantConfig;

public interface ITenantService {
    Tenant getDadosTenant(Long tenantId);

    TenantConfig getTenantConfig(Long tenantId);

    boolean isTenantAtiva(Long tenantId);

    Tenant criarTenant(CriarTenantRequest request);

    java.util.List<Tenant> listarTenants();

    Tenant updateDadosTenant(Long tenantId, TenantRequest empresaRequest);

    Tenant updateClienteConfig(Long tenantId, ClienteConfigRequest clienteConfigRequest);

    Tenant updateContatoConfig(Long tenantId, ContatoConfigRequest contatoConfigRequest);

    Tenant updateEnderecoConfig(Long tenantId, EnderecoConfigRequest enderecoConfigRequest);

    Tenant updatePermissaoConfig(Long tenantId, PermissaoConfigRequest permissaoConfigRequest);

    Tenant updateInternalTenantConfig(Long tenantId, InternalTenantConfigRequest internalTenantConfigRequest);

    Tenant updateUsuarioConfig(Long tenantId, UsuarioConfigRequest usuarioConfigRequest);

    // DELETE Operations
    void deletarTenant(Long tenantId);
}
