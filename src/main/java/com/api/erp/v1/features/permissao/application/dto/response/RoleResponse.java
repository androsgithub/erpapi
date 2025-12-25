package com.api.erp.v1.features.permissao.application.dto.response;

import com.api.erp.v1.features.permissao.domain.entity.Permissao;

import java.util.Set;

public record RoleResponse(
        Long id,
        String nome,
        Set<Permissao> permissoes
) {
}
