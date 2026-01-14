package com.api.erp.v1.features.permissao.presentation.controller;

import com.api.erp.v1.features.permissao.application.dto.request.AssociarPermissaoRequest;
import com.api.erp.v1.features.permissao.application.dto.request.CreateRoleRequest;
import com.api.erp.v1.features.permissao.application.dto.response.RoleResponse;
import com.api.erp.v1.features.permissao.application.mapper.RoleMapper;
import com.api.erp.v1.features.permissao.domain.controller.IRoleController;
import com.api.erp.v1.features.permissao.domain.entity.Role;
import com.api.erp.v1.features.permissao.domain.entity.RolePermissions;
import com.api.erp.v1.features.permissao.domain.service.IGerenciamentoPermissaoService;
import com.api.erp.v1.shared.infrastructure.security.annotations.RequiresPermission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/roles")
public class RoleController implements IRoleController {

    @Autowired
    private IGerenciamentoPermissaoService gerenciamentoPermissaoService;
    @Autowired
    private RoleMapper roleMapper;

    @PostMapping
    @RequiresPermission(RolePermissions.CRIAR)
    public ResponseEntity<RoleResponse> createRole(@RequestBody CreateRoleRequest request) {
        Role role = gerenciamentoPermissaoService.createRole(request);
        return new ResponseEntity<>(roleMapper.toResponse(role), HttpStatus.CREATED);
    }

    @GetMapping
    @RequiresPermission(RolePermissions.VISUALIZAR)
    public ResponseEntity<List<RoleResponse>> getAllRoles() {
        List<Role> roles = gerenciamentoPermissaoService.getAllRoles();
        return ResponseEntity.ok(roleMapper.toResponseList(roles));
    }

    @PostMapping("/associar")
    @RequiresPermission(RolePermissions.ASSOCIAR)
    public ResponseEntity<Void> associarPermissao(@RequestBody AssociarPermissaoRequest request) {
        gerenciamentoPermissaoService.associarPermissao(request);
        return ResponseEntity.noContent().build();
    }
}
