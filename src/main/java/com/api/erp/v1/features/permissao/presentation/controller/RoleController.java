package com.api.erp.v1.features.permissao.presentation.controller;

import com.api.erp.v1.features.permissao.application.dto.request.AssociarPermissaoRequest;
import com.api.erp.v1.features.permissao.application.dto.request.CreateRoleRequest;
import com.api.erp.v1.features.permissao.application.dto.response.RoleResponse;
import com.api.erp.v1.features.permissao.domain.entity.RolePermissions;
import com.api.erp.v1.features.permissao.domain.service.GerenciamentoPermissaoService;
import com.api.erp.v1.shared.infrastructure.security.RequiresPermission;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/roles")
@RequiredArgsConstructor
public class RoleController {

    private final GerenciamentoPermissaoService gerenciamentoPermissaoService;

    @PostMapping
    @RequiresPermission(RolePermissions.CRIAR)
    public ResponseEntity<RoleResponse> createRole(@RequestBody CreateRoleRequest request) {
        RoleResponse response = gerenciamentoPermissaoService.createRole(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
    
    @GetMapping
    @RequiresPermission(RolePermissions.VISUALIZAR)
    public ResponseEntity<List<RoleResponse>> getAllRoles() {
        List<RoleResponse> response = gerenciamentoPermissaoService.getAllRoles();
        return ResponseEntity.ok(response);
    }

    @PostMapping("/associar")
    @RequiresPermission(RolePermissions.ASSOCIAR)
    public ResponseEntity<Void> associarPermissao(@RequestBody AssociarPermissaoRequest request) {
        gerenciamentoPermissaoService.associarPermissao(request);
        return ResponseEntity.noContent().build();
    }
}
