package com.api.erp.v1.main.master.permission.domain.service;

import com.api.erp.v1.main.master.permission.application.dto.request.edit.AddPermissionToUserRequest;
import com.api.erp.v1.main.master.permission.application.dto.request.create.NewPermissionRequest;
import com.api.erp.v1.main.master.permission.application.dto.request.create.NewRoleRequest;
import com.api.erp.v1.main.master.permission.domain.entity.Permission;
import com.api.erp.v1.main.master.permission.domain.entity.Role;

import java.util.List;
import java.util.Set;

public interface IManagementPermissionService {

    Permission createPermission(NewPermissionRequest request);

    Role createRole(NewRoleRequest request);

    void associarPermission(AddPermissionToUserRequest request);
    
    Set<Permission> getAllPermissions();

    Set<Role> getAllRoles();
}
