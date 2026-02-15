package com.api.erp.v1.main.features.permissao.application.dto.response;

import com.api.erp.v1.main.features.permissao.domain.entity.TipoAcao;

public record PermissaoResponse(
        Long id,
        String codigo,
        String nome,
        String modulo,
        TipoAcao acao
) {
}
