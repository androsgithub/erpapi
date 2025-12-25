package com.api.erp.v1.features.permissao.application.dto.request;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

public record AssociarPermissaoRequest(
        UUID usuarioId,
        Set<Long> permissaoIds,
        Set<Long> roleIds,
        LocalDateTime dataInicio,
        LocalDateTime dataFim
) {
}
