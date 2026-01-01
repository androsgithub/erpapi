package com.api.erp.v1.features.permissao.domain.service;

import com.api.erp.v1.features.permissao.application.dto.request.AssociarPermissaoRequest;
import com.api.erp.v1.features.permissao.application.dto.request.CreatePermissaoRequest;
import com.api.erp.v1.features.permissao.application.dto.request.CreateRoleRequest;
import com.api.erp.v1.features.permissao.domain.entity.Permissao;
import com.api.erp.v1.features.permissao.domain.entity.Role;

import java.util.List;

public interface IGerenciamentoPermissaoService {

    Permissao createPermissao(CreatePermissaoRequest request);

    Role createRole(CreateRoleRequest request);

    void associarPermissao(AssociarPermissaoRequest request);
    
    List<Permissao> getAllPermissoes();

    List<Role> getAllRoles();
}
