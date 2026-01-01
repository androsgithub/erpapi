package com.api.erp.v1.features.empresa.presentation.controller;

import com.api.erp.v1.features.empresa.application.dto.*;
import com.api.erp.v1.features.empresa.application.mapper.EmpresaMapper;
import com.api.erp.v1.features.empresa.domain.controller.IEmpresaController;
import com.api.erp.v1.features.empresa.domain.entity.Empresa;
import com.api.erp.v1.features.empresa.domain.entity.EmpresaPermissions;
import com.api.erp.v1.features.empresa.domain.service.IEmpresaService;
import com.api.erp.v1.shared.infrastructure.security.RequiresPermission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/empresa")
public class EmpresaController implements IEmpresaController {

    @Autowired
    private IEmpresaService empresaService;
    @Autowired
    private EmpresaMapper empresaMapper;

    @GetMapping
    @RequiresPermission(EmpresaPermissions.BUSCAR)
    public ResponseEntity<EmpresaResponse> obter() {
        Empresa empresa = empresaService.getDadosEmpresa();
        return ResponseEntity.ok(empresaMapper.toResponse(empresa));
    }

    @PutMapping
    @RequiresPermission(EmpresaPermissions.ATUALIZAR)
    public ResponseEntity<EmpresaResponse> atualizar(
            @RequestBody EmpresaRequest request) {
        Empresa empresa = empresaService.updateDadosEmpresa(request);
        return ResponseEntity.ok(empresaMapper.toResponse(empresa));
    }

    @PutMapping("/config/cliente")
    @RequiresPermission(EmpresaPermissions.ATUALIZAR)
    public ResponseEntity<EmpresaResponse> atualizarClienteConfig(
            @RequestBody ClienteConfigRequest request) {
        Empresa empresa = empresaService.updateClienteConfig(request);
        return ResponseEntity.ok(empresaMapper.toResponse(empresa));
    }

    @PutMapping("/config/usuario")
    @RequiresPermission(EmpresaPermissions.ATUALIZAR)
    public ResponseEntity<EmpresaResponse> atualizarUsuarioConfig(
            @RequestBody UsuarioConfigRequest request) {
        Empresa empresa = empresaService.updateUsuarioConfig(request);
        return ResponseEntity.ok(empresaMapper.toResponse(empresa));
    }

    @PutMapping("/config/permissao")
    @RequiresPermission(EmpresaPermissions.ATUALIZAR)
    public ResponseEntity<EmpresaResponse> atualizarPermissaoConfig(
            @RequestBody PermissaoConfigRequest request) {
        Empresa empresa = empresaService.updatePermissaoConfig(request);
        return ResponseEntity.ok(empresaMapper.toResponse(empresa));
    }

    @PutMapping("/config/tenant")
    @RequiresPermission(EmpresaPermissions.ATUALIZAR)
    public ResponseEntity<EmpresaResponse> atualizarTenantConfig(
            @RequestBody TenantConfigRequest request) {
        Empresa empresa = empresaService.updateTenantConfig(request);
        return ResponseEntity.ok(empresaMapper.toResponse(empresa));
    }

    @PutMapping("/config/endereco")
    @RequiresPermission(EmpresaPermissions.ATUALIZAR)
    public ResponseEntity<EmpresaResponse> atualizarEnderecoConfig(
            @RequestBody EnderecoConfigRequest request) {
        Empresa empresa = empresaService.updateEnderecoConfig(request);
        return ResponseEntity.ok(empresaMapper.toResponse(empresa));
    }

    @PutMapping("/config/contato")
    @RequiresPermission(EmpresaPermissions.ATUALIZAR)
    public ResponseEntity<EmpresaResponse> atualizarContatoConfig(
            @RequestBody ContatoConfigRequest request) {
        Empresa empresa = empresaService.updateContatoConfig(request);
        return ResponseEntity.ok(empresaMapper.toResponse(empresa));
    }
}
