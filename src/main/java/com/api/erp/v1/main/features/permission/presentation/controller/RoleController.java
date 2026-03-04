package com.api.erp.v1.main.features.permission.presentation.controller;

import com.api.erp.v1.main.features.permission.application.dto.request.AssociarPermissionRequest;
import com.api.erp.v1.main.features.permission.application.dto.request.CreateRoleRequest;
import com.api.erp.v1.main.features.permission.application.dto.response.RoleResponse;
import com.api.erp.v1.main.features.permission.application.mapper.RoleMapper;
import com.api.erp.v1.main.features.permission.domain.controller.IRoleController;
import com.api.erp.v1.docs.openapi.features.permission.RoleOpenApiDocumentation;
import com.api.erp.v1.main.features.permission.domain.entity.Role;
import com.api.erp.v1.main.features.permission.domain.entity.RolePermissions;
import com.api.erp.v1.main.features.permission.domain.service.IManagementPermissionService;
import com.api.erp.v1.main.shared.infrastructure.security.annotations.RequiresPermission;
import com.api.erp.v1.main.shared.infrastructure.documentation.RequiresXTenantId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/roles")
public class RoleController implements IRoleController, RoleOpenApiDocumentation {

    @Autowired
    private IManagementPermissionService gerenciamentoPermissionService;
    @Autowired
    private RoleMapper roleMapper;

    @PostMapping
    @RequiresXTenantId
    @RequiresPermission(RolePermissions.CREATE)
    public ResponseEntity<RoleResponse> createRole(@RequestBody CreateRoleRequest request) {
        Role role = gerenciamentoPermissionService.createRole(request);
        return new ResponseEntity<>(roleMapper.toResponse(role), HttpStatus.CREATED);
    }

    @GetMapping
    @RequiresXTenantId
    @RequiresPermission(RolePermissions.VIEW)
    public ResponseEntity<List<RoleResponse>> getAllRoles() {
        List<Role> roles = gerenciamentoPermissionService.getAllRoles();
        return ResponseEntity.ok(roleMapper.toResponseList(roles));
    }

    @PostMapping("/associar")
    @RequiresXTenantId
    @RequiresPermission(RolePermissions.ASSIGN)
    public ResponseEntity<Void> associarPermission(@RequestBody AssociarPermissionRequest request) {
        gerenciamentoPermissionService.associarPermission(request);
        return ResponseEntity.noContent().build();
    }
}
