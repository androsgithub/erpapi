package com.api.erp.v1.main.features.customer.application.dto.response;

import java.time.LocalDateTime;

public record CustomerSimpleResponseDto(
        long id,
        String nome,
        LocalDateTime dataCriacao,
        LocalDateTime dataAtualizacao
) {
}
