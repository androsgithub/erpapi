package com.api.erp.v1.features.cliente.application.dto.response;

import java.time.LocalDateTime;

public record ClienteSimpleResponseDto(
        long id,
        String nome,
        LocalDateTime dataCriacao,
        LocalDateTime dataAtualizacao
) {
}
