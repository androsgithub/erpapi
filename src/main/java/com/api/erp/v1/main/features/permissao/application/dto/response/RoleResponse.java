package com.api.erp.v1.main.features.permissao.application.dto.response;

import com.api.erp.v1.main.features.permissao.domain.entity.Permissao;

import java.util.List;

public record RoleResponse(
        Long id,
        String nome,
        List<Permissao> permissoes
) {
}
