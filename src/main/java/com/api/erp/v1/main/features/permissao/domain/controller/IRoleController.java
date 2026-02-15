package com.api.erp.v1.main.features.permissao.domain.controller;

import com.api.erp.v1.main.features.permissao.application.dto.request.AssociarPermissaoRequest;
import com.api.erp.v1.main.features.permissao.application.dto.request.CreateRoleRequest;
import com.api.erp.v1.main.features.permissao.application.dto.response.RoleResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

public interface IRoleController {

    ResponseEntity<RoleResponse> createRole(
            @RequestBody CreateRoleRequest request
    );

    ResponseEntity<List<RoleResponse>> getAllRoles();

    ResponseEntity<Void> associarPermissao(
            @RequestBody AssociarPermissaoRequest request
    );
}
