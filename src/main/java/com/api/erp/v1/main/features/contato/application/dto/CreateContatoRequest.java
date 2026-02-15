package com.api.erp.v1.main.features.contato.application.dto;

import java.util.Map;

public record CreateContatoRequest(
        String tipo,
        String valor,
        String descricao,
        boolean principal,
        Map<String, Object> customData
) {
}
