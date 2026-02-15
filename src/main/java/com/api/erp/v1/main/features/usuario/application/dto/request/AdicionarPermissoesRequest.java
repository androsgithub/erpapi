package com.api.erp.v1.main.features.usuario.application.dto.request;

import java.time.LocalDateTime;
import java.util.Set;

public record AdicionarPermissoesRequest(
    Set<Long> permissaoIds,
    LocalDateTime dataInicio,
    LocalDateTime dataFim
) {}
