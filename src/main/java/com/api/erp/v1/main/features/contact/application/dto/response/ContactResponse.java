package com.api.erp.v1.main.features.contact.application.dto.response;

import java.time.LocalDateTime;

public record ContactResponse(
        Long id,
        String tipo,
        String valor,
        String descricao,
        boolean principal,
        boolean ativo,
        LocalDateTime create_At,
        LocalDateTime updated_at
) {
}
