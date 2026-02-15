package com.api.erp.v1.main.tenant.domain.service;

import com.api.erp.v1.main.tenant.application.dto.TenantDatasourceRequest;
import com.api.erp.v1.main.tenant.application.dto.TenantDatasourceResponse;

import java.util.Optional;


public interface ITenantDatasourceService {
    TenantDatasourceResponse configurarDatasource(Long tenantId, TenantDatasourceRequest request);
    Optional<TenantDatasourceResponse> obterDatasource(Long tenantId);
    TenantDatasourceResponse atualizarDatasource(Long tenantId, TenantDatasourceRequest request);
    boolean testarConexao(TenantDatasourceRequest request);
}
