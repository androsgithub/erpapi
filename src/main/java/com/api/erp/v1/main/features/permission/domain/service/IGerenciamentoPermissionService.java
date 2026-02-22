package com.api.erp.v1.main.features.permission.domain.service;

import com.api.erp.v1.main.features.permission.application.dto.request.AssociarPermissionRequest;
import com.api.erp.v1.main.features.permission.application.dto.request.CreatePermissionRequest;
import com.api.erp.v1.main.features.permission.application.dto.request.CreateRoleRequest;
import com.api.erp.v1.main.features.permission.domain.entity.Permission;
import com.api.erp.v1.main.features.permission.domain.entity.Role;

import java.util.List;

public interface IGerenciamentoPermissionService {

    Permission createPermission(CreatePermissionRequest request);

    Role createRole(CreateRoleRequest request);

    void associarPermission(AssociarPermissionRequest request);
    
    List<Permission> getAllPermissions();

    List<Role> getAllRoles();
}
