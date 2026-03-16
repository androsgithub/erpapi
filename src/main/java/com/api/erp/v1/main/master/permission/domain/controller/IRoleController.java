package com.api.erp.v1.main.master.permission.domain.controller;

import com.api.erp.v1.main.master.permission.application.dto.request.create.NewRoleRequest;
import com.api.erp.v1.main.master.permission.application.dto.request.edit.AddPermissionToUserRequest;
import com.api.erp.v1.main.master.permission.application.dto.response.RoleResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Set;

public interface IRoleController {

    ResponseEntity<RoleResponse> createRole(
            @RequestBody NewRoleRequest request
    );

    ResponseEntity<Set<RoleResponse>> getAllRoles();

    ResponseEntity<Void> associarPermission(
            @RequestBody AddPermissionToUserRequest request
    );
}
