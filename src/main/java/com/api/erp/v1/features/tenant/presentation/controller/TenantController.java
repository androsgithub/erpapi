package com.api.erp.v1.features.tenant.presentation.controller;

import com.api.erp.v1.features.tenant.application.dto.*;
import com.api.erp.v1.features.tenant.application.mapper.TenantMapper;
import com.api.erp.v1.features.tenant.domain.controller.ITenantController;
import com.api.erp.v1.features.tenant.domain.entity.Tenant;
import com.api.erp.v1.features.tenant.domain.entity.TenantPermissions;
import com.api.erp.v1.features.tenant.domain.service.ITenantService;
import com.api.erp.v1.shared.infrastructure.security.annotations.RequiresPermission;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller para gerenciar Tenants
 * Todos os métodos usam o datasource padrão (default)
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/tenant")
public class TenantController implements ITenantController {

    @Autowired
    private ITenantService tenantService;
    @Autowired
    private TenantMapper tenantMapper;

    @GetMapping("/{tenantId}")
    @RequiresPermission(TenantPermissions.BUSCAR)
    public ResponseEntity<TenantResponse> obter(@PathVariable Long tenantId) {
        Tenant tenant = tenantService.getDadosTenant(tenantId);
        return ResponseEntity.ok(tenantMapper.toResponse(tenant));
    }

    @PostMapping
    @RequiresPermission(TenantPermissions.ATUALIZAR)
    public ResponseEntity<TenantResponse> criar(@RequestBody CriarTenantRequest request) {
        log.info("[EMPRESA CONTROLLER] Criando nova tenant: {}", request.nome());
        Tenant tenant = tenantService.criarTenant(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(tenantMapper.toResponse(tenant));
    }

    @GetMapping("/listar")
    @RequiresPermission(TenantPermissions.BUSCAR)
    public ResponseEntity<List<TenantResponse>> listar() {
        log.info("[EMPRESA CONTROLLER] Listando todas as tenants");
        List<Tenant> tenants = tenantService.listarTenants();
        List<TenantResponse> responses = tenants.stream()
                .map(tenantMapper::toResponse)
                .toList();
        return ResponseEntity.ok(responses);
    }

    @DeleteMapping("/{tenantId}")
    @RequiresPermission(TenantPermissions.ATUALIZAR)
    public ResponseEntity<Void> deletar(@PathVariable Long tenantId) {
        log.info("[EMPRESA CONTROLLER] Deletando tenant: {}", tenantId);
        tenantService.deletarTenant(tenantId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{tenantId}")
    @RequiresPermission(TenantPermissions.ATUALIZAR)
    public ResponseEntity<TenantResponse> atualizar(
            @PathVariable Long tenantId,
            @RequestBody TenantRequest request) {
        Tenant tenant = tenantService.updateDadosTenant(tenantId, request);
        return ResponseEntity.ok(tenantMapper.toResponse(tenant));
    }

    @PutMapping("/{tenantId}/config/cliente")
    @RequiresPermission(TenantPermissions.ATUALIZAR)
    public ResponseEntity<TenantResponse> atualizarClienteConfig(
            @PathVariable Long tenantId,
            @RequestBody ClienteConfigRequest request) {
        Tenant tenant = tenantService.updateClienteConfig(tenantId,request);
        return ResponseEntity.ok(tenantMapper.toResponse(tenant));
    }

    @PutMapping("/{tenantId}/config/usuario")
    @RequiresPermission(TenantPermissions.ATUALIZAR)
    public ResponseEntity<TenantResponse> atualizarUsuarioConfig(
            @PathVariable Long tenantId,
            @RequestBody UsuarioConfigRequest request) {
        Tenant tenant = tenantService.updateUsuarioConfig(tenantId,request);
        return ResponseEntity.ok(tenantMapper.toResponse(tenant));
    }

    @PutMapping("/{tenantId}/config/permissao")
    @RequiresPermission(TenantPermissions.ATUALIZAR)
    public ResponseEntity<TenantResponse> atualizarPermissaoConfig(
            @PathVariable Long tenantId,
            @RequestBody PermissaoConfigRequest request) {
        Tenant tenant = tenantService.updatePermissaoConfig(tenantId,request);
        return ResponseEntity.ok(tenantMapper.toResponse(tenant));
    }

    @PutMapping("/{tenantId}/config/tenant")
    @RequiresPermission(TenantPermissions.ATUALIZAR)
    public ResponseEntity<TenantResponse> atualizarTenantConfig(
            @PathVariable Long tenantId,
            @RequestBody InternalTenantConfigRequest request) {
        Tenant tenant = tenantService.updateInternalTenantConfig(tenantId,request);
        return ResponseEntity.ok(tenantMapper.toResponse(tenant));
    }

    @PutMapping("/{tenantId}/config/endereco")
    @RequiresPermission(TenantPermissions.ATUALIZAR)
    public ResponseEntity<TenantResponse> atualizarEnderecoConfig(
            @PathVariable Long tenantId,
            @RequestBody EnderecoConfigRequest request) {
        Tenant tenant = tenantService.updateEnderecoConfig(tenantId, request);
        return ResponseEntity.ok(tenantMapper.toResponse(tenant));
    }

    @PutMapping("/{tenantId}/config/contato")
    @RequiresPermission(TenantPermissions.ATUALIZAR)
    public ResponseEntity<TenantResponse> atualizarContatoConfig(
            @PathVariable Long tenantId,
            @RequestBody ContatoConfigRequest request) {
        Tenant tenant = tenantService.updateContatoConfig(tenantId,request);
        return ResponseEntity.ok(tenantMapper.toResponse(tenant));
    }
}
