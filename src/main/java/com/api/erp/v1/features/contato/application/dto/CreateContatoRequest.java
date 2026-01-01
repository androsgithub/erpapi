package com.api.erp.v1.features.contato.application.dto;

import com.api.erp.v1.shared.domain.valueobject.CustomData;
import com.api.erp.v1.shared.infrastructure.persistence.converters.CustomDataAttributeConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;

public record CreateContatoRequest(
        String tipo,
        String valor,
        String descricao,
        boolean principal,
        CustomData customData
) {
}
