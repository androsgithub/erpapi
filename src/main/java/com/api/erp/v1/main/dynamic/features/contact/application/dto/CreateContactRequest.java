package com.api.erp.v1.main.dynamic.features.contact.application.dto;

import java.util.Map;

public record CreateContactRequest(
        String tipo,
        String valor,
        String descricao,
        boolean principal,
        Map<String, Object> customData
) {
}
