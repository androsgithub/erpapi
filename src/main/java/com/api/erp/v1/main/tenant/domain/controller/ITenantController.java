package com.api.erp.v1.main.tenant.domain.controller;

import com.api.erp.v1.main.tenant.application.dto.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

public interface ITenantController {

    ResponseEntity<TenantResponse> obter();

    ResponseEntity<TenantResponse> criar(
            @RequestBody CriarTenantRequest request
    );

    ResponseEntity<List<TenantResponse>> listar();

    ResponseEntity<TenantResponse> atualizar(
            @RequestBody TenantRequest request
    );

    ResponseEntity<TenantResponse> atualizarCustomerConfig(
            @RequestBody CustomerConfigRequest request
    );

    ResponseEntity<TenantResponse> atualizarUserConfig(
            @RequestBody UserConfigRequest request
    );

    ResponseEntity<TenantResponse> atualizarPermissionConfig(
            @RequestBody PermissionConfigRequest request
    );

    ResponseEntity<TenantResponse> atualizarTenantConfig(
            @RequestBody InternalTenantConfigRequest request
    );

    ResponseEntity<TenantResponse> atualizarAddressConfig(
            @RequestBody AddressConfigRequest request
    );

    ResponseEntity<TenantResponse> atualizarContactConfig(
            @RequestBody ContactConfigRequest request
    );
}
