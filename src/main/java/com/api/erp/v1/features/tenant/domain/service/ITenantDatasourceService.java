package com.api.erp.v1.features.tenant.domain.service;

import com.api.erp.v1.features.tenant.application.dto.TenantDatasourceRequest;
import com.api.erp.v1.features.tenant.application.dto.TenantDatasourceResponse;

import java.util.Optional;


public interface ITenantSchemaService {
    TenantDatasourceResponse configurarDatasource(String tenantId, TenantDatasourceRequest request);
    Optional<TenantDatasourceResponse> obterDatasource(String tenantId);
    TenantDatasourceResponse atualizarDatasource(String tenantId, TenantDatasourceRequest request);
    boolean testarConexao(TenantDatasourceRequest request);
}
