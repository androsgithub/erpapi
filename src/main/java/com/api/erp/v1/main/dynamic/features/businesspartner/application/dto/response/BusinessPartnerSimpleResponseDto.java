package com.api.erp.v1.main.dynamic.features.businesspartner.application.dto.response;

import java.time.LocalDateTime;

public record BusinessPartnerSimpleResponseDto(
        long id,
        String nome,
        LocalDateTime dataCriacao,
        LocalDateTime dataAtualizacao
) {
}
