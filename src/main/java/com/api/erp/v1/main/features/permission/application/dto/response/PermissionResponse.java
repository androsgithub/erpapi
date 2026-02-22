package com.api.erp.v1.main.features.permission.application.dto.response;

import com.api.erp.v1.main.features.permission.domain.entity.TipoAcao;

public record PermissionResponse(
        Long id,
        String codigo,
        String nome,
        String modulo,
        TipoAcao acao
) {
}
