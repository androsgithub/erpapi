package com.api.erp.v1.main.master.tenant.domain.service;

import com.api.erp.v1.main.master.tenant.application.dto.TenantDatasourceRequest;
import com.api.erp.v1.main.master.tenant.application.dto.TenantDatasourceResponse;

import java.util.Optional;


public interface ITenantDatasourceService {
    TenantDatasourceResponse configurarDatasource(Long tenantId, TenantDatasourceRequest request);
    Optional<TenantDatasourceResponse> obterDatasource(Long tenantId);
    TenantDatasourceResponse atualizarDatasource(Long tenantId, TenantDatasourceRequest request);
    boolean testarConexao(TenantDatasourceRequest request);
}
