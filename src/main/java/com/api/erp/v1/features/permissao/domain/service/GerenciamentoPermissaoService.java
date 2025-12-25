package com.api.erp.v1.features.permissao.domain.service;

import com.api.erp.v1.features.permissao.application.dto.request.AssociarPermissaoRequest;
import com.api.erp.v1.features.permissao.application.dto.request.CreatePermissaoRequest;
import com.api.erp.v1.features.permissao.application.dto.request.CreateRoleRequest;
import com.api.erp.v1.features.permissao.application.dto.response.PermissaoResponse;
import com.api.erp.v1.features.permissao.application.dto.response.RoleResponse;

import java.util.List;

public interface GerenciamentoPermissaoService {

    PermissaoResponse createPermissao(CreatePermissaoRequest request);

    RoleResponse createRole(CreateRoleRequest request);

    void associarPermissao(AssociarPermissaoRequest request);
    
    List<PermissaoResponse> getAllPermissoes();

    List<RoleResponse> getAllRoles();
}
