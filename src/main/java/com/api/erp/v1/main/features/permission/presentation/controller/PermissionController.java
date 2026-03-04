package com.api.erp.v1.main.features.permission.presentation.controller;

import com.api.erp.v1.main.features.permission.application.dto.request.CreatePermissionRequest;
import com.api.erp.v1.main.features.permission.application.dto.response.PermissionResponse;
import com.api.erp.v1.main.features.permission.application.mapper.PermissionMapper;
import com.api.erp.v1.main.features.permission.domain.controller.IPermissionController;
import com.api.erp.v1.docs.openapi.features.permission.PermissionOpenApiDocumentation;
import com.api.erp.v1.main.features.permission.domain.entity.PermissionPermissions;
import com.api.erp.v1.main.features.permission.domain.service.IManagementPermissionService;
import com.api.erp.v1.main.shared.infrastructure.security.annotations.RequiresPermission;
import com.api.erp.v1.main.shared.infrastructure.documentation.RequiresXTenantId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/permissions")
public class PermissionController implements IPermissionController, PermissionOpenApiDocumentation {

    @Autowired
    private IManagementPermissionService gerenciamentoPermissionService;
    @Autowired
    private PermissionMapper permissionMapper;

    @PostMapping
    @RequiresXTenantId
    @RequiresPermission(PermissionPermissions.CREATE)
    public ResponseEntity<PermissionResponse> createPermission(@RequestBody CreatePermissionRequest request) {
        var permission = gerenciamentoPermissionService.createPermission(request);
        return new ResponseEntity<>(permissionMapper.toResponse(permission), HttpStatus.CREATED);
    }

    @GetMapping
    @RequiresXTenantId
    @RequiresPermission(PermissionPermissions.VIEW)
    public ResponseEntity<List<PermissionResponse>> getAllPermissions() {
        var permissions = gerenciamentoPermissionService.getAllPermissions();
        return ResponseEntity.ok(permissionMapper.toResponseList(permissions));
    }
}
