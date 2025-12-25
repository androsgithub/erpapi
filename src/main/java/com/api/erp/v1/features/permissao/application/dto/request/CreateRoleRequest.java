package com.api.erp.v1.features.permissao.application.dto.request;

import java.util.Set;

public record CreateRoleRequest(
        String nome,
        Set<Long> permissaoIds
) {
}
