package com.api.erp.v1.features.contato.application.dto.response;


import com.api.erp.v1.features.customfield.domain.entity.CustomData;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public record ContatoResponse(
        Long id,
        String tipo,
        String valor,
        String descricao,
        boolean principal,
        boolean ativo,
        List<CustomData> custom_data,
        LocalDateTime create_At,
        LocalDateTime updated_at
) {
}
