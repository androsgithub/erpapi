package com.api.erp.v1.main.master.permission.domain.controller;

import com.api.erp.v1.main.master.permission.application.dto.request.AssociarPermissionRequest;
import com.api.erp.v1.main.master.permission.application.dto.request.CreateRoleRequest;
import com.api.erp.v1.main.master.permission.application.dto.response.RoleResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

public interface IRoleController {

    ResponseEntity<RoleResponse> createRole(
            @RequestBody CreateRoleRequest request
    );

    ResponseEntity<List<RoleResponse>> getAllRoles();

    ResponseEntity<Void> associarPermission(
            @RequestBody AssociarPermissionRequest request
    );
}
