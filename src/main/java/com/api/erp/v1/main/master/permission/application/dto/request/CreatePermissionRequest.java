package com.api.erp.v1.main.master.permission.application.dto.request;

import com.api.erp.v1.main.master.permission.domain.entity.TipoAcao;
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
