package com.api.erp.v1.main.tenant.domain.controller;

import com.api.erp.v1.main.tenant.application.dto.TenantDatasourceRequest;
import com.api.erp.v1.main.tenant.application.dto.TenantDatasourceResponse;
import org.springframework.http.ResponseEntity;

public interface ITenantDatabaseController {

    ResponseEntity<TenantDatasourceResponse> configurarDatasource(
            TenantDatasourceRequest request
    );

    ResponseEntity<TenantDatasourceResponse> obterDatasource();

    ResponseEntity<TenantDatasourceResponse> atualizarDatasource(
            TenantDatasourceRequest request
    );

    ResponseEntity<?> testarConexaoDatasource(
            TenantDatasourceRequest request
    );
}
