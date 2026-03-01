package com.api.erp.v1.main.tenant.domain.controller;

import com.api.erp.v1.main.tenant.application.dto.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

public interface ITenantController {

    ResponseEntity<TenantResponse> obter();

    ResponseEntity<List<TenantResponse>> listar();

    ResponseEntity<TenantResponse> atualizar(
            @RequestBody TenantRequest request
    );

    /**
     * UNIFIED CONFIG UPDATE - Consolidação de todos os 6 métodos antigos de config em 1
     */
    ResponseEntity<TenantResponse> atualizarConfigUnificada(
            @RequestBody UnifiedTenantConfigRequest request
    );
}
