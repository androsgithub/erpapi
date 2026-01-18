package com.api.erp.v1.tenant.presentation.controller;

import com.api.erp.v1.tenant.application.dto.*;
import com.api.erp.v1.tenant.application.mapper.TenantMapper;
import com.api.erp.v1.tenant.domain.controller.ITenantController;
import com.api.erp.v1.tenant.domain.entity.Tenant;
import com.api.erp.v1.tenant.domain.entity.TenantPermissions;
import com.api.erp.v1.tenant.domain.service.ITenantService;
import com.api.erp.v1.tenant.infrastructure.config.datasource.TenantContext;
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

    @GetMapping()
    @RequiresPermission(TenantPermissions.BUSCAR)
    public ResponseEntity<TenantResponse> obter() {
        Long tenantId = TenantContext.getTenantId();
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

    @PutMapping("")
    @RequiresPermission(TenantPermissions.ATUALIZAR)
    public ResponseEntity<TenantResponse> atualizar(

            @RequestBody TenantRequest request) {
        Long tenantId = TenantContext.getTenantId();
        Tenant tenant = tenantService.updateDadosTenant(tenantId, request);
        return ResponseEntity.ok(tenantMapper.toResponse(tenant));
    }

    @PutMapping("/config/cliente")
    @RequiresPermission(TenantPermissions.ATUALIZAR)
    public ResponseEntity<TenantResponse> atualizarClienteConfig(

            @RequestBody ClienteConfigRequest request) {
        Long tenantId = TenantContext.getTenantId();
        Tenant tenant = tenantService.updateClienteConfig(tenantId,request);
        return ResponseEntity.ok(tenantMapper.toResponse(tenant));
    }

    @PutMapping("/config/usuario")
    @RequiresPermission(TenantPermissions.ATUALIZAR)
    public ResponseEntity<TenantResponse> atualizarUsuarioConfig(

            @RequestBody UsuarioConfigRequest request) {
        Long tenantId = TenantContext.getTenantId();
        Tenant tenant = tenantService.updateUsuarioConfig(tenantId,request);
        return ResponseEntity.ok(tenantMapper.toResponse(tenant));
    }

    @PutMapping("/config/permissao")
    @RequiresPermission(TenantPermissions.ATUALIZAR)
    public ResponseEntity<TenantResponse> atualizarPermissaoConfig(

            @RequestBody PermissaoConfigRequest request) {
        Long tenantId = TenantContext.getTenantId();
        Tenant tenant = tenantService.updatePermissaoConfig(tenantId,request);
        return ResponseEntity.ok(tenantMapper.toResponse(tenant));
    }

    @PutMapping("/config/tenant")
    @RequiresPermission(TenantPermissions.ATUALIZAR)
    public ResponseEntity<TenantResponse> atualizarTenantConfig(

            @RequestBody InternalTenantConfigRequest request) {
        Long tenantId = TenantContext.getTenantId();
        Tenant tenant = tenantService.updateInternalTenantConfig(tenantId,request);
        return ResponseEntity.ok(tenantMapper.toResponse(tenant));
    }

    @PutMapping("/config/endereco")
    @RequiresPermission(TenantPermissions.ATUALIZAR)
    public ResponseEntity<TenantResponse> atualizarEnderecoConfig(

            @RequestBody EnderecoConfigRequest request) {
        Long tenantId = TenantContext.getTenantId();
        Tenant tenant = tenantService.updateEnderecoConfig(tenantId, request);
        return ResponseEntity.ok(tenantMapper.toResponse(tenant));
    }

    @PutMapping("/config/contato")
    @RequiresPermission(TenantPermissions.ATUALIZAR)
    public ResponseEntity<TenantResponse> atualizarContatoConfig(

            @RequestBody ContatoConfigRequest request) {
        Long tenantId = TenantContext.getTenantId();
        Tenant tenant = tenantService.updateContatoConfig(tenantId,request);
        return ResponseEntity.ok(tenantMapper.toResponse(tenant));
    }
}
