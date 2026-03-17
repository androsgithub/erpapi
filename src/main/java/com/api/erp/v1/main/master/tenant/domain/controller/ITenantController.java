package com.api.erp.v1.main.master.tenant.domain.controller;

import com.api.erp.v1.main.master.tenant.application.dto.request.create.TenantRequest;
import com.api.erp.v1.main.master.tenant.application.dto.request.update.UnifiedTenantConfigRequest;
import com.api.erp.v1.main.master.tenant.application.dto.response.TenantSummaryResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

public interface ITenantController {

    ResponseEntity<TenantSummaryResponse> obter();

    ResponseEntity<List<TenantSummaryResponse>> listar();

    ResponseEntity<TenantSummaryResponse> atualizar(
            @PathVariable Long id,
            @RequestBody TenantRequest request
    );

    /**
     * UNIFIED CONFIG UPDATE - Consolidação de todos os 6 métodos antigos de config em 1
     */
    ResponseEntity<TenantSummaryResponse> atualizarConfigUnificada(
            @PathVariable Long id,
            @RequestBody UnifiedTenantConfigRequest request
    );
}
