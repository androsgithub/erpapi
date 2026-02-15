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

    ResponseEntity<TenantResponse> atualizarClienteConfig(
            @RequestBody ClienteConfigRequest request
    );

    ResponseEntity<TenantResponse> atualizarUsuarioConfig(
            @RequestBody UsuarioConfigRequest request
    );

    ResponseEntity<TenantResponse> atualizarPermissaoConfig(
            @RequestBody PermissaoConfigRequest request
    );

    ResponseEntity<TenantResponse> atualizarTenantConfig(
            @RequestBody InternalTenantConfigRequest request
    );

    ResponseEntity<TenantResponse> atualizarEnderecoConfig(
            @RequestBody EnderecoConfigRequest request
    );

    ResponseEntity<TenantResponse> atualizarContatoConfig(
            @RequestBody ContatoConfigRequest request
    );
}
