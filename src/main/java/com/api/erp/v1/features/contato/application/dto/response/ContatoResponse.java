package com.api.erp.v1.features.contato.application.dto.response;

import com.api.erp.v1.shared.domain.valueobject.CustomData;

import java.time.LocalDateTime;

public record ContatoResponse(
        Long id,
        String tipo,
        String valor,
        String descricao,
        boolean principal,
        boolean ativo,
        CustomData customData,
        LocalDateTime dataCriacao,
        LocalDateTime dataAtualizacao
) {
}
