package com.api.erp.v1.main.features.permissao.application.dto.request;

import com.api.erp.v1.main.features.permissao.domain.entity.TipoAcao;
import java.util.Map;

public record CreatePermissaoRequest(
        String codigo,
        String nome,
        String modulo,
        TipoAcao acao,
        Map<String, String> contexto,
        boolean ativo
) {
}
