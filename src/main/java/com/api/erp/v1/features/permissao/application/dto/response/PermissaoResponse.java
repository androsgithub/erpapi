package com.api.erp.v1.features.permissao.application.dto.response;

import com.api.erp.v1.features.permissao.domain.entity.TipoAcao;

import java.util.Map;

public record PermissaoResponse(
        Long id,
        String codigo,
        String nome,
        String modulo,
        TipoAcao acao
) {
}
