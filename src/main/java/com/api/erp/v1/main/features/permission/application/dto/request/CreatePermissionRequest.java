package com.api.erp.v1.main.features.permission.application.dto.request;

import com.api.erp.v1.main.features.permission.domain.entity.TipoAcao;
import java.util.Map;

public record CreatePermissionRequest(
        String codigo,
        String nome,
        String modulo,
        TipoAcao acao,
        Map<String, String> contexto,
        boolean ativo
) {
}
